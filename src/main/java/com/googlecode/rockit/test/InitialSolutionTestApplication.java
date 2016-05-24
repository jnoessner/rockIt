package com.googlecode.rockit.test;

import java.io.IOException;
import java.sql.SQLException;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Main;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;


public class InitialSolutionTestApplication
{

    /**
     * @param args
     * @throws ReadOrWriteToFileException
     * @throws SQLException
     * @throws SolveException
     * @throws RecognitionException
     * @throws ParseException
     * @throws IOException
     */
    public static void main(String[] args) throws IOException, ParseException, RecognitionException, SolveException, SQLException, ReadOrWriteToFileException
    {
        String folder = "data/initialSolution/";
        long time1, time2;
        time1 = System.currentTimeMillis();
        Main.main(new String[] { "-data", folder + "evidence.db", "-input", folder + "prog.mln", "-output", "output.db", "-para", "rockit.properties" });
        time1 = System.currentTimeMillis() - time1;
        time2 = System.currentTimeMillis();
        Main.main(new String[] { "-data", folder + "evidence.db", "-input", folder + "prog.mln", "-output", "output.db", "-para", "rockit.properties", "-isolution", folder + "initialSolution.db" });
        time2 = System.currentTimeMillis() - time2;
        System.out.println(time1 + "\t(standard)");
        System.out.println(time2 + "\t(initial solution)");
        System.out.println(time1 - time2 + "\t(difference)");
    }

}
