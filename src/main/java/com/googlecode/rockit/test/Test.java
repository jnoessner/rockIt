package com.googlecode.rockit.test;

import java.io.IOException;
import java.sql.SQLException;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;


public class Test
{

    /**
     * @param args
     * @throws ReadOrWriteToFileException
     * @throws SQLException
     * @throws SolveException
     * @throws RecognitionException
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws ParseException, IOException, RecognitionException, SolveException, SQLException, ReadOrWriteToFileException
    {

        double d = Math.pow(10, 99);
        System.out.println(d);

        Parameters.DEBUG_OUTPUT = true;

        String modelFile = "data/others/process/prog.mln";
        String evidenceFile = "data/others/process/evidence.db";

        StandardSolverTest.test(modelFile, evidenceFile, 0.0000001, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);

    }

}
