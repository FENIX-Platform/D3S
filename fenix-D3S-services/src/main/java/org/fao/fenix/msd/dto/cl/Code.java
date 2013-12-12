package org.fao.fenix.msd.dto.cl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.fao.fenix.msd.dto.common.ValueOperator;

public class Code implements Comparable<Code> {
	
	//Attributes
	private String code;
	private CodeSystem system;
	private Integer level;

	private Map<String,String> title;
	private Map<String,String> description;
	private Map<String,String> supplemental;
	
	private Collection<ValueOperator> aggregationRules;

	//Tree connections
	@JsonIgnore
	public Collection<Code> parents;
	private Collection<Code> childs;
	
	//Matching rules
	private Collection<CodeRelationship> relations;
	private Collection<CodeConversion> conversions;
	private Collection<CodePropaedeutic> propaedeutics;

	//Code other management
	private Collection<Code> exclusionList = null; //null means standard code

	
	public Code() {}
	public Code(String composedCode) {
        String[] codeParts = getDecomposedCode(composedCode);
        init(codeParts[0],codeParts[1],codeParts[2]);
	}
	public Code(String system, String version, String code) {
        init(system,version,code);
	}
	public Code(CodeSystem system, String code) {
        this.code = code;
        setSystem(system);
	}

    private void init(String system, String version, String code) {
        this.code = code;
        setSystem(new CodeSystem(system, version));
    }


	//GET-SET
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@JsonBackReference("system") public CodeSystem getSystem() {
		return system;
	}
	@JsonBackReference("system") public void setSystem(CodeSystem system) {
		if ((this.system = system)!=null) {
			systemKey = system.getSystem();
			systemVersion = system.getVersion();
		}
		if (!isLeaf())
			for (Code child : childs)
				child.setSystem(system);
	}
	public Collection<Code> getExclusionList() {
		return exclusionList;
	}
	public void setExclusionList(Collection<Code> exclusionList) {
		this.exclusionList = exclusionList;
	}
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Map<String, String> getTitle() {
		return title;
	}
	public void setTitle(Map<String, String> title) {
		this.title = title;
	}
	public Map<String, String> getDescription() {
		return description;
	}
	public void setDescription(Map<String, String> description) {
		this.description = description;
	}
	public Map<String, String> getSupplemental() {
		return supplemental;
	}
	public void setSupplemental(Map<String, String> supplemental) {
		this.supplemental = supplemental;
	}
	public Collection<Code> getChilds() {
		return childs;
	}
	public void setChilds(Collection<Code> childs) {
		this.childs = childs;
	}
	@JsonIgnore
	public Collection<Code> getParents() {
		return parents;
	}
	@JsonIgnore
	public void setParents(Collection<Code> parents) {
		this.parents = parents;
	}
//	@JsonBackReference("linkRelationship")
	public Collection<CodeRelationship> getRelations() {
		return relations;
	}
//	@JsonBackReference("linkRelationship")
	public void setRelations(Collection<CodeRelationship> match) {
		this.relations = match;
	}
//	@JsonBackReference("linkConversion")
	public Collection<CodeConversion> getConversions() {
		return conversions;
	}
//	@JsonBackReference("linkConversion")
	public void setConversions(Collection<CodeConversion> conversions) {
		this.conversions = conversions;
	}
//	@JsonBackReference("linkPropaedeutic")
	public Collection<CodePropaedeutic> getPropaedeutics() {
		return propaedeutics;
	}
//	@JsonBackReference("linkPropaedeutic")
	public void setPropaedeutics(Collection<CodePropaedeutic> propaedeutics) {
		this.propaedeutics = propaedeutics;
	}
	public String getGlobalCode() {
		return systemKey+'|'+systemVersion+'|'+code;
	}
	public void setGlobalCode(String globalCode) {
		String[] codeElements = globalCode.split("|");
		if (codeElements.length==3) {
			setSystemKey(codeElements[0]);
			setSystemVersion(codeElements[1]);
			setCode(codeElements[2]);
		}
	}
	public Collection<ValueOperator> getAggregationRules() {
		return aggregationRules;
	}
	public void setAggregationRules(Collection<ValueOperator> aggregationRules) {
		this.aggregationRules = aggregationRules;
	}


