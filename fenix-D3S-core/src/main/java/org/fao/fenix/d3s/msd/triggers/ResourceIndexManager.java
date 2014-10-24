package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;

@ApplicationScoped
public class ResourceIndexManager extends LinksManager {
    @Inject ResourceLinksManager resourceLinksManager;
    @Inject DSDDatasetLinksManager dsdDatasetLinksManager;

    private final static String[] indexedFields = new String[]{
            "uid",
            "version",
            "meContent.resourceRepresentationType",
            "meContent.seCoverage.coverageSectors",
            "meStatisticalProcessing.seDataCompilation.aggregationProcessing"
    };

    //INIT

    private static FieldType[] fieldTypes = null;
    private enum FieldType {
        OjCodeList,OjCodeListCollection,
        OjPeriod,OjPeriodCollection,
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

        if (document!=null && "MeIdentification".equals(document.getClassName())) {
            //Indexing standard properties
            OType fieldType;
            for (String fieldName : indexedFields)
                switch (fieldType=document.fieldType("uid")) {
                    case STRING:
                        document.field("index."+fieldName, document.field(fieldName), fieldType);
                        break;
                    case EMBEDDED:
                        ODocument embeddedDocument = document.field(fieldName);
                        if (embeddedDocument!=null) {
                                document.getSchemaClass().getProperty(fieldName).getLinkedClass();
                        }

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
            ODocument resourceO = ojCodelistO.field("linkedCodeList");
            Collection<ODocument> ojCodes = ojCodelistO.field("codes");
            if (resourceO!=null) {

            }
            //MeIdentification resource = codeListDao.loadMetadata((String) ojCodelistO.field("idCodeList"), (String) ojCodelistO.field("version"));

        }
        return codes;
    }
    private void addParents(ODocument code, Collection<String> codes, String prefix) {

    }


}
