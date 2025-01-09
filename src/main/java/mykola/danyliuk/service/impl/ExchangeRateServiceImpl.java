package mykola.danyliuk.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mykola.danyliuk.service.DatabaseService;
import mykola.danyliuk.service.ExchangeRateRecord;
import mykola.danyliuk.service.ExchangeRateService;
import mykola.danyliuk.service.RateApiService;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final RateApiService apiService;
    private final DatabaseService databaseService;
    private final CacheManager cacheManager;

    private Map<String, Map<String, BigDecimal>> exchangeRatesMap = new HashMap<>();
    @Getter private long lastUpdated;
    @Getter private boolean loaded = false;

    @PostConstruct
    public void init() {
        updateExchangeRates();
    }

    public BigDecimal getRate(String base, String target) {
        validateCurrency(base);
        validateCurrency(target);
        return exchangeRatesMap.get(base).get(target);
    }

    public Map<String, BigDecimal> getAllRates(String base) {
        validateCurrency(base);
        return exchangeRatesMap.get(base);
    }

    public BigDecimal convertValue(String base, String target, double value) {
        return getRate(base, target).multiply(BigDecimal.valueOf(value));
    }

    public Map<String, BigDecimal> convertValueToList(String base, double value) {
        Map<String, BigDecimal> rates = getAllRates(base);
        BigDecimal valueBD = BigDecimal.valueOf(value);
        return rates.entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().multiply(valueBD)));
    }

    @Override
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void updateExchangeRates() {
        apiService.fetchLatestExchangeRates().ifPresentOrElse(
            this::processApiResponse,
            this::fallbackToDatabase
        );
    }

    private void processApiResponse(ExchangeRateRecord record) {
        updateLocalCache(record);
        databaseService.saveExchangeRates(record);
        log.info("Exchange rates updated successfully. Timestamp: {}, Delay: {} seconds", record.timestamp(), delayInSeconds());
    }

    private void fallbackToDatabase() {
        log.warn("Failed to fetch exchange rates from the API. Falling back to the database");
        databaseService.fetch().ifPresentOrElse(
            record -> {
                updateLocalCache(record);
                log.warn("Using database exchange rates. Timestamp: {}, Delay: {} seconds", record.timestamp(), delayInSeconds());
            },
            () -> log.error("No exchange rates available in the database")
        );
    }

    private void updateLocalCache(ExchangeRateRecord record) {
        this.exchangeRatesMap = record.allRates();
        this.lastUpdated = record.timestamp();
        this.loaded = true;

        clearCache("exchangeRates");
        clearCache("allRates");
    }

    private void clearCache(String cacheName) {
        Objects.requireNonNull(cacheManager.getCache(cacheName), "Cache not found: " + cacheName).clear();
    }

    private void validateCurrency(String currency) {
        if (!exchangeRatesMap.containsKey(currency)) {
            throw new IllegalArgumentException("Currency not found: " + currency + ". List of supported currencies could be found on /currencies endpoint");
        }
    }

    public long delayInSeconds() {
        return System.currentTimeMillis() / 1000 - lastUpdated;
    }
}