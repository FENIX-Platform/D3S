package org.fao.fenix.d3s.msd.dao.canc.common;

import java.util.ArrayList;
import java.util.Collection;

import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDContextSystem;
import org.fao.fenix.d3s.msd.dao.canc.dsd.DSDLoad;
import org.fao.fenix.d3s.msd.dao.canc.cl.CodeListLoad;
import org.fao.fenix.commons.msd.dto.templates.canc.common.Contact;
import org.fao.fenix.commons.msd.dto.templates.canc.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.templates.canc.common.Link;
import org.fao.fenix.commons.msd.dto.templates.canc.common.Publication;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

import javax.inject.Inject;

public class CommonsStore extends OrientDao {
	@Inject private CommonsLoad loadDAO;
	@Inject private CodeListLoad loadClDAO;
    @Inject private DSDLoad dsdLoadDAO;

	//UPDATE
	//Contact Identity
	public int updateContactIdentity(ContactIdentity contact, boolean append) throws Exception {
        return append ? appendContactIdentity(contact) : updateContactIdentity(contact);
	}
	
	@SuppressWarnings("unchecked")
	public int updateContactIdentity(ContactIdentity contactIdentity) throws Exception {
		ODocument contactIdentityO = loadDAO.loadContactIdentityO(contactIdentity.getId());
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
			contactIdentityO.field("region", loadClDAO.loadCodeO(contactIdentity.getRegion().getSystemKey(), contactIdentity.getRegion().getSystemVersion(), contactIdentity.getRegion().getCode()));
		else
			contactIdentityO.field("region", null, OType.LINK);

		if (contactIdentity.getRole()!=null)
			contactIdentityO.field("role", loadClDAO.loadCodeO(contactIdentity.getRole().getSystemKey(), contactIdentity.getRole().getSystemVersion(), contactIdentity.getRole().getCode()));
		else
			contactIdentityO.field("role", null, OType.LINK);
		
		if (contactIdentity.getContext()!=null)
			contactIdentityO.field("context", storeContext(contactIdentity.getContext()));
		else
			contactIdentityO.field("context", null, OType.LINK);

		Collection<ODocument> contacts = contactIdentityO.field("contactList");
		if (contacts!=null)
			for (ODocument contactO : contacts)
				contactO.delete();
		contacts = new ArrayList<ODocument>();
		if (contactIdentity.getContactList()!=null)
			for (Contact contact : contactIdentity.getContactList())
				contacts.add(storeContact(contact));
		contactIdentityO.field("contactList", contacts.size()>0 ? contacts : null, OType.LINKLIST);

		contactIdentityO.save();
		return 1;
	}
	
	@SuppressWarnings("unchecked")
	public int appendContactIdentity(ContactIdentity contactIdentity) throws Exception {
		ODocument contactIdentityO = loadDAO.loadContactIdentityO(contactIdentity.getId());
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
			contactIdentityO.field("region", loadClDAO.loadCodeO(contactIdentity.getRegion().getSystemKey(), contactIdentity.getRegion().getSystemVersion(), contactIdentity.getRegion().getCode()));
		if (contactIdentity.getRole()!=null)
			contactIdentityO.field("role", loadClDAO.loadCodeO(contactIdentity.getRole().getSystemKey(), contactIdentity.getRole().getSystemVersion(), contactIdentity.getRole().getCode()));
        if (contactIdentity.getContext()!=null)
            contactIdentityO.field("context", storeContext(contactIdentity.getContext()));

		if (contactIdentity.getContactList()!=null) {
			Collection<ODocument> contacts = (Collection<ODocument>)contactIdentityO.field("contactList");
			if (contacts!=null)
				for (ODocument contactO : contacts)
					contactO.delete();
			contacts = new ArrayList<ODocument>();
			for (Contact contact : contactIdentity.getContactList())
				contacts.add(storeContact(contact));
			contactIdentityO.field("contactList", contacts.size()>0 ? contacts : null, OType.LINKLIST);
		}

		contactIdentityO.save();
		return 1;
	}

	
	//DELETE
	//Contact Identity
	public int deleteContatcIdentity(String id) throws Exception {
		ODocument contactIdentityO = loadDAO.loadContactIdentityO(id);
		if (contactIdentityO==null)
			return 0;
		//Disconnect identity
		disconnectContatcIdentity(contactIdentityO);
		//Delete identity
		return deleteGraph(contactIdentityO, new String[]{"CSCode"});
	}
	private void disconnectContatcIdentity(ODocument systemO) throws Exception { /* Nothing to do for the moment */ }

