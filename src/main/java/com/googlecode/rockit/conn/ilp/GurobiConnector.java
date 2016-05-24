package com.googlecode.rockit.conn.ilp;

import gurobi.GRB;
import gurobi.GRB.DoubleAttr;
import gurobi.GRB.StringAttr;
import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;
import gurobi.GRBVar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.ILPInfeasibleException;
import com.googlecode.rockit.exception.SolveException;


/**
 * Represents the connector to the Gurobi solver.
 * 
 * This class is a singleton class.
 * 
 * @author jan
 *
 */
public class GurobiConnector extends ILPConnector
{

    public GurobiConnector() throws ILPException
    {
        super();
    }

    // this variables will be updated before the model is run.
    private HashMap<GRBVar, Double> objectiveVariablesToUpdate    = new HashMap<GRBVar, Double>();

    // this variables will be updated before the model is run.
    private HashMap<GRBVar, Double> lowerBoundOfVariablesToUpdate = new HashMap<GRBVar, Double>();

    private HashMap<GRBVar, Double> upperBoundOfVariablesToUpdate = new HashMap<GRBVar, Double>();

    private GRBVar                  lastZ                         = null;
    // TODO remove if never ever an error occurs (see TODO below)
    private String                  lastZName                     = null;

    private GurobiConstrs           constrsToAdd                  = new GurobiConstrs();

    // Gurobi internal stuff
    private GRBEnv                  env;
    private GRBModel                model;

    // all used variables
    private HashMap<String, GRBVar> variables;

    private double                  objectiveValue                = 0d;

    private long                    totalNumberOfConstraints      = 0;


    @Override
    public void addVariable(String varName, double objWeight, double lowerBound, double upperBound, boolean override, boolean isZVar) throws ILPException
    {
        this.addVariablePrivate(varName, objWeight, lowerBound, upperBound, override, isZVar);
    }


    private GRBVar addVariablePrivate(String varName, double objWeight, double lowerBound, double upperBound, boolean override, boolean isZVar) throws ILPException
    {
        GRBVar var = null;
        try {
            if(isZVar) {
                this.lastZName = varName;
                if(upperBound > 1) {
                    this.lastZ = model.addVar(lowerBound, upperBound, objWeight, GRB.INTEGER, varName);
                } else {
                    this.lastZ = model.addVar(lowerBound, upperBound, objWeight, GRB.BINARY, varName);
                }
            } else {
                GRBVar existingV = variables.get(varName);
                if(existingV != null) {
                    if(!override) {
                        return existingV;
                    } else {
                        if(existingV.get(DoubleAttr.Obj) != objWeight)
                            this.objectiveVariablesToUpdate.put(existingV, objWeight);
                        if(existingV.get(DoubleAttr.LB) != objWeight)
                            this.lowerBoundOfVariablesToUpdate.put(existingV, lowerBound);
                        if(existingV.get(DoubleAttr.UB) != objWeight)
                            this.upperBoundOfVariablesToUpdate.put(existingV, upperBound);
                    }
                } else {
                    if(upperBound > 1) {
                        var = model.addVar(lowerBound, upperBound, objWeight, GRB.INTEGER, varName);
                    } else {
                        var = model.addVar(lowerBound, upperBound, objWeight, GRB.BINARY, varName);
                    }
                    variables.put(varName, var);
                    // model.update();
                    return var;
                }
            }
        } catch(GRBException e) {
            throw new ILPException("Can not add variable " + varName + " with weight " + objWeight + " and upperbound " + upperBound + ". Maybe the creation fails or the addition to the ilp. " + e.getMessage());
        }
        return null;
    }


    /**
     * First the function tries to get the binary variable from the existing variables.
     * 
     * If the var has not been added yet, then a new one is created with weight 0.
     * 
     * @param varName
     * @param weight
     * @return
     * @throws ILPException
     * 
     *             private GRBVar addVariableIfItDoesNotExistPrivate(String varName) throws ILPException{
     *             try{
     *             GRBVar existingV = variables.get(varName);
     *             if(existingV != null){
     *             return existingV;
     *             }else{
     *             GRBVar var = model.addVar(0.0, 1.0, 0, GRB.BINARY, varName);
     *             variables.put(varName, var);
     *             //model.update();
     *             return var;
     *             }
     *             }catch(GRBException e){
     *             throw new ILPException("Can not add variable. Maybe the creation fails or the addition to the ilp. " + e.getMessage());
     *             }
     *             }
     */

