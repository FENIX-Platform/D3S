package test;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class DateTest {

    static SimpleDateFormat format = new SimpleDateFormat();

    public static void main(String ... params) {
        Calendar d = Calendar.getInstance();
        System.out.println(format.format(d.getTime()));
        d.setTimeInMillis(d.getTimeInMillis()+(365l*24*60*60*1000));
        //d.add(Calendar.MONTH, 10);
        System.out.println(format.format(d.getTime()));
        //d.setTimeInMillis(d.getTimeInMillis()+);
    }
}
