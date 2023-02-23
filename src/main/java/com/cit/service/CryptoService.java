package com.cit.service;

import com.cit.controller.CryptoInfoController;
import com.cit.entity.CryptoCurrency;
import com.cit.entity.Currency;
import com.cit.entity.NormalizedDto;
import com.cit.entity.Rate;
import com.cit.repository.CryptoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CryptoService {
    private static final String OLDEST_KEY = "oldest";
    private static final String NEWEST_KEY = "newest";
    private static final String MAX_KEY = "max";
    private static final String MIN_KEY = "min";

    private CryptoRepository cryptoRepository;
    private Logger logger = LoggerFactory.getLogger(CryptoInfoController.class);

    @Autowired
    public CryptoService(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    public Map<String, Map<String, Currency>> getAllCurrencyInfoByMonth() {
        logger.info("Getting Currency Info by months");
        List<Currency> currencies = mapToCurrency(cryptoRepository.findAll());
        Map<String, Map<String, Currency>> result = new HashMap<>();
        Map<String, Currency> minCurrency = new HashMap<>();
        Map<String, Currency> maxCurrency = new HashMap<>();
        Map<String, Currency> oldestCurrency = new HashMap<>();
        Map<String, Currency> newestCurrency = new HashMap<>();

        for (Currency currency : currencies) {
            Map<String, Currency> currenciesByMonth = currency.getRatesByMonth();
            logger.info(String.format("Info for %s crypto",currency));
            currenciesByMonth.forEach((k, v) -> {
                Currency temp = new Currency();
                temp.setSymbol(v.getSymbol());
                temp.getRates().add(v.getRates().stream().min(Comparator.comparing(Rate::getValue)).orElseThrow());
                minCurrency.put(k, temp);
            });
            currenciesByMonth.forEach((k, v) -> {
                Currency temp = new Currency();
                temp.setSymbol(v.getSymbol());
                temp.getRates().add(v.getRates().stream().min(Comparator.comparing(Rate::getValue)).orElseThrow());
                maxCurrency.put(k, temp);
            });
            currenciesByMonth.forEach((k, v) -> {
                Currency temp = new Currency();
                temp.setSymbol(v.getSymbol());
                temp.getRates().add(v.getRates().stream().min(Comparator.comparing(Rate::getValue)).orElseThrow());
                oldestCurrency.put(k, temp);
            });
            currenciesByMonth.forEach((k, v) -> {
                Currency temp = new Currency();
                temp.setSymbol(v.getSymbol());
                temp.getRates().add(v.getRates().stream().min(Comparator.comparing(Rate::getValue)).orElseThrow());
                newestCurrency.put(k, temp);
            });
        }
        result.put(MIN_KEY, minCurrency);
        result.put(MAX_KEY, maxCurrency);
        result.put(OLDEST_KEY, oldestCurrency);
        result.put(NEWEST_KEY, newestCurrency);
        logger.info(String.format("Currency info : %s",result));
        return result;
    }

    public List<NormalizedDto> getNormalizedValuesDesc() {
        List<Currency> currencyList = mapToCurrency(cryptoRepository.findAll());
        logger.info("Currency retrieved: ");
        List<NormalizedDto> dtoList = new ArrayList<>();
        for (Currency currency: currencyList){
            logger.info(currency.toString());
            NormalizedDto dto = new NormalizedDto();
            BigDecimal normalizedValue = calculateNormalizedRate(calculateMaxRate(currency.getRates()), calculateMinRate(currency.getRates()));
            dto.setSymbol(currency.getSymbol());
            dto.setValue(normalizedValue);
            dtoList.add(dto);
        }
        dtoList.sort(Comparator.comparing(NormalizedDto::getValue).reversed());
        logger.info(String.format("Normalized Values : %s",dtoList));
        return dtoList;
    }

    public Map<String, Rate> getCryptoByParameters(List<String> searchType, String cryptoName) {
        List<Currency> currencyList = mapToCurrency(cryptoRepository.findBySymbol(cryptoName));
        logger.info("Currency retrieved: ");
        Map<String, Rate> result = new HashMap<>();
//        for (AbstractCal parameter:searchType) { //TODO: finish this idea
//            parameter.calculate(currencies);
//        }
//        Map<String, CryptoCurrency> result = calculateWithAttributes(attributes);
        for (Currency currency : currencyList) {
            logger.info(currency.toString());
            if (currency.getSymbol().equals(cryptoName)) {
                if (searchType.contains(MIN_KEY)) {
                    result.put(MIN_KEY, calculateMinRate(currency.getRates()));
                }
                if (searchType.contains(MAX_KEY)) {
                    result.put(MAX_KEY, calculateMaxRate(currency.getRates()));
                }
                if (searchType.contains(OLDEST_KEY)) {
                    result.put(OLDEST_KEY, calculalateOldestRate(currency.getRates()));
                }
                if (searchType.contains(NEWEST_KEY)) {
                    result.put(NEWEST_KEY, calculateNewestRate(currency.getRates()));
                }
            }
        }
        logger.info(String.format("Currency found with parameters: %s",result));
        return result;
    }

    public Map<String, BigDecimal> getHighestNormalizedCrypto(String date) {
        List<Currency> list = mapToCurrency(cryptoRepository.findAll());
        logger.info("Currency retrieved: ");
        Rate maxDayRate;
        Rate minDayRate;
        Map<String, BigDecimal> normalizedMap = new HashMap<>();
        for (Currency currency:list){
            logger.info(currency.toString());
            maxDayRate = calculateMaxRate(getRatesForDay(currency,date));
            minDayRate = calculateMinRate(getRatesForDay(currency,date));
            logger.info(String.format("Rates for %s by %s date: max-%s, min-%s",currency.getSymbol(),date,maxDayRate,minDayRate));
            normalizedMap.put(currency.getSymbol(),calculateNormalizedRate(maxDayRate,minDayRate));
        }
        Map.Entry<String,BigDecimal> maxEntry = null;
        for (Map.Entry<String, BigDecimal> entry : normalizedMap.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
        logger.info(String.format("Normalized Max Value: %s",maxEntry));
        return Map.ofEntries(maxEntry);
    }

    private List<Currency> mapToCurrency(List<CryptoCurrency> cryptoCurrencies) {
        logger.info("Mapping Database data to app instances");
        cryptoCurrencies.sort(Comparator.comparing(CryptoCurrency::getSymbol));
        Map<String, List<CryptoCurrency>> mapped = cryptoCurrencies.stream().collect(Collectors.groupingBy(CryptoCurrency::getSymbol));
        List<Currency> currencies = new ArrayList<>();
        mapped.forEach((k, v) -> currencies.add(mapCurrency(k, v)));
        return currencies;
    }

    private Currency mapCurrency(String symbol, List<CryptoCurrency> list) {
        Currency currency = new Currency();
        currency.setSymbol(symbol);
        for (CryptoCurrency row : list) {
            Rate rate = new Rate();
            rate.setValue(row.getPrice());
            rate.setTimestamp(row.getTimestamp());
            currency.getRates().add(rate);
        }
        return currency;
    }

    public List<Rate> getRatesForDay(Currency currency,String date){
        LocalDate localdate = LocalDate.parse(date);
        long startDay = localdate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
        long endDay = ZonedDateTime.of(localdate.atTime(LocalTime.MAX), ZoneId.systemDefault()).toInstant().toEpochMilli();
        return currency.getRates().stream().filter(x -> x.getTimestamp() >= startDay && x.getTimestamp() <= endDay).toList();
    }

    public Rate calculateMaxRate(List<Rate> rates) {
        return rates.stream().max(Comparator.comparing(Rate::getValue)).orElseThrow();
    }

    public Rate calculateMinRate(List<Rate> rates) {
        return rates.stream().min(Comparator.comparing(Rate::getValue)).orElseThrow();
    }

    private Rate calculateNewestRate(List<Rate> rates) {
        return rates.stream().max(Comparator.comparing(Rate::getTimestamp)).orElseThrow();
    }

    private Rate calculalateOldestRate(List<Rate> rates) {
        return rates.stream().min(Comparator.comparing(Rate::getTimestamp)).orElseThrow();
    }

    private BigDecimal calculateNormalizedRate(Rate maxRate, Rate minRate) {
        BigDecimal min = minRate.getValue();
        BigDecimal max = maxRate.getValue();
        return max.subtract(min).divide(min, RoundingMode.HALF_EVEN);
    }
}