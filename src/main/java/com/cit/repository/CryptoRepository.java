package com.cit.repository;

import com.cit.entity.CryptoCurrency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface CryptoRepository extends JpaRepository<CryptoCurrency, Timestamp> {
    List<CryptoCurrency> findBySymbol(String symbol);
}
