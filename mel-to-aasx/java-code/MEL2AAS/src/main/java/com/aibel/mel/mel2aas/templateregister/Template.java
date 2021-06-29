package com.aibel.mel.mel2aas.templateregister;

import com.aibel.mel.mel2aas.util.URI;
import com.aibel.mel.mel2aas.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;

public class Template implements Iterable<TemplateArgument> {

    private static final Logger log = LoggerFactory.getLogger(Template.class);

    private final TemplateRegister templateRegister;
    private final URI instanceMapUri;
    private final URI templateUri;
    private final boolean isSelectDistinct;
    private final Map<Integer, TemplateArgument> argumentMap = new HashMap<Integer, TemplateArgument>();

    protected Template(TemplateRegister templateRegister, URI instanceMapUri, URI templateUri, boolean isSelectDistinct) {
        this.templateRegister = templateRegister;
        this.instanceMapUri = instanceMapUri;
        this.templateUri = templateUri;
        this.isSelectDistinct = isSelectDistinct;
    }

    protected void addArgument(TemplateArgument argument) {
        if (argumentMap.containsKey(argument.getArgumentIndex())) {
            throw new IllegalArgumentException("Duplicate declaration of argument index " + argument.getArgumentIndex() + " for Template: " + templateUri.getUri());
        }
        argumentMap.put(argument.getArgumentIndex(), argument);
    }

    protected void validateArguments() {
        List<Integer> argIndices = Arrays.asList(argumentMap.keySet().toArray(new Integer[0]));
        Collections.sort(argIndices);
        if (argIndices.get(0) != 0) {
            throw new IllegalArgumentException("Template arguments do not start at position 1 for Template: " + templateUri.getUri());
        }
        for (int i=0; i<argIndices.size()-1; i++) {
            if (argIndices.get(i) + 1 != argIndices.get(i+1)) {
                throw new IllegalArgumentException("Template is missing argument with " + TplRegCol.ARGUMENT_INDEX.getColumnName() + " = " + (argIndices.get(i) + 1) + ": " + templateUri.getUri());
            }
        }
    }

    public URI getInstanceMapUri() {
        return instanceMapUri;
    }

    public URI getTemplateUri() {
        return templateUri;
    }

    public boolean isSelectDistinct() {
        return isSelectDistinct;
    }

    public int getNoOfArguments() {
        return argumentMap.size();
    }

    public List<TemplateArgument> getArgumentList() {
        List<TemplateArgument> argList = new LinkedList<TemplateArgument>(argumentMap.values());
        Collections.sort(argList, new Comparator<TemplateArgument>() {
            public int compare(TemplateArgument o1, TemplateArgument o2) {
                return new Integer(o1.getArgumentIndex()).compareTo(o2.getArgumentIndex());
            }
        });
        return argList;
    }

    public Iterator<TemplateArgument> iterator() {
        return getArgumentList().iterator();
    }

    public String bottrSyntax(File melCsvFile) {
        StringBuffer sb = new StringBuffer();
        sb.append(instanceMapUri.getUri());
        sb.append(" a ottr:InstanceMap ;").append('\n');
        sb.append("   ottr:source [ a ottr:H2Source ] ;").append('\n');
        sb.append("   ottr:query \"\"\"").append('\n');
        sb.append("      SELECT ");
        if (isSelectDistinct) {
            sb.append("DISTINCT");
        }
        sb.append("\n");
        boolean isFirst = true;
        for (TemplateArgument argument : this) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",\n");
            }
            sb.append("\t\t\t");
            String colExpr = null;
            if (argument.hasValueExpression()) {
                colExpr = argument.getValueExpression().replace("#COLUMN_NAME", argument.getColumnName());
                sb.append(colExpr);
            } else {
                colExpr = argument.getColumnName();
                if (argument.trim()) {
                    colExpr = "TRIM(BOTH ' ' FROM " + colExpr + ")";
                }
                if (argument.spaceToUnderscore()) {
                    colExpr = "REPLACE(" + colExpr + ", ' ', '_')";
                }
                if (argument.hasRegexpExpr()) {
                    colExpr = "REGEXP_REPLACE(" + colExpr + ", '" + argument.getRegexpExpr() + "', ";
                    if (argument.hasRegexpReplacement()) {
                        colExpr += "'" + argument.getRegexpReplacement() + "'";
                    } else {
                        colExpr += "''";
                    }
                    if (argument.hasRegexpFlags()) {
                        colExpr += ", '" + argument.getRegexpFlags() + "'";
                    }
                    colExpr = colExpr + ")";
                }
                if (argument.hasNamespace()) {
                    if (argument.hasLocalNamePrefix()) {
                        colExpr = "'" + argument.getLocalNamePrefix() + "', " + colExpr;
                    }
                    colExpr = "CONCAT('" + argument.getNamespace() + "', " + colExpr + ")";
                }
                String nvlExpr = "NVL2(";
                if (argument.trim()) {
                    nvlExpr += "TRIM(BOTH ' ' FROM " + argument.getColumnName() + ")";
                } else {
                    nvlExpr += argument.getColumnName();
                }
                nvlExpr = nvlExpr + ", " + colExpr + ", null)";
                sb.append(nvlExpr);
            }
            if (argument.hasColumnAlias()) {
                sb.append(" AS ");
                sb.append(argument.getColumnAlias());
            }
        }
        sb.append("\n");
        sb.append("      FROM CSVREAD('").append(Util.fixPath(melCsvFile)).append("')");
        StringBuffer whereClause = new StringBuffer();
        isFirst = true;
        for (TemplateArgument argument : this) {
            if (argument.hasH2FilterCondition()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    whereClause.append(" AND \n");
                }
                String condition = argument.getH2FilterCondition();
                if (argument.trim()) {
                    condition = condition.replace("#COLUMN_NAME","TRIM(BOTH ' ' FROM " + argument.getColumnName() + ")");
                } else {
                    condition = condition.replace("#COLUMN_NAME", argument.getColumnName());
                }
                whereClause.append("\t\t\t").append(condition);
            }
        }
        if (whereClause.length() > 0) {
            sb.append("\n      WHERE \n");
            sb.append(whereClause.toString());
        }
        sb.append(";\n");
        sb.append("   \"\"\" ;").append('\n');
        sb.append("   ottr:template <").append(templateUri.getUri()).append("> ;").append('\n');
        sb.append("   ottr:argumentMaps (").append('\n');
        for (TemplateArgument argument : this) {
            sb.append("      [ ottr:type ").append(argument.getOttrType());
            if (argument.isOptional()) {
                sb.append(" ; ottr:nullValue ottr:none");
            }
            String colName = null;
            if (argument.hasColumnAlias()) {
                colName = argument.getColumnAlias();
            } else {
                colName = argument.getColumnName();
            }
            sb.append(" ] # ").append(colName).append('\n');
        }
        sb.append("   ) .");
        return sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("{");
        sb.append("templateUri='").append(templateUri.getUri()).append("';");
        sb.append("argumentMap=[");
        boolean isFirst = true;
        for (TemplateArgument argument : getArgumentList()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append(argument.getArgumentIndex());
            sb.append("=>");
            sb.append(argument.toString());
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

}
