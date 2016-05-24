package com.googlecode.rockit.javaAPI.formulas.variables.impl;

public class VariableDouble extends VariableAbstract
{
    public VariableDouble()
    {
    }


    public VariableDouble(String name)
    {
        this.setName(name);
    }


    public String toString()
    {
        return this.getName();
    }

}
