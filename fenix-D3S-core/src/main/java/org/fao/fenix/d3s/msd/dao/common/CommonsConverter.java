package org.fao.fenix.d3s.msd.dao.common;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.fao.fenix.d3s.msd.dao.cl.CodeListConverter;
import org.fao.fenix.d3s.msd.dao.dsd.DSDConverter;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;
import org.fao.fenix.commons.msd.dto.common.Contact;
import org.fao.fenix.commons.msd.dto.common.ContactIdentity;
import org.fao.fenix.commons.msd.dto.common.Link;
import org.fao.fenix.commons.msd.dto.common.Publication;
import org.fao.fenix.commons.msd.dto.common.ValueOperator;
import org.fao.fenix.commons.msd.dto.common.type.ContactType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class CommonsConverter {
	private @Autowired
    CodeListConverter clConverter;
	private @Autowired
    DSDConverter dsdConverter;

	//Identity conversion
	public Collection<ContactIdentity> toContactIdentity (Collection<ODocument> identitiesO) {
		Collection<ContactIdentity> identities = new LinkedList<ContactIdentity>();
		for (ODocument identityO : identitiesO)
			identities.add(toContactIdentity(identityO));
		return identities;
	}

	@SuppressWarnings("unchecked")
	public ContactIdentity toContactIdentity (ODocument identityO) {
		if (identityO == null)
			return null;
		
		ContactIdentity identity = new ContactIdentity();
		identity.setId(OrientDao.toString(identityO.getIdentity()));
		identity.setInstitution((String)identityO.field("institution"));
		identity.setDepartment((String)identityO.field("department"));
		identity.setName((String)identityO.field("name"));
		identity.setSurname((String)identityO.field("surname"));
		identity.setTitle((Map<String,String>)identityO.field("title",Map.class));
		identity.setDescription((Map<String,String>)identityO.field("abstract",Map.class));
		identity.setSupplemental((Map<String,String>)identityO.field("supplemental",Map.class));

		//connected elements
		if (identityO.field("region")!=null)
			identity.setRegion(clConverter.toCode((ODocument)identityO.field("region"), false, CodeListConverter.NO_LEVELS));
		if (identityO.field("role")!=null)
			identity.setRole(clConverter.toCode((ODocument)identityO.field("role"), false, CodeListConverter.NO_LEVELS));
        if (identityO.field("context")!=null)
            identity.setContext(dsdConverter.toContext((ODocument)identityO.field("context")));

		Collection<ODocument> contacts = (Collection<ODocument>)identityO.field("contactList");
		if (contacts!=null)
			for (ODocument contactO : contacts)
				identity.addContact(toContact(contactO));

		return identity;
	}

	public Contact toContact (ODocument contactO) {
		Contact contact = new Contact();
		contact.setContact((String)contactO.field("contact"));
		contact.setType(ContactType.getByCode((String)contactO.field("type")));
		return contact;
	}

	//Link conversion
	public Collection<Link> toLink (Collection<ODocument> linksO) {
		Collection<Link> links = new LinkedList<Link>();
		for (ODocument linkO : linksO)
			links.add(toLink(linkO));
		return links;
	}
	
	@SuppressWarnings("unchecked")
	public Link toLink (ODocument linkO) {
		if (linkO==null)
			return null;
		Link link = new Link();
		link.setLink((String)linkO.field("link"));
		link.setTitle((Map<String,String>)linkO.field("title",Map.class));
		link.setDescription((Map<String,String>)linkO.field("abstract",Map.class));
		return link;
	}

	//Publication conversion
	public Collection<Publication> toPublication (Collection<ODocument> publicationsO) {
		Collection<Publication> publications = new LinkedList<Publication>();
		for (ODocument publicationO : publicationsO)
			publications.add(toPublication(publicationO));
		return publications;
	}
	
	@SuppressWarnings("unchecked")
	public Publication toPublication (ODocument publicationO) {
		if (publicationO==null)
			return null;
		Publication publication = new Publication();
        publication.setId(OrientDao.toString(publicationO.getIdentity()));
		publication.setLink((String)publicationO.field("link"));
		publication.setTitle((Map<String,String>)publicationO.field("title",Map.class));
		publication.setDescription((Map<String,String>)publicationO.field("abstract",Map.class));
		publication.setPublicationDate((Date)publicationO.field("date"));
		
		return publication;
	}

	//Value operator conversion
	public Collection<ValueOperator> toOperator (Collection<ODocument> operatorsO) {
        if (operatorsO!=null) {
            Collection<ValueOperator> operators = new LinkedList<ValueOperator>();
            for (ODocument operatorO : operatorsO)
                operators.add(toOperator(operatorO));
            return operators;
        }
        return null;
    }

	public ValueOperator toOperator (ODocument operatorO) {
		if (operatorO==null)
			return null;
		ValueOperator operator = new ValueOperator();
		operator.setImplementation((String)operatorO.field("implementation"));
		operator.setRule((String)operatorO.field("rule"));
		operator.setFixedParameters((Map<String,Object>)operatorO.field("fixedParameters",Map.class));
        operator.setDimension(dsdConverter.toDimension((ODocument)operatorO.field("dimension")));
		return operator;
	}


}