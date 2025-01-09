package mykola.danyliuk.service.impl;

import mykola.danyliuk.service.DatabaseService;
import mykola.danyliuk.service.ExchangeRateRecord;
import mykola.danyliuk.service.RateApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.CacheManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static mykola.danyliuk.service.Helper.mockExchangeRateRecord;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeRateServiceImplTest {

    @Mock
    private RateApiService apiService;
    @Mock
    private DatabaseService databaseService;
    @Mock
    private CacheManager cacheManager;
    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache("exchangeRates")).thenReturn(Mockito.mock(org.springframework.cache.Cache.class));
        when(cacheManager.getCache("allRates")).thenReturn(Mockito.mock(org.springframework.cache.Cache.class));
    }

    @Test
    void convertValueSuccessfully() {
        when(databaseService.fetch()).thenReturn(Optional.of(mockExchangeRateRecord()));

        exchangeRateService.updateExchangeRates();

        BigDecimal convertedValue = exchangeRateService.convertValue("USD", "EUR", 100);

        assertEquals(BigDecimal.valueOf(85).compareTo(convertedValue),0);
    }

    @Test
    void convertValueToListSuccessfully() {

        when(databaseService.fetch()).thenReturn(Optional.of(mockExchangeRateRecord()));

        exchangeRateService.updateExchangeRates();

        Map<String, BigDecimal> convertedValues = exchangeRateService.convertValueToList("USD", 100);

        assertEquals(3, convertedValues.size());
        assertEquals(BigDecimal.valueOf(85).compareTo(convertedValues.get("EUR")),0);
        assertEquals(BigDecimal.valueOf(75).compareTo(convertedValues.get("GBP")), 0);
    }

    @Test
    void updateExchangeRatesSuccessfully() {

        var record = mockExchangeRateRecord();
        when(apiService.fetchLatestExchangeRates()).thenReturn(Optional.of(record));

        exchangeRateService.updateExchangeRates();

        assertEquals(BigDecimal.valueOf(0.85).compareTo(exchangeRateService.getRate("USD", "EUR")), 0);
        assertEquals(BigDecimal.valueOf(0.75).compareTo(exchangeRateService.getRate("USD", "GBP")), 0);
        verify(databaseService).saveExchangeRates(record);
    }

    @Test
    void fallbackToDatabaseWhenApiFails() {

        var record = mockExchangeRateRecord();
        when(apiService.fetchLatestExchangeRates()).thenReturn(Optional.empty());
        when(databaseService.fetch()).thenReturn(Optional.of(record));

        exchangeRateService.updateExchangeRates();

        assertEquals(BigDecimal.valueOf(0.85).compareTo(exchangeRateService.getRate("USD", "EUR")), 0);
        assertEquals(BigDecimal.valueOf(0.75).compareTo(exchangeRateService.getRate("USD", "GBP")), 0);
        verify(databaseService, never()).saveExchangeRates(record);}

    @Test
    void notLoadednWhenNoExchangeRatesAvailable() {

        when(apiService.fetchLatestExchangeRates()).thenReturn(Optional.empty());
        when(databaseService.fetch()).thenReturn(Optional.empty());

        exchangeRateService.updateExchangeRates();

        assertFalse(exchangeRateService.isLoaded());
    }

    @Test
    void getRateThrowsExceptionForInvalidCurrency() {
        when(apiService.fetchLatestExchangeRates()).thenReturn(Optional.of(mockExchangeRateRecord()));

        exchangeRateService.updateExchangeRates();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            exchangeRateService.getRate("INVALID", "EUR");
        });

        assertEquals("Currency not found: INVALID. List of supported currencies could be found on /currencies endpoint", exception.getMessage());
    }

}