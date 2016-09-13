package com.jimmy.IOTCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

public class ReadPropertiesConfig {
	
	private static Properties properties = null;
	private static Ini ini = null;
	private static FileInputStream fileInput = null;
	private static Preferences prefs = null;
	
	public ReadPropertiesConfig (String filePath){
		load(filePath);
	}
	
	private void load(String filePath){
		File file = new File(filePath);
		try {
			fileInput = new FileInputStream(file);
			//prefs = new IniPreferences(new Ini(new File(filePath)));
			ini = new Ini();
			properties = new Properties();
			properties.load(fileInput);
			//FileReader fileReadobj = new FileReader(file);
			//fileReadobj.skip(16);
			//ini.load(fileReadobj);
			//prefs = new IniPreferences(ini);
			fileInput.close();
		} catch (InvalidFileFormatException ef){
			LogTrace.LogError(this.getClass(), ef);
			
		} catch (FileNotFoundException e) {
			LogTrace.LogError(this.getClass(), e);
		} catch (IOException e) {
			LogTrace.LogError(this.getClass(), e);
		} 
	}
	
	public String getConfig(String sectionkey){
		String result = null;
		String []sectionNkey = null;
		
		LogTrace.LogInfo(this.getClass(), sectionkey + " aaaaaaaaaaaaa");
		LogTrace.LogInfo(this.getClass(), sectionkey + " aaaaaaaaaaaaa" + sectionkey.indexOf("."));
			if(sectionkey.indexOf(".") != -1){
					LogTrace.LogInfo(this.getClass(), "section key got dot");
					sectionNkey = sectionkey.split(Pattern.quote("."));
					LogTrace.LogError(this.getClass(), sectionNkey[0].toString() + "            " + sectionNkey[1].toString());
					properties.getProperty(sectionNkey[0].toString(), sectionNkey[1].toString());
					//Ini.Section section = ini.get(sectionNkey[0].toString());
					//result = section.get(sectionNkey[1].toString());
			}
			else{
				LogTrace.LogInfo(this.getClass(), "section key no dot");
				result = properties.getProperty(sectionkey);
				LogTrace.LogError(this.getClass(), sectionkey);
			}
					
			//
			//Enumeration enuKey = properties.keys();
			// result will be null if cannot get the property key in the config file.
			//result = properties.getProperty(key);
			
			if(result == null)
			{
				LogTrace.LogError(this.getClass(), sectionkey +" cannot find in config file" );
			}

		
		return result;
	}
	
}