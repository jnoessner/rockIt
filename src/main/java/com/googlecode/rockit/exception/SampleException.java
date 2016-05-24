package com.googlecode.rockit.exception;

public class SampleException extends Exception
{

    /**
     * Exceptions which can occur in the Parse Phase of the application.
     */
    private static final long serialVersionUID = -439022132101364L;


    public SampleException()
    {
    }


    public SampleException(String msg)
    {
        super(msg);
    }

}