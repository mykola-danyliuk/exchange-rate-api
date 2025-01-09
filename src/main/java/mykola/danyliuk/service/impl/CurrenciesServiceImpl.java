package mykola.danyliuk.service.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mykola.danyliuk.service.CurrenciesService;
import mykola.danyliuk.service.Currency;
import mykola.danyliuk.service.DatabaseService;
import mykola.danyliuk.service.RateApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CurrenciesServiceImpl implements CurrenciesService {

    private static final Logger log = LoggerFactory.getLogger(CurrenciesServiceImpl.class);
    private final RateApiService apiService;
    private final DatabaseService databaseService;

    private Collection<Currency> currencies;
    @Getter private boolean loaded = false;

    @PostConstruct
    public void init() {
        updateCurrencies();
    }

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void updateCurrencies() {
        if (!loaded) {
            var apiList = apiService.fetchCurrencies();
            if (apiList.isEmpty()) {
                var databaseList = databaseService.fetchCurrencies();
                if (databaseList.isEmpty()) {
                    log.error("No currencies available in the database.");
                    return;
                }
                currencies = databaseList;
                loaded = true;
                log.info("Currencies loaded from the database.");
            } else {
                currencies = apiList;
                loaded = true;
                databaseService.saveCurrencies(currencies);
                log.info("Currencies loaded from API.");
            }
        }
    }

    @Override
    public Collection<Currency> getCurrencies() {
        return currencies;
    }
}
