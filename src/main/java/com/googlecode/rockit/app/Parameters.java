package com.googlecode.rockit.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import com.googlecode.rockit.conn.ilp.ILPConnector.ILPSolver;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;


public class Parameters
{

    public static String    PROPERTYFILE                             = "rockit.properties";

    // ================= PARAMETERS CONFIGURABLE IN PROPERTYFILE

    /**
     * Path to store temporary files, which are deleted again after program execution.
     */
    public static String    TEMP_PATH                                = "temp/";

    /**
     * Enables cutting-plane inference (do not add all constraints at once but add them step by step)
     * # Advantage: Problems are much smaller, thus much faster to solve
     * # Disadvantage: More than one problem has to be solved (but usually the overall time is still smaller)
     */
    public static boolean   USE_CUTTING_PLANE_INFERENCE              = true;

    /**
     * If activated, z variables will be aggregated.
     * 
     * This is usually more effective than the reduction of z variables (Z_VARIABLES_REDUCTION).
     * Please note that these two techniques can not be combined. If both are set to true, only
     * Z_VARIABLES_AGGREGATION will be applied.
     * 
     * Advantage: Less formulas in ILP, ILP runs faster.
     * Disadvantage: No known disadvantage. Maybe a slightly slower Java Code.
     * 
     */
    public static boolean   USE_CUTTING_PLANE_AGGREGATION            = true;

    /**
     * Time limit in seconds. Limits the total time expended for the gurobi solver (in seconds).
     * Note that this only limits the time of the gurobi solver. Since there can be multiple calls of the gurobi solver, the overall runtime
     * is usually larger.
     * If -1 is set, time limit is infinitive.
     */
    public static double    TIME_LIMIT                               = -1;

    /**
     * Sets the gap of the Gurobi solver. This gives exacter (value around 0.000001) but slower or
     * more approximative (value 0.01) but faster solutions.
     * 
     * If the gap is set to -1, the standard Gurobi gap is used.
     * 
     * HINT: If your problem does not terminate, then increase this value (take for instance 0.01)
     * 
     * In particular, the following Parameters are set.
     * 
     * model.getEnv().set(GRB.DoubleParam.FeasibilityTol, 1e-6);
     * model.getEnv().set(GRB.DoubleParam.IntFeasTol, 1e-6);
     * model.getEnv().set(GRB.DoubleParam.MIPGap, 1e-6);
     * model.getEnv().set(GRB.DoubleParam.OptimalityTol, 1e-6);
     */
    public static double    GAP                                      = -1;

    /**
     * If false, we compute the MAP State with the standard solver. If true, we perform learning.
     */
    public static boolean   USE_LEARNING                             = false;

    /**
     * Enables larger debug output.
     */
    public static boolean   DEBUG_OUTPUT                             = false;

    /**
     * Simplifies formulas with negatives weights like it is done in classic Markov Logic solvers like Tuffy or Alchemy.
     * 
     * 
     * //-2 student(a1) v !advisedBy(a2,a1) v !advisedBy(a1,a2)
     * //-1 student(a1) v !advisedBy(a1,a2)
     * //-1 student(a1) v !advisedBy(a2,a1)
     * 
     * //1 student(a1) v advisedBy(a1,a2)
     * //1 student(a1) v advisedBy(a2,a1)
     * 
     * The formulars with negative weights are changed so that:
     * - Their weight is multiplied with -1 (made positive).
     * - Each non-negated hidden predicate (in the restriction part) is set to its negation .
     * - Each negated hidden predicate (in the restriction part) is set to be not negated.
     * - Formular becomes a conjunction
     * 
     * The formulas with conjunction are changed in a way that:
     * - The weight is divided by the number of hidden predicates.
     * - For each predicate a new formula is created.
     * 
     */
    public static boolean   SIMPLIFY_NEGATIVE_WEIGHT_AND_CONJUNCTION = true;

    /**
     * If activated, the evidence occuring in ground formulas are deleted.
     * This results in a lower number of ground formulas and easier ground formulas due to omitted literals.
     * 
     * The disadvantage is higher runtime due to dublicate detection mechanisms required for each ground formula.
     * Furthermore, most of the evidence detection is later on done by the ILP in a very effective way.
     */
    public static boolean   LEVERAGE_EVIDENCE                        = true;

    /**
	 * 
	 */
    public static boolean   CONVERT_STRING_VALUES                    = true;

    /**
     * Database configurations: username
     */
    public static String    SQL_USERNAME                             = "root";

    /**
     * Database configurations: password
     */
    public static String    SQL_PASSWORD                             = "mannheim1234";
    /**
     * Database configurations: database name
     */
    public static String    SQL_DATABASE                             = "rockit";
    /**
     * Database configurations: url to database system
     */
    public static String    SQL_URL                                  = "jdbc:mysql://127.0.0.1/";

    public static int       THREAD_NUMBER                            = -1;

    // ================= PARAMETERS NOT CONFIGURABLE FOR USER

    // ================= SAMPLING PARAMETERS

    public static boolean   USE_SAMPLING                             = false;

    public static boolean   USE_SYMMETRIES_IN_MARGINAL_INFERENCE     = true;

    // ================= LEARNING PARAMETERS

    /**
     * In the Objective Evaluator instances of hard formulas will get a weight of 100
     */
    public static double    EVALUATION_WEIGHT_FOR_HARD_FORMULA       = 100000;

