package org.fao.fenix.sdmx.d3s;

//import demo.sdmxsource.webservice.main.chapter1.Chapter1WritingStructures;
import org.fao.fenix.commons.msd.dto.data.Resource;
import org.fao.fenix.commons.msd.dto.full.Code;
import org.fao.fenix.commons.msd.dto.full.DSDCodelist;
import org.fao.fenix.commons.msd.dto.full.DSDColumn;
import org.fao.fenix.commons.msd.dto.full.DSDDataset;
import org.fao.fenix.commons.msd.dto.type.DataType;

import java.util.Collection;
import java.util.LinkedList;

public class FAOSTAT_QA_label {

    public static void main(String[] args) throws Exception {
        //Load dataset
        Resource<DSDDataset, Object[]> dataset = ResourceFactory.getDatasetInstance("FAOSTAT_QA_label");
        //TODO do something to translate to SDMX
      //   Chapter1WritingStructures test=new Chapter1WritingStructures();
      //  test.execution(dataset);
        System.out.println("All done...");
    }
}
