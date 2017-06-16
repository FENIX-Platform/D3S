package org.fao.fenix.sdmx.d3s;

import demo.sdmxsource.webservice.main.finalPackage.ExportSDMX;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.msd.dao.DatasetResourceDao;
import org.jboss.weld.context.ApplicationContext;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.NoContentException;
import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.LinkedList;

@Path("/sdmx/resources")
public class SDMXExport {
    @Inject DatasetResourceDao datasetResourceDao;
    @Inject CodeListResourceDao codeListResourceDao;

    @GET
    @Path("/{uid}")
    public String getDataset(@PathParam("uid") String uid) throws Exception {
        return getDataset(uid, null);
    }

    @GET
    @Path("/{uid}/{version}")
    public String getDataset(@PathParam("uid") String uid, @PathParam("version") String version) throws Exception {
        MeIdentification<DSDDataset> metadata = datasetResourceDao.loadMetadata(uid,version);
        if (metadata==null)
            throw new NoContentException(uid + " - "+ version);

        Resource<DSDDataset, Object[]> dataset = new Resource<>(metadata,datasetResourceDao.loadData(metadata));

        //Load codelists
        Collection<Resource<DSDCodelist,Code>> codeLists = new LinkedList<>();
        for (DSDColumn column : dataset.getMetadata().getDsd().getColumns())
            if (column.getDataType()== DataType.code){
                MeIdentification<DSDCodelist> codeListMetadata = codeListResourceDao.loadMetadata(uid,version);
                if (codeListMetadata!=null)
                    codeLists.add(new Resource<>(codeListMetadata, codeListResourceDao.loadData(codeListMetadata)));
            }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        new ExportSDMX().execution(dataset,codeLists,outputStream);
        String sdmxData = new String(outputStream.toByteArray());

        return sdmxData;
    }
}
