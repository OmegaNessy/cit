package com.cit.entity;

import lombok.Data;
import lombok.ToString;

import java.text.DateFormatSymbols;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@ToString
public class Currency {
    private String symbol;
    private List<Rate> rates = new ArrayList<>();

    public Map<String, Currency> getRatesByMonth() {
        Map<String, Currency> result = new HashMap<>();
        DateFormatSymbols dfs = new DateFormatSymbols();
        Map<String, List<Rate>> ratesByMonths = rates.stream().collect(Collectors.groupingBy(x -> {
            LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(x.getTimestamp()), ZoneId.of("GMT"));
            return dfs.getMonths()[timestamp.getMonthValue() - 1];
        }));
        ratesByMonths.forEach((k, v) -> {
            Currency temp = new Currency();
            temp.setSymbol(symbol);
            temp.setRates(v);
            result.put(k, temp);
        });
        return result;
    }

    public List<Rate> getRatesForDay(Currency currency,String date){
        LocalDate localdate = LocalDate.parse(date);
        long startDay = localdate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long endDay = ZonedDateTime.of(localdate.atTime(LocalTime.MAX), ZoneId.systemDefault()).toInstant().toEpochMilli();
        return currency.getRates().stream().filter(x -> x.getTimestamp() >= startDay && x.getTimestamp() <= endDay).toList();
    }
}
