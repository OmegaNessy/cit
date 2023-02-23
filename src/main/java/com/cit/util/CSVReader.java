package com.cit.util;

import com.cit.entity.CryptoCurrency;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CSVReader implements Reader {
    private List<Path> filePaths;

    public CSVReader(List<Path> filePaths) {
        this.filePaths = filePaths;
    }

    @Override
    public List<CryptoCurrency> read() {
        List<CryptoCurrency> data = new ArrayList<>();
        for (Path filePath : filePaths) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                CsvToBean<CryptoCurrency> cb = new CsvToBeanBuilder<CryptoCurrency>(reader)
                        .withType(CryptoCurrency.class)
                        .withSkipLines(1)
                        .build();

                data.addAll(cb.parse());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
