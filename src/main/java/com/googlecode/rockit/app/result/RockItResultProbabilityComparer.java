package com.googlecode.rockit.app.result;

import java.util.Comparator;


public class RockItResultProbabilityComparer implements Comparator<RockItResult>
{

    @Override
    public int compare(RockItResult x, RockItResult y)
    {
        int result = y.getProbability().compareTo(x.getProbability());
        if(result == 0) {
            result = x.getStatement().compareTo(y.getStatement());
        }
        return result;
    }

}
