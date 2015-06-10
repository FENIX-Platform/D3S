package org.fao.fenix.d3s.mdsd;

import com.google.gson.Gson;
import junit.framework.TestCase;
import org.fao.fenix.commons.utils.JSONUtils;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class MDSDGeneratorTest extends TestCase {

    public void testGenerate() {
        MDSDGenerator g = new MDSDGenerator();
        String json  = g.generate();
        System.out.println(json);
        assertTrue(isValid(json));
    }

    private boolean isValid(String json) {
        Gson g = new Gson();
        try {
            g.fromJson(json, Object.class);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}