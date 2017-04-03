package org.fao.fenix.export.d3s.legacy.md.full;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.log4j.Logger;
import org.fao.fenix.export.d3s.legacy.md.data.dto.MDSDescriptor;
import org.fao.fenix.export.d3s.legacy.md.layout.factory.LayoutCreator;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.ColorType;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.IndexModel;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.MDFontTypes;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.RegistrationFont;

import java.io.IOException;
import java.util.*;

public class FullLayoutCreator extends LayoutCreator {

    private static final Logger LOGGER = Logger.getLogger(FullLayoutCreator.class);
    private StyleSheetCreator styleSheetCreator;
    private static float SIMPLE_HEIGHT_MARGIN = 18;
    private static int SIMPLE_RIGHT_MARGIN = 0;
    private static String DATE_TYPEFIELD = Date.class.toString();
    private static String STRING_TYPEFIELD = String.class.toString();
    private static String RECURSIVE_TYPEFIELD = TreeMap.class.toString();
    private static String ARRAY_TYPEFIELD = ArrayList.class.toString();
    ArrayList<Chapter> chapterList;
    private Map<String, IndexModel> indexModelMap;
    private Document document;
    private RegistrationFont registrationFont;
    private TreeMap<String, Object> modelData;
    private final static String CODE_SEPARATOR = "@@";


    public FullLayoutCreator(Document document) throws DocumentException {
        this.document = document;
        // register all fenix fonts and styles
        registrationFont = new RegistrationFont();
        registrationFont.registerAll();
    }


    @Override
    public Document init(TreeMap<String, Object> modelData, String title,PdfWriter writer) throws DocumentException, IOException {

        // register all f

        this.modelData = modelData;
        styleSheetCreator = new StyleSheetCreator();
        PdfPTable table = null;
        Chapter indexChapter = null;


       /* createIndexModel();

        makeIndex();*/
        createBody();


        //  Iterator<String> dataIterator = modelData.keySet().iterator();

        return this.document;

    }

    private void createTitle(MDSDescriptor descriptor) throws DocumentException {
        document.add(new Paragraph(5, descriptor.getValue().toString(), MDFontTypes.valueField.getFontType()));
        document.add(styleSheetCreator.getSpaceParagraph("title"));
    }


    private void createBody() throws DocumentException {


        Set<String> keys = modelData.keySet();
        int indexChapter = 1;
        int margin = 0;
        for (String key : keys) {

            MDSDescriptor element = (MDSDescriptor) modelData.get(key);
          /*  if (element.getTitleBean().equals("uid")) {
                System.out.println("stop!");
            }*/
            processDocumentBody(margin, element, key, indexChapter, modelData);
        }
    }


    private void processDocumentBody(int margin, MDSDescriptor element, String key, int indexChapter, TreeMap<String, Object> dataModel) throws DocumentException {


        boolean isBiggerHeaderMArgin = key.equals("1");

        if (isAStringObject(element.getValue()) && !element.getTitleBean().equals("title")) {
            writeSimpleElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
        } else if (isAnArrayObject(element.getValue())) {
            System.out.println("array!");
            ArrayList<Object> values = (ArrayList<Object>) element.getValue();
            writeArrayElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter, values);


        } else if (isARecursiveObject(element.getValue())) {
            System.out.println("recursive!");
            writeRecursiveElement(margin, isBiggerHeaderMArgin, (MDSDescriptor) dataModel.get(key), indexChapter);
            TreeMap<String, Object> recursiveData = (TreeMap<String, Object>) element.getValue();

            Set<String> recKyes = recursiveData.keySet();
            for (String recKey : recKyes) {

                if(recKey.equals("2")){
                    System.out.println("stop!");
                }

                MDSDescriptor elemRec = (MDSDescriptor) recursiveData.get(recKey);
                processDocumentBody(margin + 5, elemRec, recKey, indexChapter, recursiveData);
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


    private void writeSimpleElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter) throws DocumentException {

        float marginApplied = (isBiggerHeaderMargin) ? SIMPLE_HEIGHT_MARGIN + 5 : SIMPLE_HEIGHT_MARGIN;

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);

        PdfPCell titleCell = new PdfPCell();
        Phrase title = new Phrase(value.getTitleToVisualize().toString(), MDFontTypes.titleField.getFontType());
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        titleCell.addElement(title);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        titleCell.setPaddingLeft(rightMargin);


        PdfPCell valueCell = new PdfPCell();
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        valueCell.addElement(new Phrase(value.getValue().toString(), MDFontTypes.valueField.getFontType()));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        valueCell.setColspan(2);

        PdfPCell descripCell = new PdfPCell();
        descripCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Phrase description = new Phrase(value.getDescription().toString(), MDFontTypes.descriptionField.getFontType());
        descripCell.addElement(description);
        descripCell.setBorder(Rectangle.NO_BORDER);
        descripCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(titleCell);
        table.addCell(valueCell);
        table.addCell(descripCell);

        // space
        table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);

