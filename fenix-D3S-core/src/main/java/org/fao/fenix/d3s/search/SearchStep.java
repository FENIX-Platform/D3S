package org.fao.fenix.d3s.search;

import java.sql.Types;
import java.util.*;

import com.orientechnologies.orient.core.record.impl.ODocument;
import org.fao.fenix.commons.msd.dto.templates.canc.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.templates.canc.dsd.DSDColumn;
import org.fao.fenix.commons.msd.dto.type.dsd.DSDDataType;
import org.fao.fenix.commons.search.dto.filter.ResourceFilter;
import org.fao.fenix.d3s.search.dto.OutputParameters;
import org.fao.fenix.commons.search.dto.filter.ColumnValueFilter;
import org.fao.fenix.d3s.server.tools.orient.OrientDao;

public abstract class SearchStep extends OrientDao implements Iterable<Object[]> {

	protected static Properties initProperties;
	public static void init(Properties properties) { initProperties = properties; }

	private static final ThreadLocal<SearchFlow> tl = new ThreadLocal<SearchFlow>();

	protected SearchFlow getFlow() {
		SearchFlow flow = tl.get();
		if (flow==null) tl.set(flow = new SearchFlow());
		return flow;
	}


    public String datasetName;
    public int columnsNumber;
    public DSDColumn[] structure;
    public Map<String,Integer> columnsName;
    public int[] sqlStructure;
    public Integer valueIndex;
    public Integer[] keyIndexes;
    public Integer[] outIndexes;

    protected Iterable<Object[]> data;
    public void setData(Iterable<Object[]> data) { this.data = data; }

    public void cloneResult(SearchStep source) {
        if (source!=null) {
            datasetName = source.datasetName;
            structure = source.structure;
            columnsName = source.columnsName;
            sqlStructure = source.sqlStructure;
            keyIndexes = source.keyIndexes;
            outIndexes = source.outIndexes;
            valueIndex = source.valueIndex;
            columnsNumber = source.columnsNumber;
            data = source;
        }
    }

    @Override
    public Iterator<Object[]> iterator() {
        return data!=null?data.iterator():null;
    }

    public boolean hasData() { return data!=null; }

