package org.fao.fenix.export.d3s.impl;


import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.d3s.impl.utilsMetadata.Language;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

@org.fao.fenix.commons.utils.annotations.export.Output("outputTable")
public class OutputTableExcel extends Output {

    private static final Logger LOGGER = Logger.getLogger(OutputTableExcel.class);

    private ArrayList<Integer> columnsOrder;
    private String language;
    private Map<String, Object> config;
    private CoreData resource;
    private SXSSFWorkbook wb;
    //DatatypeFormatter formatterValue;
    private ArrayList<DSDColumn> columns;
    private ArrayList<Integer> indexesLabelColumns;
    private static  Map<String, String> sourceM =new HashMap<>();
    private static Map<String, String> datasetM =new HashMap<>();
    private static Map<String, String> downloadedM =new HashMap<>();
    private static Map<String, String> notesM =new HashMap<>();

    static {
        sourceM.put("EN", "Source");
        sourceM.put("FR", "Source");

        datasetM.put("EN", "Dataset");
        datasetM.put("FR", "Dataset");

        downloadedM.put("EN", "Downloaded on");
        downloadedM.put("FR", "Téléchargé sur");

        notesM.put("EN", "Notes");
        notesM.put("FR", "Notes");
    }

    @Override
    public void init(Map<String, Object> config) {
        this.config = config!=null ? config : new HashMap<String, Object>();
        this.language = this.config.get("lang")!= null && !this.config.get("lang").equals("") && Language.contains(this.config.get("lang").toString().toUpperCase())? this.config.get("lang").toString(): "EN" ;
        this.indexesLabelColumns = new ArrayList<>();

    }

    @Override
    public void process(CoreData resource) throws Exception {
        this.resource = resource;
        wb = createExcel(((DSDDataset)resource.getMetadata().getDsd()).getColumns(), resource.getData());
    }

    @Override
    public CoreOutputHeader getHeader() throws Exception {

        CoreOutputHeader coreOutputHeader = new CoreOutputHeader();
        coreOutputHeader.setName(this.resource.getMetadata().getUid() + ".xlsx");
        coreOutputHeader.setSize(100);
        coreOutputHeader.setType(CoreOutputType.xlsx);
        return coreOutputHeader;
    }

    @Override
    public void write(OutputStream outputStream) throws Exception {
        wb.write(outputStream);
        outputStream.close();
        wb.dispose();
    }


    //business
    private SXSSFWorkbook createExcel(Collection<DSDColumn> collection, Iterator<Object[]> data) throws Exception {
/*

        formatterValue = new DatatypeFormatter();
        formatterValue.init(config.get("lang"));
*/

        this.columns = collection!=null ? new ArrayList(collection) : null;

        createOrderColumn();

        SXSSFWorkbook wb = new SXSSFWorkbook(100);
        String sheetName  =((String)config.get("sheetName"));

        Sheet sh = ( sheetName != null && sheetName!= "")? wb.createSheet(sheetName) : wb.createSheet();
        int rowCounter = 0;
        rowCounter = createHeader(sh, rowCounter, this.language);
        rowCounter = createBody(sh, data, rowCounter);

/*
        createFooters(sh,rowCounter);
*/

        return wb;
    }


    private int createMetadataHeader (Sheet sheet, int rowCounter) {
        // title
        Row rowSource = sheet.createRow(rowCounter);
        rowSource.setHeightInPoints(26.75f);
        rowSource.createCell(0).setCellValue(isOnMultilanguage(sourceM)? sourceM.get(this.language)+ " : ": sourceM.get("EN")+ " : ");
        rowSource.createCell(1).setCellValue(this.resource.getMetadata().getDsd().getContextSystem());
        rowCounter++;

        //dataset
        Row rowDataset = sheet.createRow(rowCounter);
        rowDataset.setHeightInPoints(26.75f);
        rowDataset.createCell(0).setCellValue(isOnMultilanguage(datasetM)? datasetM.get(this.language)+ " : ": datasetM.get("EN")+ " : ");
        rowDataset.createCell(1).setCellValue(this.resource.getMetadata().getUid());
        rowCounter++;

        // date of download
        Row rowCreatedOn = sheet.createRow(rowCounter);
        rowCreatedOn.setHeightInPoints(26.75f);
        rowCreatedOn.createCell(0).setCellValue(isOnMultilanguage(downloadedM)? downloadedM.get(this.language)+ " : ": downloadedM.get("EN")+ " : ");
        rowCreatedOn.createCell(1).setCellValue(new SimpleDateFormat("dd/MMM/yy").format(new Date()));

        //notes
        if(this.config.get("notes")!= null && !this.config.get("notes").toString().equals("")){
            rowCounter++;
            Row rowNotes = sheet.createRow(rowCounter);
            rowNotes.setHeightInPoints(26.75f);
            rowNotes.createCell(0).setCellValue(isOnMultilanguage(notesM)? notesM.get(this.language)+ " : ": notesM.get("EN")+ " : ");
            rowNotes.createCell(1).setCellValue(this.config.get("notes").toString());
        }
        rowCounter+=2;

        return rowCounter;

    }


