package com.googlecode.rockit.app.evaluator;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import com.googlecode.rockit.file.LogFileWriter;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.javaAPI.predicates.Predicate;
import com.googlecode.rockit.javaAPI.predicates.PredicateAbstract;


public class PrecisionRecallEvaluator
{

    private ArrayList<String> falseNegatives         = new ArrayList<String>();
    private ArrayList<String> falsePositives         = new ArrayList<String>();
    private ArrayList<String> truePositives          = new ArrayList<String>();
    private LogFileWriter     logFileWriter          = null;
    private ArrayList<String> specificPredicateNames = new ArrayList<String>();


    /**
     * Calls evaluate.
     * 
     * @param results
     *            the results from the solver
     * @param goldStandards
     *            list of gold standard axioms
     */
    public PrecisionRecallEvaluator(ArrayList<String> results, ArrayList<String> goldStandards)
    {
        this.evaluate(results, goldStandards);
    }


    /**
     * Calls evaluate().
     * 
     * @param results
     *            the results from the solver
     * @param model
     *            the general model
     */
    public PrecisionRecallEvaluator(ArrayList<String> results, Model model)
    {
        this.evaluate(results, model);
    }


    /**
     * Calls evaluate.
     * 
     * @param specificPredicateName
     * @param results
     * @param model
     */
    public PrecisionRecallEvaluator(ArrayList<String> results, Model model, ArrayList<String> specificPredicateNames)
    {
        this.evaluate(results, model, specificPredicateNames);
    }


    /**
     * Calls evaluate.
     * 
     * @param specificPredicateName
     * @param results
     * @param model
     */
    public PrecisionRecallEvaluator(ArrayList<String> results, Model model, String... specificPredicateNames)
    {
        this.evaluate(results, model, specificPredicateNames);
    }


    /**
     * Evaluates the results for all hidden variables. Thereby, the ground values of the hidden predicates are
     * used as gold standard.
     * 
     * @param results
     * @param model
     */
    public void evaluate(ArrayList<String> results, Model model)
    {
        ArrayList<String> allPreds = new ArrayList<String>();
        for(PredicateAbstract pred : model.getAllHiddenPredicates()) {
            allPreds.add(pred.getName());
        }
        this.evaluate(results, model, allPreds);
    }


    /**
     * Evaluates the results for specific hidden variables. Thereby, the ground values of the hidden predicates are
     * used as gold standard.
     * 
     * @param specificPredicateNames
     *            If you want to evaluate it only for a specific predicate, just plug in the name of the predicate here.
     * @param results
     * @param model
     */
    public void evaluate(ArrayList<String> results, Model model, String... specificPredicateNames)
    {
        ArrayList<String> specArrayList = new ArrayList<String>();
        for(int i = 0; i < specificPredicateNames.length; i++) {
            specArrayList.add(specificPredicateNames[i]);
        }
        this.evaluate(results, model, specArrayList);
    }


    public void evaluate(ArrayList<String> results, ArrayList<String> goldStandards)
    {

        // transform results into more efficient datastructure
        // the boolean variable states if the result has been found in the
        // gold standard (groundPredicates)
        HashMap<String, Boolean> resultsMap = new HashMap<String, Boolean>();
        for(String result : results) {
            resultsMap.put(result, false);
        }
        for(String goldStandard : goldStandards) {
            if(resultsMap.containsKey(goldStandard)) {
                // true positive: Must occur in both lists.
                this.truePositives.add(goldStandard);
                // set to true for determining the falsePositives afterwards.
                resultsMap.put(goldStandard, true);
            } else {
                // false positives: not in results but in ground values.
                this.falseNegatives.add(goldStandard);
            }
        }
        // false negatives: in results but not in ground values.
        // means that they have not been found in the previous step:
        for(String result : resultsMap.keySet()) {
            boolean found = resultsMap.get(result);
            if(!found) {
                this.falsePositives.add(result);
            }
        }

        // true negatives: not in both lists (can not be determined)
    }


