package com.jimmy.IOTCore;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LogTrace extends Thread{
	
	private static Logger log = null;
	private static LogTrace instance = null;
	
	public static LogTrace getInstance(String path){
		if(instance == null)
		{
			PropertyConfigurator.configure(path);
			instance = new LogTrace();
		}		
		return instance;
	}
	
	public static void LogInfo(Object objName, String msg){
		log = Logger.getLogger(objName.toString());
		log.info(msg);
	}
	
	public static void LogDebug(Object objName, String msg){
		log = Logger.getLogger(objName.toString());
		log.debug(msg);
	}
	
	public static void LogError(Object objName, String msg){
		log = Logger.getLogger(objName.toString());
		log.error(msg);
	}
	
	public static void LogError(Object objName, Exception ex){
		log = Logger.getLogger(objName.toString());
		log.error(ex);
	}
}