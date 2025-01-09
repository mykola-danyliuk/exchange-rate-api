package mykola.danyliuk.service;

import java.util.Collection;
import java.util.Optional;

public interface DatabaseService {
    void saveExchangeRates(ExchangeRateRecord record);
    Optional<ExchangeRateRecord> fetch();
    void saveCurrencies(Collection<Currency> currencies);
    Collection<Currency> fetchCurrencies();
}
