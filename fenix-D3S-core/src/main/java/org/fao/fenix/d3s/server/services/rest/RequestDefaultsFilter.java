package org.fao.fenix.d3s.server.services.rest;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;
import org.fao.fenix.d3s.server.tools.orient.OrientDatabase;
import org.fao.fenix.d3s.server.tools.orient.OrientServer;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.*;

@WebFilter(filterName="RequestDefaultsFilter", urlPatterns={"/*"})
public class RequestDefaultsFilter implements Filter {

    @Override public void init(FilterConfig filterConfig) throws ServletException { }
    @Override public void destroy() { }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(setRequestDefaults((HttpServletRequest) servletRequest),servletResponse);
    }

    private HttpServletRequest setRequestDefaults(HttpServletRequest request) {
        String original = request.getHeader("Accept");
        final String accept =
                original==null || original.trim().length()==0 || original.contains("*/*") ?
                "application/json" :
                original;

        return new HttpServletRequestWrapper(request) {
            @Override
            public Enumeration<String> getHeaders(String name) {
                if (name.equalsIgnoreCase("accept"))
                    return Collections.enumeration(Arrays.asList(getHeader(name)));

                return super.getHeaders(name);
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                Collection<String> names = Collections.list(super.getHeaderNames());
                if (!names.contains("accept"))
                    names.add("accept");
                return Collections.enumeration(names);
            }

            @Override
            public String getHeader(String name) {
                if (name.equalsIgnoreCase("accept"))
                    return accept;

                return super.getHeader(name);
            }
        };
    }
}
