package org.fao.fenix.d3s.server.tools.orient;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import javassist.util.proxy.Proxy;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.exception.OSerializationException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.metadata.schema.OClass;
import com.orientechnologies.orient.core.metadata.schema.OProperty;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.msd.dto.JSONEntity;

import javax.inject.Inject;
import javax.persistence.Embedded;
import javax.ws.rs.core.NoContentException;

public abstract class OrientDao {
    @Inject private DatabaseStandards dbParameters;
    @Inject private OrientServer client;

    public OObjectDatabaseTx getConnection() {
        return dbParameters.getConnection();
    }



	//DELETE UTILS

    protected int deleteGraphInclude (ODocument document, String[] boundary) { return deleteGraph(document, Arrays.asList(boundary)); }
	protected int deleteGraphInclude (ODocument document, Collection<String> boundary) { return deleteGraph(document, boundary!=null ? new HashSet<>(boundary) : new HashSet<String>(), new TreeSet<ORID>(),true); }
	protected int deleteGraph (ODocument document, String[] boundary) { return deleteGraph(document, Arrays.asList(boundary)); }
	protected int deleteGraph (ODocument document, Collection<String> boundary) { return deleteGraph(document, boundary!=null ? new HashSet<>(boundary) : new HashSet<String>(), new TreeSet<ORID>(),false); }
	@SuppressWarnings("unchecked")
	private final int deleteGraph (ODocument document, Set<String> boundary, Set<ORID> deleted, boolean include) {
		if (document==null || deleted.contains(document.getIdentity()) || (include && !boundary.contains(document.getClassName())) || (!include && boundary.contains(document.getClassName())))
			return 0;

		int count = 1;
		deleted.add(document.getIdentity());

		OProperty field = null;
		OClass vertexClass = document.getSchemaClass();
		for (String fieldName : document.fieldNames()) {
			for (OClass fieldsClass = vertexClass; fieldsClass!=null && (field = fieldsClass.getProperty(fieldName))==null; fieldsClass = fieldsClass.getSuperClass());
			Object fieldValue = document.field(fieldName);
			if (field!=null && fieldValue!=null)
				switch (field.getType()) {
					case LINK: 
						count += deleteGraph((ODocument)fieldValue, boundary, deleted,include);
						break;
					case LINKLIST:
					case LINKSET: 
						for (ODocument child : (Collection<ODocument>)fieldValue)
							count += deleteGraph(child, boundary, deleted,include);
						break;
					case LINKMAP: 
						for (ODocument child : ((Map<Object,ODocument>)document.field(fieldName)).values())
							count += deleteGraph(child, boundary, deleted,include);
						break;
				}
		}
		
		document.delete();
		
		return count;
	}

    public synchronized int command(String query, Object ... params) throws Exception {
        return getConnection().command(new OCommandSQL(query)).execute(params);
    }

    //LOAD UTILS
    private <T> OSQLSynchQuery<T> getSelect(String query, Class<T> type, Order ordering, Page paging) {
        if (ordering!=null)
            query += ordering.toSQL();
        if (paging!=null)
            query += paging.toSQL();

        return createSelect(query,type);
    }

    public <T> OSQLSynchQuery<T> createSelect(String query, Class<T> type) {
        return new OSQLSynchQuery<>(query);
    }

    public Collection<ODocument> select(String query, Object... params) throws Exception {
        return select(query, dbParameters.getOrderingInfo(), dbParameters.getPaginationInfo(), params);
    }
    public List<ODocument> select(String query, Order ordering, Page paging, Object... params) throws Exception {
        return dbParameters.getConnection().getUnderlying().query(getSelect(query, ODocument.class, ordering, paging), params);
    }
    public <T> Collection<T> select(Class<T> type, String query, Object... params) throws Exception {
        return select(type, query, dbParameters.getOrderingInfo(), dbParameters.getPaginationInfo(), params);
    }
    public <T> Collection<T> select(Class<T> type, String query, Order ordering, Page paging, Object... params) throws Exception {
        try {
            return (Collection<T>) getConnection().query(getSelect(query, type, ordering, paging), params);
        } catch (OSerializationException ex) {
            client.registerPersistentEntities();
            return select(type,query,ordering,paging,params);
        }
    }


