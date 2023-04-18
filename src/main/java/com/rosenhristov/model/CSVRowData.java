package com.rosenhristov.model;

import jakarta.validation.constraints.NotNull;
import java.util.Date;

public class CSVRowData extends Model {

    @NotNull(message="Employee ID cannot be null")
    private int employeeID;

    @NotNull(message="Project ID cannot be null")
    private int projectID;

    @NotNull(message = "Project start date cannot be null")
    private Date dateFrom;

    private Date dateTo;

    public CSVRowData(int employeeID, int projectID, Date dateFrom, Date dateTo) {
        this.employeeID = employeeID;
        this.projectID = projectID;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }
}
