package org.fao.fenix.export.d3s.legacy.md.layout.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;

/** Inner class to add a header and a footer. */
class HeaderFooter extends PdfPageEventHelper {

    /** Current page number (will be reset for every chapter). */
    int pagenumber;
    String title;
    private final static String IMAGE_PATH = "templates/fao/logos/FAO_logo.png";
    Image logo;

    public HeaderFooter(String title) {
        this.title = title;
    }


    /**
     * Initialize one of the headers.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
        pagenumber = 1;
        try {
            logo = Image.getInstance(this.getClass().getClassLoader().getResource("../").getPath() + IMAGE_PATH);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize one of the headers, based on the chapter title;
     * reset the page number.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onChapter(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document, float,
     *      com.itextpdf.text.Paragraph)
     */
    public void onChapter(PdfWriter writer, Document document,
                          float paragraphPosition, Paragraph title) {
        // pagenumber = 1;
    }

    /**
     * Increase the page number.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onStartPage(PdfWriter writer, Document document) {
        pagenumber++;
    }

    /**
     * Adds the header and the footer.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onEndPage(PdfWriter writer, Document document) {
        Rectangle rect = writer.getBoxSize("art");
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_CENTER, new Phrase(String.format(" %d", pagenumber - 1), MDFontTypes.footerField.getFontType()),
                ((rect.getLeft() + rect.getRight()) - 50), rect.getBottom() - 18, 0);
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_CENTER, new Phrase(title.toUpperCase(), MDFontTypes.headerField.getFontType()),
                ((rect.getLeft() + rect.getRight()) / 2), rect.getTop() + 5, 0);
        logo.scalePercent((float) 15);

        logo.setAbsolutePosition(rect.getLeft() + 7, rect.getTop() - 7);
        try {
            writer.getDirectContent().addImage(logo);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}