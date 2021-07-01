package com.aibel.mel.mel2aas.spring.cli;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class CommandRunner {

    private static final Logger LOG = LoggerFactory.getLogger(CommandRunner.class);

    private final String command;
    private final List<String> argList = new LinkedList<>();
    private final Runtime runtime;
    private int exitValue;
    private String stdoutOutput;
    private String stderrOutput;

    public CommandRunner(String command) {
        this.command = command;
        this.runtime = Runtime.getRuntime();
    }

    public void addArgument(String argument) {
        argList.add(argument);
    }

    public boolean execute() throws CommandRunnerException {
        Process process = null;
        try {
            String fullCommand = getCommandWithArgs();
            LOG.debug("fullCommand=" + fullCommand);

            process = runtime.exec(getCommandWithArgs());

            stdoutOutput = getOutput(process.getInputStream());

            exitValue = process.waitFor();
            LOG.debug("exitValue=" + exitValue);

            if (exitValue != 0) {
                stderrOutput = getOutput(process.getErrorStream());
                LOG.debug("stderrOutput=\n" + stderrOutput);
            }
        } catch (Exception e) {
            LOG.debug("e.getMessage()=" + e.getMessage());
            throw new CommandRunnerException("Error running command: " + e.getMessage());
        }
        return exitValue == 0;
    }

    private String getOutput(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer sb = new StringBuffer();
        String line = null;
        while ((line = reader.readLine()) != null) {
            if (line != null) {
                sb.append('\n');
            }
            sb.append(line);
        }
        reader.close();
        return sb.toString();
    }

    public String getCommand() {
        return command;
    }

    public String getCommandWithArgs() {
        StringBuffer sb = new StringBuffer();
        sb.append(command.replace("\"", ""));
        for (String arg : argList) {
            sb.append(' ');
            sb.append(arg);
        }
        return sb.toString();
    }

    public int getExitValue() {
        return exitValue;
    }

    public String getStdoutOutput() {
        return stdoutOutput;
    }

    public String getStderrOutput() {
        return stderrOutput;
    }

    public String getNonNullOutput() {
        if (stdoutOutput != null) {
            if (stderrOutput != null) {
                return "STDOUT:\n" + stdoutOutput + "\n\nSTDERR:\n" + stderrOutput;
            } else {
                return "STDOUT:\n" + stdoutOutput;
            }
        } else {
            if (stderrOutput != null) {
                return "STDERR:\n" + stderrOutput;
            } else {
                return null;
            }
        }
    }

}
