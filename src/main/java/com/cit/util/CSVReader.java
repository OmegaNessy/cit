package com.cit.util;

import com.cit.configuration.DBUpdate;
import com.cit.entity.CryptoCurrency;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVReader implements Reader {
    private final List<Path> filePaths;
    Logger logger = LoggerFactory.getLogger(CSVReader.class);

    public CSVReader(File[] files) {
        this.filePaths = Arrays.stream(files).map(File::toPath).toList();
    }

    @Override
    public List<CryptoCurrency> read() {
        List<CryptoCurrency> data = new ArrayList<>();
        for (Path filePath : filePaths) {
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                logger.info(String.format("Parsing %s file",filePath.getFileName()));
                CsvToBean<CryptoCurrency> cb = new CsvToBeanBuilder(reader)
                        .withType(CryptoCurrency.class)
                        .withSeparator(',')
                        .build();
                List<CryptoCurrency> parsedObjects = cb.parse();
                logger.info(String.format("Parsed %d rows",parsedObjects.size()));
                data.addAll(parsedObjects);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return data;
    }
}
