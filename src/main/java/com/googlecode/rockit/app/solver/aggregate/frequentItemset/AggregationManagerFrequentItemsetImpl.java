/*
 * package com.googlecode.rockit.app.solver.aggregate.frequentItemset;
 * import java.util.HashMap;
 * import com.googlecode.rockit.app.solver.aggregate.*;
 * import com.googlecode.rockit.app.solver.aggregate.simple.AggragationManagerSimpleImpl;
 * import com.googlecode.rockit.app.solver.pojo.Clause;
 * import com.googlecode.rockit.conn.ilpSolver.GurobiConnector;
 * import com.googlecode.rockit.exception.ILPException;
 * import com.googlecode.rockit.exception.SolveException;
 * import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
 * public class AggregationManagerFrequentItemsetImpl implements AggregationManager{
 * private HashMap<Integer, AggregationManager> aggregationForLength = new HashMap<Integer, AggregationManager>();
 * public AggregationManagerFrequentItemsetImpl(FormulaSoft formula){
 * aggregationForLength.put(1, new AggragationManagerSimpleImpl());
 * if(formula.getRestrictions().size()>1){
 * for(int i=2;i<=formula.getRestrictions().size();i++){
 * aggregationForLength.put(i, new AggregationFrequentItemsetForSpecificSize(i, formula));
 * }
 * }
 * }
 * @Override
 * public void resetAggregatedSoftFormulas() {
 * aggregationForLength = new HashMap<Integer, AggregationManager>();
 * }
 * @Override
 * public void addClauseForAggregation(Clause clause, FormulaSoft formula) {
 * int size = clause.getRestriction().size();
 * if(size>0) aggregationForLength.get(size).addClauseForAggregation(clause, formula);
 * }
 * @Override
 * public void addConstraintsToILP(GurobiConnector connector)
 * throws ILPException, SolveException {
 * for(AggregationManager m : this.aggregationForLength.values()){
 * m.addConstraintsToILP(connector);
 * }
 * }
 * @Override
 * public void addOrUpdateGurobiVariables(GurobiConnector con)
 * throws ILPException {
 * for(AggregationManager m : this.aggregationForLength.values()){
 * m.addOrUpdateGurobiVariables(con);
 * }
 * }
 * @Override
 * public int getNumberOfAggregatedClauses() {
 * int number = 0;
 * for(AggregationManager m : this.aggregationForLength.values()){
 * number = number + m.getNumberOfAggregatedClauses();
 * }
 * return number;
 * }
 * @Override
 * public void calculateAggregation() {
 * for(AggregationManager m : this.aggregationForLength.values()){
 * m.calculateAggregation();
 * }
 * }
 * }
 */
