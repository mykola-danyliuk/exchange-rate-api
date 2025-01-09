package mykola.danyliuk.service.impl;

import lombok.AllArgsConstructor;
import mykola.danyliuk.model.CurrencyModel;
import mykola.danyliuk.model.RateModel;
import mykola.danyliuk.repository.CurrencyRepository;
import mykola.danyliuk.repository.RateRepository;
import mykola.danyliuk.service.Currency;
import mykola.danyliuk.service.DatabaseService;
import mykola.danyliuk.service.ExchangeRateRecord;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DatabaseServiceImpl implements DatabaseService {

    private final RateRepository rateRepository;
    private final CurrencyRepository currencyRepository;

    @Override
    @Async
    @Transactional
    public void saveExchangeRates(ExchangeRateRecord record) {
        List<RateModel> rateModels = record.usdRates().entrySet().stream()
            .map(entry -> new RateModel(entry.getKey(), entry.getValue(), record.timestamp()))
            .toList();
        rateRepository.deleteAll();
        rateRepository.saveAll(rateModels);
    }

    @Override
    public Optional<ExchangeRateRecord> fetch() {
        List<RateModel> rates = rateRepository.findAll();
        if (rates.isEmpty()) {
            return Optional.empty();
        }
        long timestamp = rates.getFirst().getTimestamp();
        var usdRates = rates.stream().collect(Collectors.toMap(RateModel::getTarget, RateModel::getRate));
        return Optional.of(new ExchangeRateRecord(timestamp, usdRates));
    }

    @Override
    @Transactional
    public void saveCurrencies(Collection<Currency> currencies) {
        currencyRepository.deleteAll();
        currencyRepository.saveAll(currencies.stream().map(record -> new CurrencyModel(record.code(), record.name())).toList());
    }

    @Override
    public Collection<Currency> fetchCurrencies() {
        return currencyRepository.findAll().stream().map(model -> new Currency(model.getCode(), model.getName())).toList();
    }

}