	//STORE
	//Contact Identity
	public Collection<ODocument> storeContactIdentity(Collection<ContactIdentity> contactIdentities) throws Exception {
		Collection<ODocument> contactIdentitiesO = new ArrayList<ODocument>();
		for (ContactIdentity contactIdentity : contactIdentities)
			contactIdentitiesO.add(storeContactIdentity(contactIdentity));
		return contactIdentitiesO;
	}
	
	public ODocument storeContactIdentity(ContactIdentity contactIdentity) throws Exception {
		ODocument contactIdentityO = loadDAO.loadContactIdentityO(contactIdentity.getId());
		if (contactIdentityO!=null)
			return contactIdentityO;
		
		contactIdentityO = getConnection().createVertex("CMContactIdentity");

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
			contactIdentityO.field("region", loadClDAO.loadCodeO(contactIdentity.getRegion().getSystemKey(), contactIdentity.getRegion().getSystemVersion(), contactIdentity.getRegion().getCode()));

		if (contactIdentity.getRole()!=null)
			contactIdentityO.field("role", loadClDAO.loadCodeO(contactIdentity.getRole().getSystemKey(), contactIdentity.getRole().getSystemVersion(), contactIdentity.getRole().getCode()));

        if (contactIdentity.getContext()!=null)
            contactIdentityO.field("context", storeContext(contactIdentity.getContext()));

		Collection<ODocument> contacts = new ArrayList<ODocument>();
		if (contactIdentity.getContactList()!=null)
			for (Contact contact : contactIdentity.getContactList())
				contacts.add(storeContact(contact));
		contactIdentityO.field("contactList", contacts.size()>0 ? contacts : null, OType.LINKLIST);

		return contactIdentityO.save();
	}
	
	private ODocument storeContact(Contact contact) throws Exception {
		return getConnection().createVertex("CMContact").field("contact", contact.getContact()).field("type", contact.getType()!=null?contact.getType().getCode():null).save();
	}

	//Link
	public Collection<ODocument> storeLink(Collection<Link> links) throws Exception {
		Collection<ODocument> linksO = new ArrayList<ODocument>();
		for (Link link : links)
			linksO.add(storeLink(link));
		return linksO;
	}

	public ODocument storeLink(Link link) throws Exception {
		return getConnection().createVertex("CMLink").field("link", link.getLink()).field("title", link.getTitle()).field("abstract", link.getDescription()).save();
	}

	//Publication
    public Collection<ODocument> storePublication(Collection<Publication> publications) throws Exception {
		Collection<ODocument> publicationsO = new ArrayList<ODocument>();
		for (Publication publication : publications)
			publicationsO.add(storePublication(publication));
		return publicationsO;
	}
	public ODocument storePublication(Publication publication) throws Exception {
        ODocument publicationO = loadDAO.loadPublicationO(publication.getId());
        if (publicationO!=null)
            return publicationO;
		return getConnection().createVertex("CMPublication").field("link", publication.getLink()).field("title", publication.getTitle()).field("abstract", publication.getDescription()).field("date", publication.getPublicationDate()).save();
	}



    //context system
    public int deleteContext(String name) throws Exception {
        ODocument dsdcontext = dsdLoadDAO.loadContextSystem(name);
        if (dsdcontext==null)
            return 0;
        disconnectContext(dsdcontext);
        dsdcontext.delete();
        return 1;
    }
    private void disconnectContext(ODocument contextO) throws Exception {
        for (ODocument dsd : dsdLoadDAO.loadDsdByContextSystem(contextO)) {
            dsd.field("contextSystem", null, OType.LINK);
            dsd.save();
        }
    }

    public ODocument storeContext(DSDContextSystem context) throws Exception {
        ODocument dsdcontext = dsdLoadDAO.loadContextSystem(context.getName());
        return dsdcontext!=null ? dsdcontext : getConnection().createVertex("DSDContextSystem").field("name", context.getName()).save();
    }

}
