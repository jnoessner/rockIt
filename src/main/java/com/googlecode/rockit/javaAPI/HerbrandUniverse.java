package com.googlecode.rockit.javaAPI;

import java.util.HashMap;


public class HerbrandUniverse
{

    private static HerbrandUniverse instance = new HerbrandUniverse();


    /**
     * Singelton. Private constructor.
     */
    private HerbrandUniverse()
    {
    }


    /**
     * Gets the one and only reference to this class (singelton).
     */
    public static HerbrandUniverse getInstance()
    {
        return instance;
    }

    private final char[]            chars           = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    private final int               size            = chars.length;

    private long                    constantNr      = 0;

    private int                     currentKeySize  = 0;
    // private int maxNumberKeySize = 0;

    private HashMap<String, String> constantKeyPair = new HashMap<String, String>();

    private HashMap<String, String> keyConstantPair = new HashMap<String, String>();


    public void reset()
    {
        constantNr = 0;
        currentKeySize = 0;
        // maxNumberKeySize = 0;
        constantKeyPair = new HashMap<String, String>();
        keyConstantPair = new HashMap<String, String>();
    }


    /**
     * Returns key of constant. Creates a new Key, if no key exists yet for this constant.
     * 
     * @return
     */
    public String getKey(String constant)
    {
        String key = constantKeyPair.get(constant);
        if(key == null) {
            if(constant.matches("^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$")) { // ^-?\\d+$
                constant = String.valueOf(Double.valueOf(constant));
                // maxNumberKeySize = Math.max(constantNr.length(), maxNumberKeySize);
            }
            StringBuilder sb = new StringBuilder();
            long cNr = constantNr;
            do {
                int rest = (int) (cNr % size);
                sb.append(chars[rest]);
                cNr = cNr / size;
            } while(cNr != 0);
            key = "c" + sb.toString();
            constantNr++;
            currentKeySize = Math.max(key.length(), currentKeySize);
        }

        constantKeyPair.put(constant, key);
        keyConstantPair.put(key, constant);

        return key;
    }


    /**
     * Returns the constant given a key.
     * 
     * @param key
     * @return
     */
    public String getConstant(String key)
    {
        return keyConstantPair.get(key);
    }


    /**
     * Transforms internally used data structure of axioms back to the input format structure.
     * Especially, it transforms the short constants back to its original long names.
     * 
     * @param compromizedAxiom
     * @return
     */
    public String transformKeysToConstants(String compromizedAxiom)
    {
        String[] resArray = compromizedAxiom.split("\\|");
        StringBuilder output = new StringBuilder();
        output.append(resArray[0]).append("(");
        if(resArray.length > 2) {
            for(int i = 1; i < resArray.length - 1; i++) {
                output.append("\"").append(this.getConstant(resArray[i])).append("\"").append(", ");
            }
        }
        output.append("\"").append(this.getConstant(resArray[resArray.length - 1])).append("\"").append(")");
        return output.toString();
    }


    /**
     * Returns the maximal key size s of all current strings. This is used as length in the sql tables (char(s)).
     * 
     * @return
     */
    public int getMaximalKeySize()
    {
        // return Math.max(Math.max(currentKeySize, maxNumberKeySize), 100);
        return currentKeySize + 3;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for(String c : constantKeyPair.keySet()) {
            sb.append(c + "\t" + constantKeyPair.get(c) + System.lineSeparator());
        }
        return sb.toString();
    }


    public static void main(String[] args)
    {
        HerbrandUniverse t = HerbrandUniverse.getInstance();
        System.out.println(t.getKey("1"));
        System.out.println(t.getKey("2"));
        System.out.println(t.getKey("3"));
        System.out.println(t.getKey("4"));
        System.out.println(t.getKey("5"));
        System.out.println(t.getKey("6"));
        System.out.println(t.getKey("7"));
        System.out.println(t.getKey("8"));
        System.out.println(t.getKey("9"));
        System.out.println(t.getKey("10"));
        System.out.println(t.getKey("12"));
        System.out.println(t.getKey("13"));
        System.out.println(t.getKey("11"));
        System.out.println(t.getKey("112"));
        System.out.println(t.getKey("113"));
        System.out.println(t.getKey("111"));
        System.out.println(t.getKey("1112"));
        System.out.println(t.getKey("1113"));
        System.out.println(t.getKey("11111"));
        System.out.println(t.getKey("11112"));
        System.out.println(t.getKey("a1"));
        System.out.println(t.getKey("a2"));
        System.out.println(t.getKey("a3"));
        System.out.println(t.getKey("a4"));
        System.out.println(t.getKey("a5"));
        System.out.println(t.getKey("a6"));
        System.out.println(t.getKey("a7"));
        System.out.println(t.getKey("a8"));
        System.out.println(t.getKey("a9"));
        System.out.println(t.getKey("a10"));
        System.out.println(t.getKey("a12"));
        System.out.println(t.getKey("a13"));
        System.out.println(t.getKey("a11"));
        System.out.println(t.getKey("a112"));
        System.out.println(t.getKey("a113"));
        System.out.println(t.getKey("a111"));
        System.out.println(t.getKey("a1112"));
        System.out.println(t.getKey("a1113"));
        System.out.println(t.getKey("a11111"));
        System.out.println(t.getKey("a11112"));
        System.out.println(t.getKey("ba1"));
        System.out.println(t.getKey("ba2"));
        System.out.println(t.getKey("ba3"));
        System.out.println(t.getKey("ba4"));
        System.out.println(t.getKey("ba5"));
        System.out.println(t.getKey("ba6"));
        System.out.println(t.getKey("ba7"));
        System.out.println(t.getKey("ba8"));
        System.out.println(t.getKey("ba9"));
        System.out.println(t.getKey("ba10"));
        System.out.println(t.getKey("ba12"));
        System.out.println(t.getKey("ba13"));
        System.out.println(t.getKey("ba11"));
        System.out.println(t.getKey("ba112"));
        System.out.println(t.getKey("ba113"));
        System.out.println(t.getKey("ba111"));
        System.out.println(t.getKey("ba1112"));
        System.out.println(t.getKey("ba1113"));
        System.out.println(t.getKey("ba11111"));
        System.out.println(t.getKey("ba11112"));
        System.out.println(t.constantKeyPair);
        System.out.println(t.keyConstantPair);
        System.out.println(t.getMaximalKeySize());
    }

}
