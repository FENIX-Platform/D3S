package org.fao.fenix.export.d3s.legacy.md.layout.utils;


import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

public class RegistrationFont {

    private  boolean isRegistered = false;
    private Font coverTitle;
    private Font titleField;
    private Font titleUnderlined;
    private Font valueField;
    private Font descriptionField;
    private Font footerField;
    private Font headerField;
    private Font coverDesc;

    public Font getCoverTitle() {
        return coverTitle;
    }

    public Font getTitleUnderlined() {
        return titleUnderlined;
    }


    public Font getTitleField() {
        return titleField;
    }

    public Font getValueField() {
        return valueField;
    }

    public Font getDescriptionField() {
        return descriptionField;
    }

    public Font getFooterField() {
        return footerField;
    }

    public Font getHeaderField() {
        return headerField;
    }

    public Font getCoverDesc() {
        return coverDesc;
    }

    public RegistrationFont() {}


    private  boolean init () {

//(String fontname,
// String encoding, boolean embedded, float size, int style, BaseColor color, boolean cached)
        for(FenixFonts f: FenixFonts.values()) {

            FontFactory.register(getClass().getClassLoader().getResource( f.getUrl()).toString(), f.toString());
        }
        isRegistered = true;
        return isRegistered;
    }

    public boolean registerAll () {
        boolean result = (isRegistered)? isRegistered: init();
        registerFonts();

        return result;
    }


    private void registerFonts () {
        coverTitle =FontFactory.getFont("roboto_thin", BaseFont.IDENTITY_H, BaseFont.EMBEDDED, 26, Font.NORMAL, ColorType.descGrey.getCmykColor());
        titleField =FontFactory.getFont(("roboto_light"),9,ColorType.blueFenix.getCmykColor());
        titleUnderlined=FontFactory.getFont(("roboto_light"),10,ColorType.blueFenix.getCmykColor());
        valueField =FontFactory.getFont(("roboto_light"),9,ColorType.grey.getCmykColor());
        descriptionField =FontFactory.getFont(("roboto_light"),5,ColorType.descGrey.getCmykColor());
        footerField=FontFactory.getFont(("roboto_light"), 7, ColorType.grey.getCmykColor());
        headerField=FontFactory.getFont(("roboto_thin"), 12, ColorType.blueFenix.getCmykColor());
        coverDesc  =FontFactory.getFont(("roboto_bold"), 12, ColorType.blueFenix.getCmykColor());

    }

}
