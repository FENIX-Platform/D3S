package org.fao.fenix.export.d3s.legacy.md.standard;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.apache.log4j.Logger;
import org.fao.fenix.export.d3s.legacy.md.data.dto.MDSDescriptor;
import org.fao.fenix.export.d3s.legacy.md.layout.factory.LayoutCreator;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.ColorType;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.RegistrationFont;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.SpecialBean;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.SpecialDateBean;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StandardLayoutCreator extends LayoutCreator {

    private static final Logger LOGGER = Logger.getLogger(StandardLayoutCreator.class);
    private final float LOGO_HEIGHT = 60;
    private final float LOGO_WIDTH = 90;
    private static float SIMPLE_HEIGHT_MARGIN = 19;
    private static float SIMPLE_HEIGHT_MARGIN_PARAGRAPH = 5;
    private final float OFFSET_RIGHT_TITLE = 100;
    private final float OFFSET_RIGHT = 100;
    private final float LOGO_TOP_OFFSET_SEPARATOR = 5;
    private final float OFFSET_HEIGHT_UP_TO_CENTER = 100;
    private final float OFFSET_HEIGHT_DWN_TITLE_TO_SEP = 11;
    private static int MARGIN_TO_ADD = 6;
    private final static String IMG_PATH = "/logos/cover_logo.png";
    private final static String CODE_SEPARATOR = "@@";
/*
    private static final String IMG_PATH = "templates/fao/logos/uneca/logo_copertina.png";
*/
    private static String DESCRIPTION_COVER = "METADATA OVERVIEW";
    private static float SEPARATOR_WIDTH = (float) 0.71;
    private static int SIMPLE_RIGHT_MARGIN = 0;
    private static String DATE_TYPEFIELD = Date.class.toString();
    private static String STRING_TYPEFIELD = String.class.toString();
    private static String DOUBLE_TYPEFIELD = Double.class.toString();
    private static String INTEGER_TYPEFIELD = Integer.class.toString();

    private static String RECURSIVE_TYPEFIELD = TreeMap.class.toString();
    private static String ARRAY_TYPEFIELD = ArrayList.class.toString();
    private static int[] COLSPAN_TABLE = new int[]{2, 3};
    private Document document;
    private String template;
    private RegistrationFont registrationFont;
    private TreeMap<String, Object> modelData;


    public StandardLayoutCreator(Document document, String template) throws DocumentException {
        this.document = document;
        this.template = template;
        // register all fenix fonts and styles
        registrationFont = new RegistrationFont();
        registrationFont.registerAll();
    }


    public void createCover(String title, PdfWriter writer) throws DocumentException, IOException {

        Paragraph titleLAbel = new Paragraph(title, registrationFont.getCoverTitle());

        Image logo = Image.getInstance(getClass().getClassLoader().getResource("templates/"+ template + IMG_PATH));

        Rectangle rect = writer.getBoxSize("art");

        PdfContentByte cb = writer.getDirectContent();
        cb.setColorStroke(ColorType.borderGrey.getCmykColor());
        cb.setLineWidth(SEPARATOR_WIDTH);

        cb.moveTo(((rect.getLeft() + rect.getRight()) / 2) - OFFSET_RIGHT, (rect.getTop() / 2) + OFFSET_HEIGHT_UP_TO_CENTER);
        cb.lineTo(rect.getRight() - 15, (rect.getTop() / 2) + OFFSET_HEIGHT_UP_TO_CENTER);
        cb.stroke();

        logo.scaleToFit(LOGO_HEIGHT, LOGO_WIDTH);
        logo.setAbsolutePosition(((rect.getLeft() + rect.getRight()) / 2) - OFFSET_RIGHT - 8, (rect.getTop() / 2) +
                OFFSET_HEIGHT_UP_TO_CENTER + LOGO_TOP_OFFSET_SEPARATOR);
        document.add(logo);

        titleLAbel.setIndentationLeft(((rect.getLeft() + rect.getRight()) / 2) - (OFFSET_RIGHT + 50));
        titleLAbel.setSpacingBefore((rect.getTop() / 2) - (OFFSET_HEIGHT_UP_TO_CENTER + OFFSET_HEIGHT_DWN_TITLE_TO_SEP));
        titleLAbel.setSpacingAfter(20);

        titleLAbel.setAlignment(Element.ALIGN_LEFT);
        document.add(titleLAbel);

        Chunk CONNECT = new Chunk(new LineSeparator(SEPARATOR_WIDTH, 100, ColorType.borderGrey.getCmykColor(), Element.ALIGN_CENTER, 3.5f));

        Paragraph separator = new Paragraph(CONNECT);
        separator.setIndentationLeft(((rect.getLeft() + rect.getRight()) / 2) - (OFFSET_RIGHT + 50));
        document.add(separator);

        Paragraph description = new Paragraph(DESCRIPTION_COVER, registrationFont.getCoverDesc());
        description.setIndentationLeft(((rect.getLeft() + rect.getRight()) / 2) - (OFFSET_RIGHT + 50));
        description.setSpacingAfter(10);
        document.add(description);

        document.newPage();
    }


    @Override
    public Document init(TreeMap<String, Object> modelData, String title, PdfWriter writer) throws DocumentException, IOException {
        this.modelData = modelData;
        createCover(title, writer);
        createBody();
        return document;
    }


    private void createBody() throws DocumentException {

        Set<String> keys = modelData.keySet();
        int indexChapter = 1;
        int margin = 0;
        for (String key : keys) {
            MDSDescriptor element = (MDSDescriptor) modelData.get(key);
            processDocumentBody(margin, element, key, indexChapter, modelData);
        }
    }


    private void processDocumentBody(int margin, MDSDescriptor element, String key, int indexChapter, TreeMap<String, Object> dataModel) throws DocumentException {

        boolean isBiggerHeaderMArgin = key.equals("1");

        if (!SpecialBean.isSpecialBean(element.getTitleBean())) {

            if ((isAStringObject(element.getValue()) || isANumberObject(element.getValue())) && !element.getTitleBean().equals("title") && !element.getValue().toString().equals("")) {
                // simple case
                writeSimpleElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
            } else if (isAnArrayObject(element.getValue())) {
                // array case
                ArrayList<Object> values = (ArrayList<Object>) element.getValue();
                writeArrayElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter, values);


            } else if (isARecursiveObject(element.getValue())) {
                // recursive case
                writeRecursiveElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
                TreeMap<String, Object> recursiveData = (TreeMap<String, Object>) element.getValue();

                Set<String> recKyes = recursiveData.keySet();
                for (String recKey : recKyes) {

                    MDSDescriptor elemRec = (MDSDescriptor) recursiveData.get(recKey);
                    processDocumentBody(margin + MARGIN_TO_ADD, elemRec, recKey, indexChapter, recursiveData);
                }
            }
        } else {
            // special bean case
            element.setValue(getStringFromSpecialBean(element));
            if (element.getValue() != null) {
                writeSimpleElement(margin, isBiggerHeaderMArgin, element, indexChapter);
            }
        }

    }

    private boolean isAnArrayObject(Object object) {
        return object.getClass().toString().equals(ARRAY_TYPEFIELD);
    }

    private boolean isARecursiveObject(Object object) {
        return object.getClass().toString().equals(RECURSIVE_TYPEFIELD);
    }

    private boolean isAStringObject(Object object) {
        return object.getClass().toString().equals(STRING_TYPEFIELD) || object.getClass().toString().equals(DATE_TYPEFIELD);
    }

    private boolean isANumberObject(Object object) {
        return object.getClass().toString().equals(DOUBLE_TYPEFIELD) || object.getClass().toString().equals(INTEGER_TYPEFIELD);
    }

    private void writeSimpleElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter) throws DocumentException {

        float marginApplied = (isBiggerHeaderMargin) ? SIMPLE_HEIGHT_MARGIN + 5 : SIMPLE_HEIGHT_MARGIN;

        PdfPTable table = new PdfPTable(2);
        table.setWidths(COLSPAN_TABLE);
        table.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell();
        Paragraph title = new Paragraph(value.getTitleToVisualize().toString(), registrationFont.getTitleField());
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        titleCell.addElement(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setPaddingLeft(rightMargin);

        PdfPCell valueCell = new PdfPCell();
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        String valueString = (SpecialDateBean.isSpecialDateBean(value.getTitleBean().toString()))? trasformDate(value.getValue()): value.getValue().toString();
        valueCell.addElement(new Phrase(valueString, registrationFont.getValueField()));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setColspan(2);

        setRightHeightOfCells(titleCell, valueCell, value);

        table.addCell(titleCell);
        table.addCell(valueCell);

        // space
        table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);

        document.add(table);
    }


    private void setRightHeightOfCells(PdfPCell titleCell, PdfPCell valueCell, MDSDescriptor element) {

        int words = countCharNumber(element.getValue().toString());
        if (words > 60) {
            titleCell.setVerticalAlignment(Element.ALIGN_TOP);
        }
    }

    private int countCharNumber(String word) {
        int counter = 0;
        if (word != null && !word.equals("")) {
            char[] arr = word.toCharArray();
            counter = arr.length;
        }
        return counter;
    }

    private void writeRecursiveElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter) throws DocumentException {

        String titleString = (value.getTitleToVisualize() != null) ? value.getTitleToVisualize().toString() : "title";

        boolean isUppercase = isAllUppercase(titleString);
        Paragraph title = new Paragraph();
        Font titleFont = (isUppercase) ? registrationFont.getTitleUnderlined() : registrationFont.getTitleField();
        Phrase phrase = new Phrase(titleString, titleFont);
        title.add(phrase);
        title.setIndentationLeft(rightMargin);
        if (isUppercase) {
            isUppercase = true;
            LineSeparator UNDERLINE =
                    new LineSeparator(SEPARATOR_WIDTH, 100, ColorType.borderGrey.getCmykColor(), Element.ALIGN_CENTER, -2);
            title.add(UNDERLINE);
        }
        if (isUppercase) {
            title.setSpacingAfter(SIMPLE_HEIGHT_MARGIN_PARAGRAPH + 8);
        } else {
            title.setSpacingAfter(SIMPLE_HEIGHT_MARGIN_PARAGRAPH);
        }
        document.add(title);
    }


    private String trasformDate (Object obj) {
        if(obj!= null) {
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            Date date = (Date)obj;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return f.format(cal.getTime());
        }else{
            return "";
        }
    }


    private boolean isAllUppercase(String s) {
        if (s != null) {
            for (char c : s.toCharArray()) {
                if (!Character.isUpperCase(c) && !Character.isWhitespace(c))
                    return false;
            }
            return true;
        } else {
            return false;
        }
    }


    private void writeArrayElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter, ArrayList<Object> values) throws DocumentException {

        int arraySize = values.size();

        PdfPTable table = new PdfPTable(2);
        table.setWidths(COLSPAN_TABLE);
        table.setWidthPercentage(100);

        String arrayValue ="";

        PdfPCell titleCell = new PdfPCell();
        Phrase title = new Phrase(value.getTitleToVisualize().toString(), registrationFont.getTitleField());

        titleCell.setVerticalAlignment(Element.ALIGN_TOP);
        titleCell.addElement(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setPaddingLeft(rightMargin);

        PdfPCell valueCell = new PdfPCell();
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        for (int i = 0; i < arraySize; i++) {
            String[] codeLabel = values.get(i).toString().split(CODE_SEPARATOR);

            for (int z = 0; z < codeLabel.length; z++) {
                arrayValue += codeLabel[z];
                arrayValue += (i != arraySize - 1) ? " ; " : " ";
            }
            if (i == arraySize - 1)
                table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);
        }
        Phrase phrase = new Phrase(arrayValue, registrationFont.getValueField());
        valueCell.addElement(phrase);

        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(titleCell);
        table.addCell(valueCell);

        document.add(table);

        Paragraph blank = new Paragraph("", registrationFont.getTitleField());
        blank.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);
        document.add(blank);
    }


    private String getStringFromSpecialBean(MDSDescriptor element) {

        String result = null;

        if (isAnArrayObject(element.getValue())) {
            result = "";
            ArrayList<Object> values = (ArrayList<Object>) element.getValue();
            for (int i = 0; i < values.size(); i++) {
                if (isAStringObject(values.get(i))) {
                    result += values.get(i).toString().split(CODE_SEPARATOR)[1];
                    if (i < values.size() - 1) {
                        result += ", ";
                    }
                } else {
                    if (result == null) {
                        result = getStringFromSpecialBean((MDSDescriptor) values.get(i));
                    }
                }
            }
            return result;
        } else if (isARecursiveObject(element.getValue())) {
            TreeMap<String, Object> recursiveData = (TreeMap<String, Object>) element.getValue();

            for (String key : recursiveData.keySet()) {
                if (result == null) {
                    result = getStringFromSpecialBean((MDSDescriptor) recursiveData.get(key));
                }
            }
        }
        return result;
    }
}



