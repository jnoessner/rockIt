package com.googlecode.rockit.javaAPI.formulas;

import java.util.ArrayList;
import java.util.HashSet;

import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.formulas.expressions.IfExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableDouble;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;


/**
 * Defines a soft formular. A soft formular is a conjunction of positive or negative literals
 * assigned with a weight.
 * 
 * At the moment this is restricted to a positive weight.
 * 
 * 
 * @author jan
 *
 */

public class FormulaSoft extends FormulaHard
{
    private VariableDouble doubleVariable;
    private Double         weight = null;


    public FormulaSoft()
    {
    }


    public FormulaSoft(String name, HashSet<VariableType> forVariables, ArrayList<IfExpression> ifExpressions, VariableDouble doubleVariable, ArrayList<PredicateExpression> restrictions, boolean usesConjunction) throws ParseException
    {
        this.setForVariables(forVariables);
        this.setName(name);
        this.setIfExpressions(ifExpressions);
        this.setDoubleVariable(doubleVariable);
        this.setRestrictions(restrictions);
        if(usesConjunction) {
            this.setConjunction();
        } else {
            this.setDisjunction();
        }
    }


    public Double getWeight()
    {
        return weight;
    }


    public void setDoubleVariable(VariableDouble doubleVariable)
    {
        this.doubleVariable = doubleVariable;
    }


    public VariableDouble getDoubleVariable()
    {
        return doubleVariable;
    }


    public void setWeight(Double weight)
    {
        this.weight = weight;
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if(weight != null)
            sb.append(this.getWeight());
        if(doubleVariable != null)
            sb.append(this.doubleVariable.toString()).append(":");
        sb.append("  ");

        sb.append(super.toSuperString());

        if(this.isConjunction())
            sb.append("(");
        int i = 0;
        for(PredicateExpression restriction : this.getRestrictions()) {
            sb.append(restriction.toString());
            if(i < (this.getRestrictions().size() - 1)) {
                if(this.isConjunction()) {
                    sb.append(" n ");
                } else {
                    sb.append(" v ");
                }
            }
            i++;
        }
        if(this.isConjunction())
            sb.append(")");
        /*
         * if(this.doubleVariable!= null){
         * sb.append(" * ");
         * sb.append(this.doubleVariable.toString());
         * }
         */
        sb.append("\n");
        return sb.toString();
    }

}
