package org.fao.fenix.msd.dto.common;

import org.fao.fenix.msd.dto.common.type.ContactType;

public class Contact {
	
	private ContactType type;
	private String contact;
	
	public Contact() {}	
	public Contact(ContactType type, String contact) {
		this.type = type;
		this.contact = contact;
	}

	public ContactType getType() {
		return type;
	}
	public void setType(ContactType type) {
		this.type = type;
	}
	public String getContact() {
		return contact;
	}
	public void setContact(String contact) {
		this.contact = contact;
	}
	
}
