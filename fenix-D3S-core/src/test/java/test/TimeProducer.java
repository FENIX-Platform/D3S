package test;


import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimeProducer {

    private static final String[] dates = new String[] {
            "20140615_000000",
            "20150321_153200",
    };

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_hhmmss");

    public static void main(String[] args) throws ParseException {
        for (String date : dates)
            System.out.println(date + ':'+dateFormat.parse(date).getTime());
    }

}
