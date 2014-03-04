package org.fao.fenix.d3s.server.utils;

public class TimeNumberPeriod implements Comparable<TimeNumberPeriod> {

    private Long from, to;

    public TimeNumberPeriod(Long from, Long to) {
        setFrom(from);
        setTo(to);
    }

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from!=null ? from : Long.MIN_VALUE;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to!=null ? to : Long.MAX_VALUE;
    }

    //Compare


    @Override
    public int hashCode() {
        return (from!=null?from.hashCode():0) + (to!=null?to.hashCode():0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj!=null && obj instanceof TimeNumberPeriod && compareTo((TimeNumberPeriod)obj)==0;    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(TimeNumberPeriod o) {
        return compareToIncluded(o);
    }

    private int compareToInclude(TimeNumberPeriod o) {
        if (from>o.from)
            return 1;
        if (to<o.to)
            return -1;
        return 0;
    }
    private int compareToIncluded(TimeNumberPeriod o) {
        if (o.from>from)
            return 1;
        if (o.to<to)
            return -1;
        return 0;
    }



}
