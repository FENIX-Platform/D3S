package org.fao.fenix.d3s.msd.listeners;

import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.apache.log4j.Logger;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.ResponsiblePartyRole;
import org.fao.fenix.d3s.msd.listener.ResourceListener;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@ApplicationScoped
public class FenixIndexer implements ResourceListener {
    private static final Logger LOGGER = Logger.getLogger("info");

    private final static String[] indexedFields = new String[]{
            "dsd.contextSystem",
            "dsd.userName",
            "meContent.resourceRepresentationType",
            "meContent.seCoverage.coverageSectors",
            "meContent.seCoverage.coverageGeographic",
            "meContent.seCoverage.coverageTime",
            "meContent.seReferencePopulation.referencePeriod",
            "meContent.seReferencePopulation.referenceArea",
            "meAccessibility.seConfidentiality.confidentialityStatus",
            "meSpatialRepresentation.seBoundingBox.seVectorSpatialRepresentation.topologyLevel",
            "meSpatialRepresentation.processing",
            "meSpatialRepresentation.layerType",
            "meReferenceSystem.seProjection.projection"
    };


    private @Inject DatabaseStandards crossReferences;


    //Events
    @Override
    public void insertingMetadata(MeIdentification metadata) {}

    @Override
    public void insertedMetadata(MeIdentification metadata) {
        updateIndexInfo(metadata);
    }

    @Override
    public void updatingMetadata(MeIdentification metadata) {}

    @Override
    public void updatedMetadata(MeIdentification metadata) {
        updateIndexInfo(metadata);
    }

    @Override
    public void appendingMetadata(MeIdentification metadata) {}

    @Override
    public void appendedMetadata(MeIdentification metadata) {
        updateIndexInfo(metadata);
    }

    @Override
    public void removingMetadata(MeIdentification metadata) {}

    @Override
    public void removedMetadata(String uid, String version) {
        removeIndexInfo(uid, version);
    }

    @Override
    public void insertingResource(Resource resource) {}

    @Override
    public void insertedResource(MeIdentification metadata) {
        updateIndexInfo(metadata);
    }

    @Override
    public void updatingResource(Resource resource) {
    }

    @Override
    public void updatedResource(MeIdentification metadata) {
        updateIndexInfo(metadata);
    }

    @Override
    public void appendingResource(Resource resource) {}

    @Override
    public void appendedResource(MeIdentification metadata) {
        updateIndexInfo(metadata);
    }

    @Override
    public void removingResource(MeIdentification metadata) {}

    @Override
    public void removedResource(String uid, String version) {
        removeIndexInfo(uid, version);
    }

    @Override
    public <T extends DSD> void updatingDSD(T dsd, MeIdentification metadata) {}

    @Override
    public <T extends DSD> void updatedDSD(MeIdentification metadata) {
        updateIndexInfo(metadata);
    }

    @Override
    public <T extends DSD> void appendingDSD(T dsd, MeIdentification metadata) {}

    @Override
    public <T extends DSD> void appendedDSD(MeIdentification metadata) {
        updateIndexInfo(metadata);
    }

    @Override
    public <T extends DSD> void removingDSD(MeIdentification metadata) {}

    @Override
    public <T extends DSD> void removedDSD(MeIdentification metadata) {
        updateIndexInfo(metadata);
    }

    @Override
    public void removingData(MeIdentification metadata) {}

    @Override
    public void removedData(MeIdentification metadata) {}

    @Override
    public void updatingData(MeIdentification metadata) {}

    @Override
    public void updatedData(MeIdentification metadata) {}







    //LOGIC
    private void removeIndexInfo(String uid, String version) {
        try {
            OObjectDatabaseTx connection = crossReferences.getConnection();
            connection.command(new OCommandSQL("delete from MetadataIndex where index|id = ?")).execute(getId(uid,version));
            connection.commit();
        } catch (Exception ex) {
            LOGGER.error("Error trying to index FENIX metadata", ex);
        }
    }

