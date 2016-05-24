package com.googlecode.rockit.app.solver.exact;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.StandardSolver;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.javaAPI.Model;


/**
 * 
 * @author Bernd
 * 
 */
public class ExactSolver
{
    private HerbrandUniverse  u          = HerbrandUniverse.getInstance();

    /**
     * Grounded predicates, e.g if I have *redConf(x, _float), conf: redConf(x) => red(x), redConf(A, 1.0986), and red(x) => box(x) then the elements are { [!red(A),!box(A)], [!red(A), box(A)], [red(A), box(A)] }. An iterable for all possible worlds.
     */
    private GroundingSet      X;
    /**
     * All formulas completely grounded, e.g if I have *redConf(x, _float), conf: redConf(x) => red(x), redConf(A, 1.0986), and red(x) => box(x) then the elements are { 1.0986 red(A), and red(A) => box(A) }.
     */
    private ArrayList<Clause> G;

    /**
     * normalization constant
     */
    private double            z          = 0;

    private boolean           normalized = false;


    public ExactSolver(Model model) throws ParseException, SolveException, SQLException
    {
        StandardSolver solver = new StandardSolver(model);
        solver.performGrounding();
        G = solver.getAllClauses();
        solver.close();
        X = new GroundingSet(G);
    }


    public Map<GroundingSet, Double> solve()
    {
        System.out.println("Performing exact inference for " + X.getNumberOfWorlds() + " (potentially) possible worlds.");
        BigInteger impossible = BigInteger.ZERO;
        long start = System.currentTimeMillis();
        if(Parameters.DEBUG_OUTPUT) {
            // debug();
            // if(true) { return null; }
        }

        Map<GroundingSet, Double> result = new LinkedHashMap<GroundingSet, Double>();

        for(GroundingSet world : X)
            WorldCheck: {
                if(Parameters.DEBUG_OUTPUT) {
                    // System.out.println("#Positive: " + world.getPositive());
                    // System.out.println("#Negative: " + world.getNegative());
                }
                double weightsum = 0;
                // System.out.println("-----------------------------");
                // System.out.println("World: " + world);

                for(Clause clause : G)
                    RestrictionCheck: {
                        // System.out.println(clause);
                        boolean violation = true;

                        // System.out.println("Positive:");
                        // If any literal from the formula (restriction) is fulfilled in the world, the formula is fulfilled
                        for(Literal restriction : clause.getRestriction()) {
                            for(Literal worldLiteral : world.getPositive()) {
                                // System.out.print("(World) " + worldLiteral + "\tvs.\t " + restriction + " (Check) -> ");
                                if(restriction.equals(worldLiteral) && restriction.isPositive()) {
                                    if(!clause.isHard()) {
                                        weightsum += clause.getWeight();
                                    }
                                    violation = false;
                                    // System.out.println("TRUE");
                                    // XXX: I accidentally 93 MB, is this bad?
                                    break RestrictionCheck;
                                } else {
                                    // System.out.println("FALSE");
                                }

                            }

                            // System.out.println("Negative:");

                            if(violation) {
                                for(Literal worldLiteral : world.getNegative()) {
                                    // System.out.print("(World) " + worldLiteral + "\tvs.\t " + restriction + " (Check) -> ");
                                    if(restriction.equals(worldLiteral) && !restriction.isPositive()) {
                                        if(!clause.isHard()) {
                                            weightsum += clause.getWeight();
                                        }
                                        violation = false;
                                        // System.out.println("TRUE");
                                        break RestrictionCheck;
                                    } else {
                                        // System.out.println("FALSE");
                                    }
                                }
                            }

                        }
                        if(clause.isHard() && violation) {
                            // System.out.println("\nVIOLATION: " + clause + " is not fulfilled in world " + world.getPositive());
                            weightsum = Double.NEGATIVE_INFINITY;
                            impossible = impossible.add(BigInteger.ONE);
                            break WorldCheck;
                        }

                    }
                // This check significantly cuts down the overall runtime (and this isn't even optimal yet)
                if(weightsum != Double.NEGATIVE_INFINITY) {
                    double resultValue = Math.exp(weightsum);
                    result.put((GroundingSet) world.clone(), resultValue);
                }
            }
        System.out.println("Exact inference took " + (System.currentTimeMillis() - start) + "ms");
        System.out.println(impossible + " worlds are impossible (" + impossible.multiply(BigInteger.TEN).multiply(BigInteger.TEN).divide(X.getNumberOfWorlds()) + "%)");

        calculateNormalizationConstant(result);

        return result;
    }


