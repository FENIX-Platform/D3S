package org.fao.fenix.d3s.msd.services.rest;

import org.apache.log4j.Logger;
import org.fao.fenix.commons.find.dto.filter.CodesFilter;
import org.fao.fenix.commons.msd.dto.data.Direction;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.full.MeContent;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.full.OjCodeList;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.cache.CacheFactory;
import org.fao.fenix.d3s.cache.manager.CacheManager;
import org.fao.fenix.d3s.cache.storage.Storage;
import org.fao.fenix.d3s.cache.storage.dataset.DatasetStorage;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.msd.dao.DatasetResourceDao;
import org.fao.fenix.d3s.msd.services.spi.Codes;

import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.*;

@Path("msd/codes")
public class CodesService implements Codes {
    private static final Logger LOGGER = Logger.getLogger("access");

    @Inject private CodeListResourceDao dao;
    @Inject private DatasetResourceDao datasetDao;



    @Override
    public org.fao.fenix.commons.msd.dto.full.Code getCodeHierarchy(String uid, String code, Integer depth, Direction direction) throws Exception {
        return getCodeHierarchy(uid, null, code, depth, direction);
    }

    @Override
    public org.fao.fenix.commons.msd.dto.full.Code getCodeHierarchy(String uid, String version, String code, Integer depth, Direction direction) throws Exception {
        LOGGER.info("Codes HIERARCHY: @uid = "+uid+" - @version = "+version+" - @code = "+code+" - @depth = "+depth+" - @direction = "+direction);
        return getHierarchy(dao.getCode(uid,version,code), depth, direction);
    }


    @Override
    public Collection<Code> getCodes(CodesFilter filter, boolean tree) throws Exception {
        LOGGER.info("Codes FILTER: @tree = "+tree);
        LOGGER.debug("Codes FILTER: @filter... "+filter);
        MeIdentification metadata = filter.rid!=null ? dao.loadMetadata(filter.rid,null) : dao.loadMetadata(filter.uid,filter.version);
        return loadCodes(metadata, filter.level, filter.levels, filter.codes, filter.label, tree);
    }

    @Override
    public Code getRoot(Collection<String> codes, String uid, Integer depth) throws Exception {
        return getRoot(codes, uid, null, depth);
    }

    @Override
    public Code getRoot(Collection<String> codes, String uid, String version, Integer depth) throws Exception {
        //Retrieve data
        org.fao.fenix.commons.msd.dto.full.Code data = dao.getRoot(uid, version, codes);
        if (data!=null) {
            if (depth!=null)
                Code.levelInfo.set(new Integer[]{data.getLevel(), data.getLevel() + depth - 1});
            return ResponseBeanFactory.getInstance(Code.class, data);
        }
        return null;
    }

    @Override
    public Collection<Code> distinct(String uid, String columnId) throws Exception {
        return distinct(uid, null, columnId);
    }

    @Override
    public Collection<Code> distinct(String uid, String version, String columnId) throws Exception {
        //Retrieve data
        MeIdentification<DSDDataset> datasetMetadata = datasetDao.loadMetadata(uid,version);
        if (datasetMetadata==null)
            throw new NotFoundException("Dataset not found");
        MeIdentification<DSDCodelist> codelistMetadata = findDatasetColumnCodelist(datasetMetadata, columnId);
        if (codelistMetadata==null)
            throw new NotFoundException("Codelist not found");
        Collection<String> distinct = datasetDao.getCodedColumnDistinct(datasetMetadata, columnId);
        Collection<org.fao.fenix.commons.msd.dto.full.Code> originalCodes = dao.loadData(codelistMetadata, null, null, distinct.toArray(new String[distinct.size()]));
        //Create tree
        Collection<org.fao.fenix.commons.msd.dto.full.Code> codes = new LinkedList<>();
        for (org.fao.fenix.commons.msd.dto.full.Code code : originalCodes)
            codes.add(getHierarchy(code,null,Direction.up));
        return ResponseBeanFactory.getInstances(Code.class, mergeBranches(codes));
    }


    //Logic
    private Collection<Code> loadCodes(MeIdentification metadata, Integer level, Integer levels, Collection<String> codes, String label, boolean tree) throws Exception {
        MeContent meContent = metadata!=null ? metadata.getMeContent() : null;
        RepresentationType type = meContent!=null ? meContent.getResourceRepresentationType() : null;
        if (type == RepresentationType.codelist) {
            //Filter normalization
            String[] codesArray = codes!=null && codes.size()>0 ? codes.toArray(new String[codes.size()]) : null;
            level = level==null && codesArray==null && label==null ? new Integer(1) : level;
            //Retrieve data
            Collection<org.fao.fenix.commons.msd.dto.full.Code> data = dao.loadData(metadata, label, level, codesArray);
            //Return data proxy
            if (data!=null && data.size()>0) {
                if (tree)
                    data = toTree(data, levels);
                else if (level != null)
                    Code.levelInfo.set(new Integer[]{level, levels != null ? level + levels - 1 : null});
                else if (levels!=null)
                    data = getInjectLevelDataWrapper(data, levels);

                return ResponseBeanFactory.getInstances(Code.class, data);
            }
        }
        return null;
    }

