package com.googlecode.rockit.app.solver.aggregate.simple;

import java.util.HashMap;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;


public class AggregationManagerOptimalColumnImpl implements AggregationManager
{
    private HashMap<Integer, AggregationManagerSimpleImpl> aggregationManagers = new HashMap<Integer, AggregationManagerSimpleImpl>();
    private int                                            maxSizeOfLiterals   = 0;
    AggregationManagerSimpleImpl                           optimalAggregation  = null;


    public AggregationManagerOptimalColumnImpl(int maxSizeOfLiterals)
    {
        this.maxSizeOfLiterals = maxSizeOfLiterals;
        for(int i = 0; i < maxSizeOfLiterals; i++) {
            aggregationManagers.put(i, new AggregationManagerSimpleImpl(i));
        }
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#getAggregatedSoftFormulas()
     */
    @Override
    public void addConstraintsToILP(ILPConnector con) throws ILPException
    {
        optimalAggregation.addConstraintsToILP(con);
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#resetAggregatedSoftFormulas()
     */
    @Override
    public void resetAggregatedSoftFormulas()
    {
        aggregationManagers = new HashMap<Integer, AggregationManagerSimpleImpl>();
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#addAggregatedSoftConstraint(double, java.util.ArrayList, boolean)
     */
    @Override
    public void addClauseForAggregation(Clause clause, FormulaSoft formula)
    {
        for(int i = 0; i < maxSizeOfLiterals; i++) {
            aggregationManagers.get(i).addClauseForAggregation(clause, formula);
        }
    }


    @Override
    public int getNumberOfAggregatedClauses()
    {
        if(optimalAggregation != null) { return optimalAggregation.getNumberOfAggregatedClauses(); }
        return 0;
    }


    @Override
    public void calculateAggregation()
    {
        // we take the aggregation manager with the minimal number of agg clauses.
        int minClauses = Integer.MAX_VALUE;
        for(int i = 0; i < maxSizeOfLiterals; i++) {
            AggregationManagerSimpleImpl currentM = aggregationManagers.get(i);
            if(Parameters.DEBUG_OUTPUT)
                System.out.print("AggregationManager: " + i + "th column has " + currentM.getNumberOfAggregatedClauses() + " aggregated Clauses.");
            if(currentM.getNumberOfAggregatedClauses() < minClauses) {
                minClauses = currentM.getNumberOfAggregatedClauses();
                this.optimalAggregation = currentM;
                if(Parameters.DEBUG_OUTPUT)
                    System.out.println(" Chosen.");
            } else {
                if(Parameters.DEBUG_OUTPUT)
                    System.out.println();
            }
        }
        this.resetAggregatedSoftFormulas();
    }


    @Override
    public int getNumberOfCountingConstraintsAggregatingMoreThanOneClause()
    {

        if(optimalAggregation != null) { return optimalAggregation.getNumberOfCountingConstraintsAggregatingMoreThanOneClause(); }
        return 0;
    }


    @Override
    public int getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral()
    {
        if(optimalAggregation != null) { return optimalAggregation.getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral(); }
        return 0;
    }


    @Override
    public int getNumberOfCountingConstraintsWithMoreThanOneLiteral()
    {
        if(optimalAggregation != null) { return optimalAggregation.getNumberOfCountingConstraintsWithMoreThanOneLiteral(); }
        return 0;
    }


    @Override
    public int getNumberOfCountingConstraintsWithOneLiteral()
    {
        if(optimalAggregation != null) { return optimalAggregation.getNumberOfCountingConstraintsWithOneLiteral(); }
        return 0;
    }


    @Override
    public int getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral()
    {
        if(optimalAggregation != null) { return optimalAggregation.getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral(); }
        return 0;
    }

}
