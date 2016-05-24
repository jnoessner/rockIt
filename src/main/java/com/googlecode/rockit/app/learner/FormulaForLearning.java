package com.googlecode.rockit.app.learner;

import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;


public class FormulaForLearning
{

    private FormulaSoft formula;

    private double      sumOfWeights = 0;

    private long        expectedNumberOfTrueGroundings;


    public FormulaForLearning(FormulaSoft formula)
    {
        this.formula = formula;
    }


    public FormulaForLearning(FormulaSoft formula, long expectedNumberOfTrueGroundings)
    {
        this.formula = formula;
        this.expectedNumberOfTrueGroundings = expectedNumberOfTrueGroundings;
    }


    public FormulaSoft getFormula()
    {
        return formula;
    }


    public void addWeightForAverage(double weight)
    {
        this.sumOfWeights = this.sumOfWeights + weight;
    }


    public double returnAverage(int numberOfLearningRounds)
    {
        return this.sumOfWeights / ((double) numberOfLearningRounds);
    }


    public void setFormula(FormulaSoft formula)
    {
        this.formula = formula;
    }


    public long getExpectedNumberOfTrueGroundings()
    {
        return expectedNumberOfTrueGroundings;
    }


    public void setExpectedNumberOfTrueGroundings(long expectedNumberOfTrueGroundings)
    {
        this.expectedNumberOfTrueGroundings = expectedNumberOfTrueGroundings;
    }

}
