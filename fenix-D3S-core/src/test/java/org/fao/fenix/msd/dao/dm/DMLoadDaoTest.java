package org.fao.fenix.msd.dao.dm;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.Iterator;
import java.util.SortedSet;

import org.fao.fenix.msd.dao.cl.CodeSystemInsertDaoTest;
import org.fao.fenix.msd.dao.commons.CommonsLoadDaoTest;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.common.Link;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMAvailability;
import org.fao.fenix.msd.dto.dm.type.DMCopyrightType;
import org.fao.fenix.msd.dto.dm.type.DMDataKind;
import org.fao.fenix.msd.dto.dm.type.DMDataType;
import org.fao.fenix.msd.dto.dm.type.DMStatus;
import org.fao.fenix.msd.dto.dsd.DSD;
import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDatasource;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.fao.fenix.msd.dto.dsd.type.DSDDao;
import org.fao.fenix.msd.dto.dsd.type.DSDDataType;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.junit.Before;
import org.junit.Test;

public class DMLoadDaoTest extends DMInsertDaoTest {

	DMLoad daoLoad;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		daoLoad = SpringContext.getBean(DMLoad.class);
	}

	@Test
	public void testLoadMetadata() {
		try {
			daoCleaner.cleanALL();
			CodeSystem cl = CodeSystemInsertDaoTest.createCodeSystem1();
			daoCodeListStore.storeCodeList(cl);
			daoStore.storeDatasetMetadata(createDM(cl));

			DM dm = daoLoad.loadDatasetMetadata("d1", false);
			check(dm);
			check(dm.getDsd());
		} catch (Exception e) {
			fail("Errore di sistema: "+e.getMessage());
			e.printStackTrace();
		}
	}

	private void check(DM dm) {
		assertNotNull(dm);
		assertEquals("d1", dm.getUid());
		assertEquals("dataset title 1", dm.getTitle().get("EN"));
		assertEquals("descrizione dataset 1", dm.getDescription().get("IT"));
		assertEquals("dataset supplemental 1", dm.getSupplemental().get("EN"));
		assertEquals("accuracy 1", dm.getAccuracy());
		assertEquals("1", dm.getCompleteness());
		assertEquals(DMDataKind.census, dm.getDataKind());
		assertEquals(DMDataType.dataset, dm.getDataType());
		assertEquals(DMCopyrightType.sharedPolicy, dm.getCopyright());
		assertEquals("EN", dm.getLanguage());
//		assertEquals(DMPeriodicityType.dayly, dm.getPeriodicity());
		
		DMAvailability availability = dm.getAvailability();
		assertNotNull(availability);
		assertTrue(availability.getStatus()==DMStatus.toUpload);
		assertEquals((Integer)10, availability.getChunksNumber());
		SortedSet<Integer> indexes = availability.getChunksIndex();
		assertEquals(3, indexes.size());
		Iterator<Integer> indexIterator = indexes.iterator();
		assertEquals((Integer)1, indexIterator.next());
		assertEquals((Integer)3, indexIterator.next());
		assertEquals((Integer)7, indexIterator.next());
		
		Collection<Link> links = dm.getTransferOptions();
		assertNotNull(links);
		assertEquals(2, links.size());
		Iterator<Link> linkList = links.iterator();
		CommonsLoadDaoTest.check1(linkList.next());
		CommonsLoadDaoTest.check1(linkList.next());
		
		assertEquals("code 1", dm.getCategories().iterator().next().getCode());
		assertEquals("code 1", dm.getGeographicExtent().getCode());
	}

	private void check(DSD dsd) {
		
		assertNotNull(dsd);
		assertEquals("note dsd 1", dsd.getSupplemental().get("IT"));
		
		DSDDatasource datasource = dsd.getDatasource();
		assertNotNull(datasource);
		assertTrue(DSDDao.cstat==datasource.getDao());
		assertEquals("password1", datasource.getReference().get("psw"));
		
		DSDContextSystem context = dsd.getContextSystem();
		assertNotNull(context);
		assertEquals("context 1",context.getName());

		Collection<DSDColumn> columns = dsd.getColumns();
		assertNotNull(columns);
		assertEquals(2,columns.size());
		Iterator<DSDColumn> columnList = columns.iterator();
		
		//Column1
		DSDColumn column = columnList.next();
		assertEquals("column 1", column.getColumnId());
		assertEquals(2, (int)column.getCodesLevel());
		assertEquals("geo layer 1", column.getGeoLyer());
		assertEquals("titolo colonna 1", column.getTitle().get("IT"));
		assertEquals("note colonna 1", column.getSupplemental().get("IT"));
		assertNotNull(column.getValues());
		assertArrayEquals(new Object[]{"value 1", "value 2"}, column.getValues().toArray(new Object[column.getValues().size()]));
		assertEquals("virtual column ID 1",column.getVirtualColumn());
		assertEquals(DSDDataType.text, column.getDataType());
		assertEquals("column 1",column.getColumnId());
		assertEquals("column 1",column.getColumnId());
		assertNotNull(column.getCodeSystem());
		assertEquals("sys1", column.getCodeSystem().getSystem());
		assertEquals("1.0", column.getCodeSystem().getVersion());
		
		DSDDimension dimension = column.getDimension();
		assertNotNull(dimension);
		assertEquals("dimension 1", dimension.getName());
		assertEquals("titolo dimensione 1", dimension.getTitle().get("IT"));
		
		//Column 2
		column = columnList.next();
		assertEquals("column 2", column.getColumnId());
		assertEquals(3, (int)column.getCodesLevel());
		assertEquals("geo layer 2", column.getGeoLyer());
		assertEquals("titolo colonna 2", column.getTitle().get("IT"));
		assertEquals("note colonna 2", column.getSupplemental().get("IT"));
		assertNotNull(column.getValues());
		assertArrayEquals(new Object[]{
                new Code("sys1","1.0","code 1"),
                new Code("sys1","1.0","code 2")
            }, column.getValues().toArray(new Object[column.getValues().size()]));
		assertEquals("INTERNAL",column.getVirtualColumn());
		assertEquals(DSDDataType.code, column.getDataType());
		assertEquals("column 2",column.getColumnId());
		assertEquals("column 2",column.getColumnId());
		assertNotNull(column.getCodeSystem());
		assertEquals("sys1", column.getCodeSystem().getSystem());
		assertEquals("1.0", column.getCodeSystem().getVersion());

		dimension = column.getDimension();
		assertNotNull(dimension);
		assertEquals(DSDDimension.ELEMENT_DIMENSION.getName(), dimension.getName());
		assertEquals("titolo dimensione 2", dimension.getTitle().get("IT"));
		
	}
}
