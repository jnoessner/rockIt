package com.googlecode.rockit.app.solver.thread;

import java.util.ArrayList;
import java.util.HashMap;

import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
import com.googlecode.rockit.app.solver.pojo.Clause;
import com.googlecode.rockit.app.solver.pojo.Literal;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.conn.sql.MySQLConnector;
import com.googlecode.rockit.exception.ILPException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;


public abstract class RestrictionBuilder extends Thread
{

    public abstract FormulaHard getFormula();


    public abstract void generateRestrictions();


    public abstract void run();


    public abstract void addConstraints(ILPConnector con) throws ILPException, SolveException;


    public abstract boolean isFoundOneRestriction();


    public abstract void foundNoRestriction();


    public abstract void setTrackLiterals(boolean trackLiterals);


    public abstract HashMap<Literal, Literal> getLiterals();


    public abstract void setEvidenceAxioms(HashMap<Literal, Literal> evidence);


    public abstract void setSql(MySQLConnector sql);


    public abstract AggregationManager getAggregationManager();


    public abstract ArrayList<Clause> getClauses();

}