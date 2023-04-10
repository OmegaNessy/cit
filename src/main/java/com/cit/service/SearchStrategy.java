package com.cit.service;

import com.cit.entity.Rate;

import java.util.List;

public interface SearchStrategy {
    Rate search(List<Rate> rates);
}
