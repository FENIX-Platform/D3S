package org.fao.fenix.d3s.mdsd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import junit.framework.TestCase;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class GsonTest extends TestCase {

    public void testD3SSerializer() {
        MeIdentification i = new MeIdentification();
        Type meIdentificationType = new TypeToken<MeIdentification>() {}.getType();
        Gson g = new GsonBuilder().registerTypeAdapter(MeIdentification.class, new D3SSerializer()).setPrettyPrinting().create();
        System.out.println(g.toJson(i, meIdentificationType));
    }

    public void _testLoadFromFile() {
        String path = "org/fao/fenix/config/descriptions.json";
        StringBuilder descriptions = new StringBuilder();
        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null)
                descriptions.append(line);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Gson g = new Gson();
        String s = g.toJson(descriptions);
        JsonParser parser = new JsonParser();
        JsonObject jo = parser.parse(descriptions.toString()).getAsJsonObject();

    }

}