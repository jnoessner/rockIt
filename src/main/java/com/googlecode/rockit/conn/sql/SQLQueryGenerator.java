package com.googlecode.rockit.conn.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.javaAPI.formulas.FormulaAbstract;
import com.googlecode.rockit.javaAPI.formulas.FormulaCardinality;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
import com.googlecode.rockit.javaAPI.formulas.expressions.IfExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.EqualVariableExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.ThresholdExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableAbstract;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableArithmeticExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableDouble;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableString;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;


/**
 * The factory creates SQL queries to determine the groundings of logic formulas (soft, hard, and cardinality).
 * 
 * The SQL queries are stored in formula.setSQLQuery().
 * 
 * @author Jan
 *
 */
public class SQLQueryGenerator
{
    /**
     * This Function returns ready baked sql statements.
     * 
     * With these formulas it can be checked which variables violate restriction
     * formulas. The output is in a format that directly results in ILP
     * restrictions.
     * 
     * Basically, for every hidden predicate in the restriction formula an
     * expression
     * [+|-]||[PredicateName]|[value1]|...|[valueN]||[+|-]||[NextPredicate...
     * 
     * The first part identifies if this variable will be positive "+" or
     * negative "-" in the ILP. The second part perfectly fits with the naming
     * conventions of variables. Thus, the expression says something like:
     * "add/substract variableX" to ILP.
     * 
     * Example Query which is generated: SELECT CONCAT('-||subsumes|', xx.c1,
     * '|', xx.c2, '||','-||subsumes|', xx.c2, '|', xx.c3, '||','+||subsumes|',
     * xx.c1, '|', xx.c3) FROM ( SELECT x0.field0 as c1,x2.field0 as
     * c3,x1.field0 as c2 FROM class x0,class x1,class x2,subsumes y0,subsumes
     * y1 WHERE y0.field0 = x0.field0 AND y0.field1 = x1.field0 AND y1.field0 =
     * x1.field0 AND y1.field1 = x2.field0 ) as xx LEFT JOIN subsumes subsumes2
     * ON subsumes2.field0 = xx.c1 AND subsumes2.field1 = xx.c3 WHERE
     * subsumes2.field0 IS NULL
     * 
     * 
     * @param model
     * @return
     * @throws ParseException
     * @throws SolveException
     */
    public static HashSet<FormulaHard> getSQLQueriesForHardFormulas(Model model) throws ParseException, SolveException
    {
        HashSet<FormulaHard> sqlQueries = new HashSet<FormulaHard>();
        // if it is a Restriction Formula
        for(FormulaAbstract formulaAbstract : model.getFormulas()) {
            if(formulaAbstract.getClass().equals(FormulaHard.class)) {
                FormulaHard formula = (FormulaHard) formulaAbstract;

                // SELECT PART
                StringBuilder select = new StringBuilder();
                select.append("SELECT ");

                // sign if predicate is positive or negative
                select.append(getSQLQuerySelectSnippedVariables(formula, "xx"));

                StringBuilder sqlQuery = new StringBuilder();
                // Put select part together
                sqlQuery.append(select.toString());
                // get the sql query from the sqlstatementwithoutselect
                String sqlWithoutSelect = "";
                // only formulas cutting plane inference which do not produce
                // evidence (restriction size > 1).
                if(formula.isCuttingPlaneInferenceUsed() && formula.getRestrictions().size() > 1) {
                    sqlWithoutSelect = SQLQueryGenerator.getSQLStatementWithoutSelect(formula, true, true);
                } else {
                    formula.useCuttingPlaneInference(false);
                    sqlWithoutSelect = SQLQueryGenerator.getSQLStatementWithoutSelectWithoutCPI(formula, true, false);
                }
                sqlQuery.append(sqlWithoutSelect);

                if(Parameters.DEBUG_OUTPUT)
                    System.out.println("query hard: " + sqlQuery);
                formula.setSqlQuery(sqlQuery.toString());
                sqlQueries.add(formula);
            }
        }
        return sqlQueries;
    }


