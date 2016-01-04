package com.peterdamen.persistance.relational;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.peterdamen.persistance.Persistable;
import com.peterdamen.persistance.PersistableField;
import com.peterdamen.persistance.PersistableFilter;
import com.peterdamen.persistance.PersistableRelation;
import com.peterdamen.persistance.PersistanceEngine;
import com.peterdamen.persistance.TransactionHandler;
import com.peterdamen.persistance.exception.PersistanceException;
import com.peterdamen.persistance.util.Logger;

public class RelationalPersistanceHandler implements TransactionHandler {

	private DBAction db = null;
	
	private static final Class<?>[] emptyParameterTypes = new Class<?>[0];
	
	private enum UpdateFunction {
		
		INSERT,
		DELETE,
		UPDATE,
		SELECT,
		SELECTMULTIPLE
		
	}
	
	private static HashMap<Class<? extends Persistable>, HashMap<UpdateFunction, String>> cachedSQLMap;
	private HashMap<Class<? extends Persistable>, HashMap<UpdateFunction, PreparedStatement>> cachedPreparedStatement;
	
	static {
		
		cachedSQLMap =  new HashMap<>();
		
	}
	
	
	private PreparedStatement getCachedStatement(Class<? extends Persistable> c, UpdateFunction updateFunction) {
		
		HashMap<UpdateFunction, PreparedStatement> statementMap = cachedPreparedStatement.get(c);
		if (statementMap==null) return null;
		return statementMap.get(updateFunction);	
		
	}
	
	private String getCachedSQL(Class<? extends Persistable> c, UpdateFunction updateFunction) {
		
		HashMap<UpdateFunction, String> statementMap = cachedSQLMap.get(c);
		if (statementMap==null) return null;
		return statementMap.get(updateFunction);	
		
	}
	
	private void saveCachedSQL(Class<? extends Persistable> c, UpdateFunction updateFunction, String sql) {
		
		HashMap<UpdateFunction, String> statementMap = cachedSQLMap.get(c);
		if (statementMap==null) statementMap = new HashMap<>();
		statementMap.put(updateFunction, sql);
		cachedSQLMap.put(c, statementMap);
		
	}
	
	private void saveCachedStatment(Class<? extends Persistable> c, UpdateFunction updateFunction, PreparedStatement statement) {
		
		HashMap<UpdateFunction, PreparedStatement> statementMap = cachedPreparedStatement.get(c);
		if (statementMap==null) statementMap = new HashMap<>();
		statementMap.put(updateFunction, statement);
		cachedPreparedStatement.put(c, statementMap);
		
	}
	
	
	public RelationalPersistanceHandler(DBAction db) {
		
		this.db = db;
		cachedPreparedStatement = new HashMap<>();
	}
	
	
	public void start() {
		
	}

	public void commit() {
	
		try { 
		
		db.connection.commit();
		
		} catch (Exception e) {
			
			e.printStackTrace();
			throw new PersistanceException("Failed to Commit", e);
			
		}
		
	}

