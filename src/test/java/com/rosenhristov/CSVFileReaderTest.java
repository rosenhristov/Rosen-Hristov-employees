package com.rosenhristov;


import com.rosenhristov.model.CSVRowData;
import com.rosenhristov.service.CSVFileReader;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CSVFileReaderTest {

    private CSVFileReader csvFileReader;
    private String filePath = "src/test/resources/sample.csv";

    @BeforeAll
    void setUp() {
        try {
            this.csvFileReader = CSVFileReader.of(filePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        }
    }

    @Test
    @DisplayName("Test read() method returns expected data")
    void testRead() {
        List<CSVRowData> rowDataList = this.csvFileReader.read();

        assertEquals(500, rowDataList.size());

        CSVRowData rowData1 = rowDataList.get(0);
        CSVRowData rowData2 = rowDataList.get(1);
        CSVRowData rowData25 = rowDataList.get(25);
        CSVRowData rowData145 = rowDataList.get(145);
        CSVRowData rowData499 = rowDataList.get(499);

        assertEquals(145, rowData1.getEmployeeID());
        assertEquals(2, rowData1.getProjectID());
        assertEquals("Fri Jan 20 00:01:00 EET 2012", rowData1.getDateFrom().toString());
        assertEquals("Mon Jan 07 00:10:00 EET 2019", rowData1.getDateTo().toString());

        assertEquals(79, rowData2.getEmployeeID());
        assertEquals(20, rowData2.getProjectID());
        assertEquals("Thu Jan 10 00:03:00 EET 2013", rowData2.getDateFrom().toString());
        assertEquals("Sat Jan 07 00:07:00 EET 2017", rowData2.getDateTo().toString());

        assertEquals(22, rowData25.getEmployeeID());
        assertEquals(13, rowData25.getProjectID());
        assertEquals("Tue Jan 27 00:09:00 EET 2015", rowData25.getDateFrom().toString());
        assertEquals(new Date().toString(), rowData25.getDateTo().toString());

        assertEquals(66, rowData145.getEmployeeID());
        assertEquals(8, rowData145.getProjectID());
        assertEquals("Thu Jan 24 00:02:00 EET 2013", rowData145.getDateFrom().toString());
        assertEquals("Mon Jan 18 00:10:00 EET 2016", rowData145.getDateTo().toString());

        assertEquals(193, rowData499.getEmployeeID());
        assertEquals(20, rowData499.getProjectID());
        assertEquals("Thu Jan 16 00:11:00 EET 2014", rowData499.getDateFrom().toString());
        assertEquals("Thu Jan 10 00:11:00 EET 2019", rowData499.getDateTo().toString());
    }

    @Test
    @DisplayName("Test read() method throws exception when file is not found")
    void testReadThrowsFileNotFoundException() {
        Assertions.assertThrows(FileNotFoundException.class, () -> {
            CSVFileReader csvFileReader = CSVFileReader.of("nonexistent.csv");
            csvFileReader.read();
        });
    }

    @Test
    @DisplayName("Test read() method throws exception when file is not a CSV")
    void testReadThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            File txtFile = new File("src/test/resources/sample.txt");
            CSVFileReader csvFileReader = CSVFileReader.of(txtFile);
            csvFileReader.read();
        });
    }
}