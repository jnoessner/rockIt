package com.googlecode.rockit.app.result;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RockItResult
{
    private BigDecimal probability;
    private String     statement;
    private boolean    mapState;

    private final int  presicion = 10;


    public RockItResult(String statement)
    {
        this.probability = new BigDecimal(1);
        this.statement = statement;
        this.mapState = true;
    }


    public RockItResult(BigDecimal probability, String statement)
    {
        this.probability = probability.setScale(presicion, RoundingMode.HALF_UP);
        this.statement = statement;
        this.mapState = false;
    }


    public RockItResult(double probability, String statement)
    {
        this.probability = new BigDecimal(probability).setScale(presicion, RoundingMode.HALF_UP);
        this.statement = statement;
        this.mapState = false;
    }


    public BigDecimal getProbability()
    {
        return probability.stripTrailingZeros();
    }


    public String getStatement()
    {
        return statement;
    }


    public String toString()
    {
        if(mapState) {
            return statement;
        } else {
            return probability.stripTrailingZeros().toPlainString() + " " + statement;
        }
    }


    public String getPredicate()
    {
        return statement.substring(0, statement.indexOf("("));
    }


    public boolean equals(Object other)
    {
        boolean result = false;
        if(other instanceof RockItResult) {
            RockItResult otherResult = (RockItResult) other;
            result = (this.probability == otherResult.getProbability() && statement.equals(otherResult.getStatement()));
        }
        return result;
    }


    public List<String> getObjects()
    {
        List<String> objects = new ArrayList<String>();
        Pattern pattern = Pattern.compile("\"([^,]*)\"");
        Matcher matcher = pattern.matcher(statement);
        while(matcher.find()) {
            objects.add(matcher.group(1));
        }

        return objects;
    }
}
