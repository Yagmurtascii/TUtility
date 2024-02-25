module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.hibernate.orm.core;
    requires java.persistence;
    requires java.logging;
    requires java.naming;
    requires java.sql;
    opens com.example.demo to org.hibernate.orm.core;
    exports com.example.demo;
}