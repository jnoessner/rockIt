package com.googlecode.rockit.exception;

public class ILPException extends SolveException
{
    /**
     * When the ilp is infeasible, this exception occurs.
     */
    private static final long serialVersionUID = -4390221337485101364L;


    public ILPException()
    {
    }


    public ILPException(String msg)
    {
        super(msg);
    }
}
