package com.googlecode.rockit.app.learner;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.grounder.StandardGrounder;
import com.googlecode.rockit.app.solver.StandardSolver;
import com.googlecode.rockit.conn.sql.MySQLConnector;
import com.googlecode.rockit.conn.sql.SQLQueryGenerator;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.javaAPI.formulas.FormulaAbstract;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
import com.googlecode.rockit.parser.SyntaxReader;


/**
 * A voted perceptron learner for rockit. Currently, it operates with a fixed
 * learning rate for all formulas. This may be changed in the future.
 * 
 * @author Bernd Opitz
 * 
 */
public class VotedPerceptronLearner
{

    private ArrayList<FormulaForLearning> formulas = new ArrayList<FormulaForLearning>();

    private Model                         model;

    @SuppressWarnings ("unused")
    private ArrayList<String>             results  = new ArrayList<String>();


    public VotedPerceptronLearner(Model model) throws SQLException
    {
        this.model = model;
    }


    /**
     * Learn one iteration for the given world and update formulas accordingly.
     * 
     * @param groundValueFile
     *            a world
     * @param sql
     *            the connection to the SQL database
     * @return
     * @throws SQLException
     * @throws ParseException
     * @throws SolveException
     * @throws IOException
     * @throws RecognitionException
     */
    private Model learnIteration(String groundValueFile, MySQLConnector sql) throws SQLException, ParseException, SolveException, IOException, RecognitionException
    {
        SyntaxReader reader = new SyntaxReader();
        // read in the new values
        model = reader.getGroundValuesForLearning(groundValueFile, model);

        sql.deleteAll();
        StandardGrounder grounder = new StandardGrounder(model, sql);
        // we also have to ground hidden predicates, because we included them in
        // the model already.
        grounder.setGroundHiddenPredicates(true);
        grounder.ground();

        // evaluation of current gold standard (before learning starts)
        if(formulas.isEmpty()) {
            for(FormulaAbstract f : model.getFormulas()) {
                if(f instanceof FormulaSoft) {
                    FormulaSoft fs = (FormulaSoft) f;
                    long trueGroundings = this.numberOfTrueGroundings(fs, sql);
                    formulas.add(new FormulaForLearning(fs, trueGroundings));
                }
            }
        }

        // start of cutting plane inferences
        StandardSolver solver = new StandardSolver(model, sql);
        results = solver.runCuttingPlaneInference();
        solver.closeILPConnector();

        if(Parameters.DEBUG_OUTPUT)
            System.out.println("-- new weights --");
        for(FormulaForLearning f : formulas) {
            long currentTrueGroundings = this.numberOfTrueGroundings(f.getFormula(), sql);

            double learningRate = Parameters.LEARNING_RATE;
            double oldWeight = f.getFormula().getWeight();
            long goldStandardTrueGroundings = f.getExpectedNumberOfTrueGroundings();

            double newWeight = this.nextWeightVP(oldWeight, learningRate, currentTrueGroundings, goldStandardTrueGroundings);

            f.getFormula().setWeight(newWeight);

            f.addWeightForAverage(newWeight);

            if(Parameters.DEBUG_OUTPUT)
                System.out.println(f.getFormula());
        }

        return model;
    }


    /**
     * Simple Voted Perceptron (VP) according to 'Efficient Weight Learning With
     * Markov Logic' (http://homes.cs.washington.edu/~pedrod/papers/pkdd07.pdf)
     * We divide the learning rate with the actual true groundings. They removed
     * evidence axioms (which we did not yet).
     * 
     * @param currentWeight
     * @param learningRate
     * @param currentTrueGroundings
     * @param goldStandardTrueGroundings
     * @return
     */
    private double nextWeightVP(double currentWeight, double learningRate, long currentTrueGroundings, long goldStandardTrueGroundings)
    {
        double newWeight = currentWeight + learningRate * (goldStandardTrueGroundings - currentTrueGroundings);
        if(Parameters.DEBUG_OUTPUT)
            System.out.println(newWeight + " = " + currentWeight + " + " + learningRate + " * (" + goldStandardTrueGroundings + " - " + currentTrueGroundings + ")");

        return newWeight;

    }


