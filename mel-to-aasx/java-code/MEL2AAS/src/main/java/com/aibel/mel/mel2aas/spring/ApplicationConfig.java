package com.aibel.mel.mel2aas.spring;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Value("${application.name}")
    private String applicationName;

    @Value("${build.version}")
    private String buildVersion;

    @Value("${build.timestamp}")
    private String buildTimestamp;

    @Value("${melws.prefixFile}")
    private String prefixFile;

    @Value("${melws.tocFile}")
    private String tocFile;

    @Value("${melws.mapFile}")
    private String mapFile;

    @Value("${melws.tplRegFile}")
    private String tplRegFile;

    @Value("${melws.lutraCommand}")
    private String lutraCommand;

    @Value("${melws.pathToTemplateLibrary}")
    private String pathToTemplateLibrary;

    @Value("${melws.rdfToAasxCommand}")
    private String rdfToAasxCommand;

    public String getApplicationName() {
        return applicationName;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public String getBuildTimestamp() {
        return buildTimestamp;
    }

    public String getPrefixFile() {
        return prefixFile;
    }

    public String getTocFile() {
        return tocFile;
    }

    public String getMapFile() {
        return mapFile;
    }

    public String getTplRegFile() {
        return tplRegFile;
    }

    public String getLutraCommand() { return lutraCommand; }

    public String getPathToTemplateLibrary() { return pathToTemplateLibrary; }

    public String getRdfToAasxCommand() {
        return rdfToAasxCommand;
    }
}
