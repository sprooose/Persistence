package com.peterdamen.persistance;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TransactionHandler {

	public void start();
	
	public void commit();
	
	public void write(Persistable p);
	
	public void delete(Persistable p);
	
	public <T extends Persistable> T  get(Class<? extends Persistable> c, UUID identifier);

	public <T extends Persistable> List<T> get(Class<? extends Persistable> c, Collection<UUID> identifiers);
	
	public <T extends Persistable> List<T> get(Class<? extends Persistable> c, String attribute, Object value);
	
	public <T extends Persistable> List<T> get(Class<? extends Persistable> c, PersistableFilter filter);
		
}
