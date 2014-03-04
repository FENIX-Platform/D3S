package org.fao.fenix.d3s.server.utils;

import java.util.Iterator;

public interface  CompletenessIterator<T> extends Iterator<T> {
    int getIndex();
}
