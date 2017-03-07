package org.fao.fenix.d3s.msd.find.engine;

import org.fao.fenix.commons.utils.Context;
import org.fao.fenix.commons.utils.Engine;
import org.fao.fenix.d3s.msd.find.engine.SearchEngine;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class SearchEngineFactory {
    @Inject @Any Instance<SearchEngine> searchEngineInstances;

    //Retrieve metadata listeners
    public Collection<SearchEngine> getEngines(String context, String engineName) {
        Collection<SearchEngine> list = new LinkedList<>();
        SearchEngine instance;
        for (Iterator<SearchEngine> i = searchEngineInstances.select().iterator(); i.hasNext(); )
            if (validateContext(context, engineName, instance = i.next()))
                list.add(instance);
        return list;
    }

    private boolean validateContext(String context, String engineName, SearchEngine searchEngine) {
        return  (searchEngine.getClass().getAnnotation(Engine.class).equals(engineName))? validateContext(context,searchEngine):false;
    }

    private boolean validateContext(String context,  SearchEngine searchEngine) {
        Context contextAnnotation = searchEngine.getClass().getAnnotation(Context.class);
        String[] pluginContexts = contextAnnotation != null ? contextAnnotation.value() : null;
        Set<String> pluginContextsSet = pluginContexts != null ? new HashSet<>(Arrays.asList(pluginContexts)) : null;
        return pluginContextsSet == null || pluginContextsSet.contains(context);
    }

}