package com.googlecode.rockit.exception;

public class EvaluationException extends Exception
{

    /**
     * Exceptions which can occur in the Parse Phase of the application.
     */
    private static final long serialVersionUID = -4390221337485101364L;


    public EvaluationException()
    {
    }


    public EvaluationException(String msg)
    {
        super(msg);
    }

}