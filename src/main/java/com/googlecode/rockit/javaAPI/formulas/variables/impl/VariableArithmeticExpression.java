package com.googlecode.rockit.javaAPI.formulas.variables.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.googlecode.rockit.app.solver.numerical.Expression;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.javaAPI.types.Type;


public class VariableArithmeticExpression extends VariableAbstract
{
    // private Type type;
    private Type        type;

    private Set<String> variables = new HashSet<>();
    private String      expression;

    static private int  counter   = 0;


    public VariableArithmeticExpression(String name)
    {
        this.setExpression(name);
        setName("ae" + ++counter);
        getVariables();
    }


    public VariableArithmeticExpression()
    {
    }


    private void getVariables()
    {
        String ex = this.getExpression();

        List<String> functions = new ArrayList<String>();

        functions.add("NOT");
        functions.add("IF");
        functions.add("MIN");
        functions.add("MAX");
        functions.add("ABS");
        functions.add("ROUND");
        functions.add("FLOOR");
        functions.add("CEILING");

        for(String f : functions) {
            ex = ex.replaceAll("(?i)" + f, "");
        }

        ex = ex.replaceAll("[\\s|\\(|\\)]", "");

        List<String> operators = new ArrayList<>();
        operators.add("\\+");
        operators.add("-");
        operators.add("\\*");
        operators.add("\\/");
        operators.add(",");

        Set<String> temp = new HashSet<String>();
        temp.add(ex);
        for(String op : operators) {
            Set<String> temp2 = new HashSet<String>();
            for(String s : temp) {
                temp2.addAll(Arrays.asList(s.split(op)));
            }
            temp = temp2;
        }

        for(String var : temp) {
            try {
                Double.parseDouble(var);
            } catch(NumberFormatException e) {
                variables.add(var);
                continue;
            }
        }
    }


    public double evaluate(HashMap<String, Integer> varIndex, String[] resTemp)
    {
        String ex = getExpression();

        for(String v : variables) {
            String value = HerbrandUniverse.getInstance().getConstant(resTemp[varIndex.get(v)]);
            ex = ex.replaceAll(v, value);
        }

        Expression ex2 = new Expression(ex);
        return ex2.eval().doubleValue();

    }


    public String getExpression()
    {
        return expression;
    }


    public void setExpression(String expression)
    {
        this.expression = expression;
    }


    public Type getType()
    {
        return type;
    }


    public void setType(Type type)
    {
        this.type = type;
    }

}
