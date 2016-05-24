package com.googlecode.rockit.exception;

public class ILPInfeasibleException extends ILPException
{
    /**
     * When the ilp is infeasible, this exception occurs.
     */
    private static final long serialVersionUID = -4390221337485101364L;


    public ILPInfeasibleException()
    {
    }


    public ILPInfeasibleException(String msg)
    {
        super(msg);
    }
}
