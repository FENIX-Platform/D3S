package org.fao.fenix.cl.services.rest;

import java.util.Collection;

import javax.ws.rs.Path;

import org.fao.fenix.cl.services.impl.BasicMergeImpl;
import org.fao.fenix.cl.services.impl.BasicMergeImpl.MergeType;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.server.tools.spring.SpringContext;

@Path("cl/merge")
public class MergeCL implements org.fao.fenix.cl.services.spi.MergeCL {

	@Override
	public CodeSystem getStandardMerge(Collection<CodeSystem> clList) throws Exception {
		return SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.standard, clList, true, false);
	}
	@Override
	public CodeSystem getInterceptionMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.interception, clList, true, false);
	}
	@Override
	public CodeSystem getUnionMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.union, clList, true, false);
	}

	//UPDATE
	@Override
	public CodeSystem updStandardMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.standard, clList, false, true);
	}
	@Override
	public CodeSystem updInterceptionMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.interception, clList, false, true);
	}
	@Override
	public CodeSystem updUnionMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.union, clList, false, true);
	}

}
