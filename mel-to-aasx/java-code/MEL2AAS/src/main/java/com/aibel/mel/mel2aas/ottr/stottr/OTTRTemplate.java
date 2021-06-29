package com.aibel.mel.mel2aas.ottr.stottr;

import com.aibel.mel.mel2aas.ottr.bottr.InstanceMap;
import com.aibel.mel.mel2aas.meltoc.TocEntry;
import com.aibel.mel.mel2aas.propertymap.ColHdr;
import com.aibel.mel.mel2aas.util.URI;
import com.aibel.mel.mel2aas.util.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OTTRTemplate {

    private final InstanceMap instanceMap;
    private final URI templateUri;
    private final Map<String, String> prefixMap;
    private final List<TocEntry> tocEntryList;
    private final Map<String, TocEntry> colHdrToTocEntryMap = new HashMap<String, TocEntry>();

    public OTTRTemplate(InstanceMap map, Map<String, String> prefixMap) {
        this.instanceMap = map;
        this.templateUri = map.getTemplateUri();
        this.prefixMap = prefixMap;
        this.tocEntryList = map.getTocList();
        for (TocEntry entry : tocEntryList) {
            colHdrToTocEntryMap.put(entry.getColumnName(), entry);
        }
    }

    public String stottrSyntax() {
        StringBuffer sb = new StringBuffer();
        String prefix = Util.getPrefix(prefixMap, templateUri.getNamespace());
        if (prefix != null) {
            sb.append(prefix).append(":").append(templateUri.getLocalName());
        } else {
            sb.append("<").append(templateUri.getUri()).append(">");
        }
        sb.append(" [ ");
        boolean isFirst = true;
        for (TocEntry tocEntry : tocEntryList) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(" , ");
            }
            if (tocEntry.isOptional()) {
                sb.append("? ");
            }
            sb.append(tocEntry.getOttrType()).append(" ").append(tocEntry.getOttrTplVar());
        }
        sb.append(" ] :: {\n");
        String subjectVar = instanceMap.getSubjectColumn().getOttrTplVar();
        String typeColHeader = instanceMap.getPropertyMapping().getValue(ColHdr.TYPE_COLUMN);
        TocEntry typeTocEntry = colHdrToTocEntryMap.get(typeColHeader);
        if (typeTocEntry == null) {
            throw new IllegalArgumentException("No TocEntry for column header '" + typeColHeader + "' in: " + instanceMap);
        }
        String typeVar = typeTocEntry.getOttrTplVar();
        isFirst = true;
        if (typeColHeader != null) {
            sb.append("\t").append("o-rdf:Type(").append(subjectVar).append(", ").append(typeVar).append(")");
            isFirst = false;
        }
        for (String colName : instanceMap.getPropertyMapping().getColumnNames()) {
            TocEntry entry = colHdrToTocEntryMap.get(colName);
            if (entry == null) {
                throw new IllegalArgumentException("No TocEntry for column header '" + typeColHeader + "' in: " + instanceMap);
            }
            URI propertyUri = new URI(instanceMap.getPropertyMapping().getPropertyUri(colName));
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",\n");
            }
            sb.append("\t").append("pca-tpl:").append(propertyUri.getLocalName()).append("(").append(subjectVar).append(", ").append(entry.getOttrTplVar()).append(")");
        }
        sb.append("\n} .");
        return sb.toString();
    }

    public URI getTemplateUri() {
        return templateUri;
    }

    public InstanceMap getInstanceMap() {
        return instanceMap;
    }
}
