package org.fao.fenix.d3s.search.bl.codec.bestMatch.operations;


public class RowStructureOperation implements DecodeOperation {
	//Init
	private int[] columnMapping;
	private int columnNumber;
	boolean toApply;
	
//	public RowStructureOperation(LinkedHashMap<String, ResponseColumnStructure> fromKeyMap, LinkedHashMap<String, ResponseColumnStructure> toKeyMap) {
//		Map<String, Integer> toIndexMap = new HashMap<String, Integer>();
//		Iterator<String> toKeyIterator = toKeyMap.keySet().iterator();
//		Iterator<String> fromKeyIterator = fromKeyMap.keySet().iterator();
//		for (int i=0; toKeyIterator.hasNext(); i++) toIndexMap.put(toKeyIterator.next(), i);
//		
//		columnNumber = toKeyMap.size();
//		columnMapping = new int[fromKeyMap.size()];
//		for (int i=0; fromKeyIterator.hasNext(); i++) toApply |= i != (columnMapping[i] = toIndexMap.get(fromKeyIterator.next()));
//		toApply |= fromKeyMap.size()!=toKeyMap.size();
//	}
	
	public int[] getColumnMapping() { return columnMapping; }

	//Operation
	@Override
	public Object[] apply(Object[] row) throws Exception {
		if (!toApply)
			return row;
		Object[] newRow = new Object[columnNumber];
		for (int i=0; i<columnMapping.length; i++)
			newRow[columnMapping[i]] = row[i];
		return newRow;
	}

	@Override public boolean isApplicable() { return toApply; }

}
