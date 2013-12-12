package org.fao.fenix.msd.dto.dsd;


public class DSDContextSystem implements Comparable<DSDContextSystem> {
	
	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	//Compare
	@Override public boolean equals(Object obj) { return obj!=null && obj instanceof DSDContextSystem && (name==((DSDContextSystem)obj).name || name!=null && name.equals(((DSDContextSystem)obj).name)); }
	@Override public int hashCode() { return name==null ? -1 : name.hashCode(); }
	@Override
	public int compareTo(DSDContextSystem obj) {
		if (equals(obj))
			return 0;
		else
			return name==null ? -1 : name.compareTo(obj.name);
	}
	

}