    public static HashSet<FormulaHard> getSQLQueriesForSoftFormulas(Model model) throws ParseException, SolveException
    {
        HashSet<FormulaHard> sqlQueries = new HashSet<FormulaHard>();
        // if it is a Restriction Formula
        for(FormulaAbstract formulaAbstract : model.getFormulas()) {
            if(formulaAbstract.getClass().equals(FormulaSoft.class)) {
                FormulaSoft formula = (FormulaSoft) formulaAbstract;
                if(formula.isCuttingPlaneInferenceUsed() && (formula.getRestrictions().size() > 1 || formula.getWeight() < 0)) {
                    Double weight = formula.getWeight();
                    // if we have individual weights for each grounding, we do
                    // not know if they are positive or negative.
                    // Thus, we just include them all.
                    if(weight == null) {
                        formula.useCuttingPlaneInference(false);
                        formula.setSqlQuery(getSQLQueryForSoftFormulaAll(formula));
                    }
                    // if the weight is greater than 0 than we use the JOINs
                    else if(
                    // formula.getRestrictions().size()<=1 ||
                    (weight > 0 && formula.isDisjunction()) || (weight < 0 && formula.isConjunction())) {
                        formula.setSqlQuery(getSQLQueryForSoftFormulaJoin(formula));
                    } else {
                        formula.setSqlQuery(getSQLQueryForSoftFormulaUnion(formula));
                    }
                } else {
                    Double weight = formula.getWeight();
                    if(weight == null || weight != 0) {
                        formula.useCuttingPlaneInference(false);
                        formula.setSqlQuery(getSQLQueryForSoftFormulaAll(formula));
                    }
                }
                sqlQueries.add(formula);
            }
        }
        return sqlQueries;
    }


    private static String getSQLQueryForSoftFormulaUnion(FormulaSoft formula) throws ParseException, SolveException
    {
        // SELECT PART
        StringBuilder surroundQuery = new StringBuilder();
        surroundQuery.append("SELECT  ");

        surroundQuery.append(getSQLQuerySelectSnippedWeight(formula, "yy")).append(", ");

        // sign if predicate is positive or negative
        surroundQuery.append(getSQLQuerySelectSnippedVariables(formula, "yy"));

        surroundQuery.append(" FROM ( ");
        // System.out.println(select.toString());

        // SMALL INNER QUERIES CONNECTED WITH UNION ALL
        StringBuilder innerSelect = new StringBuilder();
        innerSelect.append("SELECT ");
        int i = 0;
        for(VariableType var : formula.getForVariables()) {
            innerSelect.append("xx.").append(var.getName()).append(" as ").append("`").append(var.getName()).append("`");
            if(i < formula.getForVariables().size() - 1) {
                innerSelect.append(",");
            }
            innerSelect.append(" ");
            i++;
        }

        ArrayList<String> innerRestQueries = new ArrayList<String>();
        // copy actual formula
        FormulaSoft copiedFormula = new FormulaSoft(formula.getName(), formula.getForVariables(), formula.getIfExpressions(), formula.getDoubleVariable(), new ArrayList<PredicateExpression>(), formula.isConjunction());
        copiedFormula.setWeight(formula.getWeight());
        for(int j = 0; j < formula.getRestrictions().size(); j++) {
            if(j >= 1) {
                PredicateExpression expr = copiedFormula.getRestrictions().get(j - 1);
                expr.setPositive(!expr.isPositive());
            }
            copiedFormula.addRestriction(formula.getRestrictions().get(j));
            String innerQu = SQLQueryGenerator.getSQLStatementWithoutSelect(copiedFormula, false, true);
            innerRestQueries.add(innerQu);
        }
        // undo positive negative changes
        for(int j = 0; j < formula.getRestrictions().size() - 1; j++) {
            PredicateExpression a = formula.getRestrictions().get(j);
            a.setPositive(!a.isPositive());
        }

        StringBuilder sqlQuery = new StringBuilder();
        // Encompassing part
        sqlQuery.append(surroundQuery.toString());

        for(int j = 0; j < innerRestQueries.size(); j++) {
            sqlQuery.append(innerSelect);
            sqlQuery.append(innerRestQueries.get(j));
            if(j < innerRestQueries.size() - 1) {
                sqlQuery.append(" UNION ALL ");
            }
        }

        sqlQuery.append(") as yy");

        if(Parameters.DEBUG_OUTPUT)
            System.out.println("query soft union: " + sqlQuery);
        return sqlQuery.toString();

    }


