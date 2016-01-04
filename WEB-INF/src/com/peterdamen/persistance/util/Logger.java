package com.peterdamen.persistance.util;

public class Logger {

	public static void debug(String text) {
		output("DEBUG: " + text);
	}
	
	public static void error(String text) {
		output("ERROR: " + text);
	}
	
	public static void warn(String text) {
		output("WARN:  " + text);
	}
	
	private static void output(String text) {
		
		System.out.println(text);
		
	}
	
}
