package com.googlecode.rockit.javaAPI;

import java.util.ArrayList;
import java.util.HashSet;

import com.googlecode.rockit.javaAPI.formulas.FormulaAbstract;
import com.googlecode.rockit.javaAPI.formulas.FormulaCardinality;
import com.googlecode.rockit.javaAPI.formulas.FormulaHard;
import com.googlecode.rockit.javaAPI.formulas.FormulaObjective;
import com.googlecode.rockit.javaAPI.formulas.FormulaSoft;
import com.googlecode.rockit.javaAPI.formulas.expressions.IfExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.EqualVariableExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.PredicateExpression;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.ThresholdExpression;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableDouble;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.VariableType;
import com.googlecode.rockit.javaAPI.predicates.PredicateAbstract;
import com.googlecode.rockit.javaAPI.predicates.PredicateNumerical;
import com.googlecode.rockit.javaAPI.types.Type;


public class Model
{
    private ArrayList<FormulaAbstract> formulars          = new ArrayList<FormulaAbstract>();

    private HashSet<Type>              types              = new HashSet<Type>();

    private HashSet<PredicateAbstract> observedPredicates = new HashSet<PredicateAbstract>();

    private HashSet<PredicateAbstract> hiddenPredicates   = new HashSet<PredicateAbstract>();

    private HashSet<PredicateAbstract> initialSolution    = new HashSet<PredicateAbstract>();


    public Model()
    {
    }


    public Model(ArrayList<FormulaAbstract> formulars)
    {
        this.addFormulas(formulars);
    }


    public Model(FormulaAbstract... formulars)
    {
        this.addFormulas(formulars);
    }


    public void addPredicate(PredicateAbstract predicate)
    {
        if(predicate.isHidden()) {
            hiddenPredicates.add(predicate);
        } else {
            observedPredicates.add(predicate);
        }
        for(Type t : predicate.getTypes()) {
            types.add(t);
        }
    }


    public void setInitialSolution(HashSet<PredicateAbstract> initialSolution)
    {
        this.initialSolution = initialSolution;
    }


    public HashSet<PredicateAbstract> getInitialSolution()
    {
        return initialSolution;
    }


    public void addType(Type type)
    {
        types.add(type);
    }


    public void addFormulas(FormulaAbstract... formulars)
    {
        for(int i = 0; i < formulars.length; i++) {
            this.addFormula(formulars[i]);
        }
    }


    public void removeFormula(FormulaAbstract formula)
    {
        formulars.remove(formula);
    }


    public void addFormulas(ArrayList<FormulaAbstract> formulars)
    {
        for(FormulaAbstract f : formulars) {
            this.addFormula(f);
        }
    }


    public void addFormula(FormulaAbstract formular)
    {
        this.hiddenPredicates.addAll(formular.getAllHiddenPredicatesSet());
        this.observedPredicates.addAll(formular.getAllObservedPredicates());
        this.types.addAll(formular.getAllTypes());

        this.formulars.add(formular);
    }


    public ArrayList<FormulaAbstract> getFormulas()
    {
        return formulars;
    }


    public HashSet<Type> getAllTypes()
    {
        return types;

    }


    public HashSet<PredicateAbstract> getAllObservedPredicates()
    {
        for(Type t : this.types) {
            if(t.getGroundValuesPredicate() != null) {
                this.observedPredicates.add(t.getGroundValuesPredicate());
            }
        }

        return this.observedPredicates;
    }


    public HashSet<PredicateAbstract> getAllHiddenPredicates()
    {
        return this.hiddenPredicates;
    }


    public HashSet<PredicateNumerical> getAllNumericalPredicates()
    {
        HashSet<PredicateNumerical> preds = new HashSet<>();
        for(PredicateAbstract p : hiddenPredicates) {
            if(p instanceof PredicateNumerical) {
                preds.add((PredicateNumerical) p);
            }
        }
        return preds;
    }


    public String toLongString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(this.toString());

        sb.append(this.toStringGroundValues());

