package org.fao.fenix.d3s.msd.find.engine;

import org.fao.fenix.commons.utils.Context;
import org.fao.fenix.commons.utils.find.Engine;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class SearchEngineFactory {
    @Inject  Instance<SearchEngine> searchEngineInstances;
    private final String DEFAULT_ENGINE = "fenix";

    //Retrieve metadata listeners
    public Collection<SearchEngine> getEngines(Collection<String> contexts, String engineName) {
        List<String> engines = new LinkedList<>();

        // Add custom engine and default search engine if it does not contain it
        if(!engineName.equals( DEFAULT_ENGINE))
            engines.add(DEFAULT_ENGINE);
        engines.add(engineName);

        Collection<SearchEngine> list = new LinkedList<>();
        SearchEngine instance;
        for(String e : engines) {
            for (Iterator<SearchEngine> i = searchEngineInstances.select().iterator(); i.hasNext(); ) {
                instance = i.next();
                boolean isValidContext = (contexts == null || contexts.isEmpty() || e.equals("fenix")) ? validateContext(e, instance.getClass().getAnnotation(Engine.class).value()) : validateContext(contexts, e, instance);
                if (isValidContext)
                    list.add(instance);
            }
        }

        return list;
    }

    private boolean validateContext(String engineName,  String  annotationEngine) {
        return annotationEngine!= null &&  engineName.equals(annotationEngine);
    }

    private boolean validateContext(Collection<String> contexts, String engineName, SearchEngine searchEngine) {
        return  (searchEngine.getClass().getAnnotation(Engine.class).value().equals(engineName))? validateContext(contexts,searchEngine):false;
    }

    private boolean validateContext(Collection<String> contexts,  SearchEngine searchEngine) {
        Context contextAnnotation = searchEngine.getClass().getAnnotation(Context.class);
        String[] pluginContexts = contextAnnotation != null ? contextAnnotation.value() : null;
        Set<String> pluginContextsSet = pluginContexts != null ? new HashSet<>(Arrays.asList(pluginContexts)) : null;
        Set<String> filterContexts = contexts != null ? new HashSet<>(contexts) : null;
        return pluginContextsSet == null || pluginContextsSet.containsAll(filterContexts);
    }

}