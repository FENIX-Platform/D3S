package org.fao.fenix.search.bl.codec.bestMatch;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fao.fenix.msd.dao.cl.CodeListConverter;
import org.fao.fenix.msd.dao.cl.CodeListLinkLoad;
import org.fao.fenix.msd.dao.cl.CodeListLoad;
import org.fao.fenix.search.SearchStep;
import org.fao.fenix.search.bl.codec.Codec;
import org.fao.fenix.search.bl.codec.CodecCodeListUtils;
import org.fao.fenix.search.dto.OutputParameters;
import org.fao.fenix.search.dto.SearchDataResponse;
import org.fao.fenix.search.dto.SearchFilter;
import org.fao.fenix.search.dto.valueFilters.ColumnValueFilter;
import org.fao.fenix.search.dto.valueFilters.ValueFilterType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
@Scope("prototype")
public class BestMatchCodec extends Codec {

	private @Autowired CodeListLoad clDao;
	private @Autowired CodeListLinkLoad clLinkDao;
	private @Autowired CodeListConverter clConverter;
	private @Autowired CodecCodeListUtils clUtils;
	
	//To specify value columns into a filter, it has to be included an 'ELEMENT' dimension. In this case, all the elements are contained into the same code list.
	@Override
	@SuppressWarnings("unchecked")
	public boolean encodeFilter(SearchFilter filter, ODocument dataset) throws Exception {
        //TODO
        //IDENTIFY FILTER ELEMENTS TO CONVERT
        //IF FILTER IS TO CONVERT REPLACE FILTER VARIABLE WITH A CLONE AND APPLY CONVERTION


        //return filter converted or not
        return false;

		//find fields containing codes and needs to be decoded
/*		if (filterCopy.getFields()!=null)
			for (Map.Entry<String, Collection<ColumnValueFilter>> entry : filterCopy.getFields().entrySet())
				if (entry.getValue()!=null) {
					Object value = dataset.field(entry.getKey());
					if (value!=null && value instanceof ODocument) {
						String system = ((ODocument)value).field("system.system");
						String version = ((ODocument)value).field("system.version");
						if (system!=null && version!=null)
							for (ColumnValueFilter valueFilter : entry.getValue())
								if (valueFilter.getType()==ValueFilterType.code && (!system.equals(valueFilter.getCode().getSystemKey()) || !version.equals(valueFilter.getCode().getSystemVersion())) ) {
									//TODO encodare
								}
					}
				}

		// find dimensions containing codes and needs to be decoded
		Map<String,ODocument> columnsByDimension = getFlow().getColumnsByDimension(dataset);
		if (filterCopy.getDimensions() != null)
			for (Map.Entry<String, Collection<ColumnValueFilter>> entry : filterCopy.getDimensions().entrySet())
				if (entry.getValue() != null) {
					ODocument column = columnsByDimension.get(entry.getKey());
					if (column!=null)
						for (ColumnValueFilter valueFilter : entry.getValue())
							if (valueFilter.getType() == ValueFilterType.code) {
								String filterSystem = valueFilter.getCode().getSystemKey();
								String filterVersion = valueFilter.getCode().getSystemVersion();
								Collection<?> values = column.field("codeSystem")==null ? (Collection<?>)column.field("values") : Arrays.asList(((Collection<ODocument>)column.field("codeSystem.rootCodes")).iterator().next());
								for (Object value : values)
									if (value instanceof ODocument) {
										String system = ((ODocument) value).field("system.system");
										String version = ((ODocument) value).field("system.version");
										if (system != null && version != null && (!system.equals(filterSystem) || !version.equals(filterVersion)) ) {
											//TODO encodare
										}
									}
							}
				}
        return true;
*/		//TODO
//		Map<String, ODocument> datasetDimClMap = groupDatasetDimCl(dataset);
//		
//		//Removing filter dimensions not included into dataset (throws exception if it's a key dimension)
//		Set<String> datasetDimensions = new HashSet<String>();
//		for (ODocument columnO : (Collection<ODocument>)dataset.field("dsd.columns"))
//			datasetDimensions.add((String)((ODocument)columnO.field("dimension")).field("name"));
//		for (Iterator<Map.Entry<String, ColumnFilter>> entryIterator = filterCopy.getStructure().entrySet().iterator(); entryIterator.hasNext(); ) {
//			Map.Entry<String, ColumnFilter> filterEntry = entryIterator.next();
//			if (!datasetDimensions.contains(filterEntry.getKey()))
//				if (filterEntry.getValue().isKey())
//					throw new Exception("Dataset doesn't have key filter dimension '"+filterEntry.getKey()+"'");
//				else
//					entryIterator.remove();
//		}
//		
//		//Convert cl dimensions where needed
//		for (Map.Entry<String, ColumnFilter> columnStructure : filterCopy.getStructure().entrySet()) {
//			ODocument datasetCodeSystemO = datasetDimClMap.get(columnStructure.getKey());
//			CodeSystem filterCodeSystem = columnStructure.getValue().getCodeSystem();
//			ODocument filterCodeSystemO = filterCodeSystem!=null ? clDao.loadSystemO(filterCodeSystem.getSystem(), filterCodeSystem.getVersion(), database) : null;
//			if (	datasetCodeSystemO!=null && filterCodeSystemO!=null && //if dimension has different code lists
//					!(datasetCodeSystemO.field("system").equals(filterCodeSystemO.field("system")) && datasetCodeSystemO.field("version").equals(filterCodeSystemO.field("version")))
//				) {
//				Collection<ColumnValueFilter> newValues = new ArrayList<ColumnValueFilter>();
//				Map<String,Collection<ODocument>>[] clRelations = clUtils.getRelationsBetween(filterCodeSystemO, datasetCodeSystemO, database);
//				for (ColumnValueFilter value : columnStructure.getValue().getFilter()) {
//					if (value.getType()==ValueFilterType.code) {
//						String codeKey = clUtils.toString(value.getCode());
//						Collection<ODocument> relations = clRelations[0].containsKey(codeKey) ? clRelations[0].get(codeKey) : clRelations[1].get(codeKey);
//						if (relations==null)
//							if (clRelations[2].containsKey(codeKey)) {
//								//TODO many to one only relation error management
//							} else {
//								//TODO no relation error management
//							}
//						for (ODocument relation : relations)
//							newValues.add(new ColumnValueFilter(clConverter.toCode((ODocument)relation.field("in"), database, false, 0)));
//					}
//				}
//				columnStructure.getValue().setFilter(newValues);
//			}
//		}
//		return filterCopy;
	}
	
	

