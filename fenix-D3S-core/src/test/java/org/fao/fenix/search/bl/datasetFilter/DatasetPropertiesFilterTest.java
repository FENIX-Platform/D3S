package org.fao.fenix.search.bl.datasetFilter;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.fao.fenix.d3s.msd.dao.Cleaner;
import org.fao.fenix.d3s.msd.dao.cl.CodeListStore;
import org.fao.fenix.d3s.search.bl.datasetFilter.DatasetPropertiesFilter;
import org.fao.fenix.msd.dao.cl.CodeSystemInsertDaoTest;
import org.fao.fenix.msd.dao.dm.DMInsertDaoTest;
import org.fao.fenix.d3s.msd.dao.dm.DMStore;
import org.fao.fenix.d3s.msd.dto.cl.Code;
import org.fao.fenix.d3s.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.d3s.search.dto.valueFilters.ColumnValueFilter;
import org.fao.fenix.d3s.server.init.MainController;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class DatasetPropertiesFilterTest extends SearchStep {
	DatasetPropertiesFilter daoSearch;
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
		daoSearch = SpringContext.getBean(DatasetPropertiesFilter.class);
		daoStore = SpringContext.getBean(DMStore.class);
		daoCleaner = SpringContext.getBean(Cleaner.class);
		daoCodeListStore = SpringContext.getBean(CodeListStore.class);
        getFlow().setMsdDatabase(getDatabase(OrientDatabase.msd));

		daoCleaner.cleanALL();
		CodeSystem cl = CodeSystemInsertDaoTest.createCodeSystem1();
		daoCodeListStore.storeCodeList(cl);
        daoStore.storeDatasetMetadata(DMInsertDaoTest.createDM(cl));
	}

	@After
	public void tearDown() throws Exception { }

	@Test
	public void testFilter1() throws Exception {
        SearchFilter filter = new SearchFilter();
        filter.addFieldFilter("source.name", new ColumnValueFilter("name 1"));
        filter.addFieldFilter("source.surname", new ColumnValueFilter("surname 1"));
        filter.addFieldFilter("region",new ColumnValueFilter(new Code("sys1", "1.0", "code 1")));
        Collection<ODocument> datasets = daoSearch.filter(filter, null);
        assertNotNull(datasets);
        assertEquals(1,datasets.size());

        filter = new SearchFilter();
        filter.addFieldFilter("source.name", new ColumnValueFilter("name 1"));
        filter.addFieldFilter("source.surname", new ColumnValueFilter("surname 1"));
        filter.addFieldFilter("region",new ColumnValueFilter(new Code("sys1", "1.1", "code 1")));
        datasets = daoSearch.filter(filter, null);
        assertNotNull(datasets);
        assertEquals(0,datasets.size());

        filter = new SearchFilter();
        filter.addFieldFilter("source.name", new ColumnValueFilter("name 1"));
        filter.addFieldFilter("source.surname", new ColumnValueFilter("surname 2"));
        filter.addFieldFilter("region",new ColumnValueFilter(new Code("sys1", "1.0", "code 1")));
        datasets = daoSearch.filter(filter, null);
        assertNotNull(datasets);
        assertEquals(0,datasets.size());

        filter = new SearchFilter();
        datasets = daoSearch.filter(filter, null);
        assertNotNull(datasets);
        assertEquals(1,datasets.size());

        Calendar c = Calendar.getInstance();
        Date dateTo = c.getTime();
        c.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH)-1);
        Date dateFrom = c.getTime();
        filter = new SearchFilter();
        filter.addFieldFilter("creationDate", new ColumnValueFilter(dateFrom,dateTo));
        filter.addFieldFilter("region",new ColumnValueFilter(new Code("sys1", "1.0", "code 1")));
        long time = System.currentTimeMillis();
        datasets = daoSearch.filter(filter, null);
        assertNotNull(datasets);
        assertEquals(1,datasets.size());

        filter = new SearchFilter();
        datasets = daoSearch.filter(filter, null);
        ColumnValueFilter filterValue = new ColumnValueFilter();
        filterValue.setRegExp("*suppl*");
        filter.addFieldFilter("supplemental",filterValue);
        assertNotNull(datasets);
        assertEquals(1,datasets.size());

    }

}
