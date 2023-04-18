package com.rosenhristov.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.rosenhristov.mapper.Mapper;
import com.rosenhristov.model.CSVRowData;
import com.rosenhristov.model.Employee;
import com.rosenhristov.model.ProjectCollaboration;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.*;

public class DataProcessor {

    private List<CSVRowData> rowData;
    private Map<Integer, Employee> employeesMap;
    private List<ProjectCollaboration> projectCollaborations;
    private Map<Pair<Integer,Integer>, Integer> totalCollaborationPerPair;
    private Map<Pair<Integer, Integer>, Integer> longestCollaborationsMap;
    private Mapper mapper;

    private DataProcessor(List<CSVRowData> rowData) {
        this.rowData = rowData;
        this.mapper = Mapper.create();
    }

    public static DataProcessor of(List<CSVRowData> csvRowData) {
        return new DataProcessor(csvRowData);
    }

    /**
     * Extracts all the pairs of employees that have worked together in one or more common projects
     *
     * @return  List of ProjectCollaboration instances that contain information about the coworkers' IDs,
     *          the project number and the number of days they have worked together.
     */
    public List<ProjectCollaboration> extractProjectCollaborations() {
        if (CollectionUtils.isEmpty(rowData)) {
            throw new RuntimeException("No data provided as csv data rows.");
        }
        if (isNull(projectCollaborations)) {
            projectCollaborations = new LinkedList<>();
        }
        Map<Integer, Employee> employees = groupProjectsByEmployee();
        Set<Integer> checkedEmployees = new HashSet<>();
        for(Employee employee : employees.values()) {
            for(Employee colleague: employees.values()) {
                if(employee.getEmployeeID() != colleague.getEmployeeID()
                        && !checkedEmployees.contains(colleague.getEmployeeID())) {
                    List<ProjectCollaboration> pairCollaborations = extractCollaborationsForEmployees(employee, colleague);
                    projectCollaborations.addAll(pairCollaborations);
                }
            }
            checkedEmployees.add(employee.getEmployeeID()); // to avoid double checking
        }
        return projectCollaborations;
    }


    /**
     * Iterates along rowData read by CSVFileReader to define the employees and the projects they have worked in
     *
     * @return map with the employees' IDs as keys and Employee instances as aa value
     */
    public Map<Integer, Employee> groupProjectsByEmployee() {
        if(MapUtils.isEmpty(employeesMap)) {
            employeesMap = new HashMap<>();
        }
        for (CSVRowData row : rowData) {
            if (employeesMap.containsKey(row.getEmployeeID())) {
                employeesMap.get(row.getEmployeeID())
                        .addProject(row.getProjectID(),
                                mapper.toLocalDate(row.getDateFrom()),
                                mapper.toLocalDate(row.getDateTo()));
            } else {
                Employee employee = new Employee(row.getEmployeeID());
                employee.addProject(
                        row.getProjectID(),
                        mapper.toLocalDate(row.getDateFrom()),
                        mapper.toLocalDate(row.getDateTo())
                );
                employeesMap.put(row.getEmployeeID(), employee);
            }
        }
        return employeesMap;
    }


    /**
     * Extracts the projects that certain pair of coworkers have cooperated in
     *
     * @return  List of ProjectCollaboration instances that contain information about the coworkers' IDs,
     *          the project number and the number of days they have worked together.
     */
    public List<ProjectCollaboration> extractCollaborationsForEmployees(Employee employee, Employee colleague) {
        List<ProjectCollaboration> employeesCollaboration = new LinkedList<>();

        Set<Integer> commonProjects = employee.getProjects().keySet().stream()
                .filter(projectID -> colleague.getProjects().keySet().contains(projectID))
                .collect(toSet());

        commonProjects.forEach(projectID -> {
            LocalDate employeeStartDate = employee.getProjects().get(projectID).getKey();
            LocalDate employeeEndDate = employee.getProjects().get(projectID).getValue();

            LocalDate colleagueStartDate = colleague.getProjects().get(projectID).getKey();
            LocalDate colleagueEndDate = colleague.getProjects().get(projectID).getValue();

            if (employeesWorkedTogether(employeeStartDate, employeeEndDate, colleagueStartDate, colleagueEndDate)) {
                LocalDate collaborationStartDate = getCollaborationStartDate(employeeStartDate, colleagueStartDate);
                LocalDate collaborationEndDate = getCollaborationEndDate(employeeEndDate, colleagueEndDate);

                int projectCollaborationDays = Period.between(collaborationStartDate, collaborationEndDate).getDays();
                employeesCollaboration.add(
                        new ProjectCollaboration(
                                employee.getEmployeeID(),
                                colleague.getEmployeeID(),
                                projectID,
                                projectCollaborationDays)
                );
            }
        });

        return employeesCollaboration;
    }

    /**
     * This method defines if two employees worked together by checking if the start dates
     * of each employee is earlier than the end date of the other.
     *
     * @param employeeStartDate the date when the first employee started working in the project
     * @param employeeEndDate the date when the first employee ended working in the project
     * @param colleagueStartDate the date when the second employee started working in the project
     * @param colleagueEndDate the date when the second employee ended working in the project
     *
     * @return boolean value answering if the two employees worked in the project together,
     *         i.e. during certain period of time
     */
    private boolean employeesWorkedTogether(LocalDate employeeStartDate, LocalDate employeeEndDate,
                                            LocalDate colleagueStartDate, LocalDate colleagueEndDate) {
        return employeeStartDate.isBefore(colleagueEndDate)
                && colleagueStartDate.isBefore(employeeEndDate);
    }

