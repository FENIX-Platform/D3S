package org.fao.fenix.d3s.backup.dao;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;

public interface RestoreBackupConsumer {

    public void storeUnit(OGraphDatabase database, OrientDatabase databaseId, String type, Object data) throws Exception;
}
