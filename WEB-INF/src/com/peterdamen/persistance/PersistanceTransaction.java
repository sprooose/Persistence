package com.peterdamen.persistance;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.peterdamen.persistance.exception.PersistanceTransactionAlreadyExistsForThreadException;
import com.peterdamen.persistance.exception.PersistanceTransactionDoesntExistException;
import com.peterdamen.persistance.exception.PersistanceTransactionHandlerNotSetException;
import com.peterdamen.persistance.util.Logger;

public class PersistanceTransaction {

	private static Map<Long, PersistanceTransaction> transactions;
	private TransactionHandler handler;
	
	long threadId;
	
	
	
	public TransactionHandler getTransactionHandler() throws PersistanceTransactionHandlerNotSetException {
		
		if (handler==null) throw new PersistanceTransactionHandlerNotSetException();
		return handler;
		
	}
	
	public static Map<Long, PersistanceTransaction> getTransactions() {
		
		if (transactions==null) {
			transactions = new HashMap<Long, PersistanceTransaction>();
		}
		
		return transactions;
		
	}
	
	private PersistanceTransaction(Long threadId) {
		
		this.threadId = threadId;
		handler = PersistanceEngine.getInstance().getHandler().getHandler(); 

	} 
	
	public void start() throws PersistanceTransactionAlreadyExistsForThreadException {
		
		if (transactions.containsKey(threadId)) throw new PersistanceTransactionAlreadyExistsForThreadException();
		handler = PersistanceEngine.getInstance().getHandler().getHandler(); 
		getTransactions().put(threadId, this);
	}
	
	
	public synchronized static PersistanceTransaction startTransaction() throws PersistanceTransactionAlreadyExistsForThreadException {
		
		long threadId = Thread.currentThread().getId();
		PersistanceTransaction p = getTransactions().get(threadId);
		
		if (p!=null) throw new PersistanceTransactionAlreadyExistsForThreadException();
		
		p = new PersistanceTransaction(threadId);
		getTransactions().put(threadId, p);
		
		return p;
		
	}
	
	public static PersistanceTransaction getCurrent() throws PersistanceTransactionDoesntExistException {
		
		PersistanceTransaction p = getTransactions().get(Thread.currentThread().getId());
		
		if (p==null) throw new PersistanceTransactionDoesntExistException();
		
		return p;
		
	}
	
	public static Object getObject(Class<? extends Persistable> c, UUID identifier) throws PersistanceTransactionDoesntExistException {
		
		PersistanceTransaction p = getTransactions().get(Thread.currentThread().getId());
		return p.get(c, identifier);
		
	}
	
	public synchronized void commit() throws PersistanceTransactionDoesntExistException {
		
		if (!transactions.containsKey(threadId)) throw new PersistanceTransactionDoesntExistException();
		
		handler.commit();
		handler = null;
		transactions.remove(threadId);
		
	}
	
	public synchronized static void commitTransaction() throws PersistanceTransactionDoesntExistException {
		
		Logger.debug("commit");
		
		long threadId = Thread.currentThread().getId();
		PersistanceTransaction p = getTransactions().get(threadId);
		
		if (p==null) throw new PersistanceTransactionDoesntExistException();
		
		p.commit();
	}
	
	
	public <T extends Persistable> T get(Class<? extends Persistable> c, UUID identifier) {
		
		if (!transactions.containsKey(threadId)) throw new PersistanceTransactionDoesntExistException();
		return handler.get(c, identifier);
		
	}
	
	
}
