package com.cit.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Rate {
    private Long timestamp;
    private BigDecimal value;

}
