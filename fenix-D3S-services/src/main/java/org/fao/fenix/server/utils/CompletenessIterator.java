package org.fao.fenix.server.utils;

import java.util.Iterator;

public interface  CompletenessIterator<T> extends Iterator<T> {
    int getIndex();
}
