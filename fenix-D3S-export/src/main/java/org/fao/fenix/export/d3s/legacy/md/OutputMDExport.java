package org.fao.fenix.export.d3s.legacy.md;


import com.fasterxml.jackson.databind.JsonNode;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.templates.standard.combined.dataset.Metadata;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.dto.CoreOutputType;
import org.fao.fenix.export.core.dto.data.CoreData;
import org.fao.fenix.export.core.dto.data.CoreMetaData;
import org.fao.fenix.export.core.output.plugin.Output;
import org.fao.fenix.export.d3s.legacy.md.data.DataCreator;
import org.fao.fenix.export.d3s.legacy.md.data.dto.MDSDescriptor;
import org.fao.fenix.export.d3s.legacy.md.layout.factory.LayoutCreator;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.ColorType;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.MDFontTypes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@org.fao.fenix.commons.utils.annotations.export.Output("outputMD")
public class OutputMDExport extends Output {

    private static final Logger LOGGER = Logger.getLogger(OutputMDExport.class);
    private Map<String, Object> config;
    private Metadata metadata;
    private DataCreator dataCreator ;
    private JsonNode mdsdNode;
    private ByteArrayOutputStream baos;


    private final String LANGUAGE_PROPERTY = "lang";

    private final String REPRESENTATION_TYPE_PROPERTY = "full";
    private final String FILENAME_PROPERTY = "fileName";
    private final String FILENAME_DEFAULT = "fenixExport.pdf";
    private final String PATH_DEFAULT_TEMPLATE = "fao";

    private String template;

    private final String FONTNAME_TITLEPAGES = "roboto_thin";
    private final float LOGO_HEIGHT = 100;
    private final float LOGO_WIDTH = 130;
    private final float OFFSET_RIGHT_TITLE = 49;
    private final float OFFSET_TOP_TITLE = 5;
    private final float OFFSET_RIGHT_LOGO = 7;
    private final float OFFSET_TOP_LOGO = 3;
    private final float DELIMITER_X_POS = 200;
    private final float DELIMITER_Y_POS_START = 805;
    private final float DELIMITER_Y_POS_END = 790;
    private final float OFFSET_FOOTER_UP = 18;
    private final int MARGIN_LEFT  = 50;
    private final int MARGIN_UP  = 80;
    private final int MARGIN_BOTTOM  = 50;
    private final int MARGIN_RIGHT  = 50;
    private final int RIGHT_OFFSET_FOOTER = 50;
    private static float SEPARATOR_WIDTH = (float) 0.71;


    /** Inner class to add a header and a footer. */
    class HeaderFooter extends PdfPageEventHelper {

        int pagenumber;
        String title;

        private final static String IMAGE_PATH_RESOURCES = "/logos/title_logo.png";

        Image logo;
        Phrase titlePhrase;
        Font titleHeaderFont;

        public HeaderFooter(String title) {
            this.title = title;
            titlePhrase = new Phrase(title);
        }


