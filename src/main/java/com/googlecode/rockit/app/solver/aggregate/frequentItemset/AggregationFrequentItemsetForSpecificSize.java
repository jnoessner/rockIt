/*
 * package com.googlecode.rockit.app.solver.aggregate.frequentItemset;
 * import gurobi.GRB;
 * import gurobi.GRBConstr;
 * import gurobi.GRBException;
 * import gurobi.GRBLinExpr;
 * import gurobi.GRBModel;
 * import gurobi.GRB.DoubleAttr;
 * import gurobi.GRBVar;
 * import java.io.*;
 * import java.util.*;
 * import java.util.Map.Entry;
 * import com.googlecode.rockit.app.ExampleOfClientCodeOfApriori;
 * import com.googlecode.rockit.app.solver.aggregate.AggregationManager;
 * import com.googlecode.rockit.app.solver.pojo.Clause;
 * import com.googlecode.rockit.app.solver.pojo.Literal;
 * import com.googlecode.rockit.conn.ilpSolver.GurobiConnector;
 * import com.googlecode.rockit.exception.ILPException;
 * import com.googlecode.rockit.exception.ReadOrWriteToFileException;
 * import com.googlecode.rockit.exception.SolveException;
 * import com.googlecode.rockit.helper.MyFileWriter;
 * import com.googlecode.rockit.helper.Parameters;
 * import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
 * import com.googlecode.rockit.helper.MyEntry;
 * /** The class encapsulates an implementation of the Apriori algorithm
 * to compute frequent itemsets.
 * Datasets contains integers (>=0) separated by spaces, one transaction by line, e.g.
 * 1 2 3
 * 0 9
 * 1 9
 * Usage with the command line :
 * $ java mining.Apriori fileName support
 * $ java mining.Apriori /tmp/data.dat 0.8
 * $ java mining.Apriori /tmp/data.dat 0.8 > frequent-itemsets.txt
 * Usage as library: see {@link ExampleOfClientCodeOfApriori}
 * http://code.google.com/p/apriori-java/
 * @author Martin Monperrus, University of Darmstadt, 2010
 * @author Nathan Magnus and Su Yibin, under the supervision of Howard Hamilton, University of Regina, June 2009.
 * GNU General Public License v3
 * No reproduction in whole or part without maintaining this copyright notice
 * and imposing this condition on any subsequent users.
 * public class AggregationFrequentItemsetForSpecificSize implements AggregationManager{
 * private HashMap<Literal, Literal> positiveLiterals = new HashMap<Literal, Literal>();
 * private HashMap<Literal, Literal> negativLiterals = new HashMap<Literal, Literal>();
 * private ArrayList<Literal> literals = new ArrayList<Literal>();
 * private HashMap<int[], Clause> inputLines = new HashMap<int[], Clause>();
 * private GRBVar zVariable = null;
 * private FormulaSoft formula=null;
 * private int numberOfAggregatedClauses = 0;
 * /** the list of current itemsets *
 * private HashMap<int[], HashMap<Clause, Clause>> itemsets ;
 * /** number of different items in the dataset *
 * private int numItems = 0;
 * /** total number of transactions in transaFile *
 * private int numTransactions = 0;
 * /** minimum support for a frequent itemset in percentage, e.g. 0.8 *
 * private double minSup = 0.2;
 * /** size of current itemset iteration (internally used)*
 * int currentSizeOfItemsets =0;
 * /** maximal size of itemsets (in our case, size of formula - 1)*
 * maxSizeOfItemsets =0;
 * AggregationFrequentItemsetForSpecificSize(int formulaRestrictionSize, FormulaSoft formula){
 * this.itemsets=new HashMap<int[], HashMap<Clause,Clause>>();
 * this.maxSizeOfItemsets=formulaRestrictionSize-1;
 * this.formula=formula;
 * }
 * //TODO
 * //apriori -n1 -m1 -s0.0000000001 -l1 input.file output.file
 * /* ===FREQUENT ITEMSET CALCULATION===================*
 * /** starts the algorithm after configuration *
 * private void go() {
 * //start timer
 * long start = System.currentTimeMillis();
 * // first we generate the candidates of size 1
 * createItemsetsOfSize1();
 * int itemsetNumber=1; //the current itemset being looked at
 * int nbFrequentSets=0;
 * for(int i =0; i<maxSizeOfItemsets;i++)
 * {
 * calculateFrequentItemsets();
 * if(itemsets.size()!=0)
 * {
 * nbFrequentSets+=itemsets.size();
 * log("Found "+itemsets.size()+" frequent itemsets of size " + itemsetNumber + " (with support "+(minSup*100)+"%)");;
 * createNewItemsetsFromPreviousOnes();
 * }
 * itemsetNumber++;
 * }
 * //display the execution time
 * long end = System.currentTimeMillis();
 * log("Execution time is: "+((double)(end-start)/1000) + " seconds.");
 * log("Found "+nbFrequentSets+ " frequents sets for support "+(minSup*100)+"% (absolute "+Math.round(numTransactions*minSup)+")");
 * log("Done");
 * }
 * /** outputs a message in Sys.err if not used as library *
 * private void log(String message) {
 * if(Parameters.DEBUG_OUTPUT) System.out.println(message);
 * }
 * /** outputs the current configuration
 * private void outputConfig() {
 * //output config info to the user
 * log("Input configuration: "+numItems+" items, "+numTransactions+" transactions, ");
 * log("minsup = "+minSup+"%");
 * }
 * /** puts in itemsets all sets of size 1,
 * i.e. all possibles items of the datasets
 * private void createItemsetsOfSize1() {
 * itemsets = new HashMap<int[], HashMap<Clause, Clause>>();
 * for(int i=0; i<numItems; i++)
 * {
 * int[] cand = {i};
 * itemsets.put(cand, new HashMap<Clause, Clause>());
 * }
 * this.currentSizeOfItemsets=0;
 * }
 * /**
 * if m is the size of the current itemsets,
 * generate all possible itemsets of size n+1 from pairs of current itemsets
 * replaces the itemsets of itemsets by the new ones
 * private void createNewItemsetsFromPreviousOnes()
 * {
 * // by construction, all existing itemsets have the same size
 * currentSizeOfItemsets++;
 * log("Creating itemsets of size "+(currentSizeOfItemsets+1)+" based on "+itemsets.size()+" itemsets of size "+currentSizeOfItemsets);
 * HashMap<String, MyEntry<int[], HashMap<Clause, Clause>>> tempCandidates = new HashMap<String, MyEntry<int[],HashMap<Clause, Clause>>>(); //temporary candidates
 * // compare each pair of itemsets of size n-1
 * for(Entry<int[], HashMap<Clause, Clause>> itemPair1 : this.itemsets.entrySet())
 * {
 * for(Entry<int[], HashMap<Clause, Clause>> itemPair2 : this.itemsets.entrySet())
 * {
 * int[] X = itemPair1.getKey();
 * int[] Y = itemPair2.getKey();
 * assert (X.length==Y.length);
 * //make a string of the first n-2 tokens of the strings
 * int [] newCand = new int[currentSizeOfItemsets+1];
 * for(int s=0; s<newCand.length-1; s++) {
 * newCand[s] = X[s];
 * }
 * int ndifferent = 0;
 * // then we find the missing value
 * for(int s1=0; s1<Y.length; s1++)
 * {
 * boolean found = false;
 * // is Y[s1] in X?
 * for(int s2=0; s2<X.length; s2++) {
 * if (X[s2]==Y[s1]) {
 * found = true;
 * break;
 * }
 * }
 * if (!found){ // Y[s1] is not in X
 * ndifferent++;
 * // we put the missing value at the end of newCand
 * newCand[newCand.length -1] = Y[s1];
 * }
 * }
 * // we have to find at least 1 different, otherwise it means that we have two times the same set in the existing candidates
 * assert(ndifferent>0);
 * if (ndifferent==1) {
 * // HashMap does not have the correct "equals" for int[] :-(
 * // I have to create the hash myself using a String :-(
 * // I use Arrays.toString to reuse equals and hashcode of String
 * Arrays.sort(newCand);
 * String keyString = Arrays.toString(newCand);
 * MyEntry<int[], HashMap<Clause, Clause>> canidate = tempCandidates.get(keyString);
 * if(canidate!=null){
 * canidate.getValue().putAll(itemPair1.getValue());
 * canidate.getValue().putAll(itemPair2.getValue());
 * }else{
 * HashMap<Clause, Clause> literals = new HashMap<Clause, Clause>();
 * literals.putAll(itemPair1.getValue());
 * literals.putAll(itemPair2.getValue());
 * MyEntry<int[], HashMap<Clause, Clause>> newEntry = new MyEntry<int[], HashMap<Clause, Clause>>(newCand,literals);
 * tempCandidates.put(keyString, newEntry);
 * }
 * }
 * }
 * }
 * //set the new itemsets
 * itemsets = new HashMap<int[], HashMap<Clause, Clause>>();
 * for(MyEntry<int[], HashMap<Clause, Clause>> tempCanidate: tempCandidates.values()){
 * itemsets.put(tempCanidate.getKey(), tempCanidate.getValue());
 * }
 * log("Created "+itemsets.size()+" unique itemsets of size "+(currentSizeOfItemsets+1));
 * }
 * /** put "true" in trans[i] if the integer i is in line *
 * private void int2booleanArray(int[] values, boolean[] trans) {
 * Arrays.fill(trans, false);
 * //put the contents of that line into the transaction array
 * for(int i : values)
 * {
 * trans[i]=true; //if it is not a 0, assign the value to true
 * }
 * }
 * /** passes through the data to measure the frequency of sets in {@link itemsets},
 * then filters thoses who are under the minimum support (minSup)
 * private void calculateFrequentItemsets()
 * {
 * log("Passing through the data to compute the frequency of " + itemsets.size()+ " itemsets of size "+this.currentSizeOfItemsets);
 * HashMap<int[], HashMap<Clause, Clause>> frequentCandidates = new HashMap<int[], HashMap<Clause, Clause>>(); //the frequent candidates for the current itemset
 * boolean match; //whether the transaction has all the items in an itemset
 * int count[] = new int[itemsets.size()]; //the number of successful matches, initialized by zeros
 * HashMap<Clause, Clause> clause[] = new HashMap[itemsets.size()];
 * // load the transaction file
 * boolean[] trans = new boolean[numItems];
 * // for each transaction
 * for (Entry<int[], Clause> inputEntry : inputLines.entrySet()) {
 * this.int2booleanArray(inputEntry.getKey(), trans);
 * // check each candidate
 * int c=0;
 * for (Entry<int[], HashMap<Clause, Clause>> itemSet : itemsets.entrySet()) {
 * match = true; // reset match to false
 * // tokenize the candidate so that we know what items need to be
 * // present for a match
 * int[] cand = itemSet.getKey();
 * //int[] cand = candidatesOptimized[c];
 * // check each item in the itemset to see if it is present in the
 * // transaction
 * for (int xx : cand) {
 * if (trans[xx] == false) {
 * match = false;
 * break;
 * }
 * }
 * if (match) { // if at this point it is a match, increase the count
 * count[c]++;
 * if(clause[c]==null){
 * HashMap<Clause,Clause> clauseMap = new HashMap<Clause, Clause>();
 * clauseMap.put(inputEntry.getValue(), inputEntry.getValue());
 * clause[c]=clauseMap;
 * }else{
 * clause[c].put(inputEntry.getValue(), inputEntry.getValue());
 * }
 * //log(Arrays.toString(cand)+" is contained in trans "+i+" ("+line+")");
 * }
 * c++;
 * }
 * }
 * int i=0;
 * for (Entry<int[], HashMap<Clause, Clause>> itemSet : itemsets.entrySet()) {
 * // if the count% is larger than the minSup%, add to the candidate to
 * // the frequent candidates
 * if ((count[i] / (double) (numTransactions)) >= minSup) {
 * HashMap<Clause, Clause> clauseHash = itemSet.getValue();
 * clauseHash.putAll(clause[i]);
 * frequentCandidates.put(itemSet.getKey(), clauseHash);
 * }
 * //else log("-- Remove candidate: "+ Arrays.toString(candidates.get(i)) + "  is: "+ ((count[i] / (double) numTransactions)));
 * i++;
 * }
 * //new candidates are only the frequent candidates
 * itemsets = frequentCandidates;
 * }
 * @Override
 * public void resetAggregatedSoftFormulas() {
 * // TODO Auto-generated method stub
 * }
 * /* ================ Aggregation Manager Methods ===============================
 */

