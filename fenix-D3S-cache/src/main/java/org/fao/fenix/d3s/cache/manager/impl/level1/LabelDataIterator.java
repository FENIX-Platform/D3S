package org.fao.fenix.d3s.cache.manager.impl.level1;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.*;
import org.fao.fenix.commons.msd.dto.type.DataType;
import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.database.DataIterator;
import org.fao.fenix.commons.utils.database.Iterator;
import org.fao.fenix.d3s.cache.dto.dataset.Column;
import org.fao.fenix.d3s.cache.dto.dataset.Table;

import java.util.*;

public class LabelDataIterator implements Iterator<Object[]> {

    //INIT
    public LabelDataIterator(Iterator<Object[]> source, Table destinationStructure, DSDDataset sourceStructure, Collection<Resource<DSDCodelist,Code>> codelists) {
        this.source = source!=null ? source : new Iterator<Object[]>() {
            @Override
            public void skip(int amount) {

            }

            @Override
            public int getIndex() {
                return 0;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public Object[] next() {
                return null;
            }

            @Override
            public void remove() {

            }
        };

        Collection<Column> columns = destinationStructure.getColumns();
        if (columns!=null && columns.size()>0) {
            //collect codelists codes map by coded column id
            Map<String, Map<String, Map<String,String>>> columnCodeLanguageLabelMap = new HashMap<>();
            for (DSDColumn column : sourceStructure.getColumns()) {
                Collection<Code> codeList = getCodeList(column,codelists);
                if (codeList!=null && codeList.size()>0)
                    columnCodeLanguageLabelMap.put(column.getId(), codesMap(codeList, new HashMap<String, Map<String, String>>()));
            }
            //Create label to code indexes reference map and labels maps
            labels = new Map[columns.size()];
            labelsCodeIndex = new Integer[columns.size()];
                //for each coded column
            for (Map.Entry<String, Map<String, Map<String,String>>> columnLabelsEntry : columnCodeLanguageLabelMap.entrySet()) {
                String codeColumnId = columnLabelsEntry.getKey();
                //Retrieve code column index
                int index = 0;
                Integer codeColumnIndex = null ;
                for (Column column : columns)
                    if (column.getName().equals(codeColumnId)) {
                        codeColumnIndex = index;
                        break;
                    } else
                        index++;
                //Assign info to correspondent label columns
                if (codeColumnIndex!=null) {
                    index = 0;
                    for (Column column : columns) {
                        String id = column.getName();
                        Language language = id.startsWith(codeColumnId + '_') && id.length() == codeColumnId.length() + 3 ? Language.getInstance(id.substring(id.length() - 2)) : null;
                        if (language!=null)
                            if ( (labels[index] = getLanguageLabels(columnLabelsEntry.getValue(), language)) != null )
                                labelsCodeIndex[index] = codeColumnIndex;
                        index++;
                    }
                }
            }
        }
    }


    //INTERNAL ATTRIBUTES
    private Iterator<Object[]> source;
    private Map<String,String>[] labels;
    private Integer[] labelsCodeIndex;


    //ITERATOR

    @Override
    public Object[] next() {
        if (labels!=null) {
            Object[] row = Arrays.copyOf(source.next(), labels.length);
            for (int i=0; i<row.length; i++)
                if (labels[i]!=null)
                    row[i] = labels[i].get(row[labelsCodeIndex[i]]);
            return row;
        } else
            return source.next();
    }


    @Override
    public void skip(int amount) {
        source.skip(amount);
    }

    @Override
    public int getIndex() {
        return source.getIndex();
    }

    @Override
    public boolean hasNext() {
        return source.hasNext();
    }

    @Override
    public void remove() {
        source.remove();
    }




    //UTILS

    private Map<String,String> getLanguageLabels(Map<String, Map<String,String>> labels, Language language) {
        Map<String,String> languageLabels = new HashMap<>();
        if (labels!=null && labels.size()>0 && language!=null) {
            String languageCode = language.getCode();
            for (Map.Entry<String, Map<String,String>> labelEntry : labels.entrySet()) {
                String label = labelEntry.getValue().get(languageCode);
                if (label!=null)
                    languageLabels.put(labelEntry.getKey(), label);
            }
        }
        return languageLabels.size()>0 ? languageLabels : null;
    }

    private Collection<Code> getCodeList(DSDColumn column, Collection<Resource<DSDCodelist,Code>> codelists) {
        if (column!=null) {
            DSDDomain domain = column.getDomain();
            Collection<OjCodeList> declaredCodeLists = domain != null ? domain.getCodes() : null;
            OjCodeList declaredCodelist = declaredCodeLists != null && declaredCodeLists.size() > 0 ? declaredCodeLists.iterator().next() : null;
            if (declaredCodelist!=null)
                if (column.getDataType() == DataType.code && codelists != null && codelists.size() > 0) {
                    String uid = declaredCodelist.getIdCodeList();
                    String version = declaredCodelist.getVersion();

                    if (uid != null)
                        for (Resource<DSDCodelist, Code> codelist : codelists) {
                            MeIdentification<DSDCodelist> metadata = codelist.getMetadata();
                            if (metadata != null && uid.equals(metadata.getUid()) && (version == null && metadata.getVersion() == null || version != null && version.equals(metadata.getVersion())))
                                return codelist.getData();
                        }
                } else if (column.getDataType() == DataType.customCode) {
                    Collection<OjCode> customCodes = declaredCodelist.getCodes();
                    if (customCodes!=null && customCodes.size()>0) {
                        Collection<Code> codes = new LinkedList<>();
                        for (OjCode customCode : customCodes)
                            codes.add(new Code(customCode.getCode(), customCode.getLabel()));
                        return codes;
                    }
                }
        }

        return null;
    }


    private Map<String, Map<String,String>> codesMap(Collection<Code> codes, Map<String, Map<String,String>> labels) {
        if (codes!=null)
            for (Code code : codes) {
                codesMap(code.getChildren(), labels);
                labels.put(code.getCode(), code.getTitle());
            }
        return labels;
    }

}
