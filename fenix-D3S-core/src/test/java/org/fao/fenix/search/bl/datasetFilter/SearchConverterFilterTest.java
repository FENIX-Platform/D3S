package org.fao.fenix.search.bl.datasetFilter;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.msd.dao.Cleaner;
import org.fao.fenix.msd.dao.cl.CodeListStore;
import org.fao.fenix.msd.dao.cl.CodeSystemInsertDaoTest;
import org.fao.fenix.msd.dao.dm.DMInsertDaoTest;
import org.fao.fenix.msd.dao.dm.DMLoad;
import org.fao.fenix.msd.dao.dm.DMStore;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.search.dto.valueFilters.ColumnValueFilter;
import org.fao.fenix.server.init.MainController;
import org.fao.fenix.server.tools.orient.OrientDatabase;
import org.fao.fenix.server.tools.spring.SpringContext;
import org.junit.*;

import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SearchConverterFilterTest extends SearchStep {
    SearchConverterFilter converterFilter;
    CodeListStore daoCodeListStore;
    DMStore daoStore;
    DMLoad daoLoadDM;
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
        converterFilter = SpringContext.getBean(SearchConverterFilter.class);
        daoStore = SpringContext.getBean(DMStore.class);
        daoCleaner = SpringContext.getBean(Cleaner.class);
        daoCodeListStore = SpringContext.getBean(CodeListStore.class);
        daoLoadDM = SpringContext.getBean(DMLoad.class);
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
        Collection<ODocument> datasets = daoLoadDM.loadDatasetMetadataO(database);
        assertNotNull(datasets);
        assertEquals(1,datasets.size());

        SearchFilter filter = new SearchFilter();
        filter.addDimensionFilter("dimension 1", new ColumnValueFilter("name 1"));
        filter.addDimensionFilter(DSDDimension.ELEMENT_DIMENSION.getName(), new ColumnValueFilter(new Code("sys1","1.0","code 1")));
        filter.addDimensionFilter(DSDDimension.ELEMENT_DIMENSION.getName(), new ColumnValueFilter(new Code("sys10","1.0","code 1")));
        Collection<ODocument> filteredDatasets = converterFilter.filter(filter, datasets);
        assertNotNull(filteredDatasets);
        assertEquals(1, filteredDatasets.size());

        filter = new SearchFilter();
        filter.addDimensionFilter("dimension 1", new ColumnValueFilter("name 1"));
        filter.addDimensionFilter(DSDDimension.ELEMENT_DIMENSION.getName(), new ColumnValueFilter(new Code("sys1","2.0","code 1")));
        filteredDatasets = converterFilter.filter(filter, datasets);
        assertNotNull(filteredDatasets);
        assertEquals(1, filteredDatasets.size());
    }
}
