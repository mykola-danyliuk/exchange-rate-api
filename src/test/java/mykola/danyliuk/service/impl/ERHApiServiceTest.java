package mykola.danyliuk.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import mykola.danyliuk.service.Currency;
import mykola.danyliuk.service.ExchangeRateRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ERHApiServiceTest {

    @Mock
    private RestTemplate restTemplate;

    private ERHApiService erhApiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        erhApiService = new ERHApiService(restTemplate, "testAccessKey", true);
    }

    @Test
    void fetchLatestExchangeRates() throws Exception {
        String jsonResponse = """
            {
                "success": true,
                "timestamp": 123456789,
                "quotes": {
                    "USDUSD": 1,
                    "USDEUR": 0.85,
                    "USDGBP": 0.75
                }
            }
        """;

        ERHApiService.LiveDTO liveDTO = objectMapper.readValue(jsonResponse, ERHApiService.LiveDTO.class);
        when(restTemplate.getForObject(anyString(), any())).thenReturn(liveDTO);

        Optional<ExchangeRateRecord> result = erhApiService.fetchLatestExchangeRates();

        assertTrue(result.isPresent());
        assertEquals(123456789L, result.get().timestamp());
        assertEquals(Map.of("USD", BigDecimal.ONE, "EUR", BigDecimal.valueOf(0.85), "GBP", BigDecimal.valueOf(0.75)), result.get().usdRates());
    }

    @Test
    void fetchCurrencies() throws Exception {
        String jsonResponse = """
            {
                "success": true,
                "currencies": {
                    "USD": "United States Dollar",
                    "EUR": "Euro",
                    "GBP": "British Pound Sterling"
                }
            }
        """;

        ERHApiService.ListDTO listDTO = objectMapper.readValue(jsonResponse, ERHApiService.ListDTO.class);
        when(restTemplate.getForObject(anyString(), any())).thenReturn(listDTO);

        List<Currency> result = erhApiService.fetchCurrencies();

        assertEquals(3, result.size());
        assertEquals(new Currency("USD", "United States Dollar"), result.get(0));
        assertEquals(new Currency("EUR", "Euro"), result.get(1));
        assertEquals(new Currency("GBP", "British Pound Sterling"), result.get(2));
    }

    @Test
    void fetchLatestExchangeRatesThrowsException() {
        doThrow(new RuntimeException("Error fetching data")).when(restTemplate).getForObject(anyString(), any());

        Optional<ExchangeRateRecord> result = erhApiService.fetchLatestExchangeRates();

        assertFalse(result.isPresent());
    }

    @Test
    void fetchCurrenciesThrowsException() {
        doThrow(new RuntimeException("Error fetching data")).when(restTemplate).getForObject(anyString(), any());

        List<Currency> result = erhApiService.fetchCurrencies();

        assertTrue(result.isEmpty());
    }
}