    private static String getSQLQueryForSoftFormulaJoin(FormulaSoft formula) throws SolveException
    {
        // SELECT PART
        StringBuilder select = new StringBuilder();
        select.append("SELECT  ");

        select.append(getSQLQuerySelectSnippedWeight(formula, "xx")).append(", ");

        // sign if predicate is positive or negative
        select.append(getSQLQuerySelectSnippedVariables(formula, "xx"));// .append(", ");
        // System.out.println(select.toString());

        StringBuilder sqlQuery = new StringBuilder();
        // Put select part together
        sqlQuery.append(select.toString());
        // get the sql query from the sqlstatementwithoutselect
        String sqlWithoutSelect = SQLQueryGenerator.getSQLStatementWithoutSelect(formula, true, true);
        sqlQuery.append(sqlWithoutSelect);

        if(Parameters.DEBUG_OUTPUT)
            System.out.println("query soft join: " + sqlQuery);
        return sqlQuery.toString();
    }


    private static String getSQLQueryForSoftFormulaAll(FormulaSoft formula) throws ParseException, SolveException
    {
        // SELECT PART
        StringBuilder select = new StringBuilder();
        select.append("SELECT ");

        select.append(getSQLQuerySelectSnippedWeight(formula, "xx")).append(", ");
        // sign if predicate is positive or negative
        select.append(getSQLQuerySelectSnippedVariables(formula, "xx"));
        // System.out.println(select.toString());

        StringBuilder sqlQuery = new StringBuilder();
        // Put select part together
        sqlQuery.append(select.toString());
        // get the sql query from the sqlstatementwithoutselect
        String sqlWithoutSelect = SQLQueryGenerator.getSQLStatementWithoutSelectWithoutCPI(formula, true, false);
        sqlQuery.append(sqlWithoutSelect);

        if(Parameters.DEBUG_OUTPUT)
            System.out.println("query soft all: " + sqlQuery);
        return sqlQuery.toString();
    }


    private static String getSQLQuerySelectSnippedWeight(FormulaSoft formula, String prefix)
    {
        StringBuilder selectSnipped = new StringBuilder();
        // double variable if exists
        if(formula.getDoubleVariable() != null) {
            selectSnipped.append("(").append(prefix).append(".").append(formula.getDoubleVariable().getName());
        } else {
            selectSnipped.append("(1 ");
        }
        // ...multiplied with the weights of the formula
        if(formula.getWeight() != null) {
            double weightValue = formula.getWeight();
            selectSnipped.append(" * ");
            selectSnipped.append(weightValue);
        } else {
            selectSnipped.append(" * 1");
        }
        selectSnipped.append(") as weight");
        return selectSnipped.toString();
    }


