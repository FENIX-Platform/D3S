package org.fao.fenix.msd.dto.cl;


public class CodeLink {
	
	private Code fromCode;
	private Code toCode;
	
	public CodeLink() {}
	public CodeLink(Code fromCode, Code toCode) {
		this.fromCode = fromCode;
		this.toCode = toCode;
	}
	
	
//	@JsonManagedReference("linkPropaedeutic")
	public Code getFromCode() {
		return fromCode;
	}
//	@JsonManagedReference("linkPropaedeutic")
	public void setFromCode(Code fromCode) {
		this.fromCode = fromCode;
	}
//	@JsonManagedReference("linkPropaedeutic")
	public Code getToCode() {
		return toCode;
	}
//	@JsonManagedReference("linkPropaedeutic")
	public void setToCode(Code toCode) {
		this.toCode = toCode;
	}

	
}
