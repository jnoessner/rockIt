package com.googlecode.rockit.test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.googlecode.rockit.app.RockItAPI;
import com.googlecode.rockit.app.result.RockItResult;


public class Issues
{

    private static RockItAPI rockIt = new RockItAPI();
    private static String    folder = "data/debug/";


    public static void main(String[] args) throws Exception
    {
        // checkIssue("3");
        check("data/codi/prog.mln", "data/codi/evidence.db");
    }


    private static void check(String prog, String data) throws Exception
    {
        List<RockItResult> result = rockIt.doMapState(prog, data);
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


    public static void checkIssue(String id) throws Exception
    {
        List<RockItResult> result = rockIt.doMapState(folder + "prog_i" + id + ".mln", folder + "evidence_i" + id + ".db");

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
