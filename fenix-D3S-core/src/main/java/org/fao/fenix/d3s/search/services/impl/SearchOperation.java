package org.fao.fenix.d3s.search.services.impl;

import java.util.*;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.search.dto.Response;
import org.fao.fenix.commons.search.dto.filter.*;
import org.fao.fenix.commons.search.dto.resource.Resource;
import org.fao.fenix.commons.search.dto.resource.data.DataType;
import org.fao.fenix.commons.search.dto.resource.data.LayerData;
import org.fao.fenix.commons.search.dto.resource.data.TableData;
import org.fao.fenix.commons.utils.JSONUtils;
import org.fao.fenix.d3s.search.dto.OutputParameters;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.msd.dao.cl.CodeListLoad;
import org.fao.fenix.d3s.msd.dao.dm.DMConverter;
import org.fao.fenix.d3s.msd.dao.dsd.DSDLoad;
import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.dm.DM;
import org.fao.fenix.commons.msd.dto.dm.type.DMCopyrightType;
import org.fao.fenix.commons.msd.dto.dm.type.DMDataKind;
import org.fao.fenix.commons.msd.dto.dm.type.DMDataType;
import org.fao.fenix.commons.msd.dto.dsd.DSD;
import org.fao.fenix.commons.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.commons.msd.dto.dsd.DSDDimension;
import org.fao.fenix.d3s.search.SearchFlow;
import org.fao.fenix.d3s.search.SearchStep;

import javax.inject.Inject;

public abstract class SearchOperation extends SearchStep {
    @Inject private DMConverter dmConverter;
    @Inject private CodeListLoad clDao;
    @Inject private DSDLoad dsdDao;

	protected static Properties initProperties;
	public static void init(Properties properties) { initProperties = properties; }
	
	
	public Response search (Filter filter) throws Exception {
        SearchFlow flowData = getFlow();
        //Cache initial business parameters
        RequiredPlugin[] businessPlugins = filter.getBusiness();
        flowData.setBusinessParameters(businessPlugins!=null && businessPlugins.length>0 ? businessPlugins[0].getProperties() : null);
        decodeOutputParameters(flowData);
        //Store filter codes link
        loadFilterCodes(filter.getFilter(),flowData);

        //Execute search
        return searchFlow(filter.getFilter());
    }

    protected abstract Response searchFlow (ResourceFilter filter) throws Exception;


    //Utils
    //Set output parameters as a Map<String,OutputParameters>
    @SuppressWarnings("unchecked")
    private void decodeOutputParameters(SearchFlow flowData) throws Exception {
        Map<String,Object> outputParameters = (Map<String,Object>)flowData.getBusinessParameters().get("output");
        if (outputParameters!=null)
            for (Map.Entry<String,Object> pEntry : outputParameters.entrySet())
                flowData.putBusinessOutputParameter(pEntry.getKey(), JSONUtils.convertValue(pEntry.getValue(), OutputParameters.class));
    }

    //Load filter codes
    private void loadFilterCodes(ResourceFilter filter, SearchFlow flowData) throws Exception {
        for (Collection<ColumnValueFilter> filterValues : filter.getMetadata().values())
            loadFilterCodes(filterValues,flowData);
        for (Collection<ColumnValueFilter> filterValues : filter.getData().values())
            loadFilterCodes(filterValues,flowData);
    }
    private void loadFilterCodes(Collection<ColumnValueFilter> filterValues, SearchFlow flowData) throws Exception {
        for (ColumnValueFilter filterValue : filterValues)
            if (filterValue.getType()== ValueFilterType.code) {
                Code code = filterValue.getCode();
                ODocument codeO = clDao.loadCodeO(code.getSystemKey(),code.getSystemVersion(),code.getCode());
                if (codeO==null)
                    throw new Exception("Search filter contains unknown code: "+code);
                flowData.addLoadedCode(code,codeO);
            }
    }


    protected Collection<Resource> buildResponse (Collection<DM> datasets) {
        if (datasets!=null) {
            Collection<Resource> response = new LinkedList<>();
            for (DM dm : datasets) {
                DSD dsd = dm.getDsd();
                dm.setDsd(null);
                if (dm.getDataType()==DMDataType.layer) {
                    DataType dataType = DataType.raster;
                    switch (dm.getLayerType()) {
                        case points: dataType = DataType.points; break;
                        case lines: dataType = DataType.lines; break;
                        case polygons: dataType = DataType.polygons; break;
                    }
                    response.add(
                            new LayerData(
                                    dm.getUid(), // resource name
                                    "layer", //resource type
                                    "D3S", //sourceName
                                    null, //index
                                    dataType,
                                    dm, //metadata
                                    dsd, //dsd
                                    null, //data
                                    null) //size
                    );
                } else
                    response.add(
                            new TableData(
                                    dm.getUid(), // resource name
                                    "dataset", //resource type
                                    "D3S", //sourceName
                                    null, //index
                                    dm, //metadata
                                    dsd, //dsd
                                    null, //data
                                    null) //size
                    );
            }
            return response;
        } else
            return null;
    }

    protected Resource buildResponse (ResourceFilter filter, SearchStep source, ODocument masterDataset) throws Exception {
        SearchFlow flow = getFlow();

        boolean infoMeta = Boolean.FALSE.equals(flow.getBusinessParameter("metadata")) ? false : true;
        boolean doDistinct = Boolean.FALSE.equals(flow.getBusinessParameter("distinct")) ? false : true;
        boolean label = doDistinct && Boolean.TRUE.equals(flow.getBusinessParameter("label"));

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
                        ODocument codeO = flow.getLoadedCode(code, clDao);
                        code.setLevel((Integer)codeO.field("level"));
                        code.setTitle((Map<String,String>)codeO.field("title",Map.class));
                        code.setDescription((Map<String,String>)codeO.field("abstract",Map.class));
                        distinctCodes.add(code);
                    }
                    distinct[i] = distinctCodes;
                }
            }

        DM metadata = createMetadata(source, distinct, infoMeta ? masterDataset : null);
        DSD dsd = metadata.getDsd();
        metadata.setDsd(null);

        return new TableData(
                "D3S aggregation", // resource name
                "dataset", //resource type
                "D3S", //sourceName
                null, //index
                metadata, //metadata
                dsd, //dsd
                data, //data
                data!=null ? data.size() : null);
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
        if (source.structure != null) {
            for (int i=0; i<source.columnsNumber; i++) {
                if (i!=source.valueIndex)
                    source.structure[i].setValues(distinct[i]);
                DSDDimension dimension = dsdDao.loadDimension(source.structure[i].getColumnId());
                source.structure[i].setDimension(dimension);
                source.structure[i].setTitle(dimension.getTitle());
            }
            dm.getDsd().setColumns(Arrays.asList(source.structure));
        }

        //Return structure
        return dm;
    }




}
