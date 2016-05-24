package com.googlecode.rockit.conn.ilp;

import gurobi.GRBException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.SolveException;


public abstract class ILPConnector
{
    public static enum ILPSolver {
        GUROBI, CPLEX, SCIP
    };

    // helper for adding multible constraints
    int rhs       = 0;

    int zVarIndex = 0;


    // Aggregation Manager
    // TODO for tests two activated
    // AggregationManager aggregationManager = null;

    public ILPConnector() throws ILPException
    {
        this.initialize();
    }


    /**
     * Adds binary restriction in which every variable has to be true.
     * 
     * It builds restrictions like
     * 
     * (1-x1) + (1-x2) + x3 + x4 + x5 <= 1
     * 
     * Thus, x1 and x2 must be negative in the ilp while
     * x3,x4 and x5 must be positive (+0) in the ilp.
     * 
     * If we have just one axiom in the list, the evidence is directly encoded by restricting the variable to
     * - lb = 0 and ub = 0 (negated) or
     * - lb = 1 and ub = 1 (positive).
     * 
     * @param axioms
     *            Array of components (including name and positive and negated information)
     * @throws ParseException
     *             When adding a restriction, there has to be as many variable names as entries in the hasToBePositive Array.
     * @throws SolveException
     */
    public void addHardConstraint(ArrayList<Literal> axioms) throws SolveException
    {
        this.rhs = 1;
        this.addConstraint(this.getLHSExpression(axioms), ILPOperator.GEQ, this.rhs);
    }


    /**
     * Generates the left hand side expression.
     * 
     * For e.g.
     * a v ! b v c
     * it creates
     * {+a, -b, +c}.
     * 
     * Furthermore, it changes rhs as followed:
     * - if sumPositive then it adds 1 for every positive axiom (in our example: rhs = rhs + 2)
     * - if !sumPositive then it substracts 1 for every negated axiom (in our example: rhs = rhs - 1)
     * 
     * @param variableNames
     * @param mustBePositive
     * @param expr
     * @return
     * @throws SolveException
     */
    private ArrayList<ILPVariable> getLHSExpression(ArrayList<Literal> axioms) throws SolveException
    {
        ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();
        for(Literal axiom : axioms) {
            if(axiom.isPositive()) {
                // less equal value does not change
                expr.add(new ILPVariable(axiom.getName(), +1, false));
            } else {
                this.rhs = rhs - 1;
                expr.add(new ILPVariable(axiom.getName(), -1, false));
            }
        }
        // assign the private variable greaterEqualValue the correct weight
        return expr;
    }


    /**
     * Adds binary restrictions for soft constraints.
     * 
     * @param weight
     *            value of the target function weight of the soft formular
     * @param variableNames
     *            names of the variables
     * @param mustBePositive
     *            Array with values if the respective variable (same position in array) has to be positive or negative
     * @param conjunction
     *            if true, the whole formula is added as conjunction, if false it is added as disjunction.
     * @param dublicateDetection
     *            this string detects if a similar formula has already been added. If no, the formula is added. If yes, the weight of the formula is increased (but no new z variable is generated).
     * @throws ParseException
     *             When adding a restriction, there has to be as many variable names as entries in the hasToBePositive Array.
     * @throws SolveException
     */
    public void addSingleSoftConstraint(double weight, ArrayList<Literal> restriction, boolean conjunction) throws SolveException
    {
        String z = this.getNextZ(weight);
        // disjunction: of course if it is no conjunction AND if the variable length is less than 1.
        if(!conjunction || (restriction != null && restriction.size() <= 1)) {

            if(weight > 0) {
                addSoftConstraintDisjunctionPositive(weight, restriction, z);
                if(Parameters.USE_SAMPLING) {
                    this.addSoftConstraintDisjunctionNegative(weight, restriction, z);
                }
            } else {
                addSoftConstraintDisjunctionNegative(weight, restriction, z);
                if(Parameters.USE_SAMPLING) {
                    this.addSoftConstraintDisjunctionPositive(weight, restriction, z);
                }
            }
        } else {
            // conjunction
            if(weight > 0) {
                addSoftConstraintConjunctionPositive(weight, restriction, z);
            } else {
                addSingleSoftConstraintConjunctionNegative(weight, restriction, z);
            }
        }
    }


