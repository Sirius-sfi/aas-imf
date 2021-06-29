package com.aibel.mel.mel2aas.spring.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface TempStorageService {

    FileHandle createFileAndCopyContent(MultipartFile multipartFile, FileType fileType) throws IOException, TempStorageServiceException;

    FileHandle createFileWithSameUuid(FileHandle fileHandle, FileType fileType) throws IOException, TempStorageServiceException;

    void remove(FileHandle fileHandle) throws TempStorageServiceException;

}
