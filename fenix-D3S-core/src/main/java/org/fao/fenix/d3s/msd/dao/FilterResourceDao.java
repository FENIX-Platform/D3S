package org.fao.fenix.d3s.msd.dao;

import org.fao.fenix.commons.msd.dto.data.CodesFilter;
import org.fao.fenix.commons.msd.dto.data.FieldFilter;
import org.fao.fenix.commons.msd.dto.data.ResourceFilter;
import org.fao.fenix.commons.msd.dto.data.TimeFilter;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

public class FilterResourceDao extends ResourceDao {
    @Inject CodeListResourceDao codeListResourceDao;


    @Override
    public Collection loadData(MeIdentification metadata) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void insertData(MeIdentification metadata, Collection data) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void updateData(MeIdentification metadata, Collection data, boolean overwrite) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteData(MeIdentification metadata) throws Exception {
        throw new UnsupportedOperationException();
    }



    public Collection<MeIdentification> filter (ResourceFilter filter) throws Exception {
        Collection params = new LinkedList();
        String query = createQuery(filter, params);
        return query!=null ? select(MeIdentification.class, query, params.toArray()) : null;
    }


    private String createQuery (ResourceFilter filter, Collection params) throws Exception {
        StringBuilder queryFilter = new StringBuilder();
        if (filter!=null)
            for (Map.Entry<String, FieldFilter> filterEntry : filter.entrySet()) {
                String fieldName = ("index."+filterEntry.getKey()).replace('.','|');
                FieldFilter fieldFilter = filterEntry.getValue();
                if (fieldFilter!=null) {
                    switch (fieldFilter.getFilterType()) {
                        case code:
                            Collection<String> codes = new LinkedList<>();
                            for (CodesFilter codesFilter : fieldFilter.codes)
                                codes.addAll(getFilterCodes(codesFilter));
                            if (codes.size()>0) {
                                params.addAll(codes);
                                queryFilter.append(" AND (");
                                for (int i=0, l=codes.size(); i<l; i++)
                                    queryFilter.append(fieldName).append(" CONTAINS ? OR ");
                                queryFilter.setLength(queryFilter.length()-4);
                                queryFilter.append(')');
                            }
                            break;
                        case enumeration:
                            params.addAll(fieldFilter.enumeration);
                            queryFilter.append(" AND (");
                            for (int i=0, l=fieldFilter.enumeration.size(); i<l; i++)
                                queryFilter.append(fieldName).append(" = ? OR ");
                            queryFilter.setLength(queryFilter.length()-4);
                            queryFilter.append(')');
                            break;
                        case time:
                            queryFilter.append(" AND (");
                            for (TimeFilter time : fieldFilter.time) {
                                if (time.from!=null) {
                                    queryFilter.append(fieldName).append("|to >= ? OR ");
                                    params.add(time.from);
                                }
                                if (time.to!=null) {
                                    queryFilter.append(fieldName).append("|from <= ? OR ");
                                    params.add(time.to);
                                }
                            }
                            queryFilter.setLength(queryFilter.length()-4);
                            queryFilter.append(')');
                        default:
                    }
                }
            }

        return "SELECT FROM MeIdentification" + (queryFilter.length()>0 ? " WHERE " + queryFilter.substring(4) : ""); //TODO
    }

    private Collection<String> getFilterCodes(CodesFilter codesFilter) throws Exception {
        Collection<String> codes = new LinkedList<>();
        if (codesFilter!=null) {
            String uid = codesFilter.getUid();
            String version = codesFilter.getVersion();
            String codeListID = uid!=null ? (uid + version!=null ? '|'+version : "") : null;
            if (codeListID!=null) {
                Collection<String> filterCodes = codesFilter.getCodes();
                if (filterCodes!=null && filterCodes.size()>0)
                    for (String filterCode : filterCodes)
                        codes.add(codeListID+'|'+filterCode);
                else
                    codes.add(codeListID);
                    //TODO support level and levels parameters
            }
        }
        return codes;
    }
}
