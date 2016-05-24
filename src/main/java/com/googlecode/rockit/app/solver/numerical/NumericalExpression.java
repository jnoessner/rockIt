//package com.googlecode.rockit.app.solver.numerical;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class NumericalExpression
//{
//    List<NumericalExpression> expressions = new ArrayList<>();
//    String                    comparator;
//
//    NumericalExpression       lhs;
//    NumericalExpression       rhs;
//
//    List<NumericalExpression> operations  = new ArrayList<>();
//    String                    operator;
//    String                    variable;
//
//    String                    functionName;
//    List<NumericalExpression> functionExpressions;
//
//
//    public NumericalExpression()
//    {
//
//    }
//
//
//    public NumericalExpression(String operator, String variable)
//    {
//        this.operator = operator;
//        this.variable = variable;
//    }
//
//
//    public NumericalExpression(String operator, String functionName, List<NumericalExpression> variables)
//    {
//        this.operator = operator;
//        this.functionName = functionName;
//        this.functionExpressions = variables;
//    }
//
//
//    public NumericalExpression(NumericalExpression lhs, NumericalExpression rhs, String comparator)
//    {
//        this.lhs = lhs;
//        this.rhs = rhs;
//        this.comparator = comparator;
//    }
//
//
//    public void addExpression(NumericalExpression ex)
//    {
//        expressions.add(ex);
//    }
//
//
//    public void addOperations(NumericalExpression ex)
//    {
//        operations.add(ex);
//    }
//
//
//    @Override
//    public String toString()
//    {
//        StringBuilder sb = new StringBuilder();
//        if(expressions.size() == 0) {
//            for(NumericalExpression o : operations) {
//                sb.append(o.operator + o.variable);
//            }
//        } else {
//            for(NumericalExpression ne : expressions) {
//                for(NumericalExpression o : ne.lhs.operations) {
//                    sb.append(o.operator + o.variable);
//                }
//                sb.append(" " + ne.comparator + " ");
//                for(NumericalExpression o : ne.rhs.operations) {
//                    sb.append(o.operator + o.variable);
//                }
//                sb.append(System.lineSeparator());
//            }
//        }
//        return sb.toString();
//    }
//
//
//    public boolean isFunction()
//    {
//        return functionName != null;
//    }
//}
