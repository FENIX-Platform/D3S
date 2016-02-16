package org.fao.fenix.d3s.server.services.rest;

import org.apache.log4j.Logger;
import org.fao.fenix.commons.utils.JSONUtils;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

public class DefaultErrorPagesProxy extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(DefaultErrorPagesProxy.class);

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        Object entity = req.getAttribute("errorEntity");
        if (entity!=null)
            try {
                res.getWriter().print(JSONUtils.toJSON(entity));
            } catch (Exception e) {
                LOGGER.error("Error parsing error entity:",e);
            }
    }
}
