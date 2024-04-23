package com.example.dbviewer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Notifications;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SearchWindow extends AbstractSecondaryWindowController{
    @FXML
    public VBox fieldContainer;
    @FXML
    public ListView tableList;
    private MainWindowController mainWindowController;
    public SearchWindow(){
    }
    @Override
    public void setupFields(ResultSet rs) throws SQLException {
        fieldContainer.getChildren().clear();
        for(int i=0;i<rs.getMetaData().getColumnCount();i++) {
            CheckBox checkBox = new CheckBox();
            checkBox.setMnemonicParsing(false);
            checkBox.setText(rs.getMetaData().getColumnName(i + 1));
            fieldContainer.getChildren().add(checkBox);
            fieldContainer.getChildren().add(new TextField());
        }
        Button button = new Button();
        button.setText("Search");
        button.setOnAction(actionEvent -> search());
        fieldContainer.getChildren().add(button);
    }
    public void setParentWindow(MainWindowController mainWindowController){
        this.mainWindowController = mainWindowController;
    }
    public void search(){
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM " + tableList.getSelectionModel().getSelectedItem().toString() +" WHERE");
        boolean hit = false;
        for(int i=0;i<fieldContainer.getChildren().size() - 1;i+=2){
            CheckBox checkBox = (CheckBox) fieldContainer.getChildren().get(i);
            if(checkBox.isSelected()){
                if(hit) queryBuilder.append(" and");
                TextField textField = (TextField) fieldContainer.getChildren().get(i+1);
                queryBuilder.append(" ");
                queryBuilder.append(checkBox.getText());
                queryBuilder.append(" ");
                queryBuilder.append(textField.getText());
                hit = true;
            }
        }
        String query = queryBuilder.toString();
        mainWindowController.displayTable(tableList.getSelectionModel().getSelectedItem().toString(), query);
    }
    public void tableSelected(){
        try {
            setupFields(mainWindowController.getQueryResult("SELECT * FROM " + tableList.getSelectionModel().getSelectedItem().toString()));
        } catch (SQLException e) {
            Notifications.create().text(e.getMessage()).showError();
        }
    }
}
