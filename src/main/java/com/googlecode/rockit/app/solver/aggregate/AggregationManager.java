package com.googlecode.rockit.app.solver.aggregate;

import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;


public interface AggregationManager
{

    public abstract void resetAggregatedSoftFormulas();


    /**
     * Adds aggregated soft constraints.
     * 
     * These aggregated soft constraints will be included into the gurobi model,
     * when solve() is executed.
     * 
     * @param weight
     * @param variableNames
     * @param mustBePositive
     * @param conjunction
     * @param id
     * @throws ILPException
     */
    public void addClauseForAggregation(Clause clause, FormulaSoft formula);


    public void addConstraintsToILP(ILPConnector connector) throws ILPException, SolveException;


    public String toString();


    public int getNumberOfAggregatedClauses();


    public int getNumberOfCountingConstraintsAggregatingMoreThanOneClause();


    public int getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral();


    public int getNumberOfCountingConstraintsWithMoreThanOneLiteral();


    public int getNumberOfCountingConstraintsWithOneLiteral();


    public int getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral();


    public void calculateAggregation();

}