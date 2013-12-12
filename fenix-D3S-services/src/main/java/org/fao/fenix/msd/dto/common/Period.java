package org.fao.fenix.msd.dto.common;

import org.fao.fenix.server.utils.DataUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Period {

    private Date from;
    private Date to;

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = DataUtils.roundDate(from);
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = DataUtils.roundDate(to);
    }

    public Map<String,Object> toMap() {
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("from",from);
        map.put("to",to);
        return map;
    }
    public static Period fromMap(Map<String,Object> map) {
        Period period = new Period();
        period.from = (Date)map.get("from");
        period.to = (Date)map.get("to");
        return period;
    }



    private static final long MILLISECONDS_PER_DAY = 24*60*60*1000;
    private Date roundDate(Date date) {
        return date!=null ? new Date((date.getTime()/MILLISECONDS_PER_DAY)*MILLISECONDS_PER_DAY) : null;
    }

}