	//JSON utils
	private String systemKey;
	private String systemVersion;
	
	public String getSystemKey() { return systemKey; }
	public void setSystemKey(String systemKey) { this.systemKey = systemKey; }
	
	public String getSystemVersion() { return systemVersion; }
	public void setSystemVersion(String systemVersion) { this.systemVersion = systemVersion; }
	
	
	//Utilities
	@JsonIgnore public static String[] getDecomposedCode(String compositCode) { return compositCode!=null ? compositCode.split("|") : null; }
	public void addTitle(String language, String title) {
        if (this.title==null)
            this.title = new HashMap<String, String>();
		this.title.put(language, title);
	}
	public void addDescription(String language, String description) {
        if (this.description==null)
            this.description = new HashMap<String, String>();
		this.description.put(language, description);
	}
	public void addSupplemental(String language, String supplemental) {
        if (this.supplemental==null)
            this.supplemental = new HashMap<String, String>();
		this.supplemental.put(language, supplemental);
	}
	public void addChild(Code child) {
        if (childs==null)
            childs = new LinkedList<Code>();
		childs.add(child);
		child.addParent(this);
		child.setSystem(system);
	}
	public void addParent(Code parent) {
		if (parents==null)
			parents = new LinkedList<Code>();
		parents.add(parent);
		if (parent.getLevel()!=null && (level==null || parent.getLevel()>=level))
			level = parent.getLevel()+1;
	}
	public void addExclusion(Code toExclude) {
		if (exclusionList==null)
			exclusionList = new LinkedList<Code>();
		exclusionList.add(toExclude);
	}
	public void addAggregationRule(ValueOperator aggregationRule) {
        if (aggregationRules==null)
            aggregationRules = new LinkedList<ValueOperator>();
		aggregationRules.add(aggregationRule);
	}
	public void addRelation(CodeRelationship rule) {
        if (relations==null)
            relations = new LinkedList<CodeRelationship>();
		relations.add(rule);
	}
	public void addConversion(CodeConversion conversion) {
        if (conversions==null)
            conversions = new LinkedList<CodeConversion>();
		conversions.add(conversion);
	}
	public void addPropaedeutic(CodePropaedeutic prop) {
        if (propaedeutics==null)
            propaedeutics = new LinkedList<CodePropaedeutic>();
		propaedeutics.add(prop);
	}
	@JsonIgnore public boolean isSemanticOther() { return exclusionList!=null && exclusionList.size()>0; }
	
	@JsonIgnore public boolean isLeaf() { return childs==null || childs.size()==0; }
	@JsonIgnore public boolean isRoot() { return parents==null || parents.size()==0; }
	public int countLevels() {
		int count=0;
		if (!isLeaf())
			for (Code child : childs)
				count = Math.max(count, child.countLevels());
		return count+1;
	}
	public Collection<Collection<Code>> findBranches() {
		return findBranches(true, new LinkedList<Collection<Code>>(), new LinkedList<Code>());
	}
	private Collection<Collection<Code>> findBranches(boolean isRoot, Collection<Collection<Code>> buffer, LinkedList<Code> branchBuffer) {
		branchBuffer.add(this);
		if (isLeaf())
			buffer.add(new LinkedList<Code>(branchBuffer));
		else
			for (Code child : childs)
				child.findBranches(false, buffer, branchBuffer);
		branchBuffer.removeLast();
		return buffer;
	}
    public void resetLevels(int currentLevel) {
        setLevel(currentLevel);
        if (!isLeaf())
            for(Code child : childs)
                child.resetLevels(currentLevel+1);
    }

	
	
	
	
	//Ordering
	@Override
	public int hashCode() {
		return getGlobalCode().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		return obj!=null && obj instanceof Code && getGlobalCode().equals(((Code)obj).getGlobalCode());
	}
	@Override
	public int compareTo(Code obj) {
		return getGlobalCode().compareTo(obj.getGlobalCode());
	}

	
	//utils
	@Override public String toString() { return getGlobalCode(); }
}
