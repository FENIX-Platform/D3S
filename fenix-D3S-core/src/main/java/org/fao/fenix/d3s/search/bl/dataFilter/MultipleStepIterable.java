package org.fao.fenix.d3s.search.bl.dataFilter;

import org.fao.fenix.d3s.search.SearchStep;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

@Component
@Scope("prototype")
public class MultipleStepIterable extends SearchStep {

    private Collection<SearchStep> sources = new LinkedList<SearchStep>();
    public void add (SearchStep source) { sources.add(source); }
    public void addAll (Collection<SearchStep> sources) { this.sources.addAll(sources); }

    @Override public boolean hasData() { return sources!=null && sources.size()>0; }

    @Override
    public Iterator<Object[]> iterator() {
        final Iterator<SearchStep> sourceIterator = sources.iterator();
        final SearchStep destinationStructure = this;
        return new Iterator<Object[]>() {
            private Iterator<Object[]> currentSurceDataIterator;
            private int[] currentColumnsMapping = new int[destinationStructure.columnsNumber];

            private void nextSource() {
                SearchStep source = sourceIterator.next();
                currentSurceDataIterator = source.iterator();
                for (int i=0; i<currentColumnsMapping.length; i++) {
                    Integer sourceIndex = source.columnsName.get(destinationStructure.structure[i].getColumnId());
                    currentColumnsMapping[i] = sourceIndex!=null ? sourceIndex : -1;
                }
            }

            @Override
            public boolean hasNext() {
                while ((currentSurceDataIterator==null || !currentSurceDataIterator.hasNext()) && sourceIterator.hasNext())
                    nextSource();
                return currentSurceDataIterator!=null && currentSurceDataIterator.hasNext();
            }

            @Override
            public Object[] next() {
                if (hasNext()) {
                    Object[] sourceRow = currentSurceDataIterator.next();
                    Object[] row = new Object[currentColumnsMapping.length];
                    for (int i=0; i<row.length; i++)
                        row[i] = currentColumnsMapping[i]>=0 ? sourceRow[currentColumnsMapping[i]] : null;
                    return row;
                }
                return null;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
