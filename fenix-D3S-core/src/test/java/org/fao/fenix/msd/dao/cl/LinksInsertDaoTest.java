package org.fao.fenix.msd.dao.cl;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.LinkedList;

import org.fao.fenix.msd.dao.Cleaner;
import org.fao.fenix.msd.dao.commons.CommonsInsertDaoTest;
import org.fao.fenix.msd.dao.dsd.DSDStore;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeConversion;
import org.fao.fenix.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.cl.type.CodeRelationshipType;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.server.init.MainController;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LinksInsertDaoTest {

	CodeListStore daoCLStore;
	CodeListLinkStore daoCLLStore;
	Cleaner daoCleaner;
	DSDStore daoDsdStore;

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
		daoCLStore = SpringContext.getBean(CodeListStore.class);
		daoCLLStore = SpringContext.getBean(CodeListLinkStore.class);
		daoCleaner = SpringContext.getBean(Cleaner.class);
		daoDsdStore = SpringContext.getBean(DSDStore.class);
	}

	@After
	public void tearDown() throws Exception { }

	@Test
	public void testInsertCodeRelation() {
		try {
			daoCleaner.cleanCodeList();
			daoCLStore.storeCodeList(CodeSystemInsertDaoTest.createCodeSystem1());
			daoCLLStore.storeCodeRelationship(createCodeRelation1());
			daoCLLStore.storeCodeRelationship(createCodeRelation2());
		} catch (Exception e) {
			fail("Errore di sistema: "+e.getMessage());
			e.printStackTrace();
		}
	}
	@Test
	public void testInsertCodeConversion() {
		try {
			daoCleaner.cleanCodeList();
			daoCLStore.storeCodeList(CodeSystemInsertDaoTest.createCodeSystem1());
			daoCLLStore.storeCodeConversion(createCodeConversion1());
			daoCLLStore.storeCodeConversion(createCodeConversion2());
		} catch (Exception e) {
			fail("Errore di sistema: "+e.getMessage());
			e.printStackTrace();
		}
	}
	@Test
	public void testInsertCodePropaedeutic() {
		try {
			daoCleaner.cleanCodeList();
			CodeSystem cl = CodeSystemInsertDaoTest.createCodeSystem1();
			daoCLStore.storeCodeList(cl);
			DSDContextSystem context = new DSDContextSystem();
			context.setName("dsd context 1");
			daoDsdStore.storeContext(context);
			
			daoCLLStore.storeCodePropaedeutic(createCodePropaedeutic1(context));
			daoCLLStore.storeCodePropaedeutic(createCodePropaedeutic2(context));
		} catch (Exception e) {
			fail("Errore di sistema: "+e.getMessage());
			e.printStackTrace();
		}
	}

	
	
	//Utils
	public static CodeRelationship createCodeRelation1() {
		CodeRelationship relation = new CodeRelationship();
		relation.setFromCode(new Code("sys1", "1.0", "code 1"));
		relation.setToCode(new Code("sys1", "1.0", "code 2"));
		relation.setType(CodeRelationshipType.oneToOne);
		return relation;
	}
	public static Collection<CodeRelationship> createCodeRelation2() {
		Collection<CodeRelationship> relations = new LinkedList<CodeRelationship>();
		CodeRelationship relation = new CodeRelationship();
		relation.setFromCode(new Code("sys1", "1.0", "code 2"));
		relation.setToCode(new Code("sys1", "1.0", "code 3"));
		relation.setType(CodeRelationshipType.oneToMany);
		relations.add(relation);
		relation = new CodeRelationship();
		relation.setFromCode(new Code("sys1", "1.0", "code 2"));
		relation.setToCode(new Code("sys1", "1.0", "code 4"));
		relation.setType(CodeRelationshipType.oneToMany);
		relations.add(relation);
		return relations;
	}
	public static CodeConversion createCodeConversion1() {
		CodeConversion conversion = new CodeConversion();
		conversion.setFromCode(new Code("sys1", "1.0", "code 1"));
		conversion.setToCode(new Code("sys1", "1.0", "code 2"));
		conversion.setConversionRule(CommonsInsertDaoTest.createOperator1());
		
		return conversion;
	}
	public static Collection<CodeConversion> createCodeConversion2() {
		Collection<CodeConversion> conversions = new LinkedList<CodeConversion>();
		CodeConversion conversion = new CodeConversion();
		conversion.setFromCode(new Code("sys1", "1.0", "code 2"));
		conversion.setToCode(new Code("sys1", "1.0", "code 3"));
		conversion.setConversionRule(CommonsInsertDaoTest.createOperator1());
		conversions.add(conversion);
		conversion = new CodeConversion();
		conversion.setFromCode(new Code("sys1", "1.0", "code 2"));
		conversion.setToCode(new Code("sys1", "1.0", "code 4"));
		conversion.setConversionRule(CommonsInsertDaoTest.createOperator1());
		conversions.add(conversion);
		return conversions;
	}
	public static CodePropaedeutic createCodePropaedeutic1(DSDContextSystem context) {
		CodePropaedeutic propaedeutic = new CodePropaedeutic();
		propaedeutic.setFromCode(new Code("sys1", "1.0", "code 1"));
		propaedeutic.setToCode(new Code("sys1", "1.0", "code 2"));
		propaedeutic.setContextSystem(context);
		return propaedeutic;
	}
	public static Collection<CodePropaedeutic> createCodePropaedeutic2(DSDContextSystem context) {
		Collection<CodePropaedeutic> propaedeutics = new LinkedList<CodePropaedeutic>();
		CodePropaedeutic propaedeutic = new CodePropaedeutic();
		propaedeutic.setFromCode(new Code("sys1", "1.0", "code 2"));
		propaedeutic.setToCode(new Code("sys1", "1.0", "code 3"));
		propaedeutic.setContextSystem(context);
		propaedeutics.add(propaedeutic);
		propaedeutic = new CodePropaedeutic();
		propaedeutic.setFromCode(new Code("sys1", "1.0", "code 2"));
		propaedeutic.setToCode(new Code("sys1", "1.0", "code 4"));
		propaedeutic.setContextSystem(context);
		propaedeutics.add(propaedeutic);
		return propaedeutics;
	}
	
}
