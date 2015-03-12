package org.fao.fenix.d3s.client;

import org.fao.fenix.commons.msd.dto.full.MeIdentification;
import org.fao.fenix.d3s.msd.services.spi.Resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * Created by meco on 12/03/15.
 */

@Consumes(value = MediaType.APPLICATION_JSON)
public class Test {

    public static void main(String[] args) throws Exception {
        new Test().doIt(args);
    }
    @Consumes(value = MediaType.APPLICATION_JSON)
    public void doIt(String[] args) throws Exception {
        D3SClient client = new D3SClient();
        client.initRest("http://localhost:7777/v2");

        Resources proxy = client.getProxy(Resources.class, "msd", "resources");
        MeIdentification metadata = (MeIdentification) proxy.getMetadataByUID("UAE_TRADE",true, true, true);

        System.out.println(metadata);

    }
}
