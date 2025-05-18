package controller;

import db.DBConnection;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import model.ToDoList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class DashboardController {
    public TableView tbl;
    public TableColumn colTime;
    public TableColumn colDescription;
    public Button btnAdd;
    public TextField txtTitle;
    public ListView listView;
    public DatePicker txtDate;
    public TextArea txtDescription;
    ArrayList<ToDoList> toDoListArrayList = new ArrayList<>();
    ArrayList<ToDoList> completedToDoListArrayList = new ArrayList<>();
    ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();


    public void btnOnActionAdd(ActionEvent actionEvent) {

        ToDoList toDoList = new ToDoList(txtTitle.getText(), txtDescription.getText(),txtDate.getValue()+"");
        if (toDoListArrayList.indexOf(toDoList)>=0){
            new Alert(Alert.AlertType.INFORMATION,"Task alredy Added!").show();
            return;
        }
        toDoListArrayList.add(toDoList);
        //System.out.print(toDoListArrayList);
        CheckBox checkBox = new CheckBox();
        checkBoxArrayList.add(checkBox);
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()){
                checkBox.setDisable(true);
                completedToDoListArrayList.add(toDoList);
                colDescription.setCellValueFactory(new PropertyValueFactory<>("title"));
                ObservableList<ToDoList> toDoListObservableList = FXCollections.observableArrayList();
                completedToDoListArrayList.forEach(toDoList1 -> {
                    toDoListObservableList.add(toDoList1);
                });
                tbl.setItems(toDoListObservableList);

            }
        });


        HBox hbox = new HBox(10,new Label(txtTitle.getText()),new Label(txtDescription.getText()),new Label(txtDate.getValue()+""),checkBox);
        listView.getItems().add(hbox);



        try {
            PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement("Insert into tasks values (?,?,?)");
            preparedStatement.setString(1,txtTitle.getText());
            preparedStatement.setString(2,txtDescription.getText());
            preparedStatement.setString(3,txtDate.getValue()+"");
            if (preparedStatement.executeUpdate()>0){
                new Alert(Alert.AlertType.INFORMATION,"Customer Added!").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
