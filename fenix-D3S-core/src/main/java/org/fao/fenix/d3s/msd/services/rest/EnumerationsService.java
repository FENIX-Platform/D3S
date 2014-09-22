package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.commons.msd.dto.type.*;
import org.fao.fenix.d3s.msd.dao.ResourceDao;
import org.fao.fenix.d3s.msd.dao.SubjectDao;
import org.fao.fenix.d3s.msd.services.spi.Enumerations;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Path;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Path("msd/choices")
public class EnumerationsService implements Enumerations {
    @Inject private Instance<ResourceDao> daoFactory;
    @Inject private SubjectDao dao;


    @Override
    public Map<String, Map<String, String>> getEnumeration(String enumName) throws Exception {
        Class enumClass = enumName!=null ? availableEnumerations.get(enumName) : null;
        if (enumClass!=null) {
            Map<String, Map<String, String>> enumValuesMap = new LinkedHashMap<>();
            Method nameMethod = enumClass.getMethod("name");
            Method labelMethod = enumClass.getMethod("getLabel");

            for (Object enumValue : enumClass.getEnumConstants())
                enumValuesMap.put((String)nameMethod.invoke(enumValue), (Map<String, String>)labelMethod.invoke(enumValue));

            return enumValuesMap;
        } else
            return null;
    }

    @Override
    public Object getAvailableEnumerations(boolean full) throws Exception {
        if (full) {
            Map<String, Map<String, Map<String, String>>> enums = new LinkedHashMap<>();
            for (String enumName : availableEnumerations.keySet())
                enums.put(enumName, getEnumeration(enumName));
            return enums;
        } else
            return availableEnumerations.keySet();
    }


    private static LinkedHashMap<String, Class> availableEnumerations = new LinkedHashMap<>();
    static {
        availableEnumerations.put("AxisType",AxisType.class);
        availableEnumerations.put("CellGeometry", CellGeometry.class);
        availableEnumerations.put("CellOfOrigin", CellOfOrigin.class);
        availableEnumerations.put("CodeListType", CodeListType.class);
        availableEnumerations.put("ConfidentialityStatus", ConfidentialityStatus.class);
        availableEnumerations.put("DimensionType", DimensionType.class);
        availableEnumerations.put("DocumentType", DocumentType.class);
        availableEnumerations.put("GeometricObjects", GeometricObjects.class);
        availableEnumerations.put("LayerType", LayerType.class);
        availableEnumerations.put("ReferenceEntity", ReferenceEntity.class);
        availableEnumerations.put("RepresentationType", RepresentationType.class);
        availableEnumerations.put("ResponsiblePartyRole", ResponsiblePartyRole.class);
        availableEnumerations.put("XYPosition", XYPosition.class);
    }

}
