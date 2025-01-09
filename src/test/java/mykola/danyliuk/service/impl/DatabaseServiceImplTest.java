package mykola.danyliuk.service.impl;

import mykola.danyliuk.model.CurrencyModel;
import mykola.danyliuk.model.RateModel;
import mykola.danyliuk.repository.CurrencyRepository;
import mykola.danyliuk.repository.RateRepository;
import mykola.danyliuk.service.Currency;
import mykola.danyliuk.service.ExchangeRateRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class DatabaseServiceImplTest {

    @Mock
    private RateRepository rateRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @InjectMocks
    private DatabaseServiceImpl databaseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveExchangeRatesSuccessfully() {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        map.put("EUR", BigDecimal.valueOf(0.85));
        map.put("GBP", BigDecimal.valueOf(0.75));
        ExchangeRateRecord record = new ExchangeRateRecord(
            123456789L,
            map
        );
        List<RateModel> rateModels = List.of(
            new RateModel("EUR", BigDecimal.valueOf(0.85), 123456789L),
            new RateModel("GBP", BigDecimal.valueOf(0.75), 123456789L)
        );

        databaseService.saveExchangeRates(record);

        verify(rateRepository).deleteAll();
        verify(rateRepository).saveAll(rateModels);
    }

    @Test
    void fetchExchangeRatesSuccessfully() {
        List<RateModel> rateModels = List.of(
            new RateModel("EUR", BigDecimal.valueOf(0.85), 123456789L),
            new RateModel("GBP", BigDecimal.valueOf(0.75), 123456789L)
        );
        when(rateRepository.findAll()).thenReturn(rateModels);

        Optional<ExchangeRateRecord> result = databaseService.fetch();

        assertTrue(result.isPresent());
        assertEquals(123456789L, result.get().timestamp());
        assertEquals(BigDecimal.valueOf(0.85), result.get().usdRates().get("EUR"));
        assertEquals(BigDecimal.valueOf(0.75), result.get().usdRates().get("GBP"));
    }

    @Test
    void fetchExchangeRatesReturnsEmptyWhenNoRates() {
        when(rateRepository.findAll()).thenReturn(List.of());

        Optional<ExchangeRateRecord> result = databaseService.fetch();

        assertTrue(result.isEmpty());
    }

    @Test
    void saveCurrenciesSuccessfully() {
        Collection<Currency> currencies = List.of(new Currency("USD", "US Dollar"), new Currency("EUR", "Euro"));
        List<CurrencyModel> currencyModels = List.of(
            new CurrencyModel("USD", "US Dollar"),
            new CurrencyModel("EUR", "Euro")
        );

        databaseService.saveCurrencies(currencies);

        verify(currencyRepository).deleteAll();
        verify(currencyRepository).saveAll(currencyModels);
    }

    @Test
    void fetchCurrenciesSuccessfully() {
        List<CurrencyModel> currencyModels = List.of(
            new CurrencyModel("USD", "US Dollar"),
            new CurrencyModel("EUR", "Euro")
        );
        when(currencyRepository.findAll()).thenReturn(currencyModels);

        Collection<Currency> result = databaseService.fetchCurrencies();

        assertEquals(2, result.size());
        assertTrue(result.contains(new Currency("USD", "US Dollar")));
        assertTrue(result.contains(new Currency("EUR", "Euro")));
    }
}