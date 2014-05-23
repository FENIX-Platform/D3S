package org.fao.fenix.d3s.server.tools.rest;

import org.fao.fenix.d3s.server.init.MainController;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/shutdown")
public class Shutdown extends HttpServlet {
    @Inject MainController mainController;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
            out.println("Shutdown in progress...");
            out.flush();
            mainController.shutdown();
            out.println("Done");
        } catch (Exception e) {
            out.println("Error: "+e.getMessage()+"\n\n");
            e.printStackTrace(out);
        }
        out.close();
    }
}