    /**
     * Adds soft constraints for Disjunction (or) and a negative weight. Internally, the function also assigns
     * the value for the private greaterEqualValue variable.
     * 
     * Further, it creates the necessary unique "zzz" variables with the help of the zVarIndex.
     * 
     * Assume for example the following formula:
     * a(B,C) or b(A,B) or ! c(A,C) - 0.5
     * 
     * Then, the target function is extended with:
     * - 0.5 * z_i
     * 
     * Further, the following restriction is added:
     * + a(B,C) + b(A,B) + (1-c(A,C)) <= 3*z_i
     * 
     * In general, for every positive literal C+ and for every negative literal C- the following restriction is added.
     * 
     * (sum_C+ y_i) + (sum_C- (1-y_i)) <= (sum_C 1) z
     * <-> (sum_C+ y_i) - (sum_C- y_i) - (sum_C 1) z <= - (sum_C- 1)
     * 
     * 
     * @param weight
     * @param variableNames
     * @param mustBePositive
     * @return
     * @throws SolveException
     */
    private void addSoftConstraintDisjunctionNegative(double weight, ArrayList<Literal> restriction, String z) throws SolveException
    {
        // accesses the private helper variable greaterEqualValue.
        this.rhs = 0;
        ArrayList<ILPVariable> lhs = this.getLHSExpression(restriction);
        int sumC = restriction.size();
        lhs.add(new ILPVariable(z, (-sumC), true));
        this.addConstraint(lhs, ILPOperator.LEQ, this.rhs);

    }


    /**
     * Adds soft constraints for Disjunction (or) and a positive weight. Internally, the function also assigns
     * the value for the private greaterEqualValue variable.
     * 
     * Further, it creates the necessary unique "zzz" variables with the help of the zVarIndex.
     * 
     * Thereby, a new variable "zzz" is added to the target function multiplied with the weight:
     * 
     * max ... + weight * z + ...
     * 
     * Further, for every positive literal C+ and for every negative literal C- the following restriction is added.
     * 
     * (sum_C+ y_i) + (sum_C- (1-y_i)) >= z
     * <-> (sum_C+ y_i) - (sum_C- y_i) - z >= - (sum_C- 1)
     * 
     * This restriction ensures that z is true if one positive literal is true or one negative literal is false.
     * 
     * @param weight
     * @param variableNames
     * @param mustBePositive
     * @return
     * @throws SolveException
     */
    private void addSoftConstraintDisjunctionPositive(double weight, ArrayList<Literal> restriction, String z) throws SolveException
    {
        this.rhs = 0;
        ArrayList<ILPVariable> lhs = this.getLHSExpression(restriction);
        lhs.add(new ILPVariable(z, -1, true));
        this.addConstraint(lhs, ILPOperator.GEQ, this.rhs);
    }


    /**
     * Adds soft constraints for Conjunction (and) and a positive weight. Internally, the function also assigns
     * the value for the private greaterEqualValue variable.
     * 
     * Further, it creates the necessary unique "z" variables with the help of the zVarIndex.
     * 
     * Thereby, a new variable "z" is added to the target function multiplied with the weight:
     * 
     * max ... + weight * z + ...
     * 
     * Further, for every positive literal C+ and for every negative literal C- the following restriction is added.
     * 
     * (sum_C+ y_i) - (sum_C- (1-y_i)) >= ((sum_C+ 1) + (sum_C- 1)) * z <=>
     * (sum_C+ y_i) - (sum_C- y_i) - ((sum_C+ 1) + (sum_C- 1)) * z >= - (sum_C- 1)
     * 
     * This restriction ensures that z is true if one positive literal is true or one negative literal is false.
     * 
     * @param weight
     * @param variableNames
     * @param mustBePositive
     * @return
     * @throws SolveException
     */
    private void addSoftConstraintConjunctionPositive(double weight, ArrayList<Literal> restriction, String z) throws SolveException
    {

        // accesses the private helper variable greaterEqualValue.
        this.rhs = 0;
        ArrayList<ILPVariable> lhs = this.getLHSExpression(restriction);
        lhs.add(new ILPVariable(z, ((-1) * restriction.size()), true));
        this.addConstraint(lhs, ILPOperator.GEQ, this.rhs);

    }


