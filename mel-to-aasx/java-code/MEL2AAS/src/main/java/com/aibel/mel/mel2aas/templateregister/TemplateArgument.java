package com.aibel.mel.mel2aas.templateregister;

import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TemplateArgument {

    private static final Logger log = LoggerFactory.getLogger(TemplateArgument.class);

    private final Template template;

    private final int argumentIndex;
    private final String ottrType;
    private final String variableName;
    private final boolean isNonBlank;
    private final boolean isOptional;

    private final String columnName;
    private final String columnAlias;
    private final String valueExpression;
    private final String namespace;
    private final String localNamePrefix;
    private final boolean trim;
    private final boolean spaceToUnderscore;
    private final String h2FilterCondition;
    private final String regexpExpr;
    private final String regexpReplacement;
    private final String regexpFlags;

    protected TemplateArgument(Template template, Row row) {
        this.template = template;

        this.argumentIndex = (Integer) TplRegCol.ARGUMENT_INDEX.getValue(row);
        this.ottrType = (String) TplRegCol.OTTR_TYPE.getValue(row);
        this.variableName = (String) TplRegCol.VARIABLE_NAME.getValue(row);
        this.isNonBlank = (Boolean) TplRegCol.NON_BLANK.getValue(row);
        this.isOptional = (Boolean) TplRegCol.OPTIONAL.getValue(row);

        this.columnName = (String) TplRegCol.COLUMN_NAME.getValue(row);
        this.columnAlias = (String) TplRegCol.COLUMN_ALIAS.getValue(row);
        this.valueExpression = (String) TplRegCol.VALUE_EXPRESSION.getValue(row);
        this.namespace = (String) TplRegCol.NAMESPACE.getValue(row);
        this.localNamePrefix = (String) TplRegCol.LOCAL_NAME_PREFIX.getValue(row);
        this.trim = (Boolean) TplRegCol.TRIM.getValue(row);
        this.spaceToUnderscore = (Boolean) TplRegCol.SPACE_TO_UNDERSCORE.getValue(row);
        this.h2FilterCondition = (String) TplRegCol.H2_FILTER_CONDITION.getValue(row);
        this.regexpExpr = (String) TplRegCol.REGEXP_EXPR.getValue(row);
        this.regexpReplacement = (String) TplRegCol.REGEXP_REPLACEMENT.getValue(row);
        this.regexpFlags = (String) TplRegCol.REGEXP_FLAGS.getValue(row);
    }

    public Template getTemplate() {
        return template;
    }

    public int getArgumentIndex() {
        return argumentIndex;
    }

    public int getArgumentPosition() {
        return argumentIndex+1;
    }

    public String getOttrType() {
        return ottrType;
    }

    public String getVariableName() {
        return variableName;
    }

    public boolean isNonBlank() {
        return isNonBlank;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getColumnAlias() {
        return columnAlias;
    }

    public boolean hasColumnAlias() {
        return columnAlias != null;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public boolean hasValueExpression() {
        return valueExpression != null;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean hasNamespace() {
        return namespace != null;
    }

    public String getLocalNamePrefix() {
        return localNamePrefix;
    }

    public boolean hasLocalNamePrefix() {
        return localNamePrefix != null;
    }

    public boolean trim() {
        return trim;
    }

    public boolean spaceToUnderscore() {
        return spaceToUnderscore;
    }

    public boolean hasH2FilterCondition() {
        return h2FilterCondition != null;
    }

    public String getH2FilterCondition() {
        return h2FilterCondition;
    }

    public String getRegexpExpr() {
        return regexpExpr;
    }

    public String getRegexpReplacement() {
        return regexpReplacement;
    }

    public String getRegexpFlags() {
        return regexpFlags;
    }

    public boolean hasRegexpExpr() {
        return regexpExpr != null;
    }

    public boolean hasRegexpReplacement() {
        return regexpReplacement != null;
    }

    public boolean hasRegexpFlags() {
        return regexpFlags != null;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("{");
        sb.append("argumentIndex=").append(argumentIndex).append(",");
        sb.append("ottrType=").append(ottrType).append(",");
        sb.append("variableName=").append(variableName).append(",");
        sb.append("isNonBlank=").append(isNonBlank).append(",");
        sb.append("isOptional=").append(isOptional).append(",");
        sb.append("columnName=").append(columnName).append(",");
        sb.append("columnAlias=").append(columnAlias).append(",");
        sb.append("namespace=").append(namespace).append(",");
        sb.append("localNamePrefix=").append(localNamePrefix).append(",");
        sb.append("trim=").append(trim).append(",");
        sb.append("spaceToUnderscore=").append(spaceToUnderscore).append(",");
        sb.append("h2FilterCondition=").append(h2FilterCondition).append(",");
        sb.append("regexpExpr=").append(regexpExpr).append(",");
        sb.append("regexpReplacement=").append(regexpReplacement).append(",");
        sb.append("regexpFlags=").append(regexpFlags);
        sb.append("}");
        return sb.toString();
    }

}
