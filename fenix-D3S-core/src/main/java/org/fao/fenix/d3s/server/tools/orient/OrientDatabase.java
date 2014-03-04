package org.fao.fenix.d3s.server.tools.orient;

public enum OrientDatabase {
	msd("msd_1.0"),cacheL1("cacheLevel1_1.0"),cacheL2("cacheLevel2_1.0"),test("test_1.0");

    private String databaseName;
    OrientDatabase(String databaseName) { this.databaseName = databaseName; }
    public String getDatabaseName() { return databaseName; }
}
