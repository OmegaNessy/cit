package com.cit.service;

import com.cit.entity.Rate;

import java.util.Comparator;
import java.util.List;

public class OldestSearchStrategy implements SearchStrategy{
    @Override
    public Rate search(List<Rate> rates) {
        return rates.stream().min(Comparator.comparing(Rate::getTimestamp)).orElseThrow();
    }
}
