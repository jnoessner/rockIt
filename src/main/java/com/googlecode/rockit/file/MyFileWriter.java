package com.googlecode.rockit.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import com.googlecode.rockit.exception.ReadOrWriteToFileException;


public class MyFileWriter
{
    private PrintWriter out;
    private String      filename = null;


    public MyFileWriter(String filename) throws ReadOrWriteToFileException
    {
        try {
            FileWriter outFile = new FileWriter(filename);
            this.filename = filename;
            this.out = new PrintWriter(outFile);
        } catch(IOException e) {
            throw new ReadOrWriteToFileException("Can not create the file with filename: " + filename);
        }

    }


    public void flush()
    {
        this.out.flush();
    }


    public void writeln(String line)
    {
        this.out.println(line);
    }


    public void write(String s)
    {
        this.out.print(s);
        this.out.flush();
    }


    public void writeSet(Set<?> set, String delimeter)
    {
        int size = set.size();
        int i = 0;
        for(Object o : set) {
            this.write(o.toString());
            i++;
            if(i < size)
                this.write(delimeter);
        }
        this.writeln("");
        out.flush();
    }


    public void deleteFile()
    {
        File f = new File(this.filename);
        f.delete();

    }


    public void closeFile()
    {
        out.flush();
        out.close();

    }
}
