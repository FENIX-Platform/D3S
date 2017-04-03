package org.fao.fenix.export.d3s.legacy.md.layout.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * EventListner for Index
 */
public   class IndexEvent extends PdfPageEventHelper {
    private int page;

    public void setBody(boolean body) {
        this.body = body;
    }

    public boolean isBody() {
        return body;
    }

    private boolean body;
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        // set page number on content
        if(body) {
            page++;
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase(page + ""), (document.right() + document.left()) / 2, document.bottom() - 18, 0);
        }
    }

}