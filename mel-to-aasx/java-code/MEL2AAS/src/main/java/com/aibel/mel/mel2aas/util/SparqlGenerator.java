package com.aibel.mel.mel2aas.util;

import com.aibel.mel.mel2aas.meltoc.TocEntry;
import com.aibel.mel.mel2aas.ottr.bottr.InstanceMap;

import java.util.Map;

public class SparqlGenerator {

    private final Map<String, String> prefixMap;
    private final InstanceMap instanceMap;

    public SparqlGenerator(Map<String, String> prefixMap, InstanceMap instanceMap) {
        this.prefixMap = prefixMap;
        this.instanceMap = instanceMap;
    }

    public String sparqlSyntax() {
        StringBuffer sb = new StringBuffer();
        for (String prefix : prefixMap.keySet()) {
            sb.append("PREFIX ").append(prefix).append(": <").append(prefixMap.get(prefix)).append(">\n");
        }
        sb.append("\n");
        sb.append("SELECT");
        for (TocEntry tocEntry : instanceMap.getTocList()) {
            sb.append(" ").append(tocEntry.getOttrTplVar());
        }
        sb.append("\n");
        sb.append("WHERE {\n");
        sb.append("\t").append("[] ottr:of <").append(instanceMap.getTemplateUri().getUri()).append("> ;\n");
        int count = 0;
        for (TocEntry tocEntry : instanceMap.getTocList()) {
            sb.append("\t   ");
            sb.append("ottr:values");
            sb.append(getRdfRest(count));
            sb.append("rdf:first ");
            sb.append(tocEntry.getOttrTplVar());
            sb.append(" ;\n");
            count++;
        }
        sb.append("\t.\n");
        sb.append("}");
        return sb.toString();
    }

    private String getRdfRest(int count) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<count; i++) {
            sb.append("/rdf:rest");
        }
        sb.append("/");
        return sb.toString();
    }
}
