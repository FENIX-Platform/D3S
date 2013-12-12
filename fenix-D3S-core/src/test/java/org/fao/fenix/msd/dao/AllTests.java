package org.fao.fenix.msd.dao;

import org.fao.fenix.msd.dao.cl.CodeSystemLoadDaoTest;
import org.fao.fenix.msd.dao.cl.LinksLoadDaoTest;
import org.fao.fenix.msd.dao.commons.CommonsLoadDaoTest;
import org.fao.fenix.msd.dao.dm.DMLoadDaoTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ CodeSystemLoadDaoTest.class, DMLoadDaoTest.class, LinksLoadDaoTest.class, CommonsLoadDaoTest.class })
public class AllTests { }
