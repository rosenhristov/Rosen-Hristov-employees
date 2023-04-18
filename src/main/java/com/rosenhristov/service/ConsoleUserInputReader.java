package com.rosenhristov.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.rosenhristov.utils.Constants.*;
import static com.rosenhristov.utils.Utils.*;
import static java.util.Objects.isNull;

public class ConsoleUserInputReader extends BaseUserInputReader {

    public static ConsoleUserInputReader create() {
        return new ConsoleUserInputReader();
    }

    @Override
    public void getUserInput() {
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;

        try {
            inputStreamReader = new InputStreamReader(System.in);
            reader = new BufferedReader(inputStreamReader);

            String response = askIfToUseTheDefaultFileNameAndLocation(reader);
            filePath = response.equalsIgnoreCase("y")
                    ? DEFAULT_FILE_PATH
                    : askForFilePath(reader);
        } catch (IOException ioe) {
            throw new RuntimeException(USER_INPUT_READER_EXCEPTION_MESSAGE, ioe);
        } finally {
            try {
                if(!isNull(reader)) {
                    reader.close();
                }
                if(!isNull(inputStreamReader)) {
                    inputStreamReader.close();
                }
            } catch(IOException ioe) {
                throw new RuntimeException(INPUT_STREAM_OR_READER_CLOSING_EXCEPTION_MESSAGE, ioe);
            }
        }
    }

    private String askIfToUseTheDefaultFileNameAndLocation(BufferedReader reader) throws IOException {
        System.out.println(ENTER_CSV_FILE_PATH_PROMPT
                + EXPECTED_RESPONSE_MESSAGE);
        String response;
        do {
            response = reader.readLine();
            if(!isResponseValid(response)) {
                System.out.println(EXPECTED_RESPONSE_MESSAGE);
            }
        } while(!isResponseValid(response));

        return response;
    }

    private String askForFilePath(BufferedReader reader) throws IOException {
        System.out.println(ENTER_CSV_FILE_PATH_PROMPT);
        do {
            filePath = reader.readLine();
            if(!isCSVFilePath(filePath)) {
                System.out.println(WRONG_CSV_FILE_PATH_MESSAGE);
            }
        } while(!isCSVFilePath(filePath));

        return filePath;
    }
}
