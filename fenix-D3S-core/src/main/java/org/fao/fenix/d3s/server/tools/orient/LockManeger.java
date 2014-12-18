package org.fao.fenix.d3s.server.tools.orient;

import com.orientechnologies.orient.core.hook.ORecordHook;
import com.orientechnologies.orient.core.record.ORecord;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet (urlPatterns = "/lock")
public class LockManeger extends HttpServlet implements ORecordHook {
    private static boolean lock = false;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String lockParameter = req.getParameter("lock");
        if (lockParameter!=null)
            if ("true".equalsIgnoreCase(lockParameter.trim()))
                lock = true;
            else if ("false".equalsIgnoreCase(lockParameter.trim()))
                lock = false;

        PrintWriter out = resp.getWriter();
        out.print(lock);
        out.close();
    }

    @Override
    public void onUnregister() {

    }

    @Override
    public RESULT onTrigger(TYPE type, ORecord oRecord) {
        return lock && (type==TYPE.BEFORE_CREATE || type==TYPE.BEFORE_DELETE || type==TYPE.BEFORE_UPDATE) ? RESULT.SKIP : RESULT.RECORD_NOT_CHANGED;
    }

    @Override
    public DISTRIBUTED_EXECUTION_MODE getDistributedExecutionMode() {
        return DISTRIBUTED_EXECUTION_MODE.SOURCE_NODE;
    }
}
