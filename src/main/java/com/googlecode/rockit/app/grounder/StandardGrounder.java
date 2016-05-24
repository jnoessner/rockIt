package com.googlecode.rockit.app.grounder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.conn.sql.MySQLConnector;
import com.googlecode.rockit.conn.sql.SQLQueryGenerator;
import com.googlecode.rockit.exception.DatabaseException;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.javaAPI.formulas.FormulaAbstract;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.FormulaObjective;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;
import com.googlecode.rockit.javaAPI.predicates.PredicateAbstract;
import com.googlecode.rockit.javaAPI.predicates.PredicateDouble;


public class StandardGrounder
{
    private boolean          groundHiddenPredicates = false;

    private Model            model                  = null;

    private MySQLConnector   connector;

    private HerbrandUniverse u                      = HerbrandUniverse.getInstance();


    public StandardGrounder(Model model, MySQLConnector connector)
    {
        this.connector = connector;
        this.model = model;
    }


    /**
     * This function generates a table for every observed predicate value.
     * 
     * The number of cols equals the number of Types of the respective predicate.
     * For Example:
     * predicate [observed] subsumesConfidence: Concept X Concept X Double;
     * 
     * results in:
     * CREATE TABLE `subsumesConfidence` ( value double, field0 varchar(50) NOT NULL, INDEX USING HASH (field0) , field1 varchar(50) NOT NULL, INDEX USING HASH (field1) , PRIMARY KEY (field0 , field1) ) ENGINE = MEMORY DEFAULT CHARSET=latin1;
     * 
     * Naming Conventions:
     * 1. The types are named "fieldX", where "X" is a number starting by 0 and counting the number of types.
     * 2. If it is a DoubleVariable, the field containing the double values is called "value"
     *
     * @param model
     * @param connector
     * @throws DatabaseException
     * @throws SQLException
     */
    private void generateObservedPredicateTables() throws DatabaseException
    {
        HashSet<PredicateAbstract> predicates = model.getAllObservedPredicates();
        if(this.groundHiddenPredicates) {
            predicates.addAll(model.getAllHiddenPredicates());
        }
        for(PredicateAbstract predicateAbstract : predicates) {

            // Create new one & Create prepared add statement
            ArrayList<String> fieldNames = new ArrayList<String>();
            for(int counter = 0; counter < predicateAbstract.getTypes().size(); counter++) {
                StringBuilder sb = new StringBuilder();
                fieldNames.add(sb.append("field").append(counter).toString());
            }

            Integer size = u.getMaximalKeySize();

            if(predicateAbstract.getClass().equals(PredicateDouble.class)) {
                connector.createInMemoryTable(predicateAbstract.getName(), "value", false, fieldNames, size);
            } else {
                connector.createInMemoryTable(predicateAbstract.getName(), null, false, fieldNames, size);
            }
        }
    }


    /**
     * This function puts the data into the tables. Thereby it uses the data stored in the
     * groundValue attributes of the observed predicates.
     * 
     * @param model
     * @param connector
     * @throws DatabaseException
     * @throws SQLException
     */
    private void putDataIntoTables() throws DatabaseException
    {
        long start = System.currentTimeMillis();
        HashSet<PredicateAbstract> predicates = model.getAllObservedPredicates();
        if(this.groundHiddenPredicates) {
            predicates.addAll(model.getAllHiddenPredicates());
        }
        for(PredicateAbstract predicateAbstract : predicates) {

            // Prepare add statement and fill with values
            ArrayList<String[]> groundValues = predicateAbstract.getGroundValues();
            StringBuilder filename = new StringBuilder();
            filename.append(predicateAbstract.getName()).append("_temp.db");
            if(PredicateDouble.class.equals(predicateAbstract.getClass())) {
                PredicateDouble predicateDouble = (PredicateDouble) predicateAbstract;
                if(Parameters.DEBUG_OUTPUT)
                    System.out.println("INSERT Data into " + predicateAbstract.getName());
                else
                    System.out.print(".");
                connector.addData(predicateAbstract.getName(), predicateDouble.getDoubleValues(), groundValues, filename.toString());
            } else {
                if(Parameters.DEBUG_OUTPUT)
                    System.out.println("INSERT Data into " + predicateAbstract.getName());
                else
                    System.out.print(".");
                connector.addData(predicateAbstract.getName(), groundValues, filename.toString());
            }
        }
        System.out.println("PutDataIntoTables duration: " + (System.currentTimeMillis() - start));
        System.out.println();
    }


