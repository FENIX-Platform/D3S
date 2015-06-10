package org.fao.fenix.d3s.mdsd;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.fao.fenix.commons.msd.dto.full.MeIdentification;

import java.lang.reflect.Type;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class MDSDGenerator {

    public String generate() {
        MeIdentification i = new MeIdentification();
        Type meIdentificationType = new TypeToken<MeIdentification>() {}.getType();
        Gson g = new GsonBuilder().registerTypeAdapter(MeIdentification.class, new D3SSerializer()).setPrettyPrinting().create();
        return g.toJson(i, meIdentificationType);
    }

}