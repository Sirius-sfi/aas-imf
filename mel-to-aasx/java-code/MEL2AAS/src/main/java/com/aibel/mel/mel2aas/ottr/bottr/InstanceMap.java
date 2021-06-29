package com.aibel.mel.mel2aas.ottr.bottr;

import com.aibel.mel.mel2aas.meltoc.MELTOC;
import com.aibel.mel.mel2aas.meltoc.TocEntry;
import com.aibel.mel.mel2aas.propertymap.ColHdr;
import com.aibel.mel.mel2aas.propertymap.MELPropertyMapping;
import com.aibel.mel.mel2aas.util.URI;
import com.aibel.mel.mel2aas.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InstanceMap {

    private final BottrSpec bottrSpec;
    private final File melCsvFile;
    private final URI templateUri;

    private final String activityUri;
    private final String functionalTypeUri;
    private final String physicalTypeUri;
    private final MELPropertyMapping propertyMapping;

    private TocEntry subjectColumn = null;

    private final List<TocEntry> tocList = new LinkedList<TocEntry>();
    private final Map<TocEntry, String> keyTocMap = new HashMap<TocEntry, String>();

    public InstanceMap(BottrSpec bottrSpec, MELTOC meltoc, MELPropertyMapping mapping, File melCsvFile) {
        this.bottrSpec = bottrSpec;
        this.melCsvFile = melCsvFile;
        this.templateUri = new URI(mapping.getOttrTemplateIri());
        this.activityUri = mapping.getActivityUri();
        this.functionalTypeUri = mapping.getFunctionalObjectUri();
        this.physicalTypeUri = mapping.getPhysicalObjectUri();
        this.propertyMapping = mapping;
        parse(meltoc, mapping);
    }

    private void parse(MELTOC meltoc, MELPropertyMapping mapping) {
        subjectColumn = meltoc.getEntry(mapping.getSubjectColumn());
        if (subjectColumn == null) {
            throw new IllegalArgumentException("Missing mandatory value '" + ColHdr.SUBJECT_COLUMN.getColumnName() + "': " + mapping);
        }
        tocList.add(subjectColumn);
        for (TocEntry tocEntry : meltoc.getKeys()) {
            String uri = null;
            for (ColHdr colHdr : new ColHdr[] { ColHdr.CLASS_OF_ACTIVITY, ColHdr.CLASS_OF_FUNCTIONAL_OBJECT, ColHdr.CLASS_OF_INANIMATE_PHYSICAL_OBJECT }) {
                if (colHdr.matches(tocEntry)) {
                    uri = mapping.getValue(colHdr);
                    if (uri == null) {
                        throw new IllegalArgumentException("TocEntry '" + tocEntry.getColumnName() + "' is KEY in MELTOC, and columnName matches MEL Mapping column, but no value is provided in " + mapping);
                    } else {
                        break;
                    }
                }
            }
            if (uri == null) {
                throw new IllegalArgumentException("TocEntry '" + tocEntry.getColumnName() + "' is KEY in MELTOC, but is not mapped in " + mapping);
            }
            tocList.add(tocEntry);
            keyTocMap.put(tocEntry, uri);
        }
        for (String columnName : mapping.getColumnNames()) {
            if (meltoc.hasEntry(columnName)) {
                tocList.add(meltoc.getEntry(columnName));
            } else {
                throw new IllegalArgumentException("Column '" + columnName + "' does not occur in MELTOC but is mapped in " + mapping);
            }
        }
    }

    public List<TocEntry> getTocList() {
        return tocList;
    }

    public URI getTemplateUri() {
        return templateUri;
    }

    public MELPropertyMapping getPropertyMapping() {
        return propertyMapping;
    }

    public TocEntry getSubjectColumn() {
        return subjectColumn;
    }

    public String bottrSyntax() throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("[] a ottr:InstanceMap ;").append('\n');
        sb.append("   ottr:source [ a ottr:H2Source ] ;").append('\n');
        sb.append("   ottr:query \"\"\"").append('\n');
        sb.append("      SELECT ");
        boolean isFirst = true;
        for (TocEntry entry : tocList) {
            if (!entry.isIgnore()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    sb.append(", ");
                }
                if (entry.hasNamespace()) {
                    String columnName = entry.getColumnName();
                    if (entry.isSpaceToUnderscore()) {
                        columnName = "REPLACE(" + columnName + ", ' ', '_')";
                    }
                    sb.append("CONCAT(");
                    sb.append("'").append(entry.getNamespace()).append("'");
                    sb.append(",");
                    if (entry.hasLocalNamePrefix()) {
                        sb.append("'").append(entry.getLocalNamePrefix()).append("'");
                        sb.append(",");
                    }
                    sb.append(columnName);
                    sb.append(")");
                } else {
                    sb.append(entry.getColumnName());
                }
            }
        }
        sb.append("\n");
        sb.append("      FROM CSVREAD('").append(Util.fixPath(melCsvFile)).append("')").append('\n');
        sb.append("      WHERE ");
        isFirst = true;
        for (TocEntry tocEntry : keyTocMap.keySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(" AND ");
            }
            sb.append(tocEntry.getColumnName());
            sb.append(" = ");
            sb.append("'").append(keyTocMap.get(tocEntry)).append("'");
        }
        sb.append(";\n");
        sb.append("   \"\"\" ;").append('\n');
        sb.append("   ottr:template <").append(templateUri.getUri()).append("> ;").append('\n');
        sb.append("   ottr:argumentMaps (").append('\n');
        for (TocEntry entry : tocList) {
            if (!entry.isIgnore()) {
                sb.append("      [ ottr:type ").append(entry.getOttrType());
                if (entry.isOptional()) {
                    sb.append(" ; ottr:nullValue ottr:none");
                }
                sb.append(" ] # ").append(entry.getColumnName()).append('\n');
            }
        }
        sb.append("   ) .");
        return sb.toString();
    }
}
