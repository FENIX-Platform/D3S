package org.fao.fenix.export.d3s.legacy.md.data;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.orientechnologies.orient.core.db.record.OTrackedList;
import com.orientechnologies.orient.core.db.record.OTrackedMap;
import org.fao.fenix.commons.msd.dto.templates.standard.combined.dataset.Metadata;
import org.fao.fenix.commons.msd.dto.templates.standard.metadata.OjCode;
import org.fao.fenix.export.d3s.legacy.md.data.dto.MDSDOProperty;
import org.fao.fenix.export.d3s.legacy.md.data.dto.MDSDescriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DataCreator {

    private Map<String, Object> metaDataCleaned;

    private JsonNode mdsd;
    private final static String OJCODE_TYPE = "OjCode";
    private final static String DEFAULT_LANG = "EN";
    private static String LANG;
    private final static String REQUIRED_FIELD = "required";
    private final static String ORDER_FIELD = "propertyOrder";
    private final static String TITLE_FIELD = "title_i18n";
    private final static String TITLE_FIELD_GENERIC = "title";
    private final static String BEAN_SPECIAL_FIELD = "additions";

    private final static String DESCRIPTION_FIELD = "description_i18n";
    private final static String GET_LABEL = "getLabel";
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
    private final static String CODE_SEPARATOR = "@@";

    private static int COUNTER = 1;


    public void initDataFromMDSD(JsonNode mdsdNode, Metadata meIdentification, String language) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        LANG = language;
        this.mdsd = mdsdNode;
        Iterator<Map.Entry<String, JsonNode>> properties = mdsdNode.get(PROPERTIES_FIELD).fields();

        this.metaDataCleaned = new TreeMap<>();

        while (properties.hasNext()) {

            Map.Entry<String, JsonNode> mapDsdTmp = properties.next();
            String key = mapDsdTmp.getKey();

            MDSDOProperty objectProperty = fillObjectProperty(Lists.newArrayList(mapDsdTmp.getValue().fields()).listIterator());
            Object returnedValue = getReturnedValueFromObject(objectProperty, meIdentification, key, false);

            if (returnedValue != null && objectProperty.getTitleToVisualize() != null) {

                // 1) IF THERE IS A TYPE
                if (mapDsdTmp.getValue().get(TYPE_FIELD) != null) {

                    MDSDescriptor value = new MDSDescriptor(key, objectProperty.getTitleToVisualize(), objectProperty.getDescription());
                    String order = getOrderFromEntity(mapDsdTmp.getValue());

                    // simple case: string type or number type
                    if (isReadyToPut(objectProperty)) {
                        this.metaDataCleaned.put(order, value.setValue(returnedValue));
                    } else {
                        this.metaDataCleaned.put(order, value.setValue(handleFields(mapDsdTmp.getValue().fields(), returnedValue, key.equals(BEAN_SPECIAL_FIELD))));

                    }
                }
                // 2) REF TYPE
                else if (mapDsdTmp.getValue().get(REF_FIELD) != null) {

                    JsonNode msdRef = getMdsdObjectFromReference(mapDsdTmp.getValue().get(REF_FIELD).asText());
                    String order = getOrderFromEntity(mapDsdTmp.getValue());
                    MDSDescriptor tempVal = initDtoMDSD(mapDsdTmp.getValue(), key);
                    this.metaDataCleaned.put(order, tempVal.setValue(handleFields(msdRef.fields(), returnedValue, key.equals(BEAN_SPECIAL_FIELD))));
                }
            }
        }
    }


    private Object handleFields(Iterator<Map.Entry<String, JsonNode>> fields, Object returnedValue, boolean isSpecialField) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {


        if(isSpecialField)
            System.out.println("here");
        Map<String, Object> tempMap = new TreeMap<>();
        ListIterator<Map.Entry<String, JsonNode>> listBack = Lists.newArrayList(fields).listIterator();
        MDSDOProperty resultObj = fillObjectProperty(listBack);
        String type = resultObj.getType();

        if (type != null) {
            /** STRING OR NUMBER**/

            if (type.equals(STRING_TYPE) || type.equals(NUMBER_TYPE)) {
                Object valueStringType = getValueFromFields(resultObj.getTitleBean(), returnedValue, false, isSpecialField);
                if (valueStringType != null && !valueStringType.toString().equals("")) {
                    tempMap.put(resultObj.getOrder(), new MDSDescriptor(resultObj.getTitleBean(), resultObj.getTitleToVisualize(), resultObj.getDescription(), valueStringType));
                    return tempMap;
                }
            }

            /** OBJECT **/

            else if (type.equals(OBJECT_TYPE)) {

                // 1) PROPERTIES
                if (resultObj.getProperties() != null) {
                    ArrayList<Map.Entry<String, JsonNode>> itProperties = (ArrayList<Map.Entry<String, JsonNode>>) resultObj.getProperties();
                    handleProperties(tempMap, itProperties, returnedValue, isSpecialField);
                }

                // 2) PATTERN PROPERTIES
                else if (resultObj.getProperties() == null && resultObj.getPatternProperties() != null) {
                    Object valueObjPatternType = getValueFromFields(resultObj.getTitleBean(), returnedValue, true, isSpecialField);
                    if (valueObjPatternType != null) {

                        if (resultObj.getPatternProperties().equals(STRING_TYPE)) {
                            String orderObj = getOrderFromEntity(resultObj.getOrder());
                            tempMap.put(
                                    orderObj,
                                    new MDSDescriptor(resultObj.getTitleBean(),
                                            resultObj.getTitleToVisualize(),
                                            resultObj.getDescription(),
                                            valueObjPatternType));
                        } else {
                            // TODO(FREEZED): there is not a type different from string
                            System.out.println("here!");

                        }
                    }
                }

                // 3) REF PROPERTY
                else if (resultObj.getProperties() == null && resultObj.getPatternProperties() == null && resultObj.getReference() != null) {
                    handleReferences(resultObj.getReference(), resultObj, tempMap, returnedValue, false, isSpecialField);
                    return tempMap;
                }
            }

            /** ARRAY**/

            else if (type.equals(ARRAY_TYPE)) {

                ArrayList<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
                ArrayList<Object> values = new ArrayList<>();
                values = (returnedValue instanceof OTrackedMap) ? getObjects((OTrackedMap) returnedValue) : (ArrayList<Object>) returnedValue;


                JsonNode items = ((ObjectNode) resultObj.getItems()).deepCopy();
                if (items.get(TYPE_FIELD) != null) {
                    if (items.get(TYPE_FIELD).asText().equals(STRING_TYPE)) {
                        String orderGlobal = getOrderFromEntity(resultObj.getOrder());

                        for (int arrCounter = 0; arrCounter < values.size(); arrCounter++) {
                            Map<String, Object> tmp = new TreeMap<String, Object>();
                            String order = getOrderFromEntity(resultObj.getOrder());
                            tmp.put(order, values.get(arrCounter));
                            result.add(tmp);
                        }
                        tempMap.put(orderGlobal, result);
                    }
                } else if (items.get(REF_FIELD) != null) {
                    String orderGlobal = getOrderFromEntity(resultObj.getOrder());

                    String ref = items.get(REF_FIELD).asText();
                    String[] refSplitted = ref.substring(2).split("/");

                    // OJCODE case
                    if (refSplitted[refSplitted.length - 1].equals(OJCODE_TYPE)) {
                        tempMap.put(orderGlobal, new MDSDescriptor(
                                refSplitted[refSplitted.length - 1],
                                resultObj.getTitleToVisualize(),
                                resultObj.getDescription(),
                                fillOjCode((ArrayList<OjCode>) returnedValue)));
                    }
                    // ARRAY of references
                    else if (resultObj.getType().equals(ARRAY_TYPE) && !refSplitted[refSplitted.length - 1].equals(OJCODE_TYPE)) {


                        for (int i = 0; i < ((ArrayList<Object>) returnedValue).size(); i++) {
                            ArrayList<Object> singleComplexEntity = new ArrayList<Object>();
                            singleComplexEntity.add((Object) (((ArrayList<Object>) returnedValue).get(i)));
                            handleReferences(items.get(REF_FIELD).asText(), null, tempMap, singleComplexEntity, true, isSpecialField);
                        }
                    } else {
                        handleReferences(items.get(REF_FIELD).asText(), null, tempMap, returnedValue, true, isSpecialField);
                        return tempMap;
                    }
                }
            }
        } else {
            if (resultObj.getReference() != null) {
                handleReferences(resultObj.getReference(), resultObj, tempMap, returnedValue, false, isSpecialField);
            }
        }

        return tempMap;

    }

    private void handleReferences(String reference, MDSDOProperty objectReference, Map<String, Object> mapToFill, Object returnedValue, boolean isArray, boolean isSpecialField) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        JsonNode mdsdNode = getMdsdObjectFromReference(reference);

        if (mdsdNode.get(TYPE_FIELD) != null && mdsdNode.get(TYPE_FIELD).asText().equals(OBJECT_TYPE)) {
            if (mdsdNode.get(PROPERTIES_FIELD) != null) {
                ArrayList<Map.Entry<String, JsonNode>> itProperties = Lists.newArrayList(mdsdNode.get(PROPERTIES_FIELD).fields());

                if (isArray) {
                    ArrayList<Object> values = (ArrayList<Object>) returnedValue;
                    for (int z = 0; z < values.size(); z++) {
                        handleProperties(mapToFill, itProperties, values.get(z), isSpecialField);
                    }
                } else {
                    if (objectReference != null) {
                        objectReference.setOrder(getOrderFromEntity(objectReference.getOrder()));
                        Map<String, Object> mapToFill2 = new TreeMap<String, Object>();
                        handleProperties(mapToFill2, itProperties, returnedValue, isSpecialField);
                        mapToFill.put(objectReference.getOrder(), new MDSDescriptor(objectReference.getTitleBean(), objectReference.getTitleToVisualize(),
                                objectReference.getDescription(), mapToFill2));
                    }

/*
                    handleProperties(mapToFill, itProperties, returnedValue);
*/
                }
            }else if(isArray && isSpecialField && mdsdNode.get(PATTERN_PROPERTIES_FIELD) != null && mdsdNode.get(PATTERN_PROPERTIES_FIELD).get(FOLLOW_PATTERN_PROPERTIES)!= null && mdsdNode.get(PATTERN_PROPERTIES_FIELD).get(FOLLOW_PATTERN_PROPERTIES).get(TYPE_FIELD).asText().equals(STRING_TYPE)) {

                //TODO: to check
                if (objectReference == null)
                    objectReference = fillObjectProperty(Lists.newArrayList(mdsdNode.fields()).listIterator());
                String titleBean = reference.substring(2).split("/")!= null ?  reference.substring(2).split("/")[1] : null;
                objectReference.setTitleBean(titleBean);
                Object valueObjPatternType = getValueFromFields(objectReference.getTitleBean(), ((Map<String, Object>)((ArrayList) returnedValue).get(0)).get(titleBean), true, isSpecialField);
                if (valueObjPatternType != null) {
                    if (objectReference.getPatternProperties().equals(STRING_TYPE)) {
                        String orderObj = getOrderFromEntity(objectReference.getOrder());
                        mapToFill.put(
                                orderObj,
                                new MDSDescriptor(objectReference.getTitleBean(),
                                        objectReference.getTitleToVisualize(),
                                        objectReference.getDescription(),
                                        valueObjPatternType));
                    }
                }
            }
        } else if (mdsdNode.get(ENUM_FIELD) != null) {
            if (objectReference != null) {
                handleEnum(mapToFill, returnedValue, objectReference);
            } else {
                System.out.println("let's see");
            }
        }
    }


    public Map<String, Object> getMetaDataCleaned() {
        return metaDataCleaned;
    }


    private JsonNode getMdsdObjectFromReference(String ref) {

        String[] path = ref.substring(2).split("/");

        JsonNode tmp = null;
        JsonNode referenceMdsd = mdsd;
        for (int i = 0; i < path.length; i++) {
            tmp = referenceMdsd.get(path[i]);
            referenceMdsd = tmp;
        }

        return referenceMdsd;
    }


    private Object getValueFromFields(String fieldName, Object instanceToUse, boolean isMap, boolean isSpecialField) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if (!isSpecialField)
            return getValueFromFields(fieldName, instanceToUse, isMap);

        return (isMap) ? (((HashMap) instanceToUse).get(LANG) != null) ? ((HashMap) instanceToUse).get(LANG) : ((HashMap) instanceToUse).get(DEFAULT_LANG) : (((HashMap) instanceToUse).get(fieldName));
    }


    private Object getValueFromFields(String fieldName, Object instanceToUse, boolean isMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object result = null;
        String methodString = null;
        if (isMap) {
            methodString = "get";
            Method method = instanceToUse.getClass().getMethod(methodString, Object.class);
            result = method.invoke(instanceToUse, (Object) LANG);
            if (result == null) {
                result = method.invoke(instanceToUse, (Object) DEFAULT_LANG);
            }

        } else {
            methodString = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = instanceToUse.getClass().getMethod(methodString, null);
            result = method.invoke(instanceToUse, null);

        }

        return result;
    }


    private MDSDescriptor initDtoMDSD(JsonNode entity, String beanName) {
        String title = (entity.get(TITLE_FIELD) != null) ? entity.get(TITLE_FIELD).get(LANG.toLowerCase()).asText() : null;
        String description = (entity.get(DESCRIPTION_FIELD) != null) ? entity.get(DESCRIPTION_FIELD).get(LANG.toLowerCase()).asText() : null;
        return new MDSDescriptor(beanName, title, description);
    }


    private String getOrderFromEntity(JsonNode entity) {

        String result = (entity.get(ORDER_FIELD) != null) ? entity.get(ORDER_FIELD).asText() : 10000 + COUNTER + "";
        COUNTER++;
        return result;
    }

    private String getOrderFromEntity(String entity) {

        String result = (entity != null) ? entity : 10000 + COUNTER + "";
        COUNTER++;
        return result;
    }


    private boolean isFieldRequired(JsonNode entity) {
        return ((entity.get(REQUIRED_FIELD) != null) && (entity.get(REQUIRED_FIELD).asBoolean()));
    }


    private MDSDOProperty fillObjectProperty(ListIterator<Map.Entry<String, JsonNode>> fields) {

        MDSDOProperty result = new MDSDOProperty();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> tmp = fields.next();

            switch (tmp.getKey()) {

                case TITLE_FIELD:
                    result.setTitleToVisualize(tmp.getValue().get(LANG.toLowerCase()).asText());
                    break;

                case DESCRIPTION_FIELD:
                    result.setDescription(tmp.getValue().get(LANG.toLowerCase()).asText());
                    break;

                case PATTERN_PROPERTIES_FIELD:
                    result.setPatternProperties(tmp.getValue().get(FOLLOW_PATTERN_PROPERTIES).get(TYPE_FIELD).asText());
                    break;

                case ORDER_FIELD:
                    result.setOrder(tmp.getValue().asText());
                    break;

                case TYPE_FIELD:
                    result.setType(tmp.getValue().asText());
                    break;

                case PROPERTIES_FIELD:
                    result.setProperties(Lists.newArrayList(tmp.getValue().fields()));
                    break;

                case REF_FIELD:
                    result.setReference(tmp.getValue().asText());
                    break;

                case REQUIRED_FIELD:
                    result.setRequired(tmp.getValue().asText());
                    break;

                case ITEMS_FIELD:
                    result.setItems(tmp.getValue());
                    break;

                case TITLE_FIELD_GENERIC:
                    if (result.getTitleToVisualize() == null) {
                        result.setTitleToVisualize(tmp.getValue().asText());
                    }
                    break;
            }

        }
        return result;
    }


    private Object getReturnedValueFromObject(MDSDOProperty mdsdoProperty, Object value, String titleBean, boolean isSpecialField) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Object returnedValue = null;
        boolean isMap = mdsdoProperty.getPatternProperties() != null;

        if (isMap) {
            Object first = getValueFromFields(titleBean, value, false, isSpecialField);
            returnedValue = (first != null) ? getValueFromFields(titleBean, first, true, isSpecialField) : first;

        } else {
            returnedValue = getValueFromFields(titleBean, value, isMap, isSpecialField);
        }
        return returnedValue;
    }


    private boolean isReadyToPut(MDSDOProperty objectProperty) {

        return (objectProperty.getType() != null && (
                (objectProperty.getType().equals(STRING_TYPE) || objectProperty.getType().equals(NUMBER_TYPE)))) ||
                (objectProperty.getPatternProperties() != null && objectProperty.getPatternProperties().equals(STRING_TYPE));
    }

    private void handleProperties(Map<String, Object> mapToFill, ArrayList<Map.Entry<String, JsonNode>> itProperties, Object returnedValue, boolean isSpecialField) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        for (int k = 0; k < itProperties.size(); k++) {
            String titleBean = itProperties.get(k).getKey();


            MDSDOProperty objectProperty = fillObjectProperty(Lists.newArrayList(itProperties.get(k).getValue().fields()).listIterator());
            if (objectProperty.getTitleBean() == null) {
                objectProperty.setTitleBean(titleBean);
            }

            Object mdsdValue = getReturnedValueFromObject(objectProperty, returnedValue, titleBean, isSpecialField);

            if (mdsdValue != null) {

                // if there is not a reference
                if (objectProperty.getReference() == null) {
                    String orderObj = getOrderFromEntity(objectProperty.getOrder());
                    if (isReadyToPut(objectProperty)) {
                        mapToFill.put(orderObj,
                                new MDSDescriptor(titleBean, objectProperty.getTitleToVisualize(), objectProperty.getDescription(), mdsdValue));
                    } else if (objectProperty.getType() != null &&
                            objectProperty.getType().equals(ARRAY_TYPE) &&
                            objectProperty.getItems().get(TYPE_FIELD) != null &&
                            objectProperty.getItems().get(TYPE_FIELD).asText().equals(STRING_TYPE)) {
                        ArrayList<String> values = new ArrayList<>();

                        if (!isSpecialField) {
                            for (String s : (List<String>) mdsdValue) {
                                values.add(s);
                            }
                        } else {
                            values = getStringArray((OTrackedList) mdsdValue);
                        }
                        mapToFill.put(orderObj, new MDSDescriptor(titleBean, objectProperty.getTitleToVisualize(), objectProperty.getDescription(), values));
                    } else {
                        mapToFill.put(orderObj, new MDSDescriptor(titleBean, objectProperty.getTitleToVisualize(), objectProperty.getDescription(), handleFields(itProperties.get(k).getValue().fields(), mdsdValue, isSpecialField)));
                    }
                }
                // if it is a reference
                else {
                    handleReferences(objectProperty.getReference(), objectProperty, mapToFill, mdsdValue, false, isSpecialField);
                }
            }
        }
    }

    private ArrayList<String> fillOjCode(ArrayList<OjCode> values) {
        ArrayList<String> mapToFill = new ArrayList<>();
        if (values instanceof OTrackedList) {
            fillCodes((OTrackedList) values, mapToFill);
        } else {
            for (int h = 0; h < values.size(); h++) {
                String value = (values.get(h).getLabel() != null) ? values.get(h).getLabel().get(LANG.toUpperCase()) : "";

                if (value == null) {
                    value = (values.get(h).getLabel() != null) ? values.get(h).getLabel().get(DEFAULT_LANG.toUpperCase()) : "";
                }
                mapToFill.add(values.get(h).getCode() + " "+CODE_SEPARATOR+" " + value);
            }
        }
        return mapToFill;
    }

    private void fillCodes(OTrackedList values, ArrayList<String> mapToFill) {
        for (Object element : values) {
            String value = ((OTrackedMap) element).get("label") != null ? ((LinkedHashMap) ((OTrackedMap) element).get("label")).get(LANG.toUpperCase()).toString() : "";
            if (value == null)
                value = ((OTrackedMap) element).get("label") != null ? ((LinkedHashMap) ((OTrackedMap) element).get("label")).get(DEFAULT_LANG.toUpperCase()).toString() : "";
            mapToFill.add(((OTrackedMap) element).get("code").toString() + " "+CODE_SEPARATOR+" " + value);
        }
    }


    private void handleEnum(Map<String, Object> mapToFill, Object returnedValue, MDSDOProperty reference) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String value = (String) invokeEnumByReflection(returnedValue);
        String order = (reference != null) ? getOrderFromEntity(reference.getOrder()) : "2000";
        mapToFill.put(order, createMDSDDescriptor(value, reference));
    }


    private Object invokeEnumByReflection(Object instanceToUse) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Object resultTemp = null;
        Object result = null;
        String methodStringMAp = null;
        Method method = instanceToUse.getClass().getMethod(GET_LABEL, null);
        resultTemp = method.invoke(instanceToUse, null);
        // now the map

        methodStringMAp = "get";
        Method methodMap = resultTemp.getClass().getMethod(methodStringMAp, Object.class);
        result = methodMap.invoke(resultTemp, (Object) LANG);

        if (result == null) {
            result = methodMap.invoke(resultTemp, (Object) DEFAULT_LANG);
        }

        return result.toString();
    }


    private MDSDescriptor createMDSDDescriptor(String value, MDSDOProperty property) {
        if (property != null) {
            return new MDSDescriptor(property.getTitleBean(), property.getTitleToVisualize(), property.getDescription(), value);
        } else {
            return new MDSDescriptor(null, null, null, value);
        }
    }


    private ArrayList<String> getStringArray(OTrackedList list) {
        ArrayList<String> result = new ArrayList<>();
        for (Object element : list) {
            String elementString = ((OTrackedMap) element).get(((OTrackedMap) element).keySet().iterator().next()) != null ? ((OTrackedMap) element).get(((OTrackedMap) element).keySet().iterator().next()).toString() : null;
            result.add(elementString);
        }
        return result;
    }


    private ArrayList<Object> getObjects(OTrackedMap values) {
        ArrayList<Object> result = new ArrayList<>();

        for (Object element : values.keySet())
            result.add(values.get(element));
        return result;
    }


}








