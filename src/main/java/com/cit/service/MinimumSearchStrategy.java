package com.cit.service;

import com.cit.entity.Rate;

import java.util.Comparator;
import java.util.List;

public class MinimumSearchStrategy implements SearchStrategy{
    @Override
    public Rate search(List<Rate> rates) {
        return rates.stream().min(Comparator.comparing(Rate::getValue)).orElseThrow();
    }
}
