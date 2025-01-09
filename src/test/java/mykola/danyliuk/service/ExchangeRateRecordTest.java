package mykola.danyliuk.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static mykola.danyliuk.service.Helper.mockExchangeRateRecord;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ExchangeRateRecordTest {

    @Test
    void testAllRates() {

        ExchangeRateRecord record = mockExchangeRateRecord();
        Map<String, Map<String, BigDecimal>> allRates = record.allRates();

        assertEquals(0, BigDecimal.valueOf(0.85).compareTo(allRates.get("USD").get("EUR")));
        assertEquals(0, BigDecimal.valueOf(0.75).compareTo(allRates.get("USD").get("GBP")));
        assertEquals(0, BigDecimal.valueOf(1.176470588235).setScale(12, RoundingMode.HALF_DOWN).compareTo(allRates.get("EUR").get("USD")));
    }
}