    private void updateIndexInfo(MeIdentification metadata) {
        try {
            OObjectDatabaseTx connection = crossReferences.getConnection();
            init(connection);

            ODocument indexDocument = getIndexDocument(getId(metadata), connection);
            updateFields(metadata, indexDocument, connection);

            connection.getUnderlying().save(indexDocument);
        } catch (Exception ex) {
            LOGGER.error("Error trying to index FENIX metadata", ex);
        }
    }

    private void updateFields(MeIdentification metadata, ODocument indexDocument, OObjectDatabaseTx connection) throws Exception {
        ODocument document = connection.getRecordByUserObject(metadata, false);
        //ID
        String uid = document!=null ? (String)document.field("uid") : null;
        if (uid != null) {
            indexDocument.field("index|id", getId(uid, (String)document.field("version")));

            //Freetext search support
            indexDocument.field("index|freetext", getFreeTextValue(document));

            //Other standard fields
            for (int i = 0; i < indexedFields.length; i++) {            //Indexing standard properties
                String fieldName = indexedFields[i].replace('.', '|');
                FieldType fieldType = fieldTypes[i];
                Collection fieldValues = getFields(document, indexedFields[i]);
                Long[] period;

                switch (fieldType) {
                    case enumeration:
                        indexDocument.field("index|" + fieldName, fieldValues != null && fieldValues.size() > 0 ? fieldValues.iterator().next() : null, OType.STRING);
                        break;
                    case OjPeriod:
                        ODocument periodO = fieldValues != null && fieldValues.size() > 0 ? (ODocument) fieldValues.iterator().next() : null;
                        if (periodO != null) {
                            period = datePeriodToPeriod((Date) periodO.field("from"), (Date) periodO.field("to"));
                            indexDocument.field("index|" + fieldName + "|from", period[0], OType.LONG);
                            indexDocument.field("index|" + fieldName + "|to", period[1], OType.LONG);
                        }
                        break;
                    case date:
                        Date date = fieldValues != null && fieldValues.size() > 0 ? (Date) fieldValues.iterator().next() : null;
                        if (date != null) {
                            period = dateToPeriod(date);
                            indexDocument.field("index|" + fieldName + "|from", period[0], OType.LONG);
                            indexDocument.field("index|" + fieldName + "|to", period[1], OType.LONG);
                        }
                    case OjCodeList:
                    case OjCodeListCollection:
                        Collection<String> codes = fieldValues != null && fieldValues.size() > 0 ? getCodes(fieldValues) : null;
                        indexDocument.field("index|" + fieldName, codes != null && codes.size() > 0 ? codes : null, OType.EMBEDDEDLIST, OType.STRING);
                        break;
                    case OjResponsibleParty:
                    case OjResponsiblePartyCollection:
                        for (Map.Entry<String, String> contactEntry : ((Map<String, String>) getContacts(fieldValues)).entrySet())
                            indexDocument.field("index|" + fieldName + '|' + contactEntry.getKey(), contactEntry.getValue(), OType.STRING);
                        break;
                    case other:
                        System.out.println("Undefined index type for " + fieldName);
                        break;
                }
            }
        }

    }



    //INIT

    private FieldType[] fieldTypes = null;
    private enum FieldType {
        OjCodeList,OjCodeListCollection,
        OjResponsibleParty,OjResponsiblePartyCollection,
        OjPeriod,
        date,
        enumeration,
        other
    }

    private Map<String,Integer> indexedFieldsIndex = new HashMap<>();

    private boolean initialized = false;
    private void init(OObjectDatabaseTx objectConnection) throws Exception {
        if (!initialized) {
            OClass meIdentityClassO = objectConnection.getMetadata().getSchema().getClass(MeIdentification.class.getSimpleName());
            for (int i = 0; i < indexedFields.length; i++)
                indexedFieldsIndex.put(indexedFields[i], i);

            setFieldTypes(meIdentityClassO);

            initialized = true;
        }
    }