    private void calculateNormalizationConstant(Map<GroundingSet, Double> result)
    {
        for(GroundingSet key : result.keySet()) {
            z += result.get(key);
        }
    }


    public Double getNormalizationConstant()
    {
        return z;
    }


    public String getDistribution(Map<GroundingSet, Double> result, boolean showNegative)
    {
        normalize(result);

        StringBuilder sb = new StringBuilder();
        for(GroundingSet key : result.keySet()) {
            // result.put(key, result.get(key) / this.getNormalizationConstant());
            StringBuilder world = new StringBuilder("{ ");
            Iterator<Literal> keyItr = key.getPositive().iterator();
            while(keyItr.hasNext()) {
                Literal l = keyItr.next();
                String name = l.getName().substring(0, l.getName().indexOf('|'));
                String[] vars = l.getName().substring(l.getName().indexOf('|') + 1).split(",");
                for(int i = 0; i < vars.length; i++) {
                    vars[i] = u.getConstant(vars[i]);
                }
                world.append(name + Arrays.toString(vars).replace("[", "(").replace("]", ")") + (keyItr.hasNext() ? ", " : " "));
            }

            if(showNegative) {
                keyItr = key.getNegative().iterator();
                while(keyItr.hasNext()) {
                    Literal l = keyItr.next();
                    String name = '!' + l.getName().substring(0, l.getName().indexOf('|'));
                    String[] vars = l.getName().substring(l.getName().indexOf('|') + 1).split(",");
                    for(int i = 0; i < vars.length; i++) {
                        vars[i] = u.getConstant(vars[i]);
                    }
                    world.append(name + Arrays.toString(vars).replace("[", "(").replace("]", ")") + (keyItr.hasNext() ? ", " : " "));
                }
            }

            world.append('}');

            sb.append(String.format("%s = %f%%%n", world, result.get(key) * 100));
            // sb.append(String.format("%s = %s%%%n", world, result.get(key) * 100));
        }
        return sb.toString();
    }


    /**
     * Aggregate the results of exact inference
     * 
     * @param inferenceResult
     *            the results of exact inference
     * @return
     */
    public HashMap<String, Double> aggregate(Map<GroundingSet, Double> inferenceResult)
    {
        normalize(inferenceResult);

        HashMap<String, Double> aggregation = new HashMap<String, Double>();
        for(GroundingSet g : inferenceResult.keySet()) {
            for(Literal l : g.getPositive()) {
                String k = l.getName();
                if(aggregation.containsKey(k)) {
                    aggregation.put(k, aggregation.get(k) + inferenceResult.get(g));
                } else {
                    aggregation.put(k, inferenceResult.get(g));
                }
            }
        }
        return aggregation;
    }


    private void normalize(Map<GroundingSet, Double> inferenceResult)
    {
        if(!normalized) {
            for(Entry<GroundingSet, Double> e : inferenceResult.entrySet()) {
                inferenceResult.put(e.getKey(), e.getValue() / z);
            }
            normalized = true;
        }
    }


    /**
     * Warning: Calling this method has side effects. Do not call and proceed with processing.
     */
    @SuppressWarnings ("unused")
    private void debug()
    {
        System.out.println("========================================");
        System.out.println("Clauses:");
        System.out.println(G);
        System.out.println("========================================");
        System.out.println("Grounding Set:");
        for(GroundingSet x : X) {
            System.out.println(x);
        }
        System.out.println();
    }

}
