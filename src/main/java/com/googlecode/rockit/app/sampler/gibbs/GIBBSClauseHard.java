package com.googlecode.rockit.app.sampler.gibbs;

import java.util.HashSet;


public class GIBBSClauseHard
{

    private HashSet<GIBBSLiteral> positiveLiterals = new HashSet<GIBBSLiteral>();

    private HashSet<GIBBSLiteral> negativeLiterals = new HashSet<GIBBSLiteral>();


    public void addPositiveLiteral(GIBBSLiteral l)
    {
        this.positiveLiterals.add(l);
    }


    public void addNegativeLiteral(GIBBSLiteral l)
    {
        this.negativeLiterals.add(l);
    }


    public HashSet<GIBBSLiteral> getPositiveLiterals()
    {
        return positiveLiterals;
    }


    public HashSet<GIBBSLiteral> getNegativeLiterals()
    {
        return negativeLiterals;
    }


    public boolean isTrue()
    {
        for(GIBBSLiteral l : positiveLiterals) {
            if(l.isPositive()) { return true; }
        }
        for(GIBBSLiteral l : negativeLiterals) {
            if(l.isNegative()) { return true; }
        }
        return false;
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GIBBSClauseHard [positiveLiterals=");
        builder.append(positiveLiterals);
        builder.append(", negativeLiterals=");
        builder.append(negativeLiterals);
        builder.append("]");
        return builder.toString();
    }

}
