package org.fao.fenix.d3s.cache.error;

public class IncompleteException extends Exception {
    private String resourceId;

    public IncompleteException() {
    }
    public IncompleteException(String id) {
        resourceId = id;
    }

    @Override
    public String getMessage() {
        return "Inconsistent cache status for the resource"+(resourceId!=null ? ": "+resourceId : "");
    }

    @Override
    public String getLocalizedMessage() {
        return getMessage();
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
