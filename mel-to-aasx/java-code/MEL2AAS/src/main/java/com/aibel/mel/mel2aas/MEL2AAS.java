package com.aibel.mel.mel2aas;

import com.aibel.mel.mel2aas.ottr.bottr.BottrSpec;
import com.aibel.mel.mel2aas.meltoc.MELTOC;
import com.aibel.mel.mel2aas.meltoc.TocEntry;
import com.aibel.mel.mel2aas.ottr.bottr.InstanceMap;
import com.aibel.mel.mel2aas.propertymap.MELPropertyMap;
import com.aibel.mel.mel2aas.ottr.stottr.OTTRTemplates;
import com.aibel.mel.mel2aas.templateregister.TemplateRegister;
import com.aibel.mel.mel2aas.util.PrefixParser;
import com.aibel.mel.mel2aas.util.SparqlGenerator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class MEL2AAS {

    private static final Logger log = LoggerFactory.getLogger(MEL2AAS.class);

    private final File melTocFile;
    private final File melMapFile;
    private final File tplRegFile;
    private final File prefixFile;
    private final Map<String, String> prefixMap;

    private final MELTOC melToc;
    private final MELPropertyMap melMap;
    private final TemplateRegister tplReg;

    public MEL2AAS(File melTocFile, File melMapFile, File tplRegFile, File prefixFile) throws IOException {
        this.melTocFile = melTocFile;
        this.melMapFile = melMapFile;
        this.tplRegFile = tplRegFile;
        this.prefixFile = prefixFile;
        PrefixParser pfxParser = new PrefixParser(prefixFile);
        this.prefixMap = pfxParser.getPrefixMap();
        this.melToc = new MELTOC(melTocFile);
//        log.debug("Parsed melToc: " + melToc.toString());
        this.melMap = new MELPropertyMap(melMapFile, melToc);
//        log.debug("Parsed melMap: " + melMap.toString());
        this.tplReg = new TemplateRegister(tplRegFile, prefixMap);
//        log.debug("Parsed tplReg: " + tplReg.toString());
    }

    public MEL2AAS(String melTocFileName, String melMapFileName, String tplRegFileName, String prefixFileName) throws IOException {
        this(
                new File(melTocFileName),
                new File(melMapFileName),
                new File(tplRegFileName),
                new File(prefixFileName)
        );
    }

    private BottrSpec getBottrSpec(File melCsvFile) {
        return new BottrSpec(melToc, melMap, tplReg, prefixMap, melCsvFile);
    }

    private void validateMelCsv(File melCsvFile) throws IOException {
        Reader in = new FileReader(melCsvFile);
        CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        for (String header : parser.getHeaderNames()) {
            if (!melToc.hasEntry(header)) {
                throw new IllegalArgumentException("Header '" + header + "' of MEL CSV file is not defined in MEL TOC file: " + melTocFile.getCanonicalPath());
            }
        }
        for (TocEntry tocEntry : melToc) {
            if (tocEntry.isKey() && !parser.getHeaderNames().contains(tocEntry.getColumnName())) {
                throw new IllegalArgumentException("Header '" + tocEntry.getColumnName() + "' defined as IS_KEY in MEL TOC file is not part of header in MEL CSV file: " + melCsvFile.getCanonicalPath());
            }
        }
    }

    public void writeBottrSpec(File melCsvFile, File bottrSpecFile) throws IOException {
        validateMelCsv(melCsvFile);
        BottrSpec bottrSpec = getBottrSpec(melCsvFile);
        BufferedWriter bw = new BufferedWriter(new FileWriter(bottrSpecFile));
        bw.write(bottrSpec.bottrSyntax());
        bw.close();
    }

    public void writeTemplateSignatures(File melCsvFile, File templateFile) throws IOException {
        validateMelCsv(melCsvFile);
        BottrSpec bottrSpec = getBottrSpec(melCsvFile);
        OTTRTemplates tpl = new OTTRTemplates(bottrSpec, prefixMap);
        BufferedWriter bw = new BufferedWriter(new FileWriter(templateFile));
        bw.write(tpl.stottrSyntax());
        bw.close();
    }

    public void writeSparqlQueries(File melCsvFile, File queryPath) throws IOException {
        if (!queryPath.exists()) {
            throw new IllegalArgumentException("Path not found: " + queryPath.getPath());
        }
        if (!queryPath.isDirectory()) {
            throw new IllegalArgumentException("Path is not a directory: " + queryPath.getPath());
        }
        validateMelCsv(melCsvFile);
        BottrSpec bottrSpec = getBottrSpec(melCsvFile);
        String path = queryPath.getPath();
        if (!path.endsWith("" + File.separator)) {
            path += File.separator;
        }
        for (InstanceMap instanceMap : bottrSpec) {
            String fileName = instanceMap.getTemplateUri().getLocalName() + ".rq";
            File file = new File(path + fileName);
            log.debug("SPARQL Query file name: " + file.getPath());
            SparqlGenerator generator = new SparqlGenerator(prefixMap, instanceMap);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(generator.sparqlSyntax());
            bw.close();
        }
    }

}
