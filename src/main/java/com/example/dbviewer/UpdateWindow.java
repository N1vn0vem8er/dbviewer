package com.example.dbviewer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import org.controlsfx.control.Notifications;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class UpdateWindow extends AbstractSecondaryWindowController {
    private ArrayList<Pair<String, TextField>> replaceFieldsMap;
    public UpdateWindow(){
        replaceFieldsMap = new ArrayList<>();
    }
    @Override
    public void setupFields(ResultSet rs) throws SQLException {
        fieldContainer.getChildren().clear();
        for(int i=0;i<rs.getMetaData().getColumnCount();i++) {
            CheckBox checkBox = new CheckBox();
            checkBox.setMnemonicParsing(false);
            checkBox.setText(rs.getMetaData().getColumnName(i + 1));
            checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1) {
                    updateFields(t1, checkBox.getText());
                }
            });
            fieldContainer.getChildren().add(checkBox);
            fieldContainer.getChildren().add(new TextField());
        }
        Button button = new Button("Update");
        button.setOnAction(x -> update());
        fieldContainer.getChildren().add(button);
        replaceFieldsMap.clear();
    }
    public void updateFields(boolean selected, String fieldName){
        if(selected) {
            TextField textField = new TextField();
            fieldContainer.getChildren().add(new Label(fieldName));
            fieldContainer.getChildren().add(textField);
            replaceFieldsMap.add(new Pair<>(fieldName, textField));
        }
        else {
            for(int i=0;i<fieldContainer.getChildren().size();i++){
                Node node = fieldContainer.getChildren().get(i);
                if(node.getClass().equals(Label.class)){
                    Label label = (Label) node;
                    if(Objects.equals(label.getText(), fieldName)){
                        fieldContainer.getChildren().remove(i);
                        fieldContainer.getChildren().remove(i);
                        break;
                    }
                }
            }
        }
    }
    public void update(){
        StringBuilder valBuilder = new StringBuilder();
        StringBuilder conBuilder = new StringBuilder();
        boolean hit = false;
        for(int i=0;i<fieldContainer.getChildren().size() - 1;i++){
            Node node = fieldContainer.getChildren().get(i);
            if(node.getClass().equals(CheckBox.class)) {
                CheckBox checkBox = (CheckBox) node;
                TextField textField = (TextField) fieldContainer.getChildren().get(i + 1);
                if (!textField.getText().isEmpty()) {
                    if (hit) valBuilder.append(",");
                    valBuilder.append(" ");
                    valBuilder.append(checkBox.getText());
                    valBuilder.append(" = ");
                    valBuilder.append(textField.getText());
                    hit = true;
                }
                i++;
            }
        }
        hit = false;
        for(var i : replaceFieldsMap){
            if(hit) conBuilder.append(" and ");
            conBuilder.append(i.getKey());
            conBuilder.append(" ");
            conBuilder.append(i.getValue().getText());
            hit = true;
        }
        String query = "UPDATE " + tableList.getSelectionModel().getSelectedItem() + " SET " + valBuilder + " WHERE " + conBuilder;
        System.out.println(query);
        try {
            int rowsAffected = connectionManager.executeUpdate(query);
            Notifications.create().text("Rows affected: " + rowsAffected).showInformation();
        } catch (SQLException e) {
            Notifications.create().text(e.getMessage()).showError();
        }
    }
}
