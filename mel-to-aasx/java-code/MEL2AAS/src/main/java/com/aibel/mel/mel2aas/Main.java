package com.aibel.mel.mel2aas;

import com.aibel.mel.mel2aas.meltocsv.MelToCsv;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Main {

    /*
     * SLF4J Logger reference:
     */
    private static final Logger log = LoggerFactory.getLogger(Main.class);

//    /*
//     * Configure Log4j:
//     */
//    private static final Properties log4jprops = new Properties();
//    private static final org.apache.log4j.Logger comAibelLogger = org.apache.log4j.Logger.getLogger("com.aibel");
//    private static final org.apache.log4j.Logger rootLogger = org.apache.log4j.Logger.getRootLogger();
//
//    static {
//        log4jprops.setProperty("log4j.rootLogger", "INFO, A1");
//        log4jprops.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
//        log4jprops.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
//        log4jprops.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
//        PropertyConfigurator.configure(log4jprops);
//    }

    private static final Options options = new Options();

    private static final Option melToCsvOpOption = Option.builder("om")
            .longOpt("melToCsv")
            .desc("Operation: Convert MEL sheet of Excel file to CSV.")
            .build();
    private static final Option bottrSpecOpOption = Option.builder("ob")
            .longOpt("bottrSpec")
            .desc("Create bOTTR spec file.")
            .build();
    private static final Option tplSigOpOption = Option.builder("ts")
            .longOpt("templateSignatures")
            .desc("Create OTTR Template Signatures.")
            .build();
    private static final Option sparqlQryOpOption = Option.builder("sp")
            .longOpt("sparqlQueries")
            .desc("Create SPARQL queries for OTTR Template Signatures.")
            .build();
    private static final OptionGroup operation = new OptionGroup();



    private static final Option melExcelFileOption = Option.builder("e")
            .longOpt("mel_excel_file")
            .desc("The MEL Excel file.")
            .hasArg().argName("excel_file")
            .build();

    private static final Option melCsvFileOption = Option.builder("c")
            .longOpt("mel_csv_file")
            .desc("The MEL file (CSV).")
            .hasArg().argName("csv_file")
            .build();

    private static final Option melTocFile = Option.builder("t")
            .longOpt("toc_file")
            .desc("The MEL TOC file (Excel).")
            .hasArg().argName("toc_file")
            .build();

    private static final Option melMapFile = Option.builder("m")
            .longOpt("map_file")
            .desc("The MEL mapping file (Excel).")
            .hasArg().argName("map_file")
            .build();

    private static final Option tplRegFile = Option.builder("r")
            .longOpt("tpl_reg_file")
            .desc("The Template Register file (Excel).")
            .hasArg().argName("tpl_reg_file")
            .build();

    private static final Option prefixFile = Option.builder("p")
            .longOpt("prefix_file")
            .desc("The prefix file (Turtle).")
            .hasArg().argName("prefix_file")
            .build();

    private static final Option bottrOutFile = Option.builder("b")
            .longOpt("bottr_file")
            .desc("The output bottr.")
            .hasArg().argName("bottr_file")
            .build();

    private static final Option tplSigOutFile = Option.builder("s")
            .longOpt("tplSigFile")
            .desc("The output OTTR TemplateSignature file.")
            .hasArg().argName("tplSigFile")
            .build();

    private static final Option sparqlQryPath = Option.builder("sqp")
            .longOpt("sparqlQueryPath")
            .desc("The path in which to write SPARQL query files.")
            .hasArg().argName("sparqlQueryPath")
            .build();

    private static final Option helpOption = Option.builder("h")
            .longOpt("help")
            .desc("Print usage and exit.")
            .build();

    private static final Option debugOption = Option.builder("d").
            required(false).
            longOpt("debug").
            desc("Set debugOption mode: output debugOption messages.").
            build();

    static {
        operation.addOption(melToCsvOpOption);
        operation.addOption(bottrSpecOpOption);
        operation.addOption(tplSigOpOption);
        operation.addOption(sparqlQryOpOption);
        operation.isRequired();
        options.addOptionGroup(operation);
        options.addOption(melExcelFileOption);
        options.addOption(melCsvFileOption);
        options.addOption(melTocFile);
        options.addOption(melMapFile);
        options.addOption(tplRegFile);
        options.addOption(prefixFile);
        options.addOption(bottrOutFile);
        options.addOption(tplSigOutFile);
        options.addOption(helpOption);
        options.addOption(debugOption);
        options.addOption(sparqlQryPath);
    }

    private static final Option[] operationOpts = new Option[] {melToCsvOpOption, bottrSpecOpOption, tplSigOpOption, sparqlQryOpOption};

    private String melCsvFileName;
    private String melTocFileName;
    private String melMapFileName;
    private String tplRegFileName;
    private String prefixFileName;
    private String bottrOutFileName;
    private String tplSigOutFileName;
    private String sparqlQryPathName;

    private CommandLine cmd = null;

    private void printError(String msg, boolean printUsage) {
        System.err.println(msg);
        if (printUsage) {
            printUsage();
        }
        System.exit(1);
    }

    private void printUsage() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "mel2aas", options);
    }

    public Main(String[] args) {

        CommandLineParser parser = new DefaultParser();

        try {
           cmd = parser.parse(options, args);
        } catch (ParseException e) {
            printError("Error: " + e.getMessage(), true);
        }

//       /*
//         * Set default log level:
//         */
//        comAibelLogger.setLevel(Level.INFO);
//
//        /*
//         * Check if debugOption mode is set, then log anyways:
//         */
//        if (cmd.hasOption(debugOption.getOpt())) {
//            comAibelLogger.setLevel(Level.DEBUG);
//        }

        if (cmd.hasOption(helpOption.getOpt())) {
            printUsage();
            System.exit(0);
        }

        if (!hasOption(operationOpts)) {
            printError("Missing required operation", true);
        }

        if (cmd.hasOption(melToCsvOpOption.getOpt())) {
            if (!cmd.hasOption(melExcelFileOption.getOpt())) {
                printError("Operation " + melToCsvOpOption.getLongOpt() + " requires option " + melExcelFileOption.getLongOpt(), true);
                System.exit(1);
            }
            final String melExcelFileName = cmd.getOptionValue(melExcelFileOption.getOpt());
            final File melExcelFile = new File(melExcelFileName);
            if (!cmd.hasOption(melCsvFileOption.getOpt())) {
                printError("Operation " + melToCsvOpOption.getLongOpt() + " requires option " + melCsvFileOption.getLongOpt(), true);
                System.exit(1);
            }
            final String melCsvFileName = cmd.getOptionValue(melCsvFileOption.getOpt());
            final File melCsvFile = new File(melCsvFileName);

            final MelToCsv melToCsv = new MelToCsv(melExcelFile);
            try {
                melToCsv.writeCsvFile(melCsvFile);
            } catch (IOException e) {
                printError(e.getMessage(), false);
            }
        }

        if (cmd.hasOption(bottrSpecOpOption.getOpt())) {
            if (cmd.hasOption(melCsvFileOption.getOpt())) {
                melCsvFileName = cmd.getOptionValue(melCsvFileOption.getOpt());
            } else {
                melCsvFileName = null;
                printError("missing required option " + melCsvFileOption.getOpt(), true);
            }

            if (cmd.hasOption(melTocFile.getOpt())) {
                melTocFileName = cmd.getOptionValue(melTocFile.getOpt());
            } else {
                melTocFileName = null;
                printError("missing required option " + melTocFile.getOpt(), true);
            }

            if (cmd.hasOption(melMapFile.getOpt())) {
                melMapFileName = cmd.getOptionValue(melMapFile.getOpt());
            } else {
                melMapFileName = null;
                printError("missing required option " + melMapFile.getOpt(), true);
            }

            if (cmd.hasOption(tplRegFile.getOpt())) {
                tplRegFileName = cmd.getOptionValue(tplRegFile.getOpt());
            } else {
                tplRegFileName = null;
                printError("missing required option " + tplRegFile.getOpt(), true);
            }

            if (cmd.hasOption(prefixFile.getOpt())) {
                prefixFileName = cmd.getOptionValue(prefixFile.getOpt());
            } else {
                prefixFileName = null;
                printError("missing required option " + prefixFile.getOpt(), true);
            }

            if (cmd.hasOption(bottrOutFile.getOpt())) {
                bottrOutFileName = cmd.getOptionValue(bottrOutFile.getOpt());
            } else {
                bottrOutFileName = null;
                printError("missing required option " + bottrOutFile.getOpt(), true);
            }

            MEL2AAS m2o = null;
            try {
                m2o = new MEL2AAS(melCsvFileName, melTocFileName, melMapFileName, tplRegFileName, prefixFileName);
                File bottrSpecFile = new File(bottrOutFileName);
                m2o.writeBottrSpec(bottrSpecFile);
            } catch (Exception e) {
//                if (comAibelLogger.getLevel() == Level.DEBUG) {
//                    e.printStackTrace(System.err);
//                }
                printError(e.getMessage(), false);
            }
        }

        if (cmd.hasOption(tplSigOpOption.getOpt())) {
            if (cmd.hasOption(melCsvFileOption.getOpt())) {
                melCsvFileName = cmd.getOptionValue(melCsvFileOption.getOpt());
            } else {
                melCsvFileName = null;
                printError("missing required option " + melCsvFileOption.getOpt(), true);
            }

            if (cmd.hasOption(melTocFile.getOpt())) {
                melTocFileName = cmd.getOptionValue(melTocFile.getOpt());
            } else {
                melTocFileName = null;
                printError("missing required option " + melTocFile.getOpt(), true);
            }

            if (cmd.hasOption(melMapFile.getOpt())) {
                melMapFileName = cmd.getOptionValue(melMapFile.getOpt());
            } else {
                melMapFileName = null;
                printError("missing required option " + melMapFile.getOpt(), true);
            }

            if (cmd.hasOption(tplRegFile.getOpt())) {
                tplRegFileName = cmd.getOptionValue(tplRegFile.getOpt());
            } else {
                tplRegFileName = null;
                printError("missing required option " + tplRegFile.getOpt(), true);
            }

            if (cmd.hasOption(prefixFile.getOpt())) {
                prefixFileName = cmd.getOptionValue(prefixFile.getOpt());
            } else {
                prefixFileName = null;
                printError("missing required option " + prefixFile.getOpt(), true);
            }

            if (cmd.hasOption(tplSigOutFile.getOpt())) {
                tplSigOutFileName = cmd.getOptionValue(tplSigOutFile.getOpt());
            } else {
                tplSigOutFileName = null;
                printError("missing required option " + tplSigOutFile.getOpt(), true);
            }

            MEL2AAS m2o = null;
            try {
                m2o = new MEL2AAS(melCsvFileName, melTocFileName, melMapFileName, tplRegFileName, prefixFileName);
                File file = new File(tplSigOutFileName);
                m2o.writeTemplateSignatures(file);
            } catch (Exception e) {
//                if (comAibelLogger.getLevel() == Level.DEBUG) {
//                    e.printStackTrace(System.err);
//                }
                printError(e.getMessage(), false);
            }
        }

        if (cmd.hasOption(sparqlQryOpOption.getOpt())) {
            if (cmd.hasOption(melCsvFileOption.getOpt())) {
                melCsvFileName = cmd.getOptionValue(melCsvFileOption.getOpt());
            } else {
                melCsvFileName = null;
                printError("missing required option " + melCsvFileOption.getOpt(), true);
            }

            if (cmd.hasOption(melTocFile.getOpt())) {
                melTocFileName = cmd.getOptionValue(melTocFile.getOpt());
            } else {
                melTocFileName = null;
                printError("missing required option " + melTocFile.getOpt(), true);
            }

            if (cmd.hasOption(melMapFile.getOpt())) {
                melMapFileName = cmd.getOptionValue(melMapFile.getOpt());
            } else {
                melMapFileName = null;
                printError("missing required option " + melMapFile.getOpt(), true);
            }

            if (cmd.hasOption(tplRegFile.getOpt())) {
                tplRegFileName = cmd.getOptionValue(tplRegFile.getOpt());
            } else {
                tplRegFileName = null;
                printError("missing required option " + tplRegFile.getOpt(), true);
            }

            if (cmd.hasOption(prefixFile.getOpt())) {
                prefixFileName = cmd.getOptionValue(prefixFile.getOpt());
            } else {
                prefixFileName = null;
                printError("missing required option " + prefixFile.getOpt(), true);
            }

            if (cmd.hasOption(sparqlQryPath.getOpt())) {
                sparqlQryPathName = cmd.getOptionValue(sparqlQryPath.getOpt());
            } else {
                sparqlQryPathName = null;
                printError("missing required option " + sparqlQryPath.getOpt(), true);
            }

            MEL2AAS m2o = null;
            try {
                m2o = new MEL2AAS(melCsvFileName, melTocFileName, melMapFileName, tplRegFileName, prefixFileName);
                File file = new File(sparqlQryPathName);
                m2o.writeSparqlQueries(file);
            } catch (Exception e) {
//                if (comAibelLogger.getLevel() == Level.DEBUG) {
//                    e.printStackTrace(System.err);
//                }
                printError(e.getMessage(), false);
            }
        }
    }

    private Option getFirstOtion(Option[] opts) {
        for (Option o : opts) {
            if (cmd.hasOption(o.getOpt())) {
                return o;
            }
        }
        return null;
    }

    private boolean hasOption(Option[] opts) {
        return getFirstOtion(opts) != null;
    }


    public static void main(String[] args) {
        new Main(args);
    }
}
