package com.cit.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class CryptoCurrency {
    @CsvBindByName
    private Timestamp timestamp;
    @CsvBindByName
    private String symbol;
    @CsvBindByName
    private BigDecimal price;
}
