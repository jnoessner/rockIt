package com.googlecode.rockit.app;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.googlecode.rockit.app.result.RockItResult;
import com.googlecode.rockit.app.result.RockItResultProbabilityComparer;
import com.googlecode.rockit.app.sampler.gibbs.GIBBSLiteral;
import com.googlecode.rockit.app.solver.numerical.MathExpression;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.parser.SyntaxReader;


public class RockItAPI
{
    private static Main main = new Main();


    public RockItAPI(String propertyFile) throws ReadOrWriteToFileException
    {
        Parameters.PROPERTYFILE = propertyFile;
        Parameters.readPropertyFile();
    }


    public RockItAPI()
    {
        try {
            Parameters.readPropertyFile();
        } catch(ReadOrWriteToFileException e) {
            e.printStackTrace();
        }
    }


    public List<RockItResult> doMapState(String input, String groundings) throws ILPException, ParseException, IOException, SolveException, SQLException, ReadOrWriteToFileException
    {
        MathExpression.reset();

        ArrayList<RockItResult> result = new ArrayList<RockItResult>();
        ArrayList<String> temp = main.doMapStateInternal(new SyntaxReader(), input, groundings);

        HerbrandUniverse u = HerbrandUniverse.getInstance();
        for(String t : temp) {
            result.add(new RockItResult(u.transformKeysToConstants(t)));
        }
        u.reset();

        return result;
    }


    public List<RockItResult> doGibbsSamplingInternal(String input, String groundings, int iterations) throws ParseException, IOException, SolveException, SQLException
    {
        ArrayList<RockItResult> result = new ArrayList<RockItResult>();
        ArrayList<GIBBSLiteral> out = main.doGibbsSamplingInternal(new SyntaxReader(), input, groundings, iterations);

        if(iterations <= 0) {
            iterations = Math.min(out.size() * 1000, 100000000);
        }

        HashMap<String, Double> transform = new HashMap<>();
        for(GIBBSLiteral l : out) {
            transform.put(l.getName(), l.return_my_probability(iterations));
        }

        HerbrandUniverse u = HerbrandUniverse.getInstance();
        for(Entry<String, Double> res : transform.entrySet()) {
            result.add(new RockItResult(res.getValue(), u.transformKeysToConstants(res.getKey())));
        }
        return result;
    }


    public List<RockItResult> doExactInference(String input, String groundings) throws ParseException, IOException, SolveException, SQLException
    {
        ArrayList<RockItResult> result = new ArrayList<RockItResult>();
        HashMap<String, Double> temp = main.doExactInferenceInternal(new SyntaxReader(), input, groundings);

        HerbrandUniverse u = HerbrandUniverse.getInstance();
        for(Entry<String, Double> res : temp.entrySet()) {
            result.add(new RockItResult(res.getValue(), u.transformKeysToConstants(res.getKey())));
        }

        return result;
    }


    // * doExactInference(SyntaxReader)

    public static void main(String[] args) throws ILPException, ParseException, IOException, SolveException, SQLException, ReadOrWriteToFileException
    {
        RockItAPI api = new RockItAPI();
        List<RockItResult> res = api.doMapState("data/friends/prog.mln", "data/friends/evidence.db");
        // List<RockItResult> res = api.doGibbsSamplingInternal("data/friends/prog.mln", "data/friends/evidence.db", 100000);
        // List<RockItResult> res = api.doExactInference("data/friends/prog.mln", "data/friends/evidence.db");

        Collections.sort(res, new RockItResultProbabilityComparer());

        for(RockItResult s : res) {
            System.out.println(s.toString());
            System.out.println("\t-" + s.getPredicate() + "- (" + s.getProbability() + ")");
            for(String o : s.getObjects()) {
                System.out.println("\t\t-" + o + "-");
            }
        }
    }

}