        return sb.toString();
    }


    public String toStringGroundValues()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n// ground axioms\n");
        // getAllPredicates & their values
        HashSet<PredicateAbstract> observedPredicates = new HashSet<PredicateAbstract>();
        HashSet<PredicateAbstract> hiddenPredicates = new HashSet<PredicateAbstract>();

        observedPredicates = this.getAllObservedPredicates();
        hiddenPredicates = this.getAllHiddenPredicates();

        for(PredicateAbstract p : observedPredicates) {
            sb.append(p.toLongString()).append("\n");
        }
        for(PredicateAbstract p : hiddenPredicates) {
            sb.append(p.toLongString()).append("\n");
        }
        return sb.toString();
    }


    public String toTheBeastWeightString()
    {
        StringBuilder sb = new StringBuilder();
        for(FormulaAbstract f : this.getFormulas()) {
            if(f instanceof FormulaSoft) {
                // >wf1
                // -0.5
                sb.append("\n>w").append(f.getName());
                sb.append("\n").append(((FormulaSoft) f).getWeight());
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    private String toTheBestAtomsString(HashSet<PredicateAbstract> predicates)
    {
        StringBuilder sb = new StringBuilder();
        for(PredicateAbstract p : predicates) {
            sb.append(">").append(p.getName().toLowerCase()).append("\n");
            for(String[] gv : p.getGroundValues()) {
                for(String g : gv) {
                    try {
                        Double.parseDouble(g);
                        sb.append(g);
                    } catch(NumberFormatException e) {
                        // not a double
                        sb.append("\"").append(g).append("\"");
                    }
                    sb.append(" ");
                }
                sb.append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    public String toTheBeastAtomsString()
    {
        StringBuilder sb = new StringBuilder();
        // >cmap
        // "hallo" "hallo2" 0.5
        sb.append(">>\n\n");

        sb.append(this.toTheBestAtomsString(this.getAllObservedPredicates()));
        sb.append(this.toTheBestAtomsString(this.getAllHiddenPredicates()));

        return sb.toString();
    }


    public String toTheBeastMLNString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n//types");
        for(Type t : this.getAllTypes()) {
            sb.append("\ntype ");
            sb.append(t.getName()).append(": ...;");
        }
        sb.append("\n\n//predicates\n");
        for(PredicateAbstract p : this.getAllHiddenPredicates()) {
            sb.append(p.toTheBeastString());
            sb.append("\n");
        }
        for(PredicateAbstract p : this.getAllObservedPredicates()) {
            sb.append(p.toTheBeastString());
            sb.append("\n");
        }
        sb.append("\n\n//hidden vs. observed");
        sb.append("\nhidden: ");
        boolean first = true;
        for(PredicateAbstract p : this.getAllHiddenPredicates()) {
            if(!first)
                sb.append(", ");
            first = false;
            sb.append(p.toString().toLowerCase());
        }
        sb.append(";");
        sb.append("\nobserved: ");
        first = true;
        for(PredicateAbstract p : this.getAllObservedPredicates()) {
            if(!first)
                sb.append(", ");
            first = false;
            sb.append(p.toString().toLowerCase());
        }
        sb.append(";");
        sb.append("\n\n//weights");
        for(FormulaAbstract f : this.getFormulas()) {
            if(f instanceof FormulaSoft) {
                sb.append("\nweight w").append(f.getName()).append(": Double;");
            }
        }
        sb.append("\n\n//formulas: ");

        for(FormulaAbstract f : this.getFormulas()) {
            // for part
            sb.append("\nfactor: for ");
            first = true;
            for(VariableType v : f.getForVariables()) {
                if(!first)
                    sb.append(", ");
                first = false;
                sb.append(v.getType().getName()).append(" ");
                sb.append(v.getName());
            }
            if(f instanceof FormulaSoft) {
                VariableDouble v = ((FormulaSoft) f).getDoubleVariable();
                if(v == null) {
                    // do nothing
                } else {
                    sb.append(", Double ").append(v.getName());
                }
            } else if(f instanceof FormulaObjective) {
                VariableDouble v = ((FormulaObjective) f).getDoubleVariable();
                sb.append(", Double ").append(v.getName());
            }
            // if part
            StringBuilder ifString = new StringBuilder();
            ifString.append("\nif ");
            first = true;
            for(IfExpression e : f.getIfExpressions()) {
                if(e instanceof ThresholdExpression) {
                    System.err.println("Threshold Expressions are not supported in TheBeast " + e + ". Rewrite formula f " + f);
                } else if(e instanceof PredicateExpression) {
                    if(!first)
                        ifString.append(" & ");
                    first = false;
                    PredicateExpression pe = (PredicateExpression) e;
                    ifString.append(pe.toTheBeastString(false));
                    // extend for part with equalString Variables
                    for(String forHelper : pe.getTheBeastForExtension()) {
                        sb.append(", ").append(forHelper);
                    }
                } else if(e instanceof EqualVariableExpression) {
                    System.err.println("Equal Variable Expressions are not supported in TheBeast " + e + ". Rewrite formula f " + f);
                }
            }

            // append if part from above
            sb.append(ifString.toString());

            // restriction part
            if(f instanceof FormulaSoft) {
                FormulaSoft fs = (FormulaSoft) f;
                sb.append("\nadd [");
                if(fs.getRestrictions().size() > 1) {
                    for(int i = 0; i < fs.getRestrictions().size(); i++) {
                        PredicateExpression p = fs.getRestrictions().get(i);
                        if(i > 0 && i < fs.getRestrictions().size() - 1) {
                            sb.append(" & ");
                            sb.append(p.toTheBeastString(true));
                        } else if(i > 0 && i == fs.getRestrictions().size() - 1) {
                            sb.append(" => ");
                            sb.append(p.toTheBeastString(false));
                        } else {
                            sb.append(p.toTheBeastString(true));
                        }
                    }
                } else {
                    PredicateExpression p = fs.getRestrictions().get(0);
                    sb.append(p.toTheBeastString(false));
                }
                sb.append("] * ");
                VariableDouble v = fs.getDoubleVariable();
                if(v == null) {
                    sb.append("w").append(f.getName());
                } else {
                    sb.append(v.getName());
                }

            } else if(f instanceof FormulaCardinality) {
                sb.append("\n");
                FormulaCardinality fc = (FormulaCardinality) f;
                if(fc.getOverVariables().size() == 1) {
                    for(VariableType v : fc.getOverVariables()) {
                        sb.append("|").append(v.getType().getName()).append(" ").append(v.getName()).append(": ");
                    }

                } else {
                    System.err.println("More than one over variable is not supported in TheBeast . Rewrite formula f " + f);
                }
                if(fc.getRestrictions().size() == 1) {
                    for(PredicateExpression p : fc.getRestrictions()) {
                        sb.append(p.toTheBeastString(false));
                    }
                    sb.append("|");
                } else {
                    System.err.println("More than one restriction is not supported in card constraints in TheBeast . Rewrite formula f " + f);
                }
            } else if(f instanceof FormulaHard) {
                sb.append(":\n");
                FormulaHard fh = (FormulaHard) f;
                if(fh.getRestrictions().size() > 1) {
                    for(int i = 0; i < fh.getRestrictions().size(); i++) {
                        PredicateExpression p = fh.getRestrictions().get(i);
                        if(i > 0 && i < fh.getRestrictions().size() - 1) {
                            sb.append(" & ");
                            sb.append(p.toTheBeastString(true));
                        } else if(i > 0 && i == fh.getRestrictions().size() - 1) {
                            sb.append(" => ");
                            sb.append(p.toTheBeastString(false));
                        } else {
                            sb.append(p.toTheBeastString(true));
                        }
                    }
                } else {
                    PredicateExpression p = fh.getRestrictions().get(0);
                    sb.append(p.toTheBeastString(false));
                }
            } else if(f instanceof FormulaObjective) {
                sb.append("\n");
                FormulaObjective fo = (FormulaObjective) f;
                sb.append("add [");
                PredicateExpression p = fo.getObjectiveExpression();
                sb.append(p.toTheBeastString(false));
                sb.append("] * ").append(fo.getDoubleVariable().getName());
            }
            sb.append(";\n\n");
        }

        return sb.toString();
    }


    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        // getAllTypes
        /*
         * sb.append("//types\n");
         * for(Type t: this.getAllTypes()){
         * sb.append(t.toMediumString()).append("\n");
         * }
         */

        sb.append("\n//predicates\n");
        // getAllPredicates
        HashSet<PredicateAbstract> observedPredicates = new HashSet<PredicateAbstract>();
        HashSet<PredicateAbstract> hiddenPredicates = new HashSet<PredicateAbstract>();
        observedPredicates = this.getAllObservedPredicates();
        hiddenPredicates = this.getAllHiddenPredicates();
        for(PredicateAbstract p : observedPredicates) {
            sb.append(p.toMediumString()).append("\n");
        }
        for(PredicateAbstract p : hiddenPredicates) {
            sb.append(p.toMediumString()).append("\n");
        }
        /*
         * HashSet<Weight> weights = this.getAllWeights();
         * if(weights.size()>0){
         * sb.append("\n//weights\n");
         * for(Weight weight:weights){
         * sb.append(weight.toString()).append("\n");
         * }
         * }
         */

        sb.append("\n//formulars\n");
        for(FormulaAbstract formular : this.formulars) {
            sb.append(formular.toString());
        }
        return sb.toString();
    }
}
