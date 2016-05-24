package com.googlecode.rockit.javaAPI.types;

import java.util.ArrayList;

import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.predicates.Predicate;


/**
 * Type for config file.
 * 
 * @author jan
 *
 */
public class Type implements Comparable<Type>
{
    private String    name;

    private Predicate groundValuesPredicate = null;


    // private int groundValueSize = 0;

    public Type()
    {
    }


    public Type(String name)
    {
        this.setName(name);
    }


    public Type(String name, Predicate groundValuesPredicate) throws ParseException
    {
        this.setName(name);
        this.setGroundValuesPredicate(groundValuesPredicate);
    }


    /**
     * Sets the ground values of the current type to those of the given predicate.
     * 
     * If the predicate has more than one "row" in ground values (e.g. more than one Type assignment),
     * an exception is thrown.
     * 
     * @param predicate
     * @throws ParseException
     */
    public void setGroundValuesPredicate(Predicate predicate) throws ParseException
    {
        if(predicate == null) { throw new ParseException("Predicates must not equal null when added to type. You tried to add a null predicate to type " + this.getName()); }
        if(predicate.getTypes().size() > 1) { throw new ParseException("Predicates which are used to set ground values must not have more than one type. This happens when trying to assign predicate " + predicate.getName() + " to type " + this.getName()); }
        this.groundValuesPredicate = predicate;
    }


    public ArrayList<String[]> getGroundValues()
    {
        return groundValuesPredicate.getGroundValues();
    }


    public Predicate getGroundValuesPredicate()
    {
        return groundValuesPredicate;
    }


    @Override
    public boolean equals(Object obj)
    {
        if(obj.getClass() == Type.class) {
            Type t = (Type) obj;
            return t.getName().equals(this.getName());
        } else {
            return false;
        }
    }


    public String getId()
    {

        return this.getClass().getName();
    }


    public String toLongString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("> type ").append(this.getName()).append("\n");
        if(this.groundValuesPredicate != null) {
            for(String[] s : this.groundValuesPredicate.getGroundValues()) {
                sb.append("\"" + s[0] + "\"\n");
            }
        }
        return sb.toString();
    }


    public String output()
    {
        String result = "Type " + this.getName() + "T = new Type(\"" + this.getName() + "\");\n";
        if(this.groundValuesPredicate != null) {
            result = result + this.getName() + ".values = " + this.getGroundValuesPredicate().getName();
        }
        return result;

    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getName()
    {
        return name;
    }


    public String toString()
    {
        return name;
    }


    public String toMediumString()
    {
        return "type " + name + ";";
    }


    @Override
    public int compareTo(Type arg0)
    {
        return this.name.compareTo(arg0.getName());
    }

    /*
     * public int getGroundValueSize() throws ParseException {
     * if(this.groundValuesPredicate==null){
     * throw new ParseException("Can not determine the size if no ground values predicate is set. Type name: " + this.name);
     * }
     * if(groundValueSize==0){
     * for(String[] p:groundValuesPredicate.getGroundValues()){
     * groundValueSize = Math.max(groundValueSize, p[0].length());
     * }
     * }
     * return groundValueSize;
     * }
     */
}
