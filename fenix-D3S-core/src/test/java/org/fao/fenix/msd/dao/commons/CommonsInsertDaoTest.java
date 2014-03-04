package org.fao.fenix.msd.dao.commons;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.fao.fenix.d3s.msd.dao.Cleaner;
import org.fao.fenix.d3s.msd.dao.common.CommonsStore;
import org.fao.fenix.d3s.msd.dto.cl.Code;
import org.fao.fenix.d3s.msd.dto.common.Contact;
import org.fao.fenix.d3s.msd.dto.common.ContactIdentity;
import org.fao.fenix.d3s.msd.dto.common.Link;
import org.fao.fenix.d3s.msd.dto.common.ValueOperator;
import org.fao.fenix.d3s.msd.dto.common.type.ContactType;
import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CommonsInsertDaoTest {

	CommonsStore cmStoreDao;
	Cleaner daoCleaner;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MainController.startupModules();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MainController.shutdownModules();
	}

	@Before
	public void setUp() throws Exception {
		cmStoreDao = SpringContext.getBean(CommonsStore.class);
		daoCleaner = SpringContext.getBean(Cleaner.class);
	}

	@After
	public void tearDown() throws Exception { }

	@Test
	public void testInsertCodeSystem() {
		try {
			daoCleaner.cleanCommons();
			cmStoreDao.storeContactIdentity(createIdentity1());
			cmStoreDao.storeLink(createLink1(), cmStoreDao.getDatabase(OrientDatabase.msd));
			cmStoreDao.storeValueOperator(createOperator1(), cmStoreDao.getDatabase(OrientDatabase.msd));
		} catch (Exception e) {
			fail("Errore di sistema: "+e.getMessage());
			e.printStackTrace();
		}
	}

	
	
	//Utils
	public static Link createLink1() {
		Link link = new Link();
		
		Map<String,String> label = new HashMap<String,String>();
		label.put("IT", "titolo link 1");
		label.put("EN", "link title 1");
		link.setTitle(label);
		
		label = new HashMap<String,String>();
		label.put("IT", "descrizione link 1");
		label.put("EN", "link description 1");
		link.setDescription(label);

		link.setLink("http://host/link1");
		
		return link;
	}
	
	public static ValueOperator createOperator1() {
		ValueOperator operator = new ValueOperator();
		operator.setImplementation("implementation.class.name.1");
		operator.setRule("rule 1");
		operator.addFixedParameter("param1", 10);
		operator.addFixedParameter("param2", "value2");
		return operator;
	}
	
	public static ContactIdentity createIdentity1() {
		ContactIdentity contactIdentity = new ContactIdentity();
		contactIdentity.setInstitution("institution 1");
		contactIdentity.setDepartment("depatment 1");
		contactIdentity.setName("name 1");
		contactIdentity.setSurname("surname 1");

		Map<String,String> label = new HashMap<String,String>();
		label.put("IT", "titolo contatto 1");
		label.put("EN", "contact title 1");
		contactIdentity.setTitle(label);
		
		label = new HashMap<String,String>();
		label.put("IT", "descrizione contatto 1");
		label.put("EN", "contact description 1");
		contactIdentity.setDescription(label);
		
		label = new HashMap<String,String>();
		label.put("IT", "note contatto 1");
		label.put("EN", "contact supplemental 1");
		contactIdentity.setSupplemental(label);

		contactIdentity.setRegion(new Code("sys1","1.0","code 2"));
		contactIdentity.setRole(new Code("sys1","1.0","code 3"));
		
		contactIdentity.addContact(new Contact(ContactType.mobile,"+863331234567"));
		contactIdentity.addContact(new Contact(ContactType.email,"aaaaa@bb.ccc"));

		return contactIdentity;
	}
}
