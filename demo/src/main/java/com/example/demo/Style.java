package com.example.demo;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class Style {
    public void HboxStyle(HBox hBox) {
        hBox.setSpacing(30);

    }

    public void VboxStyle(VBox vBox) {
        vBox.setSpacing(10);
        Insets padding = new Insets(20);
        vBox.setPadding(padding);
        vBox.setStyle(" -fx-font-size: 1.2em");
    }

    public void LabelStyle(Label label) {
        Insets padding = new Insets(5);
        label.setPadding(padding);
    }

    public void TableViewStyle(TableView tableView, TableColumn tableColumn,int n) {
        Insets padding = new Insets(20);
        tableView.setPadding(padding);
        tableView.setStyle("-fx-background-color: #e0dede; -fx-font-size: 1.2em");
    }


    public void ButtonStyle(Button button)
    {
        button.setMaxWidth(100);
        button.setStyle(" -fx-font-size: 1.3em; -fx-background-color: #e0dede;");
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-font-size: 1.3em; -fx-background-color: #57ad57;");
        });
        button.setOnMouseExited(event -> {
            button.setStyle("-fx-font-size: 1.3em; -fx-background-color: #e0dede;");
        });

    }

    public void GeneralScene(Pane pane)
    {
        Insets padding = new Insets(20);
        pane.setPadding(padding);
    }



}
