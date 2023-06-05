package com.ngoclam.querytool.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SQLite_Driver {
    private static final String location = Objects.requireNonNull(SQLite_Driver.class.getResource("/database/configureDB.db")).toExternalForm();
    //private static final String location = System.getProperty("user.home") + "/configureDB.db";
    private static boolean checkDrivers() {
        try {
            Class.forName("org.sqlite.JDBC");
            DriverManager.registerDriver(new org.sqlite.JDBC());
            return true;
        } catch (ClassNotFoundException | SQLException classNotFoundException) {
            Logger.getAnonymousLogger().log(Level.SEVERE, LocalDateTime.now() + ": Could not start SQLite Drivers");
            return false;
        }
    }

    public static Connection connect() {
        String dbPrefix = "jdbc:sqlite:";
        Connection connection = null;
        if(checkDrivers()){
            try {connection = DriverManager.getConnection(dbPrefix + location);
                System.out.println(location);
                System.out.println(SQLite_Driver.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            }
            catch (SQLException exception) {
                Logger.getAnonymousLogger().log(Level.SEVERE,
                        LocalDateTime.now() + ": Could not connect to SQLite DB at " +
                                location);
                return null;
            }
        }
        return connection;
    }
}