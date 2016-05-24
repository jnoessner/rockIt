package com.googlecode.rockit.exception;

public class SolveException extends Exception
{

    /**
     * Exceptions which can occur in the Parse Phase of the application.
     */
    private static final long serialVersionUID = -4390221337485101364L;


    public SolveException()
    {
    }


    public SolveException(String msg)
    {
        super(msg);
    }

}