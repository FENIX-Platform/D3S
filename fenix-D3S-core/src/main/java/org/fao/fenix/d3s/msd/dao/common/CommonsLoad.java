package org.fao.fenix.d3s.msd.dao.common;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.fao.fenix.commons.msd.dto.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.common.Publication;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.commons.utils.CompletenessIterator;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

import javax.inject.Inject;

public class CommonsLoad extends OrientDao {
	
	@Inject private CommonsConverter converter;
	
	private static OSQLSynchQuery<ODocument> queryLoadIdentityById = new OSQLSynchQuery<ODocument>("select from CMContactIdentity where @rid = ?");
    private static OSQLSynchQuery<ODocument> queryLoadPublicationById = new OSQLSynchQuery<ODocument>("select from CMPublication where @rid = ?");

	//Identity standard load
	public Collection<ContactIdentity> loadContactIdentities(String institution, String department, String name, String surname, String context) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadContactIdentities(institution, department, name, surname, context, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<ContactIdentity> loadContactIdentities (String institution, String department, String name, String surname, String context, OGraphDatabase database) throws Exception {
		Collection<ODocument> identitiesO = loadContactIdentitiesO(institution, department, name, surname, context, database);
		Collection<ContactIdentity> identities = new LinkedList<ContactIdentity>();
		if (identitiesO!=null)
			for (ODocument identityO : identitiesO)
				identities.add(converter.toContactIdentity(identityO));
		return identities;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadContactIdentitiesO(String institution, String department, String name, String surname, String context, OGraphDatabase database) throws Exception {
        StringBuilder queryBuffer = new StringBuilder();
        Collection<String> paramsBuffer = new LinkedList<String>();
        if (context!=null && !context.trim().equals("")) {
            paramsBuffer.add(context.trim());
            queryBuffer.append(" AND context.name = ?");
        }
        if (institution!=null && !institution.trim().equals("")) {
            paramsBuffer.add(institution.trim().toLowerCase().replace('*','%'));
            queryBuffer.append(" AND institution.toLowerCase() LIKE ?");
        }
        if (department!=null && !department.trim().equals("")) {
            paramsBuffer.add(department.trim().toLowerCase().replace('*','%'));
            queryBuffer.append(" AND department.toLowerCase() LIKE ?");
        }
        if (name!=null && !name.trim().equals("")) {
            paramsBuffer.add(name.trim().toLowerCase().replace('*','%'));
            queryBuffer.append(" AND name.toLowerCase() LIKE ?");
        }
        if (surname!=null && !surname.trim().equals("")) {
            paramsBuffer.add(surname.trim().toLowerCase().replace('*','%'));
            queryBuffer.append(" AND surname.toLowerCase() LIKE ?");
        }

        if (paramsBuffer.size()>0)
            return (Collection<ODocument>)database.query(new OSQLSynchQuery<ODocument>("select from CMContactIdentity where "+queryBuffer.substring(4)), paramsBuffer.toArray());
        else
            return new LinkedList<ODocument>();
	}
	
	//Identity load by ID
	public ContactIdentity loadContactIdentity(String id) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadContactIdentity(id, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public ContactIdentity loadContactIdentity (String id, OGraphDatabase database) throws Exception {
		ODocument identityO = loadContactIdentityO(id, database);
		return identityO!=null ? converter.toContactIdentity(identityO) : null;
	}
	public synchronized ODocument loadContactIdentityO(String id, OGraphDatabase database) throws Exception {
		queryLoadIdentityById.reset();
		queryLoadIdentityById.resetPagination();
		List<ODocument> result = database.query(queryLoadIdentityById, toRID(id));
		return result.size()==1 ? result.get(0) : null;
	}
	
	//Identity fulltext load
	public Collection<ContactIdentity> loadContactIdentitiesFulltext(String text) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			return loadContactIdentitiesFulltext (text, database);
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<ContactIdentity> loadContactIdentitiesFulltext (String text, OGraphDatabase database) throws Exception {
		Collection<ODocument> identitiesO = loadContactIdentitiesFulltextO(text, database);
		Collection<ContactIdentity> identities = new LinkedList<ContactIdentity>();
		if (identitiesO!=null)
			for (ODocument identityO : identitiesO)
				identities.add(converter.toContactIdentity(identityO));
		return identities;
	}
	@SuppressWarnings("unchecked")
	public synchronized Collection<ODocument> loadContactIdentitiesFulltextO(String text, OGraphDatabase database) throws Exception {
        String[] wordsBuffer = text!=null ? text.split(" ") : null;
        if (wordsBuffer!=null && wordsBuffer.length>0) {
            StringBuilder queryBuffer = new StringBuilder("select from CMContactIdentity where ");
            for (int i=0; i<wordsBuffer.length; i++) {
                wordsBuffer[i] = wordsBuffer[i].trim();
                queryBuffer.append("textKey CONTAINSTEXT ?");
                if (i<wordsBuffer.length-1)
                    queryBuffer.append(" OR");
            }
            return (Collection<ODocument>)database.query(new OSQLSynchQuery<ODocument>(queryBuffer.toString()), (Object[])wordsBuffer);
        } else
            return new LinkedList<ODocument>();
	}
	
	//Publication by ID
    public Publication loadPublication(String id) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            return loadPublication(id, database);
        } finally {
            if (database!=null)
                database.close();
        }
    }
    public Publication loadPublication (String id, OGraphDatabase database) throws Exception {
        ODocument publicationO = loadPublicationO(id, database);
        return publicationO!=null ? converter.toPublication(publicationO) : null;
    }
    public synchronized ODocument loadPublicationO(String id, OGraphDatabase database) throws Exception {
        queryLoadPublicationById.reset();
        queryLoadPublicationById.resetPagination();
        List<ODocument> result = database.query(queryLoadPublicationById, toRID(id));
        return result.size()==1 ? result.get(0) : null;
    }



    //Load ALL contacts iterable
	public CompletenessIterator<ContactIdentity> loadContactIdentitiesProducer() throws Exception {
        final Iterator<ODocument> producerO = loadContactIdentitiesProducerO().iterator();
        return new CompletenessIterator() {
            private int index = 0;

            @Override public void remove() { producerO.remove(); }
            @Override public boolean hasNext() { return producerO.hasNext(); }
            @Override public ContactIdentity next() {
                if (producerO.hasNext()) {
                    index++;
                    return converter.toContactIdentity(producerO.next());
                } else
                    return null;
            }

            @Override public int getIndex() { return index; }
        };
	}
    public synchronized Iterable<ODocument> loadContactIdentitiesProducerO() throws Exception {
        return browseClass("CMContactIdentity", OrientDatabase.msd);
    }
    public long countContactIdentities() throws Exception {
        return countClass("CMContactIdentity", OrientDatabase.msd);
    }


}
