package com.googlecode.rockit.app.solver.numerical;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.rockit.javaAPI.HerbrandUniverse;


public class MathExpression
{
    private static Map<String, Map<String, Integer>> mathPredPositions = new HashMap<>();
    private static Map<String, MathExpression>       mathPredEx        = new HashMap<>();

    private String                                   expression;

    public static int                                counter           = 0;


    public MathExpression(String expression)
    {
        this.expression = expression;
    }


    public static boolean evaluate(String predicate, List<String> values)
    {
        counter++;
        if(mathPredPositions.keySet().contains(predicate)) {
            Map<String, Integer> positions = mathPredPositions.get(predicate);
            MathExpression mEx = mathPredEx.get(predicate);
            String c = mEx.getExpression();

            // System.out.println(c);

            for(String variable : positions.keySet()) {
                Double value = Double.valueOf(HerbrandUniverse.getInstance().getConstant(values.get(positions.get(variable))));
                c = c.replaceAll(variable, String.valueOf(value));
            }
            BigDecimal res = new Expression(c).eval();

            // System.out.println(c + "\t" + res);
            if(res.toString().equals("1")) {
                return true;
            } else {
                return false;
            }

        }
        System.err.println("Numerical predicate \"" + predicate + "\" does not exist.");
        System.exit(0);
        return false;

    }


    public String getExpression()
    {
        return expression;
    }


    public static void setMathPredPositions(Map<String, Map<String, Integer>> mathPredPositions)
    {
        MathExpression.mathPredPositions = mathPredPositions;
    }


    public static void setMathPredEx(Map<String, MathExpression> mathPredEx)
    {
        MathExpression.mathPredEx = mathPredEx;
    }


    public static boolean isNumericPredicate(String predicate)
    {
        if(mathPredPositions.keySet().contains(predicate)) { return true; }
        return false;
    }


    public static void reset()
    {
        counter = 0;
        mathPredPositions = new HashMap<>();
        mathPredEx = new HashMap<>();
    }
}
