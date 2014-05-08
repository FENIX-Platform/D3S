package org.fao.fenix.d3s.server.tools.resteasy;

import org.fao.fenix.commons.msd.dto.cl.Code;
import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.commons.msd.dto.cl.type.CSSharingPolicy;
import org.fao.fenix.commons.msd.dto.common.ContactIdentity;
import org.fao.fenix.commons.msd.utils.DataUtils;
import org.fao.fenix.commons.utils.CSVReader;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

@Provider
@Consumes("application/csv")
public class CSVProvider implements MessageBodyReader<CodeSystem> {
    public enum CodeListFileStructure { tree, table }

    class Structure {
        String csvSeparator;
        SimpleDateFormat dateFormat;
        CodeListFileStructure fileBasicStructure;
        Integer[] codeColumnIndexes;
        Map<String,Integer> titleColumnIndexes = new HashMap<>();
        Map<String,Integer> descriptionColumnIndexes = new HashMap<>();
        Map<String,Integer> supplementalColumnIndexes = new HashMap<>();
        Integer startYearColumnIndex;
        Integer expireYearColumnIndex;
    }


    @Override
    public boolean isReadable(Class<?> aClass, Type type, Annotation[] annotations, MediaType mediaType) {
        return aClass.equals(CodeSystem.class);
    }

    @Override
    public CodeSystem readFrom(Class<CodeSystem> codeSystemClass, Type type, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> stringStringMultivaluedMap, InputStream inputStream) throws IOException, WebApplicationException {
        try {
            CodeSystem codeList = new CodeSystem();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

            //Load structure and code list metadata
            Structure csvStructure = loadCodeListMetadata(codeList,readStructureProperties(in));

            //Load code list codes
            loadCodeListData(codeList,csvStructure,in);

            //Return codelist
            return codeList;
        } catch (Exception e) {
            throw new WebApplicationException("Code list structure problems found: "+ e.getMessage(),e);
        }
    }



    private Properties readStructureProperties(BufferedReader in) throws IOException {
        StringBuilder structureBuffer = new StringBuilder();
        for (String row=in.readLine(); row!=null && !row.trim().equals("--data--"); row=in.readLine())
            structureBuffer.append(row).append('\n');
        Properties structureProperties = new Properties();
        structureProperties.load(new StringReader(structureBuffer.toString()));
        return structureProperties;
    }

    private Structure loadCodeListMetadata(CodeSystem codeList, Properties structureProperties) throws Exception {
        Structure structure = new Structure();

        //Mandatory structure init (with default values if needed)
        structure.csvSeparator = structureProperties.getProperty("csvSeparator", ";").trim();
        structure.dateFormat = new SimpleDateFormat(structureProperties.getProperty("dateFormat","yyyy/mm/dd").trim());
        structure.fileBasicStructure = CodeListFileStructure.valueOf(structureProperties.getProperty("structure", "tree").trim());
        Collection<Integer> indexes = new LinkedList<>();
        for (String index : structureProperties.getProperty("codeColumnsIndex","1,2").trim().split(","))
            indexes.add(Integer.parseInt(index)-1);
        structure.codeColumnIndexes = indexes.toArray(new Integer[indexes.size()]);

        //Fill codelist metadata and add optional structure informations
        for (String fieldName : structureProperties.stringPropertyNames()) {
            String fieldValue = structureProperties.getProperty(fieldName).trim();

            if ("name".equalsIgnoreCase(fieldName))
                codeList.setSystem(fieldValue);
            else if ("version".equalsIgnoreCase(fieldName))
                codeList.setVersion(fieldValue);
            else if (fieldName.startsWith("title_"))
                codeList.addTitle(fieldName.substring("title_".length()).trim().toUpperCase(), fieldValue);
            else if (fieldName.startsWith("description_"))
                codeList.addDescription(fieldName.substring("description_".length()).trim().toUpperCase(), fieldValue);
            else if ("region".equalsIgnoreCase(fieldName))
                codeList.setRegion(new Code(fieldValue));
            else if ("category".equalsIgnoreCase(fieldName))
                codeList.setCategory(new Code(fieldValue));
            else if ("startDate".equalsIgnoreCase(fieldName))
                codeList.setStartDate(structure.dateFormat.parse(fieldValue));
            else if ("endDate".equalsIgnoreCase(fieldName))
                codeList.setEndDate(structure.dateFormat.parse(fieldValue));
            else if ("source".equalsIgnoreCase(fieldName))
                codeList.setSource(new ContactIdentity(fieldValue));
            else if ("provider".equalsIgnoreCase(fieldName))
                codeList.setProvider(new ContactIdentity(fieldValue));
            else if ("sharingPolicy".equalsIgnoreCase(fieldName))
                codeList.setSharingPolicy(CSSharingPolicy.valueOf(fieldValue));
            else if ("keywords".equalsIgnoreCase(fieldName))
                for (String keyword : fieldValue.split(",")) {
                    if (keyword != null && !keyword.trim().equals(""))
                        codeList.addKeyWord(keyword);
                }
            else if (fieldName.startsWith("titleColumnIndex_"))
                structure.titleColumnIndexes.put(fieldName.substring("titleColumnIndex_".length()).trim().toUpperCase(), Integer.parseInt(fieldValue)-1);
            else if (fieldName.startsWith("descriptionColumnIndex_"))
                structure.descriptionColumnIndexes.put(fieldName.substring("descriptionColumnIndex_".length()).trim().toUpperCase(), Integer.parseInt(fieldValue)-1);
            else if (fieldName.startsWith("supplementalColumnIndex_"))
                structure.supplementalColumnIndexes.put(fieldName.substring("supplementalColumnIndex_".length()).trim().toUpperCase(), Integer.parseInt(fieldValue)-1);
            else if ("startYearColumnIndex".equals(fieldName))
                structure.startYearColumnIndex = Integer.parseInt(fieldValue)-1;
            else if ("expireYearColumnIndex".equals(fieldName))
                structure.expireYearColumnIndex = Integer.parseInt(fieldValue)-1;
        }

        return structure;
    }



