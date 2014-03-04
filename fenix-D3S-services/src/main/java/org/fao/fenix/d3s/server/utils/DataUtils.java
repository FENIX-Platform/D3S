package org.fao.fenix.d3s.server.utils;

import org.fao.fenix.d3s.msd.dto.dsd.type.DSDDataType;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

public class DataUtils {

    public static Date roundDate(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.HOUR,0);
            c.set(Calendar.MINUTE,0);
            c.set(Calendar.SECOND,0);
            c.set(Calendar.MILLISECOND,0);
            return c.getTime();
        } else {
            return null;
        }
    }

    public static Collection<Long> dateToNumber (Collection<?> values, DSDDataType dateType) throws Exception {
        if(values!=null) {
            Collection<Long> dates = new LinkedList<Long>();
            long number;
            for (Object value : values)
                if ((number=dateToNumber(value,dateType))==-1)
                    throw new Exception("Cannot convert date "+value+" to type "+dateType);
                else
                    dates.add(number);
            return dates;
        }
        return null;
    }
    private static long dateToNumber(Object value, DSDDataType dateType) {
        if (value!=null && (dateType==DSDDataType.year || dateType==DSDDataType.month || dateType==DSDDataType.date)) {
            if (value instanceof Date) {
                Calendar c = Calendar.getInstance();
                c.setTime((Date)value);
                switch (dateType) {
                    case year: return c.get(Calendar.YEAR);
                    case month: return c.get(Calendar.YEAR)*100+c.get(Calendar.MONTH);
                    case date: return c.get(Calendar.YEAR)*10000+c.get(Calendar.MONTH)*100+c.get(Calendar.DATE);
                }
            }
            if (value instanceof Number) {
                long number = ((Number) value).longValue();
                int digits = Long.toString(number).length();

                if (digits>4)
                    if (digits>6)
                        if (digits>8) { //timestamp
                            Calendar c = Calendar.getInstance();
                            c.setTime(new Date(number));
                            switch (dateType) {
                                case year: return c.get(Calendar.YEAR);
                                case month: return c.get(Calendar.YEAR)*100+c.get(Calendar.MONTH);
                                case date: return c.get(Calendar.YEAR)*10000+c.get(Calendar.MONTH)*100+c.get(Calendar.DATE);
                            }
                        } else //day
                            switch (dateType) {
                                case year: return number/10000;
                                case month: return number/100;
                                case date: return number;
                            }
                    else //month
                        switch (dateType) {
                            case year: return number/100;
                            case month: return number;
                            case date: return number*100+1;
                        }
                else //year
                    switch (dateType) {
                        case year: return number;
                        case month: return number*100;
                        case date: return number*1000+1;
                    }
            }
        }
        return -1;
    }

    public static Collection<Long> getTimeSeries (Date from, Date to, DSDDataType dataType) throws Exception {
        if (from!=null && to!=null) {
            Calendar cf = Calendar.getInstance();
            cf.setTime(roundDate(from));
            Calendar ct = Calendar.getInstance();
            ct.setTime(roundDate(to));
            Collection<Long> series = new LinkedList<Long>();
            switch (dataType) {
                case year:
                    for (long f = getYear(from,true),t = getYear(to,false); f<=t; series.add(f++));
                    break;
                case month:
                    for (long f = getMonth(from,true), t = getMonth(to,false); f<=t; f=(f%100==11?(f/100+1)*100:f+1))
                        series.add(f);
                    break;
                case date:
                    for (;cf.compareTo(ct)<=0;cf.add(Calendar.DATE,1))
                        series.add((long)getDay(cf));
                    break;
                default:
                    throw new Exception("Unsupported data type "+dataType+" as destination for date conversion");
            }
            return series;
        }
        return null;
    }


    public static int getYear (Date date, boolean from) {
        Calendar cF = Calendar.getInstance();
        cF.setTime(date);
        if (from)
            return cF.get(Calendar.YEAR) + (cF.get(Calendar.DATE)==1 && cF.get(Calendar.MONTH)==0 ? 0 : 1);
        else
            return cF.get(Calendar.YEAR) - (cF.get(Calendar.DATE)==31 && cF.get(Calendar.MONTH)==11 ? 0 : 1);
    }
    public static int getMonth (Date date, boolean from) {
        Calendar cF = Calendar.getInstance();
        cF.setTime(date);
        if (from && cF.get(Calendar.DATE)>1)
            cF.add(Calendar.MONTH,1);
        else if (!from && cF.get(Calendar.DATE)<cF.getMaximum(Calendar.DATE))
            cF.add(Calendar.MONTH,-1);
        return cF.get(Calendar.YEAR)*100+cF.get(Calendar.MONTH);
    }
    public static int getDay (Calendar c) {
        return c.get(Calendar.YEAR)*10000+c.get(Calendar.MONTH)*100+c.get(Calendar.DAY_OF_MONTH);
    }


    //TEST
    public static void main(String[] args) throws Exception {
        Calendar cf = Calendar.getInstance();
        cf.set(2010,10,13);
        Calendar ct = Calendar.getInstance();
        ct.set(2013,6,18);

        long year,month,date;
        System.out.println("Data a numero (anno): "+(year=dateToNumber(cf.getTime(),DSDDataType.year)));
        System.out.println("Data a numero (mese): "+(month=dateToNumber(cf.getTime(),DSDDataType.month)));
        System.out.println("Data a numero (giorno): "+(date=dateToNumber(cf.getTime(),DSDDataType.date)));
        System.out.println("millisec a numero (anno): "+dateToNumber(cf.getTime().getTime(),DSDDataType.year));
        System.out.println("millisec a numero (mese): "+dateToNumber(cf.getTime().getTime(),DSDDataType.month));
        System.out.println("millisec a numero (giorno): "+dateToNumber(cf.getTime().getTime(),DSDDataType.date));
        System.out.println("anno a numero (anno): "+dateToNumber(year,DSDDataType.year));
        System.out.println("anno a numero (mese): "+dateToNumber(year,DSDDataType.month));
        System.out.println("anno a numero (giorno): "+dateToNumber(year,DSDDataType.date));
        System.out.println("mese a numero (anno): "+dateToNumber(month,DSDDataType.year));
        System.out.println("mese a numero (mese): "+dateToNumber(month,DSDDataType.month));
        System.out.println("mese a numero (giorno): "+dateToNumber(month,DSDDataType.date));
        System.out.println("giorno a numero (anno): "+dateToNumber(date,DSDDataType.year));
        System.out.println("giorno a numero (mese): "+dateToNumber(date,DSDDataType.month));
        System.out.println("giorno a numero (giorno): "+dateToNumber(date,DSDDataType.date));

        Collection<Long> series = getTimeSeries(cf.getTime(),ct.getTime(), DSDDataType.year);
        if (series!=null) {
            System.out.println("\n\nSerie anni:");
            for (Long n : series)
                System.out.print(n+" - ");
        }
        series = getTimeSeries(cf.getTime(),ct.getTime(), DSDDataType.month);
        if (series!=null) {
            System.out.println("\n\nSerie mesi:");
            for (Long n : series)
                System.out.print(n+" - ");
        }
        series = getTimeSeries(cf.getTime(),ct.getTime(), DSDDataType.date);
        if (series!=null) {
            System.out.println("\n\nSerie giorni:");
            for (Long n : series)
                System.out.print(n+" - ");
        }
    }

}
