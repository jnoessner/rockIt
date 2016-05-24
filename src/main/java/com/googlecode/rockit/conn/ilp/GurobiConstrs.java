package com.googlecode.rockit.conn.ilp;

import java.util.ArrayList;
import java.util.TreeSet;

import gurobi.GRBConstr;
import gurobi.GRBException;
import gurobi.GRBLinExpr;
import gurobi.GRBModel;


public class GurobiConstrs
{
    private ArrayList<GRBLinExpr> expressions = new ArrayList<GRBLinExpr>();
    private ArrayList<Character>  equalTypes  = new ArrayList<Character>();
    private ArrayList<Double>     values      = new ArrayList<Double>();
    private ArrayList<String>     names       = new ArrayList<String>();


    public GurobiConstrs()
    {
        this.reset();
    }


    public void reset()
    {
        expressions = new ArrayList<GRBLinExpr>();
        equalTypes = new ArrayList<Character>();
        values = new ArrayList<Double>();
        names = new ArrayList<String>();
    }


    public void addConstr(GRBLinExpr expr, Character equalType, Double value, String name)
    {
        expressions.add(expr);
        equalTypes.add(equalType);
        values.add(value);
        names.add(name);
    }


    public void addConstr(GRBLinExpr expr, Character equalType, Double value, String name, TreeSet<String> variableName)
    {
        expressions.add(expr);
        equalTypes.add(equalType);
        values.add(value);
        names.add(name);
    }


    public GRBConstr[] addToModel(GRBModel model) throws GRBException
    {
        GRBConstr[] addedConstrs = new GRBConstr[expressions.size()];
        for(int i = 0; i < expressions.size(); i++) {
            addedConstrs[i] = model.addConstr(expressions.get(i), equalTypes.get(i), values.get(i), names.get(i));
        }
        return addedConstrs;
    }


    public ArrayList<GRBLinExpr> getExpressions()
    {
        // return expressions.toArray(new GRBLinExpr[expressions.size()]);
        return expressions;
    }


    public ArrayList<Character> getEqualTypes()
    {
        /*
         * char[] result = new char[values.size()];
         * for(int i = 0; i<equalTypes.size(); i++){
         * result[i] = equalTypes.get(i);
         * }
         */
        return equalTypes;
    }


    public ArrayList<Double> getValues()
    {
        /*
         * double[] result = new double[values.size()];
         * for(int i = 0; i<values.size(); i++){
         * result[i] = values.get(i);
         * }
         */

        return values;
    }


    public ArrayList<String> getNames()
    {
        return names;
        // names.toArray(new String[names.size()]);
    }
}
