package com.googlecode.rockit.app.sampler.gibbs;

public class GIBBSClauseSoft extends GIBBSClauseHard
{

    private double weight;


    public GIBBSClauseSoft(double weight)
    {
        this.weight = weight;
    }


    /**
     * Returns weight if clause is true, 0 otherwise.
     * 
     * @return
     */
    public double getWeightIfTrue()
    {
        if(this.isTrue()) { return weight; }
        return 0;
    }


    public void setWeight(double weight)
    {
        this.weight = weight;
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GIBBSClauseSoft [weight=");
        builder.append(weight);
        builder.append(", positiveLiterals=");
        builder.append(getPositiveLiterals());
        builder.append(", negativeLiterals=");
        builder.append(getNegativeLiterals());
        builder.append("]");
        return builder.toString();
    }

}
