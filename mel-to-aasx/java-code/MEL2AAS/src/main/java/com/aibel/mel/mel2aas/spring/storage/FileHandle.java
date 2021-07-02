package com.aibel.mel.mel2aas.spring.storage;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileHandle {

    private static final Logger LOG = LoggerFactory.getLogger(FileHandle.class);

    private final FileType fileType;
    private final String originalFileNameBase;
    private final UUID uuid;
    private final File file;

    private FileHandle(FileType fileType, String originalFileNameBase, UUID uuid, File file) {
        this.fileType = fileType;
        this.originalFileNameBase = originalFileNameBase;
        this.uuid = uuid;
        this.file = file;
    }

    protected FileHandle(FileType fileType, MultipartFile multipartFile, UUID uuid, File file) {
        this(fileType, stripSuffix(multipartFile.getOriginalFilename()), uuid, file);
    }

    protected FileHandle(FileType fileType, FileHandle fileHandle, File file) {
        this(fileType, fileHandle.getOriginalFileNameBase(), fileHandle.getUuid(), file);
    }

    private static String stripSuffix(String fileName) {
        int idx = fileName.lastIndexOf('.');
        if (idx == -1) {
            return fileName;
        } else {
            return fileName.substring(0, idx);
        }
    }

    public String getPublicFileName() {
        return originalFileNameBase + '.' + fileType.getSuffix();
    }

    public FileType getFileType() { return fileType; }

    public String getOriginalFileNameBase() {
        return originalFileNameBase;
    }

    public UUID getUuid() {
        return uuid;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("{");
        sb.append("originalFileNameBase='").append(originalFileNameBase).append("'").append(";");
        sb.append("uuid='").append(uuid.toString()).append("'").append(";");
        sb.append("file='").append(file.getPath()).append("'");
        sb.append("}");
        return sb.toString();
    }

    public String getPathSansSuffix() {
        String fullPath = file.getPath();
        int index = fullPath.lastIndexOf('.');
        return fullPath.substring(0, index);
    }

    public Resource asResource() throws IOException {
        LOG.debug("this=" + this.toString());
        return new ByteArrayResource(FileUtils.readFileToByteArray(file));
    }

    public MediaType getMediaType() {
        return fileType.getMediaType();
    }

}
