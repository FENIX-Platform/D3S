package org.fao.fenix.d3s.msd.dao.dm;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.fao.fenix.commons.msd.dto.full.common.Period;
import org.fao.fenix.commons.msd.dto.type.dsd.DSDDataType;
import org.fao.fenix.d3s.server.tools.SupportedLanguages;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.commons.msd.utils.DataUtils;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.dictionary.ODictionary;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;

public class DMIndexStore extends OrientDao {
    private static Set<String> UNINDEXED_FIELDS = new HashSet<String>();
    static {
        UNINDEXED_FIELDS.add("in");
        UNINDEXED_FIELDS.add("out");
        UNINDEXED_FIELDS.add("freeExtension");
        UNINDEXED_FIELDS.add("dsd");
    }

    @Inject private DMLoad dmLoadDAO;
	@Inject private DMConverter dmConverter;


    //Remove
    public int dropIndexDatasetMetadata(String uid) throws Exception {
        ODocument dmO = dmLoadDAO.loadDatasetMetadataO(uid);
        return dropIndexDatasetMetadata(dmO);
    }
    public int dropIndexDatasetMetadata(ODocument dmO) throws Exception {
        if (dmO == null)
            return 0;

        OClass dmClassO = dmO.getSchemaClass();
        OProperty property;
        for (String fieldName : dmO.fieldNames())
            if (fieldName.startsWith("index_") && (property=dmClassO.getProperty(fieldName))!=null)
                    dmO.field(fieldName, null, property.getType());

        dmO.save();
        return 1;
    }



