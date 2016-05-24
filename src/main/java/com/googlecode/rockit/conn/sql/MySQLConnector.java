package com.googlecode.rockit.conn.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.googlecode.rockit.app.Parameters;
import com.googlecode.rockit.exception.DatabaseException;
import com.googlecode.rockit.exception.Messages;
import com.googlecode.rockit.exception.ReadOrWriteToFileException;
import com.googlecode.rockit.file.MyFileWriter;


/**
 * This method provides access to the database schema.
 *
 * @author JanNoessner
 * 
 */
public class MySQLConnector
{

    private Connection conn = null;


    public MySQLConnector() throws DatabaseException
    {
        this.connect();
    }


    /**
     * Returns the actual singelton object or creates a new one.
     * 
     * @return
     */
    /*
     * public static MySQLConnector getMySQLConnector()
     * {
     * if (ref == null)
     * // it's ok, we can call this constructor
     * ref = new MySQLConnector();
     * return ref;
     * }
     * public Object clone()
     * throws CloneNotSupportedException
     * {
     * throw new CloneNotSupportedException();
     * }
     */

    public boolean connect() throws DatabaseException
    {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
            throw new DatabaseException("Class SQLConnector: ClassNotFoundException: Problems in initiating the SQL Connection.");
        }

        // load properties

        String url = Parameters.SQL_URL;
        String username = Parameters.SQL_USERNAME;
        String password = Parameters.SQL_PASSWORD;

        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch(SQLException e) {
            e.printStackTrace();
            throw new DatabaseException("Class SQLConnector: SQLException: Problems connecting with database. Check username, url, and password.");
        }

        // give in memory tables more space
        this.executeQuery("SET max_heap_table_size = 1844674407370954752;");

