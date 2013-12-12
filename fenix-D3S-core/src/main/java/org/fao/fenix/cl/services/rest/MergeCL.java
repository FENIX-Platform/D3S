package org.fao.fenix.cl.services.rest;

import java.util.Collection;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fao.fenix.cl.services.impl.BasicMergeImpl;
import org.fao.fenix.cl.services.impl.BasicMergeImpl.MergeType;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.server.tools.spring.SpringContext;

public class MergeCL implements org.fao.fenix.cl.services.spi.MergeCL {

	@Override
	public Response getStandardMerge(Collection<CodeSystem> clList) {
		try {
			CodeSystem merge = SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.standard, clList, true, false);
			return Response.ok(merge).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getInterceptionMerge(Collection<CodeSystem> clList) {
		try {
			CodeSystem merge = SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.interception, clList, true, false);
			return Response.ok(merge).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response getUnionMerge(Collection<CodeSystem> clList) {
		try {
			CodeSystem merge = SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.union, clList, true, false);
			return Response.ok(merge).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

	//UPDATE
	@Override
	public Response updStandardMerge(Collection<CodeSystem> clList) {
		try {
			CodeSystem merge = SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.standard, clList, false, true);
			return Response.ok(merge).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response updInterceptionMerge(Collection<CodeSystem> clList) {
		try {
			CodeSystem merge = SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.interception, clList, false, true);
			return Response.ok(merge).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
	@Override
	public Response updUnionMerge(Collection<CodeSystem> clList) {
		try {
			CodeSystem merge = SpringContext.getBean(BasicMergeImpl.class).merge(MergeType.union, clList, false, true);
			return Response.ok(merge).build();
		} catch (Exception e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}

}
