package com.peterdamen.persistance.exception;

public class PersistanceException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4920313218773543749L;

	public PersistanceException(String message) {
	
		super(message);
		
	}
	
	public PersistanceException(String message, Throwable cause) {
		
		super(message, cause);
		
	}
	
	public PersistanceException() {
		super();
	}
	
	
	
}
