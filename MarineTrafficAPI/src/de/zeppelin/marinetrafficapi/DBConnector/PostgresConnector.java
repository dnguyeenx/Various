 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zeppelin.marinetrafficapi.DBConnector;

/**
 *
 * @author dnguyeenx
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgresConnector
{

    private static final Logger LOG = Logger.getLogger(PostgresConnector.class.getName());
    private static final String URLPrefix = "jdbc:postgresql://";
    private String DBHostIP = null;
    private String DBName = null;
    private String DBUser = null;
    private String DBPassword = null;

    Connection conn = null;

    // Constructor
    public PostgresConnector(String DBHostIP, String DBName, String DBUser, String DBPassword)
    {
        
        this.DBHostIP = DBHostIP;
        this.DBName = DBName;
        this.DBUser = DBUser;
        this.DBPassword = DBPassword;

    }
    

    public void ConnetionOpen()
    {
        try {

            Properties parameters = new Properties();
            parameters.put("user", DBUser);
            parameters.put("password", DBPassword);

            conn = DriverManager.getConnection(URLPrefix + DBHostIP + "/" + DBName, parameters);

            if (conn != null) {
                LOG.info("Connected to database " + DBName + " on host " + DBHostIP);
            }else{
                LOG.warning("Can not connect to the database. ");
            }

        } catch (SQLException ex) {
            LOG.warning(ex.getMessage());
            System.exit(0);

        }

        //return conn;
    }

    public void ConnectionClose()
    {

        try {

            if (conn != null && !conn.isClosed()) {
                conn.close();
                LOG.info("Connection is closed");
            }
        } catch (SQLException e) {
            LOG.warning(e.getMessage());

        }
    }

    public PreparedStatement SetParameter(String PreSQLQuery)
    {
        
        PreparedStatement PreStat = null;

        try {
            //LOG.log(Level.INFO, "Before: {0}", PreSQLQuery);
            PreStat = conn.prepareStatement(PreSQLQuery);
        } catch (SQLException e) {
            LOG.warning(e.getMessage());
        }
        return PreStat;
    }

    public ResultSet ExecuteQuery(PreparedStatement pstst)
    {
        LOG.log(Level.INFO, "SQL-Query: {0}", pstst.toString());
        ResultSet ResultSet = null;

        try {
            if (conn != null && !conn.isClosed()) {
                ResultSet = pstst.executeQuery();

            }
        } catch (SQLException ex) {

            LOG.log(Level.WARNING, "{0} in Query {1}", new Object[]{ex.getMessage(), pstst.toString()});
            LOG.log(Level.WARNING, "Error code: {0}", ex.getSQLState());

        }
        return ResultSet;

    }

}
