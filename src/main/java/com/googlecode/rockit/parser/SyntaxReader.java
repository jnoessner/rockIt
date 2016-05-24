package com.googlecode.rockit.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.numerical.MathExpression;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.javaAPI.formulas.FormulaAbstract;
import com.googlecode.rockit.javaAPI.formulas.FormulaCardinality;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.FormulaObjective;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
import com.googlecode.rockit.javaAPI.formulas.expressions.IfExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableAbstract;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableArithmeticExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableDouble;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableString;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;
import com.googlecode.rockit.javaAPI.predicates.Predicate;
import com.googlecode.rockit.javaAPI.predicates.PredicateAbstract;
import com.googlecode.rockit.javaAPI.predicates.PredicateDouble;
import com.googlecode.rockit.javaAPI.predicates.PredicateNumerical;
import com.googlecode.rockit.javaAPI.types.Type;


public class SyntaxReader
{

    public Model getModel(String modelFile, String groundValueFile) throws ParseException, IOException
    {
        Model model;
        try {
            model = this.getModelANTLR(modelFile);
        } catch(RecognitionException e) {
            e.printStackTrace();
            throw new ParseException("Can not parse model.");
        }

        TreeSet<PredicateAbstract> groundValues;
        try {
            groundValues = this.getGroundValuesANTLR(groundValueFile);
        } catch(RecognitionException e) {
            e.printStackTrace();
            throw new ParseException("Can not read ground predicate.");
        }

        // add ground values to the model.
        ArrayList<PredicateAbstract> remainingGroundValues = this.integrateGroundValuesInModel(groundValues, model);

        // integrate the hidden predicate values. More precisely, transform them into observed predicate values and add a hard formular to connect them to the hidden pred.
        this.integrateHiddenPredicateValuesWithNewFormula(model, remainingGroundValues);

        // figures out the ground values for types and assigns a new predicate to each type containing these ground values.
        this.assignTypePredicates(model);

        // figures out which variables are not yet bound in formulas and add the "type predicate" of this variable to the if restriction.
        this.addUnboundVariablesPredicateTypes(model);

        // processes string variables - transform them to extra observed predicates and add them to types.
        this.processStringVariables(model);

        // transform soft to objective formulas if they have only one positive hidden predicate.
        this.transformSoftToObjectiveFormulas(model);

        // some preprocessing (simplifying conjunctions, etc)
        this.preprocessing(model);

        // process numerical predicates
        this.processNumericalPredicates(model);

        this.processRevelevantVariables(model);

        return model;
    }


    public void setInitialSolution(Model model, String initialSolutionFile) throws IOException, ParseException
    {
        TreeSet<PredicateAbstract> initalSolution;
        try {
            initalSolution = this.getGroundValuesANTLR(initialSolutionFile);
            model.setInitialSolution(new HashSet<PredicateAbstract>(initalSolution));
        } catch(RecognitionException e) {
            e.printStackTrace();
            throw new ParseException("Can not read initial solution.");
        }
    }


    public Model getModelForLearning(String modelFile) throws ParseException, IOException, RecognitionException
    {
        Model model = this.getModelANTLR(modelFile);

        // figures out the ground values for types and assigns a new predicate to each type containing these ground values.
        this.assignTypePredicates(model);

        // figures out which variables are not yet bound in formulas and add the "type predicate" of this variable to the if restriction.
        this.addUnboundVariablesPredicateTypes(model);

        // processes string variables - transform them to extra observed predicates and add them to types.
        this.processStringVariables(model);

        // transform soft to objective formulas if they have only one positive hidden predicate.
        this.transformSoftToObjectiveFormulas(model);

        // some preprocessing (simplifying conjunctions, etc)
        this.preprocessing(model);

        return model;
    }


    public Model getGroundValuesForLearning(String groundValueFile, Model model) throws ParseException, IOException, RecognitionException
    {
        TreeSet<PredicateAbstract> groundValues = this.getGroundValuesANTLR(groundValueFile);

        // add ground values to the model.
        ArrayList<PredicateAbstract> remainingGroundValues = this.integrateGroundValuesInModel(groundValues, model);

        // integrate the hidden predicate values.
        // here, we do just need to integrate them, no changes as in function getModel()!
        this.integrateResultsInModel(remainingGroundValues, model);

        // figures out the ground values for types and assigns a new predicate to each type containing these ground values.
        this.assignTypePredicates(model);
        return model;
    }


