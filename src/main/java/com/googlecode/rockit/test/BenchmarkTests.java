package com.googlecode.rockit.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.file.MyFileWriter;


public class BenchmarkTests
{

    public static void main(String[] args) throws ReadOrWriteToFileException, ParseException, IOException, RecognitionException, SolveException, SQLException
    {
        /*
         * try {
         * Thread.sleep(10000);
         * } catch (InterruptedException e1) {
         * // TODO Auto-generated catch block
         * e1.printStackTrace();
         * }
         */

        Parameters.USE_CUTTING_PLANE_INFERENCE = false;
        Parameters.USE_CUTTING_PLANE_AGGREGATION = false;
        // Has to be disabled for runtime comparisons!!!
        Parameters.DEBUG_OUTPUT = false;
        int cores = Runtime.getRuntime().availableProcessors();
        // Parameters.THREAD_NUMBER=cores+cores;
        Parameters.THREAD_NUMBER = cores;
        System.out.println("Parameters.THREAD_NUMBER " + Parameters.THREAD_NUMBER);

        ArrayList<String> results = new ArrayList<String>();
        MyFileWriter writer = new MyFileWriter("TUFFYatVLDB2011Results.txt");
        String s = null;

        long runtime = 0;

        // protein IO
        String modelFile = "data/pr/prog.mln";
        String evidenceFile = "data/pr/evidence.db";
        @SuppressWarnings ("unused")
        String queryFile = "data/pr/query.db";

        try {
            s = StandardSolverTest.test(modelFile, evidenceFile, 0.0000001, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
            results.add(s);
            writer.writeln(s);
            writer.flush();
            runtime = StandardSolverTest.runtime + runtime;
        } catch(Exception e) {
            s = modelFile + ";" + evidenceFile + ";Exception" + e.getMessage();// TODO Auto-generated catch block
            results.add(s);
            writer.writeln(s);
            writer.flush();
        }

        // lp IO

        modelFile = "data/lp/prog.mln";
        evidenceFile = "data/lp/evidence.db";
        queryFile = "data/lp/query.db";

        try {
            s = StandardSolverTest.test(modelFile, evidenceFile, 0.01, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
            results.add(s);
            writer.writeln(s);
            writer.flush();
            runtime = StandardSolverTest.runtime + runtime;
        } catch(Exception e) {
            s = modelFile + ";" + evidenceFile + ";Exception" + e.getMessage();// TODO Auto-generated catch block
            results.add(s);
            writer.writeln(s);
            writer.flush();
        }

        // rc IO
        modelFile = "data/rc/prog.mln";
        evidenceFile = "data/rc/evidence.db";
        queryFile = "data/rc/query.db";

        try {
            s = StandardSolverTest.test(modelFile, evidenceFile, 0.000001, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
            results.add(s);
            writer.writeln(s);
            writer.flush();
            runtime = StandardSolverTest.runtime + runtime;
        } catch(Exception e) {
            s = modelFile + ";" + evidenceFile + ";Exception" + e.getMessage();// TODO Auto-generated catch block
            results.add(s);
            writer.writeln(s);
            writer.flush();
        }

        // ie IO
        modelFile = "data/ie/prog.mln";
        evidenceFile = "data/ie/evidence.db";
        queryFile = "data/ie/query.db";

        try {
            s = StandardSolverTest.test(modelFile, evidenceFile, 0.000001, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
            results.add(s);
            writer.writeln(s);
            writer.flush();
            runtime = StandardSolverTest.runtime + runtime;
        } catch(Exception e) {
            s = modelFile + ";" + evidenceFile + ";Exception" + e.getMessage();// TODO Auto-generated catch block
            results.add(s);
            writer.writeln(s);
            writer.flush();
        }

        // er IO
        modelFile = "data/er/prog.mln";
        evidenceFile = "data/er/evidence.db";
        queryFile = "data/er/query.db";

        try {
            s = StandardSolverTest.test(modelFile, evidenceFile, 0.000001, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
            results.add(s);
            writer.writeln(s);
            writer.flush();
            runtime = StandardSolverTest.runtime + runtime;
        } catch(Exception e) {
            s = modelFile + ";" + evidenceFile + ";Exception" + e.getMessage();// TODO Auto-generated catch block
            results.add(s);
            writer.writeln(s);
            writer.flush();
        }
        // conference
        // CONFERENCE
        /*
         * String[] onts = new String[]{
         * "cmt", "confOf", "iasted"
         * };
         * for(int i = 0; i<onts.length; i++){
         * for(int j = i+1; j<onts.length;j++){
         * //String modelFile = "data/CODIatOAEI2011/conference/"+onts[i]+"-"+onts[j]+"_prog.mln";
         * //String evidenceFile = "data/CODIatOAEI2011/conference/"+onts[i]+"-"+onts[j]+"_evidence.db";
         * modelFile = "data/CODIatOAEI2011/conference/"+onts[i]+"-"+onts[j]+"_prog.mln";
         * evidenceFile = "data/CODIatOAEI2011/conference/"+onts[i]+"-"+onts[j]+"_evidence.db";
         * queryFile = "data/CODIatOAEI2011/query.db";
         * try {
         * s = StandardSolverTest.test(modelFile, evidenceFile, -1, true, true);
         * results.add(s);writer.writeln(s);writer.flush();
         * } catch (Exception e) {
         * s = modelFile+";"+evidenceFile+";Exception;"+e.getMessage();// TODO Auto-generated catch block
         * results.add(s);writer.writeln(s);writer.flush();
         * }catch (Error e){
         * s = modelFile+";"+evidenceFile+";Error;"+e.getMessage();// TODO Auto-generated catch block
         * results.add(s);writer.writeln(s);writer.flush();
         * }
         * }
         * }
         */

        results.add("duration:" + runtime);
        writer.writeln("duration:" + runtime);

        writer.closeFile();
        // print results
        System.out.println("==========================");
        System.out.println();
        for(String r : results) {
            System.out.println(r);
        }
    }
}
