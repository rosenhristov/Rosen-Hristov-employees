package com.rosenhristov.utils;

public interface Constants {

    public static final String DEFAULT_DATE_FORMAT = "yyyy-mm-dd";

    String EXPECTED_RESPONSE_MESSAGE = "Please answer with 'y' for 'Yes' or 'n' for 'No'";
    String USER_INPUT_READER_EXCEPTION_MESSAGE = "Problem occurred while reading your input";
    String INPUT_STREAM_OR_READER_CLOSING_EXCEPTION_MESSAGE = "Problems closing user input reader";
    String FILE_READER_CLOSING_EXCEPTION_MESSAGE = "Problems closing user input reader";
    String ENTER_CSV_FILE_PATH_PROMPT = "Do you want to analyse the default 'projects.csv' file in resources directory? "
            + EXPECTED_RESPONSE_MESSAGE;
    String WRONG_CSV_FILE_PATH_MESSAGE = "This is not a path to '.csv' file. The file name should have a '.csv' extension";


    String TITLE_ROW = "[EmpID, ProjectID, DateFrom, DateTo]";
    String NOT_A_CSV_FILE_PATH_MESSAGE = "This path %s is not a path to a '.csv' file.";
    String INVALID_OR_NONEXISTENT_FILE_MESSAGE = "File %s is not a valid file or it does not exist ";
    String INVALID_OR_NONEXISTENT_CSV_FILE_MESSAGE = "This file %s is not a '.csv' file or it does not exist.";
    String FILE_READING_IO_EXCEPTION_MESSAGE = "Problem occurred while reading file ";
    String FILE_READING_CSV_EXCEPTION_MESSAGE = "Problems reading lines of file ";
}
