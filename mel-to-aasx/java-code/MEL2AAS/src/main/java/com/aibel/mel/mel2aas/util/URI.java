package com.aibel.mel.mel2aas.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URI {

    private static final Logger log = LoggerFactory.getLogger(URI.class);

    private final String uri;
    private final String namespace;
    private final String localName;

    public URI(String uri) {
        this.uri = uri;
        String[] parts = split(uri);
        this.namespace = parts[0];
        this.localName = parts[1];
    }

    private String[] split(String uri) {
        final String[] str = new String[3];
        int index = uri.lastIndexOf("#");
        str[2] = "#";
        if (index == -1) {
            index = uri.lastIndexOf("/");
            str[2] = "/";
        }
        str[0] = uri.substring(0, index+1);
        str[1] = uri.substring(index+1);
        return str;
    }

    public String getUri() {
        return uri;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getLocalName() {
        return localName;
    }

}
