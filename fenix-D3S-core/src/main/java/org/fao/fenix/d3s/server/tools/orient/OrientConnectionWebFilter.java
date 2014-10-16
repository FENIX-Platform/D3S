package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName="OrientConnectionManager", urlPatterns={"/v2/*"})
public class OrientConnectionWebFilter implements Filter {
    @Inject OrientServer client;
    @Inject DatabaseStandards dbParameters;


    @Override public void init(FilterConfig filterConfig) throws ServletException { }
    @Override public void destroy() { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        OObjectDatabaseTx connection = null;
        try {
            connection = client.getODatabase(OrientDatabase.msd);
        } catch (Exception ex) {
            throw new ServletException("Database connection error.", ex);
        }

        try {
            dbParameters.setRequest((HttpServletRequest)servletRequest);
            dbParameters.setConnection(connection);
            dbParameters.setOrderingInfo(new Order(servletRequest));
            dbParameters.setPaginationInfo(new Page(servletRequest));

            filterChain.doFilter(servletRequest,servletResponse);
        } finally {
            if (connection!=null)
                connection.close();
        }
    }



}
