package com.googlecode.rockit.app.solver.pojo;

import java.util.ArrayList;


public class Clause
{
    private double             weight;
    private ArrayList<Literal> restriction;
    private boolean            aggregated = false;
    private boolean            hard       = false;


    public double getWeight()
    {
        return weight;
    }


    public boolean removeRestriction(Literal a)
    {
        return restriction.remove(a);
    }


    public void setWeight(double weight)
    {
        this.weight = weight;
    }


    public ArrayList<Literal> getRestriction()
    {
        return restriction;
    }


    public void setRestriction(ArrayList<Literal> restriction)
    {
        this.restriction = restriction;
    }


    public Clause(double weight, ArrayList<Literal> restriction, boolean isHard)
    {
        super();
        this.weight = weight;
        this.hard = isHard;
        this.restriction = restriction;
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Clause [weight=");
        builder.append(weight);
        builder.append(", restriction=");
        builder.append(restriction);
        builder.append(", hard=");
        builder.append(hard);
        builder.append("]");
        return builder.toString();
    }


    public boolean isAggregated()
    {
        return aggregated;
    }


    public void setAggregated(boolean aggregated)
    {
        this.aggregated = aggregated;
    }


    public boolean isHard()
    {
        return hard;
    }


    public void setHard(boolean hard)
    {
        this.hard = hard;
    }

}
