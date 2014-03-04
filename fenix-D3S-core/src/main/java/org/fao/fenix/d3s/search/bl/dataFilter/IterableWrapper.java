package org.fao.fenix.d3s.search.bl.dataFilter;

import java.io.Serializable;
import java.util.Iterator;

public abstract class IterableWrapper<ST,DT> implements Serializable, Iterable<DT>{
	private static final long serialVersionUID = 1L;
	
	private Iterable<ST> data;
	public IterableWrapper (Iterable<ST> data) { this.data = data; }
	
	@Override
	public Iterator<DT> iterator() {
		return new Iterator<DT> (){
			private Iterator<ST> dataIterator = data.iterator();
			@Override public boolean hasNext() { return dataIterator.hasNext(); }
			@Override public DT next() { return apply(dataIterator.next()); }
			@Override public void remove() { dataIterator.remove(); }
		};
	}

	protected abstract DT apply(ST data);
}
