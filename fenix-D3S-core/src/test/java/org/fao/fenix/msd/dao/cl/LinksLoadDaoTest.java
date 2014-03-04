package org.fao.fenix.msd.dao.cl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.fao.fenix.d3s.msd.dao.cl.CodeListLinkLoad;
import org.fao.fenix.msd.dao.commons.CommonsLoadDaoTest;
import org.fao.fenix.d3s.msd.dto.cl.Code;
import org.fao.fenix.d3s.msd.dto.cl.CodeConversion;
import org.fao.fenix.d3s.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.d3s.msd.dto.cl.CodeRelationship;
import org.fao.fenix.d3s.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.msd.dto.cl.type.CodeRelationshipType;
import org.fao.fenix.d3s.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;
import org.junit.Before;
import org.junit.Test;

public class LinksLoadDaoTest extends LinksInsertDaoTest {

	CodeListLinkLoad daoLoad;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		daoLoad = SpringContext.getBean(CodeListLinkLoad.class);
	}

	@Test
	public void testReadRelationship() {
		try {
			CodeSystem system = CodeSystemInsertDaoTest.createCodeSystem1();
			CodeSystem wrongSystem = new CodeSystem("sys1", "1.1");
			Code code1 = new Code("sys1", "1.0", "code 1");
			Code code2 = new Code("sys1", "1.0", "code 2");
			Code code2b = new Code("sys1", "1.1", "code 2");
			Code code3 = new Code("sys1", "1.0", "code 3");
			CodeRelationship relation;
			Collection<CodeRelationship> relations;
			
			daoCleaner.cleanCodeList();
			daoCLStore.storeCodeList(system);
			daoCLLStore.storeCodeRelationship(createCodeRelation1());
			daoCLLStore.storeCodeRelationship(createCodeRelation2());
			//From CL
			relations = daoLoad.loadRelationshipsFromCL(system);
			assertNotNull(relations);
			assertEquals(3, relations.size());
			Boolean[] corrispondence = new Boolean[]{false,false,false};
			for (CodeRelationship r : relations) {
				if (	r.getFromCode().getCode().equals("code 1") &&
						r.getFromCode().getSystemKey().equals("sys1") &&
						r.getFromCode().getSystemVersion().equals("1.0") &&
						r.getToCode().getCode().equals("code 2") &&
						r.getToCode().getSystemKey().equals("sys1") &&
						r.getToCode().getSystemVersion().equals("1.0") &&
						r.getType()==CodeRelationshipType.oneToOne)
					corrispondence[0] = true;
				else if (r.getFromCode().getCode().equals("code 2") &&
						r.getFromCode().getSystemKey().equals("sys1") &&
						r.getFromCode().getSystemVersion().equals("1.0") &&
						r.getToCode().getCode().equals("code 3") &&
						r.getToCode().getSystemKey().equals("sys1") &&
						r.getToCode().getSystemVersion().equals("1.0") &&
						r.getType()==CodeRelationshipType.oneToMany)
					corrispondence[1] = true;
				else if (r.getFromCode().getCode().equals("code 2") &&
						r.getFromCode().getSystemKey().equals("sys1") &&
						r.getFromCode().getSystemVersion().equals("1.0") &&
						r.getToCode().getCode().equals("code 4") &&
						r.getToCode().getSystemKey().equals("sys1") &&
						r.getToCode().getSystemVersion().equals("1.0") &&
						r.getType()==CodeRelationshipType.oneToMany)
					corrispondence[2] = true;
			}
			assertTrue(corrispondence[0] && corrispondence[1] && corrispondence[2]);
			
			relations = daoLoad.loadRelationshipsFromCL(wrongSystem);
			assertNotNull(relations);
			assertEquals(0, relations.size());
			
			//FromCLtoCL
			relations = daoLoad.loadRelationshipsFromCLtoCL(system, system);
			assertNotNull(relations);
			assertEquals(3, relations.size());
			
			relations = daoLoad.loadRelationshipsFromCLtoCL(system, wrongSystem);
			assertNotNull(relations);
			assertEquals(0, relations.size());
			
			relations = daoLoad.loadRelationshipsFromCLtoCL(wrongSystem, system);
			assertNotNull(relations);
			assertEquals(0, relations.size());
			
			//FromCLType
			relations = daoLoad.loadRelationshipsFromCLType(system, CodeRelationshipType.oneToOne);
			assertNotNull(relations);
			assertEquals(1, relations.size());
			relation = relations.iterator().next();
			assertTrue(	relation.getFromCode().getCode().equals("code 1") &&
						relation.getFromCode().getSystemKey().equals("sys1") &&
						relation.getFromCode().getSystemVersion().equals("1.0") &&
						relation.getToCode().getCode().equals("code 2") &&
						relation.getToCode().getSystemKey().equals("sys1") &&
						relation.getToCode().getSystemVersion().equals("1.0") &&
						relation.getType()==CodeRelationshipType.oneToOne);
			
			relations = daoLoad.loadRelationshipsFromCLType(system, CodeRelationshipType.oneToMany);
			assertNotNull(relations);
			assertEquals(2, relations.size());
			
			relations = daoLoad.loadRelationshipsFromCLType(system, CodeRelationshipType.manyToOne);
			assertNotNull(relations);
			assertEquals(0, relations.size());
			
			relations = daoLoad.loadRelationshipsFromCLType(wrongSystem, CodeRelationshipType.oneToMany);
			assertNotNull(relations);
			assertEquals(0, relations.size());
			
			//FromCode
			relations = daoLoad.loadRelationshipsFromCode(code1);
			assertNotNull(relations);
			assertEquals(1, relations.size());
			relation = relations.iterator().next();
			assertTrue(	relation.getFromCode().getCode().equals("code 1") &&
						relation.getFromCode().getSystemKey().equals("sys1") &&
						relation.getFromCode().getSystemVersion().equals("1.0") &&
						relation.getToCode().getCode().equals("code 2") &&
						relation.getToCode().getSystemKey().equals("sys1") &&
						relation.getToCode().getSystemVersion().equals("1.0") &&
						relation.getType()==CodeRelationshipType.oneToOne);
			
			relations = daoLoad.loadRelationshipsFromCode(code2);
			assertNotNull(relations);
			assertEquals(2, relations.size());

			relations = daoLoad.loadRelationshipsFromCode(code3);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			relations = daoLoad.loadRelationshipsFromCode(code2b);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			//FromCodeToCL
			relations = daoLoad.loadRelationshipsFromCodeToCL(code1, system);
			assertNotNull(relations);
			assertEquals(1, relations.size());
			relation = relations.iterator().next();
			assertTrue(	relation.getFromCode().getCode().equals("code 1") &&
						relation.getFromCode().getSystemKey().equals("sys1") &&
						relation.getFromCode().getSystemVersion().equals("1.0") &&
						relation.getToCode().getCode().equals("code 2") &&
						relation.getToCode().getSystemKey().equals("sys1") &&
						relation.getToCode().getSystemVersion().equals("1.0") &&
						relation.getType()==CodeRelationshipType.oneToOne);
			
			relations = daoLoad.loadRelationshipsFromCodeToCL(code2, system);
			assertNotNull(relations);
			assertEquals(2, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeToCL(code3, system);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeToCL(code2b, system);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeToCL(code2, wrongSystem);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			//FromCodeToCode
			relations = daoLoad.loadRelationshipsFromCodeToCode(code1, code2);
			assertNotNull(relations);
			assertEquals(1, relations.size());
			relation = relations.iterator().next();
			assertTrue(	relation.getFromCode().getCode().equals("code 1") &&
						relation.getFromCode().getSystemKey().equals("sys1") &&
						relation.getFromCode().getSystemVersion().equals("1.0") &&
						relation.getToCode().getCode().equals("code 2") &&
						relation.getToCode().getSystemKey().equals("sys1") &&
						relation.getToCode().getSystemVersion().equals("1.0") &&
						relation.getType()==CodeRelationshipType.oneToOne);
			
			relations = daoLoad.loadRelationshipsFromCodeToCode(code2, code3);
			assertNotNull(relations);
			assertEquals(1, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeToCode(code3, code2);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeToCode(code2b, code2);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeToCode(code1, code3);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			//FromCodeType
			relations = daoLoad.loadRelationshipsFromCodeType(code1, CodeRelationshipType.oneToOne);
			assertNotNull(relations);
			assertEquals(1, relations.size());
			relation = relations.iterator().next();
			assertTrue(	relation.getFromCode().getCode().equals("code 1") &&
						relation.getFromCode().getSystemKey().equals("sys1") &&
						relation.getFromCode().getSystemVersion().equals("1.0") &&
						relation.getToCode().getCode().equals("code 2") &&
						relation.getToCode().getSystemKey().equals("sys1") &&
						relation.getToCode().getSystemVersion().equals("1.0") &&
						relation.getType()==CodeRelationshipType.oneToOne);
			
			relations = daoLoad.loadRelationshipsFromCodeType(code2, CodeRelationshipType.oneToMany);
			assertNotNull(relations);
			assertEquals(2, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeType(code2, CodeRelationshipType.oneToOne);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeType(code2, CodeRelationshipType.oneToOne);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeType(code2b, CodeRelationshipType.oneToOne);
			assertNotNull(relations);
			assertEquals(0, relations.size());

			relations = daoLoad.loadRelationshipsFromCodeType(code1, CodeRelationshipType.oneToMany);
			assertNotNull(relations);
			assertEquals(0, relations.size());
			
		} catch (Exception e) {
			fail("System error: "+e.getMessage());
			e.printStackTrace();
		}
	}


	@Test
	public void testReadConversions() {
		try {
			CodeSystem system = CodeSystemInsertDaoTest.createCodeSystem1();
			CodeSystem wrongSystem = new CodeSystem("sys1", "1.1");
			Code code1 = new Code("sys1", "1.0", "code 1");
			Code code2 = new Code("sys1", "1.0", "code 2");
			Code code2b = new Code("sys1", "1.1", "code 2");
			Code code3 = new Code("sys1", "1.0", "code 3");

			Map<String, Object> fixedParameters1 = new HashMap<String, Object>();
			fixedParameters1.put("fixedParam1", "fixed parameter 1");
			SortedMap<String, String> dynamicParameters1 = new TreeMap<String, String>();
			dynamicParameters1.put("dynamicParam1", "dynamic parameter 1");
			Map<String, Object> fixedParameters2 = new HashMap<String, Object>();
			fixedParameters2.put("fixedParam2", "fixed parameter 2");
			SortedMap<String, String> dynamicParameters2 = new TreeMap<String, String>();
			dynamicParameters2.put("dynamicParam2", "dynamic parameter 2");
			Map<String, Object> fixedParameters3 = new HashMap<String, Object>();
			fixedParameters3.put("fixedParam3", "fixed parameter 3");
			SortedMap<String, String> dynamicParameters3 = new TreeMap<String, String>();
			dynamicParameters3.put("dynamicParam3", "dynamic parameter 3");
			
			CodeConversion conversion;
			Collection<CodeConversion> conversions;
			
			daoCleaner.cleanCodeList();
			daoCLStore.storeCodeList(system);
			daoCLLStore.storeCodeConversion(createCodeConversion1());
			daoCLLStore.storeCodeConversion(createCodeConversion2());
			//From CL
			conversions = daoLoad.loadConversionsFromCL(system);
			assertNotNull(conversions);
			assertEquals(3, conversions.size());
			Boolean[] corrispondence = new Boolean[]{false,false,false};
			for (CodeConversion c : conversions) {
				if (	c.getFromCode().getCode().equals("code 1") &&
						c.getFromCode().getSystemKey().equals("sys1") &&
						c.getFromCode().getSystemVersion().equals("1.0") &&
						c.getToCode().getCode().equals("code 2") &&
						c.getToCode().getSystemKey().equals("sys1") &&
						c.getToCode().getSystemVersion().equals("1.0"))
					corrispondence[0] = true;
				else if (c.getFromCode().getCode().equals("code 2") &&
						c.getFromCode().getSystemKey().equals("sys1") &&
						c.getFromCode().getSystemVersion().equals("1.0") &&
						c.getToCode().getCode().equals("code 3") &&
						c.getToCode().getSystemKey().equals("sys1") &&
						c.getToCode().getSystemVersion().equals("1.0"))
					corrispondence[1] = true;
				else if (c.getFromCode().getCode().equals("code 2") &&
						c.getFromCode().getSystemKey().equals("sys1") &&
						c.getFromCode().getSystemVersion().equals("1.0") &&
						c.getToCode().getCode().equals("code 4") &&
						c.getToCode().getSystemKey().equals("sys1") &&
						c.getToCode().getSystemVersion().equals("1.0"))
					corrispondence[2] = true;
				CommonsLoadDaoTest.check1(c.getConversionRule());
			}
			assertTrue(corrispondence[0] && corrispondence[1] && corrispondence[2]);
			
			conversions = daoLoad.loadConversionsFromCL(wrongSystem);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());
			
			//FromCLtoCL
			conversions = daoLoad.loadConversionsFromCLtoCL(system, system);
			assertNotNull(conversions);
			assertEquals(3, conversions.size());
			
			conversions = daoLoad.loadConversionsFromCLtoCL(system, wrongSystem);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());
			
			conversions = daoLoad.loadConversionsFromCLtoCL(wrongSystem, system);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());
			
			//FromCode
			conversions = daoLoad.loadConversionsFromCode(code1);
			assertNotNull(conversions);
			assertEquals(1, conversions.size());
			conversion = conversions.iterator().next();
			assertTrue(	conversion.getFromCode().getCode().equals("code 1") &&
						conversion.getFromCode().getSystemKey().equals("sys1") &&
						conversion.getFromCode().getSystemVersion().equals("1.0") &&
						conversion.getToCode().getCode().equals("code 2") &&
						conversion.getToCode().getSystemKey().equals("sys1") &&
						conversion.getToCode().getSystemVersion().equals("1.0"));
			
			conversions = daoLoad.loadConversionsFromCode(code2);
			assertNotNull(conversions);
			assertEquals(2, conversions.size());

			conversions = daoLoad.loadConversionsFromCode(code3);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());

			conversions = daoLoad.loadConversionsFromCode(code2b);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());

			//FromCodeToCL
			conversions = daoLoad.loadConversionsFromCodeToCL(code1, system);
			assertNotNull(conversions);
			assertEquals(1, conversions.size());
			conversion = conversions.iterator().next();
			assertTrue(	conversion.getFromCode().getCode().equals("code 1") &&
						conversion.getFromCode().getSystemKey().equals("sys1") &&
						conversion.getFromCode().getSystemVersion().equals("1.0") &&
						conversion.getToCode().getCode().equals("code 2") &&
						conversion.getToCode().getSystemKey().equals("sys1") &&
						conversion.getToCode().getSystemVersion().equals("1.0"));
			
			conversions = daoLoad.loadConversionsFromCodeToCL(code2, system);
			assertNotNull(conversions);
			assertEquals(2, conversions.size());

			conversions = daoLoad.loadConversionsFromCodeToCL(code3, system);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());

			conversions = daoLoad.loadConversionsFromCodeToCL(code2b, system);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());

			conversions = daoLoad.loadConversionsFromCodeToCL(code2, wrongSystem);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());

			//FromCodeToCode
			conversions = daoLoad.loadConversionsFromCodeToCode(code1, code2);
			assertNotNull(conversions);
			assertEquals(1, conversions.size());
			conversion = conversions.iterator().next();
			assertTrue(	conversion.getFromCode().getCode().equals("code 1") &&
						conversion.getFromCode().getSystemKey().equals("sys1") &&
						conversion.getFromCode().getSystemVersion().equals("1.0") &&
						conversion.getToCode().getCode().equals("code 2") &&
						conversion.getToCode().getSystemKey().equals("sys1") &&
						conversion.getToCode().getSystemVersion().equals("1.0"));
			
			conversions = daoLoad.loadConversionsFromCodeToCode(code2, code3);
			assertNotNull(conversions);
			assertEquals(1, conversions.size());

			conversions = daoLoad.loadConversionsFromCodeToCode(code3, code2);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());

			conversions = daoLoad.loadConversionsFromCodeToCode(code2b, code2);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());

			conversions = daoLoad.loadConversionsFromCodeToCode(code1, code3);
			assertNotNull(conversions);
			assertEquals(0, conversions.size());
			
		} catch (Exception e) {
			fail("System error: "+e.getMessage());
			e.printStackTrace();
		}
	}



	@Test
	public void testReadPropaedeutics() {
		try {
			CodeSystem system = CodeSystemInsertDaoTest.createCodeSystem1();
			CodeSystem wrongSystem = new CodeSystem("sys1", "1.1");
			Code code1 = new Code("sys1", "1.0", "code 1");
			Code code2 = new Code("sys1", "1.0", "code 2");
			Code code2b = new Code("sys1", "1.1", "code 2");
			Code code3 = new Code("sys1", "1.0", "code 3");
			CodePropaedeutic propaedeutic;
			Collection<CodePropaedeutic> propaedeutics;
			
			daoCleaner.cleanCodeList();
			daoCLStore.storeCodeList(system);
			DSDContextSystem context = new DSDContextSystem();
			context.setName("dsd context 1");
			daoDsdStore.storeContext(context);
			
			daoCLLStore.storeCodePropaedeutic(createCodePropaedeutic1(context));
			daoCLLStore.storeCodePropaedeutic(createCodePropaedeutic2(context));
			//From CL
			propaedeutics = daoLoad.loadPropaedeuticsFromCL(system);
			assertNotNull(propaedeutics);
			assertEquals(3, propaedeutics.size());
			Boolean[] corrispondence = new Boolean[]{false,false,false};
			for (CodePropaedeutic p : propaedeutics) {
				if (	p.getFromCode().getCode().equals("code 1") &&
						p.getFromCode().getSystemKey().equals("sys1") &&
						p.getFromCode().getSystemVersion().equals("1.0") &&
						p.getToCode().getCode().equals("code 2") &&
						p.getToCode().getSystemKey().equals("sys1") &&
						p.getToCode().getSystemVersion().equals("1.0") &&
						p.getContextSystem().getName().equals(context.getName()))
					corrispondence[0] = true;
				else if (p.getFromCode().getCode().equals("code 2") &&
						p.getFromCode().getSystemKey().equals("sys1") &&
						p.getFromCode().getSystemVersion().equals("1.0") &&
						p.getToCode().getCode().equals("code 3") &&
						p.getToCode().getSystemKey().equals("sys1") &&
						p.getToCode().getSystemVersion().equals("1.0") &&
						p.getContextSystem().getName().equals(context.getName()))
					corrispondence[1] = true;
				else if (p.getFromCode().getCode().equals("code 2") &&
						p.getFromCode().getSystemKey().equals("sys1") &&
						p.getFromCode().getSystemVersion().equals("1.0") &&
						p.getToCode().getCode().equals("code 4") &&
						p.getToCode().getSystemKey().equals("sys1") &&
						p.getToCode().getSystemVersion().equals("1.0") &&
						p.getContextSystem().getName().equals(context.getName()))
					corrispondence[2] = true;
			}
			assertTrue(corrispondence[0] && corrispondence[1] && corrispondence[2]);
			
			propaedeutics = daoLoad.loadPropaedeuticsFromCL(wrongSystem);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());
			
			//FromCLtoCL
			propaedeutics = daoLoad.loadPropaedeuticsFromCLtoCL(system, system);
			assertNotNull(propaedeutics);
			assertEquals(3, propaedeutics.size());
			
			propaedeutics = daoLoad.loadPropaedeuticsFromCLtoCL(system, wrongSystem);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());
			
			propaedeutics = daoLoad.loadPropaedeuticsFromCLtoCL(wrongSystem, system);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());
			
			//FromCode
			propaedeutics = daoLoad.loadPropaedeuticsFromCode(code1);
			assertNotNull(propaedeutics);
			assertEquals(1, propaedeutics.size());
			propaedeutic = propaedeutics.iterator().next();
			assertTrue(	propaedeutic.getFromCode().getCode().equals("code 1") &&
						propaedeutic.getFromCode().getSystemKey().equals("sys1") &&
						propaedeutic.getFromCode().getSystemVersion().equals("1.0") &&
						propaedeutic.getToCode().getCode().equals("code 2") &&
						propaedeutic.getToCode().getSystemKey().equals("sys1") &&
						propaedeutic.getToCode().getSystemVersion().equals("1.0") &&
						propaedeutic.getContextSystem().getName().equals(context.getName()));
			
			propaedeutics = daoLoad.loadPropaedeuticsFromCode(code2);
			assertNotNull(propaedeutics);
			assertEquals(2, propaedeutics.size());

			propaedeutics = daoLoad.loadPropaedeuticsFromCode(code3);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());

			propaedeutics = daoLoad.loadPropaedeuticsFromCode(code2b);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());

			//FromCodeToCL
			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCL(code1, system);
			assertNotNull(propaedeutics);
			assertEquals(1, propaedeutics.size());
			propaedeutic = propaedeutics.iterator().next();
			assertTrue(	propaedeutic.getFromCode().getCode().equals("code 1") &&
						propaedeutic.getFromCode().getSystemKey().equals("sys1") &&
						propaedeutic.getFromCode().getSystemVersion().equals("1.0") &&
						propaedeutic.getToCode().getCode().equals("code 2") &&
						propaedeutic.getToCode().getSystemKey().equals("sys1") &&
						propaedeutic.getToCode().getSystemVersion().equals("1.0") &&
						propaedeutic.getContextSystem().getName().equals(context.getName()));
			
			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCL(code2, system);
			assertNotNull(propaedeutics);
			assertEquals(2, propaedeutics.size());

			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCL(code3, system);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());

			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCL(code2b, system);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());

			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCL(code2, wrongSystem);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());

			//FromCodeToCode
			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCode(code1, code2);
			assertNotNull(propaedeutics);
			assertEquals(1, propaedeutics.size());
			propaedeutic = propaedeutics.iterator().next();
			assertTrue(	propaedeutic.getFromCode().getCode().equals("code 1") &&
						propaedeutic.getFromCode().getSystemKey().equals("sys1") &&
						propaedeutic.getFromCode().getSystemVersion().equals("1.0") &&
						propaedeutic.getToCode().getCode().equals("code 2") &&
						propaedeutic.getToCode().getSystemKey().equals("sys1") &&
						propaedeutic.getToCode().getSystemVersion().equals("1.0") &&
						propaedeutic.getContextSystem().getName().equals(context.getName()));
			
			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCode(code2, code3);
			assertNotNull(propaedeutics);
			assertEquals(1, propaedeutics.size());

			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCode(code3, code2);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());

			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCode(code2b, code2);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());

			propaedeutics = daoLoad.loadPropaedeuticsFromCodeToCode(code1, code3);
			assertNotNull(propaedeutics);
			assertEquals(0, propaedeutics.size());
			
		} catch (Exception e) {
			fail("System error: "+e.getMessage());
			e.printStackTrace();
		}
	}


	//Utils
	
	
//	private boolean equals(Map<String,?> m1, Map<String,?> m2) {
//		if (m1==m2)
//			return true;
//		if (m1!=null) {
//			for (Map.Entry<String, ?> entry : m1.entrySet())
//				if (!entry.getValue().equals(m2.get(entry.getKey())))
//					return false;
//			return true;
//		}
//		return false;
//	}

}
