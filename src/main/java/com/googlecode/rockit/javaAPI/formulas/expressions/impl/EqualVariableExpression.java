package com.googlecode.rockit.javaAPI.formulas.expressions.impl;

import java.util.HashSet;

import com.googlecode.rockit.javaAPI.formulas.expressions.IfExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableAbstract;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;


/**
 * Adds the clause only if two variables are equal or not equal (if isNegative()==true).
 * 
 * 
 * @author jan
 *
 */

public class EqualVariableExpression implements IfExpression
{

    private VariableType variable1;
    private VariableType variable2;
    private boolean      positive;


    public EqualVariableExpression(VariableType variable1, VariableType variable2, boolean positive)
    {
        this.variable1 = variable1;
        this.variable2 = variable2;
        this.positive = positive;
    }


    public EqualVariableExpression()
    {

    }


    public boolean isNegative()
    {

        return !positive;
    }


    public boolean isPositive()
    {
        return positive;
    }


    public void setNegative(boolean negative)
    {
        this.positive = !negative;

    }


    public void setPositive(boolean positive)
    {
        this.positive = positive;

    }


    public void setVariable1(VariableType variable1)
    {
        this.variable1 = variable1;
    }


    public VariableType getVariable1()
    {
        return variable1;
    }


    public void setVariable2(VariableType variable2)
    {
        this.variable2 = variable2;
    }


    public VariableType getVariable2()
    {
        return variable2;
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(variable1.getName());
        if(this.positive) {
            sb.append("==");
        } else {
            sb.append("!=");
        }
        sb.append(variable2.getName());
        return sb.toString();
    }


    @Override
    public HashSet<VariableAbstract> getAllVariables()
    {

        HashSet<VariableAbstract> result = new HashSet<VariableAbstract>();
        result.add(variable1);
        result.add(variable2);
        return result;
    }

}
