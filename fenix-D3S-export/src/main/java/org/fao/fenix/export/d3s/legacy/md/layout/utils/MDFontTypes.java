package org.fao.fenix.export.d3s.legacy.md.layout.utils;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

public enum MDFontTypes {


//String fontname, String encoding, boolean embedded, float size, int style, BaseColor color
    coverTitle(
        (FontFactory.getFont(
                "roboto_thin", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 26, Font.NORMAL, ColorType.descGrey.getCmykColor(), true))),
    titleField(FontFactory.getFont("roboto_light",9,ColorType.blueFenix.getCmykColor())),
    valueField((FontFactory.getFont(("roboto_light"),9,ColorType.grey.getCmykColor()))),
    descriptionField((FontFactory.getFont(("roboto_light"),5,ColorType.descGrey.getCmykColor()))),
    footerField((FontFactory.getFont(("roboto_light"), 7, ColorType.grey.getCmykColor()))),
    headerField((FontFactory.getFont(("roboto_thin"), 12, ColorType.blueFenix.getCmykColor()))),
    coverDesc((FontFactory.getFont(("roboto_bold"), 16, ColorType.blueFenix.getCmykColor())));

    private Font fontType;

    MDFontTypes(Font fontType) { this.fontType = fontType;
    }

    public Font getFontType() {
        return fontType;
    }

}
