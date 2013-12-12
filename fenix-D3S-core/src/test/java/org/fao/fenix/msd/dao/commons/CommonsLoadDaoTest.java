package org.fao.fenix.msd.dao.commons;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.fao.fenix.msd.dao.cl.CodeListStore;
import org.fao.fenix.msd.dao.cl.CodeSystemInsertDaoTest;
import org.fao.fenix.msd.dao.common.CommonsConverter;
import org.fao.fenix.msd.dao.common.CommonsLoad;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.common.Contact;
import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Link;
import org.fao.fenix.msd.dto.common.ValueOperator;
import org.fao.fenix.msd.dto.common.type.ContactType;
import org.fao.fenix.server.tools.orient.OrientDatabase;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.junit.Before;
import org.junit.Test;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class CommonsLoadDaoTest extends CommonsInsertDaoTest {

	CommonsConverter converter;
	CommonsLoad daoLoad;
	CodeListStore clStoreDao;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		daoLoad = SpringContext.getBean(CommonsLoad.class);
		clStoreDao = SpringContext.getBean(CodeListStore.class);
		converter = SpringContext.getBean(CommonsConverter.class);
	}

	@Test
	public void testLoadMetadata() {
		try {
			daoCleaner.cleanALL();
			CodeSystem cl = CodeSystemInsertDaoTest.createCodeSystem1();
			clStoreDao.storeCodeList(cl);
			String identityID = cmStoreDao.storeContactIdentity(createIdentity1());
			ODocument linkO = cmStoreDao.storeLink(createLink1(), cmStoreDao.getDatabase(OrientDatabase.msd));
			ODocument operatorO = cmStoreDao.storeValueOperator(createOperator1(), cmStoreDao.getDatabase(OrientDatabase.msd));
			
			ContactIdentity contact = daoLoad.loadContactIdentity(identityID);
			Link link = converter.toLink(linkO);
			ValueOperator operator = converter.toOperator(operatorO);

			check1(contact);
			check1(link);
			check1(operator);
		} catch (Exception e) {
			fail("Errore di sistema: "+e.getMessage());
			e.printStackTrace();
		}
	}

	public static void check1(ContactIdentity contactIdentity) {
		assertNotNull(contactIdentity);
		assertNotNull(contactIdentity.getId());
		assertEquals("contact title 1", contactIdentity.getTitle().get("EN"));
		assertEquals("descrizione contatto 1", contactIdentity.getDescription().get("IT"));
		assertEquals("contact supplemental 1", contactIdentity.getSupplemental().get("EN"));
		assertEquals("institution 1", contactIdentity.getInstitution());
		assertEquals("depatment 1", contactIdentity.getDepartment());
		assertEquals("name 1", contactIdentity.getName());
		assertEquals("surname 1", contactIdentity.getSurname());
		assertEquals(new Code("sys1","1.0","code 2"), contactIdentity.getRegion());
		assertEquals(new Code("sys1","1.0","code 3"), contactIdentity.getRole());
		
		Iterator<Contact> contacts = contactIdentity.getContactList().iterator();
		Contact contact = contacts.next();
		assertTrue(contact.getType()==ContactType.mobile);
		assertEquals("+863331234567", contact.getContact());
		contact = contacts.next();
		assertTrue(contact.getType()==ContactType.email);
		assertEquals("aaaaa@bb.ccc", contact.getContact());
	}

	public static void check1(Link contactIdentity) {
		assertNotNull(contactIdentity);
		assertEquals("link title 1", contactIdentity.getTitle().get("EN"));
		assertEquals("descrizione link 1", contactIdentity.getDescription().get("IT"));
		assertEquals("http://host/link1", contactIdentity.getLink());
	}

	public static void check1(ValueOperator aggregation) {
		assertNotNull(aggregation);
		assertEquals("implementation.class.name.1", aggregation.getImplementation());
		assertEquals("rule 1", aggregation.getRule());
		assertNotNull(aggregation.getFixedParameters());
		assertEquals(2, aggregation.getFixedParameters().size());
		assertEquals(10, aggregation.getFixedParameters().get("param1"));
		assertEquals("value2", aggregation.getFixedParameters().get("param2"));
	}
}
