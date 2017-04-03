package org.fao.fenix.export.d3s.legacy.md.layout.utils;


import com.itextpdf.text.pdf.CMYKColor;

public enum ColorType {


    grey(new CMYKColor((float) 0.0, (float) 0.0, (float) 0.0, (float) 0.80)),
    blueFenix( new CMYKColor((float)0.55,(float)0.25,(float) 0.00, (float)0.23)),
    borderGrey(new CMYKColor((float) 0.0, (float) 0.0, (float) 0.0, (float) 0.20)),
    descGrey(new CMYKColor((float) 0.0, (float) 0.0, (float) 0.0, (float) 0.40));


    private CMYKColor cmykColor;


    ColorType(CMYKColor cmykColor) { this.cmykColor = cmykColor; }


    public CMYKColor getCmykColor() {
        return cmykColor;
    }

}