	public void write(Persistable p) {
	
		 boolean inserting = false;
		 Class<? extends Persistable> c = p.getClass();
		
		 if (p.getId()==null) {
			 
			 inserting = true;
			 p.setId(UUID.randomUUID());
			 
		 }
		
		 Logger.debug("Write " + p.getClass().getName());
		 
		 PersistanceEngine pe = PersistanceEngine.getInstance();
		 PersistableRelation r = pe.getPersistableRelation(c);
		 
		 try {
		 
			 PreparedStatement ps =  null;
			 
			 if (inserting) {
			 	 
				 ps = getCachedStatement(c, UpdateFunction.INSERT);
				 
				 if (ps==null) {
					 
					 String cachedSQL = getCachedSQL(c, UpdateFunction.INSERT);
					
					 if (cachedSQL==null) {
						 
						 StringBuffer sb = new StringBuffer(3000);
						
						 sb.append(" INSERT INTO ");
						 sb.append(r.getRelationName());
						 
						 sb.append(" ( id");
						 for (PersistableField f : r.getFields()) {
							 sb.append(",");
							 sb.append( f.getFieldName() );
						 }
						 sb.append(")");
								 
						 sb.append(" VALUES ( ? ");
						 
						 for (int i=0; i < r.getFields().length; i++) {
							 sb.append(",?");
						 }
						 
						 sb.append(" ) ");
						 
						 cachedSQL = sb.toString();
						 
						 Logger.debug("Gerneated Insert Statment for Class " + c.getName() + ":\n " + cachedSQL );
						 
						 saveCachedSQL(c, UpdateFunction.INSERT, cachedSQL);
						 
					 }  
					
					 ps = db.connection.prepareStatement(cachedSQL);
						 
					 saveCachedStatment(c, UpdateFunction.INSERT, ps);
					 
				 } 
				 
				 
				 ps.setString(1, p.getId().toString());
				 
				 PersistableField f;
				 for (int i=0; i < r.getFields().length; i++) {
					 
					 f = r.getFields()[i];
					 Logger.debug("get" + f.getFieldName());
					 Object v = c.getMethod("get" + f.getFieldName(), emptyParameterTypes).invoke(p,(Object[])null);
					 
					 ps.setObject(i+2, v);
					 
				 }
			 
				 
				 Logger.debug("Write Insert with Prepared Statement: " + p.getId().toString());
				 
			 } else {
				 
				 ps = getCachedStatement(c, UpdateFunction.UPDATE);
				 
				 if (ps==null) {
					 
					 String cachedSQL = getCachedSQL(c, UpdateFunction.UPDATE);
					
					 if (cachedSQL==null) {
						 
						 StringBuffer sb = new StringBuffer(3000);
						
						 sb.append(" UPDATE ");
						 sb.append(r.getRelationName());
						 sb.append(" SET ");
						 
						 boolean first = true;
						 
						 for (PersistableField f : r.getFields()) {
							 
							 if (!first) sb.append(",");
							 first = false;
							 
							 Logger.debug("get" + f.getFieldName());
							 
							 sb.append( f.getFieldName() );
							 sb.append(" = ?");
							 
						 }
						 
						 sb.append("\n WHERE id = ? ");
						 
						 cachedSQL = sb.toString();
						 
						 Logger.debug("Gerneated Update Statment for Class " + c.getName() + ":\n " + cachedSQL );
						 
						 saveCachedSQL(c, UpdateFunction.UPDATE, cachedSQL);
						 
					 }  
					
					 ps = db.connection.prepareStatement(cachedSQL);
						 
					 saveCachedStatment(c, UpdateFunction.UPDATE, ps);
					 
				 } 
				 
				 
				 ps.setString(1, p.getId().toString());
				 
				 PersistableField f;
				 for (int i=0; i < r.getFields().length; i++) {
					 
					 f = r.getFields()[i];
					 Logger.debug("get" + f.getFieldName());
					 Object v = c.getMethod("get" + f.getFieldName(), emptyParameterTypes).invoke(p,(Object[])null);
					 
					 ps.setObject(i+1, v);
					 
				 }
				 
				 ps.setObject(r.getFields().length+1, p.getId().toString());
			 	 
				 Logger.debug("Write Update with Prepared Statement: " + p.getId().toString());
				 
			 }
			 	 
			ps.execute();
			 
			 
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
		 
	}

	public void delete(Persistable p) {
		
		 Class<? extends Persistable> c = p.getClass();
		
		 PersistanceEngine pe = PersistanceEngine.getInstance();
		 PersistableRelation r = pe.getPersistableRelation(p.getClass());
				 
		 try {
		 
			 PreparedStatement ps = getCachedStatement(c, UpdateFunction.DELETE);
			 
			 if (ps==null) {
				 
				 String cachedSQL = getCachedSQL(c, UpdateFunction.DELETE);
				
				 if (cachedSQL==null) {
					 
					 StringBuffer sb = new StringBuffer(1000);
					
					 sb.append(" DELETE FROM ");
					 sb.append(r.getRelationName());
					 sb.append(" WHERE id = ?");
					 
					 cachedSQL = sb.toString();
					 Logger.debug("Write (Delete) SQL: \n" + cachedSQL);
					 
					 saveCachedSQL(c, UpdateFunction.DELETE, cachedSQL);
					 
				 }  
				
				 ps = db.connection.prepareStatement(cachedSQL);
					 
				 saveCachedStatment(c, UpdateFunction.UPDATE, ps);
			
			 }
		 
			 ps.setString(1, p.getId().toString());
			 ps.execute();
		 	 
			 Logger.debug("Write Delete with Prepared Statement: " + p.getId().toString());
			 
		 } catch (Exception e) {
			 
			 e.printStackTrace();
			 
		 }
				
		
		 
	}

	public <T extends Persistable> T get(Class<? extends Persistable> c, UUID identifier) {
		
		Logger.debug("Get " + c.getClass().getName() + " " + identifier.toString());
		
		PersistanceEngine pe = PersistanceEngine.getInstance();
		PersistableRelation r = pe.getPersistableRelation(c);
		 
		 try {
		 
			 PreparedStatement ps =  null;
			 	 
				 ps = getCachedStatement(c, UpdateFunction.SELECT);
				 
				 if (ps==null) {
					 
					 String cachedSQL = getCachedSQL(c, UpdateFunction.SELECT);
					
					 if (cachedSQL==null) {
						 
						 StringBuffer sb = new StringBuffer(3000);
						
						 sb.append(" SELECT ");
						 sb.append(" id ");
						 for (PersistableField f : r.getFields()) {
							 sb.append(",");
							 sb.append( f.getFieldName() );
						 }
						 sb.append(" FROM ");
						 sb.append(r.getRelationName());
								 
						 sb.append(" WHERE id = ? ");
						 
						 cachedSQL = sb.toString();
						 
						 Logger.debug("Gerneated Select Statment for Class " + c.getName() + ":\n " + cachedSQL );
						 
						 saveCachedSQL(c, UpdateFunction.SELECT, cachedSQL);
						 
					 }  
					
					 ps = db.connection.prepareStatement(cachedSQL);
						 
					 saveCachedStatment(c, UpdateFunction.SELECT, ps);
					 
				 } 
			
				 ps.setString(1, identifier.toString());
				 
				 ResultSet rs = ps.executeQuery();
				 
				 T o = null;
				 
				 if (rs.next()) {
					 
					 o = getObject(c, r, rs);
					 
				 }
				 
				 return o;
				 
		 } catch (Exception e) {
				 
			 e.printStackTrace();
		
		 }
				
		 return null;
	}

	private <T extends Persistable> T getObject(Class<? extends Persistable> c, PersistableRelation r, ResultSet rs) throws Exception {
		
		 T p = (T) c.newInstance();
		
		 PersistableField f;
		 
		 UUID id = UUID.fromString(rs.getString(1));
		 p.setId(id);
		 
		 for (int i=0; i < r.getFields().length; i++) {
			 
			 f = r.getFields()[i];
			 Method setter = r.getSetters().get(f.getFieldName());
			 Logger.debug("set" + f.getFieldName());
			 setter.invoke(p, new Object[] {  rs.getObject(i + 2)} );
			 
		 }
		 
		 return p;
	}
	
	
	
	public <T extends Persistable> List<T> get(Class<? extends Persistable> c, Collection<UUID> identifiers) {
		
		 ArrayList<T> results = new ArrayList<>();
		
		 if (identifiers==null || identifiers.isEmpty()) return results;
		 
		 Logger.debug("Get Multiple " + c.getClass().getName() + " (" + identifiers.size() + ")");
		 
		 PersistanceEngine pe = PersistanceEngine.getInstance();
		 PersistableRelation r = pe.getPersistableRelation(c);
		  
		 try {
			 
			 String cachedSQL = getCachedSQL(c, UpdateFunction.SELECTMULTIPLE);
				
			 if (cachedSQL==null) {
				 
				 StringBuffer sb = new StringBuffer(3000);
				
				 sb.append(" SELECT ");
				 sb.append(" id ");
				 for (PersistableField f : r.getFields()) {
					 sb.append(",");
					 sb.append( f.getFieldName() );
				 }
				 sb.append(" FROM ");
				 sb.append(r.getRelationName());
				 
				 cachedSQL = sb.toString();
				 
				 Logger.debug("Gerneated Select Multiple Statment for Class " + c.getName() + ":\n " + cachedSQL );
				 
				 saveCachedSQL(c, UpdateFunction.SELECTMULTIPLE, cachedSQL);
				 
			 }
			  
			 StringBuffer sb = new StringBuffer(4000);
			 sb.append(cachedSQL);
			 sb.append(" \nWHERE id IN ( '' ");
			 
			 for (UUID identifier : identifiers) {
			 
				 sb.append(",");
				 sb.append( db.toSQL(identifier));
				 
			 }	 
			 sb.append(" ) ");
			
			 
			 ResultSet rs = db.executeRead(sb.toString());
			 
			 T o = null;
			 
			 while (rs.next()) {
				 
				 o = getObject(c, r, rs);
				 results.add(o);
			 }
			 
		 } catch (Exception e) {
			 
			 e.printStackTrace();
			 
		 }
		
		return results;
	}

	public <T extends Persistable> List<T> get(Class<? extends Persistable> c, String attribute, Object value) {
		
		return null;
	}

	public <T extends Persistable> List<T> get(Class<? extends Persistable> c, PersistableFilter filter) {
		return null;
	}

	
}
