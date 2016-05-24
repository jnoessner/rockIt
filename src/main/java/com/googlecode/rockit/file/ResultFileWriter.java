package com.googlecode.rockit.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.googlecode.rockit.app.sampler.gibbs.GIBBSLiteral;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;


public class ResultFileWriter extends MyFileWriter
{

    private static HerbrandUniverse u = HerbrandUniverse.getInstance();


    public ResultFileWriter(String filename) throws ReadOrWriteToFileException
    {
        super(filename);
    }


    public void printResultFile(HashMap<String, Double> results)
    {
        for(Entry<String, Double> result : results.entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append(result.getValue()).append("   ").append(u.transformKeysToConstants(result.getKey()));
            this.writeln(sb.toString());
        }
        this.closeFile();
    }


    public void printResultFile(ArrayList<String> results)
    {
        for(String result : results) {
            this.writeln(u.transformKeysToConstants(result));
        }

        this.closeFile();
    }


    public void printResultFile(ArrayList<GIBBSLiteral> results, int numberSamplingRounds)
    {
        HashMap<String, Double> transform = new HashMap<String, Double>();
        for(GIBBSLiteral l : results) {
            transform.put(l.getName(), l.return_my_probability(numberSamplingRounds));
        }
        this.printResultFile(transform);
    }

}
