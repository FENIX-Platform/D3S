package org.fao.fenix.search.bl.datasetFilter;

import static org.junit.Assert.*;

import java.util.Collection;

import org.fao.fenix.d3s.msd.dao.Cleaner;
import org.fao.fenix.d3s.msd.dao.cl.CodeListStore;
import org.fao.fenix.d3s.search.bl.datasetFilter.DatasetDimensionsFilter;
import org.fao.fenix.d3s.search.bl.datasetFilter.DatasetPropertiesFilter;
import org.fao.fenix.msd.dao.cl.CodeSystemInsertDaoTest;
import org.fao.fenix.msd.dao.dm.DMInsertDaoTest;
import org.fao.fenix.d3s.msd.dao.dm.DMLoad;
import org.fao.fenix.d3s.msd.dao.dm.DMStore;
import org.fao.fenix.d3s.msd.dto.cl.Code;
import org.fao.fenix.d3s.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.msd.dto.dsd.DSDDimension;
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

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class DatasetDimensionsFilterTest extends SearchStep {

	DatasetDimensionsFilter daoFilter;
	DatasetPropertiesFilter daoSearch;
	CodeListStore daoCodeListStore;
	DMStore daoStore;
    DMLoad dmLoad;
	Cleaner daoCleaner;
    OGraphDatabase database;

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
		daoFilter = SpringContext.getBean(DatasetDimensionsFilter.class);
		daoSearch = SpringContext.getBean(DatasetPropertiesFilter.class);
		daoStore = SpringContext.getBean(DMStore.class);
		dmLoad = SpringContext.getBean(DMLoad.class);
		daoCleaner = SpringContext.getBean(Cleaner.class);
		daoCodeListStore = SpringContext.getBean(CodeListStore.class);
        getFlow().setMsdDatabase(database = getDatabase(OrientDatabase.msd));

		daoCleaner.cleanALL();
		CodeSystem cl = CodeSystemInsertDaoTest.createCodeSystem1();
		daoCodeListStore.storeCodeList(cl);
		daoStore.storeDatasetMetadata(DMInsertDaoTest.createDM(cl));
	}

	@After
	public void tearDown() throws Exception { }

	
	@Test
	public void testFilter() throws Exception {
        Collection<ODocument> datasets = dmLoad.loadDatasetMetadataO(database);
        assertNotNull(datasets);
        assertEquals(1,datasets.size());

        SearchFilter filter = new SearchFilter();
        filter.addDimensionFilter("dimension 1", new ColumnValueFilter("value 2"));
        filter.addDimensionFilter(DSDDimension.ELEMENT_DIMENSION.getName(), new ColumnValueFilter(new Code("sys1", "1.0", "code 1")));
        Collection<ODocument> filteredDatasets = daoFilter.filter(filter, datasets);
        assertNotNull(filteredDatasets);
        assertEquals(1,filteredDatasets.size());

        filter = new SearchFilter();
        ColumnValueFilter likeValueFilter = new ColumnValueFilter();
        likeValueFilter.setRegExp("*ue 2");
        filter.addDimensionFilter("dimension 1", new ColumnValueFilter("value 3"));
        filter.addDimensionFilter("dimension 1", likeValueFilter);
        filter.addDimensionFilter(DSDDimension.ELEMENT_DIMENSION.getName(), new ColumnValueFilter(new Code("sys1", "1.0", "code 2")));
        filteredDatasets = daoFilter.filter(filter, datasets);
        assertNotNull(filteredDatasets);
        assertEquals(1,filteredDatasets.size());

        filter = new SearchFilter();
        likeValueFilter = new ColumnValueFilter();
        likeValueFilter.setRegExp("*ue 2");
        filter.addDimensionFilter("dimension 1", new ColumnValueFilter("value 3"));
        filter.addDimensionFilter("dimension 1", likeValueFilter);
        filter.addDimensionFilter(DSDDimension.ELEMENT_DIMENSION.getName(), new ColumnValueFilter(new Code("sys1", "1.0", "code 3")));
        filteredDatasets = daoFilter.filter(filter, datasets);
        assertNotNull(filteredDatasets);
        assertEquals(0,filteredDatasets.size());

        filter = new SearchFilter();
        likeValueFilter = new ColumnValueFilter();
        likeValueFilter.setRegExp("*ue2");
        filter.addDimensionFilter("dimension 1", new ColumnValueFilter("value 3"));
        filter.addDimensionFilter("dimension 1", likeValueFilter);
        filteredDatasets = daoFilter.filter(filter, datasets);
        assertNotNull(filteredDatasets);
        assertEquals(0,filteredDatasets.size());

        filter = new SearchFilter();
        likeValueFilter = new ColumnValueFilter();
        likeValueFilter.setRegExp("ue 2");
        filter.addDimensionFilter("dimension 1", new ColumnValueFilter("value 3"));
        filter.addDimensionFilter("dimension 1", likeValueFilter);
        filteredDatasets = daoFilter.filter(filter, datasets);
        assertNotNull(filteredDatasets);
        assertEquals(0,filteredDatasets.size());

        filter = new SearchFilter();
        likeValueFilter = new ColumnValueFilter();
        filter.addDimensionFilter("dimension 1", new ColumnValueFilter("value 2"));
        filter.addDimensionFilter(DSDDimension.ELEMENT_DIMENSION.getName(), new ColumnValueFilter(new Code("sys1", "1.0", "code 3")));
        filteredDatasets = daoFilter.filter(filter, datasets);
        assertNotNull(filteredDatasets);
        assertEquals(0,filteredDatasets.size());

	}

}
