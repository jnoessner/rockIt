//package com.googlecode.rockit.app.solver.numerical;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.googlecode.rockit.javaAPI.HerbrandUniverse;
//
//
//public class NumericalValues
//{
//
//    public static int                                numericalComparisonsCounter = 0;
//    public static int                                numericalOperationsCounter  = 0;
//
//    // private static Set<String> definedPredicates = new HashSet<String>();
//    // private static Set<String> newPredicates = new HashSet<String>();
//
//    private static Map<String, Map<String, Integer>> numPred                     = new HashMap<>();
//    private static Map<String, NumericalExpression>  numPredEx                   = new HashMap<>();
//
//
//    public static boolean isNumericPredicate(String predicate)
//    {
//        if(numPred.keySet().contains(predicate)) { return true; }
//        return false;
//    }
//
//
//    public static boolean evaluate(String predicate, List<String> values)
//    {
//        if(numPred.keySet().contains(predicate)) {
//            double[] v = new double[values.size()];
//            Map<String, Integer> positions = numPred.get(predicate);
//
//            for(int i = 0; i < values.size(); i++) {
//                try {
//                    v[i] = Double.valueOf(values.get(i));
//                } catch(Exception e) {
//                    v[i] = Double.valueOf(HerbrandUniverse.getInstance().getConstant(values.get(i)));
//                }
//            }
//
//            NumericalExpression pred = numPredEx.get(predicate);
//            for(NumericalExpression ex : pred.expressions) {
//                double valueLhs = compute(ex.lhs, positions, v);
//                double valueRhs = compute(ex.rhs, positions, v);
//                System.out.println(valueLhs + " " + ex.comparator + " " + valueRhs + "\t" + compLR(valueLhs, valueRhs, ex.comparator));
//                if(!compLR(valueLhs, valueRhs, ex.comparator)) { return false; }
//            }
//
//        }
//        return true;
//    }
//
//
//    private static double compute(NumericalExpression exP, Map<String, Integer> positions, double[] values)
//    {
//        numericalOperationsCounter += exP.operations.size();
//
//        double value = 0;
//        for(NumericalExpression op : exP.operations) {
//            String operator = op.operator;
//            String opVar = op.variable;
//            double v = 0;
//            if(!op.isFunction()) {
//                if(positions.containsKey(opVar)) {
//                    v = values[positions.get(opVar)];
//                } else {
//                    v = Integer.valueOf(opVar);
//                }
//            } else {
//                // use the stated function to compute the value of v
//                List<Double> varValues = new ArrayList<>();
//                for(NumericalExpression nEx : op.functionExpressions) {
//                    varValues.add(compute(nEx, positions, values));
//                }
//                boolean error = false;
//                switch(op.functionName) {
//                    case "fn:abs":
//                        if(varValues.size() == 1) {
//                            v = Math.abs(varValues.get(0));
//                        } else {
//                            error = true;
//                        }
//                    break;
//                    case "fn:sqrt":
//                        if(varValues.size() == 1) {
//                            v = Math.sqrt(varValues.get(0));
//                        } else {
//                            error = true;
//                        }
//                    break;
//                    case "fn:min":
//                        if(varValues.size() == 2) {
//                            v = Math.min(varValues.get(0), varValues.get(1));
//                        } else {
//                            error = true;
//                        }
//                    break;
//                    case "fn:max":
//                        if(varValues.size() == 2) {
//                            v = Math.max(varValues.get(0), varValues.get(1));
//                        } else {
//                            error = true;
//                        }
//                    break;
//                    default:
//                        System.err.println("The function is \"" + op.functionName + "\" is not defined.");
//                        System.exit(0);
//                }
//
//                if(error) {
//                    System.err.println("The number of required arguments for function \"" + op.functionName + "\" is not matched.");
//                    System.exit(0);
//                }
//                numericalOperationsCounter++;
//            }
//
//            switch(operator) {
//                case "+":
//                    value += v;
//                break;
//                case "-":
//                    value -= v;
//                break;
//                case "*":
//                    value *= v;
//                break;
//                case "/":
//                    value /= v;
//                break;
//                default:
//                break;
//            }
//        }
//
//        return value;
//    }
//
//
//    private static boolean compLR(double valueLhs, double valueRhs, String operator)
//    {
//        numericalComparisonsCounter++;
//        switch(operator) {
//            case "==":
//                return Math.abs(valueLhs - valueRhs) < 0.0000001;
//            case "<=":
//                return valueLhs <= valueRhs;
//            case ">=":
//                return valueLhs >= valueRhs;
//            case "<":
//                return valueLhs < valueRhs;
//            case ">":
//                return valueLhs > valueRhs;
//        }
//
//        return false;
//    }
//
//
//    public static void setNumPred(Map<String, Map<String, Integer>> numPred)
//    {
//        NumericalValues.numPred = numPred;
//    }
//
//
//    public static void setNumPredEx(Map<String, NumericalExpression> numPredEx)
//    {
//        NumericalValues.numPredEx = numPredEx;
//    }
//
//
//    public static void reset()
//    {
//        numericalComparisonsCounter = 0;
//        numericalOperationsCounter = 0;
//        numPred = new HashMap<>();
//        numPredEx = new HashMap<>();
//    }
//
//}