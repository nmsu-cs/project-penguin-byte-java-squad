package com.recipeapplication;

import java.sql.*;
public class database {

    private Connection connection;

    public Connection getConnection() throws SQLException{
        if (connection != null){
            return connection;
        }

        String url = "jdbc:mysql://dbclass.cs.nmsu.edu:3306/cs371sp24";
        String user = "cs371sp24";
        String password = "RunningBeaver120";

        try {
            this.connection = DriverManager.getConnection(url, user, password);
        }catch(Exception e){
            System.out.println("Database connection failed, com.recipeapplication.database is probably not correctly setup.");
            System.out.println(e);
        }
        return this.connection;
    }
}