    /**
     * Adds soft constraints for Conjunction (and) and a negative weight. Internally, the function also assigns
     * the value for the private greaterEqualValue variable.
     * 
     * Further, it creates the necessary unique "zzz" variables with the help of the zVarIndex.
     * 
     * Thereby, a new variable "zzz" is added to the target function multiplied with the weight:
     * 
     * max ... + weight * z_i + ...
     * 
     * Further, for every positive literal C+ and for every negative literal C- the following restriction is added.
     * 
     * (sum_C+ y_i) - (sum_C- y_i) <= z + (sum_C+ 1) + (sum_C- 1) - 1
     * <=> (sum_C+ y_i) - (sum_C- y_i) - z <= (sum_C+ 1) - 1
     * 
     * This restriction ensures that z is true if one positive literal is true or one negative literal is false.
     * 
     * Example: a n b n \neg c - 0.5
     * leads to: a + b + (1-c) <= z + 2
     * 
     * @param weight
     * @param variableNames
     * @param mustBePositive
     * @return
     * @throws SolveException
     */
    private void addSingleSoftConstraintConjunctionNegative(double weight, ArrayList<Literal> restriction, String z) throws SolveException
    {
        this.rhs = 0;
        ArrayList<ILPVariable> lhs = this.getLHSExpression(restriction);
        lhs.add(new ILPVariable(z, -1, true));
        // (sum_C+ 1) + (sum_C- 1) - (sum_C- 1) - 1 = (sum_C+ 1) - 1
        this.rhs = restriction.size() - this.rhs - 1;

        this.addConstraint(lhs, ILPOperator.LEQ, rhs);
    }


    public void addAggregatedConstraint(double weight, ArrayList<Literal> aggregatedVars, ArrayList<Literal> singleVars, boolean isConjunction) throws ILPException
    {
        String z = this.getNextZ(weight, singleVars.size());
        if(isConjunction) {
            throw new ILPException("Can not aggregate conjunction formulas");
        } else {
            // only do something if at least one single variable is present
            // 0 <= z <= k
            if(singleVars.size() > 0) {
                if(weight > 0) {
                    this.addAggregatedPositiveDisjunctionConstraint(weight, aggregatedVars, singleVars, z);
                } else {
                    this.addAggregatedNegativeDisjunctionConstraint(weight, aggregatedVars, singleVars, z);
                }
            }
        }
    }


    /**
     * w negative and disjunction:
     * 
     * sum k * aggregatedVars_positive - sum k * aggregatedVars_negative - z <= - (Sum_{negative aggregatedVars} 1) * k
     * 
     * (-1)_{if single var negative} sum singleVars - z <= - (number of negative single vars)
     * optional: 0 <= z <= k
     * 
     * @param model
     * @return
     * @throws ILPException
     */
    private void addAggregatedNegativeDisjunctionConstraint(double weight, ArrayList<Literal> aggregatedVars, ArrayList<Literal> singleVars, String z) throws ILPException
    {
        int k = singleVars.size();

        // sum k * aggregatedVars_positive - sum k * aggregatedVars_negative - z <= (Sum_{negative aggregatedVars} 1) * k
        if(aggregatedVars.size() > 0) {
            ArrayList<ILPVariable> expr1 = new ArrayList<ILPVariable>();

            for(Literal axiom : aggregatedVars) {
                if(axiom.isPositive()) {
                    // sum k * aggregatedVars_positive
                    expr1.add(new ILPVariable(axiom.getName(), k, false));
                } else {
                    // - sum k * aggregatedVars_negative
                    expr1.add(new ILPVariable(axiom.getName(), -k, false));
                }
            }
            // - z
            expr1.add(new ILPVariable(z, -1, true));
            /*
             * for(int i =0; i<k; i++){
             * expr1.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
             * }
             */

            // <=
            ILPOperator lessEqual = ILPOperator.LEQ;

            // - (Sum_{negative aggregatedVars} 1) * k
            double rhs = 0;
            for(Literal negAggVar : aggregatedVars) {
                if(!negAggVar.isPositive()) {
                    rhs = rhs - k;
                }
            }
            this.addConstraint(expr1, lessEqual, rhs);
        }

        // (-1)_{if single var negative} sum singleVars - z <= (0 if singleVars positive, k if singleVars negative)
        ArrayList<ILPVariable> expr2 = new ArrayList<ILPVariable>();

        // (-1)_{if single var negative} sum singleVars
        for(Literal singleVar : singleVars) {
            double one_or_minus_one = -1;
            if(singleVar.isPositive())
                one_or_minus_one = +1;
            expr2.add(new ILPVariable(singleVar.getName(), one_or_minus_one, false));
        }

        // - z
        expr2.add(new ILPVariable(z, -1, true));
        /*
         * for(int i =0; i<k; i++){
         * expr2.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
         * }
         */

        // <=
        ILPOperator lessEqual = ILPOperator.LEQ;

        // - (number of negative singleVars)
        double rhs = 0;
        for(Literal singleVar : singleVars) {
            if(!singleVar.isPositive()) {
                rhs = rhs - 1;
            }
        }
        this.addConstraint(expr2, lessEqual, rhs);
    }


