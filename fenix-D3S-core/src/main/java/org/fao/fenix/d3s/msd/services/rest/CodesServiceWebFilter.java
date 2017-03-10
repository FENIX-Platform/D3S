package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.commons.msd.dto.templates.codeList.Code;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.d3s.server.tools.orient.*;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName="CodesServiceParametersManager", urlPatterns={"/v2/msd/codes/engine"})
public class CodesServiceWebFilter implements Filter {
    @Inject OrientServer client;
    @Inject
    DatabaseStandards dbParameters;


    @Override public void init(FilterConfig filterConfig) throws ServletException { }
    @Override public void destroy() { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            Code.levelInfo.remove();
        }
    }



}
