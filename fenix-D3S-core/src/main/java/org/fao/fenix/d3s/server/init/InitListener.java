package org.fao.fenix.d3s.server.init;

import org.fao.fenix.commons.utils.Properties;

public interface InitListener {
    void init(Properties initParameters) throws Exception;
}
