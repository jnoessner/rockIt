package com.googlecode.rockit.conn.ilp;

public class ILPVariable
{
    private String  name;
    private double  value;
    private boolean isZVar = false;


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public ILPVariable(String name, double value, boolean isZVar)
    {
        super();
        this.name = name;
        this.value = value;
        this.isZVar = isZVar;
    }


    public double getValue()
    {
        return value;
    }


    public void setValue(double value)
    {
        this.value = value;
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
        ILPVariable other = (ILPVariable) obj;
        if(name == null) {
            if(other.name != null)
                return false;
        } else if(!name.equals(other.name))
            return false;
        return true;
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        if(value >= 0) {
            builder.append("+");
        }
        builder.append(value).append(" ");
        builder.append(name);
        return builder.toString();
    }


    public boolean isZVar()
    {
        return isZVar;
    }


    public void setZVar(boolean isZVar)
    {
        this.isZVar = isZVar;
    }

}
