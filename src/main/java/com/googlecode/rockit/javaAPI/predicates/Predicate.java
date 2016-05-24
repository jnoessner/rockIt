package com.googlecode.rockit.javaAPI.predicates;

import java.util.ArrayList;

import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.types.Type;


public class Predicate extends PredicateAbstract
{

    public Predicate()
    {
    }


    public Predicate(String name, boolean hidden, Type... types) throws ParseException
    {
        this.setName(name);
        this.setHidden(hidden);
        for(int i = 0; i < types.length; i++) {
            this.addType(types[i]);
        }
    }


    public Predicate(String name, boolean hidden) throws ParseException
    {
        this.setName(name);
        this.setHidden(hidden);
    }


    /**
     * Add one line to the ground atoms.
     * 
     * Note that the size of the line attribut has to be equal to the
     * types attribute.
     * 
     * @param line
     * @throws ParseException
     */
    public void addGroundValueLine(String... values) throws ParseException
    {
        if(values.length != this.getTypes().size()) { throw new ParseException("Predicate: " + this.getName() + " - The size of the line input has to be equal to the size of the types attribute. Add types first."); }
        this.getGroundValues().add(values);

    }


    /**
     * Add one line to the ground atoms.
     * 
     * Note that the size of the line attribut has to be equal to the
     * types attribute.
     * 
     * @param line
     * @throws ParseException
     */
    public void addGroundValueLine(ArrayList<String> line) throws ParseException
    {
        if(this.getTypes().size() > 0 && line.size() != this.getTypes().size()) { throw new ParseException("Predicate: " + this.getName() + " - The size of the line input has to be equal to the size of the types attribute. "); }
        String[] e = new String[line.size()];
        for(int i = 0; i < line.size(); i++) {
            e[i] = line.get(i);
        }
        this.getGroundValues().add(e);
    }


    public Predicate(String name, boolean hidden, ArrayList<Type> types, ArrayList<String[]> groundValues) throws ParseException
    {
        this.setName(name);
        this.setHidden(hidden);
        if(groundValues == null) { throw new ParseException("Predicate: " + this.getName() + " - Ground value axiom must not be zero."); }
        if(types == null) { throw new ParseException("Predicate: " + this.getName() + " - Types axiom must not be zero."); }
        /*
         * if(types.size()>0 && groundValues.size()>0 &&groundValues.size()!=types.size()){
         * throw new ParseException("Predicate: " + this.getName() + " - The size of the line input has to be equal to the size of the types attribute.");
         * }
         */
        this.setGroundValues(groundValues);

        this.setTypes(types);

    }


    @Override
    public String getId()
    {
        return this.getClass().getName();
    }


    public String toLongString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append("// ").append(this.getName()).append("\n");
        // print axioms
        if(this.getGroundValues().size() > 0) {
            for(int i = 0; i < this.getGroundValues().size(); i++) {
                sb.append(this.getName()).append("(");
                String[] groundValue = this.getGroundValues().get(i);
                for(int j = 0; j < groundValue.length; j++) {
                    sb.append("\"").append(u.getConstant(groundValue[j])).append("\"");
                    if(j < groundValue.length - 1) {
                        sb.append(", ");
                    }
                }
                sb.append(")\n");
            }
        } else {
            sb.append("// no values");
        }
        return sb.toString();
    }


    public String output()
    {
        StringBuilder sb = new StringBuilder();
        ArrayList<Type> types = this.getTypes();

        sb.append("Predicate " + this.getName() + "P = new Predicate(\"" + this.getName() + "\"," + this.isHidden() + ",");
        for(int i = 0; i < types.size() - 1; i++) {
            sb.append(types.get(i).getName() + "T, ");
        }
        sb.append(types.get(types.size() - 1).getName() + "T");
        sb.append(");\n");

        return sb.toString();
    }
}
