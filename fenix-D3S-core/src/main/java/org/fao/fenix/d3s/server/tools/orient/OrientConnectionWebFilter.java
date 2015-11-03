package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.fao.fenix.commons.utils.Language;
import org.fao.fenix.commons.utils.Order;
import org.fao.fenix.commons.utils.Page;
import org.fao.fenix.d3s.server.dto.DatabaseStandards;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

@WebFilter(filterName="OrientConnectionManager", urlPatterns={"/*"})
public class OrientConnectionWebFilter implements Filter {
    @Inject OrientServer client;
    @Inject
    DatabaseStandards dbParameters;


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
            Language[] languages = getLanguages(servletRequest);
            dbParameters.setRequest((HttpServletRequest)servletRequest);
            dbParameters.setConnection(connection);
            dbParameters.setOrderingInfo(new Order(servletRequest,languages));
            dbParameters.setPaginationInfo(new Page(servletRequest));
            dbParameters.setLanguageInfo(languages);

            filterChain.doFilter(servletRequest,servletResponse);
        } finally {
            if (connection!=null)
                connection.close();
        }
    }


    //Utils
    private Language[] getLanguages(ServletRequest request) {
        Collection<Language> languages = new LinkedList<>();
        String language = request.getParameter("language");
        if (language!=null && language.length()>0) {
            for (String languageCode : language.split(",")) {
                Language l = Language.getInstance(languageCode.trim().toUpperCase());
                if (l!=null)
                    languages.add(l);
            }
        }
        return languages.size()>0 ? languages.toArray(new Language[languages.size()]) : null;
    }

}
