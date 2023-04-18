package com.rosenhristov.utils;

import java.io.File;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class Utils {

    public static final String DEFAULT_FILE_PATH = "src/main/resources/projects.csv";


    public static boolean isValidLine(String[] line) {
        return !isNull(line) && !isNull(line[0]) && !isNull(line[1]) && isNotBlank(line[2]);
    }

    public static boolean isCSVFilePath(String filePath) {
        return isNotBlank(filePath) && filePath.endsWith(".csv");
    }

    public static boolean isCSVFile(File csvFile) {
        return isValidFile(csvFile) && isCSVFilePath(csvFile.getName());
    }

    public static boolean isValidFile(File file) {
        return !isNull(file)
                && file.exists()
                && file.isFile();
    }

    public static boolean isResponseValid(String response) {
        return isNotBlank(response)
                && (response.equalsIgnoreCase("n")
                        || response.equalsIgnoreCase("y"));
    }
}
