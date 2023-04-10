package com.cit.entity;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

import javax.persistence.*;
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
