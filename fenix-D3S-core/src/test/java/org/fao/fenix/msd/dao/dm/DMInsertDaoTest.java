package org.fao.fenix.msd.dao.dm;

import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Date;

import org.fao.fenix.d3s.msd.dao.Cleaner;
import org.fao.fenix.d3s.msd.dao.cl.CodeListStore;
import org.fao.fenix.d3s.msd.dao.dm.DMStore;
import org.fao.fenix.msd.dao.cl.CodeSystemInsertDaoTest;
import org.fao.fenix.msd.dao.commons.CommonsInsertDaoTest;
import org.fao.fenix.msd.dao.dsd.DSDInsertDaoTest;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.dm.DM;
import org.fao.fenix.commons.msd.dto.dm.DMAvailability;
import org.fao.fenix.commons.msd.dto.dm.type.DMCopyrightType;
import org.fao.fenix.commons.msd.dto.dm.type.DMDataKind;
import org.fao.fenix.commons.msd.dto.dm.type.DMDataType;
import org.fao.fenix.commons.msd.dto.dm.type.DMStatus;
import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DMInsertDaoTest {

	CodeListStore daoCodeListStore;
	DMStore daoStore;
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
		daoStore = SpringContext.getBean(DMStore.class);
		daoCleaner = SpringContext.getBean(Cleaner.class);
		daoCodeListStore = SpringContext.getBean(CodeListStore.class);
	}

	@After
	public void tearDown() throws Exception { }

	@Test
	public void testInsertMetadata() {
		try {
			daoCleaner.cleanALL();
			CodeSystem cl = CodeSystemInsertDaoTest.createCodeSystem1();
			daoCodeListStore.storeCodeList(cl);
			daoStore.storeDatasetMetadata(createDM(cl));
		} catch (Exception e) {
			fail("Errore di sistema: "+e.getMessage());
			e.printStackTrace();
		}
	}

	
	
	//Utils
	public static DM createDM(CodeSystem cl) {
		DM dm = new DM();
		dm.setUid("d1");
		dm.addDescription("IT", "descrizione dataset 1");
		dm.addDescription("EN", "dataset description 1");
		dm.addTitle("IT", "titolo dataset 1");
		dm.addTitle("EN", "dataset title 1");
		dm.addSummary("IT", "note dataset 1");
		dm.addSummary("EN", "dataset supplemental 1");
		dm.addSupplemental("IT", "note dataset 1");
		dm.addSupplemental("EN", "dataset supplemental 1");
		dm.addAccuracy("IT", "accuratezza 1");
		dm.addAccuracy("EN", "accuracy 1");
		dm.addCompletness("IT", "completezza 1");
		dm.addCompletness("EN", "completeness 1");
		dm.setDataKind(DMDataKind.census);
		dm.setDataType(DMDataType.dataset);
		dm.setCopyright(DMCopyrightType.sharedPolicy);
		dm.setLanguage("EN");
//		dm.setPeriodicity(DMPeriodicityType.dayly);
		dm.addCompilationProcessing("IT", "processo elaborazione 1");
		dm.addCompilationProcessing("EN", "compilation processing 1");
		dm.setCreationDate(new Date());
		dm.setUpdateDate(new Date());
		
		DMAvailability availability = new DMAvailability();
		availability.setStatus(DMStatus.toUpload);
		availability.setChunksNumber(10);
		availability.addChunkIndex(7);
		availability.addChunkIndex(3);
		availability.addChunkIndex(1);
		dm.setAvailability(availability);
		
        dm.addSource(CommonsInsertDaoTest.createIdentity1());
		dm.addTransferOption(CommonsInsertDaoTest.createLink1());
		dm.addTransferOption(CommonsInsertDaoTest.createLink1());

		Collection<Code> codes = cl.getRootCodes();
		dm.setCategories(codes);
		dm.setGeographicExtent(codes.iterator().next());
        //Set structure
        dm.setDsd(DSDInsertDaoTest.createDSD1());

		return dm;
	}
}