    public ODocument load (String rid) throws Exception {
        return load(JSONEntity.toRID(rid));
    }
    public ODocument load (ORID orid) throws Exception {
        return dbParameters.getConnection().getUnderlying().load(orid);
    }
    public <T> T loadBean (String rid, Class<T> type) throws Exception {
        try {
            return (T)loadBean(JSONEntity.toRID(rid));
        } catch (ClassCastException ex) {
            throw new NoContentException("Illegal type '"+type+"' for the entity "+rid);
        }
    }
    public <T extends JSONEntity> T loadBean (T bean) throws Exception {
        return (T)loadBean(bean.getORID());
    }
    public Object loadBean (ORID orid) throws Exception {
        try {
            Object entity = orid != null ? getConnection().load(orid) : null;
            if (entity == null)
                throw new NoContentException(JSONEntity.toString(orid));
            return entity;
        } catch (OSerializationException ex) {
            client.registerPersistentEntities();
            return loadBean(orid);
        }
    }

    public <T> Iterator<T> browse(Class<T> type) throws Exception {
        return getConnection().browseClass(type);
    }
    public Iterable<ODocument> browse (String className) throws Exception {
        final Iterator<ODocument> producerO = dbParameters.getConnection().getUnderlying().browseClass(className).iterator();
        return new Iterable<ODocument>() {
            @Override
            public Iterator<ODocument> iterator() {
                return new Iterator<ODocument>() {
                    @Override public void remove() { throw new UnsupportedOperationException(); }
                    @Override public boolean hasNext() { return producerO.hasNext(); }

                    @Override
                    public ODocument next() {
                        return producerO.hasNext() ? producerO.next() : null;
                    }
                };
            }
        };
    }
    public long count (String className) throws Exception {
        return dbParameters.getConnection().getUnderlying().countClass(className);
    }


    //SAVE UTILS

    class MethodGetSet {
        Method get, set;
        MethodGetSet(Method get, Method set) {
            this.get = get;
            this.set = set;
        }
    }
    private static final Set<Class> entityClass = new HashSet<>();
    private static final Map<Class,Collection<MethodGetSet>> standardGetSet = new HashMap<>();
    private static final Map<Class,Collection<MethodGetSet>> entityGetSet = new HashMap<>();
    private static final Map<Class,Collection<MethodGetSet>> entityCollectionGetSet = new HashMap<>();
    private static final Map<Method,Boolean> embeddedGetSet = new HashMap<>();

