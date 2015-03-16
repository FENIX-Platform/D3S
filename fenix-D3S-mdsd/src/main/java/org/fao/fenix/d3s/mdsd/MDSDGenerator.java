package org.fao.fenix.d3s.mdsd;

import org.fao.fenix.commons.annotations.Description;
import org.fao.fenix.commons.annotations.Label;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class MDSDGenerator {

    public String generate() {

        /* Initiate output. */
        StringBuilder sb = new StringBuilder();

        /* Root object. */
        MeIdentification obj = new MeIdentification();

        /* Process D3S domain. */
        sb.append(processObject(obj));

        /* Open FENIX custom objects definition file. */
        String path = "org/fao/fenix/config/descriptions.txt";
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

        /* Clean the output. */
        sb = clean(sb);

        /* Merge FENIX custom objects. */
        sb.insert(1 + sb.indexOf("{"), descriptions + ",");

        /* Return output. */
        return sb.toString();

    }

    private StringBuilder clean(StringBuilder sb) {
        Map<String, String> m = new HashMap<>();
        m.put("\\{,", "{");
        m.put("},}", "}}");
        m.put(",},", "},");
        m.put(",}", "}");
        m.put(",,", ",");
        m.put(",,,", "");
        String s = sb.toString();
        for (String key : m.keySet())
            s = s.replaceAll(key, m.get(key));
        return new StringBuilder(s);
    }

    private StringBuilder processObject(Object obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"type\": \"object\",");
        sb.append("\"properties\": {");
        Field f;
        Label l;
        Description d;
        for (int i = 0; i < obj.getClass().getDeclaredFields().length; i++) {
            f = obj.getClass().getDeclaredFields()[i];
            try {
                l = f.getAnnotation(Label.class);
                d = f.getAnnotation(Description.class);
                boolean isMap = f.getType().getSimpleName().equalsIgnoreCase(Map.class.getSimpleName());
                boolean isCollection = f.getType().getSimpleName().equalsIgnoreCase(Collection.class.getSimpleName());
                sb.append(encodeField(f, l, d, isMap, isCollection));
                sb.append(",");
            } catch (NullPointerException e) {

            }
        }
        if (sb.charAt(sb.length() - 1) == ',')
            sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        sb.append("}");
        return sb;
    }

    private StringBuilder encodeField(Field f, Label l, Description d, boolean isMap, boolean isCollection) {
        StringBuilder sb = new StringBuilder();
        boolean isOj = f.getType().getSimpleName().startsWith("Oj");
        if (isOj) {
            sb.append("\"").append(f.getName()).append("\": {");
            sb.append(encodeLabelAndDescription(l, d));
            sb.append(",");
            sb.append("\"$ref\": \"#/definitions/").append(f.getType().getSimpleName()).append("\"");
            sb.append("}");
        } else {
            if (f.getName().equalsIgnoreCase(f.getType().getSimpleName())) {
                try {
                    Object o = Class.forName(f.getType().getCanonicalName()).newInstance();
                    if (!(o instanceof String)) {
                        sb.append("\"").append(f.getName()).append("\": ");
                        sb.append(processObject(o));
                    }
                } catch (Exception e) {

                }
            } else {
                if (isMap) {
                    sb.append("\"").append(f.getName()).append("\": {");
                    sb.append("\"type\": \"object\",");
                    sb.append(encodeLabelAndDescription(l, d));
                    sb.append(",");
                    sb.append("\"patternProperties\": {\".{1,}\": {\"type\": \"string\"}}");
                } else if (isCollection) {
                    sb.append("\"").append(f.getName()).append("\": {");
                    sb.append("\"type\": \"array\",");
                    sb.append(encodeLabelAndDescription(l, d));
                    sb.append(",");
                    ParameterizedType fieldType = (ParameterizedType) f.getGenericType();
                    String generics = ((Class<?>) fieldType.getActualTypeArguments()[0]).getSimpleName();
                    if (generics.equalsIgnoreCase(String.class.getSimpleName())) {
                        sb.append("\"items\": {\"type\": \"string\"}");
                    } else {
                        sb.append("\"items\": {\"$ref\": \"#/definitions/").append(generics).append("\"}");
                    }
                } else if (f.getType().getSimpleName().equalsIgnoreCase(Date.class.getSimpleName())) {
                    sb.append("\"").append(f.getName()).append("\": {");
                    sb.append("\"type\": \"date-time\",");
                    sb.append(encodeLabelAndDescription(l, d));
                } else {
                    sb.append("\"").append(f.getName()).append("\": {");
                    sb.append("\"type\": \"").append(f.getType().getSimpleName().toLowerCase()).append("\",");
                    sb.append(encodeLabelAndDescription(l, d));
                }
                try {
                    Object o = Class.forName(f.getType().getCanonicalName()).newInstance();
                    if (!(o instanceof String) && !(o instanceof Date)) {
                        sb.append(",");
                        sb.append("\"").append(f.getName()).append("\": ");
                        sb.append(processObject(o));
                    }
                } catch (Exception e) {

                }
                sb.append("}");
            }
        }
        return sb;
    }

    private StringBuilder encodeLabelAndDescription(Label l, Description d) {
        StringBuilder sb = new StringBuilder();
        if (l.en().length() > 0 || d.en().length() > 0) {
            if (l.en().length() > 0)
                sb.append(encodeLabel(l));
            if (d.en().length() > 0) {
                sb.append(",");
                sb.append(encodeDescription(d));
            }
        }
        return sb;
    }

    private StringBuilder encodeLabel(Label l) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"title\": \"").append(l.en()).append("\",");
        sb.append("\"title_i18n\": {");
        sb.append("\"en\": \"").append(l.en()).append("\"");
        if (l.fr().length() > 0)
            sb.append(",\"fr\": \"").append(l.fr()).append("\"");
        if (l.es().length() > 0)
            sb.append(",\"es\": \"").append(l.es()).append("\"");
        if (l.ar().length() > 0)
            sb.append(",\"ar\": \"").append(l.ar()).append("\"");
        if (l.cn().length() > 0)
            sb.append(",\"cn\": \"").append(l.cn()).append("\"");
        if (l.ru().length() > 0)
            sb.append(",\"ru\": \"").append(l.ru()).append("\"");
        sb.append("}");
        return sb;
    }

    private StringBuilder encodeDescription(Description d) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"description\": \"").append(d.en()).append("\",");
        sb.append("\"description_i18n\": {");
        sb.append("\"en\": \"").append(d.en()).append("\"");
        if (d.fr().length() > 0)
            sb.append(",\"fr\": \"").append(d.fr()).append("\"");
        if (d.es().length() > 0)
            sb.append(",\"es\": \"").append(d.es()).append("\"");
        if (d.ar().length() > 0)
            sb.append(",\"ar\": \"").append(d.ar()).append("\"");
        if (d.cn().length() > 0)
            sb.append(",\"cn\": \"").append(d.cn()).append("\"");
        if (d.ru().length() > 0)
            sb.append(",\"ru\": \"").append(d.ru()).append("\"");
        sb.append("}");
        return sb;
    }

}