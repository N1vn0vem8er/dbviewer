package com.example.dbviewer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow extends Application {
    private MainWindowController controller;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("MainWindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        scene.getStylesheets().add(String.valueOf(MainWindow.class.getResource("dark-mode.css")));
        primaryStage.setTitle("DBViewer");
        primaryStage.setScene(scene);
        primaryStage.show();
        controller = fxmlLoader.getController();
        if(!DBCredentials.load()) {
            FXMLLoader loginFxmlLoader = new FXMLLoader(LoginWindow.class.getResource("LoginWindow.fxml"));
            Scene loginScene = new Scene(loginFxmlLoader.load(), 300, 200);
            loginScene.getStylesheets().add(String.valueOf(MainWindow.class.getResource("dark-mode.css")));
            Stage loginWindow = new Stage();
            loginWindow.setTitle("Login");
            loginWindow.setScene(loginScene);
            loginWindow.show();
            LoginWindow loginController = loginFxmlLoader.getController();
            loginController.setMainWindowController(controller);
        }else{
            controller.connectToDataBase();
            controller.loadTableNames();
        }
    }
    @Override
    public void stop(){
        controller.closeConnection();
    }
}
