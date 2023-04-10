package com.cit.service;

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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CryptoService {
    private static final String OLDEST_KEY = "oldest";
    private static final String NEWEST_KEY = "newest";
    private static final String MAX_KEY = "max";
    private static final String MIN_KEY = "min";

    private CryptoRepository cryptoRepository;
    private Logger logger = LoggerFactory.getLogger(CryptoService.class);

    private Map<String,SearchStrategy> searchStrategies = new HashMap<>();

    @Autowired
    public CryptoService(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
        searchStrategies.put(MIN_KEY, new MinimumSearchStrategy());
        searchStrategies.put(MAX_KEY, new MaximumSearchStrategy());
        searchStrategies.put(OLDEST_KEY, new OldestSearchStrategy());
        searchStrategies.put(NEWEST_KEY, new NewestSearchStrategy());
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
            logger.info("Info for {} crypto",currency);
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
        logger.info("Currency info : {}",result);
        return result;
    }

    public List<NormalizedDto> getNormalizedValuesDesc() {
        List<Currency> currencyList = mapToCurrency(cryptoRepository.findAll());
        logger.info("Currency retrieved: ");
        List<NormalizedDto> dtoList = new ArrayList<>();
        for (Currency currency: currencyList){
            NormalizedDto dto = new NormalizedDto();
            BigDecimal normalizedValue = calculateNormalizedRate(searchStrategies.get(MAX_KEY).search(currency.getRates()),
                                                                 searchStrategies.get(MIN_KEY).search(currency.getRates()));
            dto.setSymbol(currency.getSymbol());
            dto.setValue(normalizedValue);
            dtoList.add(dto);
        }
        dtoList.sort(Comparator.comparing(NormalizedDto::getValue).reversed());
        logger.info("Normalized Values : {}",dtoList);
        return dtoList;
    }

    public Map<String, Rate> getCryptoByParameters(List<String> searchType, String cryptoName) {
        List<Currency> currencyList = mapToCurrency(cryptoRepository.findBySymbol(cryptoName));
        logger.info("Currency retrieved: ");
        Map<String, Rate> result = new HashMap<>();
        for (Currency currency : currencyList) {
            if (currency.getSymbol().equals(cryptoName)) {
                for (String type : searchType) {
                    if (searchStrategies.containsKey(type)) {
                        result.put(type,searchStrategies.get(type).search(currency.getRates()));
                    }
                }
            }
        }
        logger.info("Currency found with parameters: {}", result);
        return result;
    }

    public Map<String, BigDecimal> getHighestNormalizedCrypto(String date) {
        List<Currency> list = mapToCurrency(cryptoRepository.findAll());
        logger.info("Currency retrieved: ");
        Rate maxDayRate;
        Rate minDayRate;
        Map<String, BigDecimal> normalizedMap = new HashMap<>();
        for (Currency currency:list){
            maxDayRate = searchStrategies.get(MAX_KEY).search(currency.getRatesForDay(currency,date));
            minDayRate = searchStrategies.get(MIN_KEY).search(currency.getRatesForDay(currency,date));
            logger.info("Rates for {} by {} date: max-{}, min-{}",currency.getSymbol(),date,maxDayRate,minDayRate);
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
        logger.info("Normalized Max Value: {}",maxEntry);
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

    private BigDecimal calculateNormalizedRate(Rate maxRate, Rate minRate) {
        BigDecimal min = minRate.getValue();
        BigDecimal max = maxRate.getValue();
        return max.subtract(min).divide(min, RoundingMode.HALF_EVEN);
    }
}