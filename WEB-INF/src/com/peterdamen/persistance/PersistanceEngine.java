package com.peterdamen.persistance;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import com.peterdamen.persistance.exception.PersistanceRelationAlreadyExistsException;
import com.peterdamen.persistance.exception.PersistanceTransactionHandlerCouldNotBeCreatedException;
import com.peterdamen.persistance.util.Logger;

public class PersistanceEngine {

	static PersistanceEngine instance;
	static String handlerClassName;
	static TransactionHandlerDelegate handlerDelegate;
	static Properties connectionProperties;
	static HashMap<Class<? extends Persistable>, PersistableRelation> relations; 
	static boolean initialized;
	
	public PersistanceEngine(Properties properties) {
		
		instance = this;
		
		relations = new HashMap<>();
		connectionProperties = new Properties();
		
		
		Enumeration<Object> p = properties.keys();		
				
		while (p.hasMoreElements()) {
			String key = p.nextElement().toString();
			if (key.toLowerCase().startsWith("delegate_")) {
				connectionProperties.setProperty(key.substring(9), properties.getProperty(key));
			}
			if (key.toLowerCase().equals("delegate")) {
				handlerClassName = properties.getProperty(key);
			}
		}
	
		getHandler();
		
		initialized = true;
		
	}
	
	
	public static PersistanceEngine getInstance() {
	
		return instance;
		
	}
	
	
	public TransactionHandlerDelegate getHandler() {
		
		if (handlerDelegate!=null) return handlerDelegate;
		
		try {
		
			handlerDelegate = (TransactionHandlerDelegate) Class.forName(handlerClassName).newInstance();
			handlerDelegate.initialise(connectionProperties);
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new PersistanceTransactionHandlerCouldNotBeCreatedException();
		}
		
		return handlerDelegate;
		
	}
	
	public void registerRelation(Class<? extends Persistable> c) {
		
		String name = c.getSimpleName();
		Method[] methods = c.getMethods();
		
		LinkedHashMap<String, PersistableField> fields = new LinkedHashMap<>();
		HashMap<String,Method> getters = new HashMap<>();
		HashMap<String,Method> setters = new HashMap<>();
		
		for (Method m : methods) {
			
			if (m.getName().equals("getId")) continue;
			
			if (m.getName().startsWith("get")) {
				
				if(m.getParameterCount() > 0) continue;
				
				String fieldName = m.getName().substring(3);
				
				if (m.getReturnType().equals(byte[].class)
						|| m.getReturnType().equals(String.class)
						|| m.getReturnType().equals(Integer.class)
						|| m.getReturnType().equals(Long.class)
						|| m.getReturnType().equals(BigDecimal.class)
						|| m.getReturnType().equals(Float.class)
						|| m.getReturnType().equals(Double.class)
						|| m.getReturnType().equals(Boolean.class)
						|| m.getReturnType().equals(Date.class)) {
					
					getters.put(fieldName, m);
					
					PersistableField p = new PersistableField(fieldName, fieldName, typeForClass(m.getReturnType()), false, null);
					fields.put(fieldName, p);
				}
				
			} else if (m.getName().startsWith("set")) {
				
				if(m.getParameterCount() > 1) continue;
				
				String fieldName = m.getName().substring(3);
				
				Parameter p = m.getParameters()[0];
				
				Class<?> type = p.getType();
				
				if (type.equals(byte[].class)
						|| type.equals(String.class)
						|| type.equals(Integer.class)
						|| type.equals(Long.class)
						|| type.equals(BigDecimal.class)
						|| type.equals(Float.class)
						|| type.equals(Double.class)
						|| type.equals(Boolean.class)
						|| type.equals(Date.class)) {
				
					setters.put(fieldName, m);
					
				}
					
			}
			
		}
		
		registerRelation(name, c, fields.values().toArray(new PersistableField[0]), getters, setters);
		
	}
	
	
	public void registerRelation(String name, Class<? extends Persistable> c, PersistableField[] fields, Map<String,Method> getters, Map<String,Method> setters) {
		
		if (relations.containsKey(c)) {
			throw new PersistanceRelationAlreadyExistsException("Relation Exists: " + c.getName());
		}
		
		Logger.debug("Relation Name: " + name);
		for (PersistableField field : fields) {
			Logger.debug("  Field Name: " + field.getFieldName() + " Type: " + field.getType().toString());
		}
		
		boolean exists = handlerDelegate.confirmRelationExists(name, c);
		
		Logger.debug("Relation Exists: " + exists);
		
		if (!exists) {
			Logger.debug("Creating Relation: " + name);
			handlerDelegate.createRelation(name, c, fields);
		} else if (!handlerDelegate.confirmRelationSchemaCorrect(name, c, fields)){
			Logger.debug("Updating Relation: " + name);
		    handlerDelegate.updateSchema(name, c, fields);
		} else {
			Logger.debug("Relation " + name + " has the correct schema");
		}
		
		PersistableRelation pr = new PersistableRelation(name, fields, this, getters, setters);
		
		relations.put(c, pr);
		
		
	}
	
	public PersistableRelation getPersistableRelation(Class<? extends Persistable> c) {
		
		return relations.get(c);
		
	}
	
	
	private static PersistableFieldType typeForClass(Class<?> c) {
		
		String className = c.getCanonicalName();
		
		switch (className) {
			case "java.lang.String":
				return PersistableFieldType.STRING;
			case "java.lang.Integer":
				return PersistableFieldType.INTEGER;
			case "java.lang.Long":
				return PersistableFieldType.LONG;
			case "java.lang.Float":
				return PersistableFieldType.NUMERIC;
			case "java.lang.DOUBLE":
				return PersistableFieldType.NUMERIC;
			case "java.sql.Date":
				return PersistableFieldType.DATE;
			case "java.util.UUID":
				return PersistableFieldType.UUID;
			case "java.lang.Boolean":
				return PersistableFieldType.BOOLEAN;
		}
	
		return PersistableFieldType.UNKNOWN;
	}
				
				
				
	
	
	
}
