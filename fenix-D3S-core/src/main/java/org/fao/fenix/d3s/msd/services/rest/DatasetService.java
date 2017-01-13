package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.commons.msd.dto.data.Direction;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.full.OjCodeList;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.msd.dao.DatasetResourceDao;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.LinkedList;

@Path("dataset")
@Produces(MediaType.APPLICATION_JSON+"; charset=utf-8")
@Consumes(MediaType.APPLICATION_JSON)
public class DatasetService {
    @Inject private DatasetResourceDao datasetDao;
    @Inject private CodeListResourceDao codelistDao;
    @Inject private CodesService codesService;


    @GET
    @Path("distinct/{uid}/{columnId}")
    public Collection getColumnDistinct(@PathParam("uid") String uid, @PathParam("columnId") String columnId) throws Exception {
        return getColumnDistinct(uid, null, columnId);
    }

    @GET
    @Path("distinct/{uid}/{version}/{columnId}")
    public Collection getColumnDistinct(@PathParam("uid") String uid, @PathParam("version") String version, @PathParam("columnId") String columnId) throws Exception {
        //Retrieve metadata
        MeIdentification<DSDDataset> datasetMetadata = datasetDao.loadMetadata(uid, version);
        if (datasetMetadata == null)
            throw new NotFoundException("Dataset not found");
        //Retrieve column
        DSDColumn column = getColumn(datasetMetadata, columnId);
        //Retrieve data
        Collection distinct = datasetDao.getColumnDistinct(datasetMetadata, columnId);
        //Format data
        if (column.getDataType()==DataType.code)
            return createCodelistTree(distinct, column);
        else
            return distinct;
    }


    Collection<org.fao.fenix.commons.msd.dto.templates.codeList.Code> createCodelistTree(Collection data, DSDColumn column) throws Exception {
        Collection<String> distinct = toStringList(data);

        MeIdentification<DSDCodelist> codelistMetadata = findCodelist(column);
        if (codelistMetadata == null)
            throw new NotFoundException("Codelist not found");
        Collection<org.fao.fenix.commons.msd.dto.full.Code> originalCodes = codelistDao.loadData(codelistMetadata, null, null, distinct.toArray(new String[distinct.size()]));
        //Create tree
        Collection<org.fao.fenix.commons.msd.dto.full.Code> codes = new LinkedList<>();
        for (org.fao.fenix.commons.msd.dto.full.Code code : originalCodes)
            codes.add(codesService.getHierarchy(code, null, Direction.up));
        return ResponseBeanFactory.getInstances(Code.class, codesService.mergeBranches(codes));
    }

    DSDColumn getColumn(MeIdentification<DSDDataset> metadata, String columnId) throws Exception {
        DSDColumn column = null;
        DSDDataset dsd = metadata.getDsd();
        Collection<DSDColumn> columns = dsd != null ? dsd.getColumns() : null;
        if (columns != null)
            for (DSDColumn c : columns)
                if (c.getId().equals(columnId))
                    column = c;
        if (column == null)
            throw new BadRequestException("Dataset column not found: " + metadata.getUid() + " - " + metadata.getVersion() + " - " + columnId);
        return column;
    }

    private MeIdentification<DSDCodelist> findCodelist(DSDColumn column) throws Exception {
        DSDDomain domain = column!=null ? column.getDomain() : null;
        Collection<OjCodeList> codeLists = domain != null ? domain.getCodes() : null;
        OjCodeList codeList = codeLists != null && codeLists.size() == 1 ? codeLists.iterator().next() : null;
        String codeListUid = codeList != null ? codeList.getIdCodeList() : null;
        String codeListVersion = codeList != null ? codeList.getVersion() : null;
        if (codeListUid == null)
            throw new BadRequestException("Column codelist not declared");
        return codelistDao.loadMetadata(codeListUid, codeListVersion);
    }


    //Utils
    private Collection<String> toStringList (Collection data) {
        Collection<String> stringList = new LinkedList<>();
        for (Object item : data)
            stringList.add(item!=null ? item.toString() : null);
        return stringList;
    }

}
