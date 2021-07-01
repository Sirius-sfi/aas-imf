package com.aibel.mel.mel2aas.spring.storage;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

public class TempStorageServiceImpl implements TempStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(TempStorageServiceImpl.class);

    private final File tmpDirectory;

    private final Map<FileType, File> directoryMap = new HashMap<>();

    private final Map<UUID, List<FileHandle>> fileHandleMap = new HashMap<>();

    public TempStorageServiceImpl(String tmpDirectory, boolean purgeFiles) throws TempStorageConfigException {
        this.tmpDirectory = new File(tmpDirectory);
        checkDirectory(this.tmpDirectory);
        if (purgeFiles) {
            LOG.debug("Purging files from: " + tmpDirectory);
            recursivelyDelete(this.tmpDirectory);
        }
        for (FileType fileType : FileType.values()) {
            directoryMap.put(fileType, getSubDirectory(fileType.getDirectory()));
            LOG.debug("Created directory: " + directoryMap.get(fileType).getPath());
        }
    }

    private static void checkDirectory(File directory) throws TempStorageConfigException {
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new TempStorageConfigException("Cannot create directory: " + directory.getPath());
            }
        } else {
            if (!directory.isDirectory()) {
                throw new TempStorageConfigException("Path exists but is not a directory: " + directory.getPath());
            }
            if (!directory.canWrite()) {
                throw new TempStorageConfigException("No write permissions to directory: " + directory.getPath());
            }
        }
    }

    private static boolean recursivelyDelete(File directory) {
        File[] allContents = directory.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                recursivelyDelete(file);
            }
        }
        return directory.delete();
    }

    private void addToMap(FileHandle fileHandle) throws TempStorageServiceException {
        List<FileHandle> fileHandleList = fileHandleMap.get(fileHandle.getUuid());
        if (fileHandleList == null) {
            fileHandleList = new LinkedList<>();
            fileHandleMap.put(fileHandle.getUuid(), fileHandleList);
        }
        if (fileHandleList.contains(fileHandle)) {
            throw new TempStorageServiceException("Already contains file: " + fileHandle.getFile().getPath());
        }
        fileHandleList.add(fileHandle);
    }

    private List<FileHandle> removeFromMap(UUID uuid) {
        return fileHandleMap.remove(uuid);
    }

    private boolean contains(FileHandle fileHandle) {
        return fileHandleMap.containsKey(fileHandle.getUuid()) && fileHandleMap.get(fileHandle.getUuid()).contains(fileHandle);
    }

    private File getSubDirectory(String subDirName) throws TempStorageConfigException {
        String subDirPath = this.tmpDirectory.getPath();
        if (!subDirPath.endsWith(File.separator)) {
            subDirPath += File.separator;
        }
        subDirPath += subDirName;
        File subDir = new File(subDirPath);
        checkDirectory(subDir);
        return subDir;
    }

    private File getFile(FileType fileType, UUID uuid) {
        File directory = directoryMap.get(fileType);
        String fileName = directory.getPath();
        if (!fileName.endsWith(File.separator)) {
            fileName += File.separator;
        }
        fileName += uuid.toString() + "." + fileType.getSuffix();
        return new File(fileName);
    }

    @Override
    public FileHandle createFileAndCopyContent(MultipartFile multipartFile, FileType fileType) throws IOException, TempStorageServiceException {
        UUID uuid = UUID.randomUUID();
        File file = getFile(fileType, uuid);

        OutputStream out  = new FileOutputStream(file);
        IOUtils.copy(multipartFile.getInputStream(), out);
        out.close();

        FileHandle fileHandle = new FileHandle(fileType, multipartFile, uuid, file);
        addToMap(fileHandle);
        return fileHandle;
    }

    @Override
    public FileHandle createFileWithSameUuid(FileHandle fileHandle, FileType fileType) throws IOException, TempStorageServiceException {
        File file = getFile(fileType, fileHandle.getUuid());
        file.createNewFile();

        FileHandle newFileHandle = new FileHandle(fileType, fileHandle, file);
        addToMap(newFileHandle);
        return newFileHandle;
    }

    @Override
    public void remove(FileHandle fileHandle) throws TempStorageServiceException {
        List<FileHandle> list = removeFromMap(fileHandle.getUuid());
        if (list == null) {
            throw new TempStorageServiceException(FileHandle.class.getSimpleName() + " not part of file store: " + fileHandle);
        }
        for (FileHandle fh : list) {
            if (!fh.getFile().delete()) {
                throw new TempStorageServiceException("Error deleting file: " + fileHandle.getFile().getPath());
            }
        }
    }

}
