package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName="OrientConnectionManager", urlPatterns={"/*"})
public class OrientConnectionWebFilter implements Filter {
    @Inject OrientServer client;
    @Inject DatabaseStandards dbParameters;


    @Override public void init(FilterConfig filterConfig) throws ServletException { }
    @Override public void destroy() { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        OGraphDatabase connection = null;
        try {
            dbParameters.setConnection(connection = client.getMsdDatabase());
        } catch (Exception ex) {
            throw new ServletException("Database connection error.", ex);
        }

        try {
            if (servletRequest.getParameter("perPage")!=null)
                dbParameters.setPaginationInfo(new Page(servletRequest));
            filterChain.doFilter(servletRequest,servletResponse);
        } finally {
            if (connection!=null)
                connection.close();
        }
    }



}
