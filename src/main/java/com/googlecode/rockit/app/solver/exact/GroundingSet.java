package com.googlecode.rockit.app.solver.exact;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;


/**
 * 
 * @author Bernd
 *
 */
@SuppressWarnings ("serial")
public class GroundingSet extends BitSet implements Iterable<GroundingSet>
{
    private static final boolean debug = true;

    private Literal[]            literals;


    public GroundingSet()
    {
        super();
    }


    public GroundingSet(List<Clause> clauses)
    {
        HashSet<Literal> tempLiterals = new HashSet<Literal>();
        for(Clause c : clauses) {
            ArrayList<Literal> r = c.getRestriction();
            tempLiterals.addAll(r);
        }

        literals = tempLiterals.toArray(new Literal[tempLiterals.size()]);
    }


    public GroundingSet(int nbits)
    {
        super(nbits);
    }


    public Set<Literal> getPositive()
    {
        Set<Literal> result = new HashSet<Literal>();
        // for(int i = 0; i < literals.length; i++) {
        // Literal l = literals[i];
        // if((this.get(i) && l.isPositive()) || (!this.get(i) && !l.isPositive()))
        // result.add(l);
        // }
        for(int i = nextSetBit(0); i >= 0; i = nextSetBit(i + 1)) {
            result.add(literals[i]);
        }
        return result;
    }


    public Set<Literal> getNegative()
    {
        Set<Literal> result = new HashSet<Literal>();
        // for(int i = 0; i < literals.length; i++) {
        // Literal l = literals[i];
        // if((this.get(i) && !l.isPositive()) || (!this.get(i) && l.isPositive())) {
        // result.add(l);
        // }
        // }
        for(int i = nextClearBit(0); i >= 0 && i < literals.length; i = nextClearBit(i + 1)) {
            result.add(literals[i]);
        }
        return result;
    }


    @Override
    public String toString()
    {
        StringBuffer s1 = new StringBuffer();
        StringBuffer s2 = new StringBuffer();
        for(int i = length() + 1; i >= 0; i--) {
            s1.append(get(i) ? 1 : 0);
        }
        if(debug) {
            s2.append("\t{");
            for(int i = nextSetBit(0); i >= 0; i = nextSetBit(i + 1)) {
                s2.append(literals[i]);
                s2.append(", ");
            }
            s2.delete(s2.length() - 2, s2.length());
            s2.append('}');
        }

        return "[" + Integer.parseInt(s1.toString(), 2) + "] " + s1 + "  " + super.toString() + s2;
    }


    @Override
    public Iterator<GroundingSet> iterator()
    {
        Iterator<GroundingSet> itr = new Iterator<GroundingSet>() {
            int index = -1;


            @Override
            public void remove()
            {
                throw new UnsupportedOperationException("Cannot remove items from a GroundingSet");
            }


            @Override
            public GroundingSet next()
            {
                if(index < 0) {
                    index = 0;
                    return GroundingSet.this;
                }
                flip(0, index);
                set(index);
                index = nextClearBit(0);
                return GroundingSet.this;
            }


            @Override
            public boolean hasNext()
            {
                return index <= literals.length - 1;
            }
        };
        return itr;
    }


    public BigInteger getNumberOfWorlds()
    {
        return (new BigInteger("2", 10).pow(literals.length));
    }
}
