package com.rosenhristov.service;

import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public abstract class BaseUserInputReader {

    protected String filePath;
    protected String dateFormat;

    public abstract void getUserInput();

    /**
     * The dateFormat input should be for example in the form "dd.mm.yyyy"
     */
    protected boolean isValidDateFormat(String dateFormat) {
        return isNotBlank(dateFormat)
                && Pattern
                .compile("dd-mm-yyyy|yyyy-mm-dd|mm-dd-yyyy|dd/mm/yyyy|mm/dd/yyyy|yyyy/mm/dd")
                .matcher(dateFormat)
                .matches();
    }

    public String getFilePath() {
        return filePath;
    }

    public String getDateFormat() {
        return dateFormat;
    }
}
