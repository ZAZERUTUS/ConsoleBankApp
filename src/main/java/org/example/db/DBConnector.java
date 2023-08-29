package org.example.db;

import lombok.SneakyThrows;
import lombok.extern.flogger.Flogger;
import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;


import java.sql.*;
import java.util.logging.Logger;

import static org.example.config.ConfigHandler.getInstanceConfig;

@Log4j2
public class DBConnector {

    protected static Connection connection = getDBConnector();


    protected DBConnector() {
    }

    protected static void closeDbConnection() {
        if (connection != null) {
            try {
                connection.close();
                log.info("DB connection closed");
            } catch (SQLException e) {
                log.error("Error in close connection DB");
                log.error("Error in close connection DB");
            }
            connection = null;
        }
    }
    @SneakyThrows
    public static Connection getDBConnector() {
        closeDbConnection();
        connection = java.sql.DriverManager.getConnection("jdbc:postgresql://" +
                getInstanceConfig().getConfig().getHostDb() + ":" +
                        getInstanceConfig().getConfig().getPortDb() + "/" +
                        getInstanceConfig().getConfig().getNameDb(),
                getInstanceConfig().getConfig().getAdminUser(),
                getInstanceConfig().getConfig().getAdminPassword());

        return connection;
    }

    protected static Statement getStatementPostgres() {
        try{
            Connection connectionLocal = getDBConnector();
            log.info("Connection succesfull!");
            return connectionLocal.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_UPDATABLE);
        } catch(Exception ex){
            log.error("Connection failed...");
            System.out.println(ex);
        }
        return null;
    }

    protected static ResultSet getResultSetBySQLQuery(Statement statement, String query) {
        if (statement != null) {
            try {
                return statement.executeQuery(query);
            } catch (SQLException e) {
                log.error(e.toString());
                return null;
            }
        }
        log.error("Statement is null");
        return null;
    }
}
