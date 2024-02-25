package com.example.demo;

import javax.persistence.*;

@Entity
@Table(name = "procuder_table")
public class ProcedureDefinition {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String databaseName;
    private String schemaName;
    private String procedureName;
    private String paramName;
    private String paramType;
    private int orderNumber;
    private int status;
    private int isOutCursor;


    public ProcedureDefinition(String s, String s1, int i, int i1, int i2) {
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getIsoutcursor() {
        return isOutCursor;
    }

    public void setIsoutcursor(int isoutcursor) {
        this.isOutCursor = isoutcursor;
    }

    public ProcedureDefinition() {
    }

    public ProcedureDefinition(String databaseName, String schemaName, String procedureName, String paramName, String paramType, int orderNumber, int status, int isoutcursor) {
        this.databaseName = databaseName;
        this.schemaName = schemaName;
        this.procedureName = procedureName;
        this.paramName = paramName;
        this.paramType = paramType;
        this.orderNumber = orderNumber;
        this.status = status;
        this.isOutCursor = isoutcursor;
    }
}