    /**
     * Learning rate
     */
    public static double    LEARNING_RATE                            = 1.0;

    /**
     * Algorithm used to solve continuous models or the root node of a MIP model
     * (-1=automatic, 0=primal simplex, 1=dual simplex, 2=barrier, 3=concurrent, 4=deterministic concurrent).
     * Concurrent optimizers run multiple solvers on multiple threads simultaneously, and choose the one that
     * finishes first. Deterministic concurrent (Method=4) gives the exact same result each time, while Method=3
     * is often faster but can produce different optimal bases when run multiple times. In the current release,
     * the default Automatic (-1) will typically choose non-deterministic concurrent (Method=3) for an LP, barrier
     * (Method=2) for a QP, and dual (Method=1) for the MIP root node. Only simplex and barrier algorithms are available
     * for continuous QP models. Only primal and dual simplex are available for solving the root of an MIQP model.
     * 
     */
    public static int       GUROBI_PARAMETER_METHOD                  = -1;

    public static Random    random                                   = new Random(1);

    public static ILPSolver ILP_SOLVER                               = ILPSolver.GUROBI;

    public static String    SCIP                                     = "";


    /**
     * Reads the parameters from the properties file and set the parameters in this class accordingly.
     * 
     * @throws ReadOrWriteToFileException
     */
    public static void readPropertyFile() throws ReadOrWriteToFileException
    {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(Parameters.PROPERTYFILE));
        } catch(IOException e) {
            throw new ReadOrWriteToFileException("Please define your properties data in a rockit.property file");
        }
        Parameters.SQL_URL = properties.getProperty("sql_url");
        Parameters.SQL_USERNAME = properties.getProperty("sql_username");
        Parameters.SQL_PASSWORD = properties.getProperty("sql_password");
        Parameters.SQL_DATABASE = properties.getProperty("sql_database");
        Parameters.TEMP_PATH = properties.getProperty("temp_path");
        File saveDir = new File(Parameters.TEMP_PATH);
        if(!saveDir.exists()) {
            saveDir.mkdirs();
        }
        if(properties.getProperty("use_cutting_plane_inference").equalsIgnoreCase("true")) {
            Parameters.USE_CUTTING_PLANE_INFERENCE = true;
        } else {
            Parameters.USE_CUTTING_PLANE_INFERENCE = false;
        }
        if(properties.getProperty("use_cutting_plane_aggregation").equalsIgnoreCase("true")) {
            Parameters.USE_CUTTING_PLANE_AGGREGATION = true;
        } else {
            Parameters.USE_CUTTING_PLANE_AGGREGATION = false;
        }
        Parameters.GAP = Double.parseDouble(properties.getProperty("gap"));
        Parameters.TIME_LIMIT = Long.parseLong(properties.getProperty("time_limit"));
        if(properties.getProperty("debug_output").equalsIgnoreCase("true")) {
            Parameters.DEBUG_OUTPUT = true;
        } else {
            Parameters.DEBUG_OUTPUT = false;
        }
        if(properties.getProperty("simplify_negative_weight_and_conjunction").equalsIgnoreCase("true")) {
            Parameters.SIMPLIFY_NEGATIVE_WEIGHT_AND_CONJUNCTION = true;
        } else {
            Parameters.SIMPLIFY_NEGATIVE_WEIGHT_AND_CONJUNCTION = false;
        }

        if(properties.getProperty("simplify_negative_weight_and_conjunction").equalsIgnoreCase("true")) {
            Parameters.SIMPLIFY_NEGATIVE_WEIGHT_AND_CONJUNCTION = true;
        } else {
            Parameters.SIMPLIFY_NEGATIVE_WEIGHT_AND_CONJUNCTION = false;
        }
        if(properties.getProperty("use_symmetries_in_marginal_inference").equalsIgnoreCase("true")) {
            Parameters.USE_SYMMETRIES_IN_MARGINAL_INFERENCE = true;
        } else {
            Parameters.USE_SYMMETRIES_IN_MARGINAL_INFERENCE = false;
        }
        if(properties.getProperty("convert_string_values").equalsIgnoreCase("true")) {
            Parameters.CONVERT_STRING_VALUES = true;
        } else {
            Parameters.CONVERT_STRING_VALUES = false;
        }
        try {
            Parameters.THREAD_NUMBER = Integer.parseInt(properties.getProperty("number_of_threads"));
            if(Parameters.THREAD_NUMBER <= 0) {
                int cores = Runtime.getRuntime().availableProcessors();
                Parameters.THREAD_NUMBER = cores;
            }
        } catch(NumberFormatException e) {
            throw new ReadOrWriteToFileException("Could not read number_of_threads from property file. Either the parameter is missing, or some non integer value is provided. ");
        }

        if(properties.containsKey("ilp_solver")) {
            try {
                Parameters.ILP_SOLVER = ILPSolver.valueOf(properties.getProperty("ilp_solver"));
            } catch(Exception e) {
                System.err.println("The selected ILP solver (" + properties.getProperty("ilp_solver") + ") does not exist");
                e.printStackTrace();
                System.exit(-1);
            }
        }
        
        if(properties.containsKey("scip")) {
            SCIP = properties.getProperty("scip").trim();
        }
    }
    // public static Random random = new Random(123456);

}
