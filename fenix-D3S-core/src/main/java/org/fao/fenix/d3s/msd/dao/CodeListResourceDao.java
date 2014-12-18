package org.fao.fenix.d3s.msd.dao;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.io.BufferedReader;
import java.util.*;

public class CodeListResourceDao extends ResourceDao<Code> {
/*
    public Collection<Resource<Code>> getCodeLists() throws Exception {
        Collection<Resource<Code>> codeLists = new LinkedList<>();
        Collection<MeIdentification> codeListResources = select(MeIdentification.class, "select from MeIdentification where meContent.resourceRepresentationType = ?", RepresentationType.codelist.name());
        for (MeIdentification codeListResource : codeListResources) {
            Collection<Code> rootLevel = select(Code.class, "select from Code where codeList = ? and level = 1", codeListResource);
            codeLists.add(new Resource(codeListResource, rootLevel));
        }
        return codeLists;
    }
*/


    @Override
    public Collection<Code> loadData(MeIdentification metadata) throws Exception {
        return loadData(metadata,1);
    }

    @Override
    protected void insertData(MeIdentification metadata, Collection<Code> data) throws Exception {
        saveCustomEntity(normalization(metadata, data, null, null),false,true);
    }

    @Override
    protected void updateData(MeIdentification metadata, Collection<Code> data, boolean overwrite) throws Exception {
        Collection<String> toDelete = overwrite ? new LinkedList<String>() : null;
        saveCustomEntity(normalization(metadata, data, loadData(metadata), toDelete),overwrite,true);
        deleteData(metadata, toDelete);
    }



    //Codes selection
    public Collection<Code> loadData(MeIdentification metadata, Integer level, String ... codes) throws Exception {
        if (metadata==null)
            return null;

        StringBuilder query = new StringBuilder("select from Code where codeList = ?");
        ORID metadataORid = metadata.getORID();

        if (codes!=null && codes.length>0) {
            Collection<Code> result = new LinkedList<>();
            if (level!=null && level>0) {
                query.append(" and level = ? and code = ?");
                for (String code : codes)
                    result.addAll(select(Code.class, query.toString(), metadataORid, level, code));
            } else {
                query.append(" and code = ?");
                for (String code : codes)
                    result.addAll(select(Code.class, query.toString(), metadataORid, code));
            }
            return result;
        } else if (level!=null && level>0) {
            query.append(" and level = ?");
            return select(Code.class, query.toString(), metadataORid, level);
        } else
            return select(Code.class, query.toString(), metadataORid);
    }

    public int deleteData(MeIdentification metadata, Collection<String> codes) throws Exception {
        return metadata!=null && codes!=null && codes.size()>0 ? command("delete from Code where codeList = ? and code in ?", metadata.getORID(), codes) : 0;
    }

    @Override
    public void deleteData(MeIdentification metadata) throws Exception {
        if (metadata!=null)
            command("delete from Code where codeList = ?", metadata.getORID());
    }


    //Utils
    private Collection<Code> normalization (MeIdentification codeListResource, Collection<Code> codeList, Collection<Code> existingCodeList, Collection<String> toDelete) throws Exception {
        Map<String,ORID> existingORIDs = getCodeListRids(existingCodeList);
        Map<String,Code> visitedNodes = new HashMap<>();
        codeList = normalization(codeListResource, codeList, null, visitedNodes, existingORIDs);
        //Find lost database codes
        if (existingCodeList!=null && toDelete!=null)
            for (String codeValue : existingORIDs.keySet())
                if (!visitedNodes.containsKey(codeValue))
                    toDelete.add(codeValue);
        //Return updated codelist objects
        return codeList;
    }

    private Collection<Code> normalization(MeIdentification codeListResource, Collection<Code> codes, Code parentCode, Map<String,Code> visitedNodes, Map<String,ORID> existingORIDs) throws Exception {
        if (codes!=null) {
            Collection<Code> buffer = new LinkedList<>();

            for (Code currentCode : codes) {
                String codeValue = currentCode.getCode();
                if (codeValue == null)
                    throw new Exception("'code' field is mandatory into Code entities.");
                //Define working code
                Code code = null;
                if (visitedNodes.containsKey(codeValue)) {
                    code = visitedNodes.get(codeValue);
                } else {
                    code = currentCode;
                    currentCode.setLevel(1);
                    currentCode.setCodeList(codeListResource);
                    visitedNodes.put(codeValue,currentCode);
                }
                //Update parents link and level
                if (parentCode != null) {
                    code.setLevel(Math.max(parentCode.getLevel() + 1, code.getLevel()));      //Update level
                    code.addParent(parentCode);                                             //Add parents links
                }
                //Update children link
                Collection<Code> normalizedChildren = normalization(codeListResource, currentCode.getChildren(), code, visitedNodes, existingORIDs);
                Collection<Code> existingChildren = code.getChildren();
                if (normalizedChildren!=null && existingChildren!=null)
                    normalizedChildren.addAll(existingChildren);
                code.setChildren(normalizedChildren);
                //Set database ORID
                if (existingORIDs!=null && existingORIDs.containsKey(codeValue))
                    code.setORID(existingORIDs.get(codeValue));
                //Update current level codes buffer
                buffer.add(code);
            }

            return buffer;
        } else
            return null;
    }


    private Map<String,ORID> getCodeListRids (Collection<Code> codeList, Map<String,ORID> ... buffer) throws ORecordDuplicatedException {
        if (codeList!=null) {
            if (buffer.length==0)
                buffer = new Map[]{ new HashMap() };
            for (Code code : codeList) {
                ORID existingOrid = buffer[0].put(code.getCode(), code.getORID());
                //Check code entity duplication TODO check if it is really possible
                if (existingOrid!=null && !existingOrid.equals(code.getORID()))
                    throw new ORecordDuplicatedException("Code '"+code.getCode()+"' entity is duplicated into database",code.getORID());
                //Apply recursion
                getCodeListRids(code.getChildren(), buffer);
            }
            return buffer[0];
        } else
            return null;
    }
}
