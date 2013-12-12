package org.fao.fenix.cl.merge.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.fao.fenix.cl.merge.CodeSystemMerge;
import org.fao.fenix.msd.dao.cl.CodeListConverter;
import org.fao.fenix.msd.dao.cl.CodeListLinkStore;
import org.fao.fenix.msd.dao.cl.CodeListLoad;
import org.fao.fenix.msd.dto.cl.CodeRelationship;
import org.fao.fenix.msd.dto.cl.CodeSystem;
import org.fao.fenix.msd.dto.cl.type.CodeRelationshipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.orientechnologies.orient.core.db.graph.OGraphDatabase;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;

@Component
public class CodeListsUnion extends CodeSystemMerge {
	
	private final static char SEPARATOR = '|';
	
	@Autowired private CodeListConverter clConverter;
	@Autowired private CodeListLoad clDao;
	@Autowired private CodeListLinkStore clLinkDao;

	@Override
	protected String getMergeKeyPrefix() { return "unionMerge"; }

	@Override
	@SuppressWarnings("unchecked")
	protected CodeSystem createMerge(OGraphDatabase database, Collection<ODocument> csList) throws Exception {
		if (csList==null || csList.size()<2)
			return null;
		
		//Try to load from virtual code lists
		String mergedSystem = "";
		String mergedVersion = "";
		String mergedAbstract = "Generated code list as union of:";
		Set<ODocument> keywords = new HashSet<ODocument>();
		for (ODocument cs : csList) {
			mergedSystem += cs.field("system");
			mergedVersion += cs.field("version");
			mergedAbstract += " " + cs.field("system") + "_" + cs.field("version");
			keywords.addAll((Collection<ODocument>)cs.field("keywords"));
		}
		ODocument merged = clDao.loadSystemO(mergedSystem, mergedVersion, database);
		//If merge doesn't exists I have to create and save it
		if (merged==null) {
			//Create the code list metadata
			merged = database.createVertex("CSVersion");
			merged.field("system", mergedSystem);
			merged.field("systemKey", mergedSystem);
			merged.field("iTitle", "Generated system");
			merged.field("version", mergedVersion);
			merged.field("startDate", new Date());
			merged.field("virtualDate", new Date());
			merged.field("iAbstract", mergedAbstract);
			merged.field("sharingPolicy", "public");
			merged.field("keywords", keywords, OType.LINKLIST);
//			if (cl.getSource()!=null)
//				csmain.field("source", storeSource(cl.getSource(), database));
//			if (cl.getProvider()!=null)
//				csmain.field("provider", storeProvider(cl.getProvider(), database));
			
			Map<ODocument,ODocument> originalToCopyCodes = new HashMap<ODocument,ODocument>();
			//Merge codes between one code list pair at time
			Iterator<ODocument> csListIterator = csList.iterator();
			ODocument masterO = csListIterator.next();
			Collection<ODocument> rootCodes = (Collection<ODocument>)masterO.field("rootCodes");
			while (csListIterator.hasNext())
				rootCodes = createRelatedCopy(rootCodes, csListIterator.next(), originalToCopyCodes);
			
			//Assign levels number
			int maxLevel = 0;
			for (ODocument rootCode : rootCodes)
				maxLevel = Math.max(maxLevel, getLevelsNumber(rootCode, 0));
			merged.field("levels", maxLevel);
			
			//Assign codes to the new code list
			for (ODocument rootCode : rootCodes)
				assignToCL(rootCode, merged);
			merged.field("rootCodes",rootCodes);
			
			//Save merged code list
			for (ODocument rootCode : rootCodes)
				saveCode(rootCode);
			for (ODocument rootCode : rootCodes)
				saveLinks(rootCode);
			merged.save();
			
			//Store one to one relations from original codes to copies and viceversa
			CodeRelationship relation = new CodeRelationship(null,null,CodeRelationshipType.oneToOne);
			for (Map.Entry<ODocument, ODocument> originalToCopyEntry : originalToCopyCodes.entrySet()) {
				clLinkDao.storeCodeRelationship(originalToCopyEntry.getKey(), originalToCopyEntry.getValue(), relation, database);
				clLinkDao.storeCodeRelationship(originalToCopyEntry.getValue(), originalToCopyEntry.getKey(), relation, database);
			}
		}
		
		return clConverter.toSystem(merged, true);
	}
	
	//Create merged codes copy
	//Include all connected master codes (every relation type between OO, OM and MO are valid)
	//Include all one to many related destination code list sub tree
	private Collection<ODocument> createRelatedCopy (Collection<ODocument> rootCodes, ODocument destinationCL, Map<ODocument,ODocument> copies) {
		//Create related codes copy
		Collection<ODocument> rootRelatedCodes = new ArrayList<ODocument>();
		Set<ODocument> innerRelations = new HashSet<ODocument>();
		for (ODocument code : rootCodes) {
			ODocument codeCopy = createRelatedCopy(code, destinationCL, copies, innerRelations);
			if (codeCopy!=null)
				rootRelatedCodes.add(codeCopy);
		}
		//update for ring edges
		for (ODocument edge : innerRelations)
			if (copies.containsKey((ODocument)edge.field("in")))
				edge.field("in",copies.get((ODocument)edge.field("in")));
		//Return copy
		return rootRelatedCodes;
	}
	
