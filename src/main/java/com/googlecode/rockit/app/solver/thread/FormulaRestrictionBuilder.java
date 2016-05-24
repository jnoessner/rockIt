package com.googlecode.rockit.app.solver.thread;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.aggregate.simple.AggregationManagerOptimalColumnImpl;
import com.googlecode.rockit.app.solver.numerical.MathExpression;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.conn.sql.MySQLConnector;
import com.googlecode.rockit.exception.DatabaseException;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableAbstract;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableArithmeticExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableString;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;


public class FormulaRestrictionBuilder extends RestrictionBuilder
{

    private FormulaHard                 formula             = null;
    private HashMap<Literal, Literal>   literals            = new HashMap<Literal, Literal>();

    private ArrayList<Clause>           clauses             = new ArrayList<Clause>();

    private MySQLConnector              sql                 = null;

    private boolean                     trackLiterals       = false;

    private int                         restrictionCounter;
    private int                         restrictionCounterWithoutEvidence;
    private HashMap<Literal, Literal>   evidenceAxioms      = null;

    private AggregationManager          aggregationManager  = null;

    private boolean                     foundOneRestriction = false;

    private Map<String, List<String[]>> inferredValues      = new HashMap<>();


    public FormulaRestrictionBuilder(FormulaHard formula)
    {
        this.formula = formula;
        this.reset();
        literals = new HashMap<Literal, Literal>();
        this.foundOneRestriction = false;
    }


    public void reset()
    {
        if(formula instanceof FormulaSoft) {
            if(Parameters.USE_CUTTING_PLANE_AGGREGATION) {
                this.aggregationManager = new AggregationManagerOptimalColumnImpl(formula.getRestrictions().size());
            }

        }

    }


