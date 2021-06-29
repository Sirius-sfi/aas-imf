package com.aibel.mel.mel2aas.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrefixParser {

    private Map<String, String> prefixMap = new HashMap<String, String>();

    public PrefixParser(File prefixFile) throws IOException {
        parse(prefixFile);
    }

    private void parse(File prefixFile) throws IOException {
        Pattern pattern = Pattern.compile("\\s*(@prefix|PREFIX)\\s+([a-zA-Z][a-zA-Z0-9_-]*):\\s+<([^>]+)>");
        BufferedReader reader = new BufferedReader(new FileReader(prefixFile));
        String line = null;
        Matcher matcher = null;
        while ((line = reader.readLine()) != null) {
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                prefixMap.put(matcher.group(2), matcher.group(3));
            }
        }
        reader.close();
    }

    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }
}
