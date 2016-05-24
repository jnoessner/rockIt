grammar Model;

// START:members
@header {
package com.googlecode.rockit.parser;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import com.googlecode.rockit.javaAPI.*;
import com.googlecode.rockit.javaAPI.predicates.*;
import com.googlecode.rockit.javaAPI.types.*;
import com.googlecode.rockit.javaAPI.formulas.*;
import com.googlecode.rockit.javaAPI.formulas.expressions.*;
import com.googlecode.rockit.javaAPI.formulas.expressions.impl.*;
import com.googlecode.rockit.javaAPI.formulas.variables.impl.*;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.Messages;
import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.solver.numerical.*;
}
@lexer::header{ 
package com.googlecode.rockit.parser; 
} 

@members {
/** Map variable name to Integer object holding value */
TreeSet<Type> types = new TreeSet<Type>();
TreeSet<PredicateAbstract> predicates = new TreeSet<PredicateAbstract>();
int formulaId = 1;
HerbrandUniverse u = HerbrandUniverse.getInstance();
}
// END:members

/**
A rockIt model consists of Soft-, Hard-, and Cardinalityformulas. Comments in the file (starting with '//' or enclosing text with '\*' and '*\') and newlines are ignored
*/

model returns [Model model = new Model()] throws ParseException:   
  (
   (ML_COMMENT) | (SL_COMMENT) | (NEWLINE) |
   (pre=predicateDefinition{model.addPredicate($pre.predicate);}) |
   (sf=softFormulaDefinition{FormulaSoft formula = $sf.formula;
                         if(formula.getRestrictions().size()==0){
                           // drop formula
                         }else{
                           model.addFormula(formula);
                         }
                     }) |
   (hf=hardFormulaDefinition{FormulaHard formula = $hf.formula;
                         if(formula.getRestrictions().size()==0){
                           // drop formula
                         }else{
                           model.addFormula(formula);
                         }
                     }) |
   (cf=cardinalityFormulaDefiniton{FormulaCardinality formula = $cf.formula;
                         if(formula.getRestrictions().size()==0){
                           // drop formula
                         }else{
                           model.addFormula(formula);
                         }
                     })
  )
  + ;
  
/**
Soft formulas are formulas which are true to a certain weight. There are two possibilities how to define this weight: 
1. The weight is a concrete float value (like 0.3) meaning that the whole formula has the weight 0.3.  or a double variable followed by a ':'. 
2. The weight is a double variable meaning that the formula gets the weight of every individual ground value.
*/        
softFormulaDefinition returns [FormulaSoft formula] throws ParseException:
    {
        Double w = null;
        VariableDouble doubleVariable = null;
    }
    ((f=FLOAT){
        if($f != null){
          w = Double.parseDouble($f.getText());
        }
     }
    | (dv=ID ':'){
        if($dv !=null){
          doubleVariable = new VariableDouble($dv.getText());
        }
    }){
        String name = "f"+formulaId;
        formula = new FormulaSoft();
        formula.setName(name);
        formulaId++;
        if(w != null) formula.setWeight(w);
        if(doubleVariable != null) formula.setDoubleVariable(doubleVariable);
    }
    formulaHard=formulaBodyDefinition
    {
      formula.setForVariables(formulaHard.getForVariables());
      formula.setIfExpressions(formulaHard.getIfExpressions());
      formula.setRestrictions(formulaHard.getRestrictions());
      if(formulaHard.isConjunction()) formula.setConjunction();
      if(formulaHard.isDisjunction()) formula.setDisjunction();
    }
    ;
    
/**
Hard formulas are formulas which must not be violated in the final MAP state.
*/ 
hardFormulaDefinition returns [FormulaHard formula] throws ParseException:
    formulaHard=formulaBodyDefinition
    {
    formula = formulaHard;
    }
    '.'
    ;    

/**
With cardinality formulas we can model various scenarios including one-to-one, one-to-many, many-to-one, but also two-to-many and so on.
Therefore, we need two additional inputs:
1. The cardinality number which is an integer number and follows at the end of the formula after '<='.
2. The over variables. For each combined element of these variables, the maximal number of occurences is determined by the cardinality number. 
*/
cardinalityFormulaDefiniton returns [FormulaCardinality formula] throws ParseException:
    // FormulaCardinality
    {
      HashSet<VariableType> preliminaryOverVariables = new HashSet<VariableType>();
    }
    ('|' (var1=ID){
         if($var1 != null){
           preliminaryOverVariables.add(new VariableType($var1.getText()));
         }
        }
    (',' (var2=ID){
         if($var2 != null){
           preliminaryOverVariables.add(new VariableType($var2.getText()));
         }
        }
    )* '|')
    hardFormula=formulaBodyDefinition
    {
      // if we have a cardinality formula, we need to figure out if the preliminary over variables
      // exist in the for variables of the formula.
      HashSet<VariableType> forVariables = hardFormula.getForVariables();
      HashSet<VariableType> overVariables = new HashSet<VariableType>();
      for(VariableType search :preliminaryOverVariables){
         boolean found = false;
         for(VariableType var : forVariables){
           if(search.getName().equals(var.getName())){
             overVariables.add(var);
             found=true;
           }
         }
         if(!found){
           throw new ParseException("Over-variable " + search + " could not be found in the formula body of the following formula: " + formula.toString() + " Every over variable has to occur in the formula body.");
         }
      }
    }
    // cardinality
    (operator=('<='|'>=')) (cardinality=FLOAT){
      int card = Integer.parseInt($cardinality.getText());
      boolean lessEqual=true;
      if($operator.getText().equals(">=")){
      	lessEqual=false;
      }
      // now we have all information to create the cardinalityformula
      formula = new FormulaCardinality(hardFormula.getName(), hardFormula.getForVariables(), hardFormula.getIfExpressions(),
      overVariables, hardFormula.getRestrictions(), card, lessEqual);
    }
    ;
    
