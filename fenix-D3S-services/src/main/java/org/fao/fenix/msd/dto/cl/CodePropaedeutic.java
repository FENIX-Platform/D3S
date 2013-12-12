package org.fao.fenix.msd.dto.cl;

import org.fao.fenix.msd.dto.dsd.DSDContextSystem;

public class CodePropaedeutic extends CodeLink {

	private DSDContextSystem contextSystem;
	
	public CodePropaedeutic() { }
	public CodePropaedeutic(Code from, Code to) { super(from, to); }
	public CodePropaedeutic(Code from, Code to, DSDContextSystem contextSystem) {
		this(from,to);
		this.contextSystem = contextSystem;
	}

	public DSDContextSystem getContextSystem() {
		return contextSystem;
	}
	public void setContextSystem(DSDContextSystem contextSystem) {
		this.contextSystem = contextSystem;
	}
	
	
	
	

}
