package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.common.listener.OProgressListener;
import com.orientechnologies.orient.core.db.document.ODatabaseDocument;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.index.OIndexManagerProxy;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OSchema;
import com.orientechnologies.orient.core.record.impl.ODocument;

import java.util.Collection;

public class SchemaClone implements OProgressListener {

    public static void cloneDatabase (ODatabaseDocument fromDatabase, ODatabaseDocument toDatabase) {
        ODocument doc = fromDatabase.browseClass("").next().copy();
        toDatabase.save(doc);
    }
    public static void cloneGraphDatabase (ODatabaseDocument fromDatabase, ODatabaseDocument toDatabase) {
        OSchema schemaTo = toDatabase.getMetadata().getSchema();
		OIndexManagerProxy fromIndexManager = fromDatabase.getMetadata().getIndexManager();
        OIndexManagerProxy toIndexManager = toDatabase.getMetadata().getIndexManager();
        OProgressListener indexCreationProgressListener = new SchemaClone();
        //Create classes
		for (OClass origin : fromDatabase.getMetadata().getSchema().getClasses())
            cloneClass(origin, fromIndexManager, schemaTo);
		//Create indexes
        for (OIndex<?> fromIndex : fromIndexManager.getIndexes())
            toIndexManager.createIndex(fromIndex.getName(), fromIndex.getType(), fromIndex.getDefinition(),getClustersIndexes(fromIndex.getClusters()), indexCreationProgressListener,null);
        //Save schema
        schemaTo.save();
	}

    private static int[] getClustersIndexes(Collection<String> clustersName) {
        int[] indexes = new int[clustersName.size()];
        int i=0;

        for (String clusterName : clustersName)
            indexes[i++] = 0; //TODO

        return indexes;
    }
	
	private static OClass cloneClass (OClass from, OIndexManagerProxy fromIndexManager, OSchema schemaTo) {
		OClass newClass = schemaTo.getClass(from.getName());
		if (newClass==null) {
			//Create new class into the new schema
			newClass = schemaTo.createClass(from.getName()).setStrictMode(from.isStrictMode());
			if (from.getShortName()!=null)
				newClass.setShortName(from.getShortName());
			//Superclass
			if (from.getSuperClass()!=null) {
				OClass superClass = schemaTo.getClass(from.getSuperClass().getName());
				if (superClass==null)
					superClass = cloneClass(from.getSuperClass(), fromIndexManager, schemaTo);
				newClass.setSuperClass(superClass);
			}
			//Properties
			for (OProperty fromProperty : from.declaredProperties())
				if (fromProperty.getLinkedClass()!=null) {
					OClass linkedClass = schemaTo.getClass(fromProperty.getLinkedClass().getName());
					if (linkedClass==null)
						linkedClass = cloneClass(fromProperty.getLinkedClass(), fromIndexManager, schemaTo);
					newClass.createProperty(fromProperty.getName(), fromProperty.getType(), linkedClass).setMin(fromProperty.getMin()).setMax(fromProperty.getMax()).setMandatory(fromProperty.isMandatory()).setNotNull(fromProperty.isNotNull());
				} else newClass.createProperty(fromProperty.getName(), fromProperty.getType()).setMin(fromProperty.getMin()).setMax(fromProperty.getMax()).setMandatory(fromProperty.isMandatory()).setNotNull(fromProperty.isNotNull());
		}
		//Return destination class
		return newClass;
	}


    @Override
    public void onBegin(Object o, long l) { }

    @Override
    public boolean onProgress(Object o, long l, float v) { return false; }

    @Override
    public void onCompletition(Object o, boolean b) { }
}
