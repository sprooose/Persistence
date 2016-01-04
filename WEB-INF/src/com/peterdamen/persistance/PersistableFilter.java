package com.peterdamen.persistance;

public class PersistableFilter {

	private enum ParentRelationship {
		
		AND,
		OR,
		ROOT
	}
	
	protected String attribute;
	protected PersistableFilterOperator operator;
	protected Object value;
	
	protected PersistableFilter parent; 
	
	private PersistableFilter(String attribute, PersistableFilterOperator operator, Object value, ParentRelationship parentRelation ) {
		
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
		
	}
	
	public static PersistableFilter add(String attribute, PersistableFilterOperator operator, Object value ) {
		
		return new PersistableFilter(attribute, operator, value, ParentRelationship.ROOT);
		
	}
	
	public PersistableFilter and(String attribute, PersistableFilterOperator operator, Object value) {
		
		PersistableFilter p = new PersistableFilter(attribute, operator, value, ParentRelationship.AND);
		p.parent = this;
		return p;
		
	}
	
	public PersistableFilter or(String attribute, PersistableFilterOperator operator, Object value) {
		
		PersistableFilter p = new PersistableFilter(attribute, operator, value, ParentRelationship.OR);
		p.parent = this;
		return p;
		
	}
	
	
	
	
	
}
