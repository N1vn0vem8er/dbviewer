package com.example.dbviewer;


import java.io.*;
import java.util.Scanner;

public record DBCredentials() {
    public static String userName;
    public static String serverName;
    public static String password;
    public static String dbName;
    public static String port;
    private static String userHome = System.getProperty("user.home");
    public static void save(){
        try {
            File o = new File(userHome+"/.dbviewer.credentials");
            o.createNewFile();
            System.out.println("Saving to file at " + userHome);
            FileWriter out = new FileWriter(userHome+"/.dbviewer.credentials");
            out.write(userName + "\n" + serverName + "\n" + password + "\n" + dbName + "\n" + port + "\n");
            out.close();
            System.out.println("Saved");
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
    public static boolean load(){
        try {
            File in = new File(userHome+"/.dbviewer.credentials");
            if(in.exists()){
                System.out.println("Loading from file at " + userHome);
                Scanner scanner = new Scanner(in);
                userName = scanner.nextLine();
                serverName = scanner.nextLine();
                password = scanner.nextLine();
                dbName = scanner.nextLine();
                port = scanner.nextLine();
                System.out.println("Loaded");
                return true;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }
}
