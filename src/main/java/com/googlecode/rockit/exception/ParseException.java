package com.googlecode.rockit.exception;

public class ParseException extends Exception
{

    /**
     * Exceptions which can occur in the Parse Phase of the application.
     */
    private static final long serialVersionUID = -4390051337485101364L;


    public ParseException()
    {
    }


    public ParseException(String msg)
    {
        super(Messages.printParseExceptionError(msg));
    }

}
