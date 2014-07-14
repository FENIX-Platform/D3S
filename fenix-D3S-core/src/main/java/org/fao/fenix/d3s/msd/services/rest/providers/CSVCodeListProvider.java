package org.fao.fenix.d3s.msd.services.rest.providers;

import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

@Provider
@Consumes("application/csv")
public class CSVCodeListProvider extends CSVProvider<Resource<Code>> {

    public enum CodeListFileStructure { tree, table }

    class Structure {
        SimpleDateFormat dateFormat;
        CodeListFileStructure fileBasicStructure;
        Integer[] codeColumnIndexes;
        Map<String,Integer> titleColumnIndexes = new HashMap<>();
        Map<String,Integer> descriptionColumnIndexes = new HashMap<>();
        Integer startYearColumnIndex;
        Integer expireYearColumnIndex;
    }


    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass.equals(Resource.class);
    }

    @Override
    protected Resource<Code> read(MeIdentification metadata, Properties structure, Iterable<String[]> data) throws Exception {
        return new Resource(metadata, createCodeListData(metadata,readStructure(structure),data) );
    }


    private Structure readStructure(Properties structureProperties) throws Exception {
        Structure structure = new Structure();

        //Mandatory structure informations
        structure.dateFormat = new SimpleDateFormat(structureProperties.getProperty("dateFormat","yyyy/mm/dd").trim());
        structure.fileBasicStructure = CodeListFileStructure.valueOf(structureProperties.getProperty("structure", "tree").trim());
        Collection<Integer> indexes = new LinkedList<>();
        for (String index : structureProperties.getProperty("codeColumnsIndex","1,2").trim().split(","))
            indexes.add(Integer.parseInt(index)-1);
        structure.codeColumnIndexes = indexes.toArray(new Integer[indexes.size()]);

        //Optional structure informations
        for (String fieldName : structureProperties.stringPropertyNames()) {
            String fieldValue = structureProperties.getProperty(fieldName).trim();

            if (fieldName.startsWith("titleColumnIndex_"))
                structure.titleColumnIndexes.put(fieldName.substring("titleColumnIndex_".length()).trim().toUpperCase(), Integer.parseInt(fieldValue) - 1);
            else if (fieldName.startsWith("descriptionColumnIndex_"))
                structure.descriptionColumnIndexes.put(fieldName.substring("descriptionColumnIndex_".length()).trim().toUpperCase(), Integer.parseInt(fieldValue) - 1);
            else if ("startYearColumnIndex".equals(fieldName))
                structure.startYearColumnIndex = Integer.parseInt(fieldValue)-1;
            else if ("expireYearColumnIndex".equals(fieldName))
                structure.expireYearColumnIndex = Integer.parseInt(fieldValue)-1;
        }

        return structure;
    }



    private Collection<Code> createCodeListData(MeIdentification metadata, Structure structure, Iterable<String[]> data) throws Exception {
        Map<String,Code> loadedCodes = new HashMap<>();
        Map<String, Set<String>> codeParents = new HashMap<>();
        Collection<String> rootCodes = new LinkedList<>();

        int rowCount = 0;
        for (String[] row : data) {
            if (row.length>0) {
                rowCount++;
                //Load code informations
                String code=null, parent=null;
                if (structure.fileBasicStructure==CodeListFileStructure.table) {
                    for (int i : structure.codeColumnIndexes)
                        if (row.length<=i)
                            throw new Exception ("Wrong structure for data row "+rowCount);
                        else if (row[i]!=null && !row[i].trim().equals("")) {
                            parent = code;
                            code = row[i];
                        } else
                            break;
                } else if (structure.fileBasicStructure==CodeListFileStructure.tree) {
                    if (structure.codeColumnIndexes.length!=2 || row.length<=structure.codeColumnIndexes[0] || row.length<=structure.codeColumnIndexes[1])
                        throw new Exception ("Wrong structure for data row "+rowCount);
                    else {
                        parent = row[structure.codeColumnIndexes[0]];
                        code = row[structure.codeColumnIndexes[1]];
                    }
                }
                if (code==null)
                    throw new Exception ("Wrong structure for data (row "+rowCount+")");

                //Create code object with labels
                Code codeObject = new Code(metadata, code);

                for (Map.Entry<String, Integer> labelIterator : structure.titleColumnIndexes.entrySet()) {
                    String label = row.length>labelIterator.getValue() ? row[labelIterator.getValue()] : null;
                    if (label!=null && !label.trim().equals(""))
                        codeObject.addTitle(labelIterator.getKey(), label);
                }
                for (Map.Entry<String, Integer> labelIterator : structure.descriptionColumnIndexes.entrySet()) {
                    String label = row.length>labelIterator.getValue() ? row[labelIterator.getValue()] : null;
                    if (label!=null && !label.trim().equals(""))
                        codeObject.addDescription(labelIterator.getKey(), label);
                }

                if (structure.startYearColumnIndex!=null && row.length>structure.startYearColumnIndex)
                    try { codeObject.setFromDate(Integer.parseInt(row[structure.startYearColumnIndex])); } catch (Exception ex) { throw new Exception ("Wrong value for start year (row "+rowCount+")"); }
                if (structure.expireYearColumnIndex!=null && row.length>structure.expireYearColumnIndex)
                    try { codeObject.setToDate(Integer.parseInt(row[structure.expireYearColumnIndex])); } catch (Exception ex) { throw new Exception ("Wrong value for expire year (row "+rowCount+")"); }

                //Store temporary code informations
                loadedCodes.put(code, codeObject);
                if (parent!=null && !parent.trim().equals("")) {
                    Set<String> children = codeParents.get(parent);
                    if (children==null)
                        codeParents.put(parent, children = new HashSet<>());
                    children.add(code);
                } else
                    rootCodes.add(code);
            }
        }

        //Append hierarchy
        Collection<Code> root = new LinkedList<>();
        for (String rootCode : rootCodes)
            root.add(loadedCodes.get(rootCode));
        for (Map.Entry<String,Set<String>> parentEntry : codeParents.entrySet()) {
            Code parentO = loadedCodes.get(parentEntry.getKey());
            for (String code : parentEntry.getValue())
                parentO.addChild(loadedCodes.get(code));
        }

        //Set level informations
        for (Code code : root)
            setCodeLevel(0, code);

        //Check orphan codes
        Collection<Code> orphanCodes = new LinkedList<>();
        for (Code codeO : loadedCodes.values())
            if (!codeO.isChild() && !rootCodes.contains(codeO.getCode()))
                orphanCodes.add(codeO);

        if (orphanCodes.size()>0) {
            StringBuilder errorMessageBuffer = new StringBuilder("There are errors in codelist import:");
            for (Code codeO : orphanCodes)
                errorMessageBuffer.append('\n').append(codeO.getCode());
            throw new Exception(errorMessageBuffer.toString());
        }

        return null;
    }

    private void setCodeLevel(int parentLevel, Code code) {
        int level = Math.max(parentLevel+1, code.getLevel()!=null ? code.getLevel() : 1);
        code.setLevel(level);
        Collection<Code> children = code.getChildren();
        if (children!=null)
            for (Code child : children)
                setCodeLevel(level,child);
    }

}