	@SuppressWarnings("unchecked")
	@Override
	public void decodeData(SearchFilter filter, ODocument dataset, SearchStep data) throws Exception  {
        cloneResult(data);
    }


//		SearchResponse dataCopy = new SearchResponse();
//		dataCopy.setCount(data.getCount());
//		//Create response datastructure and translate source datastructure values element code
//		dataCopy.setDm(createResponseDataStructure((String)dataset.field("uid"), data.getStructure(), filter));
//		
//		LinkedHashMap<String, ResponseColumnStructure> fromKeyMap = createResponseStructureMap(data.getStructure());
//		LinkedHashMap<String, ResponseColumnStructure> toKeyMap = createResponseStructureMap(dataCopy.getStructure());
//		
//		RowStructureOperation structureOperation = new RowStructureOperation(fromKeyMap, toKeyMap);
//		Collection<CodeTranslationOperation> translationOperations = new LinkedList<CodeTranslationOperation>();
//		for (String dimensionName : fromKeyMap.keySet()) {
//			CodeTranslationOperation translationOperation = toKeyMap.containsKey(dimensionName) ? new CodeTranslationOperation(fromKeyMap, toKeyMap, dimensionName, clUtils, clConverter, clDao, database) : null;
//			if (translationOperation!=null && translationOperation.isApplicable())
//				translationOperations.add(translationOperation);
//		}
//		
//		for (Object[] row : data.getData()) {
//			row = structureOperation.apply(row);
//			for (CodeTranslationOperation translationOperation : translationOperations)
//				row = translationOperation.apply(row);
//			
//			dataCopy.addRow(row);
//		}
//		
//		return dataCopy;

/*
	//DECODE DATA UTILS
	private ResponseColumnStructure[] createResponseDataStructure(String datasetUID, ResponseColumnStructure[] sourceDataStructure, SearchFilter filter) throws Exception {
		Collection<ResponseColumnStructure> dataStructure = new ArrayList<ResponseColumnStructure>();
		ColumnFilter elementFilterDimension = null;
		LinkedHashMap<String, ResponseColumnStructure> fromKeyMap = createResponseStructureMap(sourceDataStructure);
		
		//Include filter dimensions
		for (Map.Entry<String, ColumnFilter> filterDimensionEntry : filter.getStructure().entrySet()) {
			String dimensionName = filterDimensionEntry.getKey();
			ColumnFilter filterColumnStructure = filterDimensionEntry.getValue();
			Collection<ColumnValueFilter> valueFilter = filterColumnStructure.getFilter();
			
			if (dimensionName.equals(DSDDimension.ELEMENT_DIMENSION))
				elementFilterDimension = filterColumnStructure;
			else {
				DSDDataType dataType = null;
				if (fromKeyMap.containsKey(dimensionName))
					dataType = fromKeyMap.get(dimensionName).getDataType();
				else if (valueFilter!=null && valueFilter.size()>0)
					dataType = valueFilter.iterator().next().getType().getScope()[0];
				
				dataStructure.add(new ResponseColumnStructure(filterColumnStructure, dataType, datasetUID));
			}
		}

		//Include value dimensions
		Set<Code> elements = new HashSet<Code>();
		CodeSystem elementCodeSystem = null;
		if (elementFilterDimension!=null) {
			for (ColumnValueFilter element : elementFilterDimension.getFilter())
				elements.add(element.getCode());
			elementCodeSystem = elementFilterDimension.getCodeSystem();
		}
		
		for (ResponseColumnStructure sourceDimensionStructure : sourceDataStructure) {
			if (DSDDimension.VALUE_DIMENSION.getName().equals(sourceDimensionStructure.getDimension()))
				if (elementFilterDimension!=null) {
					Code sourceValueElement = sourceDimensionStructure.getValueElement();
					if (!elementCodeSystem.equals(sourceValueElement.getSystem())) {
						Collection<CodeRelationship> relations = clLinkDao.loadRelationshipsFromCodeToCL(sourceValueElement, elementCodeSystem);
						Code valueElement = null;
						for (CodeRelationship relation : relations)
							if (relation.getType()==CodeRelationshipType.oneToOne) {
								valueElement = relation.getToCode();
								break;
							}
						if (valueElement!=null)
							sourceDimensionStructure.setValueElement(valueElement);
						else
							throw new Exception("Value element '"+sourceValueElement+"' has no relation with the elements code list '"+elementCodeSystem+"'");
					}
					if (elements.contains(sourceValueElement))
						dataStructure.add(sourceDimensionStructure);
				} else {
					dataStructure.add(sourceDimensionStructure);
				}
		}
		
		return dataStructure.toArray(new ResponseColumnStructure[dataStructure.size()]);
		//TODO E' da ragionare anche se gli element devono essere disposti per colonna o per riga!!! Per ora li tengo per colonna...
	}
	
	private LinkedHashMap<String, ResponseColumnStructure> createResponseStructureMap (ResponseColumnStructure[] dataStructure) {
		LinkedHashMap<String, ResponseColumnStructure> dataStructureMap = new LinkedHashMap<String, ResponseColumnStructure>();
		for (ResponseColumnStructure structure : dataStructure)
			dataStructureMap.put(structure.getDimensionExtended(), structure);
		return dataStructureMap;
	}
	
	
	//ENCODE FILTER	UTILS
	
	@SuppressWarnings("unchecked")
	private Map<String, ODocument> groupDatasetDimCl (ODocument dataset) {
		Map<String, ODocument> datasetDimClMap = new HashMap<String, ODocument>();
		for (ODocument columnO : (Collection<ODocument>)dataset.field("dsd.columns")) {
			ODocument codeSystemO = (ODocument)columnO.field("codeSystem");
			if (codeSystemO!=null)
				datasetDimClMap.put((String)((ODocument)columnO.field("dimension")).field("name"), codeSystemO);
		}
		return datasetDimClMap;
	}
	
	*/
}
