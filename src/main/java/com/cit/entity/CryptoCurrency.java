package com.cit.entity;

import com.opencsv.bean.CsvBindByName;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
public class CryptoCurrency {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @CsvBindByName
    private Long timestamp;
    @CsvBindByName
    private String symbol;
    @CsvBindByName
    @Column(precision = 82, scale = 4)
    private BigDecimal price;
}
