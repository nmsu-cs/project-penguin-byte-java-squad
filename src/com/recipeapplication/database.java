package com.recipeapplication;

import java.sql.*;
public class database {

    private Connection connection;

    public Connection getConnection() throws SQLException{
        if (connection != null){
            return connection;
        }

        String url = "jdbc:mysql://localhost:3306/recipes";
        String user = "primary";
        String password = "1230";

        try {
            this.connection = DriverManager.getConnection(url, user, password);
        }catch(Exception e){
            System.out.println("Database connection failed, com.recipeapplication.database is probably not correctly setup.");
            System.out.println(e);
        }
        return this.connection;
    }
}
