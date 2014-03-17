package org.fao.fenix.d3s.backup.dao;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.fao.fenix.d3s.backup.dto.BackupUnit;
import org.fao.fenix.commons.utils.JSONUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;


@Component
@Scope("prototype")
public class BackupIO {

    private JsonGenerator out;
    private JsonParser in;

    public void setOut (OutputStream o) throws Exception {
        out = JSONUtils.createGenerator(new BufferedWriter(new OutputStreamWriter(o, Charset.forName("UTF-8")),1024));
    }
    public void setIn (InputStream i) throws Exception {
        in = JSONUtils.createParser(new BufferedReader(new InputStreamReader(i,Charset.forName("UTF-8")),1024));
    }


    public void write(Object data) throws Exception {
        JSONUtils.toJSON(new BackupUnit(data),out);
    }
    public void write(final Iterator<?> dataIterator) throws Exception {
        JSONUtils.toJSON(new Iterator<BackupUnit>() {
            @Override public boolean hasNext() { return dataIterator.hasNext(); }
            @Override public BackupUnit next() { return dataIterator.hasNext() ? new BackupUnit(dataIterator.next()) : null; }
            @Override public void remove() { dataIterator.remove(); }
        },out);
    }

    public void close() throws Exception {
        if (out!=null) {
            out.flush();
            out.close();
        }

        if (in!=null) {
            in.close();
        }
    }

    public Iterator<BackupUnit> read() throws Exception {
        try {
            return JSONUtils.toObject(in, BackupUnit.class);
        } catch (Exception e) {
            return null;
        }
    }

}
