package com.googlecode.rockit.app.result;

import java.util.Comparator;


public class RockItResultNameComparer implements Comparator<RockItResult>
{

    @Override
    public int compare(RockItResult x, RockItResult y)
    {
        int result = x.getStatement().compareTo(y.getStatement());
        if(result == 0) {
            result = y.getProbability().compareTo(x.getProbability());
        }
        return result;
    }

}
