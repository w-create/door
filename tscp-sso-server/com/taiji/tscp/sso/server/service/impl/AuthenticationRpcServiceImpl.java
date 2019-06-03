package com.taiji.tscp.sso.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.taiji.tscp.api.configuration.DBParam;
import com.taiji.tscp.mvc.util.StringUtils;
import com.taiji.tscp.persistence.util.TscpPersistenceUtils;
import com.taiji.tscp.sso.rpc.AuthenticationRpcService;
import com.taiji.tscp.sso.rpc.RpcUser;
import com.taiji.tscp.sso.server.common.LoginUser;
import com.taiji.tscp.sso.server.common.TokenManager;
import com.taiji.tscp.sso.server.util.TokenManagerFactory;


public class AuthenticationRpcServiceImpl implements AuthenticationRpcService {

	
	private TokenManager tokenManager = TokenManagerFactory.getTokenManager();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean validate(String token) {
		return tokenManager.validate(token) != null;
	}
	
	@Override
	public RpcUser findAuthInfo(String token) {
		LoginUser user = tokenManager.validate(token);
		if (user != null) {
			return new RpcUser(user.getAccount());
		}
		return null;
	}
	
	@Override
	public List<Map<String,Object>> findPermissionList(String token, String appCode) {
		
		try {
		if (StringUtils.isBlank(token)) {
			DBParam param = new DBParam();
			param.addParam(appCode);
			return TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findListByAppCode", param);
		}
		else {
			LoginUser user = tokenManager.validate(token);
			if (user != null) {
				DBParam param = new DBParam();
				param.addParam(appCode);
				param.addParam(user.getUserId());
				return TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findListByAppIdAndCode", param);
			}
			else {
				return new ArrayList<Map<String,Object>>(0);
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public TokenManager getTokenManager() {
		return tokenManager;
	}

	public void setTokenManager(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

	@Override
	public boolean remove(String token) {
		try {
			tokenManager.remove(token);
			return true;
		}catch(Exception e) {
			logger.error("清理用户信息失败",e);
			return false;
		}
	}
	
	
}
