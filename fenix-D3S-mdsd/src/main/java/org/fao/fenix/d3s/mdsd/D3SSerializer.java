package org.fao.fenix.d3s.mdsd;

import com.google.gson.*;
import org.fao.fenix.commons.annotations.Description;
import org.fao.fenix.commons.annotations.Label;
import org.fao.fenix.commons.annotations.Order;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class D3SSerializer implements JsonSerializer<MeIdentification> {

    public JsonElement serialize(MeIdentification src, Type typeOfSrc, JsonSerializationContext context) {

        /* Initiate root object. */
        JsonObject jo = new JsonObject();
        jo.addProperty("$schema", "http://json-schema.org/draft-04/schema#");
        jo.addProperty("type", "object");
        jo.addProperty("title", "MDSD");
        jo.addProperty("description", "D3S 2nd Level Metadata");

        /* Load custom definitions. */
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
        jo.add("definitions", parser.parse(descriptions.toString()).getAsJsonObject());

        /* Load properties. */
        jo.add("properties", new JsonObject());
        for (int i = 0; i < src.getClass().getDeclaredFields().length; i++) {
            try {
                jo.getAsJsonObject("properties").add(src.getClass().getDeclaredFields()[i].getName(), serializeField(src.getClass().getDeclaredFields()[i]));
            } catch (NullPointerException | ClassNotFoundException | InstantiationException | IllegalAccessException ignored) {
            }
        }

        /* Return result. */
        return jo;

    }

    private JsonObject serializeField(Field f) throws NullPointerException, ClassNotFoundException, InstantiationException, IllegalAccessException {

        /* Initiate the object. */
        JsonObject out = new JsonObject();

        /* Read annotations. */
        Label l = f.getAnnotation(Label.class);
        Description d = f.getAnnotation(Description.class);
        Order o = f.getAnnotation(Order.class);

        /* Encode annotations value in the JSON. */
        out.addProperty("title", l.en());
        out.add("title_i18n", new JsonObject());
        out.getAsJsonObject("title_i18n").addProperty("en", l.en());
        out.getAsJsonObject("title_i18n").addProperty("fr", l.fr());
        out.getAsJsonObject("title_i18n").addProperty("es", l.es());
        out.addProperty("description", d.en());
        out.add("description_i18n", new JsonObject());
        out.getAsJsonObject("description_i18n").addProperty("en", d.en());
        out.getAsJsonObject("description_i18n").addProperty("fr", d.fr());
        out.getAsJsonObject("description_i18n").addProperty("es", d.es());
        out.addProperty("type", "object");
        out.addProperty("propertyOrder", o.value());

        /* Encode fields. */
        Object obj;
        try {

            obj = Class.forName(f.getType().getCanonicalName()).newInstance();

            /* Test whether it's a D3S object or not. */
            if (obj.getClass().getSimpleName().startsWith("Oj")) {
                out.addProperty("$ref", "#/definitions/" + f.getType().getSimpleName());
            }

            else if (obj instanceof String) {
                out.addProperty("type", "string");
            }

            else if (obj.getClass().getSimpleName().startsWith("Date")) {
                out.addProperty("type", "string");
                out.addProperty("format", "date");
            }

            /* Recursive encoding. */
            else {
                out.add("properties", new JsonObject());
                for (int i = 0; i < obj.getClass().getDeclaredFields().length; i++) {
                    try {
                        String fieldName = obj.getClass().getDeclaredFields()[i].getName();
                        JsonElement subElement = serializeField(obj.getClass().getDeclaredFields()[i]);
                        out.getAsJsonObject("properties").add(fieldName, subElement);
                    } catch (NullPointerException | ClassNotFoundException | IllegalAccessException | InstantiationException ignored) {
                    }
                }
            }

        } catch (InstantiationException e) {

            if (f.getType().getCanonicalName().endsWith("Map")) {
                out.addProperty("title", l.en());
                out.add("patternProperties", new JsonObject());
                out.getAsJsonObject("patternProperties").add(".{1,}", new JsonObject());
                out.getAsJsonObject("patternProperties").getAsJsonObject(".{1,}").addProperty("type", "string");
            }

            else if (f.getType().getCanonicalName().endsWith("RepresentationType")) {
                out.addProperty("type", "string");
                out.addProperty("$ref", "#/definitions/RepresentationType");
            }

            else if (f.getType().getCanonicalName().endsWith("CodeListType")) {
                out.addProperty("type", "string");
                out.addProperty("$ref", "#/definitions/CodeListType");
            }

            else if (f.getType().getCanonicalName().endsWith("Collection")) {
                out.addProperty("type", "array");
                ParameterizedType fieldType = (ParameterizedType) f.getGenericType();
                String generics = ((Class<?>) fieldType.getActualTypeArguments()[0]).getSimpleName();
                out.add("items", new JsonObject());
                if (generics.equalsIgnoreCase(String.class.getSimpleName())) {
                    out.getAsJsonObject("items").addProperty("type", "string");
                } else {
                    out.getAsJsonObject("items").addProperty("$ref", "#/definitions/" + generics);
                }
            }

        }

        return out;

    }

}