    /*
     * private String getSQLQuerySelectSnippedRestrictionILP(FormulaHard
     * formula, String prefix){ StringBuilder selectSnipped = new
     * StringBuilder(); int preCount = 0; selectSnipped.append("CONCAT(");
     * for(PredicateExpression predicateExpr : formula.getRestrictions()){
     * if(predicateExpr.isPositive()){ selectSnipped.append("'+||"); }else{
     * selectSnipped.append("'-||"); }
     * selectSnipped.append(predicateExpr.getPredicate().getName());
     * selectSnipped.append("|', "); int varCount = 0; for(VariableAbstract
     * variable : predicateExpr.getVariables()){ if(variable instanceof
     * VariableString){
     * selectSnipped.append("'").append(variable.getName()).append("'"); }else{
     * selectSnipped.append(prefix).append(".");
     * selectSnipped.append(variable.getName()); }
     * if(varCount<predicateExpr.getVariables().size()-1){
     * selectSnipped.append(", '|', "); }else{ // do nothing } varCount++; }
     * if(preCount<formula.getRestrictions().size()-1){
     * selectSnipped.append(", '||',"); } preCount++; }
     * selectSnipped.append(") as restriction"); return
     * selectSnipped.toString(); }
     */

    private static String getSQLQuerySelectSnippedVariables(FormulaAbstract formula, String prefix)
    {
        StringBuilder selectSnipped = new StringBuilder();
        // selectSnipped.append("CONCAT(");
        int i = 0;
        for(VariableType v : formula.getForVariables()) {
            selectSnipped.append(prefix).append(".").append(v.toString());
            if(i < formula.getForVariables().size() - 1) {
                selectSnipped.append(", ");
            }
            i++;
        }
        // selectSnipped.append(") as dublicateDetection");
        return selectSnipped.toString();
    }


    /**
     * 
     * Build Query for FormulaCardinality. The idea is to print every
     * possibility and to execute the less equal or greater equal later in the
     * java program code.
     * 
     * Therefore, the output does not only need the String with the SQL Query,
     * but also the corresponding FormulaCardinality.
     * 
     * The first row gives the key of the cardinality:
     * [formulaName][[FixValue1]...[FixValueN] where FixValueI is the value of
     * the variables which are not in the over part.
     * 
     * The following rows (number equals number of PredicateExpressions in
     * formula) each stand for one variable to add. The variables are already in
     * the correct format: [HiddenPredName]|[Value1]|...|[ValueN]
     * 
     * Example:
     * 
     * Select Part: - FROM Part: Similar to Restriction Formula WHERE Part:
     * Similar to Restriction Formula GROUP BY: take those variables which are
     * in card part. HAVING: count > variableName
     * 
     * formula card1 for Property2 p2,Concept1 c1,Property1 p1,Concept2 c2 if
     * prop1(p1) & prop2(p2) & concept1(c1) & concept2(c2) card c1, p1:
     * hiddenCon(c1, c2) & hiddenProp(p1, p2) <= 3;
     * 
     * -->
     * 
     * 
     * SELECT CONCAT(card1.c2, card1.p2) as count, CONCAT('hiddenCon|',
     * card1.c1, '|', card1.c2), CONCAT('hiddenProp|', card1.p1, '|', card1.p2)
     * FROM hiddenCon hiddenCon0, hiddenProp hiddenProp1, card1 WHERE
     * hiddenCon0.field1 = card1.c2 AND hiddenProp1.field1 = card1.p2 AND
     * hiddenCon0.field0 = card1.c1 AND hiddenProp1.field0=card1.p1 ORDER BY
     * count
     * 
     * @param model
     * @return
     * @throws ParseException
     * @throws SolveException
     */
    public static HashSet<FormulaCardinality> getSQLQueriesForCardinalityFormula(Model model) throws ParseException, SolveException
    {

        HashSet<FormulaCardinality> sqlQueries = new HashSet<FormulaCardinality>();
        for(FormulaAbstract formulaAbstract : model.getFormulas()) {
            if(formulaAbstract.getClass().equals(FormulaCardinality.class)) {

                FormulaCardinality formula = (FormulaCardinality) formulaAbstract;

                // SELECT statement
                // =================
                StringBuilder select = new StringBuilder();
                select.append("SELECT ");
                // sign if predicate is positive or negative
                select.append("CONCAT('").append(formula.getName()).append("',");

                for(VariableType var : formula.getForVariables()) {
                    if(!formula.getOverVariables().contains(var)) {
                        select.append("xx.");
                        select.append(var.getName());
                        select.append(",");
                    }
                }
                select.deleteCharAt(select.length() - 1);
                select.append(") as count, ");
                int preCount = 0;
                for(PredicateExpression predicateExpr : formula.getRestrictions()) {
                    select.append("CONCAT('");
                    select.append(predicateExpr.getPredicate().getName());
                    select.append("|', ");
                    int varCount = 0;
                    for(VariableAbstract variable : predicateExpr.getVariables()) {
                        if(variable instanceof VariableString) {
                            select.append("'").append(variable.getName()).append("'");
                        } else {
                            select.append("xx.");
                            select.append(variable.getName());
                        }

                        if(varCount < predicateExpr.getVariables().size() - 1) {
                            select.append(", '|', ");
                        } else {
                            // do nothing
                        }
                        varCount++;
                    }
                    if(preCount < formula.getRestrictions().size() - 1) {
                        select.append("), ");
                    }
                    preCount++;
                }
                select.append(")");

                StringBuilder sqlQuery = new StringBuilder();
                sqlQuery.append(select);

                // get sql query without select
                String sqlQueryWithoutSelect = "";
                if(formula.isCuttingPlaneInferenceUsed()) {
                    sqlQueryWithoutSelect = SQLQueryGenerator.getSQLStatementWithoutSelect(formula, false, true);
                } else {
                    sqlQueryWithoutSelect = SQLQueryGenerator.getSQLStatementWithoutSelectWithoutCPI(formula, false, false);
                }
                sqlQuery.append(sqlQueryWithoutSelect);

                // ORDER BY part
                // important to make counting possible later on.
                sqlQuery.append(" ORDER BY count");
                formula.setSqlQuery(sqlQuery.toString());
                sqlQueries.add(formula);
            }
        }
        return sqlQueries;

    }


