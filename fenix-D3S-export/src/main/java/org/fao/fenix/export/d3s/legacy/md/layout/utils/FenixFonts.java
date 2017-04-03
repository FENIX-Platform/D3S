package org.fao.fenix.export.d3s.legacy.md.layout.utils;


public enum FenixFonts {

    roboto_normal("fonts/roboto/Roboto-Regular.ttf"),
    roboto_light("fonts/roboto/Roboto-Light.ttf"),
    roboto_bold("fonts/roboto/Roboto-Bold.ttf"),
    roboto_thin("fonts/roboto/Roboto-Thin.ttf"),
    roboto_thin_cover("fonts/roboto/Roboto-Thin.ttf");


    private String url;

    FenixFonts(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