    @Override
    public void addConstraint(ArrayList<ILPVariable> lhs, ILPOperator operator, double rhs) throws ILPException
    {
        GRBLinExpr expr = new GRBLinExpr();
        for(ILPVariable ilpVar : lhs) {
            GRBVar var = null;
            if(ilpVar.isZVar()) {
                // TODO remove if never ever an error occurs
                if(ilpVar.getName().equals(lastZName)) {
                    var = lastZ;
                } else {
                    throw new ILPException("Z variable " + ilpVar.getName() + " not found. Last Z variable was " + lastZName);
                }
            } else {
                var = this.addVariablePrivate(ilpVar.getName(), 0d, 0d, 1d, false, ilpVar.isZVar());
            }
            expr.addTerm(ilpVar.getValue(), var);
        }
        // the greater equal value is modified in the getHardExpression method.
        char op = GRB.LESS_EQUAL;
        if(operator.equals(ILPOperator.GEQ)) {
            op = GRB.GREATER_EQUAL;
        }
        constrsToAdd.addConstr(expr, op, rhs, "");
    }


    /**
     * Solves the ILP and returns the integer values for each solution.
     * 
     * @return solution.
     * @throws ILPException
     *             thrown if infeasible.
     */
    public HashMap<String, Integer> optimizeILP() throws ILPException
    {
        this.addPendingVariablesAndConstraintsToILP();
        HashMap<String, Integer> solutionVars = new HashMap<String, Integer>();
        try {
            // Calcucate the optimal solution
            if(Parameters.DEBUG_OUTPUT)
                System.out.print("Start ILP solver.");
            if(Parameters.DEBUG_OUTPUT)
                System.out.println(new Date());

            if(Parameters.GAP > 0 && Parameters.GAP <= 1000) {
                // model.getEnv().set(GRB.DoubleParam.FeasibilityTol, Parameters.GUROBI_TOLLERANCE);
                // model.getEnv().set(GRB.DoubleParam.IntFeasTol, Parameters.GUROBI_TOLLERANCE);
                model.getEnv().set(GRB.DoubleParam.MIPGap, Parameters.GAP);
                // model.getEnv().set(GRB.DoubleParam.OptimalityTol, Parameters.GUROBI_TOLLERANCE);

            }

            model.getEnv().set(GRB.IntParam.Presolve, 1);

            if(Parameters.TIME_LIMIT > 0) {
                model.getEnv().set(GRB.DoubleParam.TimeLimit, Parameters.TIME_LIMIT);
            }

            model.getEnv().set(GRB.IntParam.Method, Parameters.GUROBI_PARAMETER_METHOD);

            // model.getEnv().set(GRB.IntParam.PreDual, -1);
            // if(!Parameters.DEBUG_OUTPUT) model.getEnv().set(GRB.IntParam.OutputFlag, 0);
            if(Parameters.DEBUG_OUTPUT)
                model.update();
            if(Parameters.DEBUG_OUTPUT)
                model.write("model.lp");
            model.update();
            model.write("model.lp");
            model.write("model.mps");

            model.optimize();

            int optimstatus = model.get(GRB.IntAttr.Status);
            if(optimstatus == GRB.Status.INF_OR_UNBD) {
                model.getEnv().set(GRB.IntParam.Presolve, 0);
                model.optimize();
                optimstatus = model.get(GRB.IntAttr.Status);
            }

            if(optimstatus == GRB.Status.INFEASIBLE) {
                model.computeIIS();
                model.write("iis.ilp");
                throw new ILPInfeasibleException("The actual model is infeasible. The IIS has been written in the file iis.ilp.");
            }
            if(optimstatus == GRB.Status.OPTIMAL) {
                double objval = model.get(GRB.DoubleAttr.ObjVal);
                if(Parameters.DEBUG_OUTPUT)
                    System.out.println("Optimal objective: " + objval);
                // get all variables of the solution
                GRBVar[] variables = model.getVars();

                if(variables != null) {
                    for(int i = 0; i < variables.length; i++) {
                        int x = (int) variables[i].get(GRB.DoubleAttr.X);
                        String varName = variables[i].get(StringAttr.VarName);
                        solutionVars.put(varName, x);
                        // System.out.println(variables[i].get(StringAttr.VarName) + " " +x);
                    }
                }
                this.objectiveValue = model.get(GRB.DoubleAttr.ObjVal);
                if(Parameters.DEBUG_OUTPUT)
                    System.out.print("Stop ILP solver. ");
                if(Parameters.DEBUG_OUTPUT)
                    System.out.println(new Date());
            }
        } catch(GRBException e) {
            e.printStackTrace();
            throw new ILPException("Failed to solve the model. But it is not infeasible. " + e.getMessage());
        }
        return solutionVars;
    }