	@SuppressWarnings("unchecked")
	private ODocument createRelatedCopy (ODocument node, ODocument destinationCL, Map<ODocument,ODocument> copies, Set<ODocument> innerRelations) {
		Map<ODocument,CodeRelationshipType> destinationCLCodes = getRelatedCodes(node, destinationCL);
		if (destinationCLCodes.size()>0) {
			//Copy node
			ODocument nodeCopy;
			Collection<ODocument> childCodes = (Collection<ODocument>)node.field("childs");
			if (Boolean.TRUE.equals(node.field("copy"))) {
				nodeCopy = node;
			} else {
				nodeCopy = node.copy().field("copy", Boolean.TRUE);
				nodeCopy.field("in", new ArrayList<ODocument>());
				copies.put(node, nodeCopy); //async in field update for ring edges
				//Copy out edges
				Collection<ODocument> links = (Collection<ODocument>)node.field("out");
				Collection<ODocument> linksCopy = new ArrayList<ODocument>();
				if (links!=null)
					for (ODocument link : links) {
						ODocument linkCopy = link.copy().field("out",nodeCopy);
						linksCopy.add(link);
						if (node.field("system").equals(link.field("in.system"))) //async in field update for ring edges 
							innerRelations.add(linkCopy);
					}
				nodeCopy.field("out",linksCopy);
				//Copy child codes
				if (childCodes!=null) {
					Collection<ODocument> childCodesCopy = new ArrayList<ODocument>();
					for (ODocument childCode : childCodes) {
						ODocument childCodeCopy = createRelatedCopy(childCode, destinationCL, copies, innerRelations);
						if (childCodeCopy!=null)
							childCodesCopy.add(childCodeCopy);
					}
					childCodes = childCodesCopy;
				} else {
					childCodes = new ArrayList<ODocument>();
				}
			}
			//Include one to many related destination codelist codes
			for (Map.Entry<ODocument, CodeRelationshipType> destinationCodeEntry : destinationCLCodes.entrySet()) {
				if (destinationCodeEntry.getValue()==CodeRelationshipType.oneToMany) {
					//Include code as a child
					ODocument codeCopy = createSimpleCodeCopy(destinationCodeEntry.getKey(), copies, innerRelations);
					codeCopy.field("parent", nodeCopy);
					childCodes.add(codeCopy);
				}
			}
			//Refresh node childs value
			nodeCopy.field("childs",childCodes);
			//Return the copy
			return nodeCopy;
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private Map<ODocument,CodeRelationshipType> getRelatedCodes(ODocument code, ODocument destinationCL) {
		Map<ODocument,CodeRelationshipType> destinationCLCodes = new HashMap<ODocument, CodeRelationshipType>();
		Collection<ODocument> relations = (Collection<ODocument>)code.field("out");
		if (relations!=null)
			for (ODocument relation : relations)
				if (relation.getClassName().equals("CSRelationship") && destinationCL.equals(relation.field("in.system")))
					destinationCLCodes.put((ODocument)relation.field("in"),CodeRelationshipType.getByCode((String)relation.field("type")));
		return destinationCLCodes;
	}

	
	@SuppressWarnings("unchecked")
	private ODocument createSimpleCodeCopy(ODocument node, Map<ODocument,ODocument> copies, Set<ODocument> innerRelations) {
		//Copy node
		ODocument nodeCopy = node.copy().field("copy", Boolean.TRUE);
		nodeCopy.field("in", new ArrayList<ODocument>());
		copies.put(node, nodeCopy); //async in field update for ring edges
		//Copy out edges
		Collection<ODocument> links = (Collection<ODocument>)node.field("out");
		if (links!=null) {
			Collection<ODocument> linksCopy = new ArrayList<ODocument>();
			for (ODocument link : links) {
				ODocument linkCopy = link.copy().field("out",nodeCopy);
				linksCopy.add(link);
				if (node.field("system").equals(link.field("in.system"))) //async in field update for ring edges 
					innerRelations.add(linkCopy);
			}
			nodeCopy.field("out",linksCopy);
		}
		//Copy child codes
		Collection<ODocument> childCodes = (Collection<ODocument>)node.field("childs");
		if (childCodes!=null) {
			Collection<ODocument> childCodesCopy = new ArrayList<ODocument>();
			for (ODocument childCode : childCodes) {
				ODocument childCodeCopy = createSimpleCodeCopy(childCode, copies, innerRelations);
				if (childCodeCopy!=null)
					childCodesCopy.add(childCodeCopy);
			}
			childCodes = childCodesCopy;
		} else {
			childCodes = new ArrayList<ODocument>();
		}
		//Refresh node childs value
		nodeCopy.field("childs",childCodes);
		//Return the copy
		return nodeCopy;
	}
	
	@SuppressWarnings("unchecked")
	private void assignToCL (ODocument node, ODocument sourceCL) {
		node.field("code",((String)node.field("system.system"))+SEPARATOR+node.field("system.version")+SEPARATOR+node.field("code"));
		node.field("system",sourceCL);
		for (ODocument child : (Collection<ODocument>)node.field("childs"))
			assignToCL(child, sourceCL);
	}
	
	@SuppressWarnings("unchecked")
	private void saveCode(ODocument node) throws Exception {
		for (ODocument child : (Collection<ODocument>)node.field("childs"))
			saveCode(child);
		node.save();
	}
	
	@SuppressWarnings("unchecked")
	private void saveLinks(ODocument node) throws Exception {
		Collection<ODocument> links = (Collection<ODocument>)node.field("out");
		if (links!=null)
			for (ODocument link : links)
				link.save();
		for (ODocument child : (Collection<ODocument>)node.field("childs"))
			saveLinks(child);
	}
	
	@SuppressWarnings("unchecked")
	private int getLevelsNumber(ODocument node, int startLevel) {
		int maxLevel = 0;
		for (ODocument child : (Collection<ODocument>)node.field("childs"))
			maxLevel = Math.max(maxLevel, getLevelsNumber(child,startLevel+1));

		node.field("level",startLevel+1);
		return Math.max(maxLevel, startLevel+1);
	}
}
