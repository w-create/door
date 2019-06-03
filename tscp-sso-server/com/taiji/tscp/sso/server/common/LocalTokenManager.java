package com.taiji.tscp.sso.server.common;

import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.kernel.util.TscpServiceUtil;

/**
 * 单实例环境令牌管理
 * 
 * @author Joe
 */
public class LocalTokenManager extends TokenManager {
	
	
	private LocalTokenManager() {
		super();
	}

	private static TokenManager tokenManager;
	
	public static TokenManager getInstance() {
		
		if(tokenManager == null) {
			synchronized(LocalTokenManager.class) {
				if(tokenManager == null) {
					tokenManager = new LocalTokenManager();
				}
			}
		}
		
		return tokenManager;
	}

	private final Logger logger = LoggerFactory.getLogger(getClass());

	// 令牌存储结构
	private final ConcurrentHashMap<String, DummyUser> tokenMap = new ConcurrentHashMap<String, DummyUser>();

	@Override
	public void verifyExpired() {
		Date now = new Date();
		for (Entry<String, DummyUser> entry : tokenMap.entrySet()) {
			String token = entry.getKey();
			DummyUser dummyUser = entry.getValue();
			// 当前时间大于过期时间
			if (now.compareTo(dummyUser.expired) > 0) {
				// 已过期，清除对应token
				if (now.compareTo(dummyUser.expired) > 0) {
					remove(token);
					logger.debug("token : " + token + "已失效");
				}
			}
		}
	}

	public void addToken(String token, LoginUser loginUser) {
		DummyUser dummyUser = new DummyUser();
		dummyUser.loginUser = loginUser;
		extendExpiredTime(dummyUser);
		tokenMap.putIfAbsent(token, dummyUser);
	}

	public LoginUser validate(String token) {
		DummyUser dummyUser = tokenMap.get(token);
		if (dummyUser == null) {
			return null;
		}
		extendExpiredTime(dummyUser);
		return dummyUser.loginUser;
	}

	public void remove(String token) {
		try {
			TscpServiceUtil.callService("recordSystemLog",tokenMap.get(token).loginUser.getAccount(),"[SSO-人员登录退出]");
		} catch (TscpBaseException e) {
			logger.error("记录日志失败",e);
		}
		tokenMap.remove(token);
	}

	/**
	 * 扩展过期时间
	 * 
	 * @param dummyUser
	 */
	private void extendExpiredTime(DummyUser dummyUser) {
		dummyUser.expired = new Date(new Date().getTime() + tokenTimeout * 1000);
	}

	// 复合结构体，含loginUser与过期时间expried两个成员
	private class DummyUser {
		private LoginUser loginUser;
		private Date expired; // 过期时间
	}
}