    private Set<org.fao.fenix.commons.msd.dto.full.Code> toTree (Collection<org.fao.fenix.commons.msd.dto.full.Code> codes, Integer levels) throws CloneNotSupportedException {
        Set<org.fao.fenix.commons.msd.dto.full.Code> root = new HashSet<>();
        Map<String, org.fao.fenix.commons.msd.dto.full.Code> loadedCodes = new HashMap<String, org.fao.fenix.commons.msd.dto.full.Code>();
        appendParents(codes, null, loadedCodes, root);
        appendChildren(codes, null, loadedCodes, levels);
        return root;
    }
    private void appendChildren(Collection<org.fao.fenix.commons.msd.dto.full.Code> codes, org.fao.fenix.commons.msd.dto.full.Code parent, Map<String, org.fao.fenix.commons.msd.dto.full.Code> loadedCodes, Integer levels) throws CloneNotSupportedException {
        if (codes!=null && levels!=null && levels>0)
            for (org.fao.fenix.commons.msd.dto.full.Code code : codes) {
                org.fao.fenix.commons.msd.dto.full.Code clone = loadedCodes.get(code.getCode());
                if (clone==null) {
                    clone = (org.fao.fenix.commons.msd.dto.full.Code) code.clone();
                    loadedCodes.put(code.getCode(), clone);
                }

                if (parent!=null) {
                    parent.addChild(code);
                    clone.addParent(parent);
                }

                appendChildren(code.getChildren(), clone, loadedCodes, levels-1);
            }
    }
    private void appendParents (Collection<org.fao.fenix.commons.msd.dto.full.Code> codes, org.fao.fenix.commons.msd.dto.full.Code child, Map<String, org.fao.fenix.commons.msd.dto.full.Code> loadedCodes, Set<org.fao.fenix.commons.msd.dto.full.Code> root) throws CloneNotSupportedException {
        if (codes!=null)
            for (org.fao.fenix.commons.msd.dto.full.Code code : codes) {
                org.fao.fenix.commons.msd.dto.full.Code clone = loadedCodes.get(code.getCode());
                if (clone==null) {
                    clone = (org.fao.fenix.commons.msd.dto.full.Code) code.clone();
                    loadedCodes.put(code.getCode(), clone);

                    Collection<org.fao.fenix.commons.msd.dto.full.Code> parents = code.getParents();
                    if (parents != null && parents.size() > 0)
                        appendParents(parents, clone, loadedCodes, root);
                    else
                        root.add(clone);
                }

                if (child != null) {
                    clone.addChild(child);
                    child.addParent(clone);
                }
            }
    }


    //Hierarchy
    private org.fao.fenix.commons.msd.dto.full.Code getHierarchy (org.fao.fenix.commons.msd.dto.full.Code rawCode, Integer depth, Direction direction) throws CloneNotSupportedException {
        if (depth==null)
            depth = Integer.MAX_VALUE;

        org.fao.fenix.commons.msd.dto.full.Code code = rawCode!=null ? (org.fao.fenix.commons.msd.dto.full.Code) rawCode.clone() : null;
        if (code!=null) {
            Collection<org.fao.fenix.commons.msd.dto.full.Code> children = depth>1 && (direction==null || direction==Direction.down) ? rawCode.getChildren() : null;
            Collection<org.fao.fenix.commons.msd.dto.full.Code> parents = depth>1 && (direction==null || direction==Direction.up) ? rawCode.getParents() : null;
            if (children!=null)
                for (org.fao.fenix.commons.msd.dto.full.Code child : children) {
                    org.fao.fenix.commons.msd.dto.full.Code newChild = getHierarchy(child, depth - 1, Direction.down);
                    code.addChild(newChild);
                    newChild.addParent(code);
                }
            if (parents!=null)
                for (org.fao.fenix.commons.msd.dto.full.Code parent : parents) {
                    org.fao.fenix.commons.msd.dto.full.Code newParent = getHierarchy(parent, depth - 1, Direction.up);
                    code.addParent(newParent);
                    newParent.addChild(code);
                }
        }
        return code;
    }

