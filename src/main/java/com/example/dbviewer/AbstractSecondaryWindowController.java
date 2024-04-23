package com.example.dbviewer;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Notifications;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class AbstractSecondaryWindowController {
    @FXML
    public ListView tableList;
    @FXML
    public VBox fieldContainer;
    public DBConnectionManager connectionManager;
    public void setupTableNames(ArrayList<String> tableNames){
        tableList.setItems(FXCollections.observableList(tableNames));
    }
    public void setConnectionManager(DBConnectionManager connectionManager){
        this.connectionManager = connectionManager;
    }
    public abstract void setupFields(ResultSet rs) throws SQLException;
    public void tableSelected(){
        try {
            setupFields(connectionManager.getResults("SELECT * FROM " + tableList.getSelectionModel().getSelectedItem().toString()));
        } catch (SQLException e) {
            Notifications.create().text(e.getMessage()).showError();
        }
    }
}
