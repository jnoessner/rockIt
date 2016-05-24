// $ANTLR 3.4 com\\googlecode\\rockit\\parser\\GroundValues.g 2014-11-24 13:04:37
 
package com.googlecode.rockit.parser; 


import org.antlr.runtime.*;
import java.util.Stack;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked"})
public class GroundValuesLexer extends Lexer {
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
    // delegators
    public Lexer[] getDelegates() {
        return new Lexer[] {};
    }

    public GroundValuesLexer() {} 
    public GroundValuesLexer(CharStream input) {
        this(input, new RecognizerSharedState());
    }
    public GroundValuesLexer(CharStream input, RecognizerSharedState state) {
        super(input,state);
    }
    public String getGrammarFileName() { return "com\\googlecode\\rockit\\parser\\GroundValues.g"; }

    // $ANTLR start "T__12"
    public final void mT__12() throws RecognitionException {
        try {
            int _type = T__12;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:6:7: ( '(' )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:6:9: '('
            {
            match('('); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__12"

    // $ANTLR start "T__13"
    public final void mT__13() throws RecognitionException {
        try {
            int _type = T__13;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:7:7: ( ')' )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:7:9: ')'
            {
            match(')'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__13"

    // $ANTLR start "T__14"
    public final void mT__14() throws RecognitionException {
        try {
            int _type = T__14;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:8:7: ( ',' )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:8:9: ','
            {
            match(','); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "T__14"

    // $ANTLR start "ID"
    public final void mID() throws RecognitionException {
        try {
            int _type = ID;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:71:5: ( ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )* )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:71:9: ( 'a' .. 'z' | 'A' .. 'Z' ) ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            {
            if ( (input.LA(1) >= 'A' && input.LA(1) <= 'Z')||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            // com\\googlecode\\rockit\\parser\\GroundValues.g:71:28: ( 'a' .. 'z' | 'A' .. 'Z' | '0' .. '9' | '_' | '-' )*
            loop1:
            do {
                int alt1=2;
                switch ( input.LA(1) ) {
                case '-':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                case 'A':
                case 'B':
                case 'C':
                case 'D':
                case 'E':
                case 'F':
                case 'G':
                case 'H':
                case 'I':
                case 'J':
                case 'K':
                case 'L':
                case 'M':
                case 'N':
                case 'O':
                case 'P':
                case 'Q':
                case 'R':
                case 'S':
                case 'T':
                case 'U':
                case 'V':
                case 'W':
                case 'X':
                case 'Y':
                case 'Z':
                case '_':
                case 'a':
                case 'b':
                case 'c':
                case 'd':
                case 'e':
                case 'f':
                case 'g':
                case 'h':
                case 'i':
                case 'j':
                case 'k':
                case 'l':
                case 'm':
                case 'n':
                case 'o':
                case 'p':
                case 'q':
                case 'r':
                case 's':
                case 't':
                case 'u':
                case 'v':
                case 'w':
                case 'x':
                case 'y':
                case 'z':
                    {
                    alt1=1;
                    }
                    break;

                }

                switch (alt1) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:
            	    {
            	    if ( input.LA(1)=='-'||(input.LA(1) >= '0' && input.LA(1) <= '9')||(input.LA(1) >= 'A' && input.LA(1) <= 'Z')||input.LA(1)=='_'||(input.LA(1) >= 'a' && input.LA(1) <= 'z') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop1;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ID"

    // $ANTLR start "STRING"
    public final void mSTRING() throws RecognitionException {
        try {
            int _type = STRING;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:72:7: ( '\"' ( options {greedy=false; } : . )* ( '\"' | ')' | ',' ) )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:72:10: '\"' ( options {greedy=false; } : . )* ( '\"' | ')' | ',' )
            {
            match('\"'); 

            // com\\googlecode\\rockit\\parser\\GroundValues.g:72:14: ( options {greedy=false; } : . )*
            loop2:
            do {
                int alt2=2;
                int LA2_0 = input.LA(1);

                if ( (LA2_0=='\"'||LA2_0==')'||LA2_0==',') ) {
                    alt2=2;
                }
                else if ( ((LA2_0 >= '\u0000' && LA2_0 <= '!')||(LA2_0 >= '#' && LA2_0 <= '(')||(LA2_0 >= '*' && LA2_0 <= '+')||(LA2_0 >= '-' && LA2_0 <= '\uFFFF')) ) {
                    alt2=1;
                }


                switch (alt2) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:72:41: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop2;
                }
            } while (true);


            if ( input.LA(1)=='\"'||input.LA(1)==')'||input.LA(1)==',' ) {
                input.consume();
            }
            else {
                MismatchedSetException mse = new MismatchedSetException(null,input);
                recover(mse);
                throw mse;
            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "STRING"

    // $ANTLR start "NOT"
    public final void mNOT() throws RecognitionException {
        try {
            int _type = NOT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:73:4: ( '!' )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:73:7: '!'
            {
            match('!'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "NEWLINE"
    public final void mNEWLINE() throws RecognitionException {
        try {
            int _type = NEWLINE;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:74:8: ( '\\n' )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:74:10: '\\n'
            {
            match('\n'); 

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "NEWLINE"

    // $ANTLR start "WS"
    public final void mWS() throws RecognitionException {
        try {
            int _type = WS;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:75:5: ( ( ' ' | '\\t' | '\\r' )+ )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:75:9: ( ' ' | '\\t' | '\\r' )+
            {
            // com\\googlecode\\rockit\\parser\\GroundValues.g:75:9: ( ' ' | '\\t' | '\\r' )+
            int cnt3=0;
            loop3:
            do {
                int alt3=2;
                switch ( input.LA(1) ) {
                case '\t':
                case '\r':
                case ' ':
                    {
                    alt3=1;
                    }
                    break;

                }

                switch (alt3) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:
            	    {
            	    if ( input.LA(1)=='\t'||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt3 >= 1 ) break loop3;
                        EarlyExitException eee =
                            new EarlyExitException(3, input);
                        throw eee;
                }
                cnt3++;
            } while (true);


            skip();

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "WS"

    // $ANTLR start "FLOAT"
    public final void mFLOAT() throws RecognitionException {
        try {
            int _type = FLOAT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:77:3: ( ( '+' | '-' )? ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )? )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:77:5: ( '+' | '-' )? ( '0' .. '9' )+ ( '.' ( '0' .. '9' )* )?
            {
            // com\\googlecode\\rockit\\parser\\GroundValues.g:77:5: ( '+' | '-' )?
            int alt4=2;
            switch ( input.LA(1) ) {
                case '+':
                case '-':
                    {
                    alt4=1;
                    }
                    break;
            }

            switch (alt4) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\GroundValues.g:
                    {
                    if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
                        input.consume();
                    }
                    else {
                        MismatchedSetException mse = new MismatchedSetException(null,input);
                        recover(mse);
                        throw mse;
                    }


                    }
                    break;

            }


            // com\\googlecode\\rockit\\parser\\GroundValues.g:77:15: ( '0' .. '9' )+
            int cnt5=0;
            loop5:
            do {
                int alt5=2;
                switch ( input.LA(1) ) {
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    {
                    alt5=1;
                    }
                    break;

                }

                switch (alt5) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:
            	    {
            	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    if ( cnt5 >= 1 ) break loop5;
                        EarlyExitException eee =
                            new EarlyExitException(5, input);
                        throw eee;
                }
                cnt5++;
            } while (true);


            // com\\googlecode\\rockit\\parser\\GroundValues.g:77:29: ( '.' ( '0' .. '9' )* )?
            int alt7=2;
            switch ( input.LA(1) ) {
                case '.':
                    {
                    alt7=1;
                    }
                    break;
            }

            switch (alt7) {
                case 1 :
                    // com\\googlecode\\rockit\\parser\\GroundValues.g:77:31: '.' ( '0' .. '9' )*
                    {
                    match('.'); 

                    // com\\googlecode\\rockit\\parser\\GroundValues.g:77:35: ( '0' .. '9' )*
                    loop6:
                    do {
                        int alt6=2;
                        switch ( input.LA(1) ) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            {
                            alt6=1;
                            }
                            break;

                        }

                        switch (alt6) {
                    	case 1 :
                    	    // com\\googlecode\\rockit\\parser\\GroundValues.g:
                    	    {
                    	    if ( (input.LA(1) >= '0' && input.LA(1) <= '9') ) {
                    	        input.consume();
                    	    }
                    	    else {
                    	        MismatchedSetException mse = new MismatchedSetException(null,input);
                    	        recover(mse);
                    	        throw mse;
                    	    }


                    	    }
                    	    break;

                    	default :
                    	    break loop6;
                        }
                    } while (true);


                    }
                    break;

            }


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "FLOAT"

    // $ANTLR start "ML_COMMENT"
    public final void mML_COMMENT() throws RecognitionException {
        try {
            int _type = ML_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:80:5: ( '/*' ( options {greedy=false; } : . )* '*/' )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:80:9: '/*' ( options {greedy=false; } : . )* '*/'
            {
            match("/*"); 



            // com\\googlecode\\rockit\\parser\\GroundValues.g:80:14: ( options {greedy=false; } : . )*
            loop8:
            do {
                int alt8=2;
                int LA8_0 = input.LA(1);

                if ( (LA8_0=='*') ) {
                    int LA8_1 = input.LA(2);

                    if ( (LA8_1=='/') ) {
                        alt8=2;
                    }
                    else if ( ((LA8_1 >= '\u0000' && LA8_1 <= '.')||(LA8_1 >= '0' && LA8_1 <= '\uFFFF')) ) {
                        alt8=1;
                    }


                }
                else if ( ((LA8_0 >= '\u0000' && LA8_0 <= ')')||(LA8_0 >= '+' && LA8_0 <= '\uFFFF')) ) {
                    alt8=1;
                }


                switch (alt8) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:80:41: .
            	    {
            	    matchAny(); 

            	    }
            	    break;

            	default :
            	    break loop8;
                }
            } while (true);


            match("*/"); 



            _channel=HIDDEN;

            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "ML_COMMENT"

    // $ANTLR start "SL_COMMENT"
    public final void mSL_COMMENT() throws RecognitionException {
        try {
            int _type = SL_COMMENT;
            int _channel = DEFAULT_TOKEN_CHANNEL;
            // com\\googlecode\\rockit\\parser\\GroundValues.g:82:11: ( '//' (~ ( NEWLINE ) )* )
            // com\\googlecode\\rockit\\parser\\GroundValues.g:82:13: '//' (~ ( NEWLINE ) )*
            {
            match("//"); 



            // com\\googlecode\\rockit\\parser\\GroundValues.g:82:18: (~ ( NEWLINE ) )*
            loop9:
            do {
                int alt9=2;
                int LA9_0 = input.LA(1);

                if ( ((LA9_0 >= '\u0000' && LA9_0 <= '\t')||(LA9_0 >= '\u000B' && LA9_0 <= '\uFFFF')) ) {
                    alt9=1;
                }


                switch (alt9) {
            	case 1 :
            	    // com\\googlecode\\rockit\\parser\\GroundValues.g:
            	    {
            	    if ( (input.LA(1) >= '\u0000' && input.LA(1) <= '\t')||(input.LA(1) >= '\u000B' && input.LA(1) <= '\uFFFF') ) {
            	        input.consume();
            	    }
            	    else {
            	        MismatchedSetException mse = new MismatchedSetException(null,input);
            	        recover(mse);
            	        throw mse;
            	    }


            	    }
            	    break;

            	default :
            	    break loop9;
                }
            } while (true);


            }

            state.type = _type;
            state.channel = _channel;
        }
        finally {
        	// do for sure before leaving
        }
    }
    // $ANTLR end "SL_COMMENT"

    public void mTokens() throws RecognitionException {
        // com\\googlecode\\rockit\\parser\\GroundValues.g:1:8: ( T__12 | T__13 | T__14 | ID | STRING | NOT | NEWLINE | WS | FLOAT | ML_COMMENT | SL_COMMENT )
        int alt10=11;
        switch ( input.LA(1) ) {
        case '(':
            {
            alt10=1;
            }
            break;
        case ')':
            {
            alt10=2;
            }
            break;
        case ',':
            {
            alt10=3;
            }
            break;
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
        case 'G':
        case 'H':
        case 'I':
        case 'J':
        case 'K':
        case 'L':
        case 'M':
        case 'N':
        case 'O':
        case 'P':
        case 'Q':
        case 'R':
        case 'S':
        case 'T':
        case 'U':
        case 'V':
        case 'W':
        case 'X':
        case 'Y':
        case 'Z':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'g':
        case 'h':
        case 'i':
        case 'j':
        case 'k':
        case 'l':
        case 'm':
        case 'n':
        case 'o':
        case 'p':
        case 'q':
        case 'r':
        case 's':
        case 't':
        case 'u':
        case 'v':
        case 'w':
        case 'x':
        case 'y':
        case 'z':
            {
            alt10=4;
            }
            break;
        case '\"':
            {
            alt10=5;
            }
            break;
        case '!':
            {
            alt10=6;
            }
            break;
        case '\n':
            {
            alt10=7;
            }
            break;
        case '\t':
        case '\r':
        case ' ':
            {
            alt10=8;
            }
            break;
        case '+':
        case '-':
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            {
            alt10=9;
            }
            break;
        case '/':
            {
            switch ( input.LA(2) ) {
            case '*':
                {
                alt10=10;
                }
                break;
            case '/':
                {
                alt10=11;
                }
                break;
            default:
                NoViableAltException nvae =
                    new NoViableAltException("", 10, 10, input);

                throw nvae;

            }

            }
            break;
        default:
            NoViableAltException nvae =
                new NoViableAltException("", 10, 0, input);

            throw nvae;

        }

        switch (alt10) {
            case 1 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:10: T__12
                {
                mT__12(); 


                }
                break;
            case 2 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:16: T__13
                {
                mT__13(); 


                }
                break;
            case 3 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:22: T__14
                {
                mT__14(); 


                }
                break;
            case 4 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:28: ID
                {
                mID(); 


                }
                break;
            case 5 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:31: STRING
                {
                mSTRING(); 


                }
                break;
            case 6 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:38: NOT
                {
                mNOT(); 


                }
                break;
            case 7 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:42: NEWLINE
                {
                mNEWLINE(); 


                }
                break;
            case 8 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:50: WS
                {
                mWS(); 


                }
                break;
            case 9 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:53: FLOAT
                {
                mFLOAT(); 


                }
                break;
            case 10 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:59: ML_COMMENT
                {
                mML_COMMENT(); 


                }
                break;
            case 11 :
                // com\\googlecode\\rockit\\parser\\GroundValues.g:1:70: SL_COMMENT
                {
                mSL_COMMENT(); 


                }
                break;

        }

    }


 

}