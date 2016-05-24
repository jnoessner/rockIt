package com.googlecode.rockit.file;

import java.util.ArrayList;
import java.util.HashMap;

import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;


/**
 * From: http://logic.pdmi.ras.ru/~basolver/dimacs.html
 * 
 * This format is widely accepted as the standard format for boolean formulas in CNF. Benchmarks listed on satlib.org, for instance, are in the DIMACS CNF format.
 * 
 * An input file starts with comments (each line starts with c). The number of variables and the number of clauses is defined by the line p cnf variables clauses
 * 
 * Each of the next lines specifies a clause: a positive literal is denoted by the corresponding number, and a negative literal is denoted by the corresponding negative number. The last number in a line should be zero. For example,
 * 
 * c A sample .cnf file.
 * p cnf 3 2
 * 1 -3 0
 * 2 3 -1 0
 * 
 * 
 * @author jan
 *
 */

public class DimacsFileWriter
{

    private static MyFileWriter              writer;

    private static HashMap<Literal, Integer> literalMapping = new HashMap<Literal, Integer>();
    private static HashMap<Double, Integer>  weightMapping  = new HashMap<Double, Integer>();

    private static HashMap<Integer, String>  variableNames  = new HashMap<Integer, String>();

    private static int                       variablesInt   = 1;
    private static int                       clausesInt     = 1;
    private static int                       weightInt      = 3;                              // weight 1 = hard weight


    public static void initialize(String dimacsFileName) throws ReadOrWriteToFileException
    {
        writer = new MyFileWriter(dimacsFileName);
    }


    /**
     * Statische Methode, liefert die einzige Instanz dieser
     * Klasse zurück
     * 
     * @return
     */
    public static void writeSoft(double weight, ArrayList<Literal> restriction)
    {
        // one clause more
        clausesInt++;
        // weight in integer
        Integer w = weightMapping.get(weight);
        if(w == null) {
            weightMapping.put(weight, weightInt);
            w = weightInt;
            weightInt++;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(w).append(" ");
        StringBuilder sb2 = new StringBuilder();
        sb2.append(weight).append(" ");
        StringBuilder sb3 = new StringBuilder();
        sb3.append("c ").append(weight).append(" ");
        // literals in integer
        for(Literal lit : restriction) {
            Integer v = literalMapping.get(lit);
            if(v == null) {
                v = variablesInt;
                variablesInt++;
                literalMapping.put(lit, v);
                variableNames.put(variablesInt, lit.getName());
            }
            if(!lit.isPositive()) {
                sb.append("-");
            }
            sb.append(v).append(" ");
            sb2.append(v).append(" ");
            sb3.append(lit).append(" ");
        }
        sb.append("0");
        sb2.append("0");
        writer.writeln(sb.toString());
    }


    public static void writeHard(ArrayList<Literal> restriction)
    {
        // one clause more
        clausesInt++;
        // hard weight = 1
        StringBuilder sb = new StringBuilder();
        sb.append("2 ");
        // literals in integer
        for(Literal lit : restriction) {
            Integer v = literalMapping.get(lit);
            if(v == null) {
                v = variablesInt;
                variablesInt++;
                literalMapping.put(lit, v);
            }
            if(!lit.isPositive()) {
                sb.append("-");
            }
            sb.append(v).append(" ");
        }
        sb.append("0");
        writer.writeln(sb.toString());
    }


    public static String getLiteralName(int shortcut)
    {
        return variableNames.get(shortcut);
    }


    public static void close()
    {
        System.out.println("p cnf " + (variablesInt - 1) + " " + (clausesInt - 1));
        writer.closeFile();
    }
}
