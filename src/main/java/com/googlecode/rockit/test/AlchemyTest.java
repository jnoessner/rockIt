package com.googlecode.rockit.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.evaluator.ObjectiveEvaluator;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;


public class AlchemyTest
{

    public static String test(String modelFileRockit, String modelFileAlchemy, String evidenceFile, String queryFile, boolean increaseFlip) throws ParseException, IOException, RecognitionException, SolveException, SQLException, ReadOrWriteToFileException
    {
        return test(modelFileRockit, modelFileAlchemy, evidenceFile, queryFile, increaseFlip, -1);

    }


    public static String test(String modelFileRockit, String modelFileAlchemy, String evidenceFile, String queryFile, boolean increaseFlip, int maxFlips) throws ParseException, IOException, RecognitionException, SolveException, SQLException, ReadOrWriteToFileException
    {
        StringBuilder result = new StringBuilder();
        result.append(modelFileAlchemy).append(";").append(evidenceFile).append(";");
        result.append("increaseFlip").append(increaseFlip).append(";");

        String command = "./infer -m -i " + modelFileAlchemy + " -e " + evidenceFile + " -f " + queryFile + " -r " + "output.db ";
        if(maxFlips > 0) {
            command = command + "  -mwsMaxSteps " + maxFlips + " -maxSteps " + maxFlips;
        }

        // run alchemy:
        long start = System.currentTimeMillis();
        // run the Unix "ps -ef" command
        // using the Runtime exec method:
        Process p = Runtime.getRuntime().exec(command);

        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        String s;
        while((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        Long runtimeTuffy = System.currentTimeMillis() - start;

        System.out.println("=TUFFY EVAL==========================================================");
        ObjectiveEvaluator evalTuffy = new ObjectiveEvaluator();
        Double objectiveTuffy = evalTuffy.evaluate(modelFileRockit, evidenceFile, "output.db");

        result.append(runtimeTuffy).append(";objSoft;").append(objectiveTuffy).append(";objHard;").append(evalTuffy.getHardObjective());
        return result.toString();

    }

}
