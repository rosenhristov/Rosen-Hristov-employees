package com.rosenhristov;

import com.rosenhristov.service.DateAnalyst;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DateAnalystTest {

    private final List<List<String>> VALID_CSV_DATA = List.of(
            List.of("1", "1", "2021/04/01", "2022/04/15"),
            List.of("2", "1", "2021/04/05", "2022/04/18"),
            List.of("3", "1", "2021/04/01", "2022/04/11"),
            List.of("3", "2", "2021/04/11", "2019/04/22"),
            List.of("1", "2", "2021/04/22", "2022/04/30")
    );

    private final List<List<String>> CSV_DATA_WITHOUT_DATES = Arrays.asList(
            Arrays.asList("1", "jane.doe@example.com", "Apple", "Samsung"),
            Arrays.asList("2", "john.doe@example.com", "Google", "Facebook"),
            Arrays.asList("3", "bob.smith@example.com", "Twitter", "Microsoft")
    );

    @Test
    @DisplayName("Should throw an IllegalArgumentException when row strings are null")
    void shouldThrowIllegalArgumentExceptionWhenRowStringsAreNull() {
        assertThrows(IllegalArgumentException.class, () -> DateAnalyst.of(null));
    }

    @Test
    @DisplayName("Should throw an IllegalArgumentException when row strings are empty")
    void shouldThrowIllegalArgumentExceptionWhenRowStringsAreEmpty() {
        List<List<String>> emptyList = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> DateAnalyst.of(emptyList));
    }

    @Test
    @DisplayName("Should identify the correct date format")
    void testIdentifyCorrectDateFormat() {
        DateAnalyst dateAnalyst = DateAnalyst.of(VALID_CSV_DATA).identifyDateFormat();
        assertEquals("yyyy/mm/dd", dateAnalyst.getDateFormat());
    }

    @Test
    @DisplayName("Should throw an IllegalArgumentException when there are no dates in row strings")
    void shouldThrowIllegalArgumentExceptionWhenNoDatesInRowStrings() {
        assertThrows(IllegalArgumentException.class, () -> DateAnalyst.of(CSV_DATA_WITHOUT_DATES).identifyDateFormat());
    }

    @Test
    @DisplayName("Should throw an IllegalArgumentException when the date format has no valid date tokens")
    void shouldThrowIllegalArgumentExceptionWhenInvalidDateTokens() {
        List<List<String>> invalidCsvData = Arrays.asList(
                List.of("1", "jane.doe@example.com", "abc/efg/hijk", "lmn/opq/rst")
        );
        assertThrows(IllegalArgumentException.class, () -> DateAnalyst.of(invalidCsvData).identifyDateFormat());
    }
}
