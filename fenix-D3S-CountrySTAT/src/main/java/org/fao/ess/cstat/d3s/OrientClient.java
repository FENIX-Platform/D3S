package org.fao.ess.cstat.d3s;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentPool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrientClient {
    private String url,usr,psw;

    public void init(String url, String usr, String psw) {
        this.url = url;
        this.usr = usr;
        this.psw = psw;
    }

    private ODatabaseDocumentPool dPool = ODatabaseDocumentPool.global(10,300);
    public ODatabaseDocumentTx getConnection() {
        return dPool.acquire(url,usr,psw);
    }

}
