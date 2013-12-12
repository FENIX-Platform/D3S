package org.fao.fenix.cl.merge.impl.standard;

import java.util.Collection;
import java.util.LinkedList;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class CodeWrapper {
	
	private Collection<CodeWrapper> parents = new LinkedList<CodeWrapper>();
	private Collection<CodeWrapper> children = new LinkedList<CodeWrapper>();
	private ODocument code;
	
	
	public CodeWrapper() {}
	public CodeWrapper(Collection<CodeWrapper> parents, Collection<CodeWrapper> children, ODocument code) {
		this.parents = parents;
		this.children = children;
		this.code = code;
	}
	
	
	public Collection<CodeWrapper> getParents() {
		return parents;
	}
	public void setParents(Collection<CodeWrapper> parents) {
		this.parents = parents;
	}
	public Collection<CodeWrapper> getChildren() {
		return children;
	}
	public void setChildren(Collection<CodeWrapper> children) {
		this.children = children;
	}
	public ODocument getCode() {
		return code;
	}
	public void setCode(ODocument code) {
		this.code = code;
	}
	
	
	//Utils
	public void addParent(CodeWrapper parent) {
		if (parents==null)
			parents = new LinkedList<CodeWrapper>();
		parents.add(parent);
	}
	public void addChild(CodeWrapper child) {
		if (children==null)
			children = new LinkedList<CodeWrapper>();
		children.add(child);
	}
	public void addParent(ODocument parent) {
		CodeWrapper parentWrapper = new CodeWrapper();
		parentWrapper.setCode(parent);
		parentWrapper.addChild(this);
	}
	public void addChild(ODocument child) {
		CodeWrapper childWrapper = new CodeWrapper();
		childWrapper.setCode(child);
		childWrapper.addParent(this);
	}

}
