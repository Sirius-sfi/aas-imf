package com.aibel.mel.mel2aas.propertymap;

import java.util.*;

public class MELPropertyMapping implements Iterable<String> {

    private final String[] values;

    private final Map<String, String> properties = new HashMap<String, String>();
    private final List<String> columnNames = new LinkedList<String>();

    protected MELPropertyMapping(String[] values) {
        this.values = values;
    }

    protected void addProperty(String columnName, String propertyUri) {
        properties.put(columnName, propertyUri);
        columnNames.add(columnName);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public String getPropertyUri(String columnName) {
        return properties.get(columnName);
    }

    public boolean hasColumnName(String columnName) {
        return columnNames.contains(columnName);
    }

    public String getColumnName(int index) {
        return columnNames.get(index);
    }

    public Iterator<String> iterator() {
        return columnNames.iterator();
    }

    public String getValue(ColHdr colHdr) {
        return values[colHdr.getColumnIndex()];
    }

    public String getActivityUri() {
        return getValue(ColHdr.CLASS_OF_ACTIVITY);
    }

    public String getFunctionalObjectUri() {
        return getValue(ColHdr.CLASS_OF_FUNCTIONAL_OBJECT);
    }

    public String getPhysicalObjectUri() {
        return getValue(ColHdr.CLASS_OF_INANIMATE_PHYSICAL_OBJECT);
    }

    public String getDescription() {
        return getValue(ColHdr.DESCRIPTION);
    }

    public String getOttrTemplateIri() {
        return getValue(ColHdr.OTTR_TEMPLATE_IRI);
    }

    public String getSubjectColumn() {
        return getValue(ColHdr.SUBJECT_COLUMN);
    }

    public int numberOfArgs() {
        return properties.size();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("[");
        boolean isFirst = true;
        for (ColHdr colHdr : ColHdr.values()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append(colHdr.getColumnName()).append("=").append(values[colHdr.getColumnIndex()]);
        }
        for (String columnName : properties.keySet()) {
            sb.append(",");
            sb.append(columnName).append("=>");
            sb.append(properties.get(columnName));
        }
        sb.append("]");
        return sb.toString();
    }

}