    //Store index data
    public int indexDatasetMetadata(String uid) throws Exception {
        ODocument dmO = dmLoadDAO.loadDatasetMetadataO(uid);
        return indexDatasetMetadata(dmO,true);
    }
    public int indexDatasetMetadata(ODocument dmO, boolean rebuild) throws Exception {
        if (dmO == null)
            return 0;

        OGraphDatabase database = getConnection();

        //Temporary variables
        OClass dmClassO = dmO.getSchemaClass();
        ODictionary msdDictionary = database.getDictionary();
        OType propertyType;
        Object propertyValue;
        String indexedFieldName;

        //Reset index data
        dropIndexDatasetMetadata(dmO);

        //Create indexed meta data
        for (String fieldName : dmO.fieldNames())
            if (msdDictionary.containsKey(indexedFieldName = getIndexedFieldName(fieldName)) && (propertyValue=dmO.field(fieldName))!=null) {
                propertyType=dmClassO.getProperty(fieldName).getType();
                if (propertyType==OType.EMBEDDEDMAP) {
                    dmO.field(indexedFieldName, filterIndexText(getFlatText((Map<String,String>)propertyValue)));
                    for (Map.Entry<String,String> textEntry : ((Map<String,String>)propertyValue).entrySet())
                        dmO.field(indexedFieldName+'_'+textEntry.getKey(), filterIndexText(textEntry.getValue()));
                } else if (propertyType==OType.EMBEDDED && dmClassO.getProperty(fieldName).getLinkedClass().getName().equals("CMPeriod")) {
                        Period period = dmConverter.toPeriod((ODocument)propertyValue);
                        dmO.field(indexedFieldName+"_from", period.getFrom());
                        dmO.field(indexedFieldName+"_to", period.getTo());
                } else if (propertyType==OType.LINK && dmClassO.getProperty(fieldName).getLinkedClass().getName().equals("CSCode")) {
                        Collection<ODocument> loadedCodesO = new LinkedList<ODocument>();
                        getParentCodes((ODocument)propertyValue,loadedCodesO);
                        dmO.field(indexedFieldName, loadedCodesO);
                } else if (propertyType==OType.LINKLIST && dmClassO.getProperty(fieldName).getLinkedClass().getName().equals("CSCode")) {
                        Collection<ODocument> loadedCodesO = new LinkedList<ODocument>();
                        getParentCodes((Collection<ODocument>)propertyValue,loadedCodesO);
                        dmO.field(indexedFieldName, loadedCodesO);
                } else {
                        dmO.field(indexedFieldName, propertyValue, propertyType);
                }
            }

        //Create indexed dimensions data
        Collection<ODocument> columnsO = dmO.field("dsd.columns");
        Collection<ODocument> codesO;
        if (columnsO!=null)
            for (ODocument columnO : columnsO) {
                Collection<?> valuesBuffer = columnO.field("values");
                if (valuesBuffer!=null && valuesBuffer.size()>0) {
                    createDimensionIndexStructure(columnO);

                    DSDDataType fieldType = DSDDataType.getByCode((String) columnO.field("datatype"));
                    indexedFieldName = getIndexedDimensionName((String)columnO.field("dimension.name"),fieldType);
                    OType oType = OType.EMBEDDEDLIST;
                    Map<String, Collection<?>> values = new HashMap<String, Collection<?>>();
                    switch (fieldType) {
                        case year:
                        case month:
                        case date:
                            values.put(indexedFieldName, DataUtils.dateToNumber(valuesBuffer,fieldType));
                            break;
                        case number:
//                            linkedType = OType.DOUBLE;
                        case text:
//                            linkedType = OType.STRING;
                            values.put(indexedFieldName, valuesBuffer);
                            break;
                        case customCode:
                            Collection<String> codes = new LinkedList<String>();
                            for (Object v : valuesBuffer) {
                                String code = (String)((Map<String,Object>)v).get("code");
                                if (code==null)
                                    throw new Exception("Incorrect custom code found in column values property");
                                codes.add(code);
                            }
                            values.put(indexedFieldName,codes);
                            break;
                        case iText:
                            values.put(indexedFieldName,new LinkedList<String>());
                            for (SupportedLanguages language : SupportedLanguages.values())
                                values.put(indexedFieldName+'_'+language.getCode(),new LinkedList<String>());

                            for (Object v : valuesBuffer) {
                                Map<String,String> value = toStringMap((Map<String,Object>)v);
                                ((Collection<String>)values.get(indexedFieldName)).add(filterIndexText(getFlatText(value)));
                                for (Map.Entry<String,String> textEntry : value.entrySet())
                                    ((Collection<String>)values.get(indexedFieldName+'_'+textEntry.getKey())).add(filterIndexText(textEntry.getValue()));
                            }
                            break;
                        case code:
                            oType = OType.LINKLIST;
                            Collection<ODocument> loadedCodesO = new LinkedList<ODocument>();
                            codesO = new LinkedList<ODocument>();
                            if (valuesBuffer!=null)
                                for (Object value : valuesBuffer)
                                    codesO.add((ODocument)database.load((ORID)value));

                            getParentCodes(codesO, loadedCodesO);

                            values.put(indexedFieldName, loadedCodesO);
                            break;
                        case document:
                            oType = OType.LINKLIST;
                            break;
                        default:
                            throw new Exception("Indexing error: unsupported field type "+fieldType);
                    }

                    for (Map.Entry<String,Collection<?>> i : values.entrySet())
                        dmO.field(i.getKey(), i.getValue(), oType);
                }
            }

        //Rebuild indexes
        if (rebuild) {
            dmO.save();
            rebuildIndexes();
        }

        //Return
        return 1;
    }


    //Rebuild metadata indexes
    public void rebuildIndexes () throws Exception {
        for (OProperty indexProperty : getConnection().getMetadata().getSchema().getClass("DMMain").properties()) {
            Collection<OIndex<?>> indexes = indexProperty.getName().startsWith("index_") ? indexProperty.getAllIndexes() : null;
            if (indexes!=null)
                for (OIndex<?> index : indexes)
                    index.rebuild();
        }
    }


