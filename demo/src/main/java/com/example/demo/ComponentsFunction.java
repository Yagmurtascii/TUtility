package com.example.demo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
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

    public ObservableList<String> fillCombobox(ComboBox<String> comboBox, String query, String parameter) {
        System.out.println(getValue(query, parameter));
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

    String messages = "";

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
                            System.out.println(checkSave(procedureDefinition));
                            if(checkSave(procedureDefinition)==true)
                            {
                                Transaction transaction = session.beginTransaction();
                                session.save(procedureDefinition);
                                transaction.commit();
                            }
                        } catch (IndexOutOfBoundsException ex) {
                            System.err.println("Veritabanına kaydetme işlemi sırasında hata oluştu: " + ex.getMessage());
                            ex.printStackTrace();
                        }
                    }
                } else
                    System.out.println("Lütfen kayıt girin");
            } catch (IndexOutOfBoundsException e) {
                System.out.println(e);
            }
        });
    }

    public boolean checkSave(ProcedureDefinition procedureDefinition) {

        if (procedureDefinition.getDatabaseName() != "" && procedureDefinition.getSchemaName() != null
                && procedureDefinition.getProcedureName() != "" && procedureDefinition.getParamType() != ""
                && procedureDefinition.getParamName() != null) {
            messages = "Created is Successful";
            return true;
        } else {
            messages = "Created is Failed";
            return false;
        }
    }

    public String returnMessage()
    {
        return messages;
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
            System.out.println("Selected "+selectedDatabaseName);
            if(!selectedDatabaseName.equals("")) {
                schemaList = fillCombobox(comboBox2, query, selectedDatabaseName);
                comboFilter(comboBox2, schemaList);
                comboBox2.setEditable(true);
                comboBox2.setOnAction(e->
                {
                    String selectedDatabaseName1 = comboBox2.getValue();
                    System.out.println("Selected "+selectedDatabaseName1);
                    if(!selectedDatabaseName1.equals("") || selectedDatabaseName1==null)
                    {
                        textField.setEditable(true);
                        textField.textProperty().addListener((observable, oldValue, newValue) -> {
                            String selectedDatabaseName2 = newValue;
                            System.out.println("Selected "+selectedDatabaseName2);
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
                new Application().start(new Stage());
                stage.close();
            });
        });
    }
}