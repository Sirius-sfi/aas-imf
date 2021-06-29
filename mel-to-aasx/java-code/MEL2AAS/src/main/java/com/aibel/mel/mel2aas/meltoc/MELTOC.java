package com.aibel.mel.mel2aas.meltoc;

import com.aibel.mel.mel2aas.propertymap.ColHdr;
import com.aibel.mel.mel2aas.util.Util;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MELTOC implements Iterable<TocEntry> {

    public static final String TOC_SHEET_NAME = "MEL_TOC";

    public static final String HD_COLUMN_NAME         = "COLUMN_NAME";
    public static final String HD_OTTR_TYPE           = "OTTR_TYPE";
    public static final String HD_OTTR_TPL_VAR        = "OTTR_TPL_VAR";
    public static final String HD_NAMESPACE           = "NAMESPACE";
    public static final String HD_LOCAL_NAME_PREFIX   = "LOCAL_NAME_PREFIX";
    public static final String HD_SPACE_TO_UNDERSCORE = "SPACE_TO_UNDERSCORE";
    public static final String HD_IS_KEY              = "IS_KEY";
    public static final String HD_IS_OPTIONAL         = "IS_OPTIONAL";
    public static final int ID_COLUMN_NAME         = 0;
    public static final int ID_OTTR_TYPE           = 1;
    public static final int ID_OTTR_TPL_VAR        = 2;
    public static final int ID_NAMESPACE           = 3;
    public static final int ID_LOCAL_NAME_PREFIX   = 4;
    public static final int ID_SPACE_TO_UNDERSCORE = 5;
    public static final int ID_IS_KEY              = 6;
    public static final int ID_IS_OPTIONAL         = 7;

    private final File melTocFile;
    private final Workbook toc;

    private final HashMap<String, TocEntry> entries = new HashMap<String, TocEntry>();
    private final List<TocEntry> keys = new LinkedList<TocEntry>();

    public MELTOC(File melTocFile) throws IOException {
        this.melTocFile = melTocFile;
        this.toc = WorkbookFactory.create(melTocFile);
        parse();
        if (!hasEntry(ColHdr.CLASS_OF_ACTIVITY.getColumnName())) {
            throw new IllegalArgumentException("Missing mandatory column header '" + ColHdr.CLASS_OF_ACTIVITY.getColumnName() + "': " + melTocFile.getPath());
        }
        if (!hasEntry(ColHdr.CLASS_OF_FUNCTIONAL_OBJECT.getColumnName())) {
            throw new IllegalArgumentException("Missing mandatory column header '" + ColHdr.CLASS_OF_FUNCTIONAL_OBJECT.getColumnName() + "': " + melTocFile.getPath());
        }
        if (!hasEntry(ColHdr.CLASS_OF_INANIMATE_PHYSICAL_OBJECT.getColumnName())) {
            throw new IllegalArgumentException("Missing mandatory column header '" + ColHdr.CLASS_OF_INANIMATE_PHYSICAL_OBJECT.getColumnName() + "': " + melTocFile.getPath());
        }
    }

    private void parse() {
        Sheet tocSheet = toc.getSheet(TOC_SHEET_NAME);
        parseHeaderRow(tocSheet);
        parseTocEntries(tocSheet);
    }

    private void parseHeaderRow(Sheet tocSheet) {
        Row headerRow = tocSheet.getRow(0);
        for (Cell cell : headerRow) {
            // null
        }
    }

    private void parseTocEntries(Sheet tocSheet) {
        for (int i=tocSheet.getFirstRowNum()+1; i<tocSheet.getLastRowNum()+1; i++) {
            parseTocEntry(tocSheet.getRow(i));
        }
    }

    private void parseTocEntry(Row row) {
        String columnName = Util.getStringOrNull(row.getCell(ID_COLUMN_NAME));
        if (columnName == null) {
            throw new IllegalArgumentException("Missing mandatory value in " + Util.getCellRef(row, ID_COLUMN_NAME) + ": " + melTocFile.getPath());
        }
        String ottrType = Util.getStringOrNull(row.getCell(ID_OTTR_TYPE));
        if (ottrType == null) {
            throw new IllegalArgumentException("Missing mandatory value in " + Util.getCellRef(row, ID_COLUMN_NAME) + ": " + melTocFile.getPath());
        }
        String ottrTplVar = Util.getStringOrNull(row.getCell(ID_OTTR_TPL_VAR));
        if (ottrTplVar == null) {
            throw new IllegalArgumentException("Missing mandatory value in " + Util.getCellRef(row, ID_OTTR_TPL_VAR) + ": " + melTocFile.getPath());
        }
        String spaceToUnderscore = Util.getStringOrEmpty(row.getCell(ID_SPACE_TO_UNDERSCORE));
        String isKey = Util.getStringOrEmpty(row.getCell(ID_IS_KEY));
        String isOptional = Util.getStringOrEmpty(row.getCell(ID_IS_OPTIONAL));
        TocEntry tocEntry = new TocEntry(
                columnName,
                ottrType,
                ottrTplVar,
                Util.getStringOrNull(row.getCell(ID_NAMESPACE)),
                Util.getStringOrNull(row.getCell(ID_LOCAL_NAME_PREFIX)),
                spaceToUnderscore.equals("Yes"),
                isKey.equals("Yes"),
                isOptional.equals("Yes")
        );
        entries.put(columnName, tocEntry);
        if (tocEntry.isKey()) {
            keys.add(tocEntry);
        }
    }

    public int size() {
        return entries.size();
    }

    public List<TocEntry> getKeys() {
        return keys;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("{");
        for (TocEntry entry : this) {
            sb.append(entry.toString() + ";");
        }
        sb.append("}");
        return sb.toString();
    }

    public Iterator<TocEntry> iterator() {
        return entries.values().iterator();
    }

    public TocEntry getEntry(String columnName) {
        return entries.get(columnName);
    }

    public boolean hasEntry(String columnName) {
        return entries.containsKey(columnName);
    }
}
