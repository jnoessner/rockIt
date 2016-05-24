package com.googlecode.rockit.test;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.googlecode.rockit.app.RockItAPI;
import com.googlecode.rockit.app.result.RockItResult;


public class Test2
{

    public static void main(String[] args) throws Exception
    {
        RockItAPI rockIt = new RockItAPI();
        String folder2 = "../RockItExistential/tmp/data/";

        // List<RockItResult> result = rockIt.doMapState(folder + "prog_function.mln", folder + "data_function.db");
        List<RockItResult> result = rockIt.doMapState(folder2 + "love-hate-example-out.mln", folder2 + "love-hate-example-out.db");

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
