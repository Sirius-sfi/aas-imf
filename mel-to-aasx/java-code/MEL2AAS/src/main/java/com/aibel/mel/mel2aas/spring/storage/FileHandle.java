package com.aibel.mel.mel2aas.spring.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.UUID;

public class FileHandle {

    private static final Logger LOG = LoggerFactory.getLogger(FileHandle.class);

    private final FileType fileType;
    private final String originalFileName;
    private final UUID uuid;
    private final File file;

    protected FileHandle(FileType fileType, String originalFileName, UUID uuid, File file) {
        this.fileType = fileType;
        this.originalFileName = originalFileName;
        this.uuid = uuid;
        this.file = file;
    }

    public FileType getFileType() { return fileType; }

    public String getOriginalFileName() {
        return originalFileName;
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
        sb.append("originalFileName='").append(originalFileName).append("'").append(";");
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

    public Resource asResource() {
        LOG.debug("this=" + this.toString());
        return new FileSystemResource(file);
    }

}
