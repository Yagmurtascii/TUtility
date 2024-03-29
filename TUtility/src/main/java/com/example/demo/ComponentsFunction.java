package com.example.demo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;

public class ComponentsFunction {
    ObservableList<String> schemaList;
    List<String> keepComboBoxValue = new ArrayList<>();
    String paramTypeQuery = "SELECT DISTINCT paramType FROM ProcedureDefinition";

    ConfigurationSettings configurationSettings = new ConfigurationSettings();
    SessionFactory sessionFactory = configurationSettings.createConfig();

    public void alertWindow(String title, String header, String content)
    {

        Style style=new Style();
        //region Alert
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Label info=new Label(title);
        style.LabelStyle(info);
        alert.setTitle(info.getText());
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.getDialogPane().setStyle("-fx-background-color: #eaeaea;-fx-font-size: 1.5em");
        alert.getDialogPane().setMinHeight(400);

        alert.getDialogPane().setMaxWidth(400);
        ImageView icon = new ImageView("icon.png");
        icon.setFitHeight(48);
        icon.setFitWidth(48);
        alert.getDialogPane().setGraphic(icon);
        alert.showAndWait();
        //endregion
    }

    public ObservableList<String> fillCombobox(ComboBox<String> comboBox, String query, String parameter) {
        ObservableList<String> databaseList = FXCollections.observableArrayList();
        databaseList.addAll(getValue(query, parameter));
        comboBox.getItems().clear();
        comboBox.setItems(databaseList);
        return databaseList;
    }

    public ObservableList<String> getValue(String query, String parameter) {
        ObservableList<String> databaseList = FXCollections.observableArrayList();
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            List<String> databases;
            if (parameter == null) {
                databases = session.createQuery(query, String.class).list();
            } else {
                databases = session.createQuery(query, String.class).setParameter("parameter", parameter).list();
            }
            databaseList.addAll(databases);
            transaction.commit();
            return databaseList;
        }
    }

    public void comboFilter(ComboBox<String> comboBox, ObservableList<String> list) {
        comboBox.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            comboBox.setItems(list);
            if (newValue != null && !newValue.isEmpty()) {
                ObservableList<String> filteredItems = FXCollections.observableArrayList();
                for (String item : list) {
                    if (item != null && item.toLowerCase().contains(newValue.toLowerCase())) {
                        filteredItems.add(item);
                    }
                }
                comboBox.show();
                comboBox.setItems(filteredItems);

            } else {
                comboBox.setItems(list);
            }
        });
    }



    public void saveButton(List<String> list, TableView tableView, Button button, ComboBox<String> combo1, ComboBox<String> combo2, TextField textField) {
        button.setOnAction(event -> {
            ObservableList<ProcedureDefinition> items = tableView.getItems();
            try {
                if (items.size() > 0) {
                    for (int i = 0; i < items.size(); i++) {
                        ProcedureDefinition procedureDefinition = items.get(i);
                        procedureDefinition.setParamType(list.get(i));
                        procedureDefinition.setDatabaseName(combo1.getValue());
                        procedureDefinition.setProcedureName(textField.getText());
                        procedureDefinition.setSchemaName(combo2.getValue());
                        try (Session session = sessionFactory.openSession()) {
                            if(checkSave(procedureDefinition)==true)
                            {
                                Transaction transaction = session.beginTransaction();
                                session.save(procedureDefinition);
                                transaction.commit();
                                alertWindow("TUtility","Procedure Definition" ,"Procedure Definition is Created");
                            }
                            else
                                alertWindow("TUtility","Procedure Definition" ,"Procedure Definition is Failed");
                        } catch (IndexOutOfBoundsException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e) {

                alertWindow("TUtility","Procedure Definition" ,"Procedure Definition is Failed");
            }
        });
    }


    public boolean checkSave(ProcedureDefinition procedureDefinition) {

        if (procedureDefinition.getDatabaseName() != "" && procedureDefinition.getSchemaName() != null
                && procedureDefinition.getProcedureName() != "" && procedureDefinition.getParamType() != ""
                && procedureDefinition.getParamName() != null) {

            return true;
        } else {

            return false;
        }
    }

    public List<String> paramTypeColumnComboBox(TableColumn<ProcedureDefinition, String> tableColumn) {
        List<String> paramTypeList = getValue(paramTypeQuery, null);
        ObservableList<String> paramTypes = FXCollections.observableArrayList(paramTypeList);
        FilteredList<String> filteredParamTypes = new FilteredList<>(paramTypes);
        tableColumn.setCellFactory(column -> {
            return new TableCell<ProcedureDefinition, String>() {
                private final ComboBox<String> comboBox = new ComboBox<>(filteredParamTypes);
                {
                    comboBox.setEditable(true);
                    comboBox.setOnAction(event -> {
                        commitEdit(comboBox.getValue());
                        keepComboBoxValue.add(comboBox.getValue());
                    });
                    comboFilter(comboBox, filteredParamTypes);
                }

                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        comboBox.setValue(item);
                        setGraphic(comboBox);
                    }
                }
            };
        });
        return keepComboBoxValue;
    }

    public void comboBoxAction(TextField textField,TableView tableView, ComboBox<String> comboBox1, ComboBox<String> comboBox2, String query) {
        comboBox1.setOnAction(event ->
        {
            String selectedDatabaseName = comboBox1.getValue();
            if(!selectedDatabaseName.equals("")) {
                schemaList = fillCombobox(comboBox2, query, selectedDatabaseName);
                comboFilter(comboBox2, schemaList);
                comboBox2.setEditable(true);
                comboBox2.setOnAction(e->
                {
                    String selectedDatabaseName1 = comboBox2.getValue();
                    if(!selectedDatabaseName1.equals("") || selectedDatabaseName1==null)
                    {
                        textField.setEditable(true);
                        textField.textProperty().addListener((observable, oldValue, newValue) -> {
                            String selectedDatabaseName2 = newValue;
                            if(!selectedDatabaseName2.equals(""))
                                tableView.setEditable(true);
                        });
                    }
                });
            }
            else
            {
                comboBox2.setEditable(false);
                tableView.setEditable(false);
                textField.setEditable(false);
            }
        });

    }

    public void clearAll(Stage stage, Button button) {
        button.setOnAction(event -> {
            Platform.runLater(() -> {
                stage.close();
                new Application().start(new Stage());
                stage.close();
            });
        });
    }
}