package org.fao.fenix.msd.dao.cl;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;

import org.fao.fenix.d3s.msd.dao.Cleaner;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dao.cl.CodeListStore;
import org.fao.fenix.d3s.msd.dto.cl.Code;
import org.fao.fenix.d3s.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.intent.OIntentMassiveInsert;

public class CodeSystemLoadDaoStressTest {

	CodeListLoad daoLoad;
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
		daoLoad = SpringContext.getBean(CodeListLoad.class);
		daoStore = SpringContext.getBean(CodeListStore.class);
		daoCleaner = SpringContext.getBean(Cleaner.class);
	}
	
	private void insertSystems(int number) throws Exception {
		daoCleaner.cleanCodeList();
		OGraphDatabase database = daoStore.getDatabase(OrientDatabase.msd);
		try {
			database.declareIntent( new OIntentMassiveInsert() );
			CodeSystem system = CodeSystemInsertDaoTest.createCodeSystem1();
			for(int i=0;i<number;i++) {
				system.setVersion("1."+i);
				daoStore.storeCodeList(system, database);
			}
			database.declareIntent(null);
		} finally {
			if (database!=null)
				database.close();
		}
		
	}

	@Test
	public void testReadCodeSystem() throws Exception{
		
			int clNumber = 100000;
			
			insertSystems(clNumber);
			
			long time, c=100, total=0, max=0, min=Long.MAX_VALUE;
			for (int i=0; i<clNumber;i+=clNumber/100) {
				String version = "1."+i;
				time = System.currentTimeMillis();
				CodeSystem system = daoLoad.loadSystem("sys1", version, true);
				Collection<Code> branch = daoLoad.loadCodeBranch("sys1", version, "code 2");
				Collection<Code> level1Codes = daoLoad.loadCodeLevel("sys1", version, 1);
				time = System.currentTimeMillis()-time;

				total+=time;
				if (time<min)
					min = time;
				if (time>max)
					max = time;
				
				assertNotNull(system);
				
				assertEquals("sys1", system.getSystem());
				assertEquals("titolo sistema 1", system.getTitle().get("IT"));
				assertEquals("code 2", system.getRootCodes().iterator().next().getChilds().iterator().next().getCode());

				assertEquals(2, branch.size());
				Iterator<Code> branchIterator = branch.iterator();
				assertEquals("code 1", branchIterator.next().getCode());
				assertEquals("code 2", branchIterator.next().getCode());

				assertEquals(3, level1Codes.size());
				Iterator<Code> levelIterator = level1Codes.iterator();
				assertEquals("code 2", levelIterator.next().getCode());
				assertEquals("code 3", levelIterator.next().getCode());
				assertEquals("code 5", levelIterator.next().getCode());
			}
			
			System.out.println("Numero letture:"+c);
			System.out.println("Tempo totale:"+total);
			System.out.println("Tempo medio:"+(total/c));
			System.out.println("Tempo minimo:"+min);
			System.out.println("Tempo massimo:"+max);
			
	}
}
