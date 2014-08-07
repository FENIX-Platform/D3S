package test;

import test.serialization.A;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class SerializationPerformanceTest {

    public static void main (String... args) throws IOException {
        A data = new A();
        test.serialization2.A data1 = new test.serialization2.A();
        toByteArray(data1);
        toByteArray(data1);
        toByteArray(data1);
        toByteArray(data1);
        toByteArray(data1);
        toByteArray(data1);
        toByteArray(data1);
        toByteArray(data1);
        toByteArray(data1);
        toByteArray(data1);

        long time = System.currentTimeMillis();
        toByteArray(data);
        time = System.currentTimeMillis()-time;
        System.out.println("Esecuzione in "+time+" ms");

        time = System.currentTimeMillis();
        for (int i=0; i<1000; i++)
            toByteArray(data);
        time = System.currentTimeMillis()-time;
        System.out.println("Esecuzione media in "+(time/1000d)+" ms");
        //System.out.println("Esecuzione in: "+(time/1000000d)+" ms con "+source.length+" caratteri");
    }

    public static byte[] toByteArray(Object obj) throws IOException {
        byte[] bytes = null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } finally {
            if (oos != null) {
                oos.close();
            }
            if (bos != null) {
                bos.close();
            }
        }
        return bytes;
    }

}
