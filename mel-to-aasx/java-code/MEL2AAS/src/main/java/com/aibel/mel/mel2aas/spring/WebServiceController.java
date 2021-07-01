package com.aibel.mel.mel2aas.spring;

import com.aibel.mel.mel2aas.MEL2AAS;
import com.aibel.mel.mel2aas.spring.cli.CommandRunner;
import com.aibel.mel.mel2aas.spring.cli.CommandRunnerException;
import com.aibel.mel.mel2aas.spring.storage.FileHandle;
import com.aibel.mel.mel2aas.spring.storage.FileType;
import com.aibel.mel.mel2aas.spring.storage.TempStorageService;
import com.aibel.mel.mel2aas.spring.storage.TempStorageServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WebServiceController {

    private static final Logger LOG = LoggerFactory.getLogger(WebServiceController.class);

    private final TempStorageService tempStorageService;
    private final ApplicationConfig appConfig;
    private final File prefixFile;
    private final File tocFile;
    private final File mapFile;
    private final File tplRegFile;
    private final MEL2AAS mel2AAS;

    @Autowired
    public WebServiceController(TempStorageService tempStorageService, ApplicationConfig appConfig) throws IOException {
        LOG.info("application.name=" + appConfig.getApplicationName());
        LOG.info("build.version=" + appConfig.getBuildVersion());
        LOG.info("build.timestamp=" + appConfig.getBuildTimestamp());

        this.tempStorageService = tempStorageService;
        this.appConfig = appConfig;
        this.prefixFile = new File(appConfig.getPrefixFile());
        this.tocFile = new File(appConfig.getTocFile());
        this.mapFile = new File(appConfig.getMapFile());
        this.tplRegFile = new File(appConfig.getTplRegFile());
        this.mel2AAS = new MEL2AAS(tocFile, mapFile, tplRegFile, prefixFile);

        /*
         * LOG.info:
         */
        LOG.info("melws.prefixFile=" + appConfig.getPrefixFile());
        LOG.info("melws.tocFile=" + appConfig.getTocFile());
        LOG.info("melws.mapFile=" + appConfig.getMapFile());
        LOG.info("melws.tplRegFile=" + appConfig.getTplRegFile());
        LOG.info("melws.pathToTemplateLibrary=" + appConfig.getPathToTemplateLibrary());
        LOG.info("melws.lutraCommand=" + appConfig.getLutraCommand());
        LOG.info("melws.rdfToAasxCommand=" + appConfig.getRdfToAasxCommand());

        /*
         * LOG.debug:
         */
        LOG.debug("File.separator='" + File.separator + "'");
    }

    @PostMapping("/mel-to-rdf")
    @ResponseBody
    public ResponseEntity<Resource> melToRdf(
            @RequestParam("mel_csv_file") MultipartFile melCsvFile
    ) throws IOException, TempStorageServiceException, CommandRunnerException {
        FileHandle csvFile = tempStorageService.createFileAndCopyContent(melCsvFile, FileType.CSV);
        FileHandle bottrFile = tempStorageService.createFileWithSameUuid(csvFile, FileType.BOTTR);
        FileHandle rdfFile = tempStorageService.createFileWithSameUuid(csvFile, FileType.RDF);
        mel2AAS.writeBottrSpec(csvFile.getFile(), bottrFile.getFile());

        CommandRunner runner = new CommandRunner(appConfig.getLutraCommand());
        runner.addArgument("--mode expand");
        runner.addArgument("--inputFormat bottr");
        runner.addArgument("--outputFormat wottr");
        runner.addArgument("--library " + appConfig.getPathToTemplateLibrary());
        runner.addArgument("--libraryFormat stottr");
        runner.addArgument("--fetchMissing");
        runner.addArgument("--output " + rdfFile.getPathSansSuffix());
        runner.addArgument(bottrFile.getFile().getCanonicalPath());
        runner.execute();
        LOG.debug("runner.getExitValue()=" + runner.getExitValue());
        LOG.debug("runner.getNonNullOutput()=\n" + runner.getNonNullOutput());
        if (runner.getExitValue() != 0) {
            throw new CommandRunnerException(runner.getNonNullOutput());
        }
        /*
         * TODO: Remove all file handles related to CSV-file!
         */
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + rdfFile.getPublicFileName() + "\"").body(rdfFile.asResource());
    }

    @PostMapping("/rdf-to-aasx")
    @ResponseBody
    public ResponseEntity<Resource> rdfToAasx(
            @RequestParam(name = "mel_rdf_file") MultipartFile melRdfFile,
            @RequestParam(name = "uri_aas", required = false, defaultValue = "http://data.aibel.com/asset/equinor/AskjaUPP/MEL") String uriAas,
            @RequestParam(name = "uri_sub_model", required = false, defaultValue = "https://sirius.org/imf/ProductAspect") String uriSubModel,
            @RequestParam(name = "uri_asset", required = false, defaultValue = "http://equinor.com/KRA/MEL") String uriAsset,
            @RequestParam(name = "id_short_aas", required = false, defaultValue = "MEL") String idShortAas,
            @RequestParam(name = "id_short_sub_model", required = false, defaultValue = "SubModel") String idShortSubModel,
            @RequestParam(name = "id_short_asset", required = false, defaultValue = "MELAsset") String idShortAsset
    ) throws IOException, TempStorageServiceException, CommandRunnerException {
        LOG.debug("melRdfFile.getOriginalFilename()=" + melRdfFile.getOriginalFilename());
        LOG.debug("uriAas=" + uriAas);
        LOG.debug("uriSubModel=" + uriSubModel);
        LOG.debug("uriAsset=" + uriAsset);
        LOG.debug("idShortAas=" + idShortAas);
        LOG.debug("idShortSubModel=" + idShortSubModel);
        LOG.debug("idShortAsset=" + idShortAsset);

        FileHandle rdfFile = tempStorageService.createFileAndCopyContent(melRdfFile, FileType.RDF);
        FileHandle aasxFile = tempStorageService.createFileWithSameUuid(rdfFile, FileType.AASX);

        LOG.debug("rdfFile.getFile().getPath()=" + rdfFile.getFile().getPath());
        LOG.debug("aasxFile.getFile().getPath()=" + aasxFile.getFile().getPath());

        CommandRunner runner = new CommandRunner(appConfig.getRdfToAasxCommand());
        runner.addArgument("--aas_uri " + uriAas);
        runner.addArgument("--submodel_uri " + uriSubModel);
        runner.addArgument("--asset_uri " + uriAsset);
        runner.addArgument("--aas_id_short " + idShortAas);
        runner.addArgument("--submodel_id_short " + idShortSubModel);
        runner.addArgument("--asset_id_short " + idShortAsset);
        runner.addArgument("--rdf_payload " + rdfFile.getFile().getPath());
        runner.addArgument("--output " + aasxFile.getFile().getPath());
        runner.execute();
        LOG.debug("runner.getExitValue()=" + runner.getExitValue());
        if (runner.getExitValue() != 0) {
            LOG.debug("runner.getNonNullOutput()=\n" + runner.getNonNullOutput());
            throw new CommandRunnerException(runner.getNonNullOutput());
        }
        /*
         * TODO: Remove all file handles related to CSV-file!
         */
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + aasxFile.getPublicFileName() + "\"").body(aasxFile.asResource());
    }

    @GetMapping("/log")
    @ResponseBody
    public ResponseEntity<String> getLog() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(new File("/var/log/melws/spring.log")));
        String log = "";
        String line;
        while ((line = reader.readLine()) != null) {
            log += line + '\n';
        }
        reader.close();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        return new ResponseEntity<String>(log, headers, HttpStatus.OK);
    }

    @GetMapping("/version")
    @ResponseBody
    public ResponseEntity<String> getVersion() throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append("application.name").append("=").append(appConfig.getApplicationName()).append('\n');
        sb.append("build.version").append("=").append(appConfig.getBuildVersion()).append('\n');
        sb.append("build.timestamp").append("=").append(appConfig.getBuildTimestamp());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.OK);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
        LOG.debug("ex.getMessage()=" + ex.getMessage());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("text", "plain", StandardCharsets.UTF_8));
        return new ResponseEntity<String>(ex.getMessage(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
