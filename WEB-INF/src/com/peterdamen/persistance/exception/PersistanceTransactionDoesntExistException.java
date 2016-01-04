package com.peterdamen.persistance.exception;

public class PersistanceTransactionDoesntExistException extends PersistanceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5040797872929100123L;

	public PersistanceTransactionDoesntExistException(String message) {
		super(message);
	}

	public PersistanceTransactionDoesntExistException() {
	}

	
}
