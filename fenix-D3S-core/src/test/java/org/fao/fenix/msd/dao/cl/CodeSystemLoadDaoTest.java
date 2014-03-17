package org.fao.fenix.msd.dao.cl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.cl.type.CSSharingPolicy;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;
import org.junit.Before;
import org.junit.Test;

public class CodeSystemLoadDaoTest extends CodeSystemInsertDaoTest {

	CodeListLoad daoLoad;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		daoLoad = SpringContext.getBean(CodeListLoad.class);
	}

	@Test
	public void testReadCodeSystem() {
		try {
			daoCleaner.cleanCodeList();
			daoStore.storeCodeList(createCodeSystem1());
			CodeSystem system = daoLoad.loadSystem("sys1", "1.0", true);
			
			assertEquals("sys1", system.getSystem());
			assertEquals("1.0", system.getVersion());
			assertTrue(CSSharingPolicy.publicPolicy==system.getSharingPolicy());
			assertEquals(new Integer(2), system.getLevelsNumber());
			assertEquals("titolo sistema 1", system.getTitle().get("IT"));
			assertEquals("system abstract 1", system.getDescription().get("EN"));
			assertEquals("code 4", system.getRegion().getCode());

			boolean[] keywordsCorrispondence = new boolean[3];
			for (String keyword : system.getKeyWords())
				if (keyword.equals("k1"))
					keywordsCorrispondence[0] = true;
				else if (keyword.equals("k2"))
					keywordsCorrispondence[1] = true;
				else if (keyword.equals("k3"))
					keywordsCorrispondence[2] = true;
			assertTrue(keywordsCorrispondence[0]&&keywordsCorrispondence[1]&&keywordsCorrispondence[2]);

			assertEquals("code 2", system.getRootCodes().iterator().next().getChilds().iterator().next().getCode());
			assertEquals("code 2", system.getRootCodes().iterator().next().getExclusionList().iterator().next().getCode());
			
			Collection<Code> nodes = daoLoad.loadCodeBranch("sys1", "1.0", "code 2");
			assertEquals(2, nodes.size());
			
			Iterator<Code> branchIterator = nodes.iterator();
			assertEquals("code 1", branchIterator.next().getCode());
			assertEquals("code 2", branchIterator.next().getCode());
			
			Collection<Code> level1Codes = daoLoad.loadCodeLevel("sys1", "1.0", 1);
			assertEquals(3, level1Codes.size());
			Iterator<Code> levelIterator = level1Codes.iterator();
			assertEquals("code 2", levelIterator.next().getCode());
			assertEquals("code 3", levelIterator.next().getCode());
			assertEquals("code 5", levelIterator.next().getCode());
			
		} catch (Exception e) {
			fail("System error: "+e.getMessage());
			e.printStackTrace();
		}
	}
}
