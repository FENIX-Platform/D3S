package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.find.dto.condition.ConditionFilter;
import org.fao.fenix.commons.msd.dto.type.ResponsiblePartyRole;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.*;

@ApplicationScoped
public class ResourceIndexManager extends LinksManager {
    @Inject ResourceLinksManager resourceLinksManager;
    @Inject DSDDatasetLinksManager dsdDatasetLinksManager;

    private final static String[] indexedFields = new String[]{
            "uid",
            "version",
            "dsd.contextSystem",
//            "dsd.workspace",
//            "dsd.layerName",
            "meContent.resourceRepresentationType",
            "meContent.seCoverage.coverageSectors",
            "meContent.seCoverage.coverageGeographic",
            "meContent.seReferencePopulation.referencePeriod",
            "meContent.seReferencePopulation.referenceArea",
            "contacts",
            "meAccessibility.seConfidentiality.confidentialityStatus",
            "meSpatialRepresentation.seBoundingBox.seVectorSpatialRepresentation.topologyLevel",
            "meSpatialRepresentation.processing",
            "meSpatialRepresentation.layerType",
            "meReferenceSystem.seProjection.projection"
    };



    //LOGIC

    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        resourceLinksManager.onUpdate(document, connection);
        dsdDatasetLinksManager.onUpdate(document, connection);

        //ID
        String uid = document.field("uid");
        String version = document.field("version");
        if (uid!=null)
            document.field("index|id", uid + (version!=null && !version.trim().equals("")? '|'+version : ""));

        //Other standard fields
        if (document!=null && "MeIdentification".equals(document.getClassName()))

            for (int i=0; i<indexedFields.length; i++) {            //Indexing standard properties
                String fieldName = indexedFields[i].replace('.','|');
                FieldType fieldType = fieldTypes[i];
                Collection fieldValues = getFields(document,indexedFields[i]);
                Long[] period;

                switch (fieldType) {
                    case enumeration:
                        document.field("index|" + fieldName, fieldValues!=null && fieldValues.size()>0 ? fieldValues.iterator().next() : null, OType.STRING);
                        break;
                    case OjPeriod:
                        ODocument periodO = fieldValues!=null && fieldValues.size()>0 ? (ODocument)fieldValues.iterator().next() : null;
                        period = datePeriodToPeriod((Date)periodO.field("from"), (Date)periodO.field("to"));
                        document.field("index|" + fieldName + "|from", period[0], OType.LONG);
                        document.field("index|" + fieldName + "|to", period[1], OType.LONG);
                        break;
                    case date:
                        Date date = fieldValues!=null && fieldValues.size()>0 ? (Date)fieldValues.iterator().next() : null;
                        period = dateToPeriod(date);
                        document.field("index|" + fieldName + "|from", period[0], OType.LONG);
                        document.field("index|" + fieldName + "|to", period[1], OType.LONG);
                    case OjCodeList:
                    case OjCodeListCollection:
                        Collection<String> codes = fieldValues!=null && fieldValues.size()>0 ? getCodes(fieldValues) : null;
                        document.field("index|" + fieldName, codes!=null && codes.size()>0 ? codes : null, OType.EMBEDDEDLIST, OType.STRING);
                        break;
                    case OjResponsibleParty:
                    case OjResponsiblePartyCollection:
                        for (Map.Entry<String, String> contactEntry : ((Map<String,String>)getContacts(fieldValues)).entrySet())
                            document.field("index|" + fieldName + '|' + contactEntry.getKey(), contactEntry.getValue(), OType.STRING);
                        break;
                    case other:
                        System.out.println("Undefined index type for "+fieldName);
                        break;
                }
            }
        //Save changes
        document.save();
        return RESULT.RECORD_CHANGED;
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

    @Override
    public void init(OClass meIdentityClassO) throws Exception {
        for (int i=0; i<indexedFields.length;i++)
            indexedFieldsIndex.put(indexedFields[i],i);

        setFieldTypes(meIdentityClassO);
        createPropertiesIndex(meIdentityClassO);
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

    //Create single properties index
    public void createPropertiesIndex(OClass meIdentityClassO) {
        createPropertyIndex(meIdentityClassO, "index|id", OType.STRING, null, OClass.INDEX_TYPE.UNIQUE_HASH_INDEX);

        for (int i=0; i<indexedFields.length; i++) {
            String fieldName = indexedFields[i].replace('.','|');
            switch (fieldTypes[i]) {
                case enumeration:
                    createPropertyIndex(meIdentityClassO, "index|" + fieldName, OType.STRING, null, OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
                    break;
                case OjPeriod:
                case date:
                    createPropertyIndex(meIdentityClassO, "index|" + fieldName + "|from", OType.LONG, null, OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
                    createPropertyIndex(meIdentityClassO, "index|" + fieldName + "|to", OType.LONG, null, OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
                    break;
                case OjCodeList:
                case OjCodeListCollection:
                    createPropertyIndex(meIdentityClassO, "index|" + fieldName, OType.EMBEDDEDLIST, OType.STRING, OClass.INDEX_TYPE.NOTUNIQUE_HASH_INDEX);
                    break;
                case OjResponsibleParty:
                case OjResponsiblePartyCollection:
                    for (ResponsiblePartyRole role : ResponsiblePartyRole.values())
                        createPropertyIndex(meIdentityClassO, "index|" + fieldName + '|' + role, OType.STRING, null, OClass.INDEX_TYPE.FULLTEXT);
                    break;
                case other:
                    System.out.println("Undefined index type for "+fieldName);
                    break;
            }
        }
    }
    private void createPropertyIndex(OClass meIdentityClassO, String fieldName, OType type, OType linkedType, OClass.INDEX_TYPE indexType) {
        OProperty property = meIdentityClassO.getProperty(fieldName);
        if (property==null)
            meIdentityClassO.createProperty(fieldName,type,linkedType);
        if (indexType!=null) {
            OIndex index = meIdentityClassO.getClassIndex("MeIdentification."+fieldName);
            if (index==null)
                if (indexType==OClass.INDEX_TYPE.FULLTEXT)
                    meIdentityClassO.createIndex("MeIdentification."+fieldName, "FULLTEXT", null, null, "LUCENE", new String[] { fieldName });
                else
                    meIdentityClassO.createIndex("MeIdentification."+fieldName, indexType, fieldName);

        }
    }

    //Create multiple properties index
    public void createMultiplePropertiesIndex(OClass meIdentityClassO) {

    }


    //Utils
    public Map<String, Integer> getIndexedFieldsIndex() {
        return indexedFieldsIndex;
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
                        ODocument linkedCodeO = ojCodeO.field("linkedCode");
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

}