        if(Parameters.DEBUG_OUTPUT)
            System.out.println("Database connection established");
        return true;
    }


    public void useDatabase() throws DatabaseException
    {
        String database = Parameters.SQL_DATABASE;
        this.executeQuery("USE `" + database + "`");
    }


    public void deleteAll() throws DatabaseException
    {
        String database = Parameters.SQL_DATABASE;
        // delete all tables
        try {
            this.executeQuery("CREATE Database `" + database + "`");
        } catch(DatabaseException e) {
            this.executeQuery("DROP Database IF EXISTS `" + database + "` ");
            this.executeQuery("CREATE Database `" + database + "`");
        }
        this.executeQuery("USE `" + database + "`");

    }


    /**
     * Executes an SQL query, which has no output. Examples are all sorts of
     * create, delete, and update Queries.
     * 
     * @param SQLQuery
     * @throws DatabaseException
     */

    public void executeQuery(String SQLQuery) throws DatabaseException
    {
        // long time = System.currentTimeMillis();
        Statement stmt;
        try {
            stmt = conn.createStatement();

            // System.out.println("without seeelect " +SQLQuery);

            stmt.executeUpdate(SQLQuery);
            stmt.close();
        } catch(SQLException e) {
            throw new DatabaseException(Messages.printDatabaseExceptionError(SQLQuery, e.getMessage()));
        }
        // this.executionTime = System.currentTimeMillis()-time;
    }


    /**
     * Returns a PreparedStatement for this connection.
     * 
     * @throws DatabaseException
     */
    /*
     * public PreparedStatement returnPreparedStatement(String query)
     * throws DatabaseException {
     * long time = System.currentTimeMillis();
     * PreparedStatement state = null;
     * state = conn.prepareStatement(query);
     * this.executionTime = System.currentTimeMillis()-time;
     * return state;
     * }
     */

    /**
     * Fills the prepared query insert into `similarity`.`individual` " +
     * "(name, position, isBigABox) values( ? , ?, ? )"
     * 
     * with the right values and executes the query afterwards.
     * 
     * @param preStatement
     * @param ind
     * @param bigABox
     * @throws DatabaseException
     */
    /*
     * private void insertIndividual(PreparedStatement preStatement,
     * Individual ind, boolean bigABox) throws DatabaseException {
     * preStatement.setString(1, ind.getName());
     * preStatement.setInt(2, ind.getPosition());
     * preStatement.setBoolean(3, bigABox);
     * preStatement.addBatch();
     * }
     */

    /**
     * Executes a SQL Query which has several rows and columns as output.
     * 
     * The output is given as a Result set.
     * 
     * @param sqlQuery
     * @return ResultSet
     * @throws DatabaseException
     * @throws DatabaseException
     */
    public ResultSet executeSelectQuery(String sqlQuery) throws DatabaseException
    {
        // long time = System.currentTimeMillis();
        ResultSet rs = null;
        Statement s;
        try {
            s = this.conn.createStatement();
            s.executeQuery(sqlQuery);
            rs = s.getResultSet();
        } catch(SQLException e) {
            throw new DatabaseException(Messages.printDatabaseExceptionError(sqlQuery, e.getMessage()));
        }
        // this.executionTime = System.currentTimeMillis()-time;

        return rs;
    }


    /**
     * Executes a query, which has a single String output.
     * 
     * If no data is returned, then the function returns "null".
     * 
     * @param sqlQuery
     * @return
     * @throws DatabaseException
     */
    public String executeStringQuery(String sqlQuery) throws DatabaseException
    {
        // long time = System.currentTimeMillis();

        ResultSet rs = executeSelectQuery(sqlQuery);

        // If some Data has been found, return the first entry as String.

        // this.executionTime = System.currentTimeMillis()-time;
        try {
            if(rs.next()) {
                String result = rs.getString(1);
                rs.getStatement().close();
                rs.close();
                return result;
            } else {
                return null;
            }
        } catch(SQLException e) {
            throw new DatabaseException("An SQL error occured at /n Query: " + sqlQuery + "/n position: " + e.getMessage());
        }
    }


    /**
     * Execute a query which has a single long output
     * 
     * @param sqlQuery
     * @return long output of the query.
     * @throws DatabaseException
     */
    public long executeLongQuery(String sqlQuery) throws DatabaseException
    {
        // long time = System.currentTimeMillis();
        ResultSet rs = executeSelectQuery(sqlQuery);
        long result;
        try {
            rs.next();
            result = rs.getLong(1);
            rs.getStatement().close();
            rs.close();
            // this.executionTime = System.currentTimeMillis()-time;
        } catch(SQLException e) {
            throw new DatabaseException("An SQL error occured at /n Query: " + sqlQuery + "/n position: " + e.getMessage());
        }
        return result;

    }


    /*
     * public void reconnect() throws DatabaseException{
     * this.close();
     * this.connect();
     * }
     */
    /**
     * Disconnects the database Connection and frees all allocated resources.
     * 
     * @return true, if disconnection was successful (ended without errors) and
     *         false otherwise.
     * @throws DatabaseException
     */
    public boolean close() throws DatabaseException
    {
        if(conn != null) {

            try {
                this.conn.close();
                System.out.println("Database connection terminated");
                return true;
            } catch(Exception e) {
                e.printStackTrace();
                throw new DatabaseException("Could not close the connection.");
            }
        }
        return false;
    }


    public void dropDatabase()
    {
        String database = Parameters.SQL_DATABASE;

        /*
         * for (String tableName: tableNames) {
         * StringBuilder delete = new StringBuilder();
         * delete.append("DROP table ").append(tableName);
         * //System.out.println(delete.toString());
         * this.executeQuery(delete.toString());
         * }
         * tableNames = new HashSet<String>();
         */
        try {
            this.executeQuery("DROP Database `" + database + "`");
            this.executeQuery("RESET QUERY CACHE");
        } catch(DatabaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void dropTable(String tableName) throws DatabaseException
    {
        // drop old table (eventually)
        StringBuilder deleteTable = new StringBuilder();
        deleteTable.append("DROP TABLE IF EXISTS `");
        deleteTable.append(tableName).append("`; ");
        this.executeQuery(deleteTable.toString());
    }


    /**
     * Creates an in-memory table.
     * 
     * @param tableName
     *            Name of the table
     * @param doubleName
     *            [optional] name of the double column. Will be inserted before the other columns
     * @param colNames
     *            name of the colums
     * @throws DatabaseException
     */
    public void createInMemoryTable(String tableName, String doubleColName, boolean temporaryTable, ArrayList<String> colNames, Integer colSize) throws DatabaseException
    {
        // this.tableNames.add(tableName);
        StringBuilder createTable = new StringBuilder();
        StringBuilder primaryKey = new StringBuilder();
        primaryKey.append(" PRIMARY KEY (");
        createTable.append("CREATE ");
        // if(tableName.contains("varString")){
        // temporaryTable=true;
        // }

        if(temporaryTable && !Parameters.DEBUG_OUTPUT)
            createTable.append("TEMPORARY ");
        createTable.append("TABLE  `").append(tableName).append("` ( ");
        if(doubleColName != null) {
            createTable.append(doubleColName).append(" double, ");
        }
        for(int i = 0; i < colNames.size(); i++) {
            createTable.append(" `").append(colNames.get(i));
            createTable.append("` char(").append(colSize).append(") NOT NULL, INDEX USING HASH (`");
            createTable.append(colNames.get(i)).append("`) , ");
            primaryKey.append("`").append(colNames.get(i)).append("`");
            if(i < colNames.size() - 1) {
                primaryKey.append(" , ");
            }
        }
        primaryKey.append(")   ");
        createTable.append(primaryKey);
        createTable.append(") ENGINE = MEMORY DEFAULT CHARSET=latin1;");
        try {
            this.executeQuery(createTable.toString());
        } catch(DatabaseException e) {
            System.out.println(createTable);
            System.out.println(e.getMessage());
            StringBuilder drop = new StringBuilder();
            drop.append("DROP TABLE ").append(tableName);
            this.executeQuery(drop.toString());
            this.executeQuery(createTable.toString());
        }
    }


    public void addData(String tableName, ArrayList<String[]> values, String filename) throws DatabaseException
    {
        this.addData(tableName, null, values, filename);
    }


    /**
     * Adds data efficiently with prepared statement.
     * 
     * TODO: possible futher optimizations:
     * - it is not neccessary any more to really store the ground values. In principle,
     * one could directly read the GroundValues file, efficiently create db files and read them in afterwards.
     * 
     * @param tableName
     *            Name of the table where the data should be added.
     * @param doubleValues
     *            [optional] double values to add, if any.
     * @param values
     *            normal string values to add.
     * @throws DatabaseException
     * @throws ReadOrWriteToFileException
     */
    public void addData(String tableName, ArrayList<Double> doubleValues, ArrayList<String[]> values, String filename) throws DatabaseException
    {
        if(values.size() > 0) {
            if(values.size() > 1000) {
                // faster
                MyFileWriter writer = null;
                try {
                    writer = new MyFileWriter(filename);
                } catch(ReadOrWriteToFileException e) {
                    e.printStackTrace();
                    throw new DatabaseException("Insert failed because temporary file " + filename + " could not been created.");
                }

                for(int i = 0; i < values.size(); i++) {
                    StringBuilder sb = new StringBuilder();
                    if(doubleValues != null) {
                        sb.append(doubleValues.get(i)).append("\t");
                    }
                    String[] groundings = values.get(i);
                    for(String grounding : groundings) {
                        sb.append(grounding).append("\t");
                    }
                    writer.writeln(sb.toString());
                }
                writer.closeFile();
                StringBuilder insertQuery = new StringBuilder();
                insertQuery.append("LOAD DATA LOCAL INFILE '").append(filename).append("' REPLACE INTO TABLE ").append(tableName);
                this.executeQuery(insertQuery.toString());
                writer.deleteFile();

            } else {
                StringBuilder addQuery = new StringBuilder();
                addQuery.append("INSERT IGNORE INTO `");
                addQuery.append(tableName);
                addQuery.append("`  VALUES");
                for(int i = 0; i < values.size(); i++) {
                    addQuery.append("(");
                    if(doubleValues != null) {
                        addQuery.append(doubleValues.get(i));
                        addQuery.append(",");
                    }
                    String[] value = values.get(i);
                    for(int j = 0; j < value.length; j++) {
                        addQuery.append("'");
                        addQuery.append(value[j]);
                        addQuery.append("'");
                        if(j < value.length - 1) {
                            addQuery.append(",");
                        }
                    }
                    addQuery.append(")");
                    if(i < values.size() - 1) {
                        addQuery.append(",");
                    }
                }
                this.executeQuery(addQuery.toString());
            }
            /*
             * // Faster for few insert values:
             * StringBuilder addQuery = new StringBuilder();
             * addQuery.append("INSERT IGNORE INTO `");
             * addQuery.append(tableName);
             * addQuery.append("`  VALUES(");
             * if (doubleValues != null) {
             * addQuery.append("?,");
             * }
             * for (int i = 0; i < values.get(0).length; i++) {
             * addQuery.append("?");
             * if (i < values.get(0).length - 1) {
             * addQuery.append(",");
             * }
             * }
             * addQuery.append(");");
             * // Lock table for faster writing
             * StringBuilder lockTable = new StringBuilder();
             * lockTable.append("LOCK TABLES ").append(tableName)
             * .append(" WRITE;");
             * this.executeQuery(lockTable.toString());
             * java.sql.PreparedStatement statement = conn.prepareStatement(addQuery.toString());
             * int posValues = 0;
             * if (doubleValues != null
             * && doubleValues.size() != values.size()) {
             * throw new DatabaseException(
             * "The ground value list and the list of double values do not have the same size in Predicate "
             * + tableName
             * + ". There is something wrong with your input.");
             * }
             * for (String[] groundValue : values) {
             * int posQuery = 1;
             * // if it is a Double value, the first row is affected
             * if (doubleValues != null) {
             * statement.setDouble(posQuery,
             * doubleValues.get(posValues));
             * posQuery++;
             * }
             * // for any normal groundvalue do
             * for (int i = 0; i < groundValue.length; i++) {
             * String value = groundValue[i];
             * statement.setString(posQuery, value);
             * posQuery++;
             * }
             * statement.execute();
             * posValues++;
             * }
             * this.executeQuery("UNLOCK TABLES;");
             * statement.close();
             * }
             */
        }
    }

    /*
     * public long getExecutionTime() {
     * return executionTime;
     * }
     */

}
