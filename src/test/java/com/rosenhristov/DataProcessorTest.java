package com.rosenhristov;


import com.rosenhristov.mapper.Mapper;
import com.rosenhristov.model.CSVRowData;
import com.rosenhristov.model.Employee;
import com.rosenhristov.model.ProjectCollaboration;
import com.rosenhristov.service.DataProcessor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataProcessorTest {

    private List<CSVRowData> csvRowData;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");

    private Map<String, Pair<Date, Date>> datesMap;
    private List<Employee> employees;

    @BeforeEach
    void setUp() throws ParseException {
        datesMap = Map.of(
                "Emp1Proj1", ImmutablePair.of(dateFormat.parse("2021-04-01"), dateFormat.parse("2022-04-15")),
                "Emp2Proj1", ImmutablePair.of(dateFormat.parse("2021-04-05"), dateFormat.parse("2022-04-18")),
                "Emp3Proj1", ImmutablePair.of(dateFormat.parse("2021-04-01"), dateFormat.parse("2022-04-11")),
                "Emp3Proj2", ImmutablePair.of(dateFormat.parse("2021-04-11"), dateFormat.parse("2022-04-22")),
                "Emp1Proj2", ImmutablePair.of(dateFormat.parse("2021-04-22"), dateFormat.parse("2022-04-30"))
        );

        csvRowData = List.of(
                new CSVRowData(1, 1,
                        datesMap.get("Emp1Proj1").getKey(), datesMap.get("Emp1Proj1").getValue()),
                new CSVRowData(2, 1,
                        datesMap.get("Emp2Proj1").getKey(), datesMap.get("Emp2Proj1").getValue()),
                new CSVRowData(3, 1,
                        datesMap.get("Emp3Proj1").getKey(), datesMap.get("Emp3Proj1").getValue()),
                new CSVRowData(3, 2,
                        datesMap.get("Emp3Proj2").getKey(), datesMap.get("Emp3Proj2").getValue()),
                new CSVRowData(1, 2,
                        datesMap.get("Emp1Proj2").getKey(), datesMap.get("Emp1Proj2").getValue()));

        employees = List.of(
                new Employee(1,
                        Map.of(1, Pair.of(
                                        Mapper.toLocalDate(datesMap.get("Emp1Proj1").getKey()),
                                        Mapper.toLocalDate(datesMap.get("Emp1Proj1").getValue())),
                                2, Pair.of(
                                        Mapper.toLocalDate(datesMap.get("Emp1Proj2").getKey()),
                                        Mapper.toLocalDate(datesMap.get("Emp1Proj2").getValue()))
                        )
                ),
                new Employee(2,
                        Map.of(1, Pair.of(
                                Mapper.toLocalDate(datesMap.get("Emp2Proj1").getKey()),
                                Mapper.toLocalDate(datesMap.get("Emp2Proj1").getValue()))
                        )
                ),
                new Employee(3,
                        Map.of(1, ImmutablePair.of(
                                        Mapper.toLocalDate(datesMap.get("Emp3Proj1").getKey()),
                                        Mapper.toLocalDate(datesMap.get("Emp3Proj1").getValue())),
                                2, ImmutablePair.of(
                                        Mapper.toLocalDate(datesMap.get("Emp3Proj2").getKey()),
                                        Mapper.toLocalDate(datesMap.get("Emp3Proj2").getValue()))
                        )
                )
        );
    }

    @Test
    @DisplayName("GroupProjectsByEmployee")
    public void testGroupProjectsByEmployee() {
        DataProcessor dataProcessor = DataProcessor.of(csvRowData);
        Map<Integer, Employee> employees = dataProcessor.groupProjectsByEmployee();
        assertEquals(3, employees.entrySet().size());

        Employee employee1 = employees.get(1);
        assertEquals(2, employee1.getProjects().size());
        assertTrue(employee1.getProjects().containsKey(1));
        assertTrue(employee1.getProjects().containsKey(2));

        Employee employee2 = employees.get(2);
        assertEquals(1, employee2.getProjects().entrySet().size());
        assertTrue(employee2.getProjects().containsKey(1));

        Employee employee3 = employees.get(3);
        assertEquals(2, employee3.getProjects().entrySet().size());
        assertTrue(employee3.getProjects().containsKey(1));
        assertTrue(employee3.getProjects().containsKey(2));

    }

    @Test
    @DisplayName("ExtractCollaborationsForEmployees")
    public void testExtractCollaborationsForEmployees() throws ParseException {
        DataProcessor dataProcessor = DataProcessor.of(csvRowData);
        Employee employee1 = employees.get(0);
        Employee employee3 = employees.get(2);

        List<ProjectCollaboration> collaborations = dataProcessor.extractCollaborationsForEmployees(employee1, employee3);
        assertEquals(2, collaborations.size());

        ProjectCollaboration collaboration1 = collaborations.get(0);
        assertEquals(1, collaboration1.getEmployee1());
        assertEquals(3, collaboration1.getEmployee2());
        assertEquals(1, collaboration1.getProjectID());
        assertEquals(10, collaboration1.getDaysWorked());

        ProjectCollaboration collaboration2 = collaborations.get(1);
        assertEquals(1, collaboration2.getEmployee1());
        assertEquals(3, collaboration2.getEmployee2());
        assertEquals(2, collaboration2.getProjectID());
        assertEquals(0, collaboration2.getDaysWorked());
    }


    @Test
    @DisplayName("ExtractProjectCollaborations")
    public void testExtractProjectCollaborations() {
        DataProcessor dataProcessor = DataProcessor.of(csvRowData);
        List<ProjectCollaboration> collaborations = dataProcessor.extractProjectCollaborations();
        assertEquals(4, collaborations.size());
    }
}