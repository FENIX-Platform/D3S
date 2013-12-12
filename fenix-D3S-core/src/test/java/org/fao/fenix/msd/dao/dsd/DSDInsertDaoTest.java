package org.fao.fenix.msd.dao.dsd;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.fao.fenix.msd.dao.Cleaner;
import org.fao.fenix.msd.dao.cl.CodeSystemInsertDaoTest;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.dsd.DSD;
import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDatasource;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.fao.fenix.msd.dto.dsd.type.DSDDao;
import org.fao.fenix.msd.dto.dsd.type.DSDDataType;
import org.fao.fenix.server.init.MainController;
import org.fao.fenix.server.tools.orient.OrientDatabase;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DSDInsertDaoTest {

	DSDStore daoStore;
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
		daoStore = SpringContext.getBean(DSDStore.class);
		daoCleaner = SpringContext.getBean(Cleaner.class);
	}

	@After
	public void tearDown() throws Exception { }

	@Test
	public void testInsertCodeSystem() {
		try {
			daoCleaner.cleanDSD();
			daoStore.storeDSD(createDSD1(), daoStore.getDatabase(OrientDatabase.msd));
		} catch (Exception e) {
			fail("Errore di sistema: "+e.getMessage());
			e.printStackTrace();
		}
	}

	
	
	//Utils
	public static DSD createDSD1() {
		DSD dsd = new DSD();
		dsd.addSupplemental("IT", "note dsd 1");
		dsd.addSupplemental("EN", "dsd note 1");
		dsd.setStartDate(new Date());
		dsd.setEndDate(new Date());

        CodeSystem system = CodeSystemInsertDaoTest.createCodeSystem1();
		
		DSDDatasource datasource = new DSDDatasource();
		Map<String,String> label = new HashMap<String,String>();
		label.put("IT", "titolo datasource 1");
		label.put("EN", "datasource title 1");
		datasource.setDao(DSDDao.cstat);
		label = new HashMap<String,String>();
		label.put("usr", "user1");
		label.put("psw", "password1");
		datasource.setReference(label);
		dsd.setDatasource(datasource);
		
		DSDContextSystem contextElement = new DSDContextSystem();
		contextElement.setName("context 1");
		dsd.setContextSystem(contextElement);
		
		Collection<DSDColumn> columns = new LinkedList<DSDColumn>();
		DSDColumn column = new DSDColumn();
		column.setColumnId("column 1");
		column.setCodesLevel(2);
		column.setGeoLyer("geo layer 1");
		label = new HashMap<String,String>();
		label.put("IT", "titolo colonna 1");
		label.put("EN", "column title 1");
		column.setTitle(label);
		label = new HashMap<String,String>();
		label.put("IT", "note colonna 1");
		label.put("EN", "column notes 1");
		column.setSupplemental(label);
		column.setValues(Arrays.asList(new Object[]{"value 1", "value 2"}));
		column.setVirtualColumn("virtual column ID 1");
		column.setDataType(DSDDataType.text);
		column.setCodeSystem(system);
		
		DSDDimension dimension = new DSDDimension();
		dimension.setName("dimension 1");
		label = new HashMap<String,String>();
		label.put("IT", "titolo dimensione 1");
		label.put("EN", "dimension title 1");
		dimension.setTitle(label);
		column.setDimension(dimension);
		
		columns.add(column);

		column = new DSDColumn();
		column.setColumnId("column 2");
		column.setCodesLevel(3);
		column.setGeoLyer("geo layer 2");
		label = new HashMap<String,String>();
		label.put("IT", "titolo colonna 2");
		label.put("EN", "column title 2");
		column.setTitle(label);
		label = new HashMap<String,String>();
		label.put("IT", "note colonna 2");
		label.put("EN", "column notes 2");
		column.setSupplemental(label);
		column.setValues(Arrays.asList(new Object[]{
                new Code("sys1","1.0","code 1"),
                new Code("sys1","1.0","code 2")
            }));
		column.setVirtualColumn("INTERNAL");
		column.setDataType(DSDDataType.code);
		column.setCodeSystem(system);
		
		dimension = new DSDDimension();
		dimension.setName(DSDDimension.ELEMENT_DIMENSION.getName());
		label = new HashMap<String,String>();
		label.put("IT", "titolo dimensione 2");
		label.put("EN", "dimension title 2");
		dimension.setTitle(label);
		column.setDimension(dimension);
		
		columns.add(column);
		
		dsd.setColumns(columns);

		return dsd;
	}
}
