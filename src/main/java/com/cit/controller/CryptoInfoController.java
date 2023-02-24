package com.cit.controller;

import com.cit.entity.NormalizedDto;
import com.cit.entity.Rate;
import com.cit.service.CryptoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
public class CryptoInfoController {
    private CryptoService cryptoService;
    private Logger logger = LoggerFactory.getLogger(CryptoInfoController.class);

    @Autowired
    public CryptoInfoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping(value = "/crypto")
    public ResponseEntity<Map<String, Rate>> getCryptoCurrencyInfoByParameters(@RequestParam List<String> searchType, @RequestParam String cryptoName) {
        logger.info(String.format("Getting the values due to %s parameters for %s crypto",searchType,cryptoName));
        return new ResponseEntity<>(cryptoService.  getCryptoByParameters(searchType, cryptoName), HttpStatus.OK);
    }

    @GetMapping(value = "/crypto/list")
    public ResponseEntity<List<NormalizedDto>> getAllCryptosDesc() {
        logger.info("return a descending sorted list of all the cryptos, comparing the normalized range");
        return new ResponseEntity<>(cryptoService.getNormalizedValuesDesc(), HttpStatus.OK);
    }

    @GetMapping(value = "/crypto/normalized")
    public ResponseEntity<Map<String, BigDecimal>> getHighestNormalizedCrypto(@RequestParam String day) {
        logger.info(String.format("return a descending sorted list of all the cryptos, comparing the normalized range by %s date",day));
        return new ResponseEntity<>(cryptoService.getHighestNormalizedCrypto(day), HttpStatus.OK);
    }

}