    public Model getModelForEvaluation(String modelFile, String groundValueFile, String resultFile) throws IOException, RecognitionException, ParseException
    {
        Model model = this.getModel(modelFile, groundValueFile);

        TreeSet<PredicateAbstract> resultValues = this.getGroundValuesANTLR(resultFile);
        if(resultValues != null && resultValues.size() > 0) {
            this.integrateResultsInModel(resultValues, model);
        }

        // some preprocessing (simplifying conjunctions, etc)
        this.preprocessing(model);

        return model;
    }


    private ArrayList<PredicateAbstract> integrateResultsInModel(Collection<PredicateAbstract> results, Model model) throws ParseException
    {
        HashSet<PredicateAbstract> hiddenPredicatesHash = model.getAllHiddenPredicates();
        TreeSet<PredicateAbstract> hiddenPredicates = new TreeSet<PredicateAbstract>();
        for(PredicateAbstract p : hiddenPredicatesHash) {
            hiddenPredicates.add(p);
        }

        ArrayList<PredicateAbstract> remainingGroundValues = new ArrayList<PredicateAbstract>();

        // integrate those predicates which are observed.
        for(PredicateAbstract groundValue : results) {
            if(hiddenPredicates.contains(groundValue)) {
                PredicateAbstract observedPredicate = hiddenPredicates.ceiling(groundValue);
                observedPredicate.setGroundValues(groundValue.getGroundValues());
            } else {
                remainingGroundValues.add(groundValue);
            }
        }

        return remainingGroundValues;
    }


    private Model getModelANTLR(String filename) throws IOException, RecognitionException, ParseException
    {
        // Create an input character stream from standard in
        FileInputStream in = new FileInputStream(new File(filename));
        ANTLRInputStream input = new ANTLRInputStream(in);
        // Create an ModelLexer that feeds from that stream
        ModelLexer lexer = new ModelLexer(input);
        // Create a stream of tokens fed by the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Create a parser that feeds off the token stream
        ModelParser parser = new ModelParser(tokens);
        // return model
        Model m = parser.model();
        return m;
    }


    private TreeSet<PredicateAbstract> getGroundValuesANTLR(String filename) throws IOException, RecognitionException, ParseException
    {
        // Create an input character stream from standard in
        FileInputStream in = new FileInputStream(new File(filename));
        ANTLRInputStream input = new ANTLRInputStream(in);
        // Create an GroundValueLexer that feeds from that stream
        GroundValuesLexer lexer = new GroundValuesLexer(input);
        // Create a stream of tokens fed by the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // Create a parser that feeds off the token stream
        GroundValuesParser parser = new GroundValuesParser(tokens);
        // return model
        return parser.groundValues();

    }


    /**
     * If only one positive hidden predicate exist in soft formula and if the double variable is not null, it is transformed to an objective formula.
     * 
     * @return
     * @throws ParseException
     */
    private void transformSoftToObjectiveFormulas(Model model) throws ParseException
    {
        ArrayList<FormulaAbstract> formulastoRemove = new ArrayList<FormulaAbstract>();
        ArrayList<FormulaAbstract> formulastoAdd = new ArrayList<FormulaAbstract>();

        for(FormulaAbstract f : model.getFormulas()) {
            if(f instanceof FormulaSoft) {
                FormulaSoft fs = (FormulaSoft) f;
                if(fs.getRestrictions().size() == 1 && fs.getRestrictions().get(0).isPositive() && fs.getDoubleVariable() != null) {
                    formulastoRemove.add(f);
                    FormulaObjective fo = new FormulaObjective(fs.getName(), fs.getForVariables(), fs.getIfExpressions(), fs.getDoubleVariable(), fs.getRestrictions().get(0));
                    formulastoAdd.add(fo);
                }
            }
        }
        for(FormulaAbstract f : formulastoRemove) {
            model.removeFormula(f);
        }
        for(FormulaAbstract f : formulastoAdd) {
            model.addFormula(f);
        }
    }


