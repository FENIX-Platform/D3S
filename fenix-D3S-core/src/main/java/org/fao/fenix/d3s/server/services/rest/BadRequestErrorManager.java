package org.fao.fenix.d3s.server.services.rest;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import java.io.IOException;

//@WebFilter(filterName="BadRequestErrorManager", urlPatterns={"/v2/*"})
public class BadRequestErrorManager  implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        HttpServletResponse response = (HttpServletResponse)servletResponse;
        int status = response.getStatus();
        if (status==HttpServletResponse.SC_BAD_REQUEST)
            System.out.print("Bad request error on request: "+servletRequest);
    }

    @Override
    public void destroy() {

    }
}