// internalFormulaDefiniton
/**
Here the body of each formula is created. The body consists of one or more than one expressions.
*/
formulaBodyDefinition returns [FormulaHard formula] throws ParseException:
    e1=expressionDefinition{
                  String name = "f"+formulaId;
                  formula = new FormulaHard();
                  formula.setName(name);
                  formulaId++;
    
                  for(IfExpression ifExpr : $e1.expressions){
                   if(ifExpr instanceof PredicateExpression){ 
                    PredicateExpression expr = (PredicateExpression) ifExpr;  
                    if(expr.getPredicate().isObserved()){
                     expr.setPositive(!expr.isPositive());
                     formula.addIfExpression(expr);
                    }else{
                     formula.addRestriction(expr);
                    }
                   }else{
                     formula.addIfExpression(ifExpr);
                   }
                  }
                 } 
    ('v' e2=expressionDefinition{
                  for(IfExpression ifExpr : $e2.expressions){
                   if(ifExpr instanceof PredicateExpression){ 
                    PredicateExpression expr = (PredicateExpression) ifExpr;  
                    if(expr.getPredicate().isObserved()){
                     expr.setPositive(!expr.isPositive());
                     formula.addIfExpression(expr);
                    }else{
                     formula.addRestriction(expr);
                    }
                   }else{
                     formula.addIfExpression(ifExpr);
                   }
                  }
                 } 
    )* 
    {
      formula.setAllAsForVariables();
    }
    ;  

/**
Defines the expression which is used in the formulaBodyDefinition. An expression consists of a predicate (which has to be defined before) and
variables. Variables can either be:
- a variable associated with a type
- a string value, which can either be negated ('!') or not.
*/
expressionDefinition returns [ArrayList<IfExpression> expressions = new ArrayList<IfExpression>();] throws ParseException:
     (n0=NOT)? predId=ID {
                   // get Predicate from id
                   PredicateAbstract search = new Predicate($predId.getText(), false);
                   PredicateAbstract predicate = null;
                   if(!predicates.contains(search)){
                      throw new ParseException("Predicate " + Messages.printTokenDetails($predId) + " has not been defined. Check for typos (case sensitive)."); 
                   }else{
                      predicate = predicates.ceiling(search);
                   }
                   // negated or not?
                   boolean isPositive = false;
                   if($n0==null){
                    isPositive=true;
                   }
                   // create new expression.
                   expressions.add(new PredicateExpression(isPositive, predicate));
                  }
     '(' ((var1=ID)|((n1=NOT)?(string1=STRING))|ex1=NEX) { 
                  int zahl = 0;
                  PredicateExpression predExpr = (PredicateExpression) expressions.get(0);
                  VariableAbstract v = null;
                  if($var1!=null){
                   v =new VariableType();
                   v.setName($var1.getText());
                  } else if($string1!=null) {
                    v = new VariableString(u.getKey($string1.getText().replace("\"", "")));
                  } else {
                  	v = new VariableArithmeticExpression($ex1.getText().replaceAll("[\\[|\\]]", ""));
                  }
                  predExpr.addVariable(v);
                 }
     (',' ((var2=ID)|((n2=NOT)?(string2=STRING))|ex2=NEX){ 
                  PredicateExpression predExpr2 = (PredicateExpression) expressions.get(0);
                  VariableAbstract v2 = null;
                  if($var2!=null){
                   v2 =new VariableType();
                   v2.setName($var2.getText());
                  } else if($string2!=null) {
                    v2 = new VariableString(u.getKey($string2.getText().replace("\"", "")));
                  } else {
                    v2 = new VariableArithmeticExpression($ex2.getText().replaceAll("[\\[|\\]]", ""));
                  }
                  predExpr.addVariable(v2);
                  var2 = null;
                  string2 = null;
                  ex2 = null;
                 }
     )* {
      for(IfExpression ifExpression : expressions){ 
       if(ifExpression instanceof PredicateExpression){
         PredicateExpression expression = (PredicateExpression) ifExpression;
         // check if types and variable length are ok.
         PredicateAbstract p = expression.getPredicate();
         ArrayList<Type> types = p.getTypes();
         ArrayList<VariableAbstract> variables = expression.getVariables();
         if(p instanceof Predicate && !(types.size()==variables.size())){
            throw new ParseException("Predicate " + p + " has not the same size in types and variables in the current equation. Types are: "+types.toString()+". Variables are: "+variables.toString()); 
         }else if(p instanceof PredicateDouble && !(types.size()+1==variables.size())){
            throw new ParseException("Double Predicate " + p + " has not the same size in types and variables in the current equation. Types are: "+types.toString()+", Double]. Variables are: "+variables.toString());          
         }
         // set last variable of PredicateDouble to DoubleVariable. Before we have no chance to detect if this is the last variable.
         if(p instanceof PredicateDouble){
            VariableAbstract transformToDoubleVar = variables.get(variables.size()-1);
            VariableDouble transformed = new VariableDouble(transformToDoubleVar.getName());
            variables.set(variables.size()-1, transformed);
            expression.setVariables(variables);
         }
         // set variable types (in case of the double variable, the last variable is not tuched.
         for(int i = 0; i<types.size(); i++){
          Type type = types.get(i);
          VariableAbstract varAbstract = variables.get(i);
          if(varAbstract instanceof VariableType){
              VariableType var = (VariableType) varAbstract;
              var.setType(type);
          }else if(varAbstract instanceof VariableArithmeticExpression){
              VariableArithmeticExpression var = (VariableArithmeticExpression) varAbstract;
              var.setType(type);
          }else if(varAbstract instanceof VariableDouble){
             throw new ParseException("No double Type allowed "+ type+". Variable must not be a double var. "+ varAbstract);          
          }
         }
        }
       }
      }
     ')';


