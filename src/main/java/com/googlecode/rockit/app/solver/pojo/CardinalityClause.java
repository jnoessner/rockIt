package com.googlecode.rockit.app.solver.pojo;

import java.util.ArrayList;


public class CardinalityClause extends Clause
{

    private boolean lessEqual;
    private int     cardinality = 1;


    public CardinalityClause(ArrayList<Literal> restriction, boolean lessEqual, int cardinality)
    {
        super(0d, restriction, true);
        this.setLessEqual(lessEqual);
        this.setCardinality(cardinality);
    }


    public int getCardinality()
    {
        return cardinality;
    }


    public void setCardinality(int cardinality)
    {
        this.cardinality = cardinality;
    }


    public boolean isLessEqual()
    {
        return lessEqual;
    }


    public void setLessEqual(boolean lessEqual)
    {
        this.lessEqual = lessEqual;
    }

}
