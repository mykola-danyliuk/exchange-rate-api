package mykola.danyliuk.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    public static ExchangeRateRecord mockExchangeRateRecord() {
        Map<String, BigDecimal> map = new HashMap<>();
        map.put("EUR", BigDecimal.valueOf(0.85));
        map.put("GBP", BigDecimal.valueOf(0.75));
        return new ExchangeRateRecord(123456789L, map);
    }

}
