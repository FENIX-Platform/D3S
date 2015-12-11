package org.fao.fenix.sdmx.d3s;

import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.type.DataType;

import demo.sdmxsource.webservice.main.finalPackage.ExportSDMX;
import java.io.OutputStream;

import java.util.Collection;
import java.util.LinkedList;

public class FAOSTAT_QA {

    public static void main(String[] args) throws Exception {
        //Load dataset
        Resource<DSDDataset, Object[]> dataset = ResourceFactory.getDatasetInstance("FAOSTAT_QA");
        //Load codelists
        Collection<Resource<DSDCodelist,Code>> codeLists = new LinkedList<>();
        for (DSDColumn column : dataset.getMetadata().getDsd().getColumns())
        {
            if (column.getDataType()== DataType.code){
                codeLists.add(ResourceFactory.getCodelistInstance(column.getDomain().getCodes().iterator().next().getIdCodeList()));
            }
        }
                //TODO do something to translate to SDMX
        ExportSDMX test=new ExportSDMX();
       test.execution(dataset,codeLists);
       //test.
     OutputStream Object1 = test.getDatafile();
      OutputStream  Object2=test.getStructurefile();
        System.out.println("All done...");
    }
}
