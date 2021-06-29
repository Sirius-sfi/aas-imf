package com.aibel.mel.mel2aas.spring.storage;

public enum FileType {
    AASX("aasx"),
    BOTTR("bottr"),
    CSV("csv"),
    RDF("ttl", "rdf"),
    ;

    private final String suffix;
    private final String directory;

    FileType(String suffix, String directory) {
        this.suffix = suffix;
        this.directory = directory;
    }

    FileType(String suffixAndDirectory) {
        this(suffixAndDirectory, suffixAndDirectory);
    }

    public String getSuffix() {
        return suffix;
    }

    public String getDirectory() {
        return directory;
    }

}
