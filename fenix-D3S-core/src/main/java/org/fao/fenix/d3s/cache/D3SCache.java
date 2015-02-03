package org.fao.fenix.d3s.cache;

public enum D3SCache {
    fixed("D3S|fixed");


    private final String alias;

    private D3SCache(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }
}