    /**
     * The method compares the start dates of two employees who cooperated in a project.
     * The date the cooperation started is the later date, if they are not equal. Otherwise,
     * the date of the first employee is returned
     *
     * @param employeeStartDate the date when the first employee started working in the project
     * @param colleagueStartDate the date when the second employee started working in the project
     *
     * @return the date when the project cooperation started
     */
    private LocalDate getCollaborationStartDate(LocalDate employeeStartDate, LocalDate colleagueStartDate) {
        return (employeeStartDate.isEqual(colleagueStartDate) || employeeStartDate.isAfter(colleagueStartDate))
                ? employeeStartDate
                : colleagueStartDate;
    }

    /**
     * The method compares the end dates of two employees who cooperated in a project.
     * The date the cooperation ended is the earlier end date, if they are not equal. Otherwise,
     * the date of the first employee is returned
     *
     * @param employeeEndDate the date when the first employee ended working in the project
     * @param colleagueEndDate the date when the second employee ended working in the project
     *
     * @return the date when the project cooperation ended
     */
    private LocalDate getCollaborationEndDate(LocalDate employeeEndDate, LocalDate colleagueEndDate) {
        return (employeeEndDate.isEqual(colleagueEndDate) || employeeEndDate.isBefore(colleagueEndDate))
                ? employeeEndDate
                : colleagueEndDate;
    }


    /**
     * This method defines the pair(s) of coworkers that have cooperated in common projects for the
     * longest period of time
     *
     * @return Map of coworkers' ID pairs as keys and the time they have worked together which is
     *         the longest collaboration time among all pairs of coworkers
     */
    public Map<Pair<Integer, Integer>, Integer> getLongestProjectCollaboration() {
        if (MapUtils.isEmpty(totalCollaborationPerPair)) {
            calculateTotalCollaborationPerPair();
        }
        int longestCollaborationTime = totalCollaborationPerPair
                .values()
                .stream()
                .mapToInt(v -> v)
                .max()
                .orElseThrow(NoSuchElementException::new);

        longestCollaborationsMap =
                totalCollaborationPerPair.entrySet()
                        .stream()
                        .filter(entry -> entry.getValue() == longestCollaborationTime)
                        .collect(toMap(entry -> entry.getKey(),entry -> entry.getValue()));

       return longestCollaborationsMap;
    }

    /**
     * The method takes the aggregated project collaborations per pair of employees and sums the number
     * of days they have worked together, so it reduces the list of numbers in the Multimap values
     * returned by aggregateCollaborationsPerPair() method is reduced to a single number.
     *
     * @return Map of employeeIDs pairs as keys and the total number of cooperation days as value
     */
    public Map<Pair<Integer,Integer>, Integer> calculateTotalCollaborationPerPair() {
        Multimap<Pair<Integer, Integer>, Integer> collaborationsPerPair = aggregateCollaborationsPerPair();
        if (isNull(totalCollaborationPerPair)) {
            totalCollaborationPerPair = new HashMap<>();
        }
        collaborationsPerPair.keySet()
                .forEach(key ->
                        totalCollaborationPerPair.put(
                                key,
                                collaborationsPerPair
                                        .get(key)
                                        .stream()
                                        .reduce(0, Integer::sum)));
        return totalCollaborationPerPair;
    }

    /**
     * The method aggregates the cooperation in projects for every pair of employees, creating a map (multimap),
     * containing ImmutaablePair<EmployeeID, EmployeeID> as map keys and List of projectIDs as values.
     * As values are lists, multimap was chosen as data structure.
     *
     * @return Multimap of the list of common projects per every pair of coworkers
     */
    public Multimap<Pair<Integer, Integer>, Integer> aggregateCollaborationsPerPair() {
        if (CollectionUtils.isEmpty(projectCollaborations)) {
            this.extractProjectCollaborations();
        }
        Multimap<Pair<Integer, Integer>, Integer> collaborationsPerPair = ArrayListMultimap.create();
        projectCollaborations.forEach(collaboration ->
                collaborationsPerPair.put(
                        ImmutablePair.of(collaboration.getEmployee1(), collaboration.getEmployee2()),
                        collaboration.getDaysWorked())
        );
        return collaborationsPerPair;
    }

    public List<ProjectCollaboration> getWinnersCollaborations() {
        if (MapUtils.isEmpty(longestCollaborationsMap)) {
            getLongestProjectCollaboration();
        }
        return longestCollaborationsMap
                .keySet()
                .stream()
                .flatMap(pair -> extractCollaborationsForEmployees(
                        getEmployeesMap().get(pair.getKey()),
                        getEmployeesMap().get(pair.getValue())).stream())
                .collect(Collectors.toUnmodifiableList());
    }

    public Map<Integer, Employee> getEmployeesMap() {
        return employeesMap;
    }
}
