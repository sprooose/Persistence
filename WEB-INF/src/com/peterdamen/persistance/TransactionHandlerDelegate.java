package com.peterdamen.persistance;

import java.util.Properties;

public interface TransactionHandlerDelegate {

	public void initialise(Properties parameters);
	
	public TransactionHandler getHandler();
	
	public boolean confirmRelationExists(String name, Class<? extends Persistable> c);
	
	public void createRelation(String name, Class<? extends Persistable> c, PersistableField[] fields);
	
	public boolean confirmRelationSchemaCorrect(String name, Class<? extends Persistable> c, PersistableField[] fields);
	
	public void updateSchema(String name, Class<? extends Persistable> c, PersistableField[] fields);
}
