package org.fao.fenix.sdmx.d3s;

import demo.sdmxsource.webservice.main.finalPackage.ExportSDMX;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.type.DataType;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedList;

@Provider
@Produces("application/sdmx")
public class ResourceExportProvider  implements MessageBodyWriter<ResourceProxy> {


    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(ResourceProxy resource, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return 0;
    }

    @Override
    public void writeTo(ResourceProxy resource, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {

        Resource<DSDDataset, Object[]> dataset = null;
        try {
            dataset=ResourceFactory.getDatasetInstance("FAOSTAT_QA");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        dataset = ResourceFactory.getDatasetInstance("FAOSTAT_QA");
        //Load codelists
        Collection<Resource<DSDCodelist,Code>> codeLists = new LinkedList<>();
        for (DSDColumn column : dataset.getMetadata().getDsd().getColumns()){
            if (column.getDataType()== DataType.code){
                try {
                    codeLists.add(ResourceFactory.getCodelistInstance(column.getDomain().getCodes().iterator().next().getIdCodeList()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        new ExportSDMX().execution(dataset,codeLists,outputStream);
    }

}
