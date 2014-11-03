package org.fao.fenix.d3s.msd.triggers;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ResourceLinksManager extends LinksManager {

    private static String[] ojCodeListFields;


    //INIT
    public static void init(OClass meIdentityClassO) {
        Collection<String> ojCodeListFieldsList = new HashSet<>();

        if (meIdentityClassO!=null)
            appendOjCodelistFields(meIdentityClassO, ojCodeListFieldsList, "");

        ojCodeListFields = ojCodeListFieldsList.toArray(new String[ojCodeListFieldsList.size()]);
    }

    private static void appendOjCodelistFields (OClass classO, Collection<String> ojCodeListFieldsList, String prefix) {
        for (OProperty property : classO.properties()) {
            OType propertyType = property.getType();
            if (propertyType==OType.EMBEDDED || propertyType==OType.EMBEDDEDLIST || propertyType==OType.EMBEDDEDSET) {
                OClass propertyClass = property.getLinkedClass();
                String propertyClassName = propertyClass!=null ? propertyClass.getName() : null;
                String propertyName = prefix + property.getName();

                if (propertyClassName!=null && (propertyClassName.startsWith("Me") || propertyClassName.startsWith("Se") || propertyClassName.startsWith("Oj")))
                    if (propertyClassName.equals("OjCodeList"))
                        ojCodeListFieldsList.add(propertyName);
                    else
                        appendOjCodelistFields(propertyClass, ojCodeListFieldsList, propertyName+'.');
            }
        }
    }


    //UPDATE RESOURCE METADATA
    @Override
    protected RESULT onUpdate(ODocument document, ODatabase connection) throws Exception {
        if (document!=null && "MeIdentification".equals(document.getClassName()))
            linkCodes(document, ojCodeListFields);

        return RESULT.RECORD_CHANGED;
    }



}
