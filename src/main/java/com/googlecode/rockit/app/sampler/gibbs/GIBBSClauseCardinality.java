package com.googlecode.rockit.app.sampler.gibbs;

public class GIBBSClauseCardinality extends GIBBSClauseHard
{

    private boolean lessEqual   = false;
    private int     cardinality = 1;


    public GIBBSClauseCardinality(boolean lessEqual, int cardinality)
    {
        this.cardinality = cardinality;
        this.lessEqual = lessEqual;
    }


    /**
     * A cardinality constraint is true if
     * - lessEqual is true and at most cardinality literals are true
     * - lessEqual is false and at minimum cardinality literals are true
     * 
     * Different method than for hard clause.
     */
    @Override
    public boolean isTrue()
    {
        // System.out.println(this.toString());
        if(lessEqual) {
            // - lessEqual is true and at most cardinality literals are true
            int count = 0;
            for(GIBBSLiteral l : this.getPositiveLiterals()) {
                // System.out.println("+" + l);
                if(l.isPositive()) {
                    count++;
                    // System.out.println(count);
                    if(count > cardinality) {
                        // System.out.println(false);
                        return false;
                    }
                }
            }
            for(GIBBSLiteral l : this.getNegativeLiterals()) {
                System.out.println("-" + l);
                if(l.isNegative()) {
                    count++;
                    // System.out.println(count);
                    if(count > cardinality) { return false; }
                }
            }
            // System.out.println(true);
            return true;
        } else {
            // - lessEqual is false and at minimum cardinality literals are true
            int count = 0;
            for(GIBBSLiteral l : this.getPositiveLiterals()) {
                if(l.isPositive()) {
                    count++;
                    if(count > cardinality) { return true; }
                }
            }
            for(GIBBSLiteral l : this.getNegativeLiterals()) {
                if(l.isNegative()) {
                    count++;
                    if(count > cardinality) { return true; }
                }
            }
            return false;
        }
    }


    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("GIBBSClauseCardinality [lessEqual=");
        builder.append(lessEqual);
        builder.append(", cardinality=");
        builder.append(cardinality);
        builder.append("]");
        return builder.toString();
    }

}
