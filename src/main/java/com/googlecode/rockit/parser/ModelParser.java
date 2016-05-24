// $ANTLR 3.4 com\\googlecode\\rockit\\parser\\Model.g 2015-02-03 17:00:55

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


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class ModelParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "COMMA", "DOUBLEVAR", "FLOAT", "HASH", "ID", "ML_COMMENT", "NEWLINE", "NEX", "NOT", "SL_COMMENT", "STAR", "STRING", "WS", "'('", "')'", "'.'", "':'", "':='", "'<='", "'>='", "'v'", "'|'"
    };

    public static final int EOF=-1;
    public static final int T__17=17;
    public static final int T__18=18;
    public static final int T__19=19;
    public static final int T__20=20;
    public static final int T__21=21;
    public static final int T__22=22;
    public static final int T__23=23;
    public static final int T__24=24;
    public static final int T__25=25;
    public static final int COMMA=4;
    public static final int DOUBLEVAR=5;
    public static final int FLOAT=6;
    public static final int HASH=7;
    public static final int ID=8;
    public static final int ML_COMMENT=9;
    public static final int NEWLINE=10;
    public static final int NEX=11;
    public static final int NOT=12;
    public static final int SL_COMMENT=13;
    public static final int STAR=14;
    public static final int STRING=15;
    public static final int WS=16;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public ModelParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public ModelParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return ModelParser.tokenNames; }
    public String getGrammarFileName() { return "com\\googlecode\\rockit\\parser\\Model.g"; }


    /** Map variable name to Integer object holding value */
    TreeSet<Type> types = new TreeSet<Type>();
    TreeSet<PredicateAbstract> predicates = new TreeSet<PredicateAbstract>();
    int formulaId = 1;
    HerbrandUniverse u = HerbrandUniverse.getInstance();



    // $ANTLR start "model"
    // com\\googlecode\\rockit\\parser\\Model.g:40:1: model returns [Model model = new Model()] : ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+ ;
    public final Model model() throws ParseException, RecognitionException {
        Model model =  new Model();


        PredicateAbstract pre =null;

        FormulaSoft sf =null;

        FormulaHard hf =null;

        FormulaCardinality cf =null;


        try {
            // com\\googlecode\\rockit\\parser\\Model.g:40:64: ( ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+ )
            // com\\googlecode\\rockit\\parser\\Model.g:41:3: ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+
            {
            // com\\googlecode\\rockit\\parser\\Model.g:41:3: ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+
            int cnt1=0;
            loop1:
            do {
                int alt1=8;
                alt1 = dfa1.predict(input);
                switch (alt1) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:42:4: ( ML_COMMENT )
            	    {
            	    // com\\googlecode\\rockit\\parser\\Model.g:42:4: ( ML_COMMENT )
            	    // com\\googlecode\\rockit\\parser\\Model.g:42:5: ML_COMMENT
            	    {
            	    match(input,ML_COMMENT,FOLLOW_ML_COMMENT_in_model53); 

            	    }


            	    }
            	    break;
            	case 2 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:42:19: ( SL_COMMENT )
            	    {
            	    // com\\googlecode\\rockit\\parser\\Model.g:42:19: ( SL_COMMENT )
            	    // com\\googlecode\\rockit\\parser\\Model.g:42:20: SL_COMMENT
            	    {
            	    match(input,SL_COMMENT,FOLLOW_SL_COMMENT_in_model59); 

            	    }


            	    }
            	    break;
            	case 3 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:42:34: ( NEWLINE )
            	    {
            	    // com\\googlecode\\rockit\\parser\\Model.g:42:34: ( NEWLINE )
            	    // com\\googlecode\\rockit\\parser\\Model.g:42:35: NEWLINE
            	    {
            	    match(input,NEWLINE,FOLLOW_NEWLINE_in_model65); 

            	    }


            	    }
            	    break;
            	case 4 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:43:4: (pre= predicateDefinition )
            	    {
            	    // com\\googlecode\\rockit\\parser\\Model.g:43:4: (pre= predicateDefinition )
            	    // com\\googlecode\\rockit\\parser\\Model.g:43:5: pre= predicateDefinition
            	    {
            	    pushFollow(FOLLOW_predicateDefinition_in_model76);
            	    pre=predicateDefinition();

            	    state._fsp--;


            	    model.addPredicate(pre);

            	    }


            	    }
            	    break;
            	case 5 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:44:4: (sf= softFormulaDefinition )
            	    {
            	    // com\\googlecode\\rockit\\parser\\Model.g:44:4: (sf= softFormulaDefinition )
            	    // com\\googlecode\\rockit\\parser\\Model.g:44:5: sf= softFormulaDefinition
            	    {
            	    pushFollow(FOLLOW_softFormulaDefinition_in_model88);
            	    sf=softFormulaDefinition();

            	    state._fsp--;


            	    FormulaSoft formula = sf;
            	                             if(formula.getRestrictions().size()==0){
            	                               // drop formula
            	                             }else{
            	                               model.addFormula(formula);
            	                             }
            	                         

            	    }


            	    }
            	    break;
            	case 6 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:51:4: (hf= hardFormulaDefinition )
            	    {
            	    // com\\googlecode\\rockit\\parser\\Model.g:51:4: (hf= hardFormulaDefinition )
            	    // com\\googlecode\\rockit\\parser\\Model.g:51:5: hf= hardFormulaDefinition
            	    {
            	    pushFollow(FOLLOW_hardFormulaDefinition_in_model100);
            	    hf=hardFormulaDefinition();

            	    state._fsp--;


            	    FormulaHard formula = hf;
            	                             if(formula.getRestrictions().size()==0){
            	                               // drop formula
            	                             }else{
            	                               model.addFormula(formula);
            	                             }
            	                         

            	    }


            	    }
            	    break;
            	case 7 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:58:4: (cf= cardinalityFormulaDefiniton )
            	    {
            	    // com\\googlecode\\rockit\\parser\\Model.g:58:4: (cf= cardinalityFormulaDefiniton )
            	    // com\\googlecode\\rockit\\parser\\Model.g:58:5: cf= cardinalityFormulaDefiniton
            	    {
            	    pushFollow(FOLLOW_cardinalityFormulaDefiniton_in_model112);
            	    cf=cardinalityFormulaDefiniton();

            	    state._fsp--;


            	    FormulaCardinality formula = cf;
            	                             if(formula.getRestrictions().size()==0){
            	                               // drop formula
            	                             }else{
            	                               model.addFormula(formula);
            	                             }
            	                         

            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt1 >= 1 ) break loop1;
                        EarlyExitException eee =
                            new EarlyExitException(1, input);
                        throw eee;
                }
                cnt1++;
            } while (true);


            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return model;
    }
    // $ANTLR end "model"



    // $ANTLR start "softFormulaDefinition"
    // com\\googlecode\\rockit\\parser\\Model.g:73:1: softFormulaDefinition returns [FormulaSoft formula] : ( (f= FLOAT ) | (dv= ID ':' ) ) formulaHard= formulaBodyDefinition ;
    public final FormulaSoft softFormulaDefinition() throws ParseException, RecognitionException {
        FormulaSoft formula = null;


        Token f=null;
        Token dv=null;
        FormulaHard formulaHard =null;


        try {
            // com\\googlecode\\rockit\\parser\\Model.g:73:74: ( ( (f= FLOAT ) | (dv= ID ':' ) ) formulaHard= formulaBodyDefinition )
            // com\\googlecode\\rockit\\parser\\Model.g:74:5: ( (f= FLOAT ) | (dv= ID ':' ) ) formulaHard= formulaBodyDefinition
            {

                    Double w = null;
                    VariableDouble doubleVariable = null;
                

            // com\\googlecode\\rockit\\parser\\Model.g:78:5: ( (f= FLOAT ) | (dv= ID ':' ) )
            int alt2=2;
            switch ( input.LA(1) ) {
            case FLOAT:
                {
                alt2=1;
                }
                break;
            case ID:
                {
                alt2=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 2, 0, input);

                throw nvae;

            }

            switch (alt2) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\Model.g:78:6: (f= FLOAT )
                    {
                    // com\\googlecode\\rockit\\parser\\Model.g:78:6: (f= FLOAT )
                    // com\\googlecode\\rockit\\parser\\Model.g:78:7: f= FLOAT
                    {
                    f=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_softFormulaDefinition164); 

                    }



                            if(f != null){
                              w = Double.parseDouble(f.getText());
                            }
                         

                    }
                    break;
                case 2 :
                    // com\\googlecode\\rockit\\parser\\Model.g:83:7: (dv= ID ':' )
                    {
                    // com\\googlecode\\rockit\\parser\\Model.g:83:7: (dv= ID ':' )
                    // com\\googlecode\\rockit\\parser\\Model.g:83:8: dv= ID ':'
                    {
                    dv=(Token)match(input,ID,FOLLOW_ID_in_softFormulaDefinition177); 

                    match(input,20,FOLLOW_20_in_softFormulaDefinition179); 

                    }



                            if(dv !=null){
                              doubleVariable = new VariableDouble(dv.getText());
                            }
                        

                    }
                    break;

            }



                    String name = "f"+formulaId;
                    formula = new FormulaSoft();
                    formula.setName(name);
                    formulaId++;
                    if(w != null) formula.setWeight(w);
                    if(doubleVariable != null) formula.setDoubleVariable(doubleVariable);
                

            pushFollow(FOLLOW_formulaBodyDefinition_in_softFormulaDefinition191);
            formulaHard=formulaBodyDefinition();

            state._fsp--;



                  formula.setForVariables(formulaHard.getForVariables());
                  formula.setIfExpressions(formulaHard.getIfExpressions());
                  formula.setRestrictions(formulaHard.getRestrictions());
                  if(formulaHard.isConjunction()) formula.setConjunction();
                  if(formulaHard.isDisjunction()) formula.setDisjunction();
                

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return formula;
    }
    // $ANTLR end "softFormulaDefinition"



    // $ANTLR start "hardFormulaDefinition"
    // com\\googlecode\\rockit\\parser\\Model.g:108:1: hardFormulaDefinition returns [FormulaHard formula] : formulaHard= formulaBodyDefinition '.' ;
    public final FormulaHard hardFormulaDefinition() throws ParseException, RecognitionException {
        FormulaHard formula = null;


        FormulaHard formulaHard =null;


        try {
            // com\\googlecode\\rockit\\parser\\Model.g:108:74: (formulaHard= formulaBodyDefinition '.' )
            // com\\googlecode\\rockit\\parser\\Model.g:109:5: formulaHard= formulaBodyDefinition '.'
            {
            pushFollow(FOLLOW_formulaBodyDefinition_in_hardFormulaDefinition230);
            formulaHard=formulaBodyDefinition();

            state._fsp--;



                formula = formulaHard;
                

            match(input,19,FOLLOW_19_in_hardFormulaDefinition242); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return formula;
    }
    // $ANTLR end "hardFormulaDefinition"



    // $ANTLR start "cardinalityFormulaDefiniton"
    // com\\googlecode\\rockit\\parser\\Model.g:122:1: cardinalityFormulaDefiniton returns [FormulaCardinality formula] : ( '|' (var1= ID ) ( ',' (var2= ID ) )* '|' ) hardFormula= formulaBodyDefinition (operator= ( '<=' | '>=' ) ) (cardinality= FLOAT ) ;
    public final FormulaCardinality cardinalityFormulaDefiniton() throws ParseException, RecognitionException {
        FormulaCardinality formula = null;


        Token var1=null;
        Token var2=null;
        Token operator=null;
        Token cardinality=null;
        FormulaHard hardFormula =null;


        try {
            // com\\googlecode\\rockit\\parser\\Model.g:122:87: ( ( '|' (var1= ID ) ( ',' (var2= ID ) )* '|' ) hardFormula= formulaBodyDefinition (operator= ( '<=' | '>=' ) ) (cardinality= FLOAT ) )
            // com\\googlecode\\rockit\\parser\\Model.g:124:5: ( '|' (var1= ID ) ( ',' (var2= ID ) )* '|' ) hardFormula= formulaBodyDefinition (operator= ( '<=' | '>=' ) ) (cardinality= FLOAT )
            {

                  HashSet<VariableType> preliminaryOverVariables = new HashSet<VariableType>();
                

            // com\\googlecode\\rockit\\parser\\Model.g:127:5: ( '|' (var1= ID ) ( ',' (var2= ID ) )* '|' )
            // com\\googlecode\\rockit\\parser\\Model.g:127:6: '|' (var1= ID ) ( ',' (var2= ID ) )* '|'
            {
            match(input,25,FOLLOW_25_in_cardinalityFormulaDefiniton284); 

            // com\\googlecode\\rockit\\parser\\Model.g:127:10: (var1= ID )
            // com\\googlecode\\rockit\\parser\\Model.g:127:11: var1= ID
            {
            var1=(Token)match(input,ID,FOLLOW_ID_in_cardinalityFormulaDefiniton289); 

            }



                     if(var1 != null){
                       preliminaryOverVariables.add(new VariableType(var1.getText()));
                     }
                    

            // com\\googlecode\\rockit\\parser\\Model.g:132:5: ( ',' (var2= ID ) )*
            loop3:
            do {
                int alt3=2;
                switch ( input.LA(1) ) {
                case COMMA:
                    {
                    alt3=1;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:132:6: ',' (var2= ID )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_cardinalityFormulaDefiniton298); 

            	    // com\\googlecode\\rockit\\parser\\Model.g:132:10: (var2= ID )
            	    // com\\googlecode\\rockit\\parser\\Model.g:132:11: var2= ID
            	    {
            	    var2=(Token)match(input,ID,FOLLOW_ID_in_cardinalityFormulaDefiniton303); 

            	    }



            	             if(var2 != null){
            	               preliminaryOverVariables.add(new VariableType(var2.getText()));
            	             }
            	            

            	    }
            	    break;

            	default :
            	    break loop3;
                }
            } while (true);


            match(input,25,FOLLOW_25_in_cardinalityFormulaDefiniton314); 

            }


            pushFollow(FOLLOW_formulaBodyDefinition_in_cardinalityFormulaDefiniton323);
            hardFormula=formulaBodyDefinition();

            state._fsp--;



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
                

            // com\\googlecode\\rockit\\parser\\Model.g:158:5: (operator= ( '<=' | '>=' ) )
            // com\\googlecode\\rockit\\parser\\Model.g:158:6: operator= ( '<=' | '>=' )
            {
            operator=(Token)input.LT(1);

            if ( (input.LA(1) >= 22 && input.LA(1) <= 23) ) {
                input.consume();
                state.errorRecovery=false;
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                throw mse;
            }


            }


            // com\\googlecode\\rockit\\parser\\Model.g:158:28: (cardinality= FLOAT )
            // com\\googlecode\\rockit\\parser\\Model.g:158:29: cardinality= FLOAT
            {
            cardinality=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_cardinalityFormulaDefiniton353); 

            }



                  int card = Integer.parseInt(cardinality.getText());
                  boolean lessEqual=true;
                  if(operator.getText().equals(">=")){
                  	lessEqual=false;
                  }
                  // now we have all information to create the cardinalityformula
                  formula = new FormulaCardinality(hardFormula.getName(), hardFormula.getForVariables(), hardFormula.getIfExpressions(),
                  overVariables, hardFormula.getRestrictions(), card, lessEqual);
                

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return formula;
    }
    // $ANTLR end "cardinalityFormulaDefiniton"



    // $ANTLR start "formulaBodyDefinition"
    // com\\googlecode\\rockit\\parser\\Model.g:174:1: formulaBodyDefinition returns [FormulaHard formula] : e1= expressionDefinition ( 'v' e2= expressionDefinition )* ;
    public final FormulaHard formulaBodyDefinition() throws ParseException, RecognitionException {
        FormulaHard formula = null;


        ArrayList<IfExpression> e1 =null;

        ArrayList<IfExpression> e2 =null;


        try {
            // com\\googlecode\\rockit\\parser\\Model.g:174:74: (e1= expressionDefinition ( 'v' e2= expressionDefinition )* )
            // com\\googlecode\\rockit\\parser\\Model.g:175:5: e1= expressionDefinition ( 'v' e2= expressionDefinition )*
            {
            pushFollow(FOLLOW_expressionDefinition_in_formulaBodyDefinition388);
            e1=expressionDefinition();

            state._fsp--;



                              String name = "f"+formulaId;
                              formula = new FormulaHard();
                              formula.setName(name);
                              formulaId++;
                
                              for(IfExpression ifExpr : e1){
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
                             

            // com\\googlecode\\rockit\\parser\\Model.g:195:5: ( 'v' e2= expressionDefinition )*
            loop4:
            do {
                int alt4=2;
                switch ( input.LA(1) ) {
                case 24:
                    {
                    alt4=1;
                    }
                    break;

                }

                switch (alt4) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:195:6: 'v' e2= expressionDefinition
            	    {
            	    match(input,24,FOLLOW_24_in_formulaBodyDefinition397); 

            	    pushFollow(FOLLOW_expressionDefinition_in_formulaBodyDefinition401);
            	    e2=expressionDefinition();

            	    state._fsp--;



            	                      for(IfExpression ifExpr : e2){
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
            	    break;

            	default :
            	    break loop4;
                }
            } while (true);



                  formula.setAllAsForVariables();
                

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return formula;
    }
    // $ANTLR end "formulaBodyDefinition"



    // $ANTLR start "expressionDefinition"
    // com\\googlecode\\rockit\\parser\\Model.g:222:1: expressionDefinition returns [ArrayList<IfExpression> expressions = new ArrayList<IfExpression>();] : (n0= NOT )? predId= ID '(' ( (var1= ID ) | ( (n1= NOT )? (string1= STRING ) ) |ex1= NEX ) ( ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) |ex2= NEX ) )* ')' ;
    public final ArrayList<IfExpression> expressionDefinition() throws ParseException, RecognitionException {
        ArrayList<IfExpression> expressions =  new ArrayList<IfExpression>();;


        Token n0=null;
        Token predId=null;
        Token var1=null;
        Token n1=null;
        Token string1=null;
        Token ex1=null;
        Token var2=null;
        Token n2=null;
        Token string2=null;
        Token ex2=null;

        try {
            // com\\googlecode\\rockit\\parser\\Model.g:222:122: ( (n0= NOT )? predId= ID '(' ( (var1= ID ) | ( (n1= NOT )? (string1= STRING ) ) |ex1= NEX ) ( ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) |ex2= NEX ) )* ')' )
            // com\\googlecode\\rockit\\parser\\Model.g:223:6: (n0= NOT )? predId= ID '(' ( (var1= ID ) | ( (n1= NOT )? (string1= STRING ) ) |ex1= NEX ) ( ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) |ex2= NEX ) )* ')'
            {
            // com\\googlecode\\rockit\\parser\\Model.g:223:6: (n0= NOT )?
            int alt5=2;
            switch ( input.LA(1) ) {
                case NOT:
                    {
                    alt5=1;
                    }
                    break;
            }

            switch (alt5) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\Model.g:223:7: n0= NOT
                    {
                    n0=(Token)match(input,NOT,FOLLOW_NOT_in_expressionDefinition449); 

                    }
                    break;

            }


            predId=(Token)match(input,ID,FOLLOW_ID_in_expressionDefinition455); 


                               // get Predicate from id
                               PredicateAbstract search = new Predicate(predId.getText(), false);
                               PredicateAbstract predicate = null;
                               if(!predicates.contains(search)){
                                  throw new ParseException("Predicate " + Messages.printTokenDetails(predId) + " has not been defined. Check for typos (case sensitive)."); 
                               }else{
                                  predicate = predicates.ceiling(search);
                               }
                               // negated or not?
                               boolean isPositive = false;
                               if(n0==null){
                                isPositive=true;
                               }
                               // create new expression.
                               expressions.add(new PredicateExpression(isPositive, predicate));
                              

            match(input,17,FOLLOW_17_in_expressionDefinition464); 

            // com\\googlecode\\rockit\\parser\\Model.g:240:10: ( (var1= ID ) | ( (n1= NOT )? (string1= STRING ) ) |ex1= NEX )
            int alt7=3;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt7=1;
                }
                break;
            case NOT:
            case STRING:
                {
                alt7=2;
                }
                break;
            case NEX:
                {
                alt7=3;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 7, 0, input);

                throw nvae;

            }

            switch (alt7) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\Model.g:240:11: (var1= ID )
                    {
                    // com\\googlecode\\rockit\\parser\\Model.g:240:11: (var1= ID )
                    // com\\googlecode\\rockit\\parser\\Model.g:240:12: var1= ID
                    {
                    var1=(Token)match(input,ID,FOLLOW_ID_in_expressionDefinition470); 

                    }


                    }
                    break;
                case 2 :
                    // com\\googlecode\\rockit\\parser\\Model.g:240:21: ( (n1= NOT )? (string1= STRING ) )
                    {
                    // com\\googlecode\\rockit\\parser\\Model.g:240:21: ( (n1= NOT )? (string1= STRING ) )
                    // com\\googlecode\\rockit\\parser\\Model.g:240:22: (n1= NOT )? (string1= STRING )
                    {
                    // com\\googlecode\\rockit\\parser\\Model.g:240:22: (n1= NOT )?
                    int alt6=2;
                    switch ( input.LA(1) ) {
                        case NOT:
                            {
                            alt6=1;
                            }
                            break;
                    }

                    switch (alt6) {
                        case 1 :
                            // com\\googlecode\\rockit\\parser\\Model.g:240:23: n1= NOT
                            {
                            n1=(Token)match(input,NOT,FOLLOW_NOT_in_expressionDefinition477); 

                            }
                            break;

                    }


                    // com\\googlecode\\rockit\\parser\\Model.g:240:31: (string1= STRING )
                    // com\\googlecode\\rockit\\parser\\Model.g:240:32: string1= STRING
                    {
                    string1=(Token)match(input,STRING,FOLLOW_STRING_in_expressionDefinition483); 

                    }


                    }


                    }
                    break;
                case 3 :
                    // com\\googlecode\\rockit\\parser\\Model.g:240:49: ex1= NEX
                    {
                    ex1=(Token)match(input,NEX,FOLLOW_NEX_in_expressionDefinition489); 

                    }
                    break;

            }


             
                              int zahl = 0;
                              PredicateExpression predExpr = (PredicateExpression) expressions.get(0);
                              VariableAbstract v = null;
                              if(var1!=null){
                               v =new VariableType();
                               v.setName(var1.getText());
                              } else if(string1!=null) {
                                v = new VariableString(u.getKey(string1.getText().replace("\"", "")));
                              } else {
                              	v = new VariableArithmeticExpression(ex1.getText().replaceAll("[\\[|\\]]", ""));
                              }
                              predExpr.addVariable(v);
                             

            // com\\googlecode\\rockit\\parser\\Model.g:254:6: ( ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) |ex2= NEX ) )*
            loop10:
            do {
                int alt10=2;
                switch ( input.LA(1) ) {
                case COMMA:
                    {
                    alt10=1;
                    }
                    break;

                }

                switch (alt10) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:254:7: ',' ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) |ex2= NEX )
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_expressionDefinition500); 

            	    // com\\googlecode\\rockit\\parser\\Model.g:254:11: ( (var2= ID ) | ( (n2= NOT )? (string2= STRING ) ) |ex2= NEX )
            	    int alt9=3;
            	    switch ( input.LA(1) ) {
            	    case ID:
            	        {
            	        alt9=1;
            	        }
            	        break;
            	    case NOT:
            	    case STRING:
            	        {
            	        alt9=2;
            	        }
            	        break;
            	    case NEX:
            	        {
            	        alt9=3;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 9, 0, input);

            	        throw nvae;

            	    }

            	    switch (alt9) {
            	        case 1 :
            	            // com\\googlecode\\rockit\\parser\\Model.g:254:12: (var2= ID )
            	            {
            	            // com\\googlecode\\rockit\\parser\\Model.g:254:12: (var2= ID )
            	            // com\\googlecode\\rockit\\parser\\Model.g:254:13: var2= ID
            	            {
            	            var2=(Token)match(input,ID,FOLLOW_ID_in_expressionDefinition506); 

            	            }


            	            }
            	            break;
            	        case 2 :
            	            // com\\googlecode\\rockit\\parser\\Model.g:254:22: ( (n2= NOT )? (string2= STRING ) )
            	            {
            	            // com\\googlecode\\rockit\\parser\\Model.g:254:22: ( (n2= NOT )? (string2= STRING ) )
            	            // com\\googlecode\\rockit\\parser\\Model.g:254:23: (n2= NOT )? (string2= STRING )
            	            {
            	            // com\\googlecode\\rockit\\parser\\Model.g:254:23: (n2= NOT )?
            	            int alt8=2;
            	            switch ( input.LA(1) ) {
            	                case NOT:
            	                    {
            	                    alt8=1;
            	                    }
            	                    break;
            	            }

            	            switch (alt8) {
            	                case 1 :
            	                    // com\\googlecode\\rockit\\parser\\Model.g:254:24: n2= NOT
            	                    {
            	                    n2=(Token)match(input,NOT,FOLLOW_NOT_in_expressionDefinition513); 

            	                    }
            	                    break;

            	            }


            	            // com\\googlecode\\rockit\\parser\\Model.g:254:32: (string2= STRING )
            	            // com\\googlecode\\rockit\\parser\\Model.g:254:33: string2= STRING
            	            {
            	            string2=(Token)match(input,STRING,FOLLOW_STRING_in_expressionDefinition519); 

            	            }


            	            }


            	            }
            	            break;
            	        case 3 :
            	            // com\\googlecode\\rockit\\parser\\Model.g:254:50: ex2= NEX
            	            {
            	            ex2=(Token)match(input,NEX,FOLLOW_NEX_in_expressionDefinition525); 

            	            }
            	            break;

            	    }


            	     
            	                      PredicateExpression predExpr2 = (PredicateExpression) expressions.get(0);
            	                      VariableAbstract v2 = null;
            	                      if(var2!=null){
            	                       v2 =new VariableType();
            	                       v2.setName(var2.getText());
            	                      } else if(string2!=null) {
            	                        v2 = new VariableString(u.getKey(string2.getText().replace("\"", "")));
            	                      } else {
            	                        v2 = new VariableArithmeticExpression(ex2.getText().replaceAll("[\\[|\\]]", ""));
            	                      }
            	                      predExpr.addVariable(v2);
            	                      var2 = null;
            	                      string2 = null;
            	                      ex2 = null;
            	                     

            	    }
            	    break;

            	default :
            	    break loop10;
                }
            } while (true);



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
                  

            match(input,18,FOLLOW_18_in_expressionDefinition544); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return expressions;
    }
    // $ANTLR end "expressionDefinition"



    // $ANTLR start "predicateDefinition"
    // com\\googlecode\\rockit\\parser\\Model.g:314:1: predicateDefinition returns [PredicateAbstract predicate] : ( STAR )? id1= ID '(' t1= typeDefinition ( ',' t2= typeDefinition )* ( ',' d= DOUBLEVAR (id2= ID )? )? ')' ( ':=' ex= NEX )? ;
    public final PredicateAbstract predicateDefinition() throws ParseException, RecognitionException {
        PredicateAbstract predicate = null;


        Token id1=null;
        Token d=null;
        Token id2=null;
        Token ex=null;
        Token STAR1=null;
        Type t1 =null;

        Type t2 =null;


        try {
            // com\\googlecode\\rockit\\parser\\Model.g:314:80: ( ( STAR )? id1= ID '(' t1= typeDefinition ( ',' t2= typeDefinition )* ( ',' d= DOUBLEVAR (id2= ID )? )? ')' ( ':=' ex= NEX )? )
            // com\\googlecode\\rockit\\parser\\Model.g:315:9: ( STAR )? id1= ID '(' t1= typeDefinition ( ',' t2= typeDefinition )* ( ',' d= DOUBLEVAR (id2= ID )? )? ')' ( ':=' ex= NEX )?
            {
            // com\\googlecode\\rockit\\parser\\Model.g:315:9: ( STAR )?
            int alt11=2;
            switch ( input.LA(1) ) {
                case STAR:
                    {
                    alt11=1;
                    }
                    break;
            }

            switch (alt11) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\Model.g:315:10: STAR
                    {
                    STAR1=(Token)match(input,STAR,FOLLOW_STAR_in_predicateDefinition574); 

                    }
                    break;

            }


            id1=(Token)match(input,ID,FOLLOW_ID_in_predicateDefinition580); 


                                boolean isHidden = false;
                                boolean isNumerical = false;
                                MathExpression mathEx = null;
                                if(STAR1==null){
                                  isHidden=true;
                                }
                                ArrayList<Type> typesL = new ArrayList<Type>();
                               

            match(input,17,FOLLOW_17_in_predicateDefinition592); 

            pushFollow(FOLLOW_typeDefinition_in_predicateDefinition596);
            t1=typeDefinition();

            state._fsp--;


            typesL.add(t1);

            // com\\googlecode\\rockit\\parser\\Model.g:325:9: ( ',' t2= typeDefinition )*
            loop12:
            do {
                int alt12=2;
                switch ( input.LA(1) ) {
                case COMMA:
                    {
                    switch ( input.LA(2) ) {
                    case ID:
                        {
                        alt12=1;
                        }
                        break;

                    }

                    }
                    break;

                }

                switch (alt12) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\Model.g:325:10: ',' t2= typeDefinition
            	    {
            	    match(input,COMMA,FOLLOW_COMMA_in_predicateDefinition609); 

            	    pushFollow(FOLLOW_typeDefinition_in_predicateDefinition613);
            	    t2=typeDefinition();

            	    state._fsp--;


            	    typesL.add(t2);

            	    }
            	    break;

            	default :
            	    break loop12;
                }
            } while (true);


            // com\\googlecode\\rockit\\parser\\Model.g:326:9: ( ',' d= DOUBLEVAR (id2= ID )? )?
            int alt14=2;
            switch ( input.LA(1) ) {
                case COMMA:
                    {
                    alt14=1;
                    }
                    break;
            }

            switch (alt14) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\Model.g:326:10: ',' d= DOUBLEVAR (id2= ID )?
                    {
                    match(input,COMMA,FOLLOW_COMMA_in_predicateDefinition630); 

                    d=(Token)match(input,DOUBLEVAR,FOLLOW_DOUBLEVAR_in_predicateDefinition634); 

                    // com\\googlecode\\rockit\\parser\\Model.g:326:26: (id2= ID )?
                    int alt13=2;
                    switch ( input.LA(1) ) {
                        case ID:
                            {
                            alt13=1;
                            }
                            break;
                    }

                    switch (alt13) {
                        case 1 :
                            // com\\googlecode\\rockit\\parser\\Model.g:326:27: id2= ID
                            {
                            id2=(Token)match(input,ID,FOLLOW_ID_in_predicateDefinition639); 

                            }
                            break;

                    }


                    }
                    break;

            }


            match(input,18,FOLLOW_18_in_predicateDefinition653); 

            // com\\googlecode\\rockit\\parser\\Model.g:328:9: ( ':=' ex= NEX )?
            int alt15=2;
            switch ( input.LA(1) ) {
                case 21:
                    {
                    alt15=1;
                    }
                    break;
            }

            switch (alt15) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\Model.g:328:10: ':=' ex= NEX
                    {
                    match(input,21,FOLLOW_21_in_predicateDefinition664); 

                    ex=(Token)match(input,NEX,FOLLOW_NEX_in_predicateDefinition668); 


                                mathEx = new MathExpression(ex.getText().substring(1, ex.getText().length()-1));
                                isNumerical = true;
                            

                    }
                    break;

            }


            			             
            			             // System.out.println("NUMERICAL: " + isNumerical + " (" + id1.getText() + ")");
            			             if (!isNumerical){
            			             if(d == null){
            			                    predicate = new Predicate(id1.getText(),isHidden);
            			             }else{
            			                    if(isHidden){
            			                       throw new ParseException("Double predicates are not allowed to be hidden. Predicate " + Messages.printTokenDetails(id1) + " violates that rule."); 
                                      }
            			                    predicate = new PredicateDouble(id1.getText(),isHidden);
            			                    
            			             }
            			             predicate.setTypes(typesL);			             			             
            			             types.addAll(typesL);
            			             } else {
            			             	predicate = new PredicateNumerical(id1.getText());			             
            			             	((PredicateNumerical)predicate).setMathEx(mathEx);
            			             	for(Type t: typesL){
            			             		predicate.getTypes().add(new Type("int_"));
            			             		((PredicateNumerical)predicate).getVariables().add(t.getName());
            			             	}
            			             	types.add(new Type("int_"));		             	
            			             }
            			             
            			             if(predicates.contains(predicate)){
                                  throw new ParseException("Predicate " + Messages.printTokenDetails(id1) + " had already been created. The new predicate is ignored"); 
                               }else{
                                  predicates.add(predicate);
                               }
                    

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return predicate;
    }
    // $ANTLR end "predicateDefinition"



    // $ANTLR start "typeDefinition"
    // com\\googlecode\\rockit\\parser\\Model.g:367:1: typeDefinition returns [Type type] : ID ;
    public final Type typeDefinition() throws ParseException, RecognitionException {
        Type type = null;


        Token ID2=null;

        try {
            // com\\googlecode\\rockit\\parser\\Model.g:367:57: ( ID )
            // com\\googlecode\\rockit\\parser\\Model.g:368:5: ID
            {
            ID2=(Token)match(input,ID,FOLLOW_ID_in_typeDefinition720); 

            Type t=new Type(ID2.getText());
                    types.add(t);
                    type = types.ceiling(t);
                    // type = t;       
                   

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return type;
    }
    // $ANTLR end "typeDefinition"

    // Delegated rules


    protected DFA1 dfa1 = new DFA1(this);
    static final String DFA1_eotS =
        "\17\uffff";
    static final String DFA1_eofS =
        "\1\1\14\uffff\1\5\1\uffff";
    static final String DFA1_minS =
        "\1\6\5\uffff\1\21\3\uffff\1\10\1\4\1\5\1\6\1\4";
    static final String DFA1_maxS =
        "\1\31\5\uffff\1\24\3\uffff\1\17\1\22\1\17\1\31\1\22";
    static final String DFA1_acceptS =
        "\1\uffff\1\10\1\1\1\2\1\3\1\4\1\uffff\1\5\1\6\1\7\5\uffff";
    static final String DFA1_specialS =
        "\17\uffff}>";
    static final String[] DFA1_transitionS = {
            "\1\7\1\uffff\1\6\1\2\1\4\1\uffff\1\10\1\3\1\5\12\uffff\1\11",
            "",
            "",
            "",
            "",
            "",
            "\1\12\2\uffff\1\7",
            "",
            "",
            "",
            "\1\13\2\uffff\2\10\2\uffff\1\10",
            "\1\14\15\uffff\1\15",
            "\1\5\2\uffff\1\16\2\uffff\2\10\2\uffff\1\10",
            "\1\5\1\uffff\3\5\1\uffff\3\5\4\uffff\1\10\1\uffff\1\5\2\uffff"+
            "\1\10\1\5",
            "\1\14\15\uffff\1\15"
    };

    static final short[] DFA1_eot = DFA.unpackEncodedString(DFA1_eotS);
    static final short[] DFA1_eof = DFA.unpackEncodedString(DFA1_eofS);
    static final char[] DFA1_min = DFA.unpackEncodedStringToUnsignedChars(DFA1_minS);
    static final char[] DFA1_max = DFA.unpackEncodedStringToUnsignedChars(DFA1_maxS);
    static final short[] DFA1_accept = DFA.unpackEncodedString(DFA1_acceptS);
    static final short[] DFA1_special = DFA.unpackEncodedString(DFA1_specialS);
    static final short[][] DFA1_transition;

    static {
        int numStates = DFA1_transitionS.length;
        DFA1_transition = new short[numStates][];
        for (int i=0; i<numStates; i++) {
            DFA1_transition[i] = DFA.unpackEncodedString(DFA1_transitionS[i]);
        }
    }

    class DFA1 extends DFA {

        public DFA1(BaseRecognizer recognizer) {
            this.recognizer = recognizer;
            this.decisionNumber = 1;
            this.eot = DFA1_eot;
            this.eof = DFA1_eof;
            this.min = DFA1_min;
            this.max = DFA1_max;
            this.accept = DFA1_accept;
            this.special = DFA1_special;
            this.transition = DFA1_transition;
        }
        public String getDescription() {
            return "()+ loopback of 41:3: ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | (pre= predicateDefinition ) | (sf= softFormulaDefinition ) | (hf= hardFormulaDefinition ) | (cf= cardinalityFormulaDefiniton ) )+";
        }
    }
 

    public static final BitSet FOLLOW_ML_COMMENT_in_model53 = new BitSet(new long[]{0x0000000002007742L});
    public static final BitSet FOLLOW_SL_COMMENT_in_model59 = new BitSet(new long[]{0x0000000002007742L});
    public static final BitSet FOLLOW_NEWLINE_in_model65 = new BitSet(new long[]{0x0000000002007742L});
    public static final BitSet FOLLOW_predicateDefinition_in_model76 = new BitSet(new long[]{0x0000000002007742L});
    public static final BitSet FOLLOW_softFormulaDefinition_in_model88 = new BitSet(new long[]{0x0000000002007742L});
    public static final BitSet FOLLOW_hardFormulaDefinition_in_model100 = new BitSet(new long[]{0x0000000002007742L});
    public static final BitSet FOLLOW_cardinalityFormulaDefiniton_in_model112 = new BitSet(new long[]{0x0000000002007742L});
    public static final BitSet FOLLOW_FLOAT_in_softFormulaDefinition164 = new BitSet(new long[]{0x0000000000001100L});
    public static final BitSet FOLLOW_ID_in_softFormulaDefinition177 = new BitSet(new long[]{0x0000000000100000L});
    public static final BitSet FOLLOW_20_in_softFormulaDefinition179 = new BitSet(new long[]{0x0000000000001100L});
    public static final BitSet FOLLOW_formulaBodyDefinition_in_softFormulaDefinition191 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_formulaBodyDefinition_in_hardFormulaDefinition230 = new BitSet(new long[]{0x0000000000080000L});
    public static final BitSet FOLLOW_19_in_hardFormulaDefinition242 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_25_in_cardinalityFormulaDefiniton284 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_cardinalityFormulaDefiniton289 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_COMMA_in_cardinalityFormulaDefiniton298 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_cardinalityFormulaDefiniton303 = new BitSet(new long[]{0x0000000002000010L});
    public static final BitSet FOLLOW_25_in_cardinalityFormulaDefiniton314 = new BitSet(new long[]{0x0000000000001100L});
    public static final BitSet FOLLOW_formulaBodyDefinition_in_cardinalityFormulaDefiniton323 = new BitSet(new long[]{0x0000000000C00000L});
    public static final BitSet FOLLOW_set_in_cardinalityFormulaDefiniton343 = new BitSet(new long[]{0x0000000000000040L});
    public static final BitSet FOLLOW_FLOAT_in_cardinalityFormulaDefiniton353 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_expressionDefinition_in_formulaBodyDefinition388 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_24_in_formulaBodyDefinition397 = new BitSet(new long[]{0x0000000000001100L});
    public static final BitSet FOLLOW_expressionDefinition_in_formulaBodyDefinition401 = new BitSet(new long[]{0x0000000001000002L});
    public static final BitSet FOLLOW_NOT_in_expressionDefinition449 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_expressionDefinition455 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_expressionDefinition464 = new BitSet(new long[]{0x0000000000009900L});
    public static final BitSet FOLLOW_ID_in_expressionDefinition470 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_NOT_in_expressionDefinition477 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_expressionDefinition483 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_NEX_in_expressionDefinition489 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_COMMA_in_expressionDefinition500 = new BitSet(new long[]{0x0000000000009900L});
    public static final BitSet FOLLOW_ID_in_expressionDefinition506 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_NOT_in_expressionDefinition513 = new BitSet(new long[]{0x0000000000008000L});
    public static final BitSet FOLLOW_STRING_in_expressionDefinition519 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_NEX_in_expressionDefinition525 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_18_in_expressionDefinition544 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_STAR_in_predicateDefinition574 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_ID_in_predicateDefinition580 = new BitSet(new long[]{0x0000000000020000L});
    public static final BitSet FOLLOW_17_in_predicateDefinition592 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_typeDefinition_in_predicateDefinition596 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_COMMA_in_predicateDefinition609 = new BitSet(new long[]{0x0000000000000100L});
    public static final BitSet FOLLOW_typeDefinition_in_predicateDefinition613 = new BitSet(new long[]{0x0000000000040010L});
    public static final BitSet FOLLOW_COMMA_in_predicateDefinition630 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_DOUBLEVAR_in_predicateDefinition634 = new BitSet(new long[]{0x0000000000040100L});
    public static final BitSet FOLLOW_ID_in_predicateDefinition639 = new BitSet(new long[]{0x0000000000040000L});
    public static final BitSet FOLLOW_18_in_predicateDefinition653 = new BitSet(new long[]{0x0000000000200002L});
    public static final BitSet FOLLOW_21_in_predicateDefinition664 = new BitSet(new long[]{0x0000000000000800L});
    public static final BitSet FOLLOW_NEX_in_predicateDefinition668 = new BitSet(new long[]{0x0000000000000002L});
    public static final BitSet FOLLOW_ID_in_typeDefinition720 = new BitSet(new long[]{0x0000000000000002L});

}