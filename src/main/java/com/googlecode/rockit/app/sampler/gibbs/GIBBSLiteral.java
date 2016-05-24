package com.googlecode.rockit.app.sampler.gibbs;

import java.util.ArrayList;


public class GIBBSLiteral
{

    private String                     name;

    private boolean                    positive;

    private ArrayList<GIBBSClauseHard> clausesHardAndCardinality = new ArrayList<GIBBSClauseHard>();


    public ArrayList<GIBBSClauseHard> getClausesHardAndCardinality()
    {
        return clausesHardAndCardinality;
    }


    public ArrayList<GIBBSClauseSoft> getClausesSoft()
    {
        return clausesSoft;
    }

    private ArrayList<GIBBSClauseSoft> clausesSoft        = new ArrayList<GIBBSClauseSoft>();

    private ArrayList<GIBBSChange>     changedInIteration = new ArrayList<GIBBSChange>();


    /**
     * Documents in which iteration and to which state (true or false) this literal has been changed.
     * 
     * 
     * @param iteration
     * @param theChangedState
     *            state AFTER the change.
     */
    public void i_was_changed_in_iteration(int iteration, boolean theChangedState)
    {
        this.changedInIteration.add(new GIBBSChange(iteration, theChangedState));
    }


    /**
     * Adds hard clause in which the actual literal occurs.
     * 
     * @param c
     */
    public void addHardAndCardinalityClause(GIBBSClauseHard c)
    {
        this.clausesHardAndCardinality.add(c);
    }


    /**
     * Adds soft clause in which the actual literal occurs.
     * 
     * @param c
     */
    public void addSoftClause(GIBBSClauseSoft c)
    {
        this.clausesSoft.add(c);
    }


    public GIBBSLiteral(String name, boolean positive)
    {
        super();
        this.name = name;
        this.positive = positive;
    }


    public void initChangedInIteration()
    {
        // the first point in the result list, is the current state (Iteration 0).
        this.changedInIteration.add(new GIBBSChange(0, positive));
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    /**
     * Current state of the Literal.
     * 
     * @return true if positive, false else.
     */
    public boolean isPositive()
    {
        return positive;
    }


    public void setPositive(boolean positive)
    {
        this.positive = positive;
    }


    /**
     * Current state of the Literal.
     * 
     * @return true if negative, false else.
     */
    public boolean isNegative()
    {
        return !positive;
    }


    /**
     * Sets the literal to positive if it was negative. Or to negative if it was positive.
     * 
     * @return
     */
    public void swap()
    {
        this.positive = !this.positive;
    }


    /**
     * Goes through every soft clause in which the actual literal occurs.
     * And sums up the weight if the current clause is sattisfied.
     * 
     * @return Sum of weight (no exp modification, just the sum)
     */
    public double get_sum_of_clauses_if_i_stay_as_i_am()
    {
        double sum = 0;
        for(GIBBSClauseSoft c : this.clausesSoft) {
            sum = sum + c.getWeightIfTrue();
        }
        return sum;
    }


    /**
     * Swaps the current literal.
     * Goes through every soft clause in which the actual literal occurs.
     * And sums up the weight if the current clause is sattisfied.
     * Finally, it swaps the literal back.
     */
    public double get_sum_of_clauses_if_i_am_swapped()
    {
        this.swap();
        double sum = this.get_sum_of_clauses_if_i_stay_as_i_am();
        this.swap();
        return sum;
    }


    /**
     * Goes through every hard clause and checks if they are still satisfied
     * if this literal is swapped.
     * 
     * @return
     */
    public boolean is_it_possible_to_swap_me()
    {
        swap();
        for(GIBBSClauseHard c : clausesHardAndCardinality) {
            boolean t = c.isTrue();
            if(!t) {
                swap();
                return false;
            }
        }
        swap();
        return true;
    }


    /**
     * After sampling, the probability of the literal is returned. It is computed with the knowledge
     * when the literal was changed during sampling.
     * 
     * @param totalNumberOfIterations
     * @return
     */
    public double return_my_probability(int totalNumberOfIterations)
    {
        int trueInSamples = 0;

        int lastIteration = 0;
        GIBBSChange lastChange = null;

        for(GIBBSChange c : this.changedInIteration) {
            // System.out.println(c);
            // if c is set to true,
            if(c.isTrue()) {
                // then it has been false before, so we do not have to count anything.
                // however, we must remember, when it has been set to true.
                lastIteration = c.getIteration();
            } else {
                // c is set to false.
                // Now we know that c has been set to true in the past (exactly at lastIteration).
                // Thus, we can increase trueInSamples by
                // actualIteration - lastIteration
                trueInSamples = trueInSamples + c.getIteration() - lastIteration;
                // System.out.println(trueInSamples);
            }
            lastChange = c;
        }
        if(lastChange.isTrue()) {
            trueInSamples = trueInSamples + totalNumberOfIterations - lastIteration;
            // System.out.println(trueInSamples);
        }

        return (((double) trueInSamples) / ((double) totalNumberOfIterations));
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GIBBSLiteral [name=");
        builder.append(name);
        builder.append(", positive=");
        builder.append(positive);
        builder.append("]");
        return builder.toString();
    }


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
        GIBBSLiteral other = (GIBBSLiteral) obj;
        if(name == null) {
            if(other.name != null)
                return false;
        } else if(!name.equals(other.name))
            return false;
        return true;
    }
}
