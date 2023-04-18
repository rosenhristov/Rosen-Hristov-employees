package com.rosenhristov;

import com.rosenhristov.model.CSVRowData;
import com.rosenhristov.service.*;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;

public class CSVFileProcessorApp {

    private static String filePath;

    public static void main(String[] args) {
        try {

            //            BaseUserInputReader userInputReader = ConsoleUserInputReader.create();
            BaseUserInputReader userInputReader = UserInputReaderGUI.create();

            userInputReader.getUserInput();

            filePath = userInputReader.getFilePath();

            CSVFileReader csvFileReader = CSVFileReader.of(filePath);
            List<CSVRowData> csvRowData = csvFileReader.read();

            DataProcessor dataProcessor = DataProcessor.of(csvRowData);

            /*
             * I use data structure Map<Pair<EmpID, EmpID>, longestCollaborationTime> here because I cannot be sure that
             * the 'winning' pair of employees is only one, there is a probability to have two or more pairs with the
             * identified longest collaboration time so the code should be capable of handling such an edge case
             */
            Map<Pair<Integer,Integer>, Integer> longestCollaborationsMap = dataProcessor.getLongestProjectCollaboration();

            longestCollaborationsMap.keySet().forEach(key -> System.out.println(String.format(
                    "\n\n************** Longest collaboration in common projects ***************************\n" +
                    "            Employee1 ID: %s, Employee2 ID: %s, Days worked: %s" +
                    "\n***********************************************************************************\n",
                    key.getKey(), key.getValue(), longestCollaborationsMap.get(key))));

            ResultDisplayer.of(dataProcessor.getWinnersCollaborations()).displayCollaborationsAsDataGrid();

        } catch (IOException e) {
            throw new RuntimeException("Exception occurred:", e);
        }
    }
}