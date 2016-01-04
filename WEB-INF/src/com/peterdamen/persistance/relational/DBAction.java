package com.peterdamen.persistance.relational;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

public class DBAction {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	Connection connection;
	
	public DBAction(String driver, String url, Properties properties) throws Exception {
		
		connection = DriverManager.getConnection(url, properties);
		
	}
	
	public void disconnect() {
		
		
	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public String toSQL( Object o) {
		if (o==null) return "NULL";
		if (o instanceof String) return toSQL((String)o);
		if (o instanceof Number) return toSQL((Number)o);
		if (o instanceof Date) return toSQL((Date)o);
		if (o instanceof Boolean) return toSQL((Boolean)o);
		if (o instanceof UUID) return toSQL((UUID)o);
		else return toSQL(o.toString());
	}
	
	
	public String toSQL(String text) {
		if (text==null) return "NULL";
		return "'" + text + "'";
	}
	
	public String toSQL(Number number) {
		if (number==null) return "NULL";
		return number.toString();
	}
	
	public String toSQL(UUID uuid) {
		if (uuid==null) return "NULL";
		return "'" + uuid.toString() + "'";
	}
	
	public String toSQL(Date date) {
		if (date==null) return "NULL";
		return "'" + sdf.format(date) + "'";
	}
	
	public String toSQL(Boolean bool) {
		if (bool==null) return "NULL";
		return bool ? "TRUE" : "FALSE";
	}
	
	public void executeUpdate(String sql) throws Exception {
		
		 PreparedStatement s = connection.prepareStatement(sql);
		 s.execute();
		
	}
	
	public ResultSet executeRead(String sql) throws Exception {
		
		 PreparedStatement s = connection.prepareStatement(sql);
		 return s.executeQuery();
		
	}
	
	
}
