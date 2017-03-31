package org.fao.fenix.export.d3s.impl.dto;

public class CSVParameter {

    private Character characterSeparator;
    private Boolean useQuote;
    private Boolean windows;
    private String dateFormat;
    private String numberFormat;
    private String language;




    public Character getCharacterSeparator() {
        return characterSeparator;
    }

    public void setCharacterSeparator(Character characterSeparator) {
        this.characterSeparator = characterSeparator;
    }

    public Boolean getUseQuote() {
        return useQuote;
    }

    public void setUseQuote(Boolean useQuote) {
        this.useQuote = useQuote;
    }

    public Boolean getWindows() {
        return windows;
    }

    public void setWindows(Boolean windows) {
        this.windows = windows;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(String numberFormat) {
        this.numberFormat = numberFormat;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