    /**
     * Evaluates the results. Thereby, the ground values of the hidden predicates are
     * used as gold standard.
     * 
     * @param specificPredicateName
     *            If you want to evaluate it only for a specific predicate, just plug in the name of the predicate here.
     * @param results
     * @param model
     */
    public void evaluate(ArrayList<String> results, Model model, ArrayList<String> specificPredicateNames)
    {
        this.specificPredicateNames = specificPredicateNames;
        HashSet<PredicateAbstract> hiddenPredicates = model.getAllHiddenPredicates();

        ArrayList<String> goldStandard = new ArrayList<String>();

        // groundPredicate == GoldStandard.
        for(PredicateAbstract predAbstr : hiddenPredicates) {
            if(predAbstr.getClass().equals(Predicate.class)) {
                // if specificPredicateName is empty, then choose everything.
                if(specificPredicateNames.size() == 0 || specificPredicateNames.contains(predAbstr.getName())) {
                    Predicate pred = (Predicate) predAbstr;
                    ArrayList<String[]> goldGroundValues = pred.getGroundValues();
                    if(goldGroundValues.size() == 0) {
                        System.err.println("Warning: No ground values (gold standard) for Predicate " + pred.getName() + " found. All results concerning this predicate will be assigned to false negatives.");
                    }
                    for(String[] goldArray : goldGroundValues) {
                        String goldStandardValue = transformGroundValue(pred.getName(), goldArray);
                        goldStandard.add(goldStandardValue);
                    }
                } else {
                    // System.err.println("Predicate " + specificPredicateName + " does not exist. Precision, Recall and F-Measure will be 0.");
                }
            }
        }
        ArrayList<String> resultsWithNewPredNames = new ArrayList<String>();

        if(specificPredicateNames != null) {
            for(String result : results) {
                String resPredName = result.split("\\|")[0];
                for(String predName : specificPredicateNames) {
                    if(resPredName.equals(predName)) {
                        resultsWithNewPredNames.add(result);
                    }
                }
            }
        } else {
            resultsWithNewPredNames = results;
        }

        this.evaluate(resultsWithNewPredNames, goldStandard);

    }


    private String transformGroundValue(String namePredicate, String[] groundValue)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(namePredicate).append("|");
        for(int i = 0; i < groundValue.length; i++) {
            sb.append(groundValue[i]);
            if(i < groundValue.length - 1) {
                sb.append("|");
            }
        }
        return sb.toString();
    }


    public ArrayList<String> getFalsePositives()
    {
        return falseNegatives;
    }


    public ArrayList<String> getFalseNegatives()
    {
        return falsePositives;
    }


    public ArrayList<String> getTruePositives()
    {
        return truePositives;
    }


    /**
     * Gets Precision according to the formular TP/(TP + FP)
     * 
     * @return value between 0 and 1 (highest Precision)
     */
    public double getPrecision()
    {
        double denominator = (truePositives.size() + falsePositives.size());
        if(denominator == 0) {
            return 0;
        } else {
            return ((double) this.truePositives.size()) / denominator;
        }
    }


    /**
     * Gets Recall according to the formular TP/(TP + FN)
     * 
     * @return value between 0 and 1 (highest Precision)
     */
    public double getRecall()
    {
        double denominator = (truePositives.size() + falseNegatives.size());
        if(denominator == 0) {
            return 0;
        } else {
            return ((double) truePositives.size()) / denominator;
        }
    }


    public double getFMeasure()
    {
        return (2 * this.getPrecision() * this.getRecall()) / (this.getPrecision() + this.getRecall());
    }


    public void setLogFileWriter(LogFileWriter logFileWriter)
    {
        this.logFileWriter = logFileWriter;
    }


    private void writeToLog(String text)
    {
        if(this.logFileWriter != null) {
            this.logFileWriter.writeln(text);
        }
    }


    public String toShortString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Precision|").append(this.getPrecision()).append("|Recall|").append(this.getRecall()).append("|F-Measure|").append(this.getFMeasure());
        return sb.toString();
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if(this.specificPredicateNames.size() == 0) {
            sb.append("==== Results of all hidden predicates ====");
        } else {
            sb.append("==== Results of hidden predicate(s) : ").append(this.specificPredicateNames).append(" ====");
        }
        sb.append(new Date()).append("\n");
        sb.append("Precision: ").append(this.getPrecision());
        sb.append("\nRecall: ").append(this.getRecall());
        sb.append("\nF-Measure ").append(this.getFMeasure());
        this.writeToLog(sb.toString());
        return sb.toString();
    }


    public String toLongString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.toString());
        sb.append("\n\nTruePositives (in both, the results and the gold standard): \n");
        for(String s : this.truePositives) {
            sb.append(s).append("\n");
        }
        sb.append("\nFalseNegatives (not in results but in gold standard).\n");
        for(String s : this.falseNegatives) {
            sb.append(s).append("\n");
        }
        sb.append("\nFalsePositives (in results but not in gold standard).\n");
        for(String s : this.falsePositives) {
            sb.append(s).append("\n");
        }
        this.writeToLog(sb.toString());
        return sb.toString();
    }
}