        document.add(table);
    }

    private void writeRecursiveElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter) throws DocumentException {

        String titleString = (value.getTitleToVisualize() != null) ? value.getTitleToVisualize().toString() : "title";
        String descString = (value.getDescription() != null) ? value.getDescription().toString() : "Description";

        Paragraph title = new Paragraph();
        title.add(new Phrase(titleString, MDFontTypes.titleField.getFontType()));
        title.setIndentationLeft(rightMargin);

        Paragraph description = new Paragraph();
        description.add(new Phrase(descString, MDFontTypes.descriptionField.getFontType()));
        description.setIndentationLeft(rightMargin);

        description.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);

        document.add(title);
        document.add(description);

    }


    private void writeArrayElement(int rightMargin, boolean isBiggerHeaderMargin, MDSDescriptor value, int indexChapter, ArrayList<Object> values) throws DocumentException {


        int arraySize = values.size();
        boolean isUniqueValue = arraySize==1;


        for (int i = 0; i < arraySize; i++) {
            String[] codeLabel = values.get(i).toString().split(CODE_SEPARATOR);
            PdfPTable table = new PdfPTable(codeLabel.length);
            table.setWidthPercentage(100);

            PdfPCell[] cells = new PdfPCell[codeLabel.length];

            for( int z =0 ; z<codeLabel.length; z++) {
                cells[z] = new PdfPCell();
                cells[z] = new PdfPCell(new Phrase(codeLabel[z],MDFontTypes.valueField.getFontType()));
                cells[z].setMinimumHeight(20);
                cells[z].setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(cells[z]);
            }
         /*   PdfPCell codeCell = new PdfPCell();
            PdfPCell valueCell = new PdfPCell();
            String[] codeLabel = values.get(i).toString().split("-");


            PdfPCell cell1 = new PdfPCell(new Phrase(codeLabel[0],MDFontTypes.valueField.getFontType()));
            PdfPCell cell2 = new PdfPCell(new Phrase(codeLabel[1],MDFontTypes.valueField.getFontType()));
            setBorderCell(isUniqueValue, cell1, cell2);

            cell1.setMinimumHeight(20);
            cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell1);

            cell2.setMinimumHeight(20);
            cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);


            table.addCell(cell2);*/
        /*
            Font font = MDFontTypes.valueField.getFontType();

            Paragraph para1 = new Paragraph(codeLabel[0], font);
            Paragraph para2 = new Paragraph(codeLabel[1], font);

            para1.setLeading(0, 1);
            para2.setLeading(0, 1);

            codeCell.setMinimumHeight(20);
            valueCell.setMinimumHeight(20);
            codeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);


            codeCell.addElement(para1);
            valueCell.addElement(para2);
            codeCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            setBorderCell(isUniqueValue, codeCell, valueCell);


            table.addCell(codeCell);
            table.addCell(valueCell);*/



/*
            Phrase codeContent = (new Phrase(codeLabel[0], MDFontTypes.valueField.getFontType()));
            Phrase labelContent = (new Phrase(codeLabel[1], MDFontTypes.valueField.getFontType()));

            Float fontSize = codeContent.getFont().getSize();
            Float capHeight = codeContent.getFont().getBaseFont().getFontDescriptor(BaseFont.CAPHEIGHT, fontSize);

            Float ascent = codeContent.getFont().getBaseFont().getFontDescriptor(BaseFont.ASCENT, fontSize);

            System.out.println("FS: " + fontSize + " cH: " + capHeight+ " AS: "+ascent);

         *//*   codeContent.setLeading(0, 1);
            labelContent.setLeading(0, 1);
*//*
*//*

            codeCell.setMinimumHeight(20);
            System.out.println(codeContent.ALIGN_CENTER);
            System.out.println(Element.ALIGN_CENTER);
            System.out.println(Element.ALIGN_MIDDLE);
            codeCell.setVerticalAlignment(codeContent.ALIGN_MIDDLE | codeContent.ALIGN_CENTER);

            valueCell.setMinimumHeight(20);
            valueCell.setVerticalAlignment(valueCell.ALIGN_CENTER);
*//*

            codeCell.addElement(codeContent);

            valueCell.addElement(labelContent);

            codeCell.setMinimumHeight(20);
            System.out.println(codeContent.ALIGN_CENTER);
            System.out.println(Element.ALIGN_CENTER);
            System.out.println(Element.ALIGN_MIDDLE);
            codeCell.setVerticalAlignment(codeContent.ALIGN_MIDDLE | codeContent.ALIGN_CENTER);

            valueCell.setMinimumHeight(20);
            valueCell.setPadding((float) (valueCell.ALIGN_CENTER+0.6875));



          *//*  codeCell.setPadding(padding);
            codeCell.setPaddingTop(capHeight - fontSize + padding);*//*


          *//*  codeCell.setMinimumHeight(20);
            codeCell.setVerticalAlignment(Element.ALIGN_CENTER);
            valueCell.setMinimumHeight(20);
            valueCell.setVerticalAlignment(Element.ALIGN_CENTER);*//*
            setBorderCell(isUniqueValue, codeCell, valueCell);

         //   codeCell.setPaddingLeft(rightMargin);
            table.addCell(codeCell);
            table.addCell(valueCell);*/

            if (i == arraySize - 1)
                table.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);
            document.add(table);

        }


        Paragraph blank = new Paragraph("", MDFontTypes.titleField.getFontType());
        blank.setSpacingAfter(SIMPLE_HEIGHT_MARGIN);
        document.add(blank);


    }

    private void setBorderCell ( boolean isOnly,PdfPCell... cells) {

        if(isOnly) {
            for(PdfPCell cell: cells) {
                cell.setBorder(Rectangle.BOTTOM | Rectangle.TOP);
                cell.setBorderColor(ColorType.borderGrey.getCmykColor());
            }
        }else {
            for(PdfPCell cell: cells) {
                cell.setBorder(Rectangle.BOTTOM);
                cell.setBorderColor(ColorType.borderGrey.getCmykColor());
            }
        }

    }


    private void createFooter() {
        //TODO

    }

  /*  private void addStringTypeField(MDSDescriptor descriptor, int margin) throws DocumentException {
        String title = (descriptor.getTitleToVisualize() != null) ? descriptor.getTitleToVisualize() : descriptor.getTitleBean();
        String value = descriptor.getValue().toString();

        Phrase titlePhrase = new Phrase(12, title, MDFontTypes.valueField.getFontType());
        Phrase valuePhrase = new Phrase(12, value, MDFontTypes.valueField.getFontType());
        Paragraph p = new Paragraph(titlePhrase + "  :  ");
        p.add(valuePhrase);

        document.add(p);
    }


    private void addOnlyTitle(MDSDescriptor toAdd) throws DocumentException {

        String title = (toAdd.getTitleToVisualize() != null) ? toAdd.getTitleToVisualize() : toAdd.getTitleBean();
        document.add(new Paragraph(new Phrase(12, title, MDFontTypes.valueField.getFontType())));
        // then space (now I don't know how to do it :) )
    }

    private void createIndexOnFirstPage(MDSDescriptor temp) throws DocumentException {

        chapterList = new ArrayList<Chapter>();
        String[] titles = {"asd", "asd", "as", "@", "2", "23", "2", "42"};
        for (int i = 0; i < titles.length; i++) {
            Chunk chapTitle = new Chunk(titles[i] + " " + i);
            Chapter chapter = new Chapter(new Paragraph(chapTitle), i);
            chapTitle.setLocalDestination(chapter.getTitle().getContent());
            document.add(chapter);
            chapterList.add(chapter);
        }
    }

    private void makeIndex() throws DocumentException {

        PdfPTable table = new PdfPTable(2);
        Paragraph paragraph = new Paragraph("Index", MDFontTypes.titleField.getFontType());

        Chapter indexChapter = new Chapter(paragraph, 0);

        Set<String> keys = indexModelMap.keySet();

        for (String key : keys) {

            PdfPCell left = new PdfPCell(new Phrase(key, MDFontTypes.valueField.getFontType()));
            left.setBorder(Rectangle.NO_BORDER);

            String prhase = (indexModelMap.get(key).getTitle() != null) ? indexModelMap.get(key).getTitle() : "notTitle";
            Chunk pageno = new Chunk(prhase, MDFontTypes.valueField.getFontType());
            PdfPCell right = new PdfPCell(new Phrase(pageno));
            right.setHorizontalAlignment(Element.ALIGN_RIGHT);
            right.setBorder(Rectangle.NO_BORDER);

            table.addCell(left);
            table.addCell(right);
        }
        indexChapter.add(table);
        document.add(indexChapter);
    }


    private void createIndexModel() {
        indexModelMap = new HashMap<String, IndexModel>();
        Set<String> keys = modelData.keySet();
        int indexCounter = 0;
        for (String key : keys) {
            indexCounter++;
            MDSDescriptor tmp = (MDSDescriptor) modelData.get(key);
            fillIndex(tmp, "" + indexCounter);
        }
    }


    private void fillIndex(MDSDescriptor tempDescriptor, String indexKey) {

        if (tempDescriptor.getValue().getClass().toString().equals(STRING_TYPEFIELD) || tempDescriptor.getValue().getClass().toString().equals(DATE_TYPEFIELD)) {
            indexModelMap.put(indexKey.toString(), new IndexModel(tempDescriptor.getTitleToVisualize(), null));

        }
        // discendents of the root
        else if (tempDescriptor.getValue().getClass().toString().equals(TreeMap.class.toString())) {

            String indexUpd = indexKey + ".0";

            TreeMap<String, MDSDescriptor> tmpTreeMAp = (TreeMap<String, MDSDescriptor>) tempDescriptor.getValue();

            Set<String> keys = ((TreeMap<String, MDSDescriptor>) tempDescriptor.getValue()).keySet();
            indexModelMap.put(indexKey.toString(), new IndexModel(tempDescriptor.getTitleToVisualize(), null));

            for (String key : keys) {

                indexUpd = updateCounter(indexUpd);
                fillIndex(tmpTreeMAp.get(key), indexUpd);
            }
        } else if (tempDescriptor.getValue().getClass().toString().equals(ArrayList.class.toString())) {
            if (tempDescriptor.getTitleBean().equals("OjCode")) {
                indexModelMap.put(indexKey.toString(), new IndexModel(tempDescriptor.getTitleToVisualize(), null));
            } else {
                String indexUpd = indexKey + ".0";
                TreeMap<String, MDSDescriptor> tempArray = ((TreeMap<String, MDSDescriptor>) ((ArrayList) tempDescriptor.getValue()).get(0));
                Set<String> arrayKeys = tempArray.keySet();
                for (String key : arrayKeys) {
                    indexUpd = updateCounter(indexUpd);
                    fillIndex(tempArray.get(key), indexUpd);
                }
            }

        }
        LOGGER.error(tempDescriptor.getValue().getClass().toString());
    }*/

    private String updateCounter(String counter) {

        String[] counterChunks = counter.split("\\.");
        int lengthSplit = counterChunks.length;

        String result = "";
        for (int i = 0; i < lengthSplit; i++) {
            if (i != lengthSplit - 1) {
                result += counterChunks[i] + ".";
            } else {
                result += Integer.parseInt(counterChunks[i]) + 1;
            }
        }

        return result;
    }
}