    private Collection<org.fao.fenix.commons.msd.dto.full.Code> mergeBranches(Collection<org.fao.fenix.commons.msd.dto.full.Code> sourceCodes) {
        Map<String, org.fao.fenix.commons.msd.dto.full.Code> mergeCodesMap = new LinkedHashMap<>();
        Map<String, Set<String>> parentCodesMap = new HashMap<>();
        Map<String, Set<String>> childrenCodesMap = new HashMap<>();

        fillMergeCodesMap(sourceCodes, mergeCodesMap, parentCodesMap, childrenCodesMap);
        Collection<org.fao.fenix.commons.msd.dto.full.Code> root = mergeBranches(mergeCodesMap,parentCodesMap,childrenCodesMap);

        return root.size()>0 ? root : null;
    }
    private Collection<org.fao.fenix.commons.msd.dto.full.Code> mergeBranches (Map<String, org.fao.fenix.commons.msd.dto.full.Code> mergeCodesMap, Map<String, Set<String>> parentCodesMap, Map<String, Set<String>> childrenCodesMap) {
        Collection<org.fao.fenix.commons.msd.dto.full.Code> root = new LinkedList<>();
        for (org.fao.fenix.commons.msd.dto.full.Code code : mergeCodesMap.values()) {
            code.setParents(null);
            code.setChildren(null);

            Collection<String> parentsCode = parentCodesMap.get(code.getCode());
            if (parentsCode!=null && parentsCode.size()>0)
                for (String parentCode : parentsCode)
                    code.addParent(mergeCodesMap.get(parentCode));
            else
                root.add(code);

            Collection<String> childrenCode = childrenCodesMap.get(code.getCode());
            if (childrenCode!=null)
                for (String childCode : childrenCode)
                    code.addChild(mergeCodesMap.get(childCode));
        }
        return root;
    }
    private void fillMergeCodesMap(Collection<org.fao.fenix.commons.msd.dto.full.Code> leafs, Map<String, org.fao.fenix.commons.msd.dto.full.Code> mergeCodesMap, Map<String, Set<String>> parentCodesMap, Map<String, Set<String>> childrenCodesMap) {
        if (leafs!=null) {
            for (org.fao.fenix.commons.msd.dto.full.Code code : leafs) {
                Collection<org.fao.fenix.commons.msd.dto.full.Code> parents = code.getParents();
                Collection<org.fao.fenix.commons.msd.dto.full.Code> children = code.getChildren();

                mergeCodesMap.put(code.getCode(), code);
                if (parents!=null) {
                    Set<String> parentsCode = parentCodesMap.get(code.getCode());
                    if (parentsCode == null)
                        parentCodesMap.put(code.getCode(), parentsCode = new TreeSet<>());
                    for (org.fao.fenix.commons.msd.dto.full.Code parent : parents)
                        parentsCode.add(parent.getCode());
                }
                if (children!=null) {
                    Set<String> childrenCode = childrenCodesMap.get(code.getCode());
                    if (childrenCode == null)
                        childrenCodesMap.put(code.getCode(), childrenCode = new TreeSet<>());
                    for (org.fao.fenix.commons.msd.dto.full.Code child : children)
                        childrenCode.add(child.getCode());
                }

                fillMergeCodesMap(parents, mergeCodesMap, parentCodesMap, childrenCodesMap);
                //fillMergeCodesMap(children, mergeCodesMap, parentCodesMap, childrenCodesMap);
            }
        }
    }


    //Utils
    private Collection<org.fao.fenix.commons.msd.dto.full.Code> getInjectLevelDataWrapper (final Collection<org.fao.fenix.commons.msd.dto.full.Code> data, final Integer levels) {
        return new LinkedList<org.fao.fenix.commons.msd.dto.full.Code>() {
            @Override
            public int size() {
                return data.size();
            }

            @Override
            public boolean isEmpty() {
                return data.isEmpty();
            }

            @Override
            public Iterator<org.fao.fenix.commons.msd.dto.full.Code> iterator() {
                final Iterator<org.fao.fenix.commons.msd.dto.full.Code> dataIterator = data.iterator();
                return new Iterator<org.fao.fenix.commons.msd.dto.full.Code>() {
                    @Override
                    public boolean hasNext() {
                        return dataIterator.hasNext();
                    }

                    @Override
                    public org.fao.fenix.commons.msd.dto.full.Code next() {
                        org.fao.fenix.commons.msd.dto.full.Code code = dataIterator.next();
                        if (code!=null)
                            Code.levelInfo.set(new Integer[]{code.getLevel(), levels != null ? code.getLevel() + levels - 1 : null});
                        return code;
                    }

                    @Override
                    public void remove() {
                        dataIterator.remove();
                    }
                };
            }
        };
    }

    private MeIdentification<DSDCodelist> findDatasetColumnCodelist(MeIdentification<DSDDataset> metadata, String columnId) throws Exception {
        if (metadata!=null) {
            DSDColumn column = null;
            DSDDataset dsd = metadata.getDsd();
            Collection<DSDColumn> columns = dsd != null ? dsd.getColumns() : null;
            if (columns != null)
                for (DSDColumn c : columns)
                    if (c.getId().equals(columnId))
                        column = c;
            if (column == null)
                throw new BadRequestException("Dataset column not found: " + metadata.getUid() + " - " + metadata.getVersion() + " - " + columnId);
            if (column.getDataType() != DataType.code)
                throw new BadRequestException("Dataset column isn't coded: " + metadata.getUid() + " - " + metadata.getVersion() + " - " + columnId);
            DSDDomain domain = column.getDomain();
            Collection<OjCodeList> codeLists = domain != null ? domain.getCodes() : null;
            OjCodeList codeList = codeLists != null && codeLists.size() == 1 ? codeLists.iterator().next() : null;
            String codeListUid = codeList != null ? codeList.getIdCodeList() : null;
            String codeListVersion = codeList != null ? codeList.getVersion() : null;
            if (codeListUid == null)
                throw new BadRequestException("Column codelist not declared");
            return dao.loadMetadata(codeListUid, codeListVersion);
        }
        return null;
    }
}
