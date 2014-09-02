package org.fao.fenix.d3s.msd.services.rest;

import org.fao.fenix.commons.msd.dto.templates.ResponseBeanFactory;
import org.fao.fenix.commons.msd.dto.templates.standard.DSDColumnSubject;
import org.fao.fenix.d3s.msd.dao.*;
import org.fao.fenix.d3s.msd.services.spi.Subjects;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.Path;

@Path("msd/subjects")
public class SubjectsService implements Subjects {
    @Inject private Instance<ResourceDao> daoFactory;
    @Inject private SubjectDao dao;


    @Override
    public DSDColumnSubject getSubject(String rid) throws Exception {
        return ResponseBeanFactory.getInstance(dao.getSubject(rid), DSDColumnSubject.class);
    }

    @Override
    public DSDColumnSubject insertSubject(org.fao.fenix.commons.msd.dto.full.DSDColumnSubject subject) throws Exception {
        return ResponseBeanFactory.getInstance(dao.newCustomEntity(subject), DSDColumnSubject.class);
    }

    @Override
    public DSDColumnSubject updateSubject(org.fao.fenix.commons.msd.dto.full.DSDColumnSubject subject) throws Exception {
        return ResponseBeanFactory.getInstance(dao.saveCustomEntity(subject, true), DSDColumnSubject.class);
    }

    @Override
    public DSDColumnSubject appendSubject(org.fao.fenix.commons.msd.dto.full.DSDColumnSubject subject) throws Exception {
        return ResponseBeanFactory.getInstance(dao.saveCustomEntity(subject, false), DSDColumnSubject.class);
    }

}
