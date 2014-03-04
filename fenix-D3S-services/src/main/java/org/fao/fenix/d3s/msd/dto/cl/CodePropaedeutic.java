package org.fao.fenix.d3s.msd.dto.cl;

import org.fao.fenix.d3s.msd.dto.dsd.DSDContextSystem;

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
