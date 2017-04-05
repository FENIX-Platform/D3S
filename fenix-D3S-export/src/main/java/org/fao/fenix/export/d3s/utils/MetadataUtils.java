package org.fao.fenix.export.d3s.utils;

import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.full.MeContent;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;



public class MetadataUtils {

    public MeIdentification<DSDDataset> getProcessMetadata (Object metadataProxy) throws Exception {

        MeIdentification<DSDDataset> metadata = new MeIdentification<>();
        try { metadata.setUid((String)metadataProxy.getClass().getMethod("getUid").invoke(metadataProxy)); } catch (Exception ex) {}
        try { metadata.setVersion((String)metadataProxy.getClass().getMethod("getVersion").invoke(metadataProxy)); } catch (Exception ex) {}
        try { metadata.setTitle((Map<String,String>)metadataProxy.getClass().getMethod("getTitle").invoke(metadataProxy)); } catch (Exception ex) {}
        MeContent meContent = new MeContent();
        metadata.setMeContent(meContent);
        meContent.setResourceRepresentationType(RepresentationType.dataset);

        DSDDataset newDsd = new DSDDataset();
        metadata.setDsd(newDsd);
        try {
            Object dsd = metadataProxy.getClass().getMethod("getDsd").invoke(metadataProxy);
            try { newDsd.setContextSystem((String)dsd.getClass().getMethod("getContextSystem").invoke(dsd)); } catch (Exception ex) {}
            try { newDsd.setUserName((String)dsd.getClass().getMethod("getUserName").invoke(dsd)); } catch (Exception ex) {}
        } catch (Exception ex) {}
        newDsd.setColumns(getColumns(metadataProxy));

        //return metadata;
        return metadata;
    }

    private Collection<DSDColumn> getColumns(Object metadataProxy) {
        Collection<DSDColumn> columns = new LinkedList<>();
        try {
            Object dsd = metadataProxy.getClass().getMethod("getDsd").invoke(metadataProxy);
            for (Object columnProxy : (Collection) dsd.getClass().getMethod("getColumns").invoke(dsd))
                columns.add(getColumn(columnProxy));
        } catch (Exception ex) { }
        //Return found columns
        return columns;
    }

    private DSDColumn getColumn(Object columnProxy) {
        if (columnProxy!=null) {
            DSDColumn column = new DSDColumn();
            try { column.setId((String)columnProxy.getClass().getMethod("getId").invoke(columnProxy)); } catch (Exception ex) {}
            try { column.setDataType((DataType) columnProxy.getClass().getMethod("getDataType").invoke(columnProxy)); } catch (Exception ex) {}
            try { column.setKey((Boolean) columnProxy.getClass().getMethod("getKey").invoke(columnProxy)); } catch (Exception ex) {}
            try { column.setSubject((String)columnProxy.getClass().getMethod("getSubject").invoke(columnProxy)); } catch (Exception ex) {}
            try { column.setTitle((Map<String,String>)columnProxy.getClass().getMethod("getTitle").invoke(columnProxy)); } catch (Exception ex) {}
            try { column.setSupplemental((Map<String,String>)columnProxy.getClass().getMethod("getSupplemental").invoke(columnProxy)); } catch (Exception ex) {}
            //TODO Add DSDDomain?
            return column;
        }
        return null;
    }



}
