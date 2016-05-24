package com.googlecode.rockit.app.solver.aggregate.simple;

import java.util.ArrayList;
import java.util.HashMap;

import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;


public class AggregationManagerSimpleImpl implements AggregationManager
{
    private int                                   positionOfSingleLiteral = 0;

    // for aggregation of soft formulas if there is no double variable assigned
    private HashMap<String, AggregatedConstraint> aggregatedSoftFormulas  = new HashMap<String, AggregatedConstraint>();


    public AggregationManagerSimpleImpl(int positionOfSingleLiteral)
    {
        this.setPositionOfSingleLiteral(positionOfSingleLiteral);
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#getAggregatedSoftFormulas()
     */
    @Override
    public void addConstraintsToILP(ILPConnector con) throws ILPException
    {
        for(AggregatedConstraint constr : aggregatedSoftFormulas.values()) {
            constr.addConstraintAndDeleteOldOne(con);
        }
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#resetAggregatedSoftFormulas()
     */
    @Override
    public void resetAggregatedSoftFormulas()
    {
        this.aggregatedSoftFormulas = new HashMap<String, AggregatedConstraint>();
    }


    /*
     * (non-Javadoc)
     * @see com.googlecode.rockit.conn.ilpSolver.aggregate.AggregationManager#addAggregatedSoftConstraint(double, java.util.ArrayList, boolean)
     */
    @Override
    public void addClauseForAggregation(Clause clause, FormulaSoft formula)
    {
        // take the first one as single var.
        StringBuilder id = new StringBuilder();
        // everything but the first one!
        ArrayList<Literal> restriction = clause.getRestriction();
        if(restriction.size() > 0) {
            int position = Math.min(this.positionOfSingleLiteral, restriction.size() - 1);
            Literal singleAxiom = restriction.get(position);
            ArrayList<Literal> aggregatedAxioms = new ArrayList<Literal>();
            for(int i = 0; i < restriction.size(); i++) {
                if(i != position) {
                    Literal a = restriction.get(i);
                    id.append(a.toString());
                    aggregatedAxioms.add(a);
                }
            }
            AggregatedConstraint constr = aggregatedSoftFormulas.get(id.toString());

            if(constr != null) {
                constr.addSingleVar(singleAxiom);
            } else {

                AggregatedConstraint newConstr = new AggregatedConstraint(id.toString(), aggregatedAxioms, formula.isConjunction(), clause.getWeight());
                this.aggregatedSoftFormulas.put(id.toString(), newConstr);
                newConstr.addSingleVar(singleAxiom);
            }
        }
    }


    @Override
    public int getNumberOfAggregatedClauses()
    {
        return this.aggregatedSoftFormulas.size();
    }


    @Override
    public void calculateAggregation()
    {
        // here we have to do nothing, since aggregation already have been calculated.
    }


    public int getPositionOfSingleLiteral()
    {
        return positionOfSingleLiteral;
    }


    public void setPositionOfSingleLiteral(int positionOfSingleLiteral)
    {
        this.positionOfSingleLiteral = positionOfSingleLiteral;
    }


    @Override
    public int getNumberOfCountingConstraintsAggregatingMoreThanOneClause()
    {
        int moreThanOne = 0;
        for(AggregatedConstraint c : aggregatedSoftFormulas.values()) {
            if(c.aggregatedMoreThanOneClause()) {
                moreThanOne = moreThanOne + 1;
            }
        }
        return moreThanOne;
    }


    @Override
    public int getNumberOfConstraintsAggregatedByContingConstraintWithMoreThanOneLiteral()
    {
        int number = 0;
        for(AggregatedConstraint c : aggregatedSoftFormulas.values()) {
            number = number + c.numberOfClausesWithMoreThanOneLiteral();
        }
        return number;
    }


    @Override
    public int getNumberOfCountingConstraintsWithMoreThanOneLiteral()
    {
        int count = 0;
        for(AggregatedConstraint c : aggregatedSoftFormulas.values()) {
            if(c.hasMoreThanOneLiteral()) {
                count = count + 1;
            }
        }
        return count;
    }


    @Override
    public int getNumberOfCountingConstraintsWithOneLiteral()
    {
        int count = 0;
        for(AggregatedConstraint c : aggregatedSoftFormulas.values()) {
            if(!c.hasMoreThanOneLiteral()) {
                count = count + 1;
            }
        }
        return count;
    }


    @Override
    public int getNumberOfConstraintsAggregatedByContingConstraintWithOneLiteral()
    {
        int number = 0;
        for(AggregatedConstraint c : aggregatedSoftFormulas.values()) {
            number = number + c.numberOfClausesWithOneLiteral();
        }
        return number;
    }
}
