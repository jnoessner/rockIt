package com.googlecode.rockit.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.app.evaluator.ObjectiveEvaluator;
import com.googlecode.rockit.app.solver.StandardSolver;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.file.ResultFileWriter;
import com.googlecode.rockit.javaAPI.Model;
import com.googlecode.rockit.parser.SyntaxReader;


public class StandardSolverTest
{

    public static long runtime;


    public static String test(String modelFile, String evidenceFile, double gap, boolean useCPI, boolean useZVarAgg) throws ParseException, IOException, RecognitionException, SolveException, SQLException, ReadOrWriteToFileException
    {

        Parameters.USE_CUTTING_PLANE_AGGREGATION = useZVarAgg;
        StringBuilder result = new StringBuilder();
        result.append(modelFile).append(";").append(evidenceFile).append(";");

        // parameters
        Parameters.GAP = gap;
        Parameters.USE_CUTTING_PLANE_INFERENCE = useCPI;
        Parameters.USE_CUTTING_PLANE_AGGREGATION = useZVarAgg;

        result.append(gap).append(";");
        if(!Parameters.USE_CUTTING_PLANE_AGGREGATION) {
            result.append("no ");
        }
        result.append("z aggr;");
        if(!Parameters.USE_CUTTING_PLANE_INFERENCE) {
            result.append("no ");
        }
        result.append("cpi;");

        System.out.println(result.toString());

        Long start = System.currentTimeMillis();

        SyntaxReader reader = new SyntaxReader();
        Model model = reader.getModel(modelFile, evidenceFile);

        StandardSolver solver = new StandardSolver(model);
        ArrayList<String> res = solver.solve();
        solver.close();

        ResultFileWriter writer = new ResultFileWriter("output.db");
        writer.printResultFile(res);

        long runtimeRockit = System.currentTimeMillis() - start;

        double objRockit = 0;

        // evaluation
        System.out.println("=ROCKIT EVAL=CPI " + Parameters.USE_CUTTING_PLANE_INFERENCE + "=Z-VAR AGGR " + Parameters.USE_CUTTING_PLANE_AGGREGATION + "============================================");
        ObjectiveEvaluator eval = new ObjectiveEvaluator();
        objRockit = eval.evaluate(modelFile, evidenceFile, "output.db");

        /*
         * result.append(runtimeRockit).append(";objSoft;");
         * result.append(objRockit).append(";objHard;").append(eval.getHardObjective()).append(";PrepTime;");
         * result.append(solver.getPreparationTime()).append(";ILPTime;");
         * result.append(solver.getIlpSolverTime()).append(";JavaTime;");
         * result.append(solver.getJavaCodeTime()).append(";SQLTime;");
         * result.append(solver.getSqlQueryTime());
         */
        result.append(";runtime;").append(runtimeRockit).append(";objective;").append(objRockit).append(";hardObjective;").append(eval.getHardObjective());
        System.out.println(result.toString());
        runtime = runtimeRockit;

        return result.toString();
    }
}