    private void setFieldTypes(OClass meIdentityClassO) {
        fieldTypes = new FieldType[indexedFields.length];
        for (int i=0; i<indexedFields.length; i++) {
            OProperty property = getProperty(meIdentityClassO, indexedFields[i].split("\\."));
            if (property!=null) {
                OType type = property.getType();
                OClass linkedClass = property.getLinkedClass();
                switch (type) {
                    case EMBEDDED:
                    case LINK:
                        fieldTypes[i] = linkedClass!=null ? FieldType.valueOf(linkedClass.getName()) : null;
                        break;
                    case EMBEDDEDLIST:
                    case EMBEDDEDSET:
                    case LINKLIST:
                    case LINKSET:
                        fieldTypes[i] = linkedClass!=null ? FieldType.valueOf(linkedClass.getName()+"Collection") : null;
                        break;
                    case DATE:
                    case DATETIME:
                        fieldTypes[i] = FieldType.date;
                        break;
                    case STRING:
                        fieldTypes[i] = FieldType.enumeration;
                        break;
                    default:
                        fieldTypes[i] = FieldType.other;
                }
            }
        }

    }




    //Internal usage
    SimpleDateFormat dayFormatter = new SimpleDateFormat("yyyyMMdd000000");
    private static Long maxSecond = 99991231235959l;
    private static Long minSecond = 10000101000000l;

    private Long[] dateToPeriod (Date date) {
        Long[] period = new Long[2];
        period[0] = date!=null ? new Long(dayFormatter.format(date)) : null;
        period[1] = period[0]!=null ? period[0] + 235959 : null;
        return period;
    }
    private Long[] datePeriodToPeriod(Date from, Date to) {
        Long[] period = new Long[2];
        period[0] = from!=null ? dateToPeriod(from)[0] : minSecond;
        period[1] = to!=null ? dateToPeriod(to)[1] : maxSecond;
        return period;
    }

    private String getFreeTextValue(ODocument meIdentification) throws Exception {
        //Retrieve free text data
        Collection<String> uid = getFields(meIdentification, "uid");
        Collection<String> version = getFields(meIdentification, "version");
        Collection<Map<String, String>> title = getFields(meIdentification, "title");
        Collection<Map<String, String>> description = getFields(meIdentification, "meContent.description");
        Collection<String> keywords = getFields(meIdentification, "meContent.keywords");
        Collection<String> type = getFields(meIdentification, "meContent.resourceRepresentationType");
        Map<String,Map<String,Map<String,Map<String,String>>>> coverageGeographicLabels = getCodesLabel(getFields(meIdentification, "meContent.seCoverage.coverageGeographic"));
        Map<String,String> contacts = getContacts(getFields(meIdentification, "contacts"));

        //Build text
        StringBuilder textBuilder = new StringBuilder(" ");
        for (String t : uid)
            if (t!=null)
                textBuilder.append(t).append(' ');
        for (String t : version)
            if (t!=null)
                textBuilder.append(t).append(' ');
        for (Map<String, String> l : title)
            for (String t : l.values())
                if (t!=null)
                    textBuilder.append(t).append(' ');
        for (Map<String, String> l : description)
            for (String t : l.values())
                if (t!=null)
                    textBuilder.append(t).append(' ');
        for (String t : keywords)
            if (t!=null)
                textBuilder.append(t).append(' ');
        for (String t : type)
            if (t!=null)
                textBuilder.append(t).append(' ');
        for (Map<String,Map<String,Map<String,String>>> cl : coverageGeographicLabels.values())
            for (Map<String,Map<String,String>> c : cl.values())
                for (Map<String,String> l : c.values())
                    for (String t : l.values())
                        if (t!=null)
                            textBuilder.append(t).append(' ');
        for (String t : contacts.values())
            if (t!=null)
                textBuilder.append(t).append(' ');

        //Return normalized text
        return textBuilder.toString().replaceAll("\\s.{1,3}\\s"," ").replaceAll("\\s+"," ").replaceAll("^\\s","").replaceAll("\\s$","");
    }

