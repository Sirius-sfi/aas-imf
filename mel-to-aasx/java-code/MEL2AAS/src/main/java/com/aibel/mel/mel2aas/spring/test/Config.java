package com.aibel.mel.mel2aas.spring.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = StorageService.class)
public class Config {

    private final StorageService storageService = new InMemoryStorageService();

    @Bean
    public StorageService getStorageService() {
        return storageService;
    }

}
