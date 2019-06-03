package com.taiji.tscp.mvc.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 应用配置工具类
 * 
 * @author Joe
 */
public class ConfigUtils  {

	private static Properties properties;


	public static String getProperty(String name) throws IOException {
		
		if(properties == null) {
			synchronized (ConfigUtils.class) {
				if(properties == null) {
					properties = new Properties();
					properties.load(ConfigUtils.class.getResourceAsStream("/config.properties"));
				}
			}
		}
		return properties.getProperty(name);
	}
	
	
	public static void processProperties(InputStream in)throws IOException {
		
		properties = new Properties();
		properties.load(in);
	}
}