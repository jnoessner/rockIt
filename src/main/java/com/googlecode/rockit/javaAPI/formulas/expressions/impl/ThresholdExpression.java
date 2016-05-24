package com.googlecode.rockit.javaAPI.formulas.expressions.impl;

import java.util.HashSet;

import com.googlecode.rockit.javaAPI.formulas.expressions.IfExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableAbstract;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableDouble;


/**
 * Adds the clause only if the content of a variable equals the value or not (if isNegative()==true).
 * 
 * @author jan
 *
 */

public class ThresholdExpression implements IfExpression
{

    private VariableDouble variable1;
    private double         value;
    private boolean        lessEqual;


    /**
     * variable1 <= value for lessEqual = true,
     * variable1 >= value for lessEqual = false.
     * 
     * @param variable1
     * @param lessEqual
     * @param value
     */
    public ThresholdExpression(VariableDouble variable1, boolean lessEqual, double value)
    {
        this.variable1 = variable1;
        this.value = value;
        this.lessEqual = lessEqual;
    }


    public ThresholdExpression()
    {

    }


    public VariableDouble getVariable1()
    {
        return variable1;
    }


    public void setVariable1(VariableDouble variable1)
    {
        this.variable1 = variable1;
    }


    public double getValue()
    {
        return value;
    }


    public void setValue(double value)
    {
        this.value = value;
    }


    public boolean isGreaterEqual()
    {
        return !lessEqual;
    }


    /**
     * variable >= double value.
     * 
     * @param greaterEqual
     */
    public void setGreaterEqual(boolean greaterEqual)
    {
        this.lessEqual = !greaterEqual;
    }


    public boolean isLessEqual()
    {
        return lessEqual;
    }


    /**
     * variable <= double value.
     * 
     * @param lessEqual
     */
    public void setLessEqual(boolean lessEqual)
    {
        this.lessEqual = lessEqual;
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        // sb.append(variable1.getName());
        if(this.lessEqual) {
            sb.append("<= ");
        } else {
            sb.append(">= ");
        }
        sb.append(value);
        return sb.toString();
    }


    @Override
    public HashSet<VariableAbstract> getAllVariables()
    {
        HashSet<VariableAbstract> result = new HashSet<VariableAbstract>();
        result.add(variable1);
        return result;
    }

}
