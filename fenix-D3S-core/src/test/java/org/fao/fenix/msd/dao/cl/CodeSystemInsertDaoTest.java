package org.fao.fenix.msd.dao.cl;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.fao.fenix.d3s.msd.dao.Cleaner;
import org.fao.fenix.d3s.msd.dao.cl.CodeListStore;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.cl.type.CSSharingPolicy;
import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CodeSystemInsertDaoTest {

	CodeListStore daoStore;
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
		daoStore = SpringContext.getBean(CodeListStore.class);
		daoCleaner = SpringContext.getBean(Cleaner.class);
	}

	@After
	public void tearDown() throws Exception { }

	@Test
	public void testInsertCodeSystem() {
		try {
			daoCleaner.cleanCodeList();
			daoStore.storeCodeList(createCodeSystem1());
		} catch (Exception e) {
			fail("Errore di sistema: "+e.getMessage());
			e.printStackTrace();
		}
	}

	
	
	//Utils
	public static CodeSystem createCodeSystem1() {
		CodeSystem system = new CodeSystem();
		system.setSystem("sys1");
		system.setVersion("1.0");
		system.decodedKeyWords("k1|k2|k3");
		system.setLevelsNumber(2);
		system.setSharingPolicy(CSSharingPolicy.publicPolicy);
		system.setStartDate(new Date());
		system.setEndDate(new Date());
		system.setVirtualDate(new Date());
		
		Map<String,String> label = new HashMap<String,String>();
		label.put("IT", "titolo sistema 1");
		label.put("EN", "system title 1");
		system.setTitle(label);

		label = new HashMap<String,String>();
		label.put("IT", "descrizione sistema 1");
		label.put("EN", "system abstract 1");
		system.setDescription(label);
				
		Code code = new Code();
		code.setCode("code 1");
		code.setLevel(0);
		code.addTitle("IT","titolo codice 1");
		code.addTitle("EN","code title 1");
		code.addDescription("IT", "descrizione codice 1");
		code.addDescription("EN", "code description 1");
		code.addSupplemental("IT", "note codice 1");
		code.addSupplemental("EN", "code notes 1");
		
		Code code2 = new Code();
		code2.setCode("code 2");
		code2.setLevel(1);
		code2.addTitle("IT","titolo codice 2");
		code2.addTitle("EN","code title 2");
		code2.addDescription("IT", "descrizione codice 2");
		code2.addDescription("EN", "code description 2");
		code2.addSupplemental("IT", "note codice 2");
		code2.addSupplemental("EN", "code notes 2");

		Code code3 = new Code();
		code3.setCode("code 3");
		code3.setLevel(1);
		code3.addTitle("IT","titolo codice 3");
		code3.addTitle("EN","code title 3");
		code3.addDescription("IT", "descrizione codice 3");
		code3.addDescription("EN", "code description 3");
		code3.addSupplemental("IT", "note codice 3");
		code3.addSupplemental("EN", "code notes 5");
		
		code.addChild(code2);
		code.addChild(code3);
		code.addExclusion(code2);
		code.addExclusion(code3);
		
		Code code4 = new Code();
		code4.setCode("code 4");
		code4.setLevel(0);
		code4.addTitle("IT","titolo codice 4");
		code4.addTitle("EN","code title 4");
		code4.addDescription("IT", "descrizione codice 4");
		code4.addDescription("EN", "code description 4");
		code4.addSupplemental("IT", "note codice 4");
		code4.addSupplemental("EN", "code notes 4");
		
		Code code5 = new Code();
		code5.setCode("code 5");
		code5.setLevel(1);
		code5.addTitle("IT","titolo codice 5");
		code5.addTitle("EN","code title 5");
		code5.addDescription("IT", "descrizione codice 5");
		code5.addDescription("EN", "code description 5");
		code5.addSupplemental("IT", "note codice 5");
		code5.addSupplemental("EN", "code notes 5");
		
		code4.addChild(code5);
		
		system.addCode(code);
		system.addCode(code4);

		system.setRegion(code4);
		
		return system;
	}
}
