package org.fao.fenix.d3s.server.tools.orient;

public enum OrientDatabase {
	msd("plocal:","msd_2.0", 400),cacheL1("plocal:","cacheLevel1_1.0", 4000),cacheL2("plocal:","cacheLevel2_1.0", 400);

    private String databaseName;
    private String protocol;
    private int maxConnections;
    OrientDatabase(String protocol, String databaseName, int maxConnections) {
        this.databaseName = databaseName;
        this.protocol = protocol;
        this.maxConnections = maxConnections;
    }

    public String getURL(String databaseFolderPath) {
        return protocol+databaseFolderPath+databaseName;
    }

    public int getMaxConnections() {
        return maxConnections;
    }
}
