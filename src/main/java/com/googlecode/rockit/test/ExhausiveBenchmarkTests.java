package com.googlecode.rockit.test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.antlr.runtime.RecognitionException;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.exception.ParseException;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.exception.SolveException;
import com.googlecode.rockit.file.MyFileWriter;


public class ExhausiveBenchmarkTests
{

    public static void main(String[] args) throws ReadOrWriteToFileException, ParseException, IOException, RecognitionException, SolveException, SQLException
    {

        /*
         * try {
         * Thread.sleep(10000);
         * } catch (InterruptedException e1) {
         * // TODO Auto-generated catch block
         * e1.printStackTrace();
         * }
         */

        Parameters.USE_CUTTING_PLANE_AGGREGATION = false;
        Parameters.USE_CUTTING_PLANE_INFERENCE = false;
        // Has to be disabled for runtime comparisons!!!
        Parameters.DEBUG_OUTPUT = false;

        Parameters.THREAD_NUMBER = Runtime.getRuntime().availableProcessors();

        boolean rockit = false;
        boolean tuffy = false;
        boolean alchemy = true;

        if(args != null && args.length > 0) {
            for(String arg : args) {
                if(arg.equalsIgnoreCase("rockit")) {
                    rockit = true;
                }
                if(arg.equalsIgnoreCase("tuffy")) {
                    tuffy = true;
                }
                if(arg.equalsIgnoreCase("alchemy")) {
                    alchemy = true;
                }
            }
        }

        boolean smoke = false;
        boolean pr = false;
        boolean lp = false;
        boolean rc = false;
        boolean ie = false;
        boolean er = true;

        System.out.println("Parameters.THREAD_NUMBER " + Parameters.THREAD_NUMBER);

        Parameters.TIME_LIMIT = 100;

        double[] gapArray = new double[] { 0.1, 0.05, 0.01, 0.001, 0.0005, 0.0001, 0.00005, 0.00001, 0.000005, 0.000001, 0.00000000001 };
        int[] flipArray = new int[] { 100000, 200000, 500000, 1000000, 2000000, 5000000, 10000000, 11000000, 15000000, 20000000, 25000000, 30000000 };

        ArrayList<String> results = new ArrayList<String>();
        MyFileWriter writer = new MyFileWriter("runtimeResults.txt");
        String s = null;

        for(int i = 0; i < gapArray.length; i++) {
            System.out.println("=======================================================");
            System.out.println("=========================" + i + "==============================");
            long runtime = 0;
            results.add("gap:" + gapArray[i]);
            writer.writeln("gap:" + gapArray[i]);;
            results.add("flip:" + flipArray[i]);
            writer.writeln("flip:" + flipArray[i]);
            Parameters.GAP = gapArray[i];
            int maxflips = flipArray[i];

            // smoke IO
            String modelFile = "data/smoke/prog.mln";
            String modelFileAlchemy = "data/smoke/prog-alchemy.mln";
            String evidenceFile = "data/smoke/evidence.db";
            String queryFile = "data/smoke/query.db";

            if(smoke) {
                if(rockit)
                    try {
                        s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                        writer.write("rockIt;" + gapArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                        runtime = StandardSolverTest.runtime + runtime;
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(tuffy)
                    try {
                        s = TuffyTest.test(modelFile, evidenceFile, queryFile, false, maxflips);
                        writer.write("tuffy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(alchemy)
                    try {
                        s = AlchemyTest.test(modelFile, modelFileAlchemy, evidenceFile, queryFile, false, maxflips);
                        writer.write("alchemy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
            }

            if(pr) {
                modelFile = "data/pr/prog.mln";
                modelFileAlchemy = "data/pr/prog-alchemy.mln";
                evidenceFile = "data/pr/evidence.db";
                queryFile = "data/pr/query.db";
                // protein
                if(rockit)
                    try {
                        s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                        writer.write("rockIt;" + gapArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                        runtime = StandardSolverTest.runtime + runtime;
                    } catch(Exception e) {

                        s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(rockit)
                    try {
                        s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                        writer.write("rockIt;" + gapArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                        runtime = StandardSolverTest.runtime + runtime;
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }

                // does not work (even for small flips)

                if(tuffy)
                    try {
                        s = TuffyTest.test(modelFile, evidenceFile, queryFile, false, maxflips);
                        writer.write("tuffy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                // !!!!out of memory (8 GB)!!!!
                if(maxflips <= 100000) {
                    if(alchemy)
                        try {
                            s = AlchemyTest.test(modelFile, modelFileAlchemy, evidenceFile, queryFile, false, maxflips);
                            writer.write("alchemy;" + flipArray[i] + ";");
                            results.add(s);
                            writer.writeln(s);
                            writer.flush();
                        } catch(Exception e) {
                            s = modelFile + ";" + evidenceFile + ";AlchemyException" + e.getMessage();// TODO Auto-generated catch block
                            results.add(s);
                            writer.writeln(s);
                            writer.flush();
                        } catch(Error e) {
                            s = modelFile + ";" + evidenceFile + ";AlchemyError;" + e.getMessage();// TODO Auto-generated catch block
                            results.add(s);
                            writer.writeln(s);
                            writer.flush();
                        }
                }
            }

            // lp IO
            if(lp) {
                modelFile = "data/lp/prog.mln";
                modelFileAlchemy = "data/lp/prog-alchemy.mln";
                evidenceFile = "data/lp/evidence.db";
                queryFile = "data/lp/query.db";

                // test will not work for gap <0.01
                if(Parameters.GAP >= 0.01) {
                    if(rockit)
                        try {
                            s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                            writer.write("rockIt;" + gapArray[i] + ";");
                            results.add(s);
                            writer.writeln(s);
                            writer.flush();
                            runtime = StandardSolverTest.runtime + runtime;
                        } catch(Exception e) {
                            s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                            results.add(s);
                            writer.writeln(s);
                            writer.flush();
                        }
                    if(rockit)
                        try {
                            s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                            writer.write("rockIt;" + gapArray[i] + ";");
                            results.add(s);
                            writer.writeln(s);
                            writer.flush();
                            runtime = StandardSolverTest.runtime + runtime;
                        } catch(Exception e) {
                            s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                            results.add(s);
                            writer.writeln(s);
                            writer.flush();
                        }
                }
                if(tuffy)
                    try {
                        s = TuffyTest.test(modelFile, evidenceFile, queryFile, false, maxflips);
                        writer.write("tuffy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(alchemy)
                    try {
                        s = AlchemyTest.test(modelFile, modelFileAlchemy, evidenceFile, queryFile, false, maxflips);
                        writer.write("alchemy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
            }

            // rc83000 IO
            if(rc) {
                modelFile = "data/rc/prog.mln";
                modelFileAlchemy = "data/rc/prog-alchemy.mln";
                evidenceFile = "data/rc/evidence.db";
                queryFile = "data/rc/query.db";

                if(rockit)
                    try {
                        s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                        writer.write("rockIt;" + gapArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                        runtime = StandardSolverTest.runtime + runtime;
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                        writer.write("rockit;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(rockit)
                    try {
                        s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                        writer.write("rockIt;" + gapArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                        runtime = StandardSolverTest.runtime + runtime;
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                        writer.write("rockit;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(tuffy)
                    try {
                        s = TuffyTest.test(modelFile, evidenceFile, queryFile, false, maxflips);
                        writer.write("tuffy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(alchemy)
                    try {
                        s = AlchemyTest.test(modelFile, modelFileAlchemy, evidenceFile, queryFile, false, maxflips);
                        writer.write("alchemy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
            }

            // ie IO
            if(ie) {
                modelFile = "data/ie/prog.mln";
                modelFileAlchemy = "data/ie/prog-alchemy.mln";
                evidenceFile = "data/ie/evidence.db";
                queryFile = "data/ie/query.db";

                if(rockit)
                    try {
                        s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                        writer.write("rockIt;" + gapArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                        runtime = StandardSolverTest.runtime + runtime;
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(rockit)
                    try {
                        s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                        writer.write("rockIt;" + gapArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                        runtime = StandardSolverTest.runtime + runtime;
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(tuffy)
                    try {
                        s = TuffyTest.test(modelFile, evidenceFile, queryFile, false, maxflips);
                        writer.write("tuffy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(alchemy)
                    try {
                        s = AlchemyTest.test(modelFile, modelFileAlchemy, evidenceFile, queryFile, false, maxflips);
                        writer.write("alchemy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
            }

            // er IO
            if(er) {
                modelFile = "data/er/prog.mln";
                modelFileAlchemy = "data/er/prog-alchemy.mln";
                evidenceFile = "data/er/evidence.db";
                String evidenceFileAlchemy = "data/er/evidence-alchemy.db";
                queryFile = "data/er/query.db";

                if(rockit)
                    try {
                        s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                        writer.write("rockIt;" + gapArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                        runtime = StandardSolverTest.runtime + runtime;
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(rockit)
                    try {
                        s = StandardSolverTest.test(modelFile, evidenceFile, Parameters.GAP, Parameters.USE_CUTTING_PLANE_INFERENCE, Parameters.USE_CUTTING_PLANE_AGGREGATION);
                        writer.write("rockIt;" + gapArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                        runtime = StandardSolverTest.runtime + runtime;
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";RockitException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(tuffy)
                    try {
                        s = TuffyTest.test(modelFile, evidenceFile, queryFile, false, maxflips);
                        writer.write("tuffy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";TuffyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
                if(alchemy)
                    try {
                        s = AlchemyTest.test(modelFile, modelFileAlchemy, evidenceFileAlchemy, queryFile, false, maxflips);
                        writer.write("alchemy;" + flipArray[i] + ";");
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Exception e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyException" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    } catch(Error e) {
                        s = modelFile + ";" + evidenceFile + ";AlchemyError;" + e.getMessage();// TODO Auto-generated catch block
                        results.add(s);
                        writer.writeln(s);
                        writer.flush();
                    }
            }

            results.add("duration:" + runtime);
        }

        writer.closeFile();
        // print results
        System.out.println("==========================");
        System.out.println();
        for(String r : results) {
            System.out.println(r);
        }
    }
}
