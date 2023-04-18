package com.rosenhristov.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

/**
 * Analyses the date strings read from the CSV file and identifies the date format to let the Mapper instance
 * parse the dates successfully to java.util.Date objects by supporting CSV files containing dates formatted
 * using different formats. The analyser expects that all dates in a file are formatted one and the same way.
 * If the CSV file contained dates formatted differently on different rows, parsing by Mapper will fail.
 *
 * Supported formats:
 * 'dd/mm/yyyy', 'mm/dd/yyyy', 'yyyy/mm/dd', 'yyyy/dd/mm',
 * 'dd-mm-yyyy', 'mm-dd-yyyy', 'yyyy-mm-dd, 'yyyy-dd-mm',
 * 'dd_mm_yyyy', 'mm_dd_yyyy', 'yyyy_mm_dd, 'yyyy_dd_mm',
 * 'dd.mm.yyyy', 'mm.dd.yyyy', 'yyyy.mm.dd', 'yyyy.dd.mm',
 * 'dd mm yyyy', 'mm dd yyyy', 'yyyy mm dd', 'yyyy dd mm',
 * 'dd:mm:yyyy', 'mm:dd:yyyy', 'yyyy:mm:dd', 'yyyy:dd:mm',
 * 'dd;mm;yyyy', 'mm;dd;yyyy', 'yyyy;mm;dd', 'yyyy;dd;mm',
 * 'dd\\mm\\yyyy', 'mm\\dd\\yyyy', 'yyyy\\mm\\dd', 'yyyy\\dd\\mm',
 */
public class DateAnalyst {

    public static final String DAY = "dd";
    public static final String MONTH = "mm";
    public static final String YEAR = "yyyy";
    public static final String DELIMITER_REGEX = "[\\\\/\\-\\.\\:\\;\\s_]";

    private List<List<String>> rowStrings;

    private String dateFormat;
    private String leftFormatToken;
    private String midFormatToken;
    private String rightFormatToken;
    private String delimiter;

    private Set<String> leftTokens = new HashSet<>();
    private Set<String> midTokens = new HashSet<>();
    private Set<String> rightTokens = new HashSet<>();

    private DateAnalyst() {
    }

    private DateAnalyst(List<List<String>> rowStrings) {
        this.rowStrings = rowStrings;
    }

    public static DateAnalyst of(List<List<String>> rowStrings) {
        if(CollectionUtils.isEmpty(rowStrings)) {
            throw new IllegalArgumentException("No data provided to analyse.");
        }
        return new DateAnalyst(rowStrings);
    }

    public DateAnalyst identifyDateFormat() {
        //collect all date strings from the file
        List<String> dateStrings = new LinkedList<>();
        rowStrings.forEach(row -> {
            dateStrings.add(row.get(2)); // 'dateFrom' dates
            if (!row.get(3).equalsIgnoreCase("null")) {
                dateStrings.add(row.get(3)); // 'dateTo' dates if not NULL
            }
        });

        // collect all left, middle and right date parts in separate collections
        // and identify delimiter
        dateStrings.forEach(date -> {
            String[] dateTokens = date.split(DELIMITER_REGEX);
            if (isNull(this.delimiter)) {
                identifyDelimiter(date);
                if (StringUtils.isEmpty(delimiter)) {
                    throw new IllegalArgumentException("No delimiter recognized");
                }
            }
            leftTokens.add(dateTokens[0]);
            midTokens.add(dateTokens[1]);
            rightTokens.add(dateTokens[2]);
        });

        // identify which tokens are 'dd', 'mm' or 'yyyy'
        identifyDay();
        identifyMonth();
        identifyYear();

        this.dateFormat = new StringBuilder()
                .append(leftFormatToken)
                .append(delimiter)
                .append(midFormatToken)
                .append(delimiter)
                .append(rightFormatToken)
                .toString();

        return this;
    }

    private void identifyDay() {
        if (isDay(List.copyOf(leftTokens))) {
            leftFormatToken = DAY;
        } else if (isDay(List.copyOf(midTokens))) {
            midFormatToken = DAY;
        } else if (isDay(List.copyOf(rightTokens))) {
            rightFormatToken = DAY;
        } else {
            throw new IllegalArgumentException("This date format has not valid date tokens");
        }
    }

    private void identifyMonth() {
        if (isMonth(List.copyOf(leftTokens))) {
            leftFormatToken = MONTH;
        } else if (isMonth(List.copyOf(midTokens))) {
            midFormatToken = MONTH;
        } else if (isMonth(List.copyOf(rightTokens))) {
            rightFormatToken = MONTH;
        } else {
            throw new IllegalArgumentException("This date format has not valid date tokens");
        }
    }

