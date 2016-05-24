package com.googlecode.rockit.javaAPI.predicates;

import java.util.ArrayList;

import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.javaAPI.types.Type;


public abstract class PredicateAbstract implements Comparable<PredicateAbstract>
{
    private String                    name;
    private boolean                   hidden       = false;
    private ArrayList<Type>           types        = new ArrayList<Type>();
    private ArrayList<String[]>       groundValues = new ArrayList<String[]>();
    protected static HerbrandUniverse u            = HerbrandUniverse.getInstance();


    public PredicateAbstract()
    {
    }


    public abstract String getId();


    public abstract String output();


    public PredicateAbstract(String name, boolean hidden, Type... types)
    {
        this.name = name;
        this.hidden = hidden;
        for(int i = 0; i < types.length; i++) {
            this.types.add(types[i]);
        }
    }


    public PredicateAbstract(String name, boolean hidden, ArrayList<Type> types, ArrayList<ArrayList<String>> groundValues) throws ParseException
    {
        this.name = name;
        this.hidden = hidden;
        if(groundValues == null) { throw new ParseException("Predicate: " + this.name + " - Ground value axiom must not be zero."); }
        if(types == null) { throw new ParseException("Predicate: " + this.name + " - Types axiom must not be zero."); }
        if(groundValues.size() != types.size()) { throw new ParseException("Predicate: " + this.name + " - The size of the line input has to be equal to the size of the types attribute."); }

        this.types = types;

    }


    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }


    public boolean isHidden()
    {
        return hidden;
    }


    public void setObserved(boolean observed)
    {
        this.hidden = !observed;
    }


    public boolean isObserved()
    {
        return !hidden;
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
        PredicateAbstract other = (PredicateAbstract) obj;
        if(name == null) {
            if(other.name != null)
                return false;
        } else if(!name.equals(other.name))
            return false;
        return true;
    }


    /**
     * The first arraylist represents a single column,
     * while the second arraylist represents the values for this column.
     * 
     * The size of the ArrayList has to be the same as for the Types.
     * 
     * The sizes of the inner Arraylist has to be the same for all three lists.
     * 
     * @param groundValues
     * @throws ParseException
     */
    public void setGroundValues(ArrayList<String[]> groundValues) throws ParseException
    {
        if(groundValues == null || groundValues.size() == 0)
            return;
        if((types.size() > 0) && groundValues.get(0).length != types.size()) { throw new ParseException("Predicate: " + this.name + " - The size of the line input has to be equal to the size of the types attribute."); }
        this.groundValues = groundValues;
    }


    public ArrayList<String[]> getGroundValues()
    {
        return groundValues;
    }


    public void addType(Type type) throws ParseException
    {
        if((this.groundValues.size() > 0) && (this.groundValues.get(0).length > 0) && types.size() >= this.groundValues.size()) { throw new ParseException("Predicate: " + this.name + " - The size of the types must not extend the size of the line input."); }
        this.types.add(type);
    }


    public void setTypes(ArrayList<Type> types) throws ParseException
    {
        if((this.groundValues.size() > 0) && types.size() != this.groundValues.get(0).length) { throw new ParseException("Predicate: " + this.name + " - The size of the line input has to be equal to the size of the types attribute."); }
        this.types = types;
    }


    public void setTypes(Type... types) throws ParseException
    {
        if((this.groundValues.size() > 0) && types.length != this.groundValues.size()) { throw new ParseException("Predicate: " + this.name + " - The size of the line input has to be equal to the size of the types attribute."); }
        for(int i = 0; i < types.length; i++) {
            this.types.add(types[i]);
        }
    }


    public ArrayList<Type> getTypes()
    {
        return types;
    }


    public String toString()
    {
        return name;
    }


    public String toTheBeastString()
    {
        StringBuilder sb = new StringBuilder();

        // sb.append("predicate ");
        sb.append("predicate ").append(name.toLowerCase()).append(": ");
        for(int i = 0; i < types.size(); i++) {
            sb.append(types.get(i).toString());
            if(i < types.size() - 1) {
                sb.append(" x ");
            }
        }
        sb.append(";");
        return sb.toString();
    }


    public String toMediumString()
    {
        StringBuilder sb = new StringBuilder();

        // sb.append("predicate ");
        if(!this.hidden) {
            sb.append("* ");
        }
        sb.append(name).append("(");
        for(int i = 0; i < types.size(); i++) {
            sb.append(types.get(i).toString());
            if(i < types.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }


    public abstract String toLongString();


    @Override
    public int compareTo(PredicateAbstract o)
    {
        return this.getName().compareTo(o.getName());
    }

}
