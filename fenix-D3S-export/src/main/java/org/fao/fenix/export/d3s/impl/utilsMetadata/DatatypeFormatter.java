package org.fao.fenix.export.d3s.impl.utilsMetadata;

import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.OjCode;
import org.fao.fenix.commons.msd.dto.full.OjCodeList;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DatatypeFormatter {


    private static final Logger LOGGER = Logger.getLogger(DatatypeFormatter.class);


    private String language;


    public void init(Object language) {
        this.language = (language != null && !language.equals(""))? language.toString(): "EN";
    }


    public Double getRightNumberFormat(DSDColumn column, Object data, LinkedHashMap format) {

        Double result = null;

        if (!data.equals(null)) {
            Object outputFormatValue = format.get("formatValue");
            String formatValue = (outputFormatValue != null && !outputFormatValue.toString().equals(""))? outputFormatValue.toString() : "###,###.###";
            DecimalFormat df = new DecimalFormat(formatValue );
            result = Double.parseDouble(df.format(data));
        }
        return result;

    }


    public String getRightTextFormat(DSDColumn column, Object data, LinkedHashMap format) {
        return data.toString();
    }


    public String getRightCodeFormat(DSDColumn column, Object data, LinkedHashMap format) {

        String result = null;
        String label = null;
        boolean notFound = true;

        if (!data.equals(null)) {

            Iterator<OjCodeList> codeListIt = column.getValues().getCodes().iterator();

            while (codeListIt.hasNext() && notFound) {
                Iterator<OjCode> codesIt = codeListIt.next().getCodes().iterator();
                while (codesIt.hasNext() && notFound) {
                    OjCode ojCode = codesIt.next();
                    if (ojCode.getCode().equals(data.toString())) {
                        label = chooseRightLabel(ojCode);
                        notFound = false;
                    }
                }
            }
            Object outputFormatValue = format.get("formatCode");
            String formatValue = outputFormatValue != null && !outputFormatValue.toString().equals("")? outputFormatValue.toString() : "$label";
            result = formatRightValue(formatValue, data.toString(), label);
        }
        return result;
    }


    public String getRightCustomCodeFormat(DSDColumn column, Object data, LinkedHashMap format) {

        //TODO
        return getRightCodeFormat(column,data,format);
    }


    private String chooseRightLabel(OjCode ojCode) {

        String result = null;

        boolean notFound = true;
        Map<String, String> labels = ojCode.getLabel();

        if (language != null && labels.get(language) != null) {
            result = labels.get(language);
        } else {
            for (String key : labels.keySet()) {
                if (labels.get(key) != null) {
                    result = labels.get(key);
                    break;
                }
            }
        }

        return result;
    }


    private String formatRightValue(String formatCode, String data, String  label) {
        String result = null;
        if(formatCode.equals("$label")) {
            result =  label;
        }
        return result;
    }


    public String getRightLabelFormat(DSDColumn column, LinkedHashMap data, LinkedHashMap format) {
        return (String)data.get(language);
    }


    public Boolean getRightBoolFormat(DSDColumn column, Object data, LinkedHashMap format) {
        Boolean result = null;

        if (!data.equals(null)) {
            result = (Boolean)data;
        }

        return result;
    }


    public Double getRightPercentageFormat(DSDColumn column, Object data, LinkedHashMap format) {
        return getRightNumberFormat(column, data, format);
    }


    public String getRightDateFormat(DSDColumn column, Object data, LinkedHashMap format) {

        String result = null;

        if (!data.equals(null)) {
            Object formatDateObj = format.get("formatDate");

            String formatDateRequested = (formatDateObj != null && !formatDateObj.toString().equals(""))? formatDateObj.toString(): "dd/MM/yyyy";


            SimpleDateFormat formatDate;
            Date dateParsed = null;
            try {
                dateParsed = new SimpleDateFormat("yyyyMMdd").parse(String.valueOf(data));
                DateFormat rightDateFormat = new SimpleDateFormat(formatDateRequested);
                result = rightDateFormat.format(dateParsed);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return result;
    }


    public String getRightYearFormat(DSDColumn column, Object data, LinkedHashMap format) {
        String result = null;

        if (!data.equals(null)) {
            Object formatDateObj = format.get("formatDate");

            String formatDateRequested = (formatDateObj != null && formatDateObj.toString() != "")? formatDateObj.toString(): "dd/MM/yyyy";


            SimpleDateFormat formatDate;
            Date dateParsed = null;
            try {
                dateParsed = new SimpleDateFormat("yyyy").parse(String.valueOf(data));
                DateFormat rightDateFormat = new SimpleDateFormat(formatDateRequested);
                result = rightDateFormat.format(dateParsed);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return result;
    }


    public String getRightMonthFormat(DSDColumn column, Object data, LinkedHashMap format) {
        String result = null;

        if (!data.equals(null)) {
            Object formatDateObj = format.get("formatDate");

            String formatDateRequested = (formatDateObj != null && formatDateObj.toString() != "")? formatDateObj.toString(): "dd/MM/yyyy";


            SimpleDateFormat formatDate;
            Date dateParsed = null;
            try {
                dateParsed = new SimpleDateFormat("yyyyMM").parse(String.valueOf(data));
                DateFormat rightDateFormat = new SimpleDateFormat(formatDateRequested);
                result = rightDateFormat.format(dateParsed);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return result;
    }


    public String getRightTimeFormat(DSDColumn column, Object data, LinkedHashMap format) {
        String result = null;

        if (!data.equals(null)) {

            Object formatDateObj = format.get("formatDate");

            String formatDateRequested = (formatDateObj != null && formatDateObj.toString() != "")? formatDateObj.toString(): "dd/MM/yyyy";

            SimpleDateFormat formatDate;
            Date dateParsed = null;
            try {
                dateParsed = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(String.valueOf(data));
                DateFormat rightDateFormat = new SimpleDateFormat(formatDateRequested);
                result = rightDateFormat.format(dateParsed);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

        return result;
    }


    public Double getRightEnumerationformat(DSDColumn column, Object data, LinkedHashMap format){

        return getRightNumberFormat(column,data,format);
    }
}