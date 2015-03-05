package org.fao.fenix.d3s.mdsd;

import org.fao.fenix.commons.annotations.Description;
import org.fao.fenix.commons.annotations.Label;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
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

        /* Return output. */
        System.out.println(sb);
        return sb.toString();

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
                l = (Label) f.getDeclaredAnnotation(Label.class);
                d = (Description) f.getDeclaredAnnotation(Description.class);
                boolean isMap = f.getType().getSimpleName().equalsIgnoreCase(Map.class.getSimpleName());
                boolean isCollection = f.getType().getSimpleName().equalsIgnoreCase(Collection.class.getSimpleName());
                sb.append(encodeField(f, l, d, isMap, isCollection));
                sb.append(",");
            } catch (Exception e) {

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
            sb.append("\"$ref\": \"#/definitions/").append(f.getType().getSimpleName()).append("\"");
            sb.append("}");
        } else {

            if (isMap) {
                sb.append("\"").append(f.getName()).append("\": {");
                sb.append("\"type\": \"object\",");
                sb.append("\"patternProperties\": {\".{1,}\": {\"type\": \"string\"}}");
            } else if (isCollection) {
                sb.append("\"").append(f.getName()).append("\": {");
                sb.append("\"type\": \"array\",");
                ParameterizedType fieldType = (ParameterizedType) f.getGenericType();
                String generics = ((Class<?>) fieldType.getActualTypeArguments()[0]).getSimpleName();
                if (generics.equalsIgnoreCase(String.class.getSimpleName())) {
                    sb.append("\"items\": {\"type\": \"string\"}");
                } else {
                    sb.append("\"items\": {\"$ref\": \"#/definitions/").append(generics).append("\"}");
                }
            } else {
//                if (f.getName().equalsIgnoreCase(f.getType().getSimpleName()))
//                    System.out.println(f.getName() + " - " + f.getType().getSimpleName() + " - " + f.getName().equalsIgnoreCase(f.getType().getSimpleName()));
                sb.append("\"").append(f.getName()).append("\": {");
                sb.append("\"type\": \"").append(f.getType().getSimpleName()).append("\"");
            }
            if (l.en().length() > 0 || d.en().length() > 0) {
                sb.append(",");
                sb.append("\"properties\": {");
                if (l.en().length() > 0)
                    sb.append(encodeLabel(l));
                if (d.en().length() > 0) {
                    sb.append(",");
                    sb.append(encodeDescription(d));
                }
                sb.append("}");
            }
            try {
                Object o = Class.forName(f.getType().getCanonicalName()).newInstance();
                if (!(o instanceof String)) {
                    sb.append(",");
                    sb.append("\"").append(f.getName()).append("\": ");
                    sb.append(processObject(o));
                }
            } catch (Exception e) {

            }
//            sb.append("}");
            sb.append("}");
        }
        return sb;
    }

    private StringBuilder encodeString() {
        StringBuilder sb = new StringBuilder();
        return sb;
    }

    private StringBuilder encodeLabel(Label l) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"label\": {");
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
        sb.append("\"description\": {");
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