    /**
     * VP-PW according to 'Efficient Weight Learning With Markov Logic'
     * (http://homes.cs.washington.edu/~pedrod/papers/pkdd07.pdf) We divide the
     * learning rate with the actual true groundings. They removed evidence
     * axioms (which we did not yet).
     * 
     * @param currentWeight
     * @param learningRate
     * @param currentTrueGroundings
     * @param goldStandardTrueGroundings
     * @return
     */
    @SuppressWarnings ("unused")
    private double nextWeightVPPW(double currentWeight, double learningRate, long currentTrueGroundings, long goldStandardTrueGroundings)
    {
        if(currentTrueGroundings != 0) {
            if(Parameters.DEBUG_OUTPUT)
                System.out.println("previous learn rate " + learningRate);
            learningRate = learningRate / currentTrueGroundings;
            if(Parameters.DEBUG_OUTPUT)
                System.out.println("adapted learn rate =  " + learningRate);
        }
        double newWeight = currentWeight + learningRate * (goldStandardTrueGroundings - currentTrueGroundings);
        if(Parameters.DEBUG_OUTPUT)
            System.out.println(newWeight + " = " + currentWeight + " + " + learningRate + " * (" + goldStandardTrueGroundings + " - " + currentTrueGroundings + ")");

        return newWeight;

    }


    /**
     * Use the voted perceptron algorithm to learn weights based on the input
     * worlds.
     * 
     * @param groundValueFiles
     *            the file paths of the worlds to use for learning
     * @param learningIterations
     *            how many iterations to learn
     * @return
     * @throws ParseException
     * @throws IOException
     * @throws RecognitionException
     * @throws SolveException
     * @throws SQLException
     */
    public Model learn(ArrayList<String> groundValueFiles, int learningIterations) throws ParseException, IOException, RecognitionException, SolveException, SQLException
    {

        if(learningIterations < 0) {
            learningIterations = 10;
        }

        // Start rockit
        MySQLConnector sql = new MySQLConnector();

        for(int r = 0; r < learningIterations; r++) {
            System.out.println("======== START LEARNING ITERATION " + (r + 1) + " ==============");
            for(int w_i = 0; w_i < groundValueFiles.size(); w_i++) {
                String w = groundValueFiles.get(w_i);
                model = learnIteration(w, sql);
            }
        }

        sql.close();

        for(FormulaForLearning f : formulas) {
            f.getFormula().setWeight(f.returnAverage(learningIterations * groundValueFiles.size()));
        }

        // Prec Rec evaluator
        // PrecisionRecallEvaluator evaluator = new
        // PrecisionRecallEvaluator(results, model);
        // System.out.println(evaluator);
        return model;

    }


    /**
     * Returns number of true groundings.
     * 
     * @param f
     * @param sql
     * @return
     * @throws SolveException
     * @throws SQLException
     */
    private long numberOfTrueGroundings(FormulaSoft f, MySQLConnector sql) throws SolveException, SQLException
    {
        String sqlSelect = "SELECT count(*) ";
        // number of true groundings
        String query = sqlSelect + this.eliminateFrom(SQLQueryGenerator.getSQLStatementWithoutSelect(f, false, false));

        return sql.executeLongQuery(query);
    }


    /**
     * Returns number of violated (false) groundings.
     * 
     * @param f
     * @param sql
     * @return
     * @throws SolveException
     * @throws SQLException
     */
    @SuppressWarnings ("unused")
    private long numberOfFalseGroundings(FormulaSoft f, MySQLConnector sql) throws SolveException, SQLException
    {
        String sqlSelect = "SELECT count(*) ";
        // number of false groundings (violated groundings)
        String query = sqlSelect + this.eliminateFrom(SQLQueryGenerator.getSQLStatementWithoutSelect(f, true, false));

        return sql.executeLongQuery(query);
    }


    /**
     * Returns number of all groundings.
     * 
     * @param f
     * @param sql
     * @return
     * @throws SolveException
     * @throws SQLException
     * @throws ParseException
     */
    @SuppressWarnings ("unused")
    private long numberOfAllGroundings(FormulaSoft f, MySQLConnector sql) throws SolveException, SQLException, ParseException
    {
        String sqlSelect = "SELECT count(*) ";
        // number of all groundings
        String query = sqlSelect + this.eliminateFrom(SQLQueryGenerator.getSQLStatementWithoutSelectWithoutCPI(f, false, false));

        return sql.executeLongQuery(query);
    }


    private String eliminateFrom(String query)
    {
        String result = query.substring(query.lastIndexOf("FROM"), query.indexOf(")"));
        return result;
    }

}
