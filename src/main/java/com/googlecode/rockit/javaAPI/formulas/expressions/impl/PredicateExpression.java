package com.googlecode.rockit.javaAPI.formulas.expressions.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.googlecode.rockit.javaAPI.formulas.expressions.IfExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableAbstract;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableString;
import com.googlecode.rockit.javaAPI.predicates.PredicateAbstract;


/**
 * Contains the predicate and a list of variables for instantiating the
 * predicate.
 * 
 * The list of variables must be as long as the Type List for the predicate.
 * Further, the variables must have the same types than the predicates.
 * 
 * @author jan
 *
 */

public class PredicateExpression implements IfExpression
{
    private PredicateAbstract           predicate;
    private ArrayList<VariableAbstract> variables          = new ArrayList<VariableAbstract>();
    private boolean                     positive;

    private static int                  theBeastVarCounter = 0;
    private ArrayList<String>           theBeastForExtension;


    public PredicateExpression()
    {
    }


    public PredicateExpression(boolean positive, PredicateAbstract predicate)
    {
        this.positive = positive;
        this.predicate = predicate;
    }


    public PredicateExpression(boolean positive, PredicateAbstract predicate, ArrayList<VariableAbstract> variables)
    {
        this.positive = positive;
        this.predicate = predicate;
        this.variables = variables;
    }


    public PredicateExpression(boolean positive, PredicateAbstract predicate, VariableAbstract... variables)
    {
        this.positive = positive;
        this.predicate = predicate;
        for(int i = 0; i < variables.length; i++) {
            this.variables.add(variables[i]);
        }
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


    public void setPredicate(PredicateAbstract predicate)
    {
        this.predicate = predicate;
    }


    public PredicateAbstract getPredicate()
    {
        return predicate;
    }


    public void setVariables(ArrayList<VariableAbstract> variables)
    {
        this.variables = variables;
    }


    public void addVariable(VariableAbstract variable)
    {
        this.variables.add(variable);
    }


    public ArrayList<VariableAbstract> getVariables()
    {
        return variables;
    }


    public String toString()
    {
        return toString(false);
    }


    public String toString(boolean negated)
    {
        StringBuilder sb = new StringBuilder();
        // because we express it as disjunction (or), it has to be negated.
        if((this.positive && negated) || (!this.positive && !negated))
            sb.append("!");
        sb.append(this.getPredicate().toString()).append("(");
        int i = 0;
        for(VariableAbstract va : this.getVariables()) {
            sb.append(va.toString());
            if(i < this.getVariables().size() - 1) {
                sb.append(", ");
            }
            i++;
        }
        sb.append(")");
        return sb.toString();
    }


    public String toTheBeastString(boolean negated)
    {
        this.theBeastForExtension = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        // because we express it as disjunction (or), it has to be negated.
        if((this.positive && negated) || (!this.positive && !negated))
            sb.append("!");
        sb.append(this.getPredicate().toString().toLowerCase()).append("(");
        HashMap<String, String> variableStringVars = new HashMap<String, String>();
        int i = 0;
        for(VariableAbstract va : this.getVariables()) {
            if(this.predicate.isObserved() && va instanceof VariableString) {
                StringBuilder v = new StringBuilder();
                v.append("vhelp").append(theBeastVarCounter);
                theBeastVarCounter++;
                variableStringVars.put(v.toString(), va.toString());
                sb.append(v);
                StringBuilder forHelper = new StringBuilder();
                forHelper.append(this.predicate.getTypes().get(i).getName());
                forHelper.append(" ").append(v);
                this.theBeastForExtension.add(forHelper.toString());
            } else {
                sb.append(va.toString());
            }
            if(i < this.getVariables().size() - 1) {
                sb.append(", ");
            }
            i++;
        }
        sb.append(")");
        i = 0;
        for(Entry<String, String> v : variableStringVars.entrySet()) {
            sb.append(" & ").append(v.getKey()).append(" == ").append(v.getValue()).append(" ");
        }
        return sb.toString();
    }


    @Override
    public HashSet<VariableAbstract> getAllVariables()
    {
        HashSet<VariableAbstract> result = new HashSet<VariableAbstract>();
        for(VariableAbstract v : variables) {
            result.add(v);
        }
        return result;
    }


    public ArrayList<String> getTheBeastForExtension()
    {
        return theBeastForExtension;
    }

}
