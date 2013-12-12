package org.fao.fenix.msd.dto.cl;

import org.fao.fenix.msd.dto.cl.type.CodeRelationshipType;

public class CodeRelationship extends CodeLink {

	private CodeRelationshipType type;

	public CodeRelationship() { }
	public CodeRelationship(Code from, Code to) { super(from, to); }
	public CodeRelationship(Code from, Code to, CodeRelationshipType type) {
		this(from,to);
		this.type = type;
	}

	public CodeRelationshipType getType() {
		return type;
	}
	public void setType(CodeRelationshipType type) {
		this.type = type;
	}
	
	

}