    private OProperty getProperty(OClass classO, String[] propertyName) {
        OProperty property = null;
        for (int i=0; i<propertyName.length; i++) {
            property = classO.getProperty(propertyName[i]);
            if (property!=null) {
                OType propertyType = property.getType();
                if (    propertyType==OType.EMBEDDED || propertyType==OType.EMBEDDEDLIST || propertyType==OType.EMBEDDEDSET ||
                        propertyType==OType.LINK || propertyType==OType.LINKLIST || propertyType==OType.LINKSET)
                    classO = property.getLinkedClass();
                else if (i<propertyName.length-1)
                    return null;
            }
        }
        return property;
    }

    //OJCodelist codes extraction
    private Collection<String> getCodes(Collection<ODocument> ojCodelistCollectionO) throws Exception {
        Collection<String> codes = new LinkedList<>();
        if (ojCodelistCollectionO!=null)
            for (ODocument ojCodelistO : ojCodelistCollectionO)
                codes.addAll(getCodes(ojCodelistO));
        return codes;
    }
    private Collection<String> getCodes(ODocument ojCodelistO) throws Exception {
        Collection<String> codes = new LinkedList<>();
        if (ojCodelistO!=null) {
            String uid = ojCodelistO.field("idCodeList");
            String version = ojCodelistO.field("version");
            if (uid!=null) {
                String codeListID = uid + '|' + (version!=null ? version : "");
                Collection<ODocument> ojCodesO = ojCodelistO.field("codes");
                if (ojCodesO!=null && ojCodesO.size()>0)
                    for (ODocument ojCodeO : ojCodesO) {
                        ODocument linkedCodeO = null;
                        try {
                            linkedCodeO = ojCodeO.field("linkedCode");
                        } catch (ClassCastException ex) {
                            ojCodeO.field("linkedCode",null,OType.LINK); //if linked code is a broken link the field method returns an ORID object
                        }
                        if (linkedCodeO!=null)
                            addParentsToo(linkedCodeO, codes, codeListID);
                        else {
                            String code = ojCodeO.field("code");
                            if (code != null)
                                codes.add(codeListID + '|' + code);
                        }
                    }
                codes.add(codeListID);
            }
        }
        return codes;
    }
    private void addParentsToo(ODocument code, Collection<String> codes, String codeListID) {
        codes.add(codeListID + '|' + code.field("code"));
        Collection<ODocument> parents = code.field("parents");
        if (parents!=null)
            for (ODocument parent : parents)
                addParentsToo(parent, codes, codeListID);
    }


    //OJCodelist labels extraction
    //Return: Map< codelistId, Map< code, Map< field, Map< language, text >>>>
    private Map<String,Map<String,Map<String,Map<String,String>>>> getCodesLabel(Collection<ODocument> ojCodelistCollectionO) throws Exception {
        Map<String,Map<String,Map<String,Map<String,String>>>> codes = new HashMap<>();
        if (ojCodelistCollectionO!=null)
            for (ODocument ojCodelistO : ojCodelistCollectionO) {
                String uid = ojCodelistO.field("idCodeList");
                String version = ojCodelistO.field("version");
                codes.put(uid + '|' + (version!=null ? version : ""), getCodesLabel(ojCodelistO));
            }
        return codes;
    }
    private Map<String,Map<String,Map<String,String>>> getCodesLabel(ODocument ojCodelistO) throws Exception {
        Map<String,Map<String,Map<String,String>>> codes = new HashMap<>();
        if (ojCodelistO!=null) {
            Collection<ODocument> ojCodesO = ojCodelistO.field("codes");
            if (ojCodesO!=null)
                for (ODocument ojCodeO : ojCodesO)
                    try {
                        ODocument linkedCodeO = ojCodeO.field("linkedCode");
                        if (linkedCodeO != null) {
                            Map<String,Map<String,String>> code = new HashMap<>();
                            codes.put((String)linkedCodeO.field("code"), code);

                            Map<String, String> label = linkedCodeO.field("shortTitle");
                            if (label!=null)
                                code.put("shortTitle",label);
                            label = linkedCodeO.field("title");
                            if (label!=null)
                                code.put("title",label);
                            label = linkedCodeO.field("description");
                            if (label!=null)
                                code.put("description",label);
                            label = linkedCodeO.field("supplemental");
                            if (label!=null)
                                code.put("supplemental",label);
                        }
                    } catch (ClassCastException ex) {
                        ojCodeO.field("linkedCode", null, OType.LINK); //if linked code is a broken link the field method returns an ORID object
                    }
        }
        return codes;
    }

