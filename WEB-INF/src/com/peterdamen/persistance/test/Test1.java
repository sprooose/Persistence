package com.peterdamen.persistance.test;

import java.sql.Date;
import java.util.Properties;
import java.util.UUID;

import com.peterdamen.persistance.PersistanceEngine;
import com.peterdamen.persistance.PersistanceTransaction;

public class Test1 {

	public static void main(String arg[]) {
		
		Properties testProperties = new Properties();
		
		testProperties.put("delegate", "com.peterdamen.persistance.relational.RelationalPersistanceHandlerDelegate");
		testProperties.put("delegate_driver", "org.postgresql.Driver");
		testProperties.put("delegate_url", "jdbc:postgresql://localhost:5432/admin");
		testProperties.put("delegate_username", "admin");
		testProperties.put("delegate_password", "admin");
		
		PersistanceEngine pe = new PersistanceEngine(testProperties);
		
		pe.registerRelation(Person.class);
		
		PersistanceTransaction pt = PersistanceTransaction.startTransaction();
		Person p = new Person();
		p.setFirstname("Peter");
		p.setLastname("Damen");
		p.setDob(new Date(System.currentTimeMillis()));
		p.setGender("MALE");
		p.persist();
		pt.commit();
	
		pt = PersistanceTransaction.startTransaction();
		p.setGender("FEMALE");
		p.setCool(true);
		p.persist();
		pt.commit();
		
		pt.start();
		p.delete();
		pt.commit(); 
		
		pt.start();
		p = pt.get(Person.class, UUID.fromString("457a23e7-9e51-418f-9f61-ca6d114a92d4"));
		pt.commit();
		
		System.out.println(p);
		
		
	}
	
}
