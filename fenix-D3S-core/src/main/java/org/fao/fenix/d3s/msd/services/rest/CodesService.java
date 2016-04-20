package org.fao.fenix.d3s.msd.services.rest;

import org.apache.log4j.Logger;
import org.fao.fenix.commons.find.dto.filter.CodesFilter;
import org.fao.fenix.commons.msd.dto.data.Direction;
import org.fao.fenix.commons.msd.dto.full.MeContent;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.commons.msd.dto.type.RepresentationType;
import org.fao.fenix.d3s.msd.dao.CodeListResourceDao;
import org.fao.fenix.d3s.msd.services.spi.Codes;

import javax.inject.Inject;
import javax.ws.rs.Path;
import java.util.*;

@Path("msd/codes")
public class CodesService implements Codes {
    private static final Logger LOGGER = Logger.getLogger("access");

    @Inject private CodeListResourceDao dao;


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

    private org.fao.fenix.commons.msd.dto.full.Code getHierarchy (org.fao.fenix.commons.msd.dto.full.Code rawCode, Integer depth, Direction direction) throws CloneNotSupportedException {
        if (depth==null)
            depth = Integer.MAX_VALUE;

        org.fao.fenix.commons.msd.dto.full.Code code = rawCode!=null ? (org.fao.fenix.commons.msd.dto.full.Code) rawCode.clone() : null;
        if (code!=null) {
            Collection<org.fao.fenix.commons.msd.dto.full.Code> children = depth>1 && (direction==null || direction==Direction.down) ? rawCode.getChildren() : null;
            Collection<org.fao.fenix.commons.msd.dto.full.Code> parents = depth>1 && (direction==null || direction==Direction.up) ? rawCode.getParents() : null;
            if (children!=null)
                for (org.fao.fenix.commons.msd.dto.full.Code child : children)
                    code.addChild(getHierarchy(child,depth-1,Direction.down));
            if (parents!=null)
                for (org.fao.fenix.commons.msd.dto.full.Code parent : parents)
                    code.addParent(getHierarchy(parent,depth-1,Direction.up));
        }
        return code;
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
}
