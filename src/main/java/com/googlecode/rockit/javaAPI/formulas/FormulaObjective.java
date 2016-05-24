package com.googlecode.rockit.javaAPI.formulas;

import java.util.ArrayList;
import java.util.HashSet;

import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.formulas.expressions.IfExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableDouble;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;
import com.googlecode.rockit.javaAPI.predicates.PredicateAbstract;


/**
 * Formular with only 1 positive literal assigned with a double weight.
 * 
 * @author jan
 *
 */

public class FormulaObjective extends FormulaAbstract
{
    private VariableDouble      doubleVariable;
    private PredicateExpression objectiveExpression;
    private Double              weight = null;


    public FormulaObjective()
    {
    }


    public FormulaObjective(String name, HashSet<VariableType> forVariables, ArrayList<IfExpression> ifExpressions, VariableDouble doubleVariable, PredicateExpression objectiveExpression) throws ParseException
    {
        this.setForVariables(forVariables);
        this.setName(name);
        this.setIfExpressions(ifExpressions);
        this.setDoubleVariable(doubleVariable);
        this.setObjectiveExpression(objectiveExpression);
    }


    public void setObjectiveExpression(PredicateExpression objectiveExpression) throws ParseException
    {
        this.checkObjectiveExpression(objectiveExpression);
        this.objectiveExpression = objectiveExpression;
    }


    public PredicateExpression getObjectiveExpression()
    {
        return objectiveExpression;
    }


    public void setDoubleVariable(VariableDouble doubleVariable)
    {
        this.doubleVariable = doubleVariable;
    }


    public VariableDouble getDoubleVariable()
    {
        return doubleVariable;
    }


    private void checkObjectiveExpression(PredicateExpression ex) throws ParseException
    {
        if(ex.getClass().equals(PredicateExpression.class)) {
            PredicateExpression exPred = (PredicateExpression) ex;
            if(exPred.getPredicate().isObserved()) { throw new ParseException("Error in the objective part of the formular. The predicate " + exPred.getPredicate().getName() + " is set to observed. But only hidden predicates are allowed in the objective part."); }
            if(exPred.isNegative()) { throw new ParseException("Error in the objective part of the formular. The predicate " + exPred.getPredicate().getName() + " is set to negative. But only positive predicates are allowed in the objective part."); }
        }
    }


    public void setWeights(Double weight)
    {
        this.weight = weight;
    }


    public Double getWeight()
    {
        return weight;
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        if(this.weight != null) {
            sb.append(this.getWeight()).append("  ");
        } else if(this.doubleVariable != null) {
            sb.append(this.doubleVariable.getName()).append(":  ");
        }
        sb.append(super.toString());
        sb.append(" ");
        sb.append(this.objectiveExpression.toString());
        sb.append("\n");
        return sb.toString();
    }


    @Override
    public HashSet<PredicateAbstract> getAllHiddenPredicatesSet()
    {
        HashSet<PredicateAbstract> result = new HashSet<PredicateAbstract>();
        result.add(objectiveExpression.getPredicate());
        return result;
    }
}