    //OJResponsibleParty label extraction

    private Map<String, String> getContacts(Collection<ODocument> ojResponsiblePartyCollection) throws Exception {
        Map<String, String> contactsMap = new HashMap<>();
        for (ResponsiblePartyRole contactType : ResponsiblePartyRole.values())
            contactsMap.put(contactType.toString(), null);

        if (ojResponsiblePartyCollection!=null)
            for (ODocument ojResponsiblePartyO : ojResponsiblePartyCollection) {
                String contactType = ojResponsiblePartyO.field("role");
                String contact = contactType!=null ? getContact(ojResponsiblePartyO) : null;
                if (contact!=null) {
                    String contacts = contactsMap.get(contactType);
                    contactsMap.put(contactType, contacts!=null ? contacts+' '+contact : contact);
                }
            }
        return contactsMap;
    }
    private String getContact(ODocument ojResponsiblePartyO) throws Exception {
        StringBuilder buffer = new StringBuilder();
        if (ojResponsiblePartyO!=null) {

            String pointOfContact = ojResponsiblePartyO.field("pointOfContact");
            if (pointOfContact!=null)
                buffer.append(' ').append(pointOfContact);

            appendLabel(buffer, (Map<String,String>)ojResponsiblePartyO.field("organization"));
            appendLabel(buffer, (Map<String,String>)ojResponsiblePartyO.field("organizationUnit"));
            appendLabel(buffer, (Map<String,String>)ojResponsiblePartyO.field("position"));
            appendLabel(buffer, (Map<String, String>) ojResponsiblePartyO.field("specify"));
        }

        String contact = buffer.toString().trim();
        return contact.length()>0 ? contact : null;
    }
    private void appendLabel (StringBuilder buffer, Map<String, String> label) {
        if (label!=null)
            for (String labelValue : label.values())
                if (labelValue!=null)
                    buffer.append(' ').append(labelValue);
    }




    //Utils
    public Map<String, Integer> getIndexedFieldsIndex() {
        return indexedFieldsIndex;
    }
    protected Collection getFields(ODocument root, String path) {
        return root!=null && path!=null ? getFields(root,path.split("\\."),-1,new LinkedList<ODocument>()) : null;
    }
    private Collection getFields(Object field, String[] path, int index, Collection fields) {
        if (field instanceof Collection)
            for (Object fieldElement : ((Collection)field))
                getFields(fieldElement, path, index, fields);
        else if (field!=null)
            if (++index==path.length)
                fields.add(field);
            else
                getFields(((ODocument)field).field(path[index]), path, index, fields);

        return fields;
    }


    private String getId(MeIdentification metadata) {
        return getId(metadata.getUid(), metadata.getVersion());
    }
    private String getId(String uid, String version) {
        return uid+(version!=null && !version.trim().equals("") ? '|'+version : "");
    }

    private ODocument getIndexDocument(String id, OObjectDatabaseTx connection) {
        OSQLSynchQuery<ODocument> query = new OSQLSynchQuery<>("select from MetadataIndex where index|id = ?");
        List<ODocument> existingIndexes = connection.getUnderlying().query(query, id);
        return existingIndexes!=null && existingIndexes.size()>0 ? existingIndexes.iterator().next() : new ODocument("MetadataIndex");
    }

}


