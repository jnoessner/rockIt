package com.googlecode.rockit.app.solver.pojo;

public class Literal
{
    private boolean positive;
    private String  name;
    // private GRBVar var;
    private int     id = -1;


    public Literal()
    {
    }


    public Literal(String name, boolean positive)
    {
        this.name = name;
        this.positive = positive;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public boolean isPositive()
    {
        return positive;
    }


    public void setPositive(boolean positive)
    {
        this.positive = positive;
    }


    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        Literal other = (Literal) obj;
        if(name == null) {
            if(other.name != null)
                return false;
        } else if(!name.equals(other.name))
            return false;
        return true;
    }


    // public GRBVar getVar() {
    // return var;
    // }

    // public void setVar(GRBVar var) {
    // this.var = var;
    // }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        if(!positive)
            builder.append("! ");
        builder.append(name);
        builder.append("]");
        return builder.toString();
    }


    public int getId()
    {
        return id;
    }


    public void setId(int id)
    {
        this.id = id;
    }

}
