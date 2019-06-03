package com.taiji.tscp.sso.server.service.impl;

import java.util.Iterator;
import java.util.List;

import com.taiji.tscp.api.configuration.DBParam;
import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.common.annotation.BeanContainer;
import com.taiji.tscp.common.annotation.Service;
import com.taiji.tscp.persistence.util.TscpPersistenceUtils;
import com.taiji.tscp.sso.server.model.RolePermission;
import com.taiji.tscp.sso.server.util.POUtils;

@BeanContainer
public class RolePermissionServiceImpl {
	
	
	@Service(name = "saveRoleAppService")
	public void allocate(Integer appId, Integer roleId, List<Integer> permissionIdList) throws TscpBaseException {
		
		if(permissionIdList == null || permissionIdList.isEmpty()) {
			return;
		} 
		
		DBParam param = new DBParam();
		param.addParam(appId);
		param.addParam(roleId);
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteByAppAndRoleId", param);
		for (Iterator<Integer> i$ = permissionIdList.iterator(); i$.hasNext();) {
			param.clear();
			param.addParam(appId);
			param.addParam(roleId);
			param.addParam(i$.next());
			TscpPersistenceUtils.getPersistenceService().insertDataByKey("saveRoleApp", param);
		}

	}

	@Service(name="findRolePermissionByRoleId")
	public List<RolePermission> findByRoleId(Integer roleId) throws TscpBaseException {
		DBParam param = new DBParam();
		param.addParam(roleId);
		return POUtils.generateList(TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findRolePermissionByRoleId", param),RolePermission.class);
	}

	@Service(name="deleteRPByPermissionIds")
	public void deleteByPermissionIds(List<Integer> idList) throws TscpBaseException {
		DBParam param = new DBParam();
		StringBuilder sb= new StringBuilder();
		for(Integer pid : idList) {
			sb.append(",").append(pid);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteRPByPermissionIds", param);
	}
	
	public void deleteByRoleIds(List<Integer> idList) {
		//dao.deleteByRoleIds(idList);
	}
	
	public void deleteByAppIds(List<Integer> idList) {
		//dao.deleteByAppIds(idList);
	}
}