    public <T extends JSONEntity> T newCustomEntity(T bean, boolean ... checks) {
        boolean cycleCheck = checks!=null && checks.length>0 && checks[0]; //false by default
        try {
            bean.setRID(null); //Ignore bean ORID
            return saveCustomEntity(bean, false, cycleCheck); //Save in append mode for connected entities
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public <T extends JSONEntity> T saveCustomEntity(T bean, boolean ... checks) throws Exception {
        Collection<T> beans = new LinkedList<>();
        beans.add(bean);
        return saveCustomEntity(beans,checks).iterator().next();
    }
    public <T extends JSONEntity> Collection<T> saveCustomEntity(Collection<T> beans, boolean ... checks) throws Exception {
        boolean overwrite = checks!=null && checks.length>0 && checks[0]; //false by default
        boolean cycleCheck = checks!=null && checks.length>1 && checks[1]; //false by default

        OObjectDatabaseTx connection = null;
        try {
            connection = getConnection();
            connection.begin();

            Map<Object,Object> buffer = cycleCheck ? new HashMap<>() : null;
            Collection<T> beansBuffer = new LinkedList<>();
            for (T bean : beans)
                beansBuffer.add(saveCustomEntity(bean, overwrite, buffer, connection, false, null));

            connection.commit();
            return beansBuffer;
        } catch (OSerializationException e) {
            if (connection!=null)
                connection.rollback();
            client.registerPersistentEntities();
            return saveCustomEntity(beans, overwrite, cycleCheck);
        } catch (Exception e) {
            if (connection!=null)
                connection.rollback();
            throw e;
        }
    }
    private <T extends JSONEntity> T saveCustomEntity(T bean, boolean overwrite, Map<Object,Object> buffer, OObjectDatabaseTx connection, boolean embedded, T embeddedBeanProxy) throws InvocationTargetException, IllegalAccessException, InstantiationException, NoSuchMethodException, NoContentException {
        //Avoid cycle and useless proxy create/load
        if (bean==null)
            return null;
        if (bean instanceof Proxy)
            return bean;
        if (buffer!=null && buffer.containsKey(bean))
            return (T)buffer.get(bean);

        //Init recursion information about this entity
        Class<T> beanClass = (Class<T>) bean.getClass();
        if (!entityClass.contains(beanClass))
            initEntityRecursionInformation(beanClass);

        //Load/create proxy bean
        ORID orid = bean.getORID();
        T beanProxy = embeddedBeanProxy!=null ? embeddedBeanProxy : ( orid!=null ? (T)connection.load(orid) : connection.newInstance(beanClass) );
        if (beanProxy==null)
            throw new NoContentException("Cannot find bean '"+bean.getRID()+'\'');
        if (buffer!=null && !embedded)
            buffer.put(bean,beanProxy);

        //Retrieve fields value and apply recursion
        boolean empty = true;
        Set<Method> nullFields = new HashSet<>();

        Collection<? extends JSONEntity> collectionFieldValue;
        Object fieldValue;

        for (MethodGetSet methodGetSet : standardGetSet.get(beanClass))
            if ((fieldValue=methodGetSet.get.invoke(bean)) != null) {
                empty = false;
                methodGetSet.set.invoke(beanProxy, fieldValue);
            } else if (overwrite)
                nullFields.add(methodGetSet.set);

        for (MethodGetSet methodGetSet : entityGetSet.get(beanClass))
            if ((fieldValue=methodGetSet.get.invoke(bean)) != null) {
                empty = false;
                boolean embeddedField = embeddedGetSet.get(methodGetSet.set);
                methodGetSet.set.invoke(beanProxy, saveCustomEntity((JSONEntity) fieldValue, overwrite, buffer, connection, embeddedField, (T)methodGetSet.get.invoke(beanProxy)));
            } else if (overwrite)
                nullFields.add(methodGetSet.set);

        for (MethodGetSet methodGetSet : entityCollectionGetSet.get(beanClass))
            if ((collectionFieldValue = (Collection) methodGetSet.get.invoke(bean))!=null && collectionFieldValue.size()>0) {
                //Collect new proxy entities
                empty = false;
                Collection<JSONEntity> proxyCollectionFieldValue = new LinkedHashSet<>();
                for (JSONEntity elementValue : collectionFieldValue) {
                    boolean embeddedField = embeddedGetSet.get(methodGetSet.set);
                    proxyCollectionFieldValue.add(saveCustomEntity(elementValue, overwrite, buffer, connection, embeddedField, null));
                }
                //In append mode add old proxy entities (duplicates are avoided by default by Java HashSet)
                if (!overwrite) {
                    Collection<? extends JSONEntity> existingProxyCollectionFieldValue = (Collection)methodGetSet.get.invoke(beanProxy);
                    if (existingProxyCollectionFieldValue!=null && existingProxyCollectionFieldValue.size()>0)
                        for (Object existingValue : existingProxyCollectionFieldValue)
                            if (existingValue!=null) //Avoid removed links
                                proxyCollectionFieldValue.add((JSONEntity) existingValue);
                }
                //Set new value
                methodGetSet.set.invoke(beanProxy,new LinkedList<>(proxyCollectionFieldValue));
            } else if (overwrite) //In overwrite mode maintain nullable fields for the last step
                nullFields.add(methodGetSet.set);

        //Set null field values of non empty bean if in overwrite mode
        if (overwrite && !empty)
            for (Method set : nullFields)
                set.invoke(beanProxy,new Object[] {null});

        //Return updated proxy bean
        if (!embedded)
            connection.save(beanProxy);
        return beanProxy;
    }


    private synchronized <T extends JSONEntity> void initEntityRecursionInformation (Class<T> beanClass) throws NoSuchMethodException {
        Collection<MethodGetSet> standardGetSetCollection = new LinkedList<>();
        Collection<MethodGetSet> entityGetSetCollection = new LinkedList<>();
        Collection<MethodGetSet> entityCollectionGetSetCollection = new LinkedList<>();
        standardGetSet.put(beanClass,standardGetSetCollection);
        entityGetSet.put(beanClass,entityGetSetCollection);
        entityCollectionGetSet.put(beanClass,entityCollectionGetSetCollection);

        for (Class<? extends JSONEntity> c = beanClass; !c.equals(JSONEntity.class); c = (Class<? extends JSONEntity>) c.getSuperclass())
            for (Method getter : c.getDeclaredMethods())
                if (getter.getName().startsWith("get") && !getter.getName().equals("getRID") && !getter.getName().equals("getORID")) {
                    Class returnClass = getter.getReturnType();
                    MethodGetSet getSet = new MethodGetSet(getter, beanClass.getMethod('s'+getter.getName().substring(1),returnClass));
                    if (Collection.class.isAssignableFrom(returnClass)) {
                        Class elementClass = (Class) ((ParameterizedType) getter.getGenericReturnType()).getActualTypeArguments()[0];
                        if (JSONEntity.class.isAssignableFrom(elementClass))
                            entityCollectionGetSetCollection.add(getSet);
                        else
                            standardGetSetCollection.add(getSet);
                    } else if (JSONEntity.class.isAssignableFrom(returnClass))
                        entityGetSetCollection.add(getSet);
                    else
                        standardGetSetCollection.add(getSet);
                    embeddedGetSet.put(getSet.set,getSet.set.isAnnotationPresent(Embedded.class));
                }
        entityClass.add(beanClass);
    }


    //DOCUMENT DATABASE UTILS

    public void toMap(Map<String,Object> data, ODocument recordO, Set<String> classes, Integer levels) {
        toMap(data, recordO, 0, classes, levels, new Stack<ORID>());
    }
    private void toMap(Map<String,Object> data, ODocument recordO, int level, Set<String> classes, Integer levels, Stack<ORID> done) {
        if (recordO!=null && !done.contains(recordO.getIdentity()) && (classes==null || classes.isEmpty() || classes.contains(recordO.getClassName())) && (levels==null || level<=levels) ) {

            done.push(recordO.getIdentity());

            for (String fieldName : recordO.fieldNames()) {
                OType fieldType = recordO.fieldType(fieldName);
                if (fieldType==null)
                    fieldType = recordO.getSchemaClass().getProperty(fieldName).getType();
                if (fieldType==null)
                    fieldType = OType.BINARY;

                switch (fieldType) {
                    case LINK:
                        ODocument childO = recordO.field(fieldName);
                        if (childO!=null) {
                            Map<String,Object> record = new HashMap<>();
                            toMap(record, childO, level+1, classes, levels, done);
                            if (record.size()>0)
                                data.put(fieldName, record);
                        }
                        break;
                    case LINKLIST:
                        List<ODocument> childrenListO = recordO.field(fieldName);
                        if (childrenListO!=null) {
                            List<Map<String,Object>> records = new LinkedList<>();
                            for (ODocument cO : childrenListO) {
                                Map<String,Object> record = new HashMap<>();
                                toMap(record, cO, level+1, classes, levels, done);
                                if (record.size()>0)
                                    records.add(record);
                            }
                            if (records.size()>0)
                                data.put(fieldName, records);
                        }
                        break;
                    case LINKSET:
                        Set<ODocument> childrenSetO = recordO.field(fieldName);
                        if (childrenSetO!=null) {
                            Set<Map<String,Object>> records = new HashSet<>();
                            for (ODocument cO : childrenSetO) {
                                Map<String,Object> record = new HashMap<>();
                                toMap(record, cO, level+1, classes, levels, done);
                                if (record.size()>0)
                                    records.add(record);
                            }
                            if (records.size()>0)
                                data.put(fieldName, records);
                        }
                        break;
                    case LINKMAP:
                        Map<String, ODocument> childrenMapO = recordO.field(fieldName);
                        if (childrenMapO!=null) {
                            Map<String,Map<String,Object>> records = new HashMap<>();
                            for (Map.Entry<String, ODocument> ceO : childrenMapO.entrySet()) {
                                Map<String,Object> record = new HashMap<>();
                                toMap(record, ceO.getValue(), level+1, classes, levels, done);
                                if (record.size()>0)
                                    records.put(ceO.getKey(), record);
                            }
                            if (records.size()>0)
                                data.put(fieldName, records);
                        }
                        break;
                    default:
                        data.put(fieldName,recordO.field(fieldName));
                }
            }

            done.pop();
        }
    }


}
