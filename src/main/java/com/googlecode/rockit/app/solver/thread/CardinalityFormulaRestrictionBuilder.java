package com.googlecode.rockit.app.solver.thread;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.pojo.CardinalityClause;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.conn.sql.MySQLConnector;
import com.googlecode.rockit.exception.DatabaseException;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.formulas.FormulaCardinality;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;


public class CardinalityFormulaRestrictionBuilder extends RestrictionBuilder
{

    private HashMap<TreeSet<String>, Integer> alreadyAddedCardinalities = new HashMap<TreeSet<String>, Integer>();

    private HashMap<TreeSet<String>, Integer> newCardinalitiesToAdd     = new HashMap<TreeSet<String>, Integer>();

    private boolean                           foundOneRestriction       = false;

    private MySQLConnector                    sql                       = null;

    private FormulaCardinality                formula;


    public CardinalityFormulaRestrictionBuilder(FormulaCardinality formula)
    {
        this.formula = formula;
        alreadyAddedCardinalities = new HashMap<TreeSet<String>, Integer>();
    }


    public void run()
    {
        this.generateRestrictions();
    }


    /**
     * 
     * 
     * 
     * @param restrictions
     *            the list with restrictions
     * @param sql
     *            the sql reference
     * @param gurobi
     *            the gurobi reference
     * @return false if nothing has changed, true else.
     */
    public void generateRestrictions()
    {
        foundOneRestriction = false;
        newCardinalitiesToAdd = new HashMap<TreeSet<String>, Integer>();
        try {

            int restrictionCounter = 0;
            String cardinalityQuery = formula.getSqlQuery();
            // if(Parameters.DEBUG_OUTPUT)System.out.println("execute cardinality query: " + cardinalityQuery);
            ResultSet res = sql.executeSelectQuery(cardinalityQuery);

            int numberOfVars = formula.getRestrictions().size();
            int cardinalityNumber = formula.getCardinality();
            TreeSet<String> vars = new TreeSet<String>();
            String oldKey = "";
            String newKey = "";
            boolean beginFlag = true;
            while(res.next()) {
                // trick to assign oldKey the first value at the beginning!
                if(beginFlag) {

                    oldKey = res.getString(1);

                    beginFlag = false;
                }
                // in the first position the key is stored
                newKey = res.getString(1);
                // if it is still the same key
                if(newKey.equals(oldKey)) {
                    // do nothing
                } else {
                    // check if length of vars is greater than cardinalityNumber
                    if(vars.size() >= cardinalityNumber) {
                        restrictionCounter++;
                        // say, that one restriction has been found
                        if(!this.containsCardinalityKey(vars)) {
                            foundOneRestriction = true;
                            this.newCardinalitiesToAdd.put(vars, cardinalityNumber);
                            this.alreadyAddedCardinalities.put(vars, cardinalityNumber);
                        }
                        if(restrictionCounter % 100 == 0) {
                            if(Parameters.DEBUG_OUTPUT)
                                System.out.print(".");
                        }
                    }
                    // reset for next key.
                    vars = new TreeSet<String>();
                }
                // save the vars in the vars list.
                for(int i = 0; i < numberOfVars; i++) {
                    vars.add(res.getString(i + 2));
                }
                oldKey = newKey;
            }
            res.getStatement().close();
            res.close();
            // after the loop, there is exactly one key left
            // repeated code from above (did not want to create method)
            if(vars.size() >= cardinalityNumber) {
                // say that one restriction has been found
                if(!this.containsCardinalityKey(vars)) {
                    foundOneRestriction = true;
                    this.newCardinalitiesToAdd.put(vars, cardinalityNumber);
                    this.alreadyAddedCardinalities.put(vars, cardinalityNumber);
                }
            }
            // if(restrictionCounter>100) System.out.println();
            // if(Parameters.DEBUG_OUTPUT &&this.restrictionCounter%1000==0){
            // System.out.println("Soft Constraints: " + restrictionCounterSoft);
            // }else if(!Parameters.DEBUG_OUTPUT &&this.restrictionCounterSoft%10000==0){
            // System.out.print(".");
            // }
            // if(restrictionCounter>0) System.out.println("There have been added " +restrictionCounter + " cardinality restrictions to ILP.");

        } catch(DatabaseException e) {
            e.printStackTrace();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns cardinality clauses (needed for MCMC sampling)
     * 
     * @return
     */
    public ArrayList<Clause> getClauses()
    {
        ArrayList<Clause> result = new ArrayList<Clause>();
        for(Entry<TreeSet<String>, Integer> newCard : this.newCardinalitiesToAdd.entrySet()) {
            ArrayList<Literal> lits = new ArrayList<Literal>();
            for(String predName : newCard.getKey()) {
                lits.add(new Literal(predName, true));
            }
            CardinalityClause c = new CardinalityClause(lits, formula.isLessEqual(), newCard.getValue());
            result.add(c);
        }
        return result;
    }


    public void addConstraints(ILPConnector con) throws ILPException, SolveException
    {
        for(Entry<TreeSet<String>, Integer> newCard : this.newCardinalitiesToAdd.entrySet()) {
            con.addCardinalityConstraint(newCard.getKey(), formula.isLessEqual(), newCard.getValue());
        }
        this.newCardinalitiesToAdd = new HashMap<TreeSet<String>, Integer>();
    }


    public void foundNoRestriction()
    {
        this.foundOneRestriction = false;
    }


    private boolean containsCardinalityKey(TreeSet<String> variables)
    {
        for(TreeSet<String> cardSet : this.alreadyAddedCardinalities.keySet()) {
            String cardItem = cardSet.first();
            String variableItem = variables.first();
            boolean tempEquals = true;
            while(tempEquals && cardItem != null && variableItem != null) {
                if(cardItem.equals(variableItem)) {
                    // increase both
                    cardItem = cardSet.higher(cardItem);
                    variableItem = variables.higher(variableItem);

                } else {
                    tempEquals = false;
                }
            }
            if(cardItem == null && variableItem == null && tempEquals) {
                // System.out.println("BHUHUHUzzz " + cardSet + " equals " +variables);
                return true;
            }
        }
        return false;
    }


    /*
     * private HashMap<TreeSet<String>, GRBConstr> getEntailedCardinalityConstraints(TreeSet<String> variables){
     * HashMap<TreeSet<String>, GRBConstr> result = new HashMap<TreeSet<String>, GRBConstr>();
     * for(TreeSet<String> smallerItems : this.alreadyAddedCardinalities.keySet()){
     * int smallCounter = smallerItems.size();
     * int variableCounter = variables.size();
     * String smallerItem = smallerItems.first();
     * String variableItem = variables.first();
     * while(!((smallCounter>variableCounter)||smallerItem==null||variableItem==null)){
     * if(smallerItem.equals(variableItem)){
     * // increase both
     * smallerItem = smallerItems.higher(smallerItem);
     * variableItem = variables.higher(variableItem);
     * smallCounter--;
     * variableCounter--;
     * } else {
     * // increase only variable item
     * variableItem = variables.higher(variableItem);
     * variableCounter--;
     * }
     * }
     * // the small is entailed in the variables iff the small counter == 0
     * if(smallCounter==0){
     * result.put(smallerItems, this.alreadyAddedCardinalities.get(smallerItems));
     * }
     * }
     * return result;
     * }
     */

    public boolean isFoundOneRestriction()
    {
        return this.foundOneRestriction;
    }


    @Override
    public FormulaHard getFormula()
    {
        return formula;
    }


    @Override
    public void setTrackLiterals(boolean trackLiterals)
    {
        // Since no literals possible, we do not need to track them.

    }


    @Override
    public HashMap<Literal, Literal> getLiterals()
    {
        return null;
        // no literals for cardinality formulas possible
    }


    @Override
    public void setEvidenceAxioms(HashMap<Literal, Literal> evidence)
    {
        // we do not need any evidence axioms for cardinality Formulas
    }


    @Override
    public void setSql(MySQLConnector sql)
    {
        this.sql = sql;

    }


    @Override
    public AggregationManager getAggregationManager()
    {
        return null;
    }

}
