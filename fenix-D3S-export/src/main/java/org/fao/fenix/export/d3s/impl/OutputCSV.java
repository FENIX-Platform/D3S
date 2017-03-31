package org.fao.fenix.export.d3s.impl;

import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.utils.CSVWriter;
import org.fao.fenix.commons.utils.JSONUtils;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.d3s.impl.dto.CSVParameter;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@org.fao.fenix.commons.utils.annotations.export.Output("outputCSV")
public class OutputCSV extends Output {

    private static final Logger LOGGER = Logger.getLogger(OutputCSV.class);
    private CSVParameter config;
    private CoreData resource;
    private String language ;
    private final String DEFAULT_LANG = "EN";
    private CSVWriter csvWriter;
    private Collection<String> titles;



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
        getTitles(new ArrayList<DSDColumn>(((DSDDataset)resource.getMetadata().getDsd()).getColumns()));
    }

    @Override
    public CoreOutputHeader getHeader() throws Exception {
        CoreOutputHeader coreOutputHeader = new CoreOutputHeader();
        coreOutputHeader.setName(this.resource.getMetadata().getUid() + ".csv");
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
                (String[]) this.titles.toArray());
        csvWriter.write(resource.getData(), Integer.MAX_VALUE);//TODO: to fix size

    }


    // utils
    private void getTitles (ArrayList<DSDColumn> columns) {
        this.titles = new ArrayList<>();
        ArrayList<DSDColumn> dsdColumns = new ArrayList<>(columns);
        for(DSDColumn column: dsdColumns)
            titles.add((column.getTitle()!= null)? (column.getTitle().get(language)!= null)? column.getTitle().get(language).toString(): column.getTitle().get(DEFAULT_LANG):  column.getId());
    }
}
