package com.taiji.tscp.sso.server.service.impl;

import java.util.List;

import com.taiji.tscp.api.configuration.DBParam;
import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.common.annotation.BeanContainer;
import com.taiji.tscp.common.annotation.Service;
import com.taiji.tscp.persistence.util.TscpPersistenceUtils;
import com.taiji.tscp.sso.server.model.UserRole;
import com.taiji.tscp.sso.server.util.POUtils;

@BeanContainer
public class UserRoleServiceImpl  {

	
	@Service(name="addUserRole")
	public void addUserRole(Integer userId, List<DBParam> list) throws TscpBaseException {
		
		if(userId == null) {
			return;
		}
		DBParam param = new DBParam();
		param.addParam("'"+userId+"'");
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteUserRole", param);
		for(DBParam dbparam : list) {
			TscpPersistenceUtils.getPersistenceService().deleteDataByKey("insertUserRole", dbparam);
		}
	}
	
	@Service(name="findByUserRoleId")
	public UserRole findByUserRoleId(Long userId, Long roleId) throws TscpBaseException{
		DBParam param = new DBParam();
		param.addParam(userId);
		param.addParam(roleId);
		return POUtils.mapConvertUserRole(TscpPersistenceUtils.getPersistenceService().queryMapByKey("findByUserRoleId", param));
	}
	
	
}