/**
Defines predicates. The STAR indicates if the predicate is hidden or observed. Predicates must have one or more types. 
The last type may be the build-in floating-number type.
*/
predicateDefinition returns [PredicateAbstract predicate] throws ParseException:   
        (STAR)? id1=ID {
                    boolean isHidden = false;
                    boolean isNumerical = false;
                    MathExpression mathEx = null;
                    if($STAR==null){
                      isHidden=true;
                    }
                    ArrayList<Type> typesL = new ArrayList<Type>();
                   }
        '(' t1=typeDefinition {typesL.add($t1.type);}
        (',' t2=typeDefinition {typesL.add($t2.type);} )* 
        (',' d=DOUBLEVAR (id2=ID)?)?
        ')'
        (':=' ex=NEX {
            mathEx = new MathExpression($ex.getText().substring(1, $ex.getText().length()-1));
            isNumerical = true;
        })?   	      
        {			             
			             // System.out.println("NUMERICAL: " + isNumerical + " (" + $id1.getText() + ")");
			             if (!isNumerical){
			             if($d == null){
			                    predicate = new Predicate($id1.getText(),isHidden);
			             }else{
			                    if(isHidden){
			                       throw new ParseException("Double predicates are not allowed to be hidden. Predicate " + Messages.printTokenDetails($id1) + " violates that rule."); 
                          }
			                    predicate = new PredicateDouble($id1.getText(),isHidden);
			                    
			             }
			             predicate.setTypes(typesL);			             			             
			             types.addAll(typesL);
			             } else {
			             	predicate = new PredicateNumerical($id1.getText());			             
			             	((PredicateNumerical)predicate).setMathEx(mathEx);
			             	for(Type t: typesL){
			             		predicate.getTypes().add(new Type("int_"));
			             		((PredicateNumerical)predicate).getVariables().add(t.getName());
			             	}
			             	types.add(new Type("int_"));		             	
			             }
			             
			             if(predicates.contains(predicate)){
                      throw new ParseException("Predicate " + Messages.printTokenDetails($id1) + " had already been created. The new predicate is ignored"); 
                   }else{
                      predicates.add(predicate);
                   }
        }
    ;

/**
Creates new Types. Is called inside the predicateDefiniton.
*/
typeDefinition returns [Type type] throws ParseException:  
    ID {Type t=new Type($ID.getText());
        types.add(t);
        type = types.ceiling(t);
        // type = t;       
       }
    ;

// START:tokens
DOUBLEVAR: 'float_';
NEX:   '[' (options {greedy=false;} : .)* ']' ;
ID:   ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_')*;
//INT :   '0'..'9'+ ;
STRING:  '"' (options {greedy=false;} : .)* ('"'|')'|',') ;
NEWLINE: '\n';
WS  :   (' '|'\t'|'\r')+ {skip();} ;
STAR:   '*';
HASH:   '#';
COMMA:  ',';
NOT:  '!';
FLOAT
  : ('+'|'-')?( '0'..'9' )+ ( '.' ( '0'..'9' )* )?
  ;
ML_COMMENT
    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;
SL_COMMENT: '//' ~(NEWLINE)* ;
// END:tokens
