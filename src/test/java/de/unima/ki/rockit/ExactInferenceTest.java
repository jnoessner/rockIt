package de.unima.ki.rockit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.googlecode.rockit.app.sampler.gibbs.GIBBSLiteral;
import com.googlecode.rockit.app.sampler.gibbs.GIBBSSampler;
import com.googlecode.rockit.app.solver.StandardSolver;
import com.googlecode.rockit.app.solver.exact.ExactSolver;
import com.googlecode.rockit.app.solver.exact.GroundingSet;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.parser.SyntaxReader;


/**
 * 
 * @author Bernd
 * 
 */
@RunWith (Parameterized.class)
public class ExactInferenceTest
{
    // Common
    private HerbrandUniverse    u          = HerbrandUniverse.getInstance();
    private String              input;
    private String              groundings;
    private static SyntaxReader reader;
    private Model               model;
    /**
     * This parameter controls by how much the probabilities between exact inference and sampling may diverge before we consider them different.
     */
    private static final double delta      = 0.005;

    // Exact only
    private ExactSolver         exactSolver;

    // Sampling only
    /**
     * The number of iterations for sampling.
     */
    private int                 iterations = 5000000;
    private GIBBSSampler        gibbsSampler;


    @Parameters (name = "{index}: {0}, {1}")
    public static Collection<Object[]> config()
    {
        List<Object[]> result = new ArrayList<Object[]>();
        File testCasesDir = new File("src/test/resources/exact/");
        File[] cases = testCasesDir.listFiles();
        for(File c : cases) {
            File[] caseFiles = c.listFiles();
            String input = null;
            String groundings = null;
            for(File cf : caseFiles) {
                if(cf.getName().endsWith("mln")) {
                    input = cf.getPath();
                } else if(cf.getName().endsWith("db")) {
                    groundings = cf.getPath();
                }
            }

            if(input != null && groundings != null) {
                result.add(new Object[] { input, groundings });
            }
        }
        // String input1 = "data/test_cases/minimal/rockit_model.mln";
        // String groundings1 = "data/test_cases/minimal/rockit_evidence.db";
        //
        // String input2 = "data/test_cases/samplable_hard/rockit_model.mln";
        // String groundings2 = "data/test_cases/samplable_hard/rockit_evidence.db";

        // return Arrays.asList(new Object[][] { { input1, groundings1 }, { input2, groundings2 } });
        return result;
    }


    public ExactInferenceTest(String input, String groundings)
    {
        this.input = input;
        this.groundings = groundings;
    }


    @Test
    public void testSolve() throws ParseException, IOException, SolveException, SQLException
    {
        exactSolver = new ExactSolver(model);
        Map<GroundingSet, Double> exactInferenceResult = exactSolver.solve();
        HashMap<String, Double> exact_aggregatedResult = exactSolver.aggregate(exactInferenceResult);
        System.out.println("Input: " + this.input);
        // System.out.println(exactSolver.getDistribution(exactInferenceResult, true));

        // Sampling
        // Model model = reader.getModel(this.input.toString(), this.groundings.toString());
        StandardSolver solver = new StandardSolver(model);
        gibbsSampler = new GIBBSSampler();
        // ground MLN and retrieve Clauses
        ArrayList<String> consistentStartingPoints = solver.solve();
        ArrayList<Clause> clauses = solver.getAllClauses();
        Collection<Literal> evidence = solver.getEvidenceAxioms();
        solver = null; // free memory
        ArrayList<GIBBSLiteral> gibbsOutput = gibbsSampler.sample(iterations, clauses, evidence, consistentStartingPoints);

        HashMap<String, Double> gibbs_result = new HashMap<String, Double>();
        for(GIBBSLiteral l : gibbsOutput) {
            gibbs_result.put(l.getName(), l.return_my_probability(iterations));
        }

        for(Entry<String, Double> e : exact_aggregatedResult.entrySet()) {
            Double exactValue = e.getValue();
            Double gibbsValue = gibbs_result.get(e.getKey());

            assertNotNull("Could not find a value for " + e.getKey() + " in the result of the Gibbs sampler", gibbsValue);

            // This assumes that the Gibbs sampling is correct, but we do not know whether the exact inference is. Assumption does not affect the result.
            assertEquals(u.transformKeysToConstants(e.getKey()) + ": [Gibbs " + gibbsValue + " ] vs. [" + exactValue + " Exact]\t", gibbsValue, exactValue, delta);
        }
    }


    /**
     * Neither CPA nor CPA may be active for (Gibbs) sampling and exact inference.
     * 
     * @throws ReadOrWriteToFileException
     */
    @BeforeClass
    public static void setUpClass() throws ReadOrWriteToFileException
    {
        com.googlecode.rockit.app.Parameters.readPropertyFile();
        com.googlecode.rockit.app.Parameters.USE_CUTTING_PLANE_AGGREGATION = false;
        com.googlecode.rockit.app.Parameters.USE_CUTTING_PLANE_INFERENCE = false;
        reader = new SyntaxReader();
    }


    @Before
    public void setUp() throws Exception
    {
        model = reader.getModel(input, groundings);
    }


    @After
    public void tearDown() throws Exception
    {
    }

}
