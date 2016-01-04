package com.peterdamen.persistance;

public class PersistableField {

	String fieldName;
	String description;
	PersistableFieldType type;
	boolean index;
	Integer maximumSize;
	
	public PersistableField(String fieldName, String description, PersistableFieldType type, boolean index,
			Integer maximumSize) {
		super();
		this.fieldName = fieldName;
		this.description = description;
		this.type = type;
		this.index = index;
		this.maximumSize = maximumSize;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public PersistableFieldType getType() {
		return type;
	}
	public void setType(PersistableFieldType type) {
		this.type = type;
	}
	public boolean isIndex() {
		return index;
	}
	public void setIndex(boolean index) {
		this.index = index;
	}
	public Integer getMaximumSize() {
		return maximumSize;
	}
	public void setMaximumSize(Integer maximumSize) {
		this.maximumSize = maximumSize;
	}
	
	
}