    /**
     * Figures out the ground values for types and assigns a new predicate to each type containing these ground values.
     * 
     * @param groundValues
     * @param model
     * @return
     * @throws ParseException
     */
    private ArrayList<PredicateAbstract> integrateGroundValuesInModel(TreeSet<PredicateAbstract> groundValues, Model model) throws ParseException
    {
        HashSet<PredicateAbstract> observedPredicatesHash = model.getAllObservedPredicates();
        TreeSet<PredicateAbstract> observedPredicates = new TreeSet<PredicateAbstract>();
        for(PredicateAbstract p : observedPredicatesHash) {
            p.setGroundValues(new ArrayList<String[]>());
            observedPredicates.add(p);
        }

        ArrayList<PredicateAbstract> remainingGroundValues = new ArrayList<PredicateAbstract>();

        // integrate those predicates which are observed.
        // Negative groundings do not have any influence here.
        if(groundValues == null) { throw new ParseException("No ground values have been found."); }
        for(PredicateAbstract groundValue : groundValues) {
            if(observedPredicates.contains(groundValue)) {
                PredicateAbstract observedPredicate = observedPredicates.ceiling(groundValue);
                if(observedPredicate instanceof PredicateDouble && groundValue instanceof PredicateDouble) {
                    ((PredicateDouble) observedPredicate).setDoubleValues(((PredicateDouble) groundValue).getDoubleValues());
                }
                observedPredicate.setGroundValues(groundValue.getGroundValues());
            } else {
                remainingGroundValues.add(groundValue);
            }
        }

        return remainingGroundValues;
    }


    private Model integrateHiddenPredicateValuesWithNewFormula(Model model, ArrayList<PredicateAbstract> remainingGroundValues) throws ParseException
    {

        ArrayList<PredicateAbstract> remainingPositiveGroundValues = new ArrayList<PredicateAbstract>();
        ArrayList<PredicateAbstract> remainingNegativeGroundValues = new ArrayList<PredicateAbstract>();
        // introduce a hard formula and another predicate for those who are part of the hidden predicates

        for(PredicateAbstract p : remainingGroundValues) {
            if(p.getName().startsWith("!")) {
                String name = p.getName().replace("!", "");
                p.setName(name);
                remainingNegativeGroundValues.add(p);
            } else {
                remainingPositiveGroundValues.add(p);
            }
        }

        if(remainingPositiveGroundValues.size() > 0) {
            model = this.createNewHiddenPredFormula(model, remainingPositiveGroundValues, true);
        }
        if(remainingNegativeGroundValues.size() > 0) {
            model = this.createNewHiddenPredFormula(model, remainingNegativeGroundValues, false);
        }

        return model;
    }


