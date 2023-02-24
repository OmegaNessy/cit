package com.cit.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NormalizedDto {
    private String symbol;
    private BigDecimal value;
}
