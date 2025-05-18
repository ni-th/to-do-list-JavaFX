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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class DashboardController {
    public TableView tbl;
    public TableColumn colTime;
    public TableColumn colTitle;
    public Button btnAdd;
    public TextField txtTitle;
    public ListView listView;
    public DatePicker txtDate;
    public TextArea txtDescription;
    public TableColumn colCompletedData;
    ArrayList<ToDoList> toDoListArrayList = new ArrayList<>();
    ArrayList<ToDoList> completedToDoListArrayList = new ArrayList<>();
    ArrayList<CheckBox> checkBoxArrayList = new ArrayList<>();


    public void btnOnActionAdd(ActionEvent actionEvent) {
        if (toDoListArrayList.size()==0){
            refresh();
        }
        ToDoList toDoList = new ToDoList(txtTitle.getText(), txtDescription.getText(),txtDate.getValue()+"",Boolean.FALSE);
        if (toDoListArrayList.indexOf(toDoList)>=0){
            new Alert(Alert.AlertType.INFORMATION,"Task alredy Added!").show();
            return;
        }
        toDoListArrayList.add(toDoList);
        try {
            PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement("Insert into tasks(title,description,task_date) values (?,?,?)");
            preparedStatement.setString(1,txtTitle.getText());
            preparedStatement.setString(2,txtDescription.getText());
            preparedStatement.setString(3,txtDate.getValue()+"");
            if (preparedStatement.executeUpdate()>0){
                new Alert(Alert.AlertType.INFORMATION,"Task Added!").show();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        refresh();

        CheckBox checkBox = new CheckBox();
        checkBoxArrayList.add(checkBox);
        checkBox.setOnAction(e -> {
            if (checkBox.isSelected()){
                checkBox.setDisable(true);
                //completedToDoListArrayList.add(toDoList);
                try {
                    PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement("UPDATE tasks SET `iscompleted` = `TRUE` WHERE title = ?");
                    preparedStatement.setString(1,txtTitle.getText());
                    if (preparedStatement.executeUpdate()>0){
                        new Alert(Alert.AlertType.INFORMATION,"Task Added!").show();
                    }
                } catch (SQLException e2) {
                    throw new RuntimeException(e2);
                }
                colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
                ObservableList<ToDoList> toDoListObservableList = FXCollections.observableArrayList();
                completedToDoListArrayList.forEach(toDoList1 -> {
                    toDoListObservableList.add(toDoList1);
                });
                tbl.setItems(toDoListObservableList);

            }
        });


    }

    public void btnOnActionRefresh(ActionEvent actionEvent) {
        updateTable();
        try {
            toDoListArrayList.clear();
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM tasks WHERE iscompleted=0");
            while(resultSet.next()){
                toDoListArrayList.add(new ToDoList(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getBoolean(4)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        refresh();

    }
    private void refresh(){
        listView.getItems().clear();
        for (int i = 0; i < toDoListArrayList.size(); i++) {
            ToDoList toDoList = toDoListArrayList.get(i);
            CheckBox checkBox = new CheckBox();
            checkBox.setDisable(toDoListArrayList.get(i).getIsCompleted());
            HBox hbox = new HBox(10,new Label(toDoList.getTitle()),new Label(toDoList.getDescription()),new Label(toDoList.getDate()),checkBox);
            listView.getItems().add(hbox);

            checkBox.setOnAction(e -> {
                if (checkBox.isSelected()){
                    checkBox.setDisable(true);
                    //completedToDoListArrayList.add(toDoList);
                    try {
                        PreparedStatement preparedStatement = DBConnection.getInstance().getConnection().prepareStatement("UPDATE tasks SET iscompleted = TRUE WHERE title = ?");
                        preparedStatement.setString(1,toDoList.getTitle());
                        if (preparedStatement.executeUpdate()>0){
                            new Alert(Alert.AlertType.INFORMATION,"Task completed!").show();
                            updateTable();
                        }
                    } catch (SQLException e2) {
                        throw new RuntimeException(e2);
                    }

                }
            });

        }


    }

    public void updateTable(){
        completedToDoListArrayList.clear();
        try {
            ResultSet resultSet = DBConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM tasks WHERE iscompleted = 1");
            while(resultSet.next()){
                completedToDoListArrayList.add(new ToDoList(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getBoolean(4)
                ));
            }
            colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
            colCompletedData.setCellValueFactory(new PropertyValueFactory<>("date"));
            ObservableList<ToDoList> toDoListObservableList = FXCollections.observableArrayList();
            completedToDoListArrayList.forEach(toDoList1 -> {
                toDoListObservableList.add(toDoList1);
            });
            tbl.setItems(toDoListObservableList);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