    private void loadCodeListData(CodeSystem codeList, Structure csvStructure, BufferedReader in) throws Exception {
        Map<String,Code> loadedCodes = new HashMap<>();
        Map<String, Set<String>> codeParents = new HashMap<>();
        Collection<String> rootCodes = new LinkedList<>();
        CSVReader csvReader = new CSVReader(in,csvStructure.csvSeparator);

        int rowCount = 0;
        for (String[] row = csvReader.nextRow(); row!=null; row = csvReader.nextRow()) {
            if (row.length>0) {
                rowCount++;
                //Load code informations
                String code=null, parent=null;
                if (csvStructure.fileBasicStructure==CodeListFileStructure.table) {
                    for (int i : csvStructure.codeColumnIndexes)
                        if (row.length<=i)
                            throw new Exception ("Wrong structure for data row "+rowCount);
                        else if (row[i]!=null && !row[i].trim().equals("")) {
                            parent = code;
                            code = row[i];
                        } else
                            break;
                } else if (csvStructure.fileBasicStructure==CodeListFileStructure.tree) {
                    if (csvStructure.codeColumnIndexes.length!=2 || row.length<=csvStructure.codeColumnIndexes[0] || row.length<=csvStructure.codeColumnIndexes[1])
                        throw new Exception ("Wrong structure for data row "+rowCount);
                    else {
                        parent = row[csvStructure.codeColumnIndexes[0]];
                        code = row[csvStructure.codeColumnIndexes[1]];
                    }
                }
                if (code==null)
                    throw new Exception ("Wrong structure for data (row "+rowCount+")");

                //Create code object with labels
                Code codeObject = new Code(codeList.getSystem(), codeList.getVersion(), code);

                for (Map.Entry<String, Integer> labelIterator : csvStructure.titleColumnIndexes.entrySet()) {
                    String label = row.length>labelIterator.getValue() ? row[labelIterator.getValue()] : null;
                    if (label!=null && !label.trim().equals(""))
                        codeObject.addTitle(labelIterator.getKey(), label);
                }
                for (Map.Entry<String, Integer> labelIterator : csvStructure.descriptionColumnIndexes.entrySet()) {
                    String label = row.length>labelIterator.getValue() ? row[labelIterator.getValue()] : null;
                    if (label!=null && !label.trim().equals(""))
                        codeObject.addDescription(labelIterator.getKey(), label);
                }
                for (Map.Entry<String, Integer> labelIterator : csvStructure.supplementalColumnIndexes.entrySet()) {
                    String label = row.length>labelIterator.getValue() ? row[labelIterator.getValue()] : null;
                    if (label!=null && !label.trim().equals(""))
                        codeObject.addSupplemental(labelIterator.getKey(), label);
                }

                if (csvStructure.startYearColumnIndex!=null && row.length>csvStructure.startYearColumnIndex)
                    try { codeObject.setFromDate(DataUtils.fromStringToDate(row[csvStructure.startYearColumnIndex], null, null, true)); } catch (Exception ex) { throw new Exception ("Wrong value for start year (row "+rowCount+")"); }
                if (csvStructure.expireYearColumnIndex!=null && row.length>csvStructure.expireYearColumnIndex)
                    try { codeObject.setToDate(DataUtils.fromStringToDate(row[csvStructure.expireYearColumnIndex], null, null, true)); } catch (Exception ex) { throw new Exception ("Wrong value for expire year (row "+rowCount+")"); }

                //Store temporary code informations
                loadedCodes.put(code, codeObject);
                if (parent!=null && !parent.trim().equals("")) {
                    Set<String> childs = codeParents.get(parent);
                    if (childs==null)
                        codeParents.put(parent, childs = new HashSet<>());
                    childs.add(code);
                } else
                    rootCodes.add(code);
            }
        }

        //Append hierarchy
        for (String rootCode : rootCodes)
            codeList.addCode(loadedCodes.get(rootCode));
        for (Map.Entry<String,Set<String>> parentEntry : codeParents.entrySet()) {
            Code parentO = loadedCodes.get(parentEntry.getKey());
            for (String code : parentEntry.getValue())
                parentO.addChild(loadedCodes.get(code));
        }

        //Check orphan codes
        Collection<Code> orphanCodes = new LinkedList<>();
        for (Code codeO : loadedCodes.values())
            if (codeO.isRoot() && !rootCodes.contains(codeO.getCode()))
                orphanCodes.add(codeO);

        if (orphanCodes.size()>0) {
            StringBuilder errorMessageBuffer = new StringBuilder("There are errors in codelist import:");
            for (Code codeO : orphanCodes)
                errorMessageBuffer.append('\n').append(codeO.getCode());
            throw new Exception(errorMessageBuffer.toString());
        }
    }



}
