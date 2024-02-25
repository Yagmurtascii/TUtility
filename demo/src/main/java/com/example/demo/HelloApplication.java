package com.example.demo;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;


public class HelloApplication extends Application {
    ComponentsFunction functions = new ComponentsFunction();

    @Override
    public void start(Stage stage) {

        String schemaQuery = "SELECT DISTINCT schemaName FROM ProcuderCall WHERE databaseName = :parameter";
        String databaseNameQuery = "SELECT DISTINCT databaseName FROM ProcuderCall";

        Style style = new Style();

        StackPane root = new StackPane();

        VBox generalUIComponent = new VBox();
        VBox labelVBox = new VBox();
        VBox comboAndFieldVBox = new VBox();
        VBox buttonVBox = new VBox();

        HBox generalInputHBox = new HBox();
        HBox buttonHBox = new HBox();

        Label databaseName = new Label("DATABASE NAME: ");
        Label schemaName = new Label("SCHEMA NAME: ");
        Label procudereName = new Label("PROCURE NAME: ");

        TextField procuderNameText = new TextField();

        ComboBox<String> databaseCombobox = new ComboBox<>();
        ComboBox<String> schemaCombobox = new ComboBox<>();

        Button save = new Button("S A V E");
        Button clear = new Button("C L E A R");

        //region TableView
        TableView tableView = new TableView<ProcuderCall>();
        tableView.setEditable(true);

        TableColumn<ProcuderCall, String> paramNameColumn = new TableColumn<>("Param Name");
        paramNameColumn.setCellValueFactory(new PropertyValueFactory<>("paramName"));
        paramNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        paramNameColumn.setOnEditCommit(event -> {
            ProcuderCall procuderCall = event.getRowValue();
            procuderCall.setParamName(event.getNewValue());
        });

        TableColumn<ProcuderCall, String> paramTypeColumn = new TableColumn<>("Param Type");
        paramTypeColumn.setCellValueFactory(new PropertyValueFactory<>("paramType"));
        List<String> values = functions.paramTypeColumnComboBox(paramTypeColumn);


        TableColumn<ProcuderCall, Integer> orderNumberColumn = new TableColumn<>("Order Number");
        orderNumberColumn.setCellValueFactory(new PropertyValueFactory<>("orderNumber"));
        orderNumberColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        orderNumberColumn.setOnEditCommit(event ->
        {
            ProcuderCall procuderCall = event.getRowValue();
            procuderCall.setOrderNumber(event.getNewValue());
        });

        TableColumn<ProcuderCall, Integer> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        statusColumn.setOnEditCommit(event ->

        {
            ProcuderCall procuderCall = event.getRowValue();
            procuderCall.setStatus(event.getNewValue());
        });

        TableColumn<ProcuderCall, Integer> isOutCursorColumn = new TableColumn<>("Is Out Cursor");
        isOutCursorColumn.setCellValueFactory(new PropertyValueFactory<>("isoutcursor"));
        isOutCursorColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        isOutCursorColumn.setOnEditCommit(event ->

        {
            ProcuderCall procuderCall = event.getRowValue();
            procuderCall.setIsoutcursor(event.getNewValue());
        });

        tableView.getColumns().addAll(paramNameColumn, paramTypeColumn, orderNumberColumn, statusColumn, isOutCursorColumn);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getItems().add(new ProcuderCall("", "", 0, 0, 0));

        tableView.setRowFactory(tv ->
        {
            TableRow<ProcuderCall> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && row.isEmpty()) {
                    tableView.getItems().add(new ProcuderCall("", "", 0, 0, 0));
                }
            });
            return row;
        });
        //endregion

        //region Componenet function
        databaseCombobox.setEditable(true);
        schemaCombobox.setEditable(true);
        ObservableList<String> databaseList = functions.fillCombobox(databaseCombobox, databaseNameQuery, null);
        functions.comboBoxAction(databaseCombobox, schemaCombobox, schemaQuery);
        functions.comboFilter(databaseCombobox, databaseList);
        functions.saveButton(values, tableView, save, databaseCombobox, schemaCombobox, procuderNameText);
        functions.clearAll(stage, clear);
        //endregion

        //region Style
        style.LabelStyle(databaseName);
        style.LabelStyle(schemaName);
        style.LabelStyle(procudereName);
        style.VboxStyle(labelVBox);
        style.VboxStyle(comboAndFieldVBox);
        style.TableViewStyle(tableView, orderNumberColumn, 2);
        style.VboxStyle(buttonVBox);
        style.ButtonStyle(clear);
        style.ButtonStyle(save);
        style.GeneralScene(root);
        //endregion

        //region End Nodes
        labelVBox.getChildren().addAll(databaseName, schemaName, procudereName);
        comboAndFieldVBox.getChildren().addAll(databaseCombobox, schemaCombobox, procuderNameText);
        generalInputHBox.getChildren().addAll(labelVBox, comboAndFieldVBox);
        buttonHBox.getChildren().addAll(clear, save);
        buttonVBox.getChildren().addAll(save, clear);
        generalUIComponent.getChildren().addAll(generalInputHBox, tableView, buttonVBox);
        root.getChildren().addAll(generalUIComponent);
        //endregion

        Scene scene = new Scene(root, 700, 700);
        stage.setTitle("TUtility");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResource("/Images/TUtility.png").toExternalForm()));
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }

}