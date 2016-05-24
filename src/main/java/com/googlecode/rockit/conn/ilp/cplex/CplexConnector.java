package com.googlecode.rockit.conn.ilp.cplex;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.concert.IloNumVarType;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.UnknownObjectException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.conn.ilp.ILPOperator;
import com.googlecode.rockit.conn.ilp.ILPVariable;
import com.googlecode.rockit.exception.ILPException;


public class CplexConnector extends ILPConnector
{
    private IloCplex               cplex;
    private Map<String, IloNumVar> variables = new HashMap<String, IloNumVar>();
    private Map<String, Double>    objective = new HashMap<String, Double>();


    public CplexConnector() throws ILPException
    {
        super();
    }


    @Override
    public void writeModelToFile(String filename) throws ILPException
    {
        try {
            cplex.exportModel(filename);
        } catch(IloException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() throws ILPException
    {
        cplex.end();
    }


    @Override
    public void initialize() throws ILPException
    {
        try {
            cplex = new IloCplex();
        } catch(IloException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void addVariable(String varName, double objWeight, double lowerBound, double upperBound, boolean override, boolean isZVar) throws ILPException
    {
        try {
            IloNumVarType type = null;
            if(upperBound > 1) {
                type = IloNumVarType.Int;
            } else {
                type = IloNumVarType.Bool;
            }
            IloNumVar var = cplex.numVar(lowerBound, upperBound, type, varName);
            if(override || !variables.containsKey(varName)) {
                variables.put(varName, var);
                cplex.add(var);
                objective.put(varName, objWeight);
            }
        } catch(IloException e) {
            throw new ILPException(e.getMessage());
        }
    }


    @Override
    public void addStartValues(List<String> varNames) throws ILPException
    {
        // TODO: not tested
        try {
            for(String varName : varNames) {
                IloNumVar var = cplex.numVar(0.0, 1.0, IloNumVarType.Bool, varName);
                variables.put(varName, var);
                objective.put(varName, 0.0);
            }

            IloNumVar[] startVar = new IloNumVar[variables.size()];
            double[] startVal = new double[variables.size()];
            int counter = 0;
            for(IloNumVar var : variables.values()) {
                startVar[counter] = var;
                startVal[counter] = 1.0;
                counter++;
            }

            cplex.addMIPStart(startVar, startVal);
        } catch(IloException e) {
            throw new ILPException(e.getMessage());

        }

    }


    @Override
    public void addConstraint(ArrayList<ILPVariable> lhs, ILPOperator operator, double rhs) throws ILPException
    {
        try {
            IloLinearNumExpr constraint = cplex.linearNumExpr();
            for(ILPVariable var : lhs) {
                if(!variables.containsKey(var.getName())) {
                    this.addVariable(var.getName(), 0d, 0d, 1d, false, var.isZVar());
                }
                constraint.addTerm(var.getValue(), variables.get(var.getName()));
            }

            switch(operator) {
                case LEQ:
                    cplex.addLe(constraint, rhs);
                break;
                case GEQ:
                    cplex.addGe(constraint, rhs);
                break;
                default:
                break;
            }
        } catch(IloException e) {
            throw new ILPException(e.getMessage());
        }
    }

    IloLinearNumExpr objectiveExpression = null;


    private void addObjective() throws IloException
    {
        IloLinearNumExpr expr = cplex.linearNumExpr();
        for(String var : objective.keySet()) {
            expr.addTerm(objective.get(var), variables.get(var));
        }
        if(cplex.getObjective() == null) {
            cplex.addMaximize(expr);
        } else {
            cplex.getObjective().setExpr(expr);
        }
    }


    @Override
    public HashMap<String, Integer> optimizeILP() throws ILPException
    {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        try {

            addObjective();

            if(cplex.solve()) {
                System.out.println("Solution status = " + cplex.getStatus());
                System.out.println("Solution value  = " + cplex.getObjValue());

                for(String key : variables.keySet()) {
                    double value = cplex.getValue(variables.get(key));
                    result.put(key, (int) value);
                }
            }
        } catch(UnknownObjectException e) {
            throw new ILPException(e.getMessage());
        } catch(IloException e) {
            e.printStackTrace();
            throw new ILPException(e.getMessage());
        }
        return result;
    }


    @Override
    public double getObjectiveValue()
    {
        try {
            return cplex.getObjValue();
        } catch(IloException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return 0;
    }
}