    /**
     * w positive and disjunction:
     * sum k * aggregatedVars_positive - sum k * aggregatedVars_negative + (-1)_{if single var negative} sum singleVars - z >= - (Sum_{negative aggVariables} 1) * k - (sum_{negative singleVars} 1)
     * 0 <= z <= k
     * 
     * @param model
     * @return
     * @throws GRBException
     */
    private void addAggregatedPositiveDisjunctionConstraint(double weight, ArrayList<Literal> aggregatedVars, ArrayList<Literal> singleVars, String z) throws ILPException
    {
        int k = singleVars.size();
        ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();

        for(Literal aggAxiom : aggregatedVars) {
            if(aggAxiom.isPositive()) {
                // sum k * aggregatedVars_positive
                expr.add(new ILPVariable(aggAxiom.getName(), k, false));
            } else {
                // - sum k * aggregatedVars_negative
                expr.add(new ILPVariable(aggAxiom.getName(), -k, false));
            }
        }

        // (-1)_{if single var negative} sum singleVars

        for(Literal singleVar : singleVars) {
            double one_or_minus_one = -1;
            if(singleVar.isPositive())
                one_or_minus_one = 1;
            expr.add(new ILPVariable(singleVar.getName(), one_or_minus_one, false));
        }

        // - z
        expr.add(new ILPVariable(z, -1, true));
        /*
         * for(int i =0; i<k; i++){
         * expr.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
         * }
         */

        // >=
        ILPOperator greaterEqual = ILPOperator.GEQ;

        // - (Sum_{negative aggVariables} 1) * k - (sum_{negative singleVars} 1)
        double rhs = 0;
        for(Literal aggAxiom : aggregatedVars) {
            if(!aggAxiom.isPositive()) {
                rhs = rhs - k;
            }
        }
        for(Literal singAxiom : singleVars) {
            if(!singAxiom.isPositive()) {
                rhs = rhs - 1;
            }
        }

        this.addConstraint(expr, greaterEqual, rhs);

    }


    /**
     * Builds cardinality restrictions of the form:
     * 
     * x1 + x2 + ... + xN <= cardinality
     * 
     * and adds it to the ILP.
     * 
     * @param variableNames
     * @param cardinality
     * @throws ILPException
     */
    public boolean addCardinalityConstraint(Set<String> variableNames, boolean lessEqual, int cardinality) throws ILPException
    {

        // build constraint
        ArrayList<ILPVariable> expr = new ArrayList<ILPVariable>();

        ILPOperator operator = ILPOperator.LEQ;
        if(!lessEqual) {
            operator = ILPOperator.GEQ;
        }
        for(String varName : variableNames) {
            expr.add(new ILPVariable(varName, 1, false));
        }
        // add constraint
        this.addConstraint(expr, operator, (double) cardinality);

        return true;
    }


    /**
     * If the soft formula has not already been added, it creates a new z variable with the given weight.
     * If the soft formula already exists, the weight is just updated.
     * 
     * @param weight
     * @param dublicateDetection
     * @return
     * @throws ILPException
     */
    private String getNextZ(double weight) throws ILPException
    {
        return this.getNextZ(weight, 1.0);
    }


