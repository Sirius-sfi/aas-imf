package com.aibel.mel.mel2aas.meltocsv;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MelToCsv {

    private static final Logger log = LoggerFactory.getLogger(MelToCsv.class);

    public static final String MEL_SHEET_NAME = "MEL";
    public static final int IDX_HEADER_ROW = 5;

    private static final char[] REMOVE_HEADING_CHAR = new char[] { '(', ')', '&', '.', ',', '[', ']' };
    private static final String[][] REPLACE_HEADING_STR =
            new String[][] {
                    new String[] { "\n", " " },
                    new String[] { "  ", " " },
                    new String[] { "m²", "_m2" },
                    new String[] { "m³", "_m3" },
                    new String[] { "°C", "_degC" },
                    new String[] { "Barg", "_barg" },
                    new String[] { "/hr", "_per_hr" },
                    new String[] { "%", "pct" },
                    new String[] { " ", "_" },
                    new String[] { ":", "_" },
                    new String[] { "__", "_" },
                    new String[] { "_/_", "_or_" },
                    new String[] { "_-_", "_" },
                    new String[] { "_/", "_or_" }
            };

    private final File melExcelFile;

    public MelToCsv(File melExcelFile) {
        this.melExcelFile = melExcelFile;
    }

    public void writeCsvFile(File csvFile) throws IOException {
        Workbook wb = WorkbookFactory.create(melExcelFile);
        Sheet melSheet = wb.getSheet(MEL_SHEET_NAME);
        if (melSheet == null) {
            throw new IllegalArgumentException("MEL Excel file contains no sheet with name '" + MEL_SHEET_NAME + "': " + melExcelFile.getCanonicalPath());
        }
        final BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile));
        Row headerRow = melSheet.getRow(IDX_HEADER_ROW);
        List<String> headerList = new LinkedList<String>();
        for (int i=0; i<headerRow.getLastCellNum(); i++) {
            String val = getHeaderVal(headerRow, i);
            headerList.add(val);
        }
        final String[] headers = headerList.toArray(new String[]{});
        final CSVPrinter csvPrinter =
                CSVFormat.DEFAULT
                        .withHeader(headers)
                        .withQuote('\"')
                        .withQuoteMode(QuoteMode.NON_NUMERIC)
                        .print(writer);
        for (int i=IDX_HEADER_ROW+1; i<melSheet.getLastRowNum()+1; i++) {
            Row row = melSheet.getRow(i);
            Object[] csvValues = new Object[headers.length];
            for (int j=0; j<csvValues.length; j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    csvValues[j] = null;
                } else {
                    if (cell.getCellType().equals(CellType.NUMERIC) || cell.getCellType().equals(CellType.FORMULA)) {
                        csvValues[j] = cell.getNumericCellValue();
                    } else {
                        String str = cell.getStringCellValue();
                        str = str.replace("\n", " ");
                        str = str.trim();
                        if (str.length() == 0) {
                            csvValues[j] = null;
                        } else {
                            csvValues[j] = str;
                        }

                    }
                }
            }
            if (properRecord(csvValues)) {
                csvPrinter.printRecord(csvValues);
            } else {
//                log.debug("Throwing away record: " + Arrays.toString(csvValues));
            }
        }

        writer.close();
    }

    private boolean properRecord(Object[] record) {
        for (int i=0; i<record.length; i++) {
            if (record[i] != null) {
                if (record[i] instanceof Double) {
                    Double d = (Double) record[i];
                    if (d != 0) {
                        return true;
                    }
                } else if (record[i] instanceof String) {
                    String s = (String) record[i];
                    return !s.equals("BOTTOM LINE");
                }
            }
        }
        return false;
    }

    private String getHeaderVal(Row headerRow, int i) {
        Cell cell = headerRow.getCell(i);
        if (cell == null) {
            return null;
        } else {
            String val = cell.getStringCellValue().trim();
//            log.debug("val after trim: " + val);
            for (char removeChar : REMOVE_HEADING_CHAR) {
                val = val.replace("" + removeChar, "");
            }
//            log.debug("val after removeAllWithSameUuid char: " + val);
            for (String[] replacePair : REPLACE_HEADING_STR) {
                val = val.replace(replacePair[0], replacePair[1]);
            }
//            log.debug("val after replace string: " + val);
            return val;
        }
    }
}
