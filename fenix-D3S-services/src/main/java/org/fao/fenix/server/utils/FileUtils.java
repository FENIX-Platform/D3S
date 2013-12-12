package org.fao.fenix.server.utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.Files;

public class FileUtils {
    public static final Charset UTF8 = Charset.forName("UTF-8");


    public static void copy (File source, File destination) throws IOException {
        Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        if (source.isDirectory())
            for (File sourceFile : source.listFiles())
                copy(sourceFile,new File (destination,sourceFile.getName()));
    }

    public static void delete(File toDelete) {
        if (toDelete.exists()) {
            if (toDelete.isDirectory())
                for (File f:toDelete.listFiles())
                    delete(f);
            toDelete.delete();
        }
    }

    public static String readTextFile(String file) throws IOException { return readTextFile(new File(file), UTF8); }
    public static String readTextFile(File file) throws IOException { return readTextFile(file, UTF8); }
    public static String readTextFile(String file, Charset charset) throws IOException { return readTextFile(new File(file), charset); }
    public static String readTextFile(File file, Charset charset) throws IOException {
        StringBuilder buffer = new StringBuilder((int)file.length());
        for (String line : Files.readAllLines(file.toPath(), Charset.forName("UTF-8")))
            buffer.append(line).append('\n');
        return buffer.toString();
    }

    public static void writeTextFile(File file, String text) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(file,false), 1024);
        out.write(text);
        out.flush();
        out.close();
    }


}
