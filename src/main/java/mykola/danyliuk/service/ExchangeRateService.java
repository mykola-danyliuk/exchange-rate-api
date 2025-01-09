package mykola.danyliuk.service;

import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface ExchangeRateService {
    BigDecimal getRate(String base, String target);
    Map<String, BigDecimal> getAllRates(String base);
    BigDecimal convertValue(String base, String target, double value);
    Map<String, BigDecimal> convertValueToList(String base, double value);

    long delayInSeconds();
    long getLastUpdated();
    boolean isLoaded();

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    void updateExchangeRates();
}
