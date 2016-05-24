package com.googlecode.rockit.app.solver;

import java.sql.SQLException;
import java.util.ArrayList;

import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.file.LogFileWriter;


public abstract class AbstractSolver
{
    // log file
    private LogFileWriter logfileWriter = null;


    public void setLogFileWriter(LogFileWriter logFileWriter)
    {
        this.logfileWriter = logFileWriter;
    }


    protected void writeToLog(String text)
    {
        if(this.logfileWriter != null) {
            this.logfileWriter.writeln(text);
        }
    }


    public void closeLogFile()
    {
        if(this.logfileWriter != null) {
            this.logfileWriter.closeLogFile();
        }
    }


    public abstract ArrayList<String> solve() throws SolveException, SQLException, ParseException, ReadOrWriteToFileException;
}
