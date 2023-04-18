package com.rosenhristov.model;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.*;

public class Employee extends Model {

    @NotNull
    private int employeeID;

    private Map<Integer, Pair<LocalDate, LocalDate>> projects;

    public Employee(int employeeID) {
        this.employeeID = employeeID;
    }

    public Employee(@NotNull int employeeID, Map<Integer, Pair<LocalDate, LocalDate>> projects) {
        this.employeeID = employeeID;
        this.projects = projects;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public void setProjects(Map<Integer, Pair<LocalDate, LocalDate>> projects) {
        this.projects = projects;
    }

    public Pair<LocalDate, LocalDate> addProject(int projectID, LocalDate dateFrom, LocalDate dateTo) {
        return getProjects().put(projectID, Pair.of(dateFrom, dateTo));
    }

    public Map<Integer, Pair<LocalDate, LocalDate>> getProjects() {
        if (MapUtils.isEmpty(projects)) {
            projects = new HashMap<>();
        }
        return projects;
    }
}
