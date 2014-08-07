package test;

import java.util.UUID;

public class UUIDTest {

    public static void main (String ... args) {
        byte[] source = getText().getBytes();
        source = new byte[] {0,1,2,0,0,0,-1,-1};
        //Test same uid on the same text
        UUID id1 = UUID.nameUUIDFromBytes(source);
        UUID id2 = UUID.nameUUIDFromBytes(source);
        System.out.println("id1: "+id1);
        System.out.println("id2: "+id2);
        System.out.println(id2.equals(id1));

        //Test performance
        long time = System.currentTimeMillis();
        for (int i=0; i<1000000; i++)
            UUID.nameUUIDFromBytes(source);
        time = System.currentTimeMillis()-time;
        System.out.println("Esecuzione in: "+(time/1000000d)+" ms con "+source.length+" caratteri");

        //Test performance random
        time = System.currentTimeMillis();
        for (int i=0; i<1000000; i++)
            UUID.randomUUID();
        time = System.currentTimeMillis()-time;
        System.out.println("Esecuzione in: "+(time/1000000d)+" ms");
    }

    private static String getText()  {
        return  "public final class UUID\n" +
                "extends Object\n" +
                "implements Serializable, Comparable<UUID>\n" +
                "A class that represents an immutable universally unique identifier (UUID). A UUID represents a 128-bit value.\n" +
                "There exist different variants of these global identifiers. The methods of this class are for manipulating the Leach-Salz variant, although the constructors allow the creation of any variant of UUID (described below).\n" +
                "\n" +
                "The layout of a variant 2 (Leach-Salz) UUID is as follows: The most significant long consists of the following unsigned fields:\n" +
                "\n" +
                " 0xFFFFFFFF00000000 time_low\n" +
                " 0x00000000FFFF0000 time_mid\n" +
                " 0x000000000000F000 version\n" +
                " 0x0000000000000FFF time_hi\n" +
                " \n" +
                "The least significant long consists of the following unsigned fields:\n" +
                " 0xC000000000000000 variant\n" +
                " 0x3FFF000000000000 clock_seq\n" +
                " 0x0000FFFFFFFFFFFF node\n" +
                " \n" +
                "The variant field contains a value which identifies the layout of the UUID. The bit layout described above is valid only for a UUID with a variant value of 2, which indicates the Leach-Salz variant.\n" +
                "\n" +
                "The version field holds a value that describes the type of this UUID. There are four different basic types of UUIDs: time-based, DCE security, name-based, and randomly generated UUIDs. These types have a version value of 1, 2, 3 and 4, respectively.\n" +
                "\n" +
                "For more information including algorithms used to create UUIDs, see RFC 4122: A Universally Unique IDentifier (UUID) URN Namespace, section 4.2 \"Algorithms for Creating a Time-Based UUID\".";
    }
}