    /**
     * Generates tables for every objective formular.
     * 
     * The cols of the tables are the variable names from the for part of the formular.
     * 
     * The concrete grounding then takes place with the if part of a formular.
     * Thereby, the cols are filled with the values comming from the given observed predicate values.
     * More precisely, the observed predicate tables are always joined iff two variables of the predicate expression are the same.
     * 
     * For example:
     * formular: objF
     * for Double cconf,Concept c2,Concept c1
     * if class(c1) & class(c2) & subsumesConfidence(c1, c2, cconf)
     * 
     * results in table "objF":
     * cconf | c2 | c1
     * 
     * The values are filled with an Insert-Select statement where
     * "class.c1 == subsumesConfidence.c1" and "class.c2 == subsumesConfidence.c2"
     * 
     * Note: due to the generalization this looks a little bit more ugly in reality:
     * INSERT INTO objF (cconf,c2,c1)
     * SELECT x2.value,x1.field0,x0.field0
     * FROM class x0,class x1,subsumesConfidence x2
     * WHERE x0.field0 = x2.field0 AND x1.field0 = x2.field1
     * 
     * @param model
     * @param connector
     * @throws SQLException
     * @throws ParseException
     * @throws SolveException
     */
    private void groundObjectiveFormulas() throws ParseException, SolveException
    {
        for(FormulaAbstract formularAbstract : model.getFormulas()) {
            if(formularAbstract.getClass().equals(FormulaObjective.class)) {
                FormulaObjective formular = (FormulaObjective) formularAbstract;
                // generate table for whole formular
                String tableName = formular.getName();
                ArrayList<String> colNames = new ArrayList<String>();
                for(VariableType variable : formular.getForVariables()) {
                    colNames.add(variable.getName());
                }
                String doubleName = null;
                if(formular.getDoubleVariable() != null) {
                    doubleName = formular.getDoubleVariable().getName();
                }
                Integer size = u.getMaximalKeySize();
                if(doubleName == null) {
                    connector.createInMemoryTable(tableName, null, true, colNames, size);
                } else {
                    connector.createInMemoryTable(tableName, doubleName, true, colNames, size);
                }

                // create select insert query
                StringBuilder insert = new StringBuilder();
                insert.append("INSERT INTO ");
                StringBuilder select = new StringBuilder();
                select.append(" SELECT ");

                // insert
                insert.append(formular.getName()).append(" (");
                int pos = 0;
                if(doubleName != null) {
                    insert.append(doubleName).append(",");
                    select.append("xx.").append(doubleName).append(",");
                }
                for(VariableType variable : formular.getForVariables()) {
                    insert.append(variable.getName());
                    select.append("xx.").append(variable.getName());
                    if(pos < formular.getForVariables().size() - 1) {
                        insert.append(",");
                        select.append(",");
                    }
                    pos++;
                }
                insert.append(") ");

                // select
                // for each ForVariable exactly one select statement is needed.
                // in the hashmap the mapping between the forvariable and the ground values is saved.

                // Transform formular objective to Formula Soft so that the function of the standardSolver can be used.
                // the restrictives are an empty list
                FormulaHard helper = new FormulaHard(formular.getName(), formular.getForVariables(), formular.getIfExpressions(), new ArrayList<PredicateExpression>(), false);
                // Execute getWithoutSelect from StandardSolver
                String innerSQLQuery = SQLQueryGenerator.getSQLStatementWithoutSelect(helper, false, false);
                // System.out.println("innerq " + innerSQLQuery);
                StringBuilder insertSelectQuery = new StringBuilder();
                insertSelectQuery.append(insert).append(select).append(innerSQLQuery);

                if(Parameters.DEBUG_OUTPUT)
                    System.out.println("insert query " + insertSelectQuery.toString());

                // execute Insert / Select Query
                connector.executeQuery(insertSelectQuery.toString());
            }
        }
    }


    /**
     * This function grounds the predicates of the model.
     * Thereby, first the ground values of the observed predicates are written into
     * database. Then, we compute the if-part of every formular and calculate
     * the grounded version (that means, every possible values) of every grounded formulars.
     * These grounded values are written in tables called after the formular names.
     * 
     * !! since preprocessing steps are included, overwrite your previous model with the result of this function!!
     * 
     * @param model
     * @throws SQLException
     * @throws ParseException
     * @throws SolveException
     */
    public void ground() throws ParseException, SolveException
    {
        System.out.print("===== Start Standard Grounder =====");
        System.out.println(new Date());
        if(Parameters.DEBUG_OUTPUT)
            System.out.println(model);

        this.generateObservedPredicateTables();
        this.putDataIntoTables();
        this.groundObjectiveFormulas();

        System.out.print(model.getAllHiddenPredicates().size());
        System.out.println(" hidden Predicates");
        System.out.print(model.getAllObservedPredicates().size());
        System.out.println(" observed Predicates");
        System.out.print(model.getFormulas().size());
        System.out.println(" number of formulas");

        long size = 0;
        for(PredicateAbstract pred : model.getAllHiddenPredicates()) {
            size = size + pred.getGroundValues().size();
        }
        for(PredicateAbstract pred : model.getAllObservedPredicates()) {
            size = size + pred.getGroundValues().size();
        }
        System.out.print(size);
        System.out.println(" evidence atoms");

    }


    public boolean isGroundHiddenPredicates()
    {
        return groundHiddenPredicates;
    }


    public void setGroundHiddenPredicates(boolean groundHiddenPredicates)
    {
        this.groundHiddenPredicates = groundHiddenPredicates;
    }

}
