package mykola.danyliuk.service;

import java.util.Collection;

public interface CurrenciesService {
    Collection<Currency> getCurrencies();
    boolean isLoaded();
}
