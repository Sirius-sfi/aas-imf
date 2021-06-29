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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class WebServiceController {

    private static final Logger LOG = LoggerFactory.getLogger(WebServiceController.class);

    private final TempStorageService tempStorageService;
    private final ApplicationConfig appConfig;
    private final File prefixFile;
    private final File tocFile;
    private final File mapFile;
    private final File tplRegFile;

    @Autowired
    public WebServiceController(TempStorageService tempStorageService, ApplicationConfig appConfig) {
        LOG.debug("File.separator='" + File.separator + "'");

        this.tempStorageService = tempStorageService;
        this.appConfig = appConfig;

        LOG.debug("appConfig.getPrefixFile()=" + appConfig.getPrefixFile());
        LOG.debug("appConfig.getTocFile()=" + appConfig.getTocFile());
        LOG.debug("appConfig.getMapFile()=" + appConfig.getMapFile());
        LOG.debug("appConfig.getTplRegFile()=" + appConfig.getTplRegFile());

        this.prefixFile = new File(appConfig.getPrefixFile());
        this.tocFile = new File(appConfig.getTocFile());
        this.mapFile = new File(appConfig.getMapFile());
        this.tplRegFile = new File(appConfig.getTplRegFile());

        LOG.debug("appConfig.getLutraCommand()=" + appConfig.getLutraCommand());
        LOG.debug("appConfig.getPathToTemplateLibrary()=" + appConfig.getPathToTemplateLibrary());
        LOG.debug("appConfig.getRdfToAasxCommand()=" + appConfig.getRdfToAasxCommand());
    }

    @PostMapping("/mel-to-rdf")
    public ResponseEntity<Resource> melToRdf(
            @RequestParam("mel_csv_file") MultipartFile melCsvFile
    ) throws IOException, TempStorageServiceException, CommandRunnerException {
        FileHandle csvFile = tempStorageService.createFileAndCopyContent(melCsvFile, FileType.CSV);
        FileHandle bottrFile = tempStorageService.createFileWithSameUuid(csvFile, FileType.BOTTR);
        FileHandle rdfFile = tempStorageService.createFileWithSameUuid(csvFile, FileType.RDF);
        MEL2AAS mel2Aas = new MEL2AAS(csvFile.getFile(), tocFile, mapFile, tplRegFile, prefixFile);
        mel2Aas.writeBottrSpec(bottrFile.getFile());

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
        if (runner.getExitValue() != 0) {
            LOG.debug("runner.getNonNullOutput()=\n" + runner.getNonNullOutput());
            throw new CommandRunnerException(runner.getNonNullOutput());
        }
        Resource resource = rdfFile.asResource();
        /*
         * TODO: Remove all file handles related to CSV-file!
         */
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

    @PostMapping("/rdf-to-aasx")
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
        Resource resource = aasxFile.asResource();
        /*
         * TODO: Remove all file handles related to CSV-file!
         */
        return new ResponseEntity<>(resource, HttpStatus.OK);
    }

}
