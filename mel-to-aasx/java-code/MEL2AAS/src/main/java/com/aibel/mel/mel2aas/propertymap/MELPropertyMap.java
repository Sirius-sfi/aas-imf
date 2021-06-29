package com.aibel.mel.mel2aas.propertymap;

import com.aibel.mel.mel2aas.meltoc.MELTOC;
import com.aibel.mel.mel2aas.util.Util;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MELPropertyMap implements Iterable<MELPropertyMapping> {

    public final int IDX_MEL_CSV_COL_HDR_START = 8;

    private final List<String> header = new LinkedList<String>();
    private final Map<String, Integer> colHeaderToColIdx = new HashMap<String, Integer>();
    private final List<MELPropertyMapping> mappings = new LinkedList<MELPropertyMapping>();

    public MELPropertyMap(File propertyMap, MELTOC meltoc) throws IOException {
        Workbook wb = WorkbookFactory.create(propertyMap);
        parse(wb, meltoc);
    }

    private void parse(Workbook wb, MELTOC meltoc) {
        Sheet sheet = wb.getSheetAt(0);
        parseHeader(sheet.getRow(1), meltoc);
        parseRows(sheet);
    }

    private void parseHeader(Row row, MELTOC meltoc) {
        for (int i=0; i<row.getLastCellNum(); i++) {
            if (row.getCell(i) == null) {
                throw new IllegalArgumentException("Missing header value in " + Util.getCellRef(row, i));
            } else {
                String str = row.getCell(i).getStringCellValue().trim();
                header.add(str);
                if (i >= IDX_MEL_CSV_COL_HDR_START) {
                    if (!meltoc.hasEntry(str)) {
                        throw new IllegalArgumentException("While parsing MEL Mapping - header '" + str + "' in cell " + Util.getCellRef(row, i) + " not defined in MEL TOC");
                    }
                    colHeaderToColIdx.put(str, i);
                }
            }
        }
    }

    private void parseRows(Sheet sheet) {
        String[] values = null;
        for (int i=2; i<sheet.getLastRowNum()+1; i++) {
            Row row = sheet.getRow(i);
            values = new String[ColHdr.values().length];
            for (ColHdr colHdr : ColHdr.values()) {
                String value = Util.getStringOrNull(row.getCell(colHdr.getColumnIndex()), true);
                if (value == null && colHdr.isRequired()) {
                    throw new IllegalArgumentException("Missing required value in cell " + Util.getCellRef(row, colHdr.getColumnIndex()) + " in MEL Mapping");
                }
                values[colHdr.getColumnIndex()] = value;
            }
            MELPropertyMapping mapping = new MELPropertyMapping(values);
            for (int j=IDX_MEL_CSV_COL_HDR_START; j<header.size(); j++) {
                String cellValue = Util.getStringOrNull(row.getCell(j), true);
                if (cellValue != null && !cellValue.trim().equals("N/A")) {
                    mapping.addProperty(header.get(j), cellValue.trim());
                }
            }
            mappings.add(mapping);
        }
    }

    public int size() {
        return mappings.size();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append("{");
        boolean first = true;
        for (MELPropertyMapping m : mappings) {
            if (first) {
                first = false;
            } else {
                sb.append(";");
            }
            sb.append(m.toString());
        }

        sb.append("}");
        return sb.toString();
    }

    public Iterator<MELPropertyMapping> iterator() {
        return mappings.iterator();
    }
}