    public static String getSQLStatementWithoutSelectWithoutCPI(FormulaHard formula, boolean reversePositiveAndNegative, boolean dublicateDetection) throws SolveException, ParseException
    {
        ArrayList<PredicateExpression> restrictions = formula.getRestrictions();
        formula.setRestrictions(new ArrayList<PredicateExpression>());
        String result = SQLQueryGenerator.getSQLStatementWithoutSelect(formula, reversePositiveAndNegative, dublicateDetection);
        formula.setRestrictions(restrictions);
        return result;
    }


    /**
     * This function returns the from and where parts of the select statement
     * for all kinds of formulas, Soft, Hard, Cardinality and Restriction
     * Formulas. Every query will have a complete inner query sourrounded by a
     * very simple outer query.
     * 
     * Example:
     * 
     * formula [cpi=yes] f1 for person a1,paper a3,paper a2,cat a4 if wrote(a1,
     * a3) & wrote(a1, a2) & cat_typePred(a4) soft category(a3, a4) |
     * !category(a2, a4) * w1;
     * 
     * !wrote(a1,a3) v !wrote(a1,a2) v category(a3,a4) v !category(a2,a4)
     * 
     * will be:
     * 
     * 
     * FROM (SELECT x0.field0 as a1,x0.field1 as a3,x1.field1 as a2,x2.field0 as
     * a4 FROM wrote as x0 INNER JOIN wrote x1 ON x0.field0 = x1.field0 INNER
     * JOIN cat_typePred x2 LEFT JOIN category y0 ON y0.field0 = x0.field1 AND
     * y0.field1 = x2.field0 INNER JOIN category y1 ON y1.field0 = x1.field1 AND
     * y1.field1 = x2.field0 AND y1.field1 = y0.field1 WHERE y0.field0 IS NULL)
     * as xx
     * 
     * 
     * @param formula
     * @param reversePositiveAndNegative
     *            in case of Hard formulas it has to be true; in case of Soft /
     *            Cardinality formulas false.
     * @return
     */
    public static String getSQLStatementWithoutSelect(FormulaHard formula, boolean reversePositiveAndNegative, boolean dublicateDetection) throws SolveException
    {
        // SELECT, FROM and WHERE Statements from inner query
        // here we only need to cover the positive ones --> they will be
        // negative...
        StringBuilder selectStatements = new StringBuilder();
        // from statements includes inner / outer join predicates
        StringBuilder fromStatements = new StringBuilder();
        ArrayList<String> whereStatements = new ArrayList<String>();

        HashMap<VariableAbstract, ArrayList<String>> databaseFieldsForVariable = new HashMap<VariableAbstract, ArrayList<String>>();

        // 1. PRECATEGORIZE EXPRESSIONS in positive and negative ones
        ArrayList<PredicateExpression> normalJoinExpressions = new ArrayList<PredicateExpression>();
        ArrayList<PredicateExpression> outerJoinExpressions = new ArrayList<PredicateExpression>();
        // If part of formula
        for(IfExpression ifExpression : formula.getIfExpressions()) {
            if(ifExpression instanceof PredicateExpression) {
                PredicateExpression predicateExpr = (PredicateExpression) ifExpression;
                if(predicateExpr.isPositive()) {
                    // normal join
                    normalJoinExpressions.add(predicateExpr);
                } else {
                    // outer join
                    outerJoinExpressions.add(predicateExpr);
                }
            }
        }
        // Restrictions part of formula
        for(PredicateExpression predicateExpr : formula.getRestrictions()) {
            if((predicateExpr.isPositive() && !reversePositiveAndNegative) || (!predicateExpr.isPositive() && reversePositiveAndNegative)) {
                // normal join
                normalJoinExpressions.add(predicateExpr);
            } else {
                // outer join
                outerJoinExpressions.add(predicateExpr);
            }
        }

        // 2. BUILD FROM part
        for(int i = 0; i < normalJoinExpressions.size(); i++) {
            StringBuilder fromPart = new StringBuilder();
            PredicateExpression expr = normalJoinExpressions.get(i);

            // FROM wrote as x0
            // INNER JOIN wrote x1
            if(i == 0) {
                fromPart.append(" FROM ");
            } else {
                fromPart.append(" INNER JOIN ");
            }
            fromPart.append("`").append(expr.getPredicate().getName()).append("`");
            fromPart.append(" x").append(i).append(" ");

            StringBuilder onPart = new StringBuilder();
            for(int j = 0; j < expr.getVariables().size(); j++) {
                VariableAbstract var = expr.getVariables().get(j);
                if(!(var instanceof VariableString)) {
                    StringBuilder varString = new StringBuilder();
                    varString.append("x").append(i).append(".");
                    if(var instanceof VariableType)
                        varString.append("field").append(j);
                    else if(var instanceof VariableDouble)
                        varString.append("value");
                    ArrayList<String> databaseFields = databaseFieldsForVariable.get(var);
                    if(databaseFields == null) {
                        databaseFields = new ArrayList<String>();
                        databaseFields.add(varString.toString());
                        databaseFieldsForVariable.put(var, databaseFields);
                    } else {
                        for(String databaseField : databaseFields) {
                            // if we are at position 1, we have to do the join
                            // predicates in the where clause
                            if(i == 0) {
                                StringBuilder newWhere = new StringBuilder();
                                newWhere.append(varString).append(" = ").append(databaseField);
                                whereStatements.add(newWhere.toString());
                            } else {
                                // else we have them as 'on's
                                if(onPart.length() == 0)
                                    onPart.append("ON ");
                                else
                                    onPart.append("AND ");
                                onPart.append(varString).append(" = ").append(databaseField).append(" ");
                            }
                        }
                        databaseFields.add(varString.toString());
                    }
                } else {
                    // Extra: If we have a string variable, we have to add it to
                    // sql
                    // code dublication from above
                    if(var instanceof VariableString) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(" x").append(i).append(".");
                        sb.append("field").append(j);
                        sb.append(" = ");
                        sb.append("'").append(var.getName()).append("'");
                        whereStatements.add(sb.toString());
                    }
                }
            }
            fromPart.append(onPart);
            fromStatements.append(fromPart);
        }

