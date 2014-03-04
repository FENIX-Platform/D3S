package org.fao.fenix.d3s.search.services.impl;

import java.util.*;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dao.dm.DMConverter;
import org.fao.fenix.d3s.msd.dao.dsd.DSDLoad;
import org.fao.fenix.d3s.msd.dto.cl.Code;
import org.fao.fenix.d3s.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.msd.dto.dm.DM;
import org.fao.fenix.d3s.msd.dto.dm.type.DMCopyrightType;
import org.fao.fenix.d3s.msd.dto.dm.type.DMDataKind;
import org.fao.fenix.d3s.msd.dto.dm.type.DMDataType;
import org.fao.fenix.d3s.msd.dto.dsd.DSD;
import org.fao.fenix.d3s.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.d3s.msd.dto.dsd.DSDDimension;
import org.fao.fenix.d3s.search.SearchFlow;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.SearchDataResponse;
import org.fao.fenix.d3s.search.dto.SearchFilter;
import org.fao.fenix.d3s.search.dto.SearchResponse;
import org.fao.fenix.d3s.search.dto.valueFilters.ColumnValueFilter;
import org.fao.fenix.d3s.search.dto.valueFilters.ValueFilterType;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SearchOperation extends SearchStep {
    @Autowired private DMConverter dmConverter;
    @Autowired private CodeListLoad clDao;
    @Autowired private DSDLoad dsdDao;

	protected static Properties initProperties;
	public static void init(Properties properties) { initProperties = properties; }
	
	
	public SearchResponse search (SearchFilter filter) throws Exception {
        OGraphDatabase database = getDatabase(OrientDatabase.msd);
        try {
            //Cache database instance
            getFlow().setMsdDatabase(database);
            //Cache initial business parameters
            getFlow().setBusinessParameters(filter.getParameters());
            //Store filter codes link
            loadFilterCodes(filter,getFlow(),database);

            //Execute search
            return searchFlow(filter);
        } finally {
            if (database!=null)
                database.close();
        }
    }

    protected abstract SearchResponse searchFlow (SearchFilter filter) throws Exception;


    //Utils
    //Load filter codes
    private void loadFilterCodes(SearchFilter filter, SearchFlow flowData, OGraphDatabase database) throws Exception {
        for (Collection<ColumnValueFilter> filterValues : filter.getDimensions().values())
            for (ColumnValueFilter filterValue : filterValues)
                if (filterValue.getType()== ValueFilterType.code) {
                    Code code = filterValue.getCode();
                    ODocument codeO = clDao.loadCodeO(code.getSystemKey(),code.getSystemVersion(),code.getCode(),database);
                    if (codeO==null)
                        throw new Exception("Search filter contains unknown code: "+code);
                    flowData.addLoadedCode(code,codeO);
                }
    }


    protected SearchDataResponse buildResponse (SearchFilter filter, SearchStep source, ODocument masterDataset) throws Exception {
        boolean infoMeta = Boolean.FALSE.equals(filter.getParameter("metadata")) ? false : true;
        boolean doDistinct = Boolean.FALSE.equals(filter.getParameter("distinct")) ? false : true;
        boolean label = doDistinct && Boolean.TRUE.equals(filter.getParameter("label"));
        SearchFlow flow = getFlow();
        SearchDataResponse response = new SearchDataResponse();

        Collection<Object>[] distinct = new Collection[source.columnsNumber];
        for (int i=0; i<distinct.length; i++)
            distinct[i] = new TreeSet<Object>();
        Collection<Object[]> data = new LinkedList<Object[]>();

        for (Object[] sRow : source) {
            data.add(sRow);
            if (doDistinct)
                for (int i=0; i<sRow.length; i++)
                    if (sRow[i]!=null)
                        distinct[i].add(sRow[i]);
        }

        if (label)
            for (int i=0; i<distinct.length; i++) {
                CodeSystem system = source.structure[i].getCodeSystem();
                if (system!=null) {
                    Set<Object> distinctCodes = new TreeSet<Object>();
                    for (Object v : distinct[i]) {
                        Code code = new Code(system,(String)v);
                        ODocument codeO = flow.getLoadedCode(code);
                        code.setLevel((Integer)codeO.field("level"));
                        code.setTitle((Map<String,String>)codeO.field("title",Map.class));
                        code.setDescription((Map<String,String>)codeO.field("abstract",Map.class));
                        distinctCodes.add(code);
                    }
                    distinct[i] = distinctCodes;
                }
            }


        response.setData(data);
        response.setCount(data.size());
        response.setDm(createMetadata(source, distinct, infoMeta ? masterDataset : null));

        return response;
    }

    private DM createMetadata (SearchStep source, Collection<Object>[] distinct, ODocument masterDataset) throws Exception {
        //Define metadata
        Date date = new Date();

        DSDContextSystem cs = new DSDContextSystem();
        cs.setName("D3S");

        DSD dsd = new DSD();
        dsd.setContextSystem(cs);
        dsd.setStartDate(date);
        dsd.setEndDate(date);

        DM dm = null;
        if (masterDataset!=null)
            dm = dmConverter.toDM(masterDataset,false);
        else {
            dm = new DM();
            dm.setDataKind(DMDataKind.automated);
            dm.setCopyright(DMCopyrightType.publicPolicy);
            dm.setDataType(DMDataType.dataset);
            dm.setCreationDate(date);
        }
        dm.setDsd(dsd);

        //Define structure
        OGraphDatabase database = getFlow().getMsdDatabase();
        if (source.structure != null) {
            for (int i=0; i<source.columnsNumber; i++) {
                if (i!=source.valueIndex)
                    source.structure[i].setValues(distinct[i]);
                DSDDimension dimension = dsdDao.loadDimension(source.structure[i].getColumnId(), database);
                source.structure[i].setDimension(dimension);
                source.structure[i].setTitle(dimension.getTitle());
            }
            dm.getDsd().setColumns(Arrays.asList(source.structure));
        }

        //Return structure
        return dm;
    }




}
