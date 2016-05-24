// $ANTLR 3.4 com\\googlecode\\rockit\\parser\\GroundValues.g 2014-11-24 13:04:37

package com.googlecode.rockit.parser;
import java.util.TreeSet;
import java.util.ArrayList;
import com.googlecode.rockit.javaAPI.predicates.*;
import com.googlecode.rockit.javaAPI.types.*;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class GroundValuesParser extends Parser {
    public static final String[] tokenNames = new String[] {
        "<invalid>", "<EOR>", "<DOWN>", "<UP>", "FLOAT", "ID", "ML_COMMENT", "NEWLINE", "NOT", "SL_COMMENT", "STRING", "WS", "'('", "')'", "','"
    };

    public static final int EOF=-1;
    public static final int T__12=12;
    public static final int T__13=13;
    public static final int T__14=14;
    public static final int FLOAT=4;
    public static final int ID=5;
    public static final int ML_COMMENT=6;
    public static final int NEWLINE=7;
    public static final int NOT=8;
    public static final int SL_COMMENT=9;
    public static final int STRING=10;
    public static final int WS=11;

    // delegates
    public Parser[] getDelegates() {
        return new Parser[] {};
    }

    // delegators


    public GroundValuesParser(TokenStream input) {
        this(input, new RecognizerSharedState());
    }
    public GroundValuesParser(TokenStream input, RecognizerSharedState state) {
        super(input, state);
    }

    public String[] getTokenNames() { return GroundValuesParser.tokenNames; }
    public String getGrammarFileName() { return "com\\googlecode\\rockit\\parser\\GroundValues.g"; }


    TreeSet<PredicateAbstract> predicates = new TreeSet<PredicateAbstract>();
    HerbrandUniverse u = HerbrandUniverse.getInstance();



    // $ANTLR start "groundValues"
    // com\\googlecode\\rockit\\parser\\GroundValues.g:24:1: groundValues returns [TreeSet<PredicateAbstract> predicates] : ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | ( predicateDefinition ) )* ;
    public final TreeSet<PredicateAbstract> groundValues() throws ParseException, RecognitionException {
        TreeSet<PredicateAbstract> predicates = null;


        try {
            // com\\googlecode\\rockit\\parser\\GroundValues.g:24:83: ( ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | ( predicateDefinition ) )* )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:25:3: ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | ( predicateDefinition ) )*
            {
            // com\\googlecode\\rockit\\parser\\GroundValues.g:25:3: ( ( ML_COMMENT ) | ( SL_COMMENT ) | ( NEWLINE ) | ( predicateDefinition ) )*
            loop1:
            do {
                int alt1=5;
                switch ( input.LA(1) ) {
                case ML_COMMENT:
                    {
                    alt1=1;
                    }
                    break;
                case SL_COMMENT:
                    {
                    alt1=2;
                    }
                    break;
                case NEWLINE:
                    {
                    alt1=3;
                    }
                    break;
                case ID:
                case NOT:
                    {
                    alt1=4;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:26:4: ( ML_COMMENT )
            	    {
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:26:4: ( ML_COMMENT )
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:26:5: ML_COMMENT
            	    {
            	    match(input,ML_COMMENT,FOLLOW_ML_COMMENT_in_groundValues51); 

            	    }


            	    }
            	    break;
            	case 2 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:26:19: ( SL_COMMENT )
            	    {
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:26:19: ( SL_COMMENT )
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:26:20: SL_COMMENT
            	    {
            	    match(input,SL_COMMENT,FOLLOW_SL_COMMENT_in_groundValues57); 

            	    }


            	    }
            	    break;
            	case 3 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:26:34: ( NEWLINE )
            	    {
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:26:34: ( NEWLINE )
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:26:35: NEWLINE
            	    {
            	    match(input,NEWLINE,FOLLOW_NEWLINE_in_groundValues63); 

            	    }


            	    }
            	    break;
            	case 4 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:27:4: ( predicateDefinition )
            	    {
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:27:4: ( predicateDefinition )
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:27:5: predicateDefinition
            	    {
            	    pushFollow(FOLLOW_predicateDefinition_in_groundValues72);
            	    predicateDefinition();

            	    state._fsp--;


            	    predicates = this.predicates;

            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
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
        return predicates;
    }
    // $ANTLR end "groundValues"



    // $ANTLR start "predicateDefinition"
    // com\\googlecode\\rockit\\parser\\GroundValues.g:33:1: predicateDefinition : ( (n= NOT )? ) pid= ID '(' (t1= ID |s1= STRING ) ( ',' (t2= ID |s1= STRING ) )* ( ',' f= FLOAT )? ')' ;
    public final void predicateDefinition() throws ParseException, RecognitionException {
        Token n=null;
        Token pid=null;
        Token t1=null;
        Token s1=null;
        Token t2=null;
        Token f=null;

        try {
            // com\\googlecode\\rockit\\parser\\GroundValues.g:33:42: ( ( (n= NOT )? ) pid= ID '(' (t1= ID |s1= STRING ) ( ',' (t2= ID |s1= STRING ) )* ( ',' f= FLOAT )? ')' )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:34:8: ( (n= NOT )? ) pid= ID '(' (t1= ID |s1= STRING ) ( ',' (t2= ID |s1= STRING ) )* ( ',' f= FLOAT )? ')'
            {
            // com\\googlecode\\rockit\\parser\\GroundValues.g:34:8: ( (n= NOT )? )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:34:9: (n= NOT )?
            {
            // com\\googlecode\\rockit\\parser\\GroundValues.g:34:11: (n= NOT )?
            int alt2=2;
            switch ( input.LA(1) ) {
                case NOT:
                    {
                    alt2=1;
                    }
                    break;
            }

            switch (alt2) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\GroundValues.g:34:11: n= NOT
                    {
                    n=(Token)match(input,NOT,FOLLOW_NOT_in_predicateDefinition121); 

                    }
                    break;

            }


            }


            pid=(Token)match(input,ID,FOLLOW_ID_in_predicateDefinition127); 


                                PredicateAbstract predicateAbstract = null;
                                String name = pid.getText();
                                if(n != null){
                                  name = "!"+name;
                                } 
                                ArrayList<String> line = new ArrayList<String>();
                                
                               

            match(input,12,FOLLOW_12_in_predicateDefinition139); 

            // com\\googlecode\\rockit\\parser\\GroundValues.g:43:13: (t1= ID |s1= STRING )
            int alt3=2;
            switch ( input.LA(1) ) {
            case ID:
                {
                alt3=1;
                }
                break;
            case STRING:
                {
                alt3=2;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 3, 0, input);

                throw nvae;

            }

            switch (alt3) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\GroundValues.g:43:14: t1= ID
                    {
                    t1=(Token)match(input,ID,FOLLOW_ID_in_predicateDefinition144); 

                    line.add(u.getKey(t1.getText()));

                    }
                    break;
                case 2 :
                    // com\\googlecode\\rockit\\parser\\GroundValues.g:43:57: s1= STRING
                    {
                    s1=(Token)match(input,STRING,FOLLOW_STRING_in_predicateDefinition150); 

                     line.add(u.getKey(s1.getText().replace("\"", "")));

                    }
                    break;

            }


            // com\\googlecode\\rockit\\parser\\GroundValues.g:44:9: ( ',' (t2= ID |s1= STRING ) )*
            loop5:
            do {
                int alt5=2;
                switch ( input.LA(1) ) {
                case 14:
                    {
                    switch ( input.LA(2) ) {
                    case ID:
                    case STRING:
                        {
                        alt5=1;
                        }
                        break;

                    }

                    }
                    break;

                }

                switch (alt5) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:44:10: ',' (t2= ID |s1= STRING )
            	    {
            	    match(input,14,FOLLOW_14_in_predicateDefinition164); 

            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:44:14: (t2= ID |s1= STRING )
            	    int alt4=2;
            	    switch ( input.LA(1) ) {
            	    case ID:
            	        {
            	        alt4=1;
            	        }
            	        break;
            	    case STRING:
            	        {
            	        alt4=2;
            	        }
            	        break;
            	    default:
            	        NoViableAltException nvae =
            	            new NoViableAltException("", 4, 0, input);

            	        throw nvae;

            	    }

            	    switch (alt4) {
            	        case 1 :
            	            // com\\googlecode\\rockit\\parser\\GroundValues.g:44:15: t2= ID
            	            {
            	            t2=(Token)match(input,ID,FOLLOW_ID_in_predicateDefinition169); 

            	            line.add(u.getKey(t2.getText()));

            	            }
            	            break;
            	        case 2 :
            	            // com\\googlecode\\rockit\\parser\\GroundValues.g:44:58: s1= STRING
            	            {
            	            s1=(Token)match(input,STRING,FOLLOW_STRING_in_predicateDefinition175); 

            	             line.add(u.getKey(s1.getText().replace("\"", "")));

            	            }
            	            break;

            	    }


            	    }
            	    break;

            	default :
            	    break loop5;
                }
            } while (true);


            // com\\googlecode\\rockit\\parser\\GroundValues.g:45:9: ( ',' f= FLOAT )?
            int alt6=2;
            switch ( input.LA(1) ) {
                case 14:
                    {
                    alt6=1;
                    }
                    break;
            }

            switch (alt6) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\GroundValues.g:45:10: ',' f= FLOAT
                    {
                    match(input,14,FOLLOW_14_in_predicateDefinition193); 

                    f=(Token)match(input,FLOAT,FOLLOW_FLOAT_in_predicateDefinition197); 

                    }
                    break;

            }



                     PredicateAbstract search = null;
                     Double value = 0d;
                     if(f != null){
                      search = new PredicateDouble(name,false);
                      value = Double.parseDouble(f.getText());
                     }else{
                      search = new Predicate(name,false);
                     }
                     if(predicates.contains(search)){
                      predicateAbstract = predicates.ceiling(search);                       
                     }else{
                      predicates.add(search);
                      predicateAbstract = search;
                     }
                     if(predicateAbstract instanceof Predicate){
                      ((Predicate) predicateAbstract).addGroundValueLine(line);
                     }else if(predicateAbstract instanceof PredicateDouble){
                      ((PredicateDouble) predicateAbstract).addGroundValueLine(value, line);
                     }
                    

            match(input,13,FOLLOW_13_in_predicateDefinition210); 

            }

        }
        catch (RecognitionException re) {
            reportError(re);
            recover(input,re);
        }

        finally {
        	// do for sure before leaving
        }
        return ;
    }
    // $ANTLR end "predicateDefinition"

    // Delegated rules


 

    public static final BitSet FOLLOW_ML_COMMENT_in_groundValues51 = new BitSet(new long[]{0x00000000000003E2L});
    public static final BitSet FOLLOW_SL_COMMENT_in_groundValues57 = new BitSet(new long[]{0x00000000000003E2L});
    public static final BitSet FOLLOW_NEWLINE_in_groundValues63 = new BitSet(new long[]{0x00000000000003E2L});
    public static final BitSet FOLLOW_predicateDefinition_in_groundValues72 = new BitSet(new long[]{0x00000000000003E2L});
    public static final BitSet FOLLOW_NOT_in_predicateDefinition121 = new BitSet(new long[]{0x0000000000000020L});
    public static final BitSet FOLLOW_ID_in_predicateDefinition127 = new BitSet(new long[]{0x0000000000001000L});
    public static final BitSet FOLLOW_12_in_predicateDefinition139 = new BitSet(new long[]{0x0000000000000420L});
    public static final BitSet FOLLOW_ID_in_predicateDefinition144 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_STRING_in_predicateDefinition150 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_14_in_predicateDefinition164 = new BitSet(new long[]{0x0000000000000420L});
    public static final BitSet FOLLOW_ID_in_predicateDefinition169 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_STRING_in_predicateDefinition175 = new BitSet(new long[]{0x0000000000006000L});
    public static final BitSet FOLLOW_14_in_predicateDefinition193 = new BitSet(new long[]{0x0000000000000010L});
    public static final BitSet FOLLOW_FLOAT_in_predicateDefinition197 = new BitSet(new long[]{0x0000000000002000L});
    public static final BitSet FOLLOW_13_in_predicateDefinition210 = new BitSet(new long[]{0x0000000000000002L});

}