    private int createHeader(Sheet sheet,int rowCounter, Object lang){

       rowCounter = createMetadataHeader(sheet,rowCounter);

        // column headers
        Row rowColumnHeaders = sheet.createRow(rowCounter);

        for(int i=0; i<this.columnsOrder.size(); i++) {
            int index = this.columnsOrder.get(i);
            Map<String, String> titles = this.columns.get(this.columnsOrder.get(i)).getTitle();
            if (titles != null) {

                if (titles.get(lang) == null) {
                    boolean notFound = true;
                    Iterator itTitles = titles.keySet().iterator();

                    while (itTitles.hasNext() && notFound) {
                        String titleKeyLabel = itTitles.next().toString();
                        if (titles.get(titleKeyLabel) != null) {
                            rowColumnHeaders.createCell(i).setCellValue(titles.get(titleKeyLabel).toUpperCase());
                            notFound =false;
                        }
                    }
                } else {
                    String labelTitle = (this.indexesLabelColumns.contains(index) )?
                            (isOnMultilanguage(titles))? titles.get(this.language).toUpperCase()+"_LABEL": titles.get("EN").toUpperCase()+"_LABEL":
                            (isOnMultilanguage(titles))? titles.get(this.language).toUpperCase(): titles.get("EN").toUpperCase();
                    rowColumnHeaders.createCell(i ).setCellValue(labelTitle);
                }
            }
        }
        return rowCounter;
    }


    private int createBody(Sheet sheet, Iterator<Object[]> data, int rowCounter) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {


        // row
        while (data.hasNext()){
            Object[] rowData = data.next();
            rowCounter++;
            Row row = sheet.createRow(rowCounter);

            for(int i =0; i< columnsOrder.size(); i++){
                String value = rowData[columnsOrder.get(i)]!= null? rowData[columnsOrder.get(i)].toString() : "";
                if(this.columns.get(columnsOrder.get(i)).getDataType().getLabel("EN").equals("Number") && rowData[columnsOrder.get(i)]!= null){
                    row.createCell(i).setCellValue(Double.parseDouble(value));
                }else {
                    row.createCell(i).setCellValue(value);
                }
            }
        }

        return rowCounter;
    }


    private void createFooter(Sheet sheet, int rowCounter){

        rowCounter +=3;
        Row rowFooter = sheet.createRow(rowCounter);
        rowFooter.createCell(0).setCellValue("Created on : ");
        rowFooter.createCell(1).setCellValue(new SimpleDateFormat("dd/MMM/yy").format(new Date()));
    }


    // utils
    private int getIndexVirtualColumn (String idToFind, int startIndex) {

        int result = -1;
        boolean found = false;
        for(int i = startIndex; i< this.columns.size() && !found; i++) {
            if(this.columns.get(i).getId().length() >3) {
                String substring = this.columns.get(i).getId().substring(0, this.columns.get(i).getId().length() - 3);
                if (substring.equals(idToFind)) {
                    this.indexesLabelColumns.add(i);
                    result = i;
                    found = true;
                }

            }
        }

        return result;
    }


    private void createOrderColumn () {
        this.columnsOrder = new ArrayList<>();

        for(int i =0; i< this.columns.size(); i++) {


            if(!this.columnsOrder.contains(i)){
                this.columnsOrder.add(i);
            }

            int possibleIndex = getIndexVirtualColumn(this.columns.get(i).getId(),i);
            int indexTrue = possibleIndex!= -1? possibleIndex : i;
            if(!this.columnsOrder.contains(indexTrue)){
                this.columnsOrder.add(indexTrue);
            }
        }
    }


    private boolean isOnMultilanguage (Map<String,String> labels) {
        return labels.get(this.language)!= null && !labels.get(this.language).equals("");
    }

}
