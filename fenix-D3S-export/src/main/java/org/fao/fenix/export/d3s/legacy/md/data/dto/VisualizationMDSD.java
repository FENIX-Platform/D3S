package org.fao.fenix.export.d3s.legacy.md.data.dto;

import com.fasterxml.jackson.databind.JsonNode;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class VisualizationMDSD {

    private Map<String, Object> metaDataCleaned;

    private JsonNode mdsd;

    private final static String LANG = "EN";
    private final static String REQUIRED_FIELD = "required";
    private final static String ORDER_FIELD = "order";
    private final static String TITLE_FIELD = "title_i18n";
    private final static String DESCRIPTION_FIELD = "description_i18n";
    private final static String REF_FIELD = "$ref";
    private final static String TYPE_FIELD = "type";
    private final static String PATTERN_PROPERTIES_FIELD = "patternProperties";
    private final static String FOLLOW_PATTERN_PROPERTIES = ".{1,}";
    private final static String ENUM_FIELD = "enum";
    private final static String PROPERTIES_FIELD = "properties";
    private final static String STRING_TYPE = "string";
    private final static String NUMBER_TYPE = "number";
    private final static String ARRAY_TYPE = "array";
    private final static String OBJECT_TYPE = "object";
    private final static String ITEMS_FIELD = "items";
    private static int COUNTER = 1;


    public void init (JsonNode mdsdNode, MeIdentification meIdentification) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.mdsd = mdsdNode;
        this.metaDataCleaned = new TreeMap<String, Object>();
        createDataFromMDSD(mdsdNode.get(PROPERTIES_FIELD).fields(), meIdentification);
    }


    public void createDataFromMDSD(Iterator<Map.Entry<String, JsonNode>> properties, MeIdentification meIdentification) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        while (properties.hasNext()) {

            Map.Entry<String, JsonNode> property = properties.next();




        }

    }
}
