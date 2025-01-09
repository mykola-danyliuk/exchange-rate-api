package mykola.danyliuk.service;

import java.util.List;
import java.util.Optional;

public interface RateApiService {
    Optional<ExchangeRateRecord> fetchLatestExchangeRates();
    List<Currency> fetchCurrencies();
}