    public void close() throws ILPException
    {
        try {
            if(model != null) {
                model.dispose();
                System.out.println("Dispose model");
                model = null;
            }

            if(env != null) {
                env.dispose();
                System.out.println("Dispose environment");
                env = null;
            }
            System.gc();
        } catch(GRBException e) {
            throw new ILPException("Error in ILP Solver. Cant initialize model and/or environment. " + e.getMessage());
        }
    }


    public void initialize() throws ILPException
    {
        try {
            // Set variables to zero
            objectiveVariablesToUpdate = new HashMap<GRBVar, Double>();
            lowerBoundOfVariablesToUpdate = new HashMap<GRBVar, Double>();
            upperBoundOfVariablesToUpdate = new HashMap<GRBVar, Double>();

            constrsToAdd = new GurobiConstrs();
            variables = new HashMap<String, GRBVar>();

            // log file
            env = new GRBEnv("gurobi.log");

            // New Object
            model = new GRBModel(env);
            // maximizing problem
            model.set(GRB.IntAttr.ModelSense, -1);

        } catch(GRBException e) {
            throw new ILPException("Error in ILP Solver. Cant initialize model and/or environment. " + e.getMessage());
        }
    }


    /**
     * Gets the objective of the ILP from the last solve call.
     * 
     * @return
     */
    public double getObjectiveValue()
    {
        return objectiveValue;
    }


    /**
     * Adds pending variables and constraints to the ILP.
     * 
     * @throws SolveException
     */
    private void addPendingVariablesAndConstraintsToILP() throws ILPException
    {
        // update (Vars has been added)
        try {
            model.update();

            // update variables with new weight
            for(Entry<GRBVar, Double> pair : objectiveVariablesToUpdate.entrySet()) {
                pair.getKey().set(DoubleAttr.Obj, pair.getValue());
            }
            this.objectiveVariablesToUpdate = new HashMap<GRBVar, Double>();

            // update variables with new lowerbound
            for(Entry<GRBVar, Double> pair : lowerBoundOfVariablesToUpdate.entrySet()) {
                pair.getKey().set(DoubleAttr.LB, pair.getValue());
            }
            this.lowerBoundOfVariablesToUpdate = new HashMap<GRBVar, Double>();

            for(Entry<GRBVar, Double> pair : upperBoundOfVariablesToUpdate.entrySet()) {
                pair.getKey().set(DoubleAttr.UB, pair.getValue());
            }
            this.upperBoundOfVariablesToUpdate = new HashMap<GRBVar, Double>();

            // include constrs
            int numbConstrAdded = constrsToAdd.getExpressions().size();
            System.out.print("add ");
            System.out.print(numbConstrAdded);
            System.out.print(" constraints (total number = ");
            totalNumberOfConstraints = totalNumberOfConstraints + numbConstrAdded;
            System.out.print(totalNumberOfConstraints);
            System.out.println(")");
            constrsToAdd.addToModel(model);
            // due to an error in gurobi java api, one can not use the addConstrs() function.
            // GRBConstr[] addedCs=model.addConstrs(this.constrsToAdd.getExpressions(),this.constrsToAdd.getEqualTypes(),
            // this.constrsToAdd.getValues(), this.constrsToAdd.getNames());
            this.constrsToAdd.reset();
        } catch(GRBException e) {
            e.printStackTrace();
            throw new ILPException("Could not add pending variables or constraints to the ILP");
        }

    }


    /**
     * Sets the start values of the given variables to 1.
     */
    public void addStartValues(List<String> varNames) throws ILPException
    {
        try {
            for(String varName : varNames) {
                GRBVar var = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, varName);
                variables.put(varName, var);
            }
            model.update();
            for(GRBVar var : variables.values()) {
                var.set(GRB.DoubleAttr.Start, 1.0);
            }
        } catch(GRBException e) {
            e.printStackTrace();
            throw new ILPException("Could not set initial start value of variable(s).");
        }
    }


    /**
     * Writes model to file. File has to end with .ilp or lp
     * 
     * @param filename
     * @throws ILPException
     */
    public void writeModelToFile(String filename) throws ILPException
    {
        try {
            model.write(filename);
        } catch(GRBException e) {
            throw new ILPException("Can not write model to file. " + e.getMessage());
        }
    }

}
