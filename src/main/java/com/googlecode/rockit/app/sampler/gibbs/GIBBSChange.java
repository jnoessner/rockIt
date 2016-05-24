package com.googlecode.rockit.app.sampler.gibbs;

/**
 * Documents in which iteration the literal has been changed to which value.
 * 
 * Always applied in combination with GIBBSLiteral.
 * 
 * @author jan
 *
 */
public class GIBBSChange
{

    private int     iteration;

    private boolean value;


    public GIBBSChange(int iteration, boolean value)
    {
        super();
        this.iteration = iteration;
        this.value = value;
    }


    public int getIteration()
    {
        return iteration;
    }


    public boolean isTrue()
    {
        return value;
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GIBBSChange [iteration=");
        builder.append(iteration);
        builder.append(", value=");
        builder.append(value);
        builder.append("]");
        return builder.toString();
    }

}
