package com.example.dbviewer;

import java.sql.*;
import java.util.ArrayList;

public class DBConnectionManager {
    private String dbms;
    private String serverName;
    private String userName;
    private String password;
    private String portNumber;
    private String dbName;
    private Connection conn;
    public DBConnectionManager(String serverName, String userName, String password, String portNumber, String dbName) {
        this.serverName = serverName;
        this.userName = userName;
        this.password = password;
        this.portNumber = portNumber;
        this.dbName = dbName;
    }

    public DBConnectionManager(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
    public void connect() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.OracleDriver");
        conn = DriverManager.getConnection("jdbc:oracle:thin:@"+this.serverName+":"+this.portNumber+":"+this.dbName, this.userName, this.password);
        System.out.println("Connected to database");
    }
    public Connection getConnection(){
        return conn;
    }
    public void closeConnection() throws SQLException {
        System.out.println("Closing connection");
        conn.close();
    }
    public ArrayList<String> getTablesNames() throws SQLException {
        System.out.println("Getting table names");
        ResultSet rs = conn.getMetaData().getTables(null, this.userName.toUpperCase(), "%", new String[]{"TABLE"});
        ArrayList<String> list = new ArrayList<>();
        while (rs.next()){
            System.out.println(rs.getString(3));
            list.add(rs.getString(3));
        }
        return list;
    }
    public ResultSet getResults(String query) throws SQLException {
        return conn.createStatement().executeQuery(query);
    }
    public int executeUpdate(String query) throws SQLException {
        return conn.createStatement().executeUpdate(query);
    }
    public void commit() throws SQLException {
        conn.commit();
    }
    public void rollback() throws SQLException {
        conn.rollback();
    }
}
