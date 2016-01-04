package com.peterdamen.persistance.relational;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import com.peterdamen.persistance.Persistable;
import com.peterdamen.persistance.PersistableField;
import com.peterdamen.persistance.PersistableFieldType;
import com.peterdamen.persistance.TransactionHandler;
import com.peterdamen.persistance.TransactionHandlerDelegate;
import com.peterdamen.persistance.util.Logger;

public class RelationalPersistanceHandlerDelegate implements TransactionHandlerDelegate {

	DBAction db = null;
	
	
	public void initialise(Properties parameters) {
		
		Properties connectionProperties = new Properties();
		connectionProperties.setProperty("user", parameters.getProperty("username"));
		connectionProperties.setProperty("password", parameters.getProperty("password"));
		
		try {
		
			db = new DBAction(parameters.getProperty("driver"), parameters.getProperty("url"), connectionProperties);
			db.connection.setAutoCommit(false);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}

	}

	
	public TransactionHandler getHandler() {
		
		return new RelationalPersistanceHandler(db);
	}


	public boolean confirmRelationExists(String name, Class<? extends Persistable> c) {
		
		try {
		
			ResultSet rs = db.getConnection().getMetaData().getTables(null, null, null, new String[] { "TABLE" });
		
			while (rs.next()) {
				
				String tableName = rs.getString(3);
				if (tableName.toLowerCase().equals(name.toLowerCase())) {
					return true;
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
			
		return false;
	}
	
	
	public void createRelation(String name, Class<? extends Persistable> c, PersistableField[] fields) {
		
		StringBuffer sql = new StringBuffer(2000);
		sql.append("CREATE TABLE ");
		sql.append(name.toLowerCase());
		sql.append(" ( \n");
		sql.append(" id TEXT ");
		for (PersistableField field : fields) {
			sql.append(", \n");
			sql.append(field.getFieldName().toLowerCase());
			sql.append(" ");
			sql.append(databaseTypeForType(field.getType()));
		}
		sql.append(" ) \n");
		
		Logger.debug("Table Generation SQL:\n" + sql.toString());
		
		try {
		
			db.executeUpdate(sql.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	
	public static String databaseTypeForType(PersistableFieldType type) {
		
		switch (type) {
			case STRING:
				return "TEXT";
			case  INTEGER:
				return "INT";
			case  LONG:
				return "INT";
			case NUMERIC:
				return "NUMERIC";
			case DATE:
				return "DATE";
			case UUID:
				return "UUID";
			case BOOLEAN:
				return "BOOLEAN";
			default:
				break;
		}
	
		return null;
	}


	public boolean confirmRelationSchemaCorrect(String name, Class<? extends Persistable> c, PersistableField[] fields) {
		
		try {
			
			ResultSet rs = db.getConnection().getMetaData().getTables(null, null, null, new String[] { "TABLE" });
		
			String table = null;
			
			while (rs.next()) {
				
				String tableName = rs.getString(3);
				if (tableName.toLowerCase().equals(name.toLowerCase())) {
					table = tableName;
					break;
				}
			}
		
			if (table==null) return false;
			
			rs = db.getConnection().getMetaData().getColumns(null, null, table, null);
						
			ArrayList<PersistableField> fieldList = new ArrayList<PersistableField>(Arrays.asList(fields));
			
			while (rs.next()) {
				
				String tableName = rs.getString(3);
				String columnName = rs.getString(4);
				if (!tableName.toLowerCase().equals(name.toLowerCase())) {
					continue;
				}
				
				for (PersistableField field : fields) {
					if (columnName.equalsIgnoreCase(field.getFieldName())) {
						fieldList.remove(field);
					}
				}
				
			}
			
			if (fieldList.isEmpty()) return true;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return false;
		
	}


	
	public void updateSchema(String name, Class<? extends Persistable> c, PersistableField[] fields) {
	
	try {
			
			ResultSet rs = db.getConnection().getMetaData().getTables(null, null, null, new String[] { "TABLE" });
		
			String table = null;
			
			while (rs.next()) {
				
				String tableName = rs.getString(3);
				if (tableName.toLowerCase().equals(name.toLowerCase())) {
					table = tableName;
					break;
				}
			}
		
			if (table==null) { 
				
				Logger.warn("Cannot find relation '" + name + "'");
				return;
			}
			
			rs = db.getConnection().getMetaData().getColumns(null, null, table, null);
						
			ArrayList<PersistableField> fieldList = new ArrayList<PersistableField>(Arrays.asList(fields));
			
			while (rs.next()) {
				
				String tableName = rs.getString(3);
				String columnName = rs.getString(4);
				if (!tableName.toLowerCase().equals(name.toLowerCase())) {
					continue;
				}
				
				for (PersistableField field : fields) {
					if (columnName.equalsIgnoreCase(field.getFieldName())) {
						fieldList.remove(field);
					}
				}
				
			}
			
			if (fieldList.isEmpty()) {
				
				Logger.warn("Relation '" + name + "' already correct");
				return;
			}
			
		    for (PersistableField field : fieldList) {
		    	StringBuffer sql = new StringBuffer(1000);
		    	sql.append("ALTER TABLE ");
		    	sql.append(name);
		    	sql.append(" ADD COLUMN ");
				sql.append(field.getFieldName().toLowerCase());
				sql.append(" ");
				sql.append(databaseTypeForType(field.getType()));
				Logger.debug("Schema update SQL: \n" + sql.toString());
				db.executeUpdate(sql.toString());
				Logger.warn("Added " + field.getFieldName() + " to relation '" + name + "'");
		    }
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
	}
	

}
