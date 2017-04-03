package org.fao.fenix.export.d3s.legacy.md.layout.utils;


public enum SpecialDateBean {
    to,
    from,
    creationDate;

    public static boolean isSpecialDateBean(String beanName) {

        if (beanName != null && !beanName.equals("")) {
            for (SpecialDateBean c : SpecialDateBean.values()) {
                if (c.name().equals(beanName)) {
                    return true;
                }
            }
        }
        return false;

    }

}
