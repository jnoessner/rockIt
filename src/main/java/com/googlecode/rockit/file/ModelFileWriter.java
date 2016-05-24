package com.googlecode.rockit.file;

import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.javaAPI.Model;


public class ModelFileWriter
{

    public void writeModel(Model model, String fileName) throws ReadOrWriteToFileException
    {
        MyFileWriter writer = new MyFileWriter(fileName);
        writer.write(model.toString());
        writer.closeFile();
    }

}
