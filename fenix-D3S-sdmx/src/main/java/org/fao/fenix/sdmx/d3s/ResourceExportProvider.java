package org.fao.fenix.sdmx.d3s;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.data.ResourceProxy;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("application/sdmx")
public class ResourceExportProvider  implements MessageBodyWriter<ResourceProxy> {
    @Override
    public boolean isWriteable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    String testMessage = "Vediamo se se lo ciuccia il provider";

    @Override
    public long getSize(ResourceProxy resource, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return testMessage.getBytes().length;
    }

    @Override
    public void writeTo(ResourceProxy resource, Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> multivaluedMap, OutputStream outputStream) throws IOException, WebApplicationException {
        new PrintStream(outputStream).print(testMessage);
    }
}
