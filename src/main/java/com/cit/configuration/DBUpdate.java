package com.cit.configuration;

import com.cit.repository.CryptoRepository;
import com.cit.util.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;

@Component
public class DBUpdate {
    private final CryptoRepository cryptoRepository;
    Logger logger = LoggerFactory.getLogger(DBUpdate.class);

    @Autowired
    public DBUpdate(CryptoRepository cryptoRepository) {
        this.cryptoRepository = cryptoRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        URL url = getClass().getClassLoader().getResource("static");
        if (url!=null){
            File[] dir = new File(url.getFile()).listFiles();
            logger.info(String.format("Found %d files with data",dir.length));
            cryptoRepository.saveAllAndFlush(new CSVReader(dir).read());
        }
        else{
            logger.error("No file data provided. Other methods not supported");
            throw new UnsupportedOperationException("Supported only file data");
        }
    }

}
