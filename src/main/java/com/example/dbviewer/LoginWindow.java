package com.example.dbviewer;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginWindow {
    public TextField userNameField;
    public PasswordField passwordField;
    public TextField dbNameField;
    public TextField portFiled;
    public TextField hostNameField;
    private MainWindowController mainWindowController;
    public void setMainWindowController(MainWindowController mainWindowController){
        this.mainWindowController = mainWindowController;
    }
    public void login(){
        DBCredentials.userName = userNameField.getText();
        DBCredentials.password = passwordField.getText();
        DBCredentials.dbName = dbNameField.getText();
        DBCredentials.serverName = hostNameField.getText();
        DBCredentials.port = portFiled.getText();
        DBCredentials.save();
        mainWindowController.connectToDataBase();
        mainWindowController.loadTableNames();
    }
}