        for(int i = 0; i < outerJoinExpressions.size(); i++) {
            StringBuilder fromPart = new StringBuilder();
            PredicateExpression expr = outerJoinExpressions.get(i);

            // LEFT JOIN category y0

            // code dublication from normalJoinExpressions
            // ON y0.field0 = x0.field1 AND y0.field1 = x2.field0
            StringBuilder onPart = new StringBuilder();
            String oneNormalVar = null;
            for(int j = 0; j < expr.getVariables().size(); j++) {
                VariableAbstract var = expr.getVariables().get(j);
                if(!(var instanceof VariableString || var instanceof VariableArithmeticExpression)) {
                    StringBuilder varString = new StringBuilder();
                    varString.append("y").append(i).append(".");
                    if(var instanceof VariableType) {
                        varString.append("field").append(j);
                        // TODO check...
                        if(var.addToWhere())
                            oneNormalVar = varString.toString();

                    } else if(var instanceof VariableDouble)
                        varString.append("value");
                    ArrayList<String> databaseFields = databaseFieldsForVariable.get(var);
                    if(databaseFields == null) {
                        // System.err.println("Warning: variable " + var +
                        // " is not bound. Formula: " + formula);
                        throw new SolveException("Could not find a positive binding for variable " + var + ". Formula: " + formula);
                    } else {
                        for(String databaseField : databaseFields) {
                            // String databaseField=databaseFields.get(0);
                            if(onPart.length() == 0)
                                onPart.append("ON ");
                            else
                                onPart.append("AND ");
                            onPart.append(varString).append(" = ").append(databaseField).append(" ");
                        }
                    }
                }
            }
            for(int j = 0; j < expr.getVariables().size(); j++) {
                VariableAbstract var = expr.getVariables().get(j);
                if(var instanceof VariableString) {
                    // Extra: If we have a string variable, we have to add it to
                    // sql
                    // code dublication from above
                    if(var instanceof VariableString) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(" y").append(i).append(".");
                        sb.append("field").append(j);
                        // if(oneNormalVar!=null) {
                        sb.append(" = ");
                        // }else{
                        // sb.append(" <> ");
                        // }
                        sb.append("'").append(var.getName()).append("'");
                        whereStatements.add(sb.toString());
                    }
                }
            }
            if(onPart.length() == 0) {
                fromPart.append(" JOIN ");
            } else {
                fromPart.append(" LEFT JOIN ");
            }
            fromPart.append("`").append(expr.getPredicate().getName()).append("`");
            fromPart.append(" y").append(i).append(" ");

