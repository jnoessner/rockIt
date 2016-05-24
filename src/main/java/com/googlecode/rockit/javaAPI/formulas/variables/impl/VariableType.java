package com.googlecode.rockit.javaAPI.formulas.variables.impl;

import com.googlecode.rockit.javaAPI.types.Type;


public class VariableType extends VariableAbstract
{
    private Type type;


    public VariableType()
    {
    }


    public VariableType(String name, Type type)
    {
        this.setName(name);
        this.type = type;
    }


    public VariableType(String name)
    {
        this.setName(name);
    }


    public void setType(Type type)
    {
        this.type = type;

    }


    public Type getType()
    {
        return type;
    }


    public String toString()
    {
        return getName();
    }

}
