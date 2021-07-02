package com.aibel.mel.mel2aas.spring.storage;

import org.springframework.http.MediaType;

public enum FileType {
    AASX("aasx", MediaType.APPLICATION_OCTET_STREAM),
    BOTTR("bottr", MediaType.TEXT_PLAIN),
    CSV("csv", new MediaType("text", "csv")),
    RDF("ttl", "rdf", new MediaType("text", "turtle")),
    ;

    private final String suffix;
    private final String directory;
    private final MediaType mediaType;

    FileType(String suffix, String directory, MediaType mediaType) {
        this.suffix = suffix;
        this.directory = directory;
        this.mediaType = mediaType;
    }

    FileType(String suffixAndDirectory, MediaType mediaType) {
        this(suffixAndDirectory, suffixAndDirectory, mediaType);
    }

    public String getSuffix() {
        return suffix;
    }

    public String getDirectory() {
        return directory;
    }

    public MediaType getMediaType() { return mediaType; }

}
