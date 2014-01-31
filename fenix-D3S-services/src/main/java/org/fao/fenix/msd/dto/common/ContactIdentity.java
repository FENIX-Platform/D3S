package org.fao.fenix.msd.dto.common;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.fao.fenix.msd.dto.cl.Code;
import org.fao.fenix.msd.dto.dsd.DSDContextSystem;

public class ContactIdentity {
	
	private String id;
	private String institution;
	private String department;
	private String name;
	private String surname;
	private Map<String,String> title;
	private Map<String,String> description;
	private Map<String,String> supplemental;
	private Code region;
	private Code role;
    private DSDContextSystem context;
	
	private Collection<Contact> contactList;
	
	public ContactIdentity() { }
	public ContactIdentity(String id) {
		this.id = id;
	}
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getInstitution() {
		return institution;
	}
	public void setInstitution(String institution) {
		this.institution = institution;
	}
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
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
	public Code getRegion() {
		return region;
	}
	public void setRegion(Code region) {
		this.region = region;
	}
	public Code getRole() {
		return role;
	}
	public void setRole(Code role) {
		this.role = role;
	}
	public Collection<Contact> getContactList() {
		return contactList;
	}
	public void setContactList(Collection<Contact> contactList) {
		this.contactList = contactList;
	}
    public DSDContextSystem getContext() {
        return context;
    }
    public void setContext(DSDContextSystem context) {
        this.context = context;
    }

    //utils
	public boolean hasTextKey() { return institution!=null || department!=null || name!=null || surname!=null; }
	@JsonIgnore
	public String getTextKey() { return getTextKey(institution, department, name, surname); }
	public static String getTextKey(String institution, String department, String name, String surname) {
		StringBuilder textKey = new StringBuilder();
		if (institution!=null)
			textKey.append(institution).append(' ');
		if (department!=null)
			textKey.append(department).append(' ');
		if (name!=null)
			textKey.append(name).append(' ');
		if (surname!=null)
			textKey.append(surname);
		return textKey.toString();
	}
	public void addTitle (String language, String title) {
        if (this.title==null)
            this.title = new HashMap<String, String>();
		this.title.put(language, title);
	}
	public void addDescription (String language, String description) {
        if (this.description==null)
            this.description = new HashMap<String, String>();
		this.description.put(language, description);
	}
	public void addSupplemental (String language, String supplemental) {
        if (this.supplemental==null)
            this.supplemental = new HashMap<String, String>();
		this.supplemental.put(language, supplemental);
	}
	public void addContact (Contact contact) {
        if (contactList==null)
            contactList = new LinkedList<Contact>();
		contactList.add(contact);
	}


    @Override
    public String toString() {
        return getTextKey();
    }
}
