package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

@ApplicationScoped
public class ResourceIndexManager extends LinksManager {
    @Inject ResourceLinksManager resourceLinksManager;
    @Inject DSDDatasetLinksManager dsdDatasetLinksManager;

    private final static String[] indexedFields = new String[]{
            //"meStatisticalProcessing.seDataCompilation.aggregationProcessing",
            "uid",
            "version",
            "meContent.resourceRepresentationType",
            "meContent.seCoverage.coverageSectors",
            "meSpatialRepresentation.seBoundingBox.seVectorSpatialRepresentation.topologyLevel"
    };

    //INIT

    private static FieldType[] fieldTypes = null;
    private enum FieldType {
        OjCodeList,OjCodeListCollection,
        OjPeriod,
        date,
        enumeration,
        other
    }

    public static void init(OClass meIdentityClassO) {
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


    //LOGIC

    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        resourceLinksManager.onUpdate(document, connection);
        dsdDatasetLinksManager.onUpdate(document, connection);

        if (document!=null && "MeIdentification".equals(document.getClassName()))

            for (int i=0; i<indexedFields.length; i++) {            //Indexing standard properties
                String fieldName = indexedFields[i].replace('.','|');
                FieldType fieldType = fieldTypes[i];
                Collection fieldValues = getFields(document,indexedFields[i]);

                switch (fieldType) {
                    case enumeration:
                        document.field("index|" + fieldName, fieldValues!=null && fieldValues.size()>0 ? fieldValues.iterator().next() : null, OType.STRING);
                        break;
                    case OjPeriod:
                        ODocument periodO = fieldValues!=null && fieldValues.size()>0 ? (ODocument)fieldValues.iterator().next() : null;
                        if (periodO!=null) {
                            document.field("index|" + fieldName + "|from", (Date)periodO.field("from"), OType.DATE);
                            document.field("index|" + fieldName + "|to", (Date)periodO.field("to"), OType.DATE);
                        }
                        break;
                    case date:
                        Date date = fieldValues!=null && fieldValues.size()>0 ? (Date)fieldValues.iterator().next() : null;
                        if (date!=null) {
                            document.field("index|" + fieldName + "|from", date, OType.DATE);
                            document.field("index|" + fieldName + "|to", date, OType.DATE);
                        }
                    case OjCodeList:
                    case OjCodeListCollection:
                        Collection<String> codes = fieldValues!=null && fieldValues.size()>0 ? getCodes(fieldValues) : null;
                        document.field("index|" + fieldName, codes!=null && codes.size()>0 ? codes : null, OType.EMBEDDEDLIST, OType.STRING);
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




    //Utils

    private static OProperty getProperty(OClass classO, String[] propertyName) {
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
            String codeListID = uid!=null ? uid + (version!=null ? '|'+version : "") : null;
            if (codeListID!=null) {
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


}