    private void identifyYear() {
        if (isYear(List.copyOf(leftTokens))) {
            leftFormatToken = YEAR;
        } else if (isYear(List.copyOf(midTokens))) {
            midFormatToken = YEAR;
        } else if (isYear(List.copyOf(rightTokens))) {
            rightFormatToken = YEAR;
        } else {
            throw new IllegalArgumentException("This date format has not valid date tokens");
        }
    }

    private void identifyDelimiter(String date) {
        for (char ch : date.toCharArray()) {
            if(!CharUtils.isAsciiAlphanumeric(ch)
                && (date.indexOf(ch) != date.lastIndexOf(ch))) {
                    this.delimiter = String.valueOf(ch);
                    return;
            }
        }
    }

    private boolean isDay(List<String> tokens) {
        List<String> randomTokens = getRandomTokens(tokens);
        List<String> dayTokens = randomTokens
                .stream()
                .filter(token -> isDayOrMonthToken(token))
                .collect(Collectors.toList());

        // if random string verification did not work skip integer verification
        if (dayTokens.size() != randomTokens.size()) {
            return false;
        }

        return areDayTokens(tokens);
    }

    private boolean isMonth(List<String> tokens) {
        List<String> randomTokens = getRandomTokens(tokens);
        List<String> monthTokens = randomTokens
                .stream()
                .filter(token -> isDayOrMonthToken(token))
                .collect(Collectors.toList());

        // if random string verification did not work skip integer verification
        if (monthTokens.size() != randomTokens.size()) {
            return false;
        }

        return areMonthTokens(tokens);
    }

    private boolean isYear(List<String> tokens) {
        List<String> randomTokens = getRandomTokens(tokens);
        List<String> yearTokens =
                randomTokens
                        .stream()
                        .filter(token -> isYearToken(token))
                        .collect(Collectors.toList());

        return yearTokens.size() == randomTokens.size();
    }

    /**
     * Checks if the date part is of type 'dd' or 'mm',
     * i.e. '03' or '25'
     *
     * @param token - the date part to be verified
     * @return
     */
    private boolean isDayOrMonthToken(String token) {
        return token.length() == 2 && isNumber(token);
    }

    /**
     * Checks if the date part is year, i,.e. of type 'yyyy', e.g. '2023'
     *
     * @param token - the date part to be verified
     * @return
     */
    private boolean isYearToken(String token) {
        return token.length() == 4 && isNumber(token);
    }

    /**
     * Checks if the date parts already identified as 'dd' or 'mm' token are day ('dd') part - if the max value
     * in the collection is greater than 12, i.e. within the range [1:31]
     *
     * @param tokens - the collection of day parts to be verified
     * @return true if the tokens are identified as 'dd' tokens, otherwise returns false
     */
    private boolean areDayTokens(List<String> tokens) {
        OptionalInt optMax = getMaxValue(tokens);
        checkMaxInt(optMax);

        return optMax.getAsInt() > 12;
    }

    /**
     * Checks if the date parts already identified as 'dd' or 'mm' token are month ('mm') parts - if
     * the max value in the collection is less than 12, i.e. in the range [1:12]
     *
     * @param tokens - the collection of day parts to be verified
     * @return true if the tokens are identified as 'mm' tokens, otherwise returns false
     */
    private boolean areMonthTokens(List<String> tokens) {
        OptionalInt optMax = getMaxValue(tokens);
        checkMaxInt(optMax);

        return getMaxValue(tokens).getAsInt() <= 12;
    }

    private OptionalInt getMaxValue(List<String> tokens) {
        return tokens.stream()
                .map(token -> {
                    token = token.startsWith("0")
                            ? token.substring(1)
                            : token;
                    return Integer.parseInt(token);
                })
                .mapToInt(num -> num)
                .max();
    }

    private List<String> getRandomTokens(List<String> tokens) {
        Set<String> randomTokens = new HashSet<>();

        int randomTokensSize = tokens.size() > 10 ? 10 : tokens.size();

        // if collection is small, return it to be fully checked
        if (randomTokensSize == tokens.size()) {
            return tokens;
        }

        /*
         * else get 10 random but unique (stored in Set) tokens for initial string verification
         * to avoid iteration along the full date parts collection later, during integer verification,
         * in case this is not the needed collection of date parts
         */
        Random rand = new Random();
        do {
            randomTokens.add(
                    tokens.get(
                            rand.nextInt(
                                    tokens.size())));
        } while (randomTokens.size() < randomTokensSize);

        return List.copyOf(randomTokens);
    }

    private boolean isNumber(String token) {
        for (char ch : token.toCharArray()) {
            if(!CharUtils.isAsciiNumeric(ch)) {
                return false;
            }
        }
        return true;
    }

    private void checkMaxInt(OptionalInt optMax) {
        if(optMax.isEmpty()) {
            throw new IllegalArgumentException("No max value identified in date tokens");
        }
    }

    public String getDateFormat() {
        return this.dateFormat;
    }
}