    /**
     * Creates a new integer z variable. Used for the aggregated soft constraints.
     * 
     * private GRBVar getNextIntegerZ(double weight) throws ILPException{
     * return this.getNextIntegerZ(weight, GRB.INFINITY);
     * }/
     * 
     * /**
     * Creates a new integer z variable. Used for the aggregated soft constraints.
     */
    private String getNextZ(double weight, double upperbound) throws ILPException
    {
        StringBuilder varName = new StringBuilder();
        varName.append("z").append(this.zVarIndex);
        this.zVarIndex++;

        try {
            this.addVariable(varName.toString(), weight, 0.0d, upperbound, false, true);
        } catch(ILPException e) {
            e.printStackTrace();
            throw new ILPException("Failed to set the next z variable variable. " + e.getMessage());
        }
        return varName.toString();
    }


    /**
     * Transforms a given list of integer values so that it only returns
     * (1) those where the integer solution is 1 and
     * (2) those which are no added 'z' variables.
     * 
     * @param input
     * @return
     */
    public ArrayList<String> returnTrueGroundings(Map<String, Integer> input)
    {
        ArrayList<String> solutionVars = new ArrayList<String>();
        for(Entry<String, Integer> set : input.entrySet()) {
            int x = set.getValue();
            if(x == 1) {
                String varName = set.getKey();
                try {
                    Integer.parseInt(varName.substring(1));
                } catch(NumberFormatException e) {
                    solutionVars.add(varName);
                }
            }
            // System.out.println(variables[i].get(StringAttr.VarName) + " " +x);
        }
        return solutionVars;
    }


    /**
     * Solves the ILP, and
     * transforms the results so that it returns only true groundings.
     * 
     * @return
     * @throws SolveException
     */
    public ArrayList<String> solve() throws SolveException
    {
        return this.returnTrueGroundings(this.optimizeILP());
    }


    /**
     * Sets the start value of the given variables in varNames to 1.
     * 
     * @param varNames
     * @throws ILPException
     */
    public abstract void addStartValues(List<String> varNames) throws ILPException;


    /**
     * Writes model to file. File has to end with .ilp or lp
     * 
     * @param filename
     * @throws ILPException
     */
    public abstract void writeModelToFile(String filename) throws ILPException;


    /**
     * Returns all the variables available in the current ILP including the "zzz" variables.
     * 
     * The double value contains the actual weight of the variable in the target function.
     * 
     * @return
     * @throws ILPException
     */
    /*
     * public HashMap<String, Double> getVariables() throws ILPException{
     * HashMap<String,Double> result = new HashMap<String,Double>();
     * for(String varName : variables.keySet()){
     * GRBVar var = variables.get(varName);
     * try {
     * result.put(varName, var.get(DoubleAttr.Obj));
     * } catch (GRBException e) {
     * e.printStackTrace();
     * throw new ILPException("The objective value could not be read of variable " +varName);
     * }
     * }
     * return result;
     * }
     */

    public abstract void close() throws ILPException;


    public abstract void initialize() throws ILPException;


    /**
     * Adds boolean or integer variable. If upperBound is 1 or lower, then the variable is boolean. If upperBound is larger than 1, the variable is integer.
     * 
     * @param varName
     * @param weight
     *            weight in objective
     * @param upperBound
     *            upper bound of the variable (if > 1 then integer variable)
     * @param override
     *            if true, then the set values are overridden in case the variable already exists.
     * @param isZVar
     *            the connectors can handle zVars different from usual vars
     * @throws ILPException
     */
    public abstract void addVariable(String varName, double objWeight, double lowerBound, double upperBound, boolean override, boolean isZVar) throws ILPException;


    /**
     * Add ILP constraint. All containing variables have been previously added with the 'addVariable()' function.
     * 
     * @param lhs
     * @param operator
     * @param rhs
     * @throws ILPException
     */
    public abstract void addConstraint(ArrayList<ILPVariable> lhs, ILPOperator operator, double rhs) throws ILPException;


    /**
     * Solves the ILP and returns the integer values for each solution.
     * 
     * @return solution.
     * @throws ILPException
     *             thrown if infeasible.
     */
    public abstract Map<String, Integer> optimizeILP() throws ILPException;


    /**
     * Gets the objective of the ILP from the last solve call.
     * 
     * @return
     */
    public abstract double getObjectiveValue();

}