    public ArrayList<Clause> getClauses()
    {
        return this.clauses;
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#getFormula()
     */
    @Override
    public FormulaHard getFormula()
    {
        return formula;
    }


    public void run()
    {
        this.generateRestrictions();
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#generateRestrictions()
     */
    @Override
    public void generateRestrictions()
    {
        clauses = new ArrayList<Clause>();
        literals = new HashMap<Literal, Literal>();
        foundOneRestriction = false;
        int additionalRestrictionCounter = 0;

        ResultSet res;
        boolean isHard = true;
        boolean doubleVariableExists = false;
        FormulaSoft formulaSoft = null;
        if(formula instanceof FormulaSoft) {
            isHard = false;
            formulaSoft = (FormulaSoft) formula;
            if(((FormulaSoft) formula).getDoubleVariable() != null) {
                doubleVariableExists = true;
            }
        }

        try {
            res = sql.executeSelectQuery(formula.getSqlQuery());

            HashMap<String, Integer> varIndex = new HashMap<String, Integer>();
            int i = 0;
            for(VariableType var : formula.getForVariables()) {
                varIndex.put(var.getName(), i);
                i++;
            }
            int numberOfVars = varIndex.size();

            ArrayList<String[]> valuesToInsert = new ArrayList<String[]>();
            ArrayList<Double> doubleToInsert = new ArrayList<Double>();
            try {
                while(res.next()) {

                    // if this restriction has not been added yet.
                    restrictionCounterWithoutEvidence++;

                    // get information out of axiom
                    String[] resTemp = new String[numberOfVars];
                    i = 1;
                    if(formula instanceof FormulaSoft)
                        i = 2;
                    for(int j = i; j < (i + numberOfVars); j++) {
                        resTemp[j - i] = res.getString(j);
                        // if(formula instanceof FormulaSoft && ((FormulaSoft) formula).getWeight()<0)System.out.println("ohhh mmmmyyy goood " + res.getString(j));
                    }

                    if(formula.isCuttingPlaneInferenceUsed())
                        valuesToInsert.add(resTemp);

                    double weight = 0;
                    if(formula instanceof FormulaSoft) {
                        weight = res.getDouble(1);
                        if(formula.isCuttingPlaneInferenceUsed())
                            doubleToInsert.add(weight);
                    }

                    Clause clause = this.getRestrictionFromSQLResult(weight, varIndex, resTemp, isHard);

                    if(clause != null && clause.getRestriction().size() > 0) {
                        foundOneRestriction = true;
                        additionalRestrictionCounter++;
                        if(Parameters.USE_CUTTING_PLANE_AGGREGATION && !doubleVariableExists && !isHard) {
                            this.aggregationManager.addClauseForAggregation(clause, formulaSoft);
                        } else {
                            this.clauses.add(clause);
                        }
                    }
                    /*
                     * if(this.restrictionCounter%10000==0){
                     * System.out.print(".");
                     * }
                     */
                }

                if(Parameters.USE_CUTTING_PLANE_AGGREGATION && !doubleVariableExists && !isHard) {
                    this.aggregationManager.calculateAggregation();
                }

                res.getStatement().close();
                res.close();
            } catch(SQLException e) {
                throw new DatabaseException("FormulaRestrictionBuilder: Problems in reading the SQL result.");
            }
            this.restrictionCounter = this.restrictionCounter + additionalRestrictionCounter;
            if(Parameters.DEBUG_OUTPUT) {
                System.out.print(formula);
                // System.out.println(clauses);
                System.out.print("Restrictions found: (new) " + additionalRestrictionCounter + " (overall without evidence) " + restrictionCounter + " (overall) " + restrictionCounterWithoutEvidence);
                if(Parameters.USE_CUTTING_PLANE_AGGREGATION) {
                    if(this.aggregationManager != null) {
                        System.out.println(" (aggregated) " + this.aggregationManager.getNumberOfAggregatedClauses() + " clauses.");
                    }
                } else {
                    System.out.println();
                }
            }

            if(formula.isCuttingPlaneInferenceUsed()) {
                StringBuilder filename = new StringBuilder();
                filename.append(Parameters.TEMP_PATH).append(formula.getName()).append("_tempfile.db");

                if(formula instanceof FormulaSoft) {
                    sql.addData(formula.getName(), doubleToInsert, valuesToInsert, filename.toString());
                } else {
                    sql.addData(formula.getName(), valuesToInsert, filename.toString());
                }
            }

            for(String tableName : inferredValues.keySet()) {
                sql.addData(tableName, (ArrayList<String[]>) inferredValues.get(tableName), tableName + "_temp.db");
            }

        } catch(SolveException e1) {
            e1.printStackTrace();
        }

    }


    private Clause getRestrictionFromSQLResult(double weight, HashMap<String, Integer> varIndex, String[] resTemp, boolean isHard) throws SQLException
    {
        ArrayList<Literal> axiomsForClause = new ArrayList<Literal>();
        boolean isConjunction = formula.isConjunction();
        for(PredicateExpression expr : formula.getRestrictions()) {
            boolean isPositive = expr.isPositive();
            boolean isNumerical = false;
            List<String> groundValues = new ArrayList<String>();
            String predicate = expr.getPredicate().getName();
            if(MathExpression.isNumericPredicate(predicate)) {
                isNumerical = true;
            }

            StringBuilder axiomBuilder = new StringBuilder();
            axiomBuilder.append(predicate);
            for(VariableAbstract var : expr.getVariables()) {
                if(var instanceof VariableType && !(var instanceof VariableArithmeticExpression)) {
                    axiomBuilder.append("|");
                    axiomBuilder.append(resTemp[varIndex.get(var.getName())]);
                    groundValues.add(resTemp[varIndex.get(var.getName())]);
                }
                if(var instanceof VariableString) {
                    axiomBuilder.append("|");
                    axiomBuilder.append(var.getName());
                    groundValues.add(var.getName());
                }
                if(var instanceof VariableArithmeticExpression) {
                    VariableArithmeticExpression varX = (VariableArithmeticExpression) var;
                    double value = varX.evaluate(varIndex, resTemp);
                    String huKey = HerbrandUniverse.getInstance().getKey(String.valueOf(value));
                    axiomBuilder.append("|");
                    axiomBuilder.append(huKey);
                    groundValues.add(huKey);

                    // collect inferred values
                    String tableName = ((VariableArithmeticExpression) var).getType() + "_typePred";
                    List<String[]> valuesForInsert = null;
                    if(!inferredValues.containsKey(tableName)) {
                        inferredValues.put(tableName, new ArrayList<String[]>());
                    }
                    valuesForInsert = inferredValues.get(tableName);
                    valuesForInsert.add(new String[] { huKey });

                }
            }

            Literal newAxiom = new Literal(axiomBuilder.toString(), isPositive);
            // check evidence: dublicate detection required.
            if((Parameters.LEVERAGE_EVIDENCE && evidenceAxioms != null) || isNumerical) {
                if(isNumerical) {
                    boolean eval = false;
                    try {
                        eval = MathExpression.evaluate(predicate, groundValues);
                    } catch(Exception e) {
                        System.err.println("Formula Restriction Builder");
                        System.err.println(newAxiom);
                        System.err.println(predicate);
                        for(String s : groundValues) {
                            System.err.println("\t" + s + "\t" + HerbrandUniverse.getInstance().getConstant(s));
                        }
                        System.exit(-1);
                    }
                    if(!isConjunction) {
                        if((isPositive && eval) || (!isPositive && !eval)) {
                            // formula is not violated
                            return null;
                        } else {
                            // ignore the current axiom (it is not positive)
                        }
                    } else {
                        if((isPositive && eval) || (!isPositive && !eval)) {
                            // do nothing
                        } else {
                            System.err.println("TODO: numeric values && conjunctions .. (current clause is violated...) [FormulaRestrionBuilder]");
                            System.exit(-1);
                        }
                    }
                } else {
                    Literal evidenceAxiom = this.evidenceAxioms.get(newAxiom);
                    if(evidenceAxiom != null) {
                        // if we have a disjunction AND evidenceAxiom and new Axiom are both negated or both positive
                        if(!isConjunction && (evidenceAxiom.isPositive() == newAxiom.isPositive())) {
                            // then we know that it will always be true --> no need to add the restriction.
                            return null;
                        } else if(!isConjunction && (evidenceAxiom.isPositive() == newAxiom.isPositive())) {
                            // then we know that we can omit the current newAxiom.
                        } else if(isConjunction) {
                            // TODO think of conjunctions
                            axiomsForClause.add(newAxiom);
                            if(trackLiterals)
                                this.literals.put(newAxiom, newAxiom);
                        }

                    } else {
                        axiomsForClause.add(newAxiom);
                        if(trackLiterals)
                            this.literals.put(newAxiom, newAxiom);
                    }
                }
            } else {
                // if we do not check evidence, we just add the axiom.
                axiomsForClause.add(newAxiom);
                if(trackLiterals)
                    this.literals.put(newAxiom, newAxiom);
            }
        }
        Clause clause = new Clause(weight, axiomsForClause, isHard);

        return clause;
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#addConstraints(com.googlecode.rockit.conn.ilpSolver.GurobiConnector)
     */
    @Override
    public void addConstraints(ILPConnector con) throws ILPException, SolveException
    {

        if(formula instanceof FormulaSoft) {
            if(Parameters.USE_CUTTING_PLANE_AGGREGATION && ((FormulaSoft) formula).getDoubleVariable() == null) {
                aggregationManager.addConstraintsToILP(con);
            } else {
                for(Clause c : this.clauses) {
                    con.addSingleSoftConstraint(c.getWeight(), c.getRestriction(), formula.isConjunction());
                }
            }
        } else {
            for(Clause c : this.clauses) {
                con.addHardConstraint(c.getRestriction());
            }
        }
        this.reset();

    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#isFoundOneRestriction()
     */
    @Override
    public boolean isFoundOneRestriction()
    {
        return foundOneRestriction;
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#foundNoRestriction()
     */
    @Override
    public void foundNoRestriction()
    {
        foundOneRestriction = false;
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#setTrackLiterals(boolean)
     */
    @Override
    public void setTrackLiterals(boolean trackLiterals)
    {
        this.trackLiterals = trackLiterals;
        if(!trackLiterals) {
            literals = new HashMap<Literal, Literal>();
        }
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#getLiterals()
     */
    @Override
    public HashMap<Literal, Literal> getLiterals()
    {
        return literals;
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#setEvidenceAxioms(java.util.HashMap)
     */
    @Override
    public void setEvidenceAxioms(HashMap<Literal, Literal> evidence)
    {
        this.evidenceAxioms = evidence;
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.app.solver.thread.RestrictionBuilder#setSql(com.googlecode.rockit.conn.sql.MySQLConnector)
     */
    @Override
    public void setSql(MySQLConnector sql)
    {
        this.sql = sql;
    }


    @Override
    public AggregationManager getAggregationManager()
    {
        return aggregationManager;
    }

}
