package com.googlecode.rockit.javaAPI.formulas;

import java.util.ArrayList;
import java.util.HashSet;

import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.formulas.expressions.IfExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;
import com.googlecode.rockit.javaAPI.predicates.PredicateAbstract;
import com.googlecode.rockit.javaAPI.types.Type;


/**
 * The basic class of all formular.
 * 
 * Defines the For- and the If-part of every formular.
 * 
 * @author jan
 *
 */

public abstract class FormulaAbstract implements Comparable<FormulaAbstract>
{
    private HashSet<VariableType>   forVariables          = new HashSet<VariableType>();
    private ArrayList<IfExpression> ifExpressions         = new ArrayList<IfExpression>();
    private String                  name;
    private boolean                 cuttingPlaneInference = true;
    private String                  sqlQuery;


    public abstract HashSet<PredicateAbstract> getAllHiddenPredicatesSet();


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        FormulaAbstract other = (FormulaAbstract) obj;
        if(name == null) {
            if(other.name != null)
                return false;
        } else if(!name.equals(other.name))
            return false;
        return true;
    }


    public HashSet<PredicateAbstract> getAllObservedPredicates()
    {
        HashSet<PredicateAbstract> result = new HashSet<PredicateAbstract>();

        for(IfExpression ifE : ifExpressions) {
            if(ifE instanceof PredicateExpression) {
                result.add(((PredicateExpression) ifE).getPredicate());
            }
        }
        return result;
    }


    public HashSet<Type> getAllTypes()
    {
        HashSet<Type> result = new HashSet<Type>();
        for(VariableType var : this.forVariables) {
            if(var instanceof VariableType) {
                result.add(((VariableType) var).getType());
            }
        }
        return result;
    }


    public void setForVariables(VariableType... forVariables)
    {
        for(int i = 0; i < forVariables.length; i++) {
            this.forVariables.add(forVariables[i]);
        }
    }


    public void setForVariables(HashSet<VariableType> forVariables)
    {
        this.forVariables = forVariables;
    }


    public HashSet<VariableType> getForVariables()
    {
        return forVariables;
    }


    public void setIfExpressions(ArrayList<IfExpression> ifExpressions) throws ParseException
    {
        for(IfExpression ex : ifExpressions) {
            this.checkIfExpression(ex);
        }
        this.ifExpressions = ifExpressions;
    }


    public void addIfExpression(IfExpression ifExpression) throws ParseException
    {
        this.checkIfExpression(ifExpression);

        this.ifExpressions.add(ifExpression);
    }


    public void setIfExpressions(IfExpression... ifExpressions) throws ParseException
    {
        for(int i = 0; i < ifExpressions.length; i++) {
            this.checkIfExpression(ifExpressions[i]);
            this.ifExpressions.add(ifExpressions[i]);
        }
    }


    public ArrayList<IfExpression> getIfExpressions()
    {
        return ifExpressions;
    }


    private void checkIfExpression(IfExpression ex) throws ParseException
    {
        if(ex.getClass().equals(PredicateExpression.class)) {
            PredicateExpression exPred = (PredicateExpression) ex;
            if(exPred.getPredicate().isHidden()) { throw new ParseException("Error in the if part of the formular. The predicate " + exPred.getPredicate().getName() + " is set to hidden. But only observed predicates are allowed here."); }
        }
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        /*
         * sb.append("//formular ");
         * if(!(this instanceof FormularObjective)){
         * sb.append("[cpi=");
         * if(this.cuttingPlaneInference){
         * sb.append("yes");
         * }else{
         * sb.append("no");
         * }
         * sb.append("] ");
         * }
         * sb.append(this.getName());
         * sb.append("\n  ");
         */
        /*
         * int i=0;
         * for(VariableType fv: this.getVariableTypes()){
         * sb.append(fv.toMediumString());
         * if(i==this.getVariableTypes().size()-1){
         * // nothing
         * } else{
         * sb.append(",");
         * }
         * i++;
         * }
         */
        // sb.append("\n  if ");
        // int i=0;
        for(IfExpression ie : this.getIfExpressions()) {
            if(ie instanceof PredicateExpression) {
                PredicateExpression p = (PredicateExpression) ie;
                sb.append(p.toString(true));
                sb.append(" v ");
            }
            // i++;
        }

        // sb.append("");
        return sb.toString();
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getName()
    {
        return name;
    }


    @Override
    public int compareTo(FormulaAbstract o)
    {
        return this.getName().compareTo(o.getName());
    }


    public void useCuttingPlaneInference(boolean cuttingPlaneInference)
    {
        this.cuttingPlaneInference = cuttingPlaneInference;
    }


    public boolean isCuttingPlaneInferenceUsed()
    {
        return cuttingPlaneInference;
    }


    public String getSqlQuery()
    {
        return sqlQuery;
    }


    public void setSqlQuery(String sqlQuery)
    {
        this.sqlQuery = sqlQuery;
    }
}
