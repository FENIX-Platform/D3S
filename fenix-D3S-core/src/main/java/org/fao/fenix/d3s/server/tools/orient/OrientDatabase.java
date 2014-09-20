package org.fao.fenix.d3s.server.tools.orient;

public enum OrientDatabase {
	msd("plocal:","msd_2.0"),cacheL1("plocal:","cacheLevel1_1.0"),cacheL2("plocal:","cacheLevel2_1.0");

    private String databaseName;
    private String protocol;
    OrientDatabase(String protocol, String databaseName) {
        this.databaseName = databaseName;
        this.protocol = protocol;
    }

    public String getURL(String databaseFolderPath) {
        return protocol+databaseFolderPath+databaseName;
    }

}
