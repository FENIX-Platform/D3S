package org.fao.fenix.export.d3s.legacy.md.full;


import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.CMYKColor;
import org.fao.fenix.export.d3s.legacy.md.layout.utils.MDFontTypes;

import java.util.HashMap;

public class StyleSheetCreator {


    private HashMap<String, Font> fontMap;
    private MDFontTypes fontType;
    private static final String TITLE = "title";


    public void init () {

        fontMap = new HashMap<>();
        fontMap.put("title",new Font(Font.FontFamily.HELVETICA ,30, Font.BOLD,
                new CMYKColor(0.9f, 0.7f, 0.4f, 0.1f)));
/*
        fontMap.put(fontType.normal, FontFactory.getFont(FontFactory.COURIER, 10, Font.NORMAL));
*/
    }

    public Paragraph getSpaceParagraph (String section) {

        Paragraph result= null;
        switch (section) {

            case TITLE:
                result = new Paragraph(new Paragraph(20, "",  MDFontTypes.titleField.getFontType()));
                break;
        }

        return result;

    }



    public Font getFont (MDFontTypes font) {
        if(fontMap!= null)
            return fontMap.get(font);
        return null;
    }
}
