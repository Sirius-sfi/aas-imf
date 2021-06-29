package com.aibel.mel.mel2aas.spring.test;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class InMemoryStorageService implements StorageService {

    private final Map<String, ByteArrayResource> map = new HashMap<>();

    @Override
    public void init() {}

    @Override
    public void store(MultipartFile file) throws IOException {
        map.put(file.getOriginalFilename(), new ByteArrayResource(file.getBytes()));
    }

    @Override
    public Stream<String> loadAll() {
        return map.keySet().stream();
    }

//    @Override
//    public Path load(String filename) {
//        return null;
//    }

    @Override
    public Resource loadAsResource(String filename) throws StorageFileNotFoundException {
        if (!map.containsKey(filename)) {
            throw new StorageFileNotFoundException(filename);
        }
        return map.get(filename);
    }

    @Override
    public void deleteAll() {
        map.clear();
    }
}
