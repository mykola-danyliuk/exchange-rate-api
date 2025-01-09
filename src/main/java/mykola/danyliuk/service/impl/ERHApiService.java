package mykola.danyliuk.service.impl;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mykola.danyliuk.service.Currency;
import mykola.danyliuk.service.ExchangeRateRecord;
import mykola.danyliuk.service.RateApiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ERHApiService implements RateApiService {

    private static final String API_TEMPLATE = "http://api.exchangerate.host/%s?access_key=%s";

    private final RestTemplate restTemplate;
    private final String liveURL;
    private final String listURL;
    private final boolean enabled;

    public ERHApiService(RestTemplate restTemplate, @Value("${erh.access.key}") String accessKey, @Value("${erh.enabled}") boolean enabled) {
        this.restTemplate = restTemplate;
        this.liveURL = String.format(API_TEMPLATE, "live", accessKey);
        this.listURL = String.format(API_TEMPLATE, "list", accessKey);
        this.enabled = enabled;
    }

    @Override
    public Optional<ExchangeRateRecord> fetchLatestExchangeRates() {
        if (enabled) {
            try {
                LiveDTO response = restTemplate.getForObject(liveURL, LiveDTO.class);
                if (response != null && Boolean.TRUE.equals(response.getSuccess())) {
                    return Optional.of(new ExchangeRateRecord(response.getTimestamp(), extractUsdRates(response.getQuotes())));
                }
            } catch (Exception e) {
                log.error("Error fetching live rates: {}", e.getMessage());
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Currency> fetchCurrencies() {
        if (enabled) {
            try {
                ListDTO response = restTemplate.getForObject(listURL, ListDTO.class);
                if (response != null && Boolean.TRUE.equals(response.getSuccess())) {
                    return response.getCurrencies().entrySet().stream()
                        .map(entry -> new Currency(entry.getKey(), entry.getValue())).toList();
                }
            } catch (Exception e) {
                log.error("Error fetching currencies: {}", e.getMessage());
            }
        }
        return Collections.emptyList();
    }

    private Map<String, BigDecimal> extractUsdRates(Map<String, Object> quotes) {
        return quotes.entrySet().stream()
            .collect(Collectors.toMap(
                entry -> entry.getKey().substring(3),
                entry -> new BigDecimal(String.valueOf(entry.getValue()))
            ));
    }

    @Data
    public static class LiveDTO {
        private Boolean success;
        private long timestamp;
        private Map<String, Object> quotes;
    }

    @Data
    public static class ListDTO {
        private Boolean success;
        private Map<String, String> currencies;
    }

}