    private Model createNewHiddenPredFormula(Model model, ArrayList<PredicateAbstract> remainingGroundValues, boolean positive) throws ParseException
    {

        HashSet<PredicateAbstract> hiddenPreds = model.getAllHiddenPredicates();
        for(PredicateAbstract hiddenPred : hiddenPreds) {

            int index = remainingGroundValues.indexOf(hiddenPred);
            if(index >= 0) {
                PredicateAbstract groundValue = remainingGroundValues.get(index);
                remainingGroundValues.remove(index);

                // generate new observed Predicate and formular for the hidden one.
                // 1. forVariables for formular
                HashSet<VariableType> forVariables = new HashSet<VariableType>();
                int variableId = 0;
                for(Type t : hiddenPred.getTypes()) {
                    forVariables.add(new VariableType(("v" + variableId), ((Type) t)));
                    variableId++;
                }

                PredicateAbstract newObservedPred = null;
                FormulaAbstract formular = null;
                if(hiddenPred instanceof PredicateDouble) {
                    StringBuffer name = new StringBuffer();
                    name.append(hiddenPred.getName()).append("_observed");
                    if(!positive) {
                        name.append("Neg");
                    }

                    newObservedPred = new PredicateDouble(name.toString(), false, hiddenPred.getTypes(), groundValue.getGroundValues());

                    VariableDouble ddd = new VariableDouble("ddd");
                    ArrayList<VariableAbstract> varList = new ArrayList<VariableAbstract>();
                    for(VariableAbstract var : forVariables) {
                        if(var instanceof VariableAbstract) {
                            varList.add((VariableAbstract) var);
                        }
                    }
                    FormulaSoft formularSoft = new FormulaSoft();
                    formularSoft.useCuttingPlaneInference(false);
                    formularSoft.addIfExpression(new PredicateExpression(true, newObservedPred, varList));
                    name.append("F");
                    formularSoft.setName(name.toString());
                    formularSoft.setForVariables(forVariables);
                    formularSoft.setDoubleVariable(ddd);
                    formularSoft.setRestrictions(new PredicateExpression(positive, hiddenPred, varList));
                    formular = formularSoft;
                } else if(hiddenPred instanceof Predicate) {
                    StringBuffer name = new StringBuffer();
                    name.append(hiddenPred.getName()).append("_observed");
                    if(!positive) {
                        name.append("Neg");
                    }
                    newObservedPred = new Predicate(name.toString(), false, hiddenPred.getTypes(), groundValue.getGroundValues());

                    ArrayList<VariableAbstract> varList = new ArrayList<VariableAbstract>();
                    for(VariableType var : forVariables) {
                        if(var instanceof VariableAbstract) {
                            varList.add((VariableAbstract) var);
                        }
                    }
                    FormulaHard formularH = new FormulaHard();
                    formularH.addIfExpression(new PredicateExpression(true, newObservedPred, varList));
                    name.append("F");
                    formularH.setName(name.toString());
                    formularH.setForVariables(forVariables);
                    formularH.setRestrictions(new PredicateExpression(positive, hiddenPred, varList));
                    formular = formularH;
                }
                model.addFormula(formular);
            }
        }
        return model;
    }


    /**
     * Figures out the groundings of all types and assignes ground values to the types. This is done by creation of a dummy predicate
     * per type.
     * 
     * @param model
     * @throws ParseException
     */
    private void assignTypePredicates(Model model) throws ParseException
    {
        HashSet<Type> allTypes = model.getAllTypes();
        // initialize hashmap
        HashMap<String, HashSet<String>> typePredicateValues = new HashMap<String, HashSet<String>>();
        for(Type t : allTypes) {
            typePredicateValues.put(t.getName(), new HashSet<String>());
        }

        // iterate over all groundValues and fill non dublicates into the hashmap
        for(PredicateAbstract p : model.getAllObservedPredicates()) {
            Type[] a = new Type[0];
            Type[] typeArray = p.getTypes().toArray(a);

            for(String[] groundValueArray : p.getGroundValues()) {
                for(int i = 0; i < groundValueArray.length; i++) {
                    Type t = typeArray[i];
                    HashSet<String> value = typePredicateValues.get(t.getName());
                    value.add(groundValueArray[i]);
                }
            }
        }

        // create ground value predicates and their corresponding ground values
        for(String predName : typePredicateValues.keySet()) {
            for(Type t : allTypes) {
                if(predName.equals(t.getName())) {
                    ArrayList<String[]> finalValues = new ArrayList<String[]>();
                    for(String val : typePredicateValues.get(predName)) {
                        String[] sArray = new String[1];
                        sArray[0] = val;
                        finalValues.add(sArray);
                    }
                    // if we have already a type predicate, we use the existing one
                    Predicate typePredicate = t.getGroundValuesPredicate();
                    if(typePredicate == null) {
                        typePredicate = new Predicate(predName + "_typePred", false, t);
                        t.setGroundValuesPredicate(typePredicate);
                    }
                    typePredicate.setGroundValues(finalValues);

                }
            }
        }

    }

    /**
     * Processes string variables - transform them to extra observed predicates and add them to types.
     * 
     * If predicates are hidden and if variables are StringVariables, they are transformed as followed:
     * 
     * Example:
     * Original:
     * box(b,b)
     * !box(x,y) v box(x,"white").
     * 
     * Transformed:
     * box(b, b)
     * varString0(varString0)
     * 
     * !varString0(varString0) v !box(x, y) v box(x, varString0)
     * 
     * @throws ParseException
     */
    private int processStringVariableCounter = 0;


