package org.fao.fenix.d3s.mdsd;

import org.fao.fenix.commons.annotations.Description;
import org.fao.fenix.commons.annotations.Label;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class MDSDGenerator {

    public String generate() {

        /* Initiate output. */
        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\"type\": \"object\",\"properties\": {");

        MeIdentification obj = new MeIdentification();
        for (Field f : obj.getClass().getDeclaredFields()) {
//            System.out.println(f.getName() + " - " + f.getType().getSimpleName());
            try {
                Label l = (Label)f.getDeclaredAnnotation(Label.class);
                Description d = (Description)f.getDeclaredAnnotation(Description.class);
                System.out.println(f.getType().getSimpleName());
                if (f.getType().getSimpleName().equalsIgnoreCase("Collection")) {
                    ParameterizedType stringListType = (ParameterizedType) f.getGenericType();
                    Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                    System.out.println("\t" + stringListClass.getSimpleName());
                }
                sb.append(encodeField(f.getName(), f.getType().getSimpleName(), l, d));
                sb.append(",");
            } catch (Exception e) {

            }
        }
        sb.deleteCharAt(sb.length() - 1);

        /* Return output. */
        sb.append("}}");
        System.out.println(sb);
        return sb.toString();

    }

    private StringBuilder encodeField(String fieldName, String fieldType, Label l, Description d) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(fieldName).append("\": {");
        sb.append("\"type\": \"").append(fieldType).append("\",\"properties\": {");

        sb.append(encodeLabel(l));
        sb.append(",");
        sb.append(encodeDescription(d));


        sb.append("}");
        sb.append("}");
        return sb;
    }

    private StringBuilder encodeLabel(Label l) {
        StringBuilder sb = new StringBuilder();
        sb.append("\"label\": {");
        if (l.en().length() > 0)
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
        if (d.en().length() > 0)
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