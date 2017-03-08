package org.fao.fenix.d3s.msd.find.engine;

import org.fao.fenix.commons.utils.Context;
import org.fao.fenix.commons.utils.Engine;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.*;

@ApplicationScoped
public class SearchEngineFactory {
    @Inject  Instance<SearchEngine> searchEngineInstances;

    //Retrieve metadata listeners
    public Collection<SearchEngine> getEngines(Collection<String> contexts, String engineName) {
        engineName = (engineName == null)? "fenix":engineName;
        Collection<SearchEngine> list = new LinkedList<>();
        SearchEngine instance;
        for (Iterator<SearchEngine> i = searchEngineInstances.select().iterator(); i.hasNext(); ) {
            instance = i.next();
            boolean isValidContext = (contexts== null || contexts.isEmpty()|| engineName.equals("fenix"))? validateContext(engineName, instance.getClass().getAnnotation(Engine.class).value()) :validateContext(contexts, engineName, instance ) ;
            if (isValidContext)
                list.add(instance);
        }
        return list;
    }

    private boolean validateContext(String engineName,  String  annotationEngine) {
        return annotationEngine!= null &&  engineName.equals(annotationEngine);
    }

    private boolean validateContext(Collection<String> contexts, String engineName, SearchEngine searchEngine) {
        return  (searchEngine.getClass().getAnnotation(Engine.class).equals(engineName))? validateContext(contexts,searchEngine):false;
    }

    private boolean validateContext(Collection<String> contexts,  SearchEngine searchEngine) {
        Context contextAnnotation = searchEngine.getClass().getAnnotation(Context.class);
        String[] pluginContexts = contextAnnotation != null ? contextAnnotation.value() : null;
        Set<String> pluginContextsSet = pluginContexts != null ? new HashSet<>(Arrays.asList(pluginContexts)) : null;
        Set<String> filterContexts = contexts != null ? new HashSet<>(contexts) : null;
        return pluginContextsSet == null || pluginContextsSet.contains(filterContexts);
    }

}