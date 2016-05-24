package com.googlecode.rockit.test;

import java.io.IOException;
import java.sql.SQLException;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Main;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;


public class MiniTestApplication
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
        Main.main(new String[] { "-data", "data/friends/evidence.db", "-input", "data/friends/prog.mln", "-output", "output.db", "-para", "rockit.properties" });

    }

}
