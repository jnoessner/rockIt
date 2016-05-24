package com.googlecode.rockit.file;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.googlecode.rockit.exception.ReadOrWriteToFileException;


public class LogFileWriter
{

    private PrintWriter          out;

    private static LogFileWriter ref;


    private LogFileWriter(String filename) throws ReadOrWriteToFileException
    {
        try {
            FileWriter outFile = new FileWriter(filename);
            this.out = new PrintWriter(outFile);
        } catch(IOException e) {
            throw new ReadOrWriteToFileException("Could not create new logfile with filename " + filename + " " + e.getMessage());
        }
    }


    public static LogFileWriter openNewLogFile(String fileName) throws ReadOrWriteToFileException
    {
        // if (ref == null)
        // it's ok, we can call this constructor
        ref = new LogFileWriter(fileName);
        return ref;
    }


    public static LogFileWriter connectToLogFile() throws ReadOrWriteToFileException
    {
        return ref;
    }


    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }


    public void writeln(String line)
    {
        this.out.println(line);
        this.out.flush();
    }


    public void write(String s)
    {
        this.out.print(s);
        this.out.flush();
    }


    public void closeLogFile()
    {
        out.flush();
        out.close();

    }
}
