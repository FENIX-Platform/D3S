package org.fao.fenix.backup.dao;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import org.fao.fenix.server.tools.orient.OrientDatabase;

public interface RestoreBackupConsumer {

    public void storeUnit(OGraphDatabase database, OrientDatabase databaseId, String type, Object data) throws Exception;
}
