package com.peterdamen.persistance;

import java.util.UUID;

public class Persistable {

	private UUID id;
	
	public UUID getId() {
		return id;
	}
	
	public void setId(UUID id) {
		this.id = id;
	}
	
	public final void persist() {
		
		PersistanceTransaction p = PersistanceTransaction.getCurrent();
		if (isModified()) p.getTransactionHandler().write(this);
		
	}
	
	public boolean isModified() {
		
		return true;
		
	}
	
	public final void delete() {
		
		PersistanceTransaction p = PersistanceTransaction.getCurrent();
		p.getTransactionHandler().delete(this);
		
	}
	
}
