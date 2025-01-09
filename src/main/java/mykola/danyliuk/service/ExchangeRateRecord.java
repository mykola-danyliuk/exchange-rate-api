package mykola.danyliuk.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

public record ExchangeRateRecord(long timestamp, Map<String, BigDecimal> usdRates) {

    public Map<String, Map<String, BigDecimal>> allRates() {
        usdRates.put("USD", BigDecimal.ONE);
        Map<String, Map<String, BigDecimal>> rates = new HashMap<>();
        usdRates.forEach((from, rateFrom) -> {
            usdRates.forEach((to, rateTo) -> {
                rates.computeIfAbsent(from, k -> new HashMap<>())
                    .put(to, rateTo.divide(rateFrom, 12, RoundingMode.HALF_DOWN));
            });
        });
        return rates;
    }

}