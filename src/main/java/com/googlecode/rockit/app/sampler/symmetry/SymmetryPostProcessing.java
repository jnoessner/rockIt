package com.googlecode.rockit.app.sampler.symmetry;

import java.util.ArrayList;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.sampler.gibbs.GIBBSLiteral;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.file.DimacsFileWriter;


public class SymmetryPostProcessing
{

    /**
     * 
     * Converts all clauses to the dimacs format and write it in a temporary file.
     * 
     * // TODO Modify the DimacsFileWriter so that the line printed in
     * the DimacsFileWriter.close() method (e.g.: p cnf 14 16) is put at the START (first line)
     * of the dimacs.saucy file.
     * 
     * @param clauses
     */
    public SymmetryPostProcessing(ArrayList<Clause> clauses)
    {
        StringBuilder filename = new StringBuilder();
        filename.append(Parameters.TEMP_PATH).append("dimacs.saucy");
        try {
            DimacsFileWriter.initialize(filename.toString());
            for(Clause c : clauses) {
                if(c.isHard()) {
                    DimacsFileWriter.writeHard(c.getRestriction());
                } else {
                    DimacsFileWriter.writeSoft(c.getWeight(), c.getRestriction());
                }
            }
            DimacsFileWriter.close();
        } catch(ReadOrWriteToFileException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    /**
     * TODO
     * General comment: Rockit makes the input literals a little bit shorter (e.g.smokes|h).
     * For debugging it might be useful to get the correct name.
     * To that end, call : ResultFileWriter.transformKeysToConstants(smokes|h)
     * 
     * 
     * 1. Start saucy with file created in constructor.
     * - Maybe you have to compile the files Mathias send you for windows (I have sent you a HOWTO for that)
     * 2. Read saucy results and perform aggregation (as you implemented it).
     * - the method getLiteralName(int) helps to format the saucy output back to literals (e.g.smokes|h).
     * - Use GIBBSLiteral as nodes in your tree.
     * 3. Compute average marginal probabilities.
     * - the method return_my_probability(numberOfSamples) in GIBBSLiteral computes the probability of an axiom. This
     * computation works in linear (but not in constant) time. So do not call that method more often than neccessary.
     * 4. Delete temp file.
     * 
     * IN GENERAL: Everything is time and memory critical. So use StringBuilders, read the input of Saucy as efficiently as possible, etc...
     * 
     * @param literals
     * @param numberOfSamples
     */
    public void computeSymmetries(ArrayList<GIBBSLiteral> literals, int numberOfSamples)
    {
        // TODO see above

    }

}
