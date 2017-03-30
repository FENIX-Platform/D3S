package org.fao.fenix.export.d3s.legacy;

import org.apache.log4j.Logger;
import org.fao.fenix.export.core.controller.GeneralController;
import org.fao.fenix.export.core.dto.CoreConfig;
import org.fao.fenix.export.core.dto.CoreOutputHeader;
import org.fao.fenix.export.core.utils.parser.JSONParser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

@WebServlet(urlPatterns = "/fenix/export")
public class LegacyExportServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LegacyExportServlet.class);

    private static Map<String, CoreOutputHeader> headers = new HashMap<>(1024);

    private static File tmpFolder;

    @Override
    public void init(ServletConfig config) throws ServletException {
        Properties properties = new Properties();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            InputStream input = classLoader.getResourceAsStream("tempfiles/tempfile.properties");
            if (input!=null) {
                properties.load(input);
                init(properties);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(Properties properties) throws IOException {
        init(properties.getProperty("export.tmp.folder"));
    }

    public void init(String folderPath) throws IOException {
        tmpFolder = new File(folderPath);
        if (!tmpFolder.exists())
            tmpFolder.mkdirs();

    }




    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (tmpFolder==null)
            throw new ServletException("Initialization error: no tmp folder");

        LOGGER.warn("after response header");

        CoreConfig config = null;
        try {
            config = JSONParser.createFenixCore(request.getInputStream());
        } catch (Exception e) {
            throw new ServletException("Configuration parsing exception.", e);
        }

        GeneralController core = null;
        try {
            core = new GeneralController(config);
        } catch (Exception e) {
            throw new ServletException("Initialization exception.", e);
        }

        File tmpFile = createTmpFile();
        try {
            core.write(new FileOutputStream(tmpFile));
            headers.put(tmpFile.getName(), core.getHeader());
            response.getWriter().print(createTmpFileURL(request,tmpFile));
        } catch (Exception e) {
            if (tmpFile.exists())
                tmpFile.delete();
            throw new ServletException("Output data producing error.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (tmpFolder==null)
            throw new ServletException("Initialization error: no tmp folder");

        //Create header
        CoreOutputHeader outputHeader = headers.get(request.getParameter("id"));

        if (outputHeader!=null) {
            File tmpFile = new File(tmpFolder,request.getParameter("id"));
            if (tmpFile.exists()) {
                response.setContentType(outputHeader.getType().getContentType());
                response.setHeader("Content-Disposition", "attachment; filename="+outputHeader.getName());
                response.setContentLength((int)tmpFile.length());
                writeFile(tmpFile, response.getOutputStream());
                tmpFile.delete();
                return;
            }
        }

        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private File createTmpFile() throws IOException {
        UUID fileUUID = UUID.randomUUID();
        return new File(tmpFolder,String.valueOf(fileUUID.getMostSignificantBits())+String.valueOf(fileUUID.getLeastSignificantBits()));
        //TODO Creare un MD5 sulla configurazione
    }

    private String createTmpFileURL(HttpServletRequest request, File tmpFile) {
        return request.getRequestURL().toString() + "?id=" + tmpFile.getName();
    }

    private void writeFile(File file, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        FileInputStream in = new FileInputStream(file);
        for (int c=in.read(buffer); c>0; c=in.read(buffer))
            out.write(buffer,0,c);
        out.close();
    }

}
