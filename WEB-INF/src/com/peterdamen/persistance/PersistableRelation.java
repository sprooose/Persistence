package com.peterdamen.persistance;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class PersistableRelation {

	String name;
	String relationName;
	LinkedHashMap<String, PersistableField> fields;
	Map<String, Method> getters;
	Map<String, Method> setters;
	
	public PersistableRelation(String name, PersistableField[] fields, PersistanceEngine engine, Map<String, Method> getters, Map<String, Method> setters) {
		
		this.name = name;
		this.relationName = name.toLowerCase();
		this.fields = new LinkedHashMap<String, PersistableField>();
		this.setters = setters;
		this.getters = getters;
		
		for (PersistableField field : fields) {
			this.fields.put(field.getFieldName(), field);
		}
		
	}
	
	public Map<String, Method> getGetters() {
		return Collections.unmodifiableMap(getters);
	}

	public Map<String, Method> getSetters() {
		return Collections.unmodifiableMap(setters);
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRelationName() {
		return relationName;
	}
	public void setRelationName(String relationName) {
		this.relationName = relationName;
	}
	public PersistableField[] getFields() {
		return fields.values().toArray(new PersistableField[0]);
	}
	public PersistableField getField(String fieldName) {
		return fields.get(fieldName);
	}
	
	
}
