package com.googlecode.rockit.app.solver.aggregate.simple;

import java.util.ArrayList;

import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.exception.ILPException;


public class AggregatedConstraint
{
    private String             id;
    private ArrayList<Literal> aggregatedVars = new ArrayList<Literal>();
    private ArrayList<Literal> singleVars     = new ArrayList<Literal>();

    private double             weight;
    private boolean            conjunction;


    // private ArrayList<GRBConstr> oldConstraints = new ArrayList<GRBConstr>();

    // private boolean deleteConstr =true;

    public AggregatedConstraint(String id, ArrayList<Literal> aggregatedVars, boolean conjunction, double weight)
    {
        this.id = id;
        // this.zVariable=zVariable;
        this.aggregatedVars = aggregatedVars;
        this.weight = weight;
        this.conjunction = conjunction;
        // this.oldConstraints=new ArrayList<GRBConstr>();
        // this.deleteConstr=true;
    }


    public void addSingleVar(Literal singleVar)
    {
        this.singleVars.add(singleVar);
        // this.deleteConstr=true;
    }


    public double getWeight()
    {
        return this.weight;
    }


    public void addConstraintAndDeleteOldOne(ILPConnector con) throws ILPException
    {
        con.addAggregatedConstraint(weight, aggregatedVars, singleVars, conjunction);
    }


    public boolean aggregatedMoreThanOneClause()
    {
        if(singleVars.size() > 1) { return true; }
        return false;
    }


    public boolean hasMoreThanOneLiteral()
    {
        if(aggregatedVars.size() > 0) { return true; }
        return false;
    }


    public int numberOfClausesWithMoreThanOneLiteral()
    {
        if(hasMoreThanOneLiteral()) { return singleVars.size(); }
        return 0;
    }


    public int numberOfClausesWithOneLiteral()
    {
        if(hasMoreThanOneLiteral()) { return 0; }
        return singleVars.size();
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregatedConstraint#hashCode()
     */
    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregatedConstraint#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        AggregatedConstraint other = (AggregatedConstraint) obj;
        if(id == null) {
            if(other.id != null)
                return false;
        } else if(!id.equals(other.id))
            return false;
        return true;
    }

}
