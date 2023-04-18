package com.rosenhristov.mapper;

import com.rosenhristov.model.CSVRowData;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.rosenhristov.utils.Constants.DEFAULT_DATE_FORMAT;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class Mapper {

    public String dateFormat;

    private DateFormat dateFormatter;

    private Mapper() {
        this.dateFormat = DEFAULT_DATE_FORMAT;
        this.dateFormatter = new SimpleDateFormat(dateFormat);
    }

    private Mapper(String dateFormat) {
        this.dateFormat = dateFormat;
        this.dateFormatter = new SimpleDateFormat(dateFormat);
    }

    public static Mapper create() {
        return new Mapper();
    }

    public static Mapper create(String dateFormat) {
        return isBlank(dateFormat)
                ? new Mapper()
                : new Mapper(dateFormat);
    }

    public CSVRowData mapCells(List<String> line) {
        CSVRowData CSVRowData;
        try {
            CSVRowData = new CSVRowData(
                    Integer.parseInt(line.get(0)),
                    Integer.parseInt(line.get(1)),
                    parseDate(line.get(2)),
                    parseDate(line.get(3)));
        } catch (ParseException pe) {
            throw new RuntimeException("Problem parsing date on line " + Arrays.toString(line.toArray()), pe);
        }

        return CSVRowData;
    }

    private Date parseDate(String dateString) throws ParseException {
        return dateString.equalsIgnoreCase("null")
                ? new Date()
                : dateFormatter.parse(dateString);
    }

    public static LocalDate toLocalDate(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
