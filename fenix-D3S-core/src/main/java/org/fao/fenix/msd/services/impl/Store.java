package org.fao.fenix.msd.services.impl;

import java.util.Collection;

import org.fao.fenix.msd.dao.cl.CodeListLinkStore;
import org.fao.fenix.msd.dao.cl.CodeListStore;
import org.fao.fenix.msd.dao.common.CommonsStore;
import org.fao.fenix.msd.dao.dm.DMIndexStore;
import org.fao.fenix.msd.dao.dm.DMStore;
import org.fao.fenix.msd.dao.dsd.DSDStore;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.cl.CodeConversion;
import org.fao.fenix.msd.dto.cl.CodePropaedeutic;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.common.ContactIdentity;
import org.fao.fenix.msd.dto.common.Publication;
import org.fao.fenix.msd.dto.dm.DM;
import org.fao.fenix.msd.dto.dm.DMMeta;
import org.fao.fenix.msd.dto.dsd.DSDColumn;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;
import org.fao.fenix.msd.dto.dsd.DSDDimension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Store {
	@Autowired
	private CommonsStore cmStoreDAO;
	@Autowired
	private DSDStore dsdStoreDAO;
    @Autowired
    private DMStore dmStoreDAO;
    @Autowired
    private DMIndexStore dmIndexStoreDAO;
	@Autowired
	private CodeListStore clStoreDAO;
	@Autowired
	private CodeListLinkStore clLinkStoreDAO;

	// STORE
	public String newContactIdentity(ContactIdentity contactIdentity) throws Exception {
		return cmStoreDAO.storeContactIdentity(contactIdentity);
	}

	public String newPublication(Publication publication) throws Exception {
		return cmStoreDAO.storePublication(publication);
	}

	public void newCodeList(CodeSystem cl) throws Exception {
		clStoreDAO.storeCodeList(cl);
	}

    public String newDatasetMetadata(DM dm) throws Exception {
        return dmStoreDAO.storeDatasetMetadata(dm);
    }

    public String newMetadataStructure(DMMeta mm) throws Exception {
        return dmStoreDAO.storeMetadataStructure(mm);
    }

    public void newKeyword(String keyword) throws Exception {
		clStoreDAO.storeKeyword(keyword);
	}

	public void newDimension(DSDDimension dimension) throws Exception {
		dsdStoreDAO.storeDimension(dimension);
	}

	public void newContextSystem(DSDContextSystem context) throws Exception {
		dsdStoreDAO.storeContext(context);
	}

	public void newRelationship(Collection<CodeRelationship> relations)
			throws Exception {
		clLinkStoreDAO.storeCodeRelationship(relations);
	}

	public void newRelationship(CodeRelationship relation) throws Exception {
		clLinkStoreDAO.storeCodeRelationship(relation);
	}

	public void newConversion(Collection<CodeConversion> conversions)
			throws Exception {
		clLinkStoreDAO.storeCodeConversion(conversions);
	}

	public void newConversion(CodeConversion conversion) throws Exception {
		clLinkStoreDAO.storeCodeConversion(conversion);
	}

	public void newPropaedeutic(Collection<CodePropaedeutic> propaedeutics)
			throws Exception {
		clLinkStoreDAO.storeCodePropaedeutic(propaedeutics);
	}

	public void newPropaedeutic(CodePropaedeutic propaedeutic) throws Exception {
		clLinkStoreDAO.storeCodePropaedeutic(propaedeutic);
	}

	// UPDATE
	public int updateContactIdentity(ContactIdentity contactIdentity, boolean append) throws Exception {
		return cmStoreDAO.updateContactIdentity(contactIdentity, append);
	}

	public int updateCodeList(CodeSystem cl, boolean append) throws Exception {
		return clStoreDAO.updateCodeList(cl, append);
	}

	public int updateCode(Code code) throws Exception {
		return clStoreDAO.updateCode(code);
	}

    public int updateDatasetMetadata(DM dm, boolean append) throws Exception {
        return dmStoreDAO.updateDatasetMetadata(dm, append);
    }

    public int updateMetadataStructure(DMMeta mm, boolean append) throws Exception {
        return dmStoreDAO.updateMetadataStructure(mm, append);
    }

	public int updateColumn(String datasetUID, DSDColumn column)
			throws Exception {
		return dsdStoreDAO.updateColumn(datasetUID, column);
	}

	public int updateDimension(DSDDimension dimension) throws Exception {
		return dsdStoreDAO.updateDimension(dimension);
	}

	public int updateConversion(CodeConversion conversion) throws Exception {
		return clLinkStoreDAO.updateCodeConversion(conversion);
	}

	public int addCategoriesToDataset(String datasetUID,
			Collection<Code> listOfCodes) throws Exception {
		return dmStoreDAO.addCategoriesToDataset(datasetUID, listOfCodes);
	}

    //INDEX
    public int codeListIndex(String system, String version) throws Exception {
        return system!=null && version!=null && system.trim().length()>0 && version.trim().length()>0 ? clStoreDAO.createHierarchyLinks(system, version) : clStoreDAO.createHierarchyLinks();
    }

    public int datasetIndex(String uid) throws Exception {
        if (uid!=null)
            return dmIndexStoreDAO.indexDatasetMetadata(uid);
        dmIndexStoreDAO.rebuildIndexes();
        return 1;
    }

}
