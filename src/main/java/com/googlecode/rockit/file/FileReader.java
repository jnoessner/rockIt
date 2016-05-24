package com.googlecode.rockit.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.googlecode.rockit.exception.ReadOrWriteToFileException;


public class FileReader
{

    private BufferedReader reader;


    public FileReader(String filename) throws ReadOrWriteToFileException
    {

        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
            throw new ReadOrWriteToFileException("Can not access (open) file. Something went wrong while reading the file " + filename);
        }

    }


    public String nextLine() throws ReadOrWriteToFileException
    {
        try {
            return reader.readLine();
        } catch(IOException e) {
            e.printStackTrace();
            throw new ReadOrWriteToFileException("Can not read file. Something went wrong while reading the file.");
        }
    }


    public void close() throws ReadOrWriteToFileException
    {
        try {
            reader.close();
        } catch(IOException e) {
            e.printStackTrace();
            throw new ReadOrWriteToFileException("Can not close file. Something went wrong while reading the file.");
        }
    }

}
