package com.aibel.mel.mel2aas.ottr.stottr;

import com.aibel.mel.mel2aas.ottr.NS;
import com.aibel.mel.mel2aas.ottr.bottr.BottrSpec;
import com.aibel.mel.mel2aas.ottr.bottr.InstanceMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OTTRTemplates {

    private final List<OTTRTemplate> ottrTemplates = new LinkedList<OTTRTemplate>();
    private final Map<String, String> prefixMap = new HashMap<String, String>();

    public OTTRTemplates(BottrSpec bottrSpec, Map<String, String> prefixMap) {
        this.prefixMap.putAll(prefixMap);
        this.prefixMap.putAll(NS.getPrefixMap());
        for (InstanceMap instanceMap : bottrSpec) {
            OTTRTemplate tpl = new OTTRTemplate(instanceMap, prefixMap);
            ottrTemplates.add(tpl);
        }
    }

    public String stottrSyntax() {
        StringBuffer sb = new StringBuffer();
        for (String prefix : prefixMap.keySet()) {
            sb.append("PREFIX ").append(prefix).append(": <").append(prefixMap.get(prefix)).append(">\n");
        }
        sb.append("\n");
        boolean isFirst = true;
        for (OTTRTemplate tpl : ottrTemplates) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append("\n\n");
            }
            sb.append(tpl.stottrSyntax());
        }
        return sb.toString();
    }
}
