package com.googlecode.rockit.app.sampler.gibbs;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.sampler.symmetry.SymmetryPostProcessing;
import com.googlecode.rockit.app.solver.pojo.CardinalityClause;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.SolveException;


public class GIBBSSampler
{

    /**
     * We build a datastructure out of the ground clauses so that we can sample efficiently.
     * 
     * Therefore, we create a list of literals where each literal is connected with its containing ground clause.
     * If we now pick one of those literals, we know immediately, which ground clauses are affected if we swap the literal.
     * 
     * We distinguish between soft, hard and cardinality (includes existential) clauses.
     * 
     * @param clauses
     * @param evidence
     * @param startingPoints
     *            literals set to true initially (retrieved from MAP state)
     * @return
     */
    public ArrayList<GIBBSLiteral> convertToDataStructure(ArrayList<Clause> clauses, Collection<Literal> evidence, ArrayList<String> startingPoints)
    {
        HashMap<String, GIBBSLiteral> literals = new HashMap<String, GIBBSLiteral>();

        System.out.println("- convert to GIBBS data structure");

        for(Clause c : clauses) {
            // create new GIBBSClause (soft, hard, or cardinality)
            GIBBSClauseHard cGibbs = null;
            if(c instanceof CardinalityClause) {
                CardinalityClause cc = (CardinalityClause) c;
                cGibbs = new GIBBSClauseCardinality(cc.isLessEqual(), cc.getCardinality());
            } else {
                if(c.isHard()) {
                    cGibbs = new GIBBSClauseHard();
                } else {
                    cGibbs = new GIBBSClauseSoft(c.getWeight());
                }
            }

            for(Literal l : c.getRestriction()) {
                // get or create new GIBBSVariable
                String lName = l.getName();
                GIBBSLiteral lGibbs = literals.get(lName);
                if(lGibbs == null) {
                    // every literal is initially set to false
                    lGibbs = new GIBBSLiteral(lName, false);
                    literals.put(lName, lGibbs);
                }

                // link clause with variable and vice versa
                if(c.isHard()) {
                    lGibbs.addHardAndCardinalityClause(cGibbs);
                } else {
                    lGibbs.addSoftClause((GIBBSClauseSoft) cGibbs);
                }
                if(l.isPositive()) {
                    cGibbs.addPositiveLiteral(lGibbs);
                } else {
                    cGibbs.addNegativeLiteral(lGibbs);
                }
            }

        }

        // set all literals to true, which are true in the current map state
        for(String trueLiteral : startingPoints) {
            GIBBSLiteral l = literals.get(trueLiteral);
            if(l == null) {
                literals.put(trueLiteral, new GIBBSLiteral(trueLiteral, true));
            } else {
                l.setPositive(true);
            }

        }

        // init the datastructure which will document the sampling results
        // with the current truth value of the literal and prepare the resulting ArrayList.
        ArrayList<GIBBSLiteral> result = new ArrayList<GIBBSLiteral>();
        for(GIBBSLiteral lGibbs : literals.values()) {
            lGibbs.initChangedInIteration();
            result.add(lGibbs);
            // System.out.println(lGibbs);
            // System.out.println("hard " + lGibbs.getClausesHard());
            // System.out.println("soft " + lGibbs.getClausesSoft());
        }

        return result;
    }


    /**
     * Gibbs sampling.
     * 
     * Decides if to swap one axiom per sampling round.
     * We do not swap the axiom if it violates any hard constraint (we then go to the next sampling round).
     * 
     * We swap the axiom with probability:
     * exp(sum w with swapped literal)/(exp(sum w with swapped literal)+exp(sum w without change)
     * 
     * Only those clauses are included in the calculation which might change when swapping the selected litereal.
     * 
     * @param numberOfSamples
     *            Number of literals we try to swap
     * @param solver
     *            Standard Solver
     * @return
     * @throws SQLException
     * @throws SolveException
     * @throws ParseException
     */
    public ArrayList<GIBBSLiteral> sample(int numberOfSamples, ArrayList<Clause> clauses, Collection<Literal> evidence, ArrayList<String> startingPoints) throws SQLException, SolveException, ParseException
    {

        // if we want to use symmetry sampling, we write the clauses to the Dimacs file writer.
        SymmetryPostProcessing symmetry = null;
        if(Parameters.USE_SYMMETRIES_IN_MARGINAL_INFERENCE) {
            symmetry = new SymmetryPostProcessing(clauses);
        }
        // convert clauses to efficient data structure
        ArrayList<GIBBSLiteral> literals = this.convertToDataStructure(clauses, evidence, startingPoints);
        clauses = null; // free memory

        long startSampling = System.currentTimeMillis();

        // initialize random and some important variables.
        Random r = new Random();
        int numberLiterals = literals.size();
        System.out.print("- number of literals ");
        System.out.println(numberLiterals);
        long intervall = 10000;
        // if numberOfSamples was not set (or set to a negative value), we set it

        if(numberOfSamples < 0) {
            numberOfSamples = Math.min(numberLiterals * 1000, 100000000);
        }

        System.out.print("- start ");
        System.out.print(numberOfSamples);
        System.out.println(" GIBBS sampling rounds (feedback every 10 seconds).");
        for(int iteration = 1; iteration <= numberOfSamples; iteration++) {
            // System.out.println("iteration  " +iteration);

            // fetch random literal
            GIBBSLiteral l = null;

            l = literals.get(r.nextInt(numberLiterals));

            // if literal l is swapable...
            if(l.is_it_possible_to_swap_me()) {
                // ... we compute swap probability.
                // exp(sum w with swapped literal)/(exp(sum w with swapped literal)+exp(sum w without change)
                double swappedExpSum = Math.exp(l.get_sum_of_clauses_if_i_am_swapped());
                double nonSwappedExpSum = Math.exp(l.get_sum_of_clauses_if_i_stay_as_i_am());
                double swapProbability = swappedExpSum / (swappedExpSum + nonSwappedExpSum);

                // System.out.println(swapProbability);
                // If our random variable is less or equal our swap probability, we...
                // System.out.println("swap-prob: " +swapProbability);
                if(r.nextDouble() <= swapProbability) {
                    // System.out.println("swap!");
                    // ... swap literal.
                    l.swap();
                    // ... document the swap in the result
                    l.i_was_changed_in_iteration(iteration, l.isPositive());
                    // System.out.println("### changed " + l + " to " + l.isPositive() + " in iteration " + iteration);
                }
            }
            if(iteration % 100 == 0) {
                long intermediate = System.currentTimeMillis() - startSampling;
                if(intermediate > intervall) {
                    System.out.print("Sampled ");
                    System.out.print(iteration);
                    System.out.print(" of ");
                    System.out.print(numberOfSamples);
                    System.out.print(" in ");
                    System.out.print(intermediate);
                    System.out.println(" ms.");
                    intervall = intervall + 10000;
                }
            }
        }
        long intermediate = System.currentTimeMillis() - startSampling;
        System.out.print("Finished: Sampled ");
        System.out.print(numberOfSamples);
        System.out.print(" in ");
        System.out.print(intermediate);
        System.out.println(" ms.");

        if(Parameters.USE_SYMMETRIES_IN_MARGINAL_INFERENCE) {
            symmetry.computeSymmetries(literals, numberOfSamples);
        }

        return literals;

    }

}
