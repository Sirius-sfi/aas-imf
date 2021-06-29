package com.aibel.mel.mel2aas.ottr.bottr;

import com.aibel.mel.mel2aas.meltoc.MELTOC;
import com.aibel.mel.mel2aas.ottr.NS;
import com.aibel.mel.mel2aas.propertymap.MELPropertyMap;
import com.aibel.mel.mel2aas.propertymap.MELPropertyMapping;
import com.aibel.mel.mel2aas.templateregister.Template;
import com.aibel.mel.mel2aas.templateregister.TemplateRegister;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BottrSpec implements Iterable<InstanceMap> {

    private final Map<String, String> prefixMap = new HashMap<String, String>();
    private final List<InstanceMap> instanceMaps = new LinkedList<InstanceMap>();
    private final File melCsvFile;
    private final TemplateRegister tplReg;

    public BottrSpec(MELTOC meltoc, MELPropertyMap melPropertyMap, TemplateRegister tplReg, Map<String, String> prefixMap, File melCsvFile) {
        this.prefixMap.putAll(prefixMap);
        this.prefixMap.putAll(NS.getPrefixMap());
        this.melCsvFile = melCsvFile;
        this.tplReg = tplReg;
        parse(meltoc, melPropertyMap, melCsvFile);
    }

    private void parse(MELTOC meltoc, MELPropertyMap melPropertyMap, File csvFile) {
        for (MELPropertyMapping mapping : melPropertyMap) {
            InstanceMap instanceMap = new InstanceMap(this, meltoc, mapping, csvFile);
            instanceMaps.add(instanceMap);
        }
    }

    public void addPrefixes(Map<String, String> prefixes) {
        this.prefixMap.putAll(prefixes);
    }

    public void addPrefix(String prefix, String namespace) {
        this.prefixMap.put(prefix, namespace);
    }

    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }

    public String bottrSyntax() throws IOException {
        StringBuffer sb = new StringBuffer();
        for (String prefix : prefixMap.keySet()) {
            sb.append("PREFIX ").append(prefix).append(": <").append(prefixMap.get(prefix)).append(">\n");
        }
        for (InstanceMap instanceMap : instanceMaps) {
            sb.append("\n\n");
            sb.append(instanceMap.bottrSyntax());
        }
        for (Template template : tplReg) {
            sb.append("\n\n");
            sb.append(template.bottrSyntax(melCsvFile));
        }
        return sb.toString();
    }

    public Iterator<InstanceMap> iterator() {
        return instanceMaps.iterator();
    }
}
