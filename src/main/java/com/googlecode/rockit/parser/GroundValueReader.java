package com.googlecode.rockit.parser;

import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.javaAPI.predicates.Predicate;
import com.googlecode.rockit.javaAPI.predicates.PredicateAbstract;
import com.googlecode.rockit.javaAPI.predicates.PredicateDouble;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;


@Deprecated
public class GroundValueReader
{
    private BufferedReader reader = null;


    public void read(String filename, Model model) throws ReadOrWriteToFileException, ParseException
    {
        HashSet<PredicateAbstract> predicates = model.getAllObservedPredicates();
        for(PredicateAbstract pred : model.getAllHiddenPredicates()) {
            predicates.add(pred);
        }
        this.read(filename, predicates.toArray(new PredicateAbstract[0]));
    }


    public void read(String filename, PredicateAbstract... predicates) throws ReadOrWriteToFileException, ParseException
    {
        // Open a bufferedReader first. It represents a stream to the file.

        ArrayList<String> readPredicates = new ArrayList<String>();
        String line = null;

        try {
            reader = new BufferedReader(new FileReader(filename));

            // Now read the file one at a time until you get a null indicating the end
            // of file.
            PredicateAbstract actualPredicate = null;

            while((line = reader.readLine()) != null) {
                // if there is an actual predicate, then
                // add ground values to this predicate.
                if(actualPredicate != null && !line.startsWith(">")) {
                    String trimmedLine = line.trim();
                    String replacedLine = trimmedLine.replaceAll(" ", "");
                    replacedLine = replacedLine.replaceAll("\\\"\\\"", "\"");
                    // System.out.print(actualPredicate);
                    // System.out.println(" " + replacedLine);
                    if(!replacedLine.equals("")) {
                        replacedLine = replacedLine.replaceFirst("\\\"", "");
                        String[] lineArray = replacedLine.split("\"");
                        if(actualPredicate.getClass().equals(Predicate.class)) {
                            Predicate pred = (Predicate) actualPredicate;
                            if((lineArray.length) != pred.getTypes().size()) { throw new ParseException("Error while reading the values for predicate " + pred.getName() + ". " + "The number of predicates (" + pred.getTypes().size() + ") differ from the lenth of the input line" + " (" + (lineArray.length)
                                    + "). The error occurs in the following line: " + line); }
                            pred.addGroundValueLine(lineArray);
                        }
                        if(actualPredicate.getClass().equals(PredicateDouble.class)) {
                            PredicateDouble pred = (PredicateDouble) actualPredicate;
                            if((lineArray.length) != (pred.getTypes().size() + 1)) { throw new ParseException("Error while reading the values for predicate " + pred.getName() + ". " + "The number of predicates (" + (pred.getTypes().size() + 1) + ") differ from the lenth of the input line" + " (" + (lineArray.length)
                                    + "). The error occurs in the following line: " + line); }
                            double value = Double.parseDouble(lineArray[pred.getTypes().size()]);
                            String[] lineArrayMinus1 = new String[pred.getTypes().size()];
                            for(int i = 0; i < lineArrayMinus1.length; i++) {
                                lineArrayMinus1[i] = lineArray[i];
                            }
                            pred.addGroundValueLine(value, lineArrayMinus1);
                        }
                    }

                }

                // if line starts with >
                // swich predicate to the one written after the ">"
                // (>subsumes) --> Prediacte.getName().equals("subsumes")...
                if(line.startsWith(">")) {
                    boolean found = false;
                    String trimmedLine = line.trim();
                    String replacedLine = trimmedLine.replaceAll(" ", "");
                    for(int i = 0; i < predicates.length; i++) {
                        StringBuilder compare = new StringBuilder();
                        compare.append(">").append(predicates[i].getName());
                        if(replacedLine.equalsIgnoreCase(compare.toString())) {
                            readPredicates.add(predicates[i].getName());
                            actualPredicate = predicates[i];
                            found = true;
                        }
                    }
                    if(!found) {
                        actualPredicate = null;
                    }
                }
            }
            reader.close();
        } catch(IOException e) {
            throw new ReadOrWriteToFileException("Could not read the file containing the ground values probably. Had to stop at line " + line + ". Filename was " + filename + " " + e.getMessage());
        }
        System.out.println("Read Predicates " + readPredicates + " from file " + filename);

    }
}
