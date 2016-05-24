package com.googlecode.rockit.exception;

public class DatabaseException extends SolveException
{
    /**
     * Exceptions which can occur in the Parse Phase of the application.
     */
    private static final long serialVersionUID = -439022234234101364L;


    public DatabaseException()
    {
    }


    public DatabaseException(String msg)
    {
        super(msg);
    }
}