    private void processStringVariables(Model model) throws ParseException
    {
        if(Parameters.CONVERT_STRING_VALUES) {
            for(FormulaAbstract f : model.getFormulas()) {
                if(f instanceof FormulaHard) {
                    FormulaHard fh = (FormulaHard) f;
                    // get all observed variables and add string constants to types
                    for(IfExpression exprIf : fh.getIfExpressions()) {
                        if(exprIf instanceof PredicateExpression) {
                            PredicateExpression expr = (PredicateExpression) exprIf;
                            ArrayList<VariableAbstract> vars = expr.getVariables();
                            PredicateAbstract predA = expr.getPredicate();
                            ArrayList<Type> types = predA.getTypes();
                            for(int i = 0; i < vars.size(); i++) {
                                VariableAbstract var = vars.get(i);
                                if(var instanceof VariableString) {
                                    Type t = types.get(i);
                                    // add string constant to types
                                    t.getGroundValues().add(new String[] { var.getName() });
                                }
                            }
                        }
                    }

                    // get all hidden variables and add string constants to types
                    for(PredicateExpression expr : fh.getRestrictions()) {
                        ArrayList<VariableAbstract> vars = expr.getVariables();
                        PredicateAbstract predA = expr.getPredicate();
                        ArrayList<Type> types = predA.getTypes();
                        for(int i = 0; i < vars.size(); i++) {
                            VariableAbstract var = vars.get(i);
                            if(var instanceof VariableString) {
                                Type t = types.get(i);
                                // add string constant to types
                                t.getGroundValues().add(new String[] { var.getName() });
                                // if hidden predicate expression is positive:
                                // - transform
                                // box(b,b)
                                // !box(x,y) v box(x,"white").
                                // - to:
                                // * b_typePred(b)
                                // * box_observed(b, b)
                                // * neww0(neww0)
                                // box(b, b)
                                // !neww0(neww0) v !box(x, y) v box(x, neww0).

                                // pred(..., newVar, ...)
                                // newVar -> newType
                                // newType.groundValues("Hello")
                                if(Parameters.CONVERT_STRING_VALUES && expr.isPositive()) {
                                    String name = new StringBuilder().append("varString").append(processStringVariableCounter).toString();
                                    Type newType = new Type(name);
                                    Predicate newPred = new Predicate(name, false, newType);
                                    newPred.addGroundValueLine(var.getName());
                                    newType.setGroundValuesPredicate(newPred);
                                    VariableType newVar = new VariableType(name, newType);
                                    vars.set(i, newVar);
                                    model.addPredicate(newPred);
                                    f.addIfExpression(new PredicateExpression(true, newPred, newVar));
                                    f.getForVariables().add(newVar);
                                    processStringVariableCounter++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    /**
     * Figures out which variables are not yet bound in formulas and add the "type predicate" of this variable to the if restriction.
     * 
     * @param model
     * @throws ParseException
     */
    private void addUnboundVariablesPredicateTypes(Model model) throws ParseException
    {
        for(FormulaAbstract formularAbstract : model.getFormulas()) {
            if(formularAbstract instanceof FormulaHard || formularAbstract instanceof FormulaSoft || formularAbstract instanceof FormulaCardinality) {
                FormulaHard formular = (FormulaHard) formularAbstract;

                // get bound variables
                HashSet<VariableAbstract> boundVariables = new HashSet<VariableAbstract>();
                HashSet<VariableAbstract> unboundVariableTypes = new HashSet<VariableAbstract>();

                for(IfExpression ifE : formular.getIfExpressions()) {
                    if(ifE instanceof PredicateExpression) {
                        PredicateExpression expr = (PredicateExpression) ifE;
                        if(expr.isPositive()) {
                            for(VariableAbstract var : expr.getAllVariables()) {
                                boundVariables.add(var);
                            }
                        } else {
                            for(VariableAbstract var : expr.getAllVariables()) {
                                if(!boundVariables.contains(var)) {
                                    unboundVariableTypes.add(var);
                                    boundVariables.add(var);
                                }
                            }
                        }
                    }
                }

                // get unbound variables
                for(PredicateExpression expr : formular.getRestrictions()) {
                    for(VariableAbstract var : expr.getVariables()) {
                        if(boundVariables.contains(var)) {
                            // do nothing, variable is bound.
                        } else {
                            unboundVariableTypes.add(var);
                            boundVariables.add(var);
                        }
                    }
                }

                // add ground value predicates of unbound types to if expression of formular
                for(VariableAbstract varAbstr : unboundVariableTypes) {
                    if(varAbstr instanceof VariableType) {
                        VariableType var = (VariableType) varAbstr;
                        Type t = var.getType();
                        if(t.getGroundValuesPredicate() == null) {
                            Predicate typePredicate = new Predicate(t.getName() + "_typePred", false, t);
                            t.setGroundValuesPredicate(typePredicate);
                        }
                        Predicate groundValuesP = t.getGroundValuesPredicate();
                        formular.addIfExpression(new PredicateExpression(true, groundValuesP, varAbstr));

                    }
                }
            }
        }
    }


    /**
     * Performs some preprocessing.
     * 
     * For example:
     * - If hard formulas with conjunctions exists, split them up into new formulas
     * - If a soft formula has the weight 0 the formular is deleted.
     * - Disable CPI if parameter is set
     * - simplify negative weights and conjunction if parameter is set.
     * 
     * @param model
     * @throws ParseException
     */
    private Model preprocessing(Model model) throws ParseException
    {
        // - simplify negative weights if parameter is set.
        if(Parameters.SIMPLIFY_NEGATIVE_WEIGHT_AND_CONJUNCTION) {
            for(FormulaAbstract f : model.getFormulas()) {
                if(f instanceof FormulaSoft && ((FormulaSoft) f).getRestrictions().size() > 1) {
                    FormulaSoft fs = (FormulaSoft) f;
                    if(fs.getWeight() != null) {
                        double weight = fs.getWeight();
                        if(weight < 0) {
                            fs.setWeight((weight * (-1)));
                            for(PredicateExpression e : fs.getRestrictions()) {
                                e.setPositive(!e.isPositive());
                            }
                            if(fs.isConjunction()) {
                                fs.setDisjunction();
                            } else {
                                fs.setConjunction();
                            }
                        }
                    }
                }
            }
        }
        ArrayList<FormulaAbstract> newFormulas = new ArrayList<FormulaAbstract>();
        ArrayList<FormulaAbstract> formulasToRemove = new ArrayList<FormulaAbstract>();

        // - If hard formulas with conjunctions exists, split them up into new formulas
        // - If a soft formula has the weight 0 the formular is deleted.
        for(FormulaAbstract formularAbstract : model.getFormulas()) {
            if(formularAbstract.getClass().equals(FormulaHard.class)) {
                FormulaHard formular = (FormulaHard) formularAbstract;
                if(formular.isConjunction()) {
                    int i = 0;
                    for(PredicateExpression expr : formular.getRestrictions()) {
                        FormulaHard newHard = new FormulaHard((formular.getName() + i), formular.getForVariables(), formular.getIfExpressions(), new ArrayList<PredicateExpression>(), false);
                        newHard.setRestrictions(expr);
                        newHard.useCuttingPlaneInference(formular.isCuttingPlaneInferenceUsed());
                        newFormulas.add(newHard);
                        i++;
                    }
                    formulasToRemove.add(formular);
                }
            } /*
               * else if(formularAbstract instanceof FormulaSoft){
               * FormulaSoft s = (FormulaSoft) formularAbstract;
               * double value = 1;
               * for(Weight w : s.getWeights()){
               * value = value* w.getValue();
               * }
               * if(s.getDoubleVariable()==null && value==0d){
               * formulasToRemove.add(s);
               * }
               * }
               */
        }

        // - simplify conjunction if parameter is set.
        if(Parameters.SIMPLIFY_NEGATIVE_WEIGHT_AND_CONJUNCTION) {
            for(FormulaAbstract f : model.getFormulas()) {
                if(f instanceof FormulaSoft && ((FormulaSoft) f).isConjunction()) {
                    FormulaSoft fs = (FormulaSoft) f;
                    double denominator = 1 / ((double) fs.getRestrictions().size());

                    int i = 0;
                    if(fs.getRestrictions().size() > 1) {
                        for(PredicateExpression expr : fs.getRestrictions()) {
                            Double weight = fs.getWeight() * denominator;
                            FormulaSoft newSoft = new FormulaSoft((fs.getName() + "simplified" + i), fs.getForVariables(), fs.getIfExpressions(), fs.getDoubleVariable(), new ArrayList<PredicateExpression>(), false);
                            newSoft.setWeight(weight);
                            newSoft.setRestrictions(expr);
                            newSoft.useCuttingPlaneInference(fs.isCuttingPlaneInferenceUsed());
                            newFormulas.add(newSoft);
                            i++;
                        }
                        formulasToRemove.add(fs);
                    }
                }
            }
        }
        for(FormulaAbstract f : newFormulas) {
            model.addFormula(f);
        }
        for(FormulaAbstract f : formulasToRemove) {
            model.removeFormula(f);
        }

        // - Disable CPI if parameter is set
        if(!Parameters.USE_CUTTING_PLANE_INFERENCE) {
            for(FormulaAbstract f : model.getFormulas()) {
                f.useCuttingPlaneInference(false);
            }
        }

        /*
         * for(FormulaAbstract f : model.getFormulas()){
         * if(f instanceof FormulaSoft && ((FormulaSoft) f).getRestrictions().size()<=1){
         * f.useCuttingPlaneInference(false);
         * }
         * }
         */
        return model;
    }


    private void processNumericalPredicates(Model model)
    {
        Map<String, Map<String, Integer>> mathPredPositions = new HashMap<>();
        Map<String, MathExpression> mathPredEx = new HashMap<>();
        for(PredicateNumerical pred : model.getAllNumericalPredicates()) {
            Map<String, Integer> varPosition = new HashMap<>();
            for(int i = 0; i < pred.getVariables().size(); i++) {
                varPosition.put(pred.getVariables().get(i), i);
            }
            mathPredPositions.put(pred.getName(), varPosition);
            mathPredEx.put(pred.getName(), pred.getMathEx());
        }

        MathExpression.setMathPredPositions(mathPredPositions);
        MathExpression.setMathPredEx(mathPredEx);

        // for(String pred : mathPredPositions.keySet()) {
        // System.out.println(pred);
        // for(String v : mathPredPositions.get(pred).keySet()) {
        // System.out.println("\t" + v + " -> " + mathPredPositions.get(pred).get(v));
        // }
        // System.out.println(mathPredEx.get(pred));
        // System.out.println("=======================");
        // }
    }


    private void processRevelevantVariables(Model model)
    {
        for(FormulaAbstract fT : model.getFormulas()) {
            if(!(fT instanceof FormulaHard)) {
                continue;
            }
            FormulaHard f = (FormulaHard) fT;
            Set<IfExpression> atoms = new HashSet<IfExpression>();
            atoms.addAll(f.getIfExpressions());
            atoms.addAll(f.getRestrictions());

            Set<String> variables = new HashSet<String>();
            Set<PredicateExpression> predexAE = new HashSet<PredicateExpression>();

            // collect all variables and atoms having algebraic expressions
            for(IfExpression a : atoms) {
                PredicateExpression predicate = (PredicateExpression) a;
                for(VariableAbstract var : a.getAllVariables()) {
                    if(var instanceof VariableType) {
                        variables.add(var.getName());
                    } else if(var instanceof VariableArithmeticExpression) {
                        predexAE.add(predicate);
                    }
                }
            }

            // detect irrelevant variables
            for(PredicateExpression a : predexAE) {
                for(VariableAbstract var : a.getAllVariables()) {
                    if((var instanceof VariableType) && (variables.contains(var.getName()))) {
                        var.setAddToWhere(false);
                    }
                }
            }
        }
    }
}
