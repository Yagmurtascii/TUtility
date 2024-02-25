package com.example.demo;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.HibernateException;
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

    public void saveButton(List<String> list, TableView tableView, Button button, ComboBox<String> combo1, ComboBox<String> combo2, TextField textField) {
        button.setOnAction(event -> {
            ObservableList<ProcedureDefinition> items = tableView.getItems();
            System.out.println(items.size());
            for (int i = 0; i < items.size(); i++) {
                ProcedureDefinition procuderCall = items.get(i);
                procuderCall.setParamType(list.get(i));
                procuderCall.setDatabaseName(combo1.getValue());
                procuderCall.setProcedureName(textField.getText());
                procuderCall.setSchemaName(combo2.getValue());
                try (Session session = sessionFactory.openSession()) {
                    Transaction transaction = session.beginTransaction();
                    session.save(procuderCall);
                    transaction.commit();
                } catch (HibernateException ex) {
                    System.err.println("Veritabanına kaydetme işlemi sırasında hata oluştu: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
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

    public void comboBoxAction(TableView tableView,ComboBox<String> comboBox1, ComboBox<String> comboBox2, String query) {
        comboBox1.setOnAction(event ->
        {
            String selectedDatabaseName = comboBox1.getValue();
            schemaList = fillCombobox(comboBox2, query, selectedDatabaseName);
            comboFilter(comboBox2, schemaList);
            tableView.setEditable(true);
            comboBox2.setEditable(true);
        });

    }

    public void clearAll(Stage stage, Button button) {
        button.setOnAction(event -> {
            Platform.runLater(() -> {
                new HelloApplication().start(new Stage());
                stage.close();
            });
        });
    }
}