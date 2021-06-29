package com.aibel.mel.mel2aas.meltoc;

public class TocEntry {

    protected final String columnName;
    protected final String ottrType;
    protected final String ottrTplVar;
    protected final String namespace;
    protected final String localNamePrefix;
    protected final boolean spaceToUnderscore;
    protected final boolean isKey;
    protected final boolean isOptional;

    protected TocEntry(String columnName, String ottrType, String ottrTplVar, String namespace, String localNamePrefix, boolean spaceToUnderscore, boolean isKey, boolean isOptional) {
        this.columnName = columnName;
        this.ottrType = ottrType;
        this.ottrTplVar = ottrTplVar;
        this.namespace = namespace;
        this.localNamePrefix = localNamePrefix;
        this.spaceToUnderscore = spaceToUnderscore;
        this.isKey = isKey;
        this.isOptional = isOptional;
    }

    protected TocEntry(String columnName, String ottrType, String ottrTplVar, String namespace, String localNamePrefix) {
        this(columnName, ottrType, ottrTplVar, namespace, localNamePrefix, false, false, false);
    }

    protected TocEntry(String columnName, String ottrType, String ottrTplVar, String namespace) {
        this(columnName, ottrType, ottrTplVar, namespace, null, false, false, false);
    }

    protected TocEntry(String columnName, String ottrType, String ottrTplVar) {
        this(columnName, ottrType, ottrTplVar, null, null, false, false, false);
    }

    public String getColumnName() {
        return columnName;
    }

    public String getOttrType() {
        return ottrType;
    }

    public String getOttrTplVar() { return ottrTplVar; }

    public String getNamespace() {
        return namespace;
    }

    public String getLocalNamePrefix() {
        return localNamePrefix;
    }

    public boolean isSpaceToUnderscore() {
        return spaceToUnderscore;
    }

    public boolean isKey() {
        return isKey;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("[");
        sb.append("columnName=" + columnName).append(",");
        sb.append("ottrType=" + ottrType).append(",");
        sb.append("ottrTplVar=" + ottrTplVar).append(",");
        sb.append("namespace=" + namespace).append(",");
        sb.append("localNamePrefix=" + localNamePrefix).append(",");
        sb.append("spaceToUnderscore=" + spaceToUnderscore).append(",");
        sb.append("isKey=" + isKey).append(",");
        sb.append("isOptional=" + isOptional);
        sb.append("]");
        return sb.toString();
    }

    public boolean isIgnore() {
        return ottrType.equals("IGNORE");
    }

    public boolean hasNamespace() {
        return namespace != null;
    }

    public boolean hasLocalNamePrefix() {
        return localNamePrefix != null;
    }

}
