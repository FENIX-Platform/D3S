package org.fao.fenix.export.d3s.impl;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.utils.CSVWriter;
import org.fao.fenix.commons.utils.JSONUtils;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.d3s.impl.dto.CSVParameter;

import java.io.*;
import java.util.*;


@org.fao.fenix.commons.utils.annotations.export.Output("outputCSV")
public class OutputCSV extends Output {

    private static final Logger LOGGER = Logger.getLogger(OutputCSV.class);
    private CSVParameter config;
    private CoreData resource;
    private String language ;
    private String filename;
    private final String DEFAULT_LANG = "EN";
    private CSVWriter csvWriter;
    private String[] columnTitles;
    private OutputStream fw;



    @Override
    public void init(Map<String, Object> config) {
        try {
            this.config =(config!=null)? JSONUtils.convertValue(config, CSVParameter.class): new CSVParameter();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void process(CoreData resource) throws Exception {
        this.resource = resource;
        getParameters(resource.getMetadata());
        getTitles(new ArrayList<>(((DSDDataset)resource.getMetadata().getDsd()).getColumns()));



    }

    @Override
    public CoreOutputHeader getHeader() throws Exception {
        CoreOutputHeader coreOutputHeader = new CoreOutputHeader();
        coreOutputHeader.setName(this.filename + ".csv");
        coreOutputHeader.setSize(100);
        coreOutputHeader.setType(CoreOutputType.csv);
        return coreOutputHeader;
    }

    @Override
    public void write(OutputStream outputStream) throws Exception {
        csvWriter = new CSVWriter(
                outputStream,
                this.config.getCharacterSeparator(),
                this.config.getUseQuote(),
                this.config.getWindows(),
                null,
                this.config.getDateFormat(),
                this.config.getNumberFormat(),
                null);
        csvWriter.write(this.resource.getData(), Integer.MAX_VALUE);
        outputStream.close();


    }


    // utils
    private void getTitles (ArrayList<DSDColumn> columns) {
        this.columnTitles = new String[columns.size()];
        ArrayList<DSDColumn> dsdColumns = new ArrayList<>(columns);
        for(int i=0; i<dsdColumns.size(); i++)
            columnTitles[i] = ((dsdColumns.get(i).getTitle()!= null)?
                    (dsdColumns.get(i).getTitle().get(language)!= null)?
                            dsdColumns.get(i).getTitle().get(language).toString():
                            dsdColumns.get(i).getTitle().get(DEFAULT_LANG):  dsdColumns.get(i).getId());
    }

    private void getParameters (MeIdentification meIdentification) {
        this.language = this.config.getLanguage()!= null? this.config.getLanguage().toUpperCase(): "EN";
        this.filename = meIdentification.getUid();
    }







}