     //Create index structure
    //Update descriptive dataset metadata (from DMMain class)
     public void createDynamicIndexStructure() {
         OGraphDatabase database = getConnection();

         ODictionary<?> msdDictionary = database.getDictionary();
         OSchema schema = database.getMetadata().getSchema();
         OClass mainClass = schema.getClass("DMMain");
         String fieldName, indexedFieldName;
         OProperty newProperty;
         for (OProperty fieldStructure : mainClass.properties()) {
             fieldName = fieldStructure.getName();
             indexedFieldName = getIndexedFieldName(fieldName);
             if (!msdDictionary.containsKey(indexedFieldName) && !fieldName.startsWith("index_") && !UNINDEXED_FIELDS.contains(fieldName)) {
                 OClass linkedClass = fieldStructure.getLinkedClass();
                 OType linkedType = fieldStructure.getLinkedType();
                 switch (fieldStructure.getType()) {
                     case EMBEDDEDMAP:
                         newProperty = mainClass.createProperty(indexedFieldName,OType.STRING);
                         newProperty.createIndex(OClass.INDEX_TYPE.FULLTEXT);
                         for (SupportedLanguages language : SupportedLanguages.values()) {
                             newProperty = mainClass.createProperty(indexedFieldName+'_'+language.getCode(),OType.STRING);
                             newProperty.createIndex(OClass.INDEX_TYPE.FULLTEXT);
                         }
                         break;
                     case EMBEDDED:
                         if (linkedClass!=null && "CMPeriod".equals(linkedClass.getName())) {
                             newProperty = mainClass.createProperty(indexedFieldName+"_from",OType.DATE);
                             newProperty.createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
                             newProperty = mainClass.createProperty(indexedFieldName+"_to",OType.DATE);
                             newProperty.createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
                         }
                         break;
                     case LINK:
                         if (linkedClass!=null && "CSCode".equals(linkedClass.getName())) {
                             newProperty = mainClass.createProperty(indexedFieldName, OType.LINKLIST, linkedClass);
                             newProperty.createIndex(OClass.INDEX_TYPE.NOTUNIQUE);
                             break;
                         }
                     default:
                         if (linkedClass!=null)
                            newProperty = mainClass.createProperty(indexedFieldName, fieldStructure.getType(), fieldStructure.getLinkedClass());
                         else if (linkedType!=null)
                             newProperty = mainClass.createProperty(indexedFieldName, fieldStructure.getType(), fieldStructure.getLinkedType());
                         else
                             newProperty = mainClass.createProperty(indexedFieldName, fieldStructure.getType());
                         newProperty.createIndex(fieldName.equals("uid") ? OClass.INDEX_TYPE.UNIQUE : OClass.INDEX_TYPE.NOTUNIQUE);
                 }

                 msdDictionary.put(indexedFieldName, toRID("1_0"));
             }
         }
         schema.save();
     }

    //Update structural dataset metadata (from DMMain class)
     public int createDimensionIndexStructure(ODocument columnO) throws Exception {
         OGraphDatabase database = getConnection();

         ODictionary<?> msdDictionary = database.getDictionary();
         DSDDataType fieldType = DSDDataType.getByCode((String) columnO.field("datatype"));
         String fieldName = getIndexedDimensionName((String)columnO.field("dimension.name"),fieldType);

         if (!msdDictionary.containsKey(fieldName)) {
             OSchema schema = database.getMetadata().getSchema();
             OClass mainClass = schema.getClass("DMMain");
             Collection<String> fieldNames = new LinkedList<String>();
             fieldNames.add(fieldName);
             OProperty newProperty;
             OClass linkedClass = null;
             OType linkedType = null;
             OType oType = OType.EMBEDDEDLIST;
             OClass.INDEX_TYPE indexType = OClass.INDEX_TYPE.NOTUNIQUE;

             switch (fieldType) {
                 case year:
                 case month:
                 case date:
                     linkedType = OType.LONG;
                     break;
                 case number:
                     linkedType = OType.DOUBLE;
                     break;
                 case customCode:
                     linkedType = OType.STRING;
                     break;
                 case iText:
                     linkedType = OType.STRING;
                     indexType = OClass.INDEX_TYPE.FULLTEXT;
                     for (SupportedLanguages language : SupportedLanguages.values())
                         fieldNames.add(fieldName+'_'+language.getCode());
                     break;
                 case text:
                     linkedType = OType.STRING;
                     indexType = OClass.INDEX_TYPE.FULLTEXT;
                     break;
                 case code:
                     oType = OType.LINKLIST;
                     linkedClass = database.getMetadata().getSchema().getClass("CSCode");
                     break;
                 case document:
                     oType = OType.LINKLIST;
                     break;
                 default:
                     throw new Exception("Indexing error: unsupported field type '"+fieldType+"'");
             }

             for (String name : fieldNames) {
                 if (linkedClass!=null)
                     newProperty = mainClass.createProperty(name,oType,linkedClass);
                 else if (linkedType!=null)
                    newProperty = mainClass.createProperty(name,oType,linkedType);
                 else
                    newProperty = mainClass.createProperty(name,oType);

                 if (indexType!=null)
                    newProperty.createIndex(indexType);
             }

             msdDictionary.put(fieldName, toRID("1_0"));
             schema.save();
         }

         return 1;
     }


