package org.fao.fenix.d3s.msd.find.engine;

import org.fao.fenix.commons.utils.annotations.find.Engine;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class SearchEngineFactory {
    @Inject  Instance<SearchEngine> searchEngineInstances;
    private final String DEFAULT_ENGINE_NAME = "fenix";

    //Retrieve metadata listeners
    public Collection<SearchEngine> getEngines(Collection<String> engineNames) throws Exception {
        //Assign default engine
        if (engineNames==null || engineNames.isEmpty())
            engineNames.add(DEFAULT_ENGINE_NAME);
        //Find engines
        Set<String> names = new HashSet<>(engineNames);
        Collection<SearchEngine> engines = new LinkedList<>();
        for (Iterator<SearchEngine> i = searchEngineInstances.select().iterator(); i.hasNext(); ) {
            SearchEngine searchEngine = i.next();
            if (names.contains(searchEngine.getClass().getAnnotation(Engine.class).value()))
                engines.add(searchEngine);
        }
        //Validate found engines
        if (engines.size()!=names.size())
            throw new Exception("Search engine not found");
        //Return found engines
        return engines;
    }

}