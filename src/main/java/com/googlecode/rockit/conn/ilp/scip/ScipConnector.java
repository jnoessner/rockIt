package com.googlecode.rockit.conn.ilp.scip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.conn.ilp.ILPConnector;
import com.googlecode.rockit.conn.ilp.ILPOperator;
import com.googlecode.rockit.conn.ilp.ILPVariable;
import com.googlecode.rockit.exception.ILPException;


public class ScipConnector extends ILPConnector
{

    private Map<String, Variable> variables   = new HashMap<String, Variable>();
    private double                objective   = 0;
    private List<String>          constraints = new ArrayList<String>();
    private static int            varCounter  = 0;
    private Map<String, String>   varNames    = new HashMap<String, String>();


    public ScipConnector() throws ILPException
    {
        super();
    }


    @Override
    public void initialize() throws ILPException
    {
    }


    @Override
    public void close() throws ILPException
    {
        variables = null;
    }


    @Override
    public void addStartValues(List<String> varNames) throws ILPException
    {
        throw new ILPException("SCIP: This feature (setting an initial soluation) is not implemented");
    }


    @Override
    public void writeModelToFile(String filename) throws ILPException
    {
        StringBuilder out = new StringBuilder();
        out.append(getObjectiveFormula() + System.lineSeparator());
        if(constraints.size() > 0) {
            out.append("Subject To" + System.lineSeparator());
            for(String c : constraints) {
                out.append("\t" + c + System.lineSeparator());
            }
        }

        StringBuilder intVars = new StringBuilder("General" + System.lineSeparator() + "\t");
        StringBuilder boolVars = new StringBuilder("Binaries" + System.lineSeparator() + "\t");
        boolean ints = false, bools = false;
        out.append("Bounds" + System.lineSeparator());

        for(Variable var : variables.values()) {
            out.append("\t" + var.lowerBound + " <= " + var.varName + " <= " + var.upperBound + System.lineSeparator());
            if(var.binary) {
                boolVars.append(var.varName + " ");
                bools = true;
            } else {
                intVars.append(var.varName + " ");
                ints = true;
            }
        }

        if(bools) {
            out.append(boolVars.toString() + System.lineSeparator());
        }
        if(ints) {
            out.append(intVars.toString() + System.lineSeparator());
        }

        out.append("End");

        Writer outWriter;
        try {
            outWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename, false), "UTF-8"));
            outWriter.write(out.toString());
            outWriter.flush();
            outWriter.close();
        } catch(Exception e) {
            e.printStackTrace();
            throw new ILPException();
        }
    }


    private String getObjectiveFormula()
    {
        StringBuilder objective = new StringBuilder();
        objective.append("Maximize" + System.lineSeparator() + "\t");
        for(Variable var : variables.values()) {
            if(var.objWeight >= 0) {
                objective.append(" + ");
            } else {
                objective.append(" - ");
            }
            objective.append(Math.abs(var.objWeight) + " " + var.varName);
        }
        return objective.toString();
    }


    @Override
    public void addVariable(String varName, double objWeight, double lowerBound, double upperBound, boolean override, boolean isZVar) throws ILPException
    {
        if(!variables.containsKey(varName) || override) {
            Variable var = new Variable(varName, objWeight, lowerBound, upperBound);
            variables.put(varName, var);
            varNames.put(var.varName, varName);
        }
    }


    @Override
    public void addConstraint(ArrayList<ILPVariable> lhs, ILPOperator operator, double rhs) throws ILPException
    {
        StringBuilder constraint = new StringBuilder();
        // constraint
        for(ILPVariable var : lhs) {
            if(!variables.containsKey(var.getName())) {
                this.addVariable(var.getName(), 0d, 0d, 1d, false, var.isZVar());
            }
            if(var.getValue() > 0) {
                constraint.append(" +");
            } else {
                constraint.append(" -");
            }
            constraint.append(Math.abs(var.getValue()) + " " + variables.get(var.getName()).varName);

        }

        switch(operator) {
            case LEQ:
                constraint.append(" <= " + rhs);
            break;
            case GEQ:
                constraint.append(" >= " + rhs);
            break;
            default:
            break;
        }
        constraints.add(constraint.toString());

    }


    @Override
    public HashMap<String, Integer> optimizeILP() throws ILPException
    {
        String path = System.getProperty("user.dir") + File.separator;
        String scipILP = path + "scip_ilp.lp";
        String scipBatch = path + "scip_batch.txt";
        String scipOut = path + "scip_out.txt";

        StringBuilder scip = new StringBuilder();
        scip.append("read " + scipILP + System.lineSeparator());
        scip.append("optimize" + System.lineSeparator());
        scip.append("write solution " + scipOut + System.lineSeparator());
        scip.append("q");

        writeModelToFile(scipILP);
        Writer outWriter;
        try {
            outWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(scipBatch, false), "UTF-8"));
            outWriter.write(scip.toString());
            outWriter.flush();
            outWriter.close();
        } catch(Exception e) {
            e.printStackTrace();
            throw new ILPException();
        }

        // start scip
        String OS = System.getProperty("os.name").toLowerCase();
        String command = null;
        if(OS.contains("windows")) {
            command = Parameters.SCIP;
        } else if(OS.contains("nix")) {
            command = Parameters.SCIP;
        } else {
            System.exit(-1);
        }

        try {
            Process process = new ProcessBuilder(command, "-b", scipBatch).start();

            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch(Exception e) {
            throw new ILPException();
        }
        System.out.println("SCIP is termaninated.");

        // read results
        Pattern pattern = Pattern.compile("(\\w*)\\s*(\\d*).*");
        HashMap<String, Integer> result = new HashMap<String, Integer>();

        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(scipOut), "UTF-8"));
            String line;
            int counter = 0;
            while((line = br.readLine()) != null) {
                counter++;
                if(counter == 1) {
                    System.out.println(line);
                } else if(counter == 2) {
                    System.out.println(line);
                    objective = Double.valueOf(line.replace("objective value:", "").trim());
                } else {
                    Matcher matcher = pattern.matcher(line);
                    if(matcher.find()) {
                        String var = matcher.group(1);
                        Integer value = Integer.valueOf(matcher.group(2));
                        result.put(varNames.get(var), value);
                    }
                }

            }
            br.close();
        } catch(Exception e) {
            throw new ILPException();
        }

        return result;
    }


    @Override
    public double getObjectiveValue()
    {
        return objective;
    }

    class Variable
    {
        String  varName;
        double  objWeight;
        double  lowerBound;
        double  upperBound;
        boolean binary;


        public Variable(String varName, double objWeight, double lowerBound, double upperBound)
        {
            this.varName = "x" + varCounter++;
            this.objWeight = objWeight;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.binary = (this.upperBound <= 1);
        }
    }
}
