package com.googlecode.rockit.javaAPI.formulas.variables.impl;

public abstract class VariableAbstract implements Comparable<VariableAbstract>
{
    private String  name;
    private boolean addToWhere = true;


    public VariableAbstract()
    {
    }


    public VariableAbstract(String name)
    {
        this.name = name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getName()
    {
        return name;
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
        VariableAbstract other = (VariableAbstract) obj;
        if(name == null) {
            if(other.name != null)
                return false;
        } else if(!name.equals(other.name))
            return false;
        return true;
    }


    public boolean equals(VariableAbstract var)
    {
        if(this == var)
            return true;
        if(var == null)
            return false;
        if(name == null) {
            if(var.name != null)
                return false;
        } else if(!name.equals(var.name))
            return false;
        return true;
    }


    public String toString()
    {
        return name;
    }


    @Override
    public int compareTo(VariableAbstract arg0)
    {
        return this.name.compareTo(arg0.getName());
    }


    public boolean addToWhere()
    {
        return addToWhere;
    }


    public void setAddToWhere(boolean addToWhere)
    {
        this.addToWhere = addToWhere;
    }

}
