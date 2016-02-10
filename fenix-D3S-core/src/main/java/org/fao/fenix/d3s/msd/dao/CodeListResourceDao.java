package org.fao.fenix.d3s.msd.dao;

import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.storage.ORecordDuplicatedException;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.server.tools.orient.DocumentTrigger;

import java.io.BufferedReader;
import java.text.CollationElementIterator;
import java.util.*;

public class CodeListResourceDao extends ResourceDao<DSDCodelist, Code> {
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
    public void fetch(MeIdentification metadata) throws Exception {
        //TODO linking to external codelists isn't supported
        throw new UnsupportedOperationException();
    }

    @Override
    public Long getSize(MeIdentification<DSDCodelist> metadata) throws Exception {
        ORID metadataORid = metadata!=null ? metadata.getORID() : null;
        Collection<ODocument> size = metadataORid!=null ? select("select count(*) as size from Code where codeList = ?",metadataORid) : null;
        return size!=null && size.size()>0 ? (Long)size.iterator().next().field("size") : null;
    }

    @Override
    public Collection<Code> loadData(MeIdentification<DSDCodelist> metadata) throws Exception {
        return loadData(metadata, null, 1);
    }

    @Override
    protected void insertData(MeIdentification<DSDCodelist> metadata, Collection<Code> data) throws Exception {
        Collection<Code> normalizedData = normalization(metadata, data, null, null, true);
        saveCustomEntity(false,true,true, normalizedData.toArray(new Code[normalizedData.size()]));
    }

    @Override
    protected void updateData(MeIdentification<DSDCodelist> metadata, Collection<Code> data, boolean overwrite) throws Exception {
        Collection<String> toDelete = overwrite ? new LinkedList<String>() : null;
        Collection<Code> normalizedData = normalization(metadata, data, loadData(metadata), toDelete, overwrite);

        saveCustomEntity(overwrite, true, true, normalizedData.toArray(new Code[normalizedData.size()]));
        deleteData(metadata, toDelete);
    }



    //Codes selection
    public Code getCode(String uid, String version, String code) throws Exception {
        return getCode(loadMetadata(uid,version), code);
    }
    public Code getCode(MeIdentification<DSDCodelist> metadata, String code) throws Exception {
        Collection<Code> result = loadData(metadata,null,null,code);
        return result!=null && result.size()==1 ? result.iterator().next() : null;
    }

    public Collection<Code> loadData(MeIdentification<DSDCodelist> metadata, String label, Integer level, String ... codes) throws Exception {
        if (metadata==null)
            return null;

        Collection<Object> params = new LinkedList<>();
        StringBuilder query = new StringBuilder("select from Code where");

        if (label!=null && label.trim().length()>0) {
            query.append(" indexLabel lucene ? and");
            params.add(label.toLowerCase());
        }

        query.append(" codeList = ?");
        params.add(metadata.getORID());

        if (level!=null && level>0) {
            query.append(" and level = ?");
            params.add(level);
        }
        if (codes!=null && codes.length>0) {
            query.append(" and code in [");
            for (String code : codes) {
                query.append("?,");
                params.add(code);
            }
            query.setCharAt(query.length()-1,']');
        }

        return select(Code.class, query.toString(),null,null, params.toArray());
    }


    public int deleteData(MeIdentification<DSDCodelist> metadata, Collection<String> codes) throws Exception {
        return metadata!=null && codes!=null && codes.size()>0 ? command("delete from Code where codeList = ? and code in ?", metadata.getORID(), codes) : 0;
    }

    @Override
    public void deleteData(MeIdentification<DSDCodelist> metadata) throws Exception {
        if (metadata!=null)
            command("delete from Code where codeList = ?", metadata.getORID());
    }


    //Utils
    private Collection<Code> normalization (MeIdentification<DSDCodelist> codeListResource, Collection<Code> codeList, Collection<Code> existingCodeList, Collection<String> toDelete, boolean overwrite) throws Exception {
        Map<String,ORID> existingORIDs = getCodeListRids(existingCodeList);
        Map<String,Code> visitedNodes = new HashMap<>();
        codeList = normalization(codeListResource, codeList, null, visitedNodes, existingORIDs, overwrite);
        //Find lost database codes
        if (existingCodeList!=null && toDelete!=null)
            for (String codeValue : existingORIDs.keySet())
                if (!visitedNodes.containsKey(codeValue))
                    toDelete.add(codeValue);
        //Fill leaf field
        setLeafField(codeList, overwrite);
        //Fill label index field
        setLabelIndexField(codeList, overwrite);
        //Return updated codelist objects
        return codeList;
    }

    private Collection<Code> normalization(MeIdentification<DSDCodelist> codeListResource, Collection<Code> codes, Code parentCode, Map<String,Code> visitedNodes, Map<String,ORID> existingORIDs, boolean overwrite) throws Exception {
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
                    if (parentCode.getCode().equals(code.getCode()))
                        throw new Exception("Child and parent with the same code: "+code.getCode());
                    code.setLevel(Math.max(parentCode.getLevel() + 1, code.getLevel()));      //Update level
                    code.addParent(parentCode);                                             //Add parents links
                }
                //Update children link
                Collection<Code> normalizedChildren = normalization(codeListResource, currentCode.getChildren(), code, visitedNodes, existingORIDs, overwrite);
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

    private void setLeafField(Collection<Code> codeList, boolean overwrite) {
        if (codeList!=null)
            for (Code code : codeList) {
                Collection<Code> children = code.getChildren();
                if (overwrite || code.getLeaf()==null || children!=null)
                    code.setLeaf(children==null || children.size()==0);
                setLeafField(children, overwrite);
            }
    }

    private void setLabelIndexField(Collection<Code> codeList, boolean overwrite) {
        if (codeList!=null)
            for (Code code : codeList) {
                Map<String, String> title = code.getTitle();
                Map<String, String> description = code.getDescription();
                if (overwrite || code.getIndexLabel()==null || title!=null || description!=null) {
                    //Update code label index
                    StringBuilder indexLabel = new StringBuilder(code.getCode());
                    if (title!=null)
                        for (String t : title.values())
                            indexLabel.append(' ').append(t.trim().toLowerCase());
                    if (description!=null)
                        for (String d : description.values())
                            indexLabel.append(' ').append(d.trim().toLowerCase());

                    code.setIndexLabel(indexLabel.toString());
                }
                setLabelIndexField(code.getChildren(), overwrite);
            }
    }


}


/*
    public Collection<Code> loadData(MeIdentification<DSDCodelist> metadata, Integer level, String ... codes) throws Exception {
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
*/
