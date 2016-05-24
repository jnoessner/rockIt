package com.googlecode.rockit.app.evaluator;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.grounder.StandardGrounder;
import com.googlecode.rockit.conn.sql.MySQLConnector;
import com.googlecode.rockit.conn.sql.SQLQueryGenerator;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.javaAPI.formulas.FormulaAbstract;
import com.googlecode.rockit.javaAPI.formulas.FormulaCardinality;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.FormulaObjective;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.parser.SyntaxReader;


/**
 * Computes the reached objective. This is often different from the objective of the integer linear program solver since usually (if CPI is activated) not all groundings are added to the ILP.
 * 
 * @author jan
 *
 */
public class ObjectiveEvaluator
{

    private double hardObjective;


    private String eliminateFrom(String query)
    {
        String result = query.substring(query.lastIndexOf("FROM"), query.indexOf(")"));
        return result;
    }


    /**
     * Returns the objective (number of true groundings multiplied with the weight) for each formula.
     * 
     * @param formula
     *            Formula for which the objective is computed.
     * @param sql
     *            the sql connector.
     * @return
     * @throws ParseException
     * @throws SQLException
     * @throws SolveException
     */
    public double getObjective(FormulaAbstract formula, MySQLConnector sql) throws ParseException, SQLException, SolveException
    {
        String sqlSelect = "SELECT count(*)";
        FormulaHard fh = null;
        if(formula instanceof FormulaObjective) {
            ArrayList<PredicateExpression> restrictions = new ArrayList<PredicateExpression>();
            restrictions.add(((FormulaObjective) formula).getObjectiveExpression());
            fh = new FormulaSoft(formula.getName(), formula.getForVariables(), formula.getIfExpressions(), ((FormulaObjective) formula).getDoubleVariable(), restrictions, false);
        }
        if((formula instanceof FormulaHard) || (formula instanceof FormulaSoft) || (formula instanceof FormulaCardinality)) {
            fh = (FormulaHard) formula;
        }
        String sqlSelectFormula = sqlSelect;
        if(formula instanceof FormulaSoft && ((FormulaSoft) formula).getDoubleVariable() != null) {
            sqlSelectFormula = sqlSelectFormula + ", sum(" + ((FormulaSoft) formula).getDoubleVariable().getName() + ") ";
        } else if(formula instanceof FormulaObjective && ((FormulaObjective) formula).getDoubleVariable() != null) {
            sqlSelectFormula = sqlSelectFormula + ", sum(value) ";

        } else {
            sqlSelectFormula = sqlSelectFormula + ", 0 ";
        }
        String query = sqlSelectFormula + this.eliminateFrom(SQLQueryGenerator.getSQLStatementWithoutSelect(fh, true, false));

        ResultSet res = sql.executeSelectQuery(query);
        res.next();
        Double countNegatives = res.getDouble(1);
        Double sumSoftNegatives = res.getDouble(2);
        res.getStatement().close();
        res.close();

        query = sqlSelectFormula + this.eliminateFrom(SQLQueryGenerator.getSQLStatementWithoutSelectWithoutCPI(fh, false, false));
        res = sql.executeSelectQuery(query);
        res.next();
        Double countTotal = res.getDouble(1);
        Double sumSoftTotal = res.getDouble(2);
        res.getStatement().close();
        res.close();
        Double sumSoftPositive = sumSoftTotal - sumSoftNegatives;

        if(Parameters.DEBUG_OUTPUT)
            System.out.print(fh);
        double weight = 0;
        if(formula instanceof FormulaSoft) {
            if(Parameters.DEBUG_OUTPUT)
                System.out.print("weight (s) ");
            if(((FormulaSoft) formula).getDoubleVariable() == null) {
                weight = ((FormulaSoft) formula).getWeight();
            } else {
                weight = 0;
            }
        } else if(formula instanceof FormulaObjective) {
            if(Parameters.DEBUG_OUTPUT)
                System.out.print("weight (s) ");
            if(((FormulaObjective) formula).getDoubleVariable() == null) {
                weight = ((FormulaObjective) formula).getWeight();
            } else {
                weight = 0;
            }
        } else {
            if(Parameters.DEBUG_OUTPUT)
                System.out.print("weight (h) ");
            weight = 0;
            return (countTotal - countNegatives);
            // weight = Parameters.EVALUATION_WEIGHT_FOR_HARD_FORMULA;
        }
        if(Parameters.DEBUG_OUTPUT)
            System.out.print(weight);
        if(Parameters.DEBUG_OUTPUT)
            System.out.print(" - total possible results: ");
        if(Parameters.DEBUG_OUTPUT)
            System.out.print(countTotal);
        if(Parameters.DEBUG_OUTPUT)
            System.out.print(" - recived results: ");
        if(Parameters.DEBUG_OUTPUT)
            System.out.print(countTotal - countNegatives);
        if(Parameters.DEBUG_OUTPUT && sumSoftPositive != 0)
            System.out.print(" - recieved sum of double var: ");
        if(Parameters.DEBUG_OUTPUT && sumSoftPositive != 0)
            System.out.print(sumSoftPositive);
        if(Parameters.DEBUG_OUTPUT)
            System.out.print(" - additional objective: ");
        if(Parameters.DEBUG_OUTPUT)
            System.out.print((countTotal - countNegatives) * weight + sumSoftPositive);
        if(Parameters.DEBUG_OUTPUT)
            System.out.println();
        if(Parameters.DEBUG_OUTPUT)
            System.out.println();

        return (countTotal - countNegatives) * weight + sumSoftPositive;
    }


    /**
     * Returns the objective of every soft formula AND computes the number of sattisfied hard clauses.
     * 
     * @param modelFile
     *            path to the model file.
     * @param groundValueFile
     *            path to the ground value file.
     * @param resultFile
     * @return
     * @throws ParseException
     * @throws SQLException
     * @throws SolveException
     */
    public double evaluate(String modelFile, String groundValueFile, String resultFile) throws ParseException, SQLException, SolveException
    {
        SyntaxReader reader = new SyntaxReader();
        // reads also hidden predicates from the result file
        Model m;
        try {
            m = reader.getModelForEvaluation(modelFile, groundValueFile, resultFile);
        } catch(RecognitionException e) {
            throw new ParseException("Could not get Model. " + e.getMessage());
        } catch(IOException e) {
            throw new ParseException("Could not get Model. " + e.getMessage());
        }

        MySQLConnector sql = new MySQLConnector();
        sql.deleteAll();

        StandardGrounder grounder = new StandardGrounder(m, sql);
        // we also have to ground hidden predicates, because we included them in the model already.
        grounder.setGroundHiddenPredicates(true);
        grounder.ground();

        hardObjective = 0;
        double objective = 0;

        for(FormulaAbstract f : m.getFormulas()) {

            if((f instanceof FormulaSoft) || (f instanceof FormulaObjective)) {
                objective = objective + this.getObjective(f, sql);
            } else {
                this.hardObjective = this.hardObjective + this.getObjective(f, sql);
            }

        }

        // sql.close();
        System.out.println("Soft objective " + objective);
        System.out.println("Hard objective " + hardObjective);
        return objective;
    }


    public double getHardObjective()
    {
        return hardObjective;
    }

}