/**
 * Add aggregated soft constraints. Internally, we give every Literal in the clause an id.
 *
 * @Override
 *           public void addClauseForAggregation(Clause clause, FormulaSoft formula) {
 *           // give unique numbers to Clauses
 *           this.numTransactions++;
 *           for(Literal l : clause.getRestriction()){
 *           if(l.isPositive()){
 *           Literal l2 = this.positiveLiterals.get(l);
 *           if(l2==null){
 *           l.setId(numItems);
 *           this.literals.add(l);
 *           numItems++;
 *           this.positiveLiterals.put(l, l);
 *           }else{
 *           l.setId(l2.getId());
 *           }
 *           }else{
 *           Literal l2 = this.negativLiterals.get(l);
 *           if(l2==null){
 *           l.setId(numItems);
 *           this.literals.add(l);
 *           numItems++;
 *           this.negativLiterals.put(l, l);
 *           }else{
 *           l.setId(l2.getId());
 *           }
 *           }
 *           }
 *           // store literals of clauses as integers
 *           int size = clause.getRestriction().size();
 *           int[] inputKey = new int[size];
 *           for(int i=0; i<size; i++){
 *           inputKey[i]=clause.getRestriction().get(i).getId();
 *           }
 *           this.inputLines.put(inputKey, clause);
 *           }
 * 
 * 
 * 
 * 
 *           public void addConstraintsToILP(GurobiConnector con) throws SolveException{
 *           // add itemset variables.
 *           this.numberOfAggregatedClauses=this.itemsets.size();
 *           for(Entry<int[], HashMap<Clause, Clause>> itemSet:this.itemsets.entrySet()){
 *           ArrayList<Literal> aggregatedVars = new ArrayList<Literal>();
 *           ArrayList<Literal> singleVars = new ArrayList<Literal>();
 * 
 *           for(int aggVarInt : itemSet.getKey()){
 *           aggregatedVars.add(this.literals.get(aggVarInt));
 *           }
 * 
 *           for(Clause c : itemSet.getValue().keySet()){
 *           for(Literal l: c.getRestriction()){
 *           boolean isAggregated = false;
 *           for(int aggVarInt : itemSet.getKey()){
 *           if(l.getId()==aggVarInt) isAggregated=true;
 *           }
 *           if(!isAggregated) singleVars.add(l);
 *           }
 *           c.setAggregated(true);
 *           }
 * 
 *           if(!formula.isConjunction() || (aggregatedVars.size()==0)){
 *           if(formula.getWeight()>=0){
 *           this.getPositiveDisjunctionConstraint(con, aggregatedVars, singleVars);
 *           }else{
 *           this.getNegativeDisjunctionConstraint(con, aggregatedVars, singleVars);
 *           }
 *           } else{
 *           // TODO add positive conjunctions
 *           // TODO add negative conjunctions
 *           System.out.println(this.formula);
 *           throw new ILPException("At the moment only positive and negative disjunctions are supported. Conjunction support will follow soon.");
 *           }
 *           }
 * 
 *           // not aggregated constraints
 *           for(Clause c : inputLines.values()){
 *           if(!c.isAggregated()){
 *           this.numberOfAggregatedClauses++;
 *           con.addSingleSoftConstraint(c.getWeight(), c.getRestriction(), formula.isConjunction());
 *           }
 *           }
 * 
 * 
 *           }
 * 
 *           /**
 *           w negative and disjunction:
 * 
 *           sum k * aggregatedVars_positive - sum k * aggregatedVars_negative - z <= - (Sum_{negative aggregatedVars} 1) * k
 * 
 *           (-1)_{if single var negative} sum singleVars - z <= - (number of negative single vars)
 *           optional: 0 <= z <= k
 * 
 * @param model
 * @return
 * @throws GRBException
 *
 *             private void getNegativeDisjunctionConstraint(GurobiConnector con, ArrayList<Literal> aggregatedVars, ArrayList<Literal> singleVars) throws ILPException{
 *             //sum k * aggregatedVars_positive - sum k * aggregatedVars_negative - z <= (Sum_{negative aggregatedVars} 1) * k
 *             int k = singleVars.size();
 *             if(aggregatedVars.size()>0){
 *             GRBLinExpr expr1 = new GRBLinExpr();
 * 
 *             for(Literal axiom : aggregatedVars){
 *             if(axiom.isPositive()){
 *             //sum k * aggregatedVars_positive
 *             expr1.addTerm(k, axiom.getVar());
 *             }else{
 *             //- sum k * aggregatedVars_negative
 *             expr1.addTerm(- k, axiom.getVar());
 *             }
 *             }
 *             // - z
 *             expr1.addTerm(-1, zVariable);
 *             /*for(int i =0; i<k; i++){
 *             expr1.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
 *             }*
 * 
 *             //<=
 *             char lessEqual = GRB.LESS_EQUAL;
 * 
 * 
 *             //- (Sum_{negative aggregatedVars} 1) * k
 *             double rhs = 0;
 *             for(Literal negAggVar : aggregatedVars){
 *             if(!negAggVar.isPositive()){
 *             rhs=rhs-k;
 *             }
 *             }
 * 
 *             con.addConstraint(expr1, lessEqual, rhs, "");
 * 
 *             }
 * 
 *             // (-1)_{if single var negative} sum singleVars - z <= (0 if singleVars positive, k if singleVars negative)
 *             GRBLinExpr expr2 = new GRBLinExpr();
 * 
 *             //(-1)_{if single var negative} sum singleVars
 *             for(Literal singleVar : singleVars){
 *             double one_or_minus_one = -1;
 *             if (singleVar.isPositive()) one_or_minus_one = +1;
 *             expr2.addTerm(one_or_minus_one, singleVar.getVar());
 *             }
 * 
 *             // - z
 *             expr2.addTerm(-1, zVariable);
 *             /*for(int i =0; i<k; i++){
 *             expr2.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
 *             }*
 * 
 *             //<=
 *             char lessEqual = GRB.LESS_EQUAL;
 * 
 *             // - (number of negative singleVars)
 *             double rhs = 0;
 *             for(Literal singleVar : singleVars){
 *             if(!singleVar.isPositive()){
 *             rhs = rhs-1;
 *             }
 *             }
 *             con.addConstraint(expr2, lessEqual, rhs, "");
 * 
 * 
 *             //0 <= z <= k
 *             try {
 *             zVariable.set(DoubleAttr.UB, k);
 *             } catch (GRBException e) {
 *             e.printStackTrace();
 *             throw new ILPException("Could not set upper bound " + k+" of variable " +zVariable+". "+e.getMessage());
 *             }
 * 
 *             }
 * 
 * 
 *             /**
 *             w positive and disjunction:
 *             sum k * aggregatedVars_positive - sum k * aggregatedVars_negative + (-1)_{if single var negative} sum singleVars - z >= - (Sum_{negative aggVariables} 1) * k - (sum_{negative singleVars} 1)
 *             0 <= z <= k
 * 
 * @param model
 * @return
 * @throws GRBException
 *
 *             private void getPositiveDisjunctionConstraint(GurobiConnector con, ArrayList<Literal> aggregatedVars, ArrayList<Literal> singleVars) throws ILPException{
 *             int k = singleVars.size();
 *             GRBLinExpr expr = new GRBLinExpr();
 * 
 *             for(Literal aggAxiom : aggregatedVars){
 *             if(aggAxiom.isPositive()){
 *             //sum k * aggregatedVars_positive
 *             expr.addTerm(k, aggAxiom.getVar());
 *             }else{
 *             //- sum k * aggregatedVars_negative
 *             expr.addTerm(- k, aggAxiom.getVar());
 *             }
 *             }
 * 
 *             //(-1)_{if single var negative} sum singleVars
 * 
 *             for(Literal singleVar : singleVars){
 *             double one_or_minus_one = -1;
 *             if(singleVar.isPositive()) one_or_minus_one = 1;
 *             expr.addTerm(one_or_minus_one, singleVar.getVar());
 *             }
 * 
 *             // - z
 *             expr.addTerm(-1, zVariable);
 *             /*for(int i =0; i<k; i++){
 *             expr.addTerm(-1, con.getOrUpdateNextZ(this.weight, ""));
 *             }*
 * 
 *             //>=
 *             char greaterEqual = GRB.GREATER_EQUAL;
 * 
 *             // - (Sum_{negative aggVariables} 1) * k - (sum_{negative singleVars} 1)
 *             double rhs = 0;
 *             for(Literal aggAxiom : aggregatedVars){
 *             if(!aggAxiom.isPositive()){
 *             rhs=rhs-k;
 *             }
 *             }
 *             for(Literal singAxiom: singleVars){
 *             if(!singAxiom.isPositive()){
 *             rhs = rhs-1;
 *             }
 *             }
 * 
 *             //0 <= z <= k
 *             try {
 *             zVariable.set(DoubleAttr.UB, k);
 *             } catch (GRBException e) {
 *             e.printStackTrace();
 *             throw new ILPException("Could not set upper bound " + k+" of variable " +zVariable+". "+e.getMessage());
 *             }
 * 
 *             con.addConstraint(expr, greaterEqual, rhs, "");
 * 
 *             }
 * @Override
 *           public void addOrUpdateGurobiVariables(GurobiConnector con)
 *           throws ILPException {
 * 
 *           for(Literal l:positiveLiterals.keySet()){
 *           if(l.getVar()==null){
 *           l.setVar(con.addObjectiveVariablePrivate(l.getName()));
 *           }
 *           }
 * 
 *           for(Literal l:negativLiterals.keySet()){
 *           if(l.getVar()==null){
 *           l.setVar(con.addObjectiveVariablePrivate(l.getName()));
 *           }
 *           }
 *           if(zVariable==null){
 *           zVariable = con.getNextIntegerZ(formula.getWeight());
 *           }
 *           }
 * @Override
 *           public int getNumberOfAggregatedClauses() {
 *           return numberOfAggregatedClauses;
 *           }
 * @Override
 *           public void calculateAggregation() {
 * 
 *           try {
 *           MyFileWriter write = new MyFileWriter(formula.getName()+"_frequItemset.csv");
 *           for(int[] line : inputLines.keySet()){
 *           StringBuilder lineSB=new StringBuilder();
 *           for(int i : line){
 *           lineSB.append(i).append(" ");
 *           }
 *           write.writeln(lineSB.toString());
 *           }
 *           write.closeFile();
 *           } catch (ReadOrWriteToFileException e) {
 *           // TODO Auto-generated catch block
 *           e.printStackTrace();
 *           }
 * 
 * 
 *           this.outputConfig();
 * 
 *           // calculates the sets
 *           this.go();
 * 
 *           }
 * 
 *           }
 */
