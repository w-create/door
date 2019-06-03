package com.taiji.tscp.sso.server.util;

import java.io.IOException;
import java.util.Properties;

import com.taiji.tscp.sso.server.common.LocalTokenManager;
import com.taiji.tscp.sso.server.common.RedisTokenManager;
import com.taiji.tscp.sso.server.common.TokenManager;

public class TokenManagerFactory {
	
	
	private static String systemTokenType;
	
	
	public static TokenManager getTokenManager() {
		
		return getTokenManager(systemTokenType);
	}
	
	 
	public static TokenManager getTokenManager(String tokenType) {

		if(tokenType == null || "".equals(tokenType)) {
			tokenType = (systemTokenType != null && !"".equals(systemTokenType))?systemTokenType:"local";
		}
		
		if("local".equals(tokenType)) {
			return LocalTokenManager.getInstance();
		}else if("redis".equals(tokenType)){
			return RedisTokenManager.getInstance();
		}else {
			throw new IllegalArgumentException("unknown token type");
		}
		
	}
	
	static {
		
		Properties properties = new Properties();
		try {
			properties.load(TokenManagerFactory.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		systemTokenType = properties.getProperty("tokenManager.type");
		
	}
	

}
