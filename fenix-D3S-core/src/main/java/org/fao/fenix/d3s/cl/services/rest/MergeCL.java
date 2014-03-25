package org.fao.fenix.d3s.cl.services.rest;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.fao.fenix.commons.msd.dto.cl.CodeSystem;
import org.fao.fenix.d3s.cl.services.impl.BasicMergeImpl;
import org.fao.fenix.d3s.server.tools.spring.SpringContext;

@Path("cl/merge")
public class MergeCL implements org.fao.fenix.d3s.cl.services.spi.MergeCL {
    @Context HttpServletRequest request;

    @Override
	public CodeSystem getStandardMerge(Collection<CodeSystem> clList) throws Exception {
		return SpringContext.getBean(BasicMergeImpl.class).merge(BasicMergeImpl.MergeType.standard, clList, true, false);
	}
	@Override
	public CodeSystem getInterceptionMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(BasicMergeImpl.MergeType.interception, clList, true, false);
	}
	@Override
	public CodeSystem getUnionMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(BasicMergeImpl.MergeType.union, clList, true, false);
	}

	//UPDATE
	@Override
	public CodeSystem updStandardMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(BasicMergeImpl.MergeType.standard, clList, false, true);
	}
	@Override
	public CodeSystem updInterceptionMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(BasicMergeImpl.MergeType.interception, clList, false, true);
	}
	@Override
	public CodeSystem updUnionMerge(Collection<CodeSystem> clList) throws Exception {
        return SpringContext.getBean(BasicMergeImpl.class).merge(BasicMergeImpl.MergeType.union, clList, false, true);
	}

}
