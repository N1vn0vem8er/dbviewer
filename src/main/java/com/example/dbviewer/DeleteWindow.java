package com.example.dbviewer;

import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import org.controlsfx.control.Notifications;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DeleteWindow extends AbstractSecondaryWindowController{

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
        button.setText("Delete");
        button.setOnAction(actionEvent -> delete());
        fieldContainer.getChildren().add(button);
    }
    private void delete(){
        StringBuilder queryBuilder = new StringBuilder();
        boolean hit = false;
        for(int i=0;i<fieldContainer.getChildren().size()-1;i+=2){
            CheckBox checkBox = (CheckBox) fieldContainer.getChildren().get(i);
            TextField textField = (TextField) fieldContainer.getChildren().get(i+1);
            if(checkBox.isSelected()){
                if(hit) queryBuilder.append(" and");
                queryBuilder.append(" ");
                queryBuilder.append(checkBox.getText());
                queryBuilder.append(" ");
                queryBuilder.append(textField.getText());
                hit = true;

            }
        }
        String query = "DELETE FROM " + tableList.getSelectionModel().getSelectedItem() + " WHERE" + queryBuilder;
        System.out.println(query);
        try {
            int rowsAffected = connectionManager.executeUpdate(query);
            Notifications.create().text("Rows affected: " + rowsAffected).showInformation();
        } catch (SQLException e) {
            Notifications.create().text(e.getMessage()).showError();
        }
    }
}
