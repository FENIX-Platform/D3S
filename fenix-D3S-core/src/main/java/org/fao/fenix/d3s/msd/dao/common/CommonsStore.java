package org.fao.fenix.d3s.msd.dao.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import org.fao.fenix.commons.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.d3s.msd.dao.dsd.DSDLoad;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dao.dsd.DSDStore;
import org.fao.fenix.commons.msd.dto.common.Contact;
import org.fao.fenix.commons.msd.dto.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.common.Link;
import org.fao.fenix.commons.msd.dto.common.Publication;
import org.fao.fenix.commons.msd.dto.common.ValueOperator;
import org.fao.fenix.commons.msd.dto.dsd.DSDDimension;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;

public class CommonsStore extends OrientDao {
	@Inject private CommonsLoad loadDAO;
	@Inject private CodeListLoad loadClDAO;
    @Inject private DSDLoad dsdLoadDAO;

    //@Inject private DSDStore storeDSDDAO;

	//UPDATE
	//Contact Identity
	public int updateContactIdentity(ContactIdentity contact, boolean append) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			int count = append ? appendContactIdentity(contact, database) : updateContactIdentity(contact, database);
			return count;
		} finally {
			if (database!=null)
				database.close();
		}
	}
	
	@SuppressWarnings("unchecked")
	public int updateContactIdentity(ContactIdentity contactIdentity, OGraphDatabase database) throws Exception {
		ODocument contactIdentityO = loadDAO.loadContactIdentityO(contactIdentity.getId(), database);
		if (contactIdentityO==null)
			return 0;
		
		contactIdentityO.field("textKey", contactIdentity.getTextKey());
		contactIdentityO.field("institution", contactIdentity.getInstitution());
		contactIdentityO.field("department", contactIdentity.getDepartment());
		contactIdentityO.field("name", contactIdentity.getName());
		contactIdentityO.field("surname", contactIdentity.getSurname());
		contactIdentityO.field("title", contactIdentity.getTitle());
		contactIdentityO.field("abstract", contactIdentity.getDescription());
		contactIdentityO.field("supplemental", contactIdentity.getSupplemental());

		//connected elements
		if (contactIdentity.getRegion()!=null)
			contactIdentityO.field("region", loadClDAO.loadCodeO(contactIdentity.getRegion().getSystemKey(), contactIdentity.getRegion().getSystemVersion(), contactIdentity.getRegion().getCode(), database));
		else
			contactIdentityO.field("region", null, OType.LINK);

		if (contactIdentity.getRole()!=null)
			contactIdentityO.field("role", loadClDAO.loadCodeO(contactIdentity.getRole().getSystemKey(), contactIdentity.getRole().getSystemVersion(), contactIdentity.getRole().getCode(), database));
		else
			contactIdentityO.field("role", null, OType.LINK);
		
		if (contactIdentity.getContext()!=null)
			contactIdentityO.field("context", storeContext(contactIdentity.getContext(),database));
		else
			contactIdentityO.field("context", null, OType.LINK);

		Collection<ODocument> contacts = contactIdentityO.field("contactList");
		if (contacts!=null)
			for (ODocument contactO : contacts)
				contactO.delete();
		contacts = new ArrayList<ODocument>();
		if (contactIdentity.getContactList()!=null)
			for (Contact contact : contactIdentity.getContactList())
				contacts.add(storeContact(contact, database));
		contactIdentityO.field("contactList", contacts.size()>0 ? contacts : null, OType.LINKLIST);

		contactIdentityO.save();
		return 1;
	}
	
	@SuppressWarnings("unchecked")
	public int appendContactIdentity(ContactIdentity contactIdentity, OGraphDatabase database) throws Exception {
		ODocument contactIdentityO = loadDAO.loadContactIdentityO(contactIdentity.getId(), database);
		if (contactIdentityO==null)
			return 0;
		
		String institution=null,department=null,name=null,surname=null;
		
		if ((institution=contactIdentity.getInstitution())!=null)
			contactIdentityO.field("institution", contactIdentity.getInstitution());
		else
			institution=(String)contactIdentityO.field("institution");
		if ((department=contactIdentity.getDepartment())!=null)
			contactIdentityO.field("department", contactIdentity.getDepartment());
		else
			department=(String)contactIdentityO.field("department");
		if ((name=contactIdentity.getName())!=null)
			contactIdentityO.field("name", contactIdentity.getName());
		else
			name=(String)contactIdentityO.field("name");
		if ((surname=contactIdentity.getSurname())!=null)
			contactIdentityO.field("surname", contactIdentity.getSurname());
		else
			surname=(String)contactIdentityO.field("surname");

		contactIdentityO.field("textKey", ContactIdentity.getTextKey(institution, department, name, surname));

		if (contactIdentity.getTitle()!=null)
			contactIdentityO.field("title", contactIdentity.getTitle());
		if (contactIdentity.getDescription()!=null)
			contactIdentityO.field("abstract", contactIdentity.getDescription());
		if (contactIdentity.getSupplemental()!=null)
			contactIdentityO.field("supplemental", contactIdentity.getSupplemental());

		//connected elements
		if (contactIdentity.getRegion()!=null)
			contactIdentityO.field("region", loadClDAO.loadCodeO(contactIdentity.getRegion().getSystemKey(), contactIdentity.getRegion().getSystemVersion(), contactIdentity.getRegion().getCode(), database));
		if (contactIdentity.getRole()!=null)
			contactIdentityO.field("role", loadClDAO.loadCodeO(contactIdentity.getRole().getSystemKey(), contactIdentity.getRole().getSystemVersion(), contactIdentity.getRole().getCode(), database));
        if (contactIdentity.getContext()!=null)
            contactIdentityO.field("context", storeContext(contactIdentity.getContext(),database));

		if (contactIdentity.getContactList()!=null) {
			Collection<ODocument> contacts = (Collection<ODocument>)contactIdentityO.field("contactList");
			if (contacts!=null)
				for (ODocument contactO : contacts)
					contactO.delete();
			contacts = new ArrayList<ODocument>();
			for (Contact contact : contactIdentity.getContactList())
				contacts.add(storeContact(contact, database));
			contactIdentityO.field("contactList", contacts.size()>0 ? contacts : null, OType.LINKLIST);
		}

		contactIdentityO.save();
		return 1;
	}

	
	//DELETE
	//Contact Identity
	public int deleteContatcIdentity(String id) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			int count = deleteContatcIdentity(id, database);
			return count;
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public int deleteContatcIdentity(String id, OGraphDatabase database) throws Exception {
		ODocument contactIdentityO = loadDAO.loadContactIdentityO(id, database);
		if (contactIdentityO==null)
			return 0;
		//Disconnect identity
		disconnectContatcIdentity(contactIdentityO, database);
		//Delete identity
		return deleteGraph(contactIdentityO, new String[]{"CSCode"});
	}
	private void disconnectContatcIdentity(ODocument systemO, OGraphDatabase database) throws Exception { /* Nothing to do for the moment */ }

	//STORE
	//Contact Identity
	public String storeContactIdentity (ContactIdentity contactIdentity) throws Exception {
		OGraphDatabase database = getDatabase(OrientDatabase.msd);
		try {
			ODocument contactIdentityO = storeContactIdentity(contactIdentity, database);
			return toString(contactIdentityO.getIdentity());
		} finally {
			if (database!=null)
				database.close();
		}
	}
	public Collection<ODocument> storeContactIdentity(Collection<ContactIdentity> contactIdentities, OGraphDatabase database) throws Exception {
		Collection<ODocument> contactIdentitiesO = new ArrayList<ODocument>();
		for (ContactIdentity contactIdentity : contactIdentities)
			contactIdentitiesO.add(storeContactIdentity(contactIdentity, database));
		return contactIdentitiesO;
	}
	
	public ODocument storeContactIdentity(ContactIdentity contactIdentity, OGraphDatabase database) throws Exception {
		ODocument contactIdentityO = loadDAO.loadContactIdentityO(contactIdentity.getId(), database);
		if (contactIdentityO!=null)
			return contactIdentityO;
		
		contactIdentityO = database.createVertex("CMContactIdentity");

		contactIdentityO.field("textKey", contactIdentity.getTextKey());
		contactIdentityO.field("institution", contactIdentity.getInstitution());
		contactIdentityO.field("department", contactIdentity.getDepartment());
		contactIdentityO.field("name", contactIdentity.getName());
		contactIdentityO.field("surname", contactIdentity.getSurname());
		contactIdentityO.field("title", contactIdentity.getTitle());
		contactIdentityO.field("abstract", contactIdentity.getDescription());
		contactIdentityO.field("supplemental", contactIdentity.getSupplemental());

		//connected elements
		if (contactIdentity.getRegion()!=null)
			contactIdentityO.field("region", loadClDAO.loadCodeO(contactIdentity.getRegion().getSystemKey(), contactIdentity.getRegion().getSystemVersion(), contactIdentity.getRegion().getCode(), database));

		if (contactIdentity.getRole()!=null)
			contactIdentityO.field("role", loadClDAO.loadCodeO(contactIdentity.getRole().getSystemKey(), contactIdentity.getRole().getSystemVersion(), contactIdentity.getRole().getCode(), database));

        if (contactIdentity.getContext()!=null)
            contactIdentityO.field("context", storeContext(contactIdentity.getContext(),database));

		Collection<ODocument> contacts = new ArrayList<ODocument>();
		if (contactIdentity.getContactList()!=null)
			for (Contact contact : contactIdentity.getContactList())
				contacts.add(storeContact(contact, database));
		contactIdentityO.field("contactList", contacts.size()>0 ? contacts : null, OType.LINKLIST);

		return contactIdentityO.save();
	}
	
	private ODocument storeContact(Contact contact, OGraphDatabase database) throws Exception {
		return database.createVertex("CMContact").field("contact", contact.getContact()).field("type", contact.getType()!=null?contact.getType().getCode():null).save();
	}

	//Link
	public Collection<ODocument> storeLink(Collection<Link> links, OGraphDatabase database) throws Exception {
		Collection<ODocument> linksO = new ArrayList<ODocument>();
		for (Link link : links)
			linksO.add(storeLink(link, database));
		return linksO;
	}

	public ODocument storeLink(Link link, OGraphDatabase database) throws Exception {
		return database.createVertex("CMLink").field("link", link.getLink()).field("title", link.getTitle()).field("abstract", link.getDescription()).save();
	}

	//Publication
    public String storePublication (Publication publication) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            ODocument publicationO = storePublication(publication, database);
            return toString(publicationO.getIdentity());
        } finally {
            if (database!=null)
                database.close();
        }
    }
	public Collection<ODocument> storePublication(Collection<Publication> publications, OGraphDatabase database) throws Exception {
		Collection<ODocument> publicationsO = new ArrayList<ODocument>();
		for (Publication publication : publications)
			publicationsO.add(storePublication(publication, database));
		return publicationsO;
	}
	public ODocument storePublication(Publication publication, OGraphDatabase database) throws Exception {
        ODocument publicationO = loadDAO.loadPublicationO(publication.getId(), database);
        if (publicationO!=null)
            return publicationO;
		return database.createVertex("CMPublication").field("link", publication.getLink()).field("title", publication.getTitle()).field("abstract", publication.getDescription()).field("date", publication.getPublicationDate()).save();
	}



    //context system
    public int deleteContext(String name) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            return deleteContext(name, database);
        } finally {
            if (database!=null)
                database.close();
        }
    }
    public int deleteContext(String name, OGraphDatabase database) throws Exception {
        ODocument dsdcontext = dsdLoadDAO.loadContextSystem(name, database);
        if (dsdcontext==null)
            return 0;
        disconnectContext(dsdcontext, database);
        dsdcontext.delete();
        return 1;
    }
    private void disconnectContext(ODocument contextO, OGraphDatabase database) throws Exception {
        for (ODocument dsd : dsdLoadDAO.loadDsdByContextSystem(contextO, database)) {
            dsd.field("contextSystem", null, OType.LINK);
            dsd.save();
        }
    }

    public ODocument storeContext(DSDContextSystem context, OGraphDatabase database) throws Exception {
        ODocument dsdcontext = dsdLoadDAO.loadContextSystem(context.getName(), database);
        return dsdcontext!=null ? dsdcontext : database.createVertex("DSDContextSystem").field("name", context.getName()).save();
    }

    public int storeContext(DSDContextSystem context) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        int count=0;
        try {
            storeContext(context, database);
        } finally {
            if (database!=null)
                database.close();
        }
        return count;
    }

}
