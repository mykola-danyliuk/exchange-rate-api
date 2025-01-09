package mykola.danyliuk.service.impl;

import mykola.danyliuk.service.Currency;
import mykola.danyliuk.service.DatabaseService;
import mykola.danyliuk.service.RateApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class CurrenciesServiceImplTest {

    @Mock
    private RateApiService apiService;
    @Mock
    private DatabaseService databaseService;
    @InjectMocks
    private CurrenciesServiceImpl currenciesService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchCurrenciesFromApi() {
        List<Currency> apiCurrencies = List.of(new Currency("USD", "US Dollar"), new Currency("EUR", "Euro"));
        when(apiService.fetchCurrencies()).thenReturn(apiCurrencies);

        currenciesService.init();

        assertEquals(apiCurrencies, currenciesService.getCurrencies());
    }

    @Test
    void fetchCurrenciesFromDatabaseWhenApiFails() {
        when(apiService.fetchCurrencies()).thenReturn(Collections.emptyList());
        List<Currency> dbCurrencies = List.of(new Currency("USD", "US Dollar"), new Currency("EUR", "Euro"));
        when(databaseService.fetchCurrencies()).thenReturn(dbCurrencies);

        currenciesService.init();

        assertEquals(dbCurrencies, currenciesService.getCurrencies());
    }

    @Test
    void notLoadedWhenNoCurrenciesAvailable() {
        when(apiService.fetchCurrencies()).thenReturn(Collections.emptyList());
        when(databaseService.fetchCurrencies()).thenReturn(Collections.emptyList());

        currenciesService.init();

        assertFalse(currenciesService.isLoaded());
    }

    @Test
    void saveCurrenciesToDatabaseWhenFetchedFromApi() {
        List<Currency> apiCurrencies = List.of(new Currency("USD", "US Dollar"), new Currency("EUR", "Euro"));
        when(apiService.fetchCurrencies()).thenReturn(apiCurrencies);

        currenciesService.init();

        assertEquals(apiCurrencies, currenciesService.getCurrencies());
    }
}