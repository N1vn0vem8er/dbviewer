package com.example.dbviewer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.Notifications;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertWindow extends AbstractSecondaryWindowController {
    @FXML
    public ListView tableList;
    @FXML
    public VBox fieldsContainer;
    public InsertWindow(){

    }
    @Override
    public void setupFields(ResultSet rs) throws SQLException {
        fieldsContainer.getChildren().clear();
        for(int i=0;i<rs.getMetaData().getColumnCount();i++){
            fieldsContainer.getChildren().add(new Label(rs.getMetaData().getColumnName(i + 1)));
            fieldsContainer.getChildren().add(new TextField());
        }
        Button button = new Button();
        button.setText("Insert");
        button.setOnAction(actionEvent -> insert());
        fieldsContainer.getChildren().add(button);
    }
    public void insert(){
        StringBuilder fieldNamesBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();
        for(int i=0;i<fieldsContainer.getChildren().size()-1;i+=2){
            Label label = (Label) fieldsContainer.getChildren().get(i);
            TextField textField = (TextField) fieldsContainer.getChildren().get(i + 1);
            if(textField.getText().isEmpty()){
                Notifications.create().text("Empty field "+ label.getText()).showError();
                return;
            }
            fieldNamesBuilder.append(label.getText());
            if(i!=fieldsContainer.getChildren().size()-3)
                fieldNamesBuilder.append(", ");
            valuesBuilder.append(textField.getText());
            if(i + 1!=fieldsContainer.getChildren().size()-2)
                valuesBuilder.append(", ");
        }
        String query = "INSERT INTO "+tableList.getSelectionModel().getSelectedItem().toString()+"("+fieldNamesBuilder.toString()+") VALUES ("+valuesBuilder.toString()+")";
        try {
            int rowsAffected = connectionManager.executeUpdate(query);
            Notifications.create().text("Rows affected: " + rowsAffected).showInformation();
        } catch (SQLException e) {
            Notifications.create().text(e.getMessage()).showError();
        }
    }
}
