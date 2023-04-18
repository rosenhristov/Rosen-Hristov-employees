package com.rosenhristov.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.rosenhristov.mapper.Mapper;
import com.rosenhristov.model.CSVRowData;
import org.jetbrains.annotations.TestOnly;

import java.io.*;

import java.util.*;

import static com.rosenhristov.utils.Constants.*;
import static com.rosenhristov.utils.Utils.*;
import static java.util.Objects.isNull;

public class CSVFileReader {

    private File csvFile;

    private Mapper mapper;

    private String dateFormat;

    private CSVFileReader(File csvFile) {
        this.csvFile = csvFile;
        this.mapper = Mapper.create();
    }

    private CSVFileReader(String filePath) {
        this(new File(filePath));
    }

    public static CSVFileReader of(String filePath)
            throws IllegalArgumentException, FileNotFoundException {

        if (!isCSVFilePath(filePath)) {
            throw new IllegalArgumentException(
                    String.format(NOT_A_CSV_FILE_PATH_MESSAGE, filePath));
        }
        File csvFile = new File(filePath);
        if (!isValidFile(csvFile)) {
            throw new FileNotFoundException(String.format(INVALID_OR_NONEXISTENT_FILE_MESSAGE, csvFile.getName()));
        }
        return new CSVFileReader(csvFile);
    }

    public static CSVFileReader of(File csvFile) {
        if (!isCSVFile(csvFile)) {
            throw new IllegalArgumentException(
                    String.format(INVALID_OR_NONEXISTENT_CSV_FILE_MESSAGE, csvFile.getName()));
        }
        return new CSVFileReader(csvFile);
    }

    public List<CSVRowData> read() {
        List<CSVRowData> rowData = new LinkedList<>();
        String[] line;
        FileReader fileReader = null;
        CSVReader csvReader = null;
        try {
            fileReader = new FileReader(this.csvFile);
            csvReader = new CSVReader(fileReader);

            List<List<String>> rowStrings = new LinkedList<>();
            do {
                line = csvReader.readNext();
                if(isValidLine(line) && !Arrays.toString(line).equals(TITLE_ROW)) {
                    rowStrings.add(List.of(line));
                }
            } while(!isNull(line));

            dateFormat = DateAnalyst
                    .of(rowStrings)
                    .identifyDateFormat()
                    .getDateFormat();
            mapper = Mapper.create(dateFormat);

            for (List<String> cells : rowStrings) {
                CSVRowData CSVRowData = mapper.mapCells(cells);
                if (!isNull(CSVRowData)) {
                    rowData.add(CSVRowData);
                }
            }
        } catch (IOException ioe) {
            throw new RuntimeException(FILE_READING_IO_EXCEPTION_MESSAGE + this.csvFile.getName(), ioe);
        } catch (CsvValidationException e) {
            throw new RuntimeException(FILE_READING_CSV_EXCEPTION_MESSAGE + this.csvFile.getName(), e);
        } finally {
            try {
                if(!isNull(csvReader)) {
                    csvReader.close();
                }
                if(!isNull(fileReader)) {
                    fileReader.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(FILE_READER_CLOSING_EXCEPTION_MESSAGE, e);
            }
        }
        return rowData;
    }

    @TestOnly
    public File getCsvFile() {
        return this.csvFile;
    }
}
