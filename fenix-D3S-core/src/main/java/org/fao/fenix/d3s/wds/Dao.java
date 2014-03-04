package org.fao.fenix.d3s.wds;

import java.util.*;

import org.fao.fenix.d3s.msd.dto.cl.Code;
import org.fao.fenix.d3s.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.msd.dto.dm.DM;
import org.fao.fenix.d3s.msd.dto.dm.type.DMCopyrightType;
import org.fao.fenix.d3s.msd.dto.dm.type.DMDataKind;
import org.fao.fenix.d3s.msd.dto.dm.type.DMDataType;
import org.fao.fenix.d3s.msd.dto.dsd.DSD;
import org.fao.fenix.d3s.msd.dto.dsd.DSDColumn;
import org.fao.fenix.d3s.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.d3s.msd.dto.dsd.DSDDimension;
import org.fao.fenix.d3s.msd.dto.dsd.type.DSDDataType;
import org.fao.fenix.d3s.search.SearchStep;
import org.fao.fenix.d3s.search.dto.OutputParameters;
import org.fao.fenix.d3s.search.dto.SearchFilter;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;

public abstract class Dao extends SearchStep {
	//Init
    protected Map<String, String> properties;

    public void init(Map<String,String> properties) {
        this.properties = properties;
    }
    //Interface
	public abstract void load(SearchFilter filter, ODocument dataset) throws Exception;
	public abstract void store(Iterable<Object[]> data, ODocument dataset) throws Exception;


    //Utils
    public DM createMetadata (ODocument dataset, Collection<ODocument> columnsO) {
        Date date = new Date();

        DSDContextSystem cs = new DSDContextSystem();
        cs.setName("cstat");

        DSD dsd = new DSD();
        dsd.setContextSystem(cs);
        dsd.setStartDate(date);
        dsd.setEndDate(date);

        OGraphDatabase database = getFlow().getMsdDatabase();
        Collection<DSDColumn> columns = new LinkedList<DSDColumn>();
        for (ODocument columnO : columnsO)
			columns.add(createColumnMetadata(columnO,database));
        dsd.setColumns(columns);

        DM dm = new DM();
        dm.setDataKind(DMDataKind.automated);
        dm.setCopyright(DMCopyrightType.privatePolicy);
        dm.setDataType(DMDataType.getByCode((String)dataset.field("datatype")));
        dm.setUid((String)dataset.field("uid"));
        dm.setCreationDate(date);
        dm.setDsd(dsd);

        return dm;
    }
    
    @SuppressWarnings("unchecked")
	private DSDColumn createColumnMetadata (ODocument columnO, OGraphDatabase database) {
    	String dimensionName = (String)((ODocument)columnO.field("dimension")).field("name");

    	DSDColumn column = new DSDColumn();
		column.setColumnId(dimensionName);
		column.setTitle((Map<String,String>)columnO.field("title",Map.class));
		column.setCodesLevel((Integer)columnO.field("codesLevel"));
		column.setDataType(DSDDataType.getByCode((String)columnO.field("datatype")));
		column.setGeoLyer((String)columnO.field("geoLayer"));
		column.setVirtualColumn((String)columnO.field("virtualColumn"));
		//Values only if single value
		Collection<Object> values = (Collection<Object>)columnO.field("values");
		if (values!=null && values.size()==1) {
			Object value = values.iterator().next();
			if (value instanceof ORID && DSDDataType.code==column.getDataType()) {
				value = (ODocument)database.load((ORID)value);
				value = new Code((String)((ODocument)value).field("system.system"),(String)((ODocument)value).field("system.version"),(String)((ODocument)value).field("code"));
			}
			column.addValue(value);
		}
		//Connected elements
		column.setDimension(new DSDDimension(dimensionName));
		ODocument codeSystemO = (ODocument)columnO.field("codeSystem");
		if (codeSystemO!=null)
			column.setCodeSystem(new CodeSystem((String)codeSystemO.field("system"),(String)codeSystemO.field("version")));
		//Return column
		return column;
    }

    @SuppressWarnings("unchecked")
	protected Collection<ODocument> getOutputColumns(SearchFilter filter, ODocument dataset) {
        Collection<ODocument> columns = new LinkedList<ODocument>();

        Map<String,ODocument> colByDim = getFlow().getColumnsByDimension(dataset);
        Set<String> dimensionsByFilter = filter.getDimensions().keySet();
        Map<String,OutputParameters> outputParameters = filter.getParameters()!=null ? (Map<String,OutputParameters>)filter.getParameters().get("output") : null;
        for (Map.Entry<String,ODocument> columnEntry : colByDim.entrySet())
            if (    outputParameters.containsKey(columnEntry.getKey()) ||
                    dimensionsByFilter.contains(columnEntry.getKey()) ||
                    columnEntry.getKey().equals(DSDDimension.VALUE_DIMENSION.getName()))
                columns.add(columnEntry.getValue());
        return columns;
    }
    
    

}