            fromPart.append(onPart);
            fromStatements.append(fromPart);

            // where y0.field0 IS NULL
            // + StringExpressions like y0.field1 = "SomeString"
            if(oneNormalVar != null)
                whereStatements.add(new StringBuilder().append(oneNormalVar).append(" IS NULL").toString());
        }

        // 3. DUBLICATE DETECTION JOIN (Join with database called like the
        // table)
        // only for formulas with more than one restriction.
        // only if dublicate detection is enabled
        if(dublicateDetection) {
            int lastY = outerJoinExpressions.size(); // one larger than before
            fromStatements.append(" LEFT JOIN `");
            fromStatements.append(formula.getName());
            fromStatements.append("` y").append(lastY);
            ArrayList<StringBuilder> onParts = new ArrayList<StringBuilder>();
            // VariableAbstract someVariable = null;
            int i = 0;
            for(Entry<VariableAbstract, ArrayList<String>> entry : databaseFieldsForVariable.entrySet()) {
                VariableAbstract var = entry.getKey();
                if(var instanceof VariableType) {
                    if(i == 0) {
                        // where y3.v1 is null
                        StringBuilder whereNull = new StringBuilder();
                        whereNull.append("y").append(lastY).append(".").append(var.getName()).append(" IS NULL");
                        whereStatements.add(whereNull.toString());
                    }
                    i++;
                    // someVariable=var;
                    for(String col : entry.getValue()) {
                        StringBuilder onPart = new StringBuilder();
                        onPart.append("y").append(lastY).append(".").append(var.getName());
                        onPart.append(" = ");
                        onPart.append(col);
                        onParts.add(onPart);
                    }
                }
            }
            i = 0;
            for(StringBuilder onPart : onParts) {
                if(i == 0) {
                    fromStatements.append(" ON ");
                } else {
                    fromStatements.append(" AND ");
                }
                fromStatements.append(onPart);
                i++;
            }
        }

        // 4. ENHANCE WHERE PART with equalVariable-, and ThresholdExpressions
        for(IfExpression ifExpression : formula.getIfExpressions()) {
            if(ifExpression instanceof EqualVariableExpression) {
                EqualVariableExpression varExpr = (EqualVariableExpression) ifExpression;
                VariableType var1 = varExpr.getVariable1();
                VariableType var2 = varExpr.getVariable2();
                ArrayList<String> databaseFields1 = databaseFieldsForVariable.get(var1);
                ArrayList<String> databaseFields2 = databaseFieldsForVariable.get(var2);
                if(databaseFields1 != null && databaseFields2 != null) {
                    for(String databaseField1 : databaseFields1) {
                        // String databaseField1 = databaseFields1.get(0);
                        for(String databaseField2 : databaseFields2) {
                            // String databaseField2 = databaseFields2.get(0);
                            StringBuilder sb = new StringBuilder();
                            sb.append(databaseField1);
                            if(varExpr.isNegative())
                                sb.append(" != ");
                            else
                                sb.append(" = ");
                            sb.append(databaseField2);
                            whereStatements.add(sb.toString());
                        }
                    }
                }
            } else if(ifExpression instanceof ThresholdExpression) {
                ThresholdExpression thresholdExpr = (ThresholdExpression) ifExpression;
                VariableDouble var = thresholdExpr.getVariable1();
                ArrayList<String> databaseFields = databaseFieldsForVariable.get(var);
                if(databaseFields != null) {
                    for(String databaseField : databaseFields) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(databaseField);
                        if(thresholdExpr.isGreaterEqual())
                            sb.append(" >= ");
                        else
                            sb.append(" <= ");
                        sb.append(thresholdExpr.getValue());
                        whereStatements.add(sb.toString());
                    }
                }
            }
        }

        // 4. BUILD SELECT with variables of databaseFieldsForVariable
        // SELECT x0.field0 as a1,x0.field1 as a3,x1.field1 as a2,x2.field0 as
        // a4
        int i = 0;
        for(VariableAbstract var : databaseFieldsForVariable.keySet()) {
            if(!(var instanceof VariableString)) {
                String databaseField = databaseFieldsForVariable.get(var).get(0);
                if(i == 0)
                    selectStatements.append("SELECT ");
                else
                    selectStatements.append(", ");
                selectStatements.append(databaseField).append(" as `").append(var.getName()).append("`");
                i++;
            }
        }

        // 5. CONCAT WHOLE QUERY
        StringBuilder queryWithoutSelect = new StringBuilder();
        queryWithoutSelect.append(" FROM (");
        queryWithoutSelect.append(selectStatements);
        queryWithoutSelect.append(fromStatements);
        for(i = 0; i < whereStatements.size(); i++) {
            if(i == 0)
                queryWithoutSelect.append(" WHERE ");
            else
                queryWithoutSelect.append(" AND ");
            queryWithoutSelect.append(whereStatements.get(i));
        }
        queryWithoutSelect.append(") as xx");

        return queryWithoutSelect.toString();

    }

}