    public void initStructure(ResourceFilter filter,ODocument dataset, boolean original, boolean forOutput) throws Exception {
        datasetName = null;
        columnsNumber = 0;
        structure = null;
        columnsName = null;
        sqlStructure = null;
        keyIndexes = null;
        outIndexes = null;
        valueIndex = null;

        if (dataset==null || filter==null)
            throw new Exception("Undefined dataset or filter during output structure creation.");

        if (original) {
            datasetName = dataset.field("uid");
            Collection<ODocument> columns = dataset.field("dsd.columns");
            if (columns!=null) {
                Map<String,OutputParameters> outputParameters = getFlow().getBusinessOutputParameters();
                Collection<Integer> keyIndexes = new LinkedList<Integer>();
                Collection<Integer> outIndexes = new LinkedList<Integer>();

                structure = new DSDColumn[columnsNumber=columns.size()];
                columnsName = new LinkedHashMap<String, Integer>();
                int i=0;
                for (ODocument columnO : columns) {
                    String dimensionName = (String)columnO.field("dimension.name");
                    if (dimensionName==null)
                        throw new Exception("Column "+datasetName+'.'+columnO.field("column")+" has no dimension!");

                    OutputParameters outputParameter = outputParameters!=null ? outputParameters.get(dimensionName) : null;
                    if ((outputParameter!=null && Boolean.TRUE.equals(outputParameter.getOut())) || (outputParameter==null && "VALUE".equals(dimensionName)))
                        outIndexes.add(i);
                    else if (forOutput)
                        continue;
                    if (outputParameter!=null && Boolean.TRUE.equals(outputParameter.getKey()))
                        keyIndexes.add(i);

                    structure[i] = new DSDColumn();
                    structure[i].setColumnId(dimensionName);
                    structure[i].setDataType(DSDDataType.getByCode((String)columnO.field("datatype")));
                    structure[i].setCodesLevel((Integer)columnO.field("codesLevel"));
                    ODocument clO = columnO.field("codeSystem");
                    if (clO!=null)
                        structure[i].setCodeSystem(new CodeSystem((String)clO.field("system"), (String)clO.field("version")));

                    columnsName.put(dimensionName,i++);
                }
                this.outIndexes = outIndexes.toArray(new Integer[outIndexes.size()]);
                this.keyIndexes = keyIndexes.toArray(new Integer[keyIndexes.size()]);
                valueIndex = columnsName.get("VALUE");
                sqlStructure = getSQLStructure(structure);
            }
        } else {
            Collection<DSDColumn> columns = new LinkedList<DSDColumn>();
            Map<String,Collection<ColumnValueFilter>> filterColumns = filter.getData();
            Map<String,OutputParameters> outputParameters = getFlow().getBusinessOutputParameters();

            DSDColumn column;
            int i=0;
            Set<String> allFilterColumns = new LinkedHashSet<String>(outputParameters.keySet());
            allFilterColumns.addAll(filterColumns.keySet());
            columnsName = new LinkedHashMap<String, Integer>();

            //Create dimensions
            Collection<Integer> keyIndexes = new LinkedList<Integer>();
            Collection<Integer> outIndexes = new LinkedList<Integer>();
            Map<String,ODocument> columnsO = getFlow().getColumnsByDimension(dataset);
            for (String filterColumnName : allFilterColumns) {
                Collection<ColumnValueFilter> filterColumn = filterColumns.get(filterColumnName);
                OutputParameters outputParameter = outputParameters.get(filterColumnName);
                ODocument columnO = columnsO!=null ? columnsO.get(filterColumnName) : null;

                if (outputParameter!=null && Boolean.TRUE.equals(outputParameter.getOut()))
                    outIndexes.add(i);
                else if (forOutput)
                    continue;
                if (outputParameter!=null && Boolean.TRUE.equals(outputParameter.getKey()))
                    keyIndexes.add(i);

                DSDDataType dataType = outputParameter!=null ? outputParameter.getDataType() : null;
                if (dataType==null)
                    dataType = columnO!=null ? DSDDataType.getByCode((String)columnsO.get(filterColumnName).field("datatype")) : null;
                if (dataType==null)
                    throw new Exception("Cannot find destination datatype from filter dimension "+filterColumnName);

                columns.add(column = new DSDColumn());
                column.setColumnId(filterColumnName);
                column.setCodesLevel(outputParameter != null ? outputParameter.getCodesLevel() : null);
                column.setDataType(dataType);
                if (dataType==DSDDataType.code) {
                    CodeSystem codeSystem = outputParameter.getCodeSystem();
                    if (codeSystem==null && columnO!=null) {
                        ODocument systemO = columnO.field("codeSystem");
                        codeSystem = systemO!=null ? new CodeSystem((String)systemO.field("system"),(String)systemO.field("version")) : null;
                    }
                    column.setCodeSystem(codeSystem);
                }

                columnsName.put(column.getColumnId(),i++);
            }

            //Append value column if absent
            if (!columnsName.containsKey("VALUE")) {
                columns.add(createValueColumn());
                columnsName.put("VALUE", i);
                outIndexes.add(i);
            }

            //Derive other informations
            datasetName = "D3SAggregation";
            valueIndex = columnsName.get("VALUE");
            structure = columns.toArray(new DSDColumn[columnsNumber=columns.size()]);
            sqlStructure = getSQLStructure(structure);
            this.outIndexes = outIndexes.toArray(new Integer[outIndexes.size()]);
            this.keyIndexes = keyIndexes.toArray(new Integer[keyIndexes.size()]);
        }
    }


    //Utils


    protected int[] getSQLStructure(DSDColumn[] columns) {
        int[] structure = new int[columns.length];
        int i=0;

        for (DSDColumn column : columns)
            switch (column.getDataType()) {
                case iText:
                    structure[i++] = Types.JAVA_OBJECT;
                    break;
                case year:
                case month:
                case date:
                    structure[i++] = Types.INTEGER;
                    break;
                case number:
                    structure[i++] = Types.DOUBLE;
                    break;
                case period:
                    throw new UnsupportedOperationException("period data format not supported into CountrySTAT data");
                default:
                    structure[i++] = Types.VARCHAR;
            }

        return structure;
    }


    private DSDColumn createValueColumn() {
        DSDColumn column = new DSDColumn();
        column.setColumnId("VALUE");
        column.setDataType(DSDDataType.number);
        return column;
    }


}