    //CLEAN
    public void removeIndexes () throws Exception {
        OGraphDatabase database = getConnection();

        ODictionary<?> msdDictionary = database.getDictionary();
        OSchema schema = database.getMetadata().getSchema();
        OClass mainClass = schema.getClass("DMMain");
        Collection<String> propertiesName = new LinkedList<String>();
        Map<String,String> propertiesError = new HashMap<String, String>();

        for (OProperty fieldStructure : mainClass.properties())
            if (fieldStructure.getName().startsWith("index_"))
                propertiesName.add(fieldStructure.getName());

        for (String propertyName : propertiesName) {
            try {
                database.command(new OCommandSQL("delete from index:DMMain."+propertyName)).execute();
                database.command(new OCommandSQL("drop index DMMain."+propertyName)).execute();
            } catch (Exception ex) {
                propertiesError.put(propertyName,ex.getMessage());
            }
            try {
                mainClass.dropProperty(propertyName);
            } catch (Exception ex) {
                propertiesError.put(propertyName,ex.getMessage());
            }
        }

        propertiesName.clear();
        for (Object i : msdDictionary.keys())
            if (i.toString().startsWith("index_"))
                propertiesName.add(i.toString());
        for (String propertyName : propertiesName)
            msdDictionary.remove(propertyName);

        schema.save();

        if (propertiesError.size()>0) {
            StringBuilder error = new StringBuilder("Error on indexed properties remove:");
            for (Map.Entry<String,String> propertyError : propertiesError.entrySet())
                error.append('\n').append(propertyError.getKey()).append(": ").append(propertyError.getValue());
            throw new Exception(error.toString());
        }
    }




    // Utils
     private final static int MIN_WORD_LENGTH = 4;
     private final static Pattern wordPattern = Pattern.compile("\\w{"+MIN_WORD_LENGTH+",}");
     private final static Pattern htmlPattern = Pattern.compile("/n|<(\"[^\"]*\"|'[^']*'|[^'\">])*>");
     public static String filterIndexText(String text) {
         StringBuilder buffer = new StringBuilder();
         if (text!=null)
             for (Matcher m = wordPattern.matcher(htmlPattern.matcher(text).replaceAll(" "));m.find();)
                 buffer.append(m.group()).append(' ');
         return buffer.toString();
     }

     private String getFlatText (Map<String,String> value) {
        StringBuilder buffer = new StringBuilder();
        for (String text : value.values())
            if (text!=null)
                buffer.append(text).append('\n');
        return buffer.toString();
    }
    private Map<String,String> toStringMap(Map<String,Object> value) {
        if (value==null) return null;
        Map<String,String> strValue = new HashMap<String, String>();
        for (Map.Entry<String,Object> i : value.entrySet())
            if (i.getValue()!=null)
                strValue.put(i.getKey(),i.getValue().toString());
        return strValue;
    }
	
     private void getParentCodes (ODocument codeO, Collection<ODocument> loadedCodesO) throws Exception {
		if (codeO!=null) {
                loadedCodesO.add(codeO);
                getParentCodes((Collection<ODocument>) codeO.field("parents"), loadedCodesO);
        }
	}
	
     private Collection<ODocument> getParentCodes (Collection<ODocument> codesO, Collection<ODocument> loadedCodesO) throws Exception {
		if (codesO!=null)
            for (ODocument codeO : codesO)
                getParentCodes(codeO, loadedCodesO);
		return loadedCodesO;
	}
    
     public String getIndexedDimensionName(String dimensionName, DSDDataType dataType) {
         return "index_dim_"+dimensionName+'_'+dataType.getCode();
    }
     public String getIndexedFieldName(String fieldName) {
         return "index_"+fieldName;
    }
}
