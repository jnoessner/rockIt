grammar GroundValues;

// START:members
@header {
package com.googlecode.rockit.parser;
import java.util.TreeSet;
import java.util.ArrayList;
import com.googlecode.rockit.javaAPI.predicates.*;
import com.googlecode.rockit.javaAPI.types.*;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.javaAPI.HerbrandUniverse;
}
@lexer::header{ 
package com.googlecode.rockit.parser; 
} 

@members {
TreeSet<PredicateAbstract> predicates = new TreeSet<PredicateAbstract>();
HerbrandUniverse u = HerbrandUniverse.getInstance();
}
// END:members


groundValues returns [TreeSet<PredicateAbstract> predicates] throws ParseException:   
  (
   (ML_COMMENT) | (SL_COMMENT) | (NEWLINE) |
   (predicateDefinition{predicates = this.predicates;})
  )
  * ;
          

// predicateDefinition:  NOT? ID '(' STRING (',' STRING )* (',' FLOAT)? ')'
predicateDefinition throws ParseException:   
       (n = NOT?) pid=ID {
                    PredicateAbstract predicateAbstract = null;
                    String name = $pid.getText();
                    if($n != null){
                      name = "!"+name;
                    } 
                    ArrayList<String> line = new ArrayList<String>();
                    
                   }
        '(' (t1=ID {line.add(u.getKey($t1.getText()));}|s1=STRING { line.add(u.getKey($s1.getText().replace("\"", "")));})
        (',' (t2=ID {line.add(u.getKey($t2.getText()));}|s1=STRING { line.add(u.getKey($s1.getText().replace("\"", "")));}) )* 
        (',' f=FLOAT)?
        {
         PredicateAbstract search = null;
         Double value = 0d;
         if($f != null){
          search = new PredicateDouble(name,false);
          value = Double.parseDouble($f.getText());
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
        }')'
    ;


// START:tokens
ID  :   ('a'..'z'|'A'..'Z')('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'-')*;
STRING:  '"' (options {greedy=false;} : .)* ('"'|')'|',') ;
NOT:  '!';
NEWLINE: '\n';
WS  :   (' '|'\t'|'\r')+ {skip();} ;
FLOAT
  : ('+'|'-')?( '0'..'9' )+ ( '.' ( '0'..'9' )* )?
  ;
ML_COMMENT
    :   '/*' (options {greedy=false;} : .)* '*/' {$channel=HIDDEN;}
    ;
SL_COMMENT: '//' ~(NEWLINE)* ;
// END:tokens