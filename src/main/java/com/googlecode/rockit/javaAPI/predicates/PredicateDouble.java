package com.googlecode.rockit.javaAPI.predicates;

import java.util.ArrayList;

import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.types.Type;


/**
 * This predicate also has the possibility to has one array of double values
 * 
 * @author jan
 *
 */
public class PredicateDouble extends PredicateAbstract
{
    private ArrayList<Double> doubleValues = new ArrayList<Double>();


    public ArrayList<Double> getDoubleValues()
    {
        return doubleValues;
    }


    public void setDoubleValues(ArrayList<Double> doubleValues)
    {
        this.doubleValues = doubleValues;
    }


    public PredicateDouble()
    {
    }


    public PredicateDouble(String name, boolean hidden)
    {
        this.setHidden(hidden);
        this.setName(name);
    }


    /**
     * In the groundvalues, the last position of the Array contains the double value (encoded as string).
     * 
     */
    public void setGroundAndDoubleValues(ArrayList<String[]> groundValues) throws ParseException
    {
        if(groundValues == null || groundValues.size() == 0)
            return;
        if((this.getTypes().size() > 0) && groundValues.get(0).length != this.getTypes().size() + 1) {
            for(String[] s : groundValues) {
                for(String ss : s) {
                    System.out.print(ss);
                    System.out.print(",");
                }
                System.out.println();
            }
            throw new ParseException("PredicateDouble: " + this.getName() + " - The size of the line input has to be equal to the size of the types attribute.");
        }
        this.doubleValues = new ArrayList<Double>();
        this.setGroundValues(new ArrayList<String[]>());
        for(String[] sArray : groundValues) {
            int lastPos = sArray.length - 1;
            double value = 0;
            try {
                value = Double.parseDouble(sArray[lastPos]);
            } catch(NumberFormatException e) {
                throw new ParseException("In PrdicateDouble " + this.getName() + " is a Double value which can not be parsed to a double number: " + sArray[lastPos]);
            }
            String[] groundV = new String[lastPos];
            for(int i = 0; i < lastPos; i++) {
                groundV[i] = sArray[i];
            }
            this.addGroundValueLine(value, groundV);
        }
    }


    public PredicateDouble(String name, boolean hidden, Type... types) throws ParseException
    {
        this.setName(name);
        this.setHidden(hidden);
        for(int i = 0; i < types.length; i++) {
            this.addType(types[i]);
        }
    }


    /**
     * Add one line to the ground atoms.
     * 
     * Note that the size of the line attribut has to be equal to the
     * types attribute.
     * 
     * @param line
     * @param value
     *            The value of the predicate. has to be between 0 and 1.
     * @throws ParseException
     */
    public void addGroundValueLine(double value, String... values) throws ParseException
    {
        /*
         * if(value <0){
         * throw new ParseException("Predicate: " + this.getName() + " - Value has to be greater than 0.");
         * }
         */
        if(values.length != getTypes().size()) { throw new ParseException("Predicate: " + this.getName() + " - The size of the line input has to be equal the size of the types attribute. Add types first." + " actual size line: " + values.length + " actual size types " + getTypes().size()); }

        this.getGroundValues().add(values);

        this.doubleValues.add(value);

    }


    public void addGroundValueLine(double value, ArrayList<String> line) throws ParseException
    {
        if(this.getTypes().size() > 0 && line.size() != this.getTypes().size()) { throw new ParseException("Predicate: " + this.getName() + " - The size of the line input has to be equal to the size of the types attribute. "); }
        String[] e = new String[line.size()];
        for(int i = 0; i < line.size(); i++) {
            e[i] = line.get(i);
        }

        this.doubleValues.add(value);

        this.getGroundValues().add(e);
    }


    public PredicateDouble(String name, boolean hidden, ArrayList<Type> types, ArrayList<String[]> groundValues) throws ParseException
    {
        this.setName(name);
        this.setHidden(hidden);
        if(groundValues == null) { throw new ParseException("Predicate: " + this.getName() + " - Ground value axiom must not be zero."); }
        if(types == null) { throw new ParseException("Predicate: " + this.getName() + " - Types axiom must not be zero."); }
        if(groundValues.size() != types.size()) { throw new ParseException("Predicate: " + this.getName() + " - The size of the line input has to be equal to the size of the types attribute."); }
        this.setGroundValues(groundValues);
        this.setTypes(types);

    }


    @Override
    public String getId()
    {
        return this.getClass().getName();
    }


    public String toTheBeastString()
    {
        String superString = super.toMediumString();
        String result = superString.substring(0, superString.length() - 1);
        result = result.replace(";", "");
        StringBuilder sb = new StringBuilder();
        sb.append(result).append(" x Double;");
        return sb.toString();
    }


    public String toMediumString()
    {
        String superString = super.toMediumString();
        String result = superString.substring(0, superString.length() - 1);
        result = result.replace(")", ",");
        StringBuilder sb = new StringBuilder();
        sb.append(result).append(", float_ )");
        return sb.toString();
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
                    sb.append("\"").append(u.getConstant(groundValue[j])).append("\"").append(", ");
                }
                sb.append(this.doubleValues.get(i));
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

        sb.append("PredicateDouble " + this.getName() + "P = new PredicateDouble(\"" + this.getName() + "\"," + this.isHidden() + ",");
        for(int i = 0; i < types.size() - 1; i++) {
            sb.append(types.get(i).getName() + "T, ");
        }
        sb.append(types.get(types.size() - 1).getName() + "T");
        sb.append(");\n");

        return sb.toString();
    }
}
