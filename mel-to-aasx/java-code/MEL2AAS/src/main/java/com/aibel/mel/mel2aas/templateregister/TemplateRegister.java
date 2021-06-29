package com.aibel.mel.mel2aas.templateregister;

import com.aibel.mel.mel2aas.util.URI;
import com.aibel.mel.mel2aas.util.Util;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TemplateRegister implements Iterable<Template> {

    private static final Logger log = LoggerFactory.getLogger(TemplateRegister.class);

    public static final String SHEET_NAME_ARGUMENTS = "Arguments";

    public static final int ROW_INDEX_HEADER = 1;

    private final File registerFile;
    private final Workbook registerWorkbook;
    private final Sheet argumentsSheet;

    private final Map<String, String> prefixMap;
    private final Map<String, Template> templateMap = new HashMap<String, Template>();

    public TemplateRegister(File registerFile, Map<String, String> prefixMap) throws IOException {
        this.registerFile = registerFile;
        this.prefixMap = prefixMap;
        this.registerWorkbook = WorkbookFactory.create(registerFile);
        this.argumentsSheet = registerWorkbook.getSheet(SHEET_NAME_ARGUMENTS);
        if (argumentsSheet == null) {
            throw new IllegalArgumentException("Missing required sheet '" + SHEET_NAME_ARGUMENTS + "' from TemplateRegister file: " + registerFile.getPath());
        }
        parse();
    }

    private void parse() {
        verifyArgHeader();
        parseArgs();
        for (Template template : templateMap.values()) {
            template.validateArguments();
        }
    }

    private void verifyArgHeader() {
        Row headerRow = argumentsSheet.getRow(ROW_INDEX_HEADER);
        for (TplRegCol col : TplRegCol.values()) {
            Cell cell = headerRow.getCell(col.getColumnIndex());
            if (cell == null) {
                throw new IllegalArgumentException("Missing column header '" + col.getColumnName() + "' in cell " + Util.getCellRef(headerRow, col.getColumnIndex()) + " in TemplateRegister: " + registerFile.getPath());
            }
            String cellValue = cell.getStringCellValue();
            if (!cellValue.equals(col.getColumnName())) {
                throw new IllegalArgumentException("Expected column header '" + col.getColumnName() + "' in cell " + Util.getCellRef(headerRow, col.getColumnIndex()) + ", but got '" + cellValue + "' in TemplateRegister: " + registerFile.getPath());
            }
        }
    }

    private void parseArgs() {
        for (int rowIdx=ROW_INDEX_HEADER+1; rowIdx<argumentsSheet.getLastRowNum()+1; rowIdx++) {
            Row row = argumentsSheet.getRow(rowIdx);
            URI instanceMapUri = (URI) TplRegCol.INSTANCE_MAP_URI.getValue(row);
            Template template = getTemplate(instanceMapUri);
            if (template == null) {
                URI templateUri = (URI) TplRegCol.TEMPLATE_URI.getValue(row);
                boolean isSelectDistinct = (Boolean) TplRegCol.SELECT_DISTINCT.getValue(row);
                template = new Template(this, instanceMapUri, templateUri, isSelectDistinct);
                templateMap.put(instanceMapUri.getUri(), template);
            }
            TemplateArgument argument = null;
            try {
                argument = new TemplateArgument(template, row);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Error parsing argument from file '" + registerFile.getPath() + "', sheet '" + SHEET_NAME_ARGUMENTS + "', row " + row.getRowNum() + ": " + e.getMessage());
            }
            template.addArgument(argument);
        }
    }

    public Template getTemplate(URI instanceMapUri) {
        return templateMap.get(instanceMapUri.getUri());
    }

    public Map<String, String> getPrefixMap() {
        return prefixMap;
    }

    public Iterator<Template> iterator() {
        return templateMap.values().iterator();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("{");
        sb.append("registerFile='").append(registerFile.getPath()).append("'");
        sb.append(";");
        sb.append("templates=[");
        boolean isFirst = true;
        for (Template template : this) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append(",");
            }
            sb.append(template.toString());
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

}
