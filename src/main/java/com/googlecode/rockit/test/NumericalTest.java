package com.googlecode.rockit.test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.googlecode.rockit.app.RockItAPI;
import com.googlecode.rockit.app.result.RockItResult;


public class NumericalTest
{

    private static RockItAPI rockIt  = new RockItAPI();
    private static String    folder1 = "data/numeric/generic/";
    private static String    folder2 = "data/numeric/grounding/";

    private static String    folder3 = "data/numeric/examples/";


    public static void main(String[] args) throws Exception
    {
        // temporal();
        // math();
        example();
    }


    public static void example() throws Exception
    {
        List<RockItResult> result = rockIt.doMapState(folder3 + "prog_np.mln", folder3 + "evidence_np.db");

        System.out.println(System.lineSeparator() + System.lineSeparator() + "Result:");
        System.out.println("===========");

        Set<String> set = new TreeSet<String>();
        for(RockItResult rs : result) {
            set.add(rs.toString());
        }

        for(String s : set) {
            System.out.println("\t" + s);
        }

    }


    public static void math() throws Exception
    {
        // List<RockItResult> result = rockIt.doMapState(folder + "prog_function.mln", folder + "data_function.db");
        List<RockItResult> result = rockIt.doMapState(folder2 + "prog_basic_grounding.mln", folder2 + "data_basic_grounding.db");

        System.out.println(System.lineSeparator() + System.lineSeparator() + "Result:");
        System.out.println("===========");

        Set<String> set = new TreeSet<String>();
        for(RockItResult rs : result) {
            set.add(rs.toString());
        }

        for(String s : set) {
            System.out.println("\t" + s);
        }

    }


    public static void temporal() throws Exception
    {
        List<RockItResult> result = rockIt.doMapState(folder1 + "prog_calc.mln", folder1 + "data_calc.db");

        System.out.println(System.lineSeparator() + System.lineSeparator() + "Result:");
        System.out.println("===========");

        Set<String> set = new TreeSet<String>();
        for(RockItResult rs : result) {
            set.add(rs.toString());
        }

        for(String s : set) {
            System.out.println("\t" + s);
        }

    }
}