        public void onOpenDocument(PdfWriter writer, Document document) {
            pagenumber = -1;
            try {
                template = (config.get("template")!= null)? config.get("template").toString(): PATH_DEFAULT_TEMPLATE;
                logo = Image.getInstance(getClass().getClassLoader().getResource("templates/"+ template +IMAGE_PATH_RESOURCES));

            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void onStartPage(PdfWriter writer, Document document) {
            pagenumber = (pagenumber!=0)? pagenumber+1 : pagenumber+2;
        }


        public void onEndPage(PdfWriter writer, Document document) {

            if (pagenumber != 0) {
                if(titleHeaderFont == null) {
                    titleHeaderFont = FontFactory.getFont((FONTNAME_TITLEPAGES), ((float)(7)), ColorType.grey.getCmykColor());
                }
                Phrase titlePhrase = new Phrase(title,titleHeaderFont);
                Rectangle rect = writer.getBoxSize("art");
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_LEFT, new Phrase(String.format(" %d", pagenumber - 1), MDFontTypes.footerField.getFontType()),
                        ((rect.getLeft() + rect.getRight()) - RIGHT_OFFSET_FOOTER), rect.getBottom() - OFFSET_FOOTER_UP, 0);
                ColumnText.showTextAligned(writer.getDirectContent(),
                        Element.ALIGN_LEFT, titlePhrase,
                        ((rect.getLeft() + rect.getRight()) / 2)-OFFSET_RIGHT_TITLE, rect.getTop() + OFFSET_TOP_TITLE, 0);

                logo.scaleToFit(LOGO_HEIGHT, LOGO_WIDTH);
                logo.setAbsolutePosition(rect.getLeft() + OFFSET_RIGHT_LOGO, rect.getTop() - OFFSET_TOP_LOGO);
                try {
                    document.add(logo);

                    PdfContentByte cb = writer.getDirectContent();
                    cb.setColorStroke(ColorType.borderGrey.getCmykColor());
                    cb.setLineWidth(SEPARATOR_WIDTH);
                    //DELIMITER_X_POS - offsetToRMV * OFFSET_TITLE_LEFT
                    cb.moveTo( DELIMITER_X_POS, DELIMITER_Y_POS_END);
                    cb.lineTo( DELIMITER_X_POS, DELIMITER_Y_POS_START);
                    cb.stroke();

                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void init(Map<String, Object> config) {this.config = config;
        dataCreator = new DataCreator();}

    @Override
    public void process(CoreData resource) throws Exception {
        metadata = ((CoreMetaData)resource).getMetadataTemplate();
        mdsdNode = ((CoreMetaData)resource).getMdsd();

        // getting data in the right format
        String language = (config.get(LANGUAGE_PROPERTY)!= null && !config.get(LANGUAGE_PROPERTY).toString().equals(""))? config.get(LANGUAGE_PROPERTY).toString(): "EN";
        dataCreator.initDataFromMDSD(mdsdNode,metadata, language);

        // TODO: setting configuration pagesize
        Document document = new Document(PageSize.A4, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_UP, MARGIN_BOTTOM);
        baos = new ByteArrayOutputStream();
        PdfWriter contentWriter = PdfWriter.getInstance(document, baos);
        String title = retrieveTitle((TreeMap<String, Object>) dataCreator.getMetaDataCleaned());

        HeaderFooter event = new HeaderFooter(title);
        contentWriter.setBoxSize("art", new Rectangle(36, 54, 559, 788));
        contentWriter.setPageEvent(event);

        document.open();
        boolean isFull = config.get(REPRESENTATION_TYPE_PROPERTY)!= null? Boolean.getBoolean(config.get(REPRESENTATION_TYPE_PROPERTY).toString()): false;
        LayoutCreator layoutCreator = LayoutCreator.createInstance(isFull, document, template);

        document = layoutCreator.init((TreeMap<String, Object>) dataCreator.getMetaDataCleaned(), title, contentWriter);
        document.close();

    }

    @Override
    public CoreOutputHeader getHeader() throws Exception {
        CoreOutputHeader coreOutputHeader = new CoreOutputHeader();
        coreOutputHeader.setName(((config.get(FILENAME_PROPERTY) != null) ? config.get(FILENAME_PROPERTY).toString() : FILENAME_DEFAULT));
        coreOutputHeader.setSize(baos.size());
        coreOutputHeader.setType(CoreOutputType.pdf);
        return coreOutputHeader;
    }


    @Override
    public void write(OutputStream outputStream) throws Exception {
        baos.writeTo(outputStream);
        outputStream.close();
        outputStream.flush();
    }


    private String retrieveTitle (TreeMap<String, Object> mdsdStructure) {
        String result = null ;
        Set<String> keys = mdsdStructure.keySet();
        for(String key: keys) {
            MDSDescriptor tmp= (MDSDescriptor)mdsdStructure.get(key);
            if(tmp.getTitleBean().equals("title")){
                result = tmp.getValue().toString();
                break;
            }
        }
        return result;
    }

}
