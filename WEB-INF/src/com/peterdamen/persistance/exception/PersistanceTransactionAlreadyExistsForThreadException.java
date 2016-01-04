package com.peterdamen.persistance.exception;

public class PersistanceTransactionAlreadyExistsForThreadException extends PersistanceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8681866334864079161L;

	public PersistanceTransactionAlreadyExistsForThreadException(String message) {
		super(message);
	}

	public PersistanceTransactionAlreadyExistsForThreadException() {
	}

}
