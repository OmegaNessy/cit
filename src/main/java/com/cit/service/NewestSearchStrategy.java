package com.cit.service;

import com.cit.entity.Rate;

import java.util.Comparator;
import java.util.List;

public class NewestSearchStrategy implements SearchStrategy{
    @Override
    public Rate search(List<Rate> rates) {
        return rates.stream().max(Comparator.comparing(Rate::getTimestamp)).orElseThrow();
    }
}
