package org.fao.fenix.d3s.mdsd;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:guido.barbaglia@fao.org">Guido Barbaglia</a>
 * @author <a href="mailto:guido.barbaglia@gmail.com">Guido Barbaglia</a>
 */
public class MDSDGeneratorTest extends TestCase {

    public void testGenerate() {
        MDSDGenerator g = new MDSDGenerator();
        System.out.println(g.generate());
    }

}