package com.googlecode.rockit.exception;

import org.antlr.runtime.Token;


public class Messages
{
    public static String printDatabaseExceptionError(String sqlQuery, String message)
    {
        return "\n===========================================================================\n" + "RockIt terminated with an error in the SQL Query. There are two reasons for this error:\n"
                + "(a) The most common reason for this is that your model is too large. In this case you will get timeout errors or 'SQLException: Incorrect key file for table' errors.\n"
                + "    Either your -data file contains too many entries or your -input file contains formulas with too many variables (more than 3 variables might be already critical)\n" + "    Please make your -data file much smaller (not more than 20 entries) and try again.\n\n"
                + "(b) If you still get an SQL Error (typically saying 'You have an error in your SQL syntax'), this is presumably a bug.\n" + "    Please generate a ticket at https://code.google.com/p/rockit/issues/list and upload your MLN and data file. \n"
                + "    Furthermore, please post the following error messages into the ticket:" + "\n - Query " + sqlQuery + "\n - Message " + message + "\nThank you for your help in improving RockIt.\n===========================================================================\n";
    }


    public static String printParseExceptionError(String individualMessage)
    {
        return "\n===========================================================================\n" + "RockIt terminated with a parse exception either in your -input or -data file. \n"
                + "\nPlease also check for messages like 'line X:Y ...' (where X is the line number and Y the position within the line) above and address them first.\n" + "---------------------------------------------------------\n" + individualMessage + "\n---------------------------------------------------------\n"
                + "If you are not able to identify your parsing error, please generate a ticket at https://code.google.com/p/rockit/issues/list, upload your MLN and data file in this ticket, and post the error message. " + "\n===========================================================================\n";
    }


    public static String printTokenDetails(Token token)
    {
        return token.getText() + " at line " + token.getLine() + " and position " + token.getCharPositionInLine();
    }

}
