package org.fao.fenix.d3s.msd.find.business;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public interface Business {

    public Collection<String> getOrderedUid(Map<String, Collection<String>> id) throws Exception;
}
