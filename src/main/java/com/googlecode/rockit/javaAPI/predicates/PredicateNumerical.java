package com.googlecode.rockit.javaAPI.predicates;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.rockit.app.solver.numerical.MathExpression;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.types.Type;


public class PredicateNumerical extends Predicate
{
    private MathExpression mathEx;
    private boolean        numeric   = false;
    private List<String>   variables = new ArrayList<>();


    public PredicateNumerical(String name, boolean hidden, Type... types) throws ParseException
    {
        super(name, hidden, types);
    }


    public PredicateNumerical(String name) throws ParseException
    {
        super(name, true);
    }


    public boolean isNumeric()
    {
        return numeric;
    }


    public void setNumeric(boolean numeric)
    {
        this.numeric = numeric;
    }


    public MathExpression getMathEx()
    {
        return mathEx;
    }


    public void setMathEx(MathExpression mathEx)
    {
        this.numeric = true;
        this.mathEx = mathEx;
    }


    public List<String> getVariables()
    {
        return variables;
    }


    public void setVariables(List<String> variables)
    {
        this.variables = variables;
    }

}
