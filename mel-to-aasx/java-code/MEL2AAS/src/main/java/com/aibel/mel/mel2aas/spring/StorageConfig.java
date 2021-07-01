package com.aibel.mel.mel2aas.spring;

import com.aibel.mel.mel2aas.spring.storage.TempStorageConfigException;
import com.aibel.mel.mel2aas.spring.storage.TempStorageService;
import com.aibel.mel.mel2aas.spring.storage.TempStorageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    private static final Logger LOG = LoggerFactory.getLogger(StorageConfig.class);

    private final String temporaryDirectory;
    private final TempStorageService tempStorageService;

    @Autowired
    public StorageConfig(@Value("${melws.temporaryDirectory}") String temporaryDirectory, @Value("${melws.temporaryDirectoryPurgeFiles}") boolean purgeFiles) throws TempStorageConfigException {
        LOG.info("melws.temporaryDirectory=" + temporaryDirectory);
        LOG.info("melws.temporaryDirectoryPurgeFiles=" + purgeFiles);
        this.temporaryDirectory = temporaryDirectory;
        this.tempStorageService = new TempStorageServiceImpl(temporaryDirectory, purgeFiles);
    }

    @Bean
    public TempStorageService getTempStorageService() {
        return tempStorageService;
    }

}
