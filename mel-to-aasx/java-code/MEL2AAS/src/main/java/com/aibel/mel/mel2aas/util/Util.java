package com.aibel.mel.mel2aas.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.util.Map;

public class Util {

    private Util() {}

    public static String getStringOrEmpty(Cell cell) {
        if (cell == null) {
            return "";
        } else {
            return cell.getStringCellValue();
        }
    }

    public static String getStringOrNull(Cell cell, boolean trim) {
        if (cell == null) {
            return null;
        } else {
            String val = cell.getStringCellValue();
            if (trim) {
                val = val.trim();
            }
            if (val.length() > 0) {
                return val;
            } else {
                return null;
            }
        }
    }

    public static String getStringOrNull(Cell cell) {
        return getStringOrNull(cell, false);
    }

    public static String getStringOrDie(Row row, int cellIndex) {
        return getCellOrDie(row, cellIndex).getStringCellValue();
    }

    public static Cell getCellOrDie(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) {
            throw new IllegalArgumentException("Expected non-empty cell " + getCellRef(row, cellIndex));
        }
        return cell;
    }

    public static Integer getIntegerOrDie(Row row, int cellIndex) {
        return new Double(getCellOrDie(row, cellIndex).getNumericCellValue()).intValue();
    }

    public static String getCellRef(Row row, int pCol) {
        CellReference ref = new CellReference(row.getRowNum(), pCol);
        return ref.formatAsString();
    }

    public static String getPrefix(Map<String, String> prefixMap, String namespace) {
        for (String prefix : prefixMap.keySet()) {
            if (prefixMap.get(prefix).equals(namespace)) {
                return prefix;
            }
        }
        return null;
    }

    public static String fixPath(File file) {
        String str = file.getPath();
        str = str.replace("..\\", "");
        str = str.replace("\\", "/");
        return str;
    }

}
