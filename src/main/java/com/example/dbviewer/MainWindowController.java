package com.example.dbviewer;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainWindowController {
    @FXML
    private ListView<String> tableList;
    @FXML
    private TabPane tabView;
    @FXML
    private MenuBar menuBar;
    private DBConnectionManager connectionManager;
    public MainWindowController() {
    }
    public void connectToDataBase(){
        connectionManager = new DBConnectionManager(DBCredentials.serverName, DBCredentials.userName, DBCredentials.password, DBCredentials.port, DBCredentials.dbName);
        try{
            connectionManager.connect();
            Notifications.create().title("Connected").text("Connected to database").showInformation();
        }catch (SQLException | ClassNotFoundException e){
            System.out.println("Couldn't connect to database");
            Notifications.create().title("Error").text("Couldn't connect to database\n" + e.getMessage()).showError();
            System.out.println(e.getMessage());
        }
    }
    public void loadTableNames(){
        try {
            tableList.setItems(FXCollections.observableList(connectionManager.getTablesNames()));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public void closeConnection(){
        try {
            connectionManager.closeConnection();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void tableSelected(MouseEvent event){
        displayTable(tableList.getSelectionModel().getSelectedItem(), "SELECT * FROM " + tableList.getSelectionModel().getSelectedItem());
    }
    public void addTab(String name, Node node){
        tabView.getTabs().add(new Tab(name, node));
    }
    public void displayTable(String name, String query){
        TableView tableView = new TableView();
        try {
            ResultSet rs = connectionManager.getResults(query);
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            List<String> columnNames = new ArrayList<>();
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {

                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
                    if (param.getValue().get(j) != null) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    } else {
                        return null;
                    }
                });
                tableView.getColumns().addAll(col);
                columnNames.add(col.getText());
            }
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);

            }
            tableView.setItems(data);
        } catch (SQLException e) {
            Notifications.create().title("Error").text(e.getMessage()).showError();
        }
        addTab(name, tableView);
    }
    private TableView getTableView(String name){
        TableView tableView = new TableView();
        try {
            ResultSet rs = connectionManager.getResults("SELECT * FROM "+name);
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            List<String> columnNames = new ArrayList<>();
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {

                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> {
                    if (param.getValue().get(j) != null) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    } else {
                        return null;
                    }
                });
                tableView.getColumns().addAll(col);
                columnNames.add(col.getText());
            }
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                data.add(row);

            }
            tableView.setItems(data);
        } catch (SQLException e) {
            Notifications.create().title("Error").text(e.getMessage()).showError();
        }
        return tableView;
    }
    public void addTableTab(String name, TableView tableView){
        addTab(name, tableView);
    }
    public void displayEditor(){
        addTab("New File", new TextArea());
    }
    public void executeQueryFromEditor(){
        if(tabView.getSelectionModel().getSelectedItem().getContent().getClass().equals(TextArea.class)){
            TextArea textArea = (TextArea) tabView.getSelectionModel().getSelectedItem().getContent();
            System.out.println(textArea.getText());
            displayTable("Result", textArea.getText());
        }
    }
    public void commit(){
        try {
            connectionManager.commit();
        } catch (SQLException e) {
            Notifications.create().title("Error").text(e.getMessage()).showError();
        }
    }
    public void rollback(){
        try{
            connectionManager.rollback();
        }catch (SQLException e){
            Notifications.create().text("Error").text(e.getMessage()).showError();
        }
    }
    public void refreshTables(){
        loadTableNames();
        Notifications.create().title("Refreshed").text("Refreshed table names").showInformation();
    }
    public void refreshTable(){
        if(tabView.getSelectionModel().getSelectedItem().getContent().getClass().equals(TableView.class)){
            tabView.getSelectionModel().getSelectedItem().setContent(getTableView(tabView.getSelectionModel().getSelectedItem().getText()));
            Notifications.create().title("Refreshed").text("Refreshed table").showInformation();
        }
    }
    public void refreshConnection(){
        try {
            connectionManager.closeConnection();
        } catch (SQLException e) {
            Notifications.create().text(e.getMessage()).showError();
        }
        connectToDataBase();
        refreshTables();
    }
    public void openSearch() throws IOException {
        FXMLLoader loader = new FXMLLoader(SearchWindow.class.getResource("SearchWindow.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        scene.getStylesheets().add(String.valueOf(SearchWindow.class.getResource("dark-mode.css")));
        Stage stage = new Stage();
        stage.setTitle("Search");
        stage.setScene(scene);
        stage.show();
        SearchWindow searchWindow = loader.getController();
        try {
            searchWindow.setupTableNames(connectionManager.getTablesNames());
            searchWindow.setParentWindow(this);
        }catch (Exception e){
            Notifications.create().text(e.getMessage()).showError();
        }
    }
    public void openInsert() throws IOException {
        FXMLLoader loader = new FXMLLoader(InsertWindow.class.getResource("InsertWindow.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        scene.getStylesheets().add(String.valueOf(SearchWindow.class.getResource("dark-mode.css")));
        Stage stage = new Stage();
        stage.setTitle("Insert");
        stage.setScene(scene);
        stage.show();
        InsertWindow insertWindow = loader.getController();
        try{
            insertWindow.setupTableNames(connectionManager.getTablesNames());
            insertWindow.setConnectionManager(connectionManager);
        }catch (Exception e){
            Notifications.create().text(e.getMessage()).showError();
        }
    }
    public void openDelete() throws IOException {
        FXMLLoader loader = new FXMLLoader(DeleteWindow.class.getResource("DeleteWindow.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        scene.getStylesheets().add(String.valueOf(SearchWindow.class.getResource("dark-mode.css")));
        Stage stage = new Stage();
        stage.setTitle("Delete");
        stage.setScene(scene);
        stage.show();
        DeleteWindow deleteWindow = loader.getController();
        deleteWindow.setConnectionManager(connectionManager);
        try {
            deleteWindow.setupTableNames(connectionManager.getTablesNames());
        } catch (SQLException e) {
            Notifications.create().text(e.getMessage()).showError();
        }
    }
    public void openUpdate() throws  IOException {
        FXMLLoader loader = new FXMLLoader(UpdateWindow.class.getResource("UpdateWindow.fxml"));
        Scene scene = new Scene(loader.load(), 800, 600);
        scene.getStylesheets().add(String.valueOf(SearchWindow.class.getResource("dark-mode.css")));
        Stage stage = new Stage();
        stage.setTitle("Update");
        stage.setScene(scene);
        stage.show();
        UpdateWindow updateWindow = loader.getController();
        updateWindow.setConnectionManager(connectionManager);
        try {
            updateWindow.setupTableNames(connectionManager.getTablesNames());
        } catch (SQLException e) {
            Notifications.create().text(e.getMessage()).showError();
        }
    }
    public ResultSet getQueryResult(String query) throws SQLException {
        return connectionManager.getResults(query);
    }
}
