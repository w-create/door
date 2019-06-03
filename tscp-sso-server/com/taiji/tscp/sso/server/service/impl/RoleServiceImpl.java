package com.taiji.tscp.sso.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.taiji.tscp.api.configuration.DBParam;
import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.common.annotation.BeanContainer;
import com.taiji.tscp.common.annotation.Service;
import com.taiji.tscp.mvc.model.Pagination;
import com.taiji.tscp.persistence.util.TscpPersistenceUtils;
import com.taiji.tscp.sso.client.StringUtils;
import com.taiji.tscp.sso.server.model.Role;
import com.taiji.tscp.sso.server.util.POUtils;


@BeanContainer
public class RoleServiceImpl {


	@Service(name="enableRole")
	public void enableRole(Integer isEnable, List<Integer> idList) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(isEnable);
		StringBuilder sb= new StringBuilder();
		for(Integer id : idList) {
			sb.append(",").append(id);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		TscpPersistenceUtils.getPersistenceService().updateDataByKey("enableRole", param);
		
	}
	
	
	@Service(name="getRoleById")
	public Role getRoleById(Integer id) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(id);
		return POUtils.mapConvertRole(TscpPersistenceUtils.getPersistenceService().queryMapByKey("getRoleById", param));
	}
	

	@Service(name="saveRoleService")
	public void saveRoleService(Role role) throws TscpBaseException {
		
		DBParam param = new DBParam();
		if(role.getId() != null) {
			param.addParam(role.getName());
			param.addParam(role.getSort());
			param.addParam(role.getDescription());
			param.addParam(role.getIsEnable() ? 1 : 0);
			param.addParam(role.getId());
			TscpPersistenceUtils.getPersistenceService().updateDataByKey("updateRole", param);
		}else {
			param.clear();
			param.addParam(role.getName());
			param.addParam(role.getSort());
			param.addParam(role.getDescription());
			param.addParam(role.getIsEnable() ? 1 : 0);
			TscpPersistenceUtils.getPersistenceService().insertDataByKey("saveRole", param);
		}
	}
	

	@Service(name="findRolePaginationByName")
	public Pagination<Role> findRolePaginationByName(String name, Integer pageNo,Integer pageSize) throws TscpBaseException {
		
		DBParam param = new DBParam();
		if(StringUtils.isEmpty(name)) {
			param.addParam((pageNo - 1) * pageSize);
			param.addParam(pageSize);
			return POUtils.generatePagination(TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findRolePaginationByNameAll", param), pageNo, pageSize, 
					((Long)TscpPersistenceUtils.getPersistenceService().queryMapByKey("findRolePaginationByNameAllSize", null).get("num")).intValue(),Role.class);
		}else {
			param.addParam(name);
			Integer totalSize = ((Long)TscpPersistenceUtils.getPersistenceService().queryMapByKey("findRolePaginationByNameSize", param).get("num")).intValue();
			param.addParam((pageNo - 1) * pageSize);
			param.addParam(pageSize);
			return POUtils.generatePagination(TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findRolePaginationByName", param), pageNo, pageSize,totalSize,Role.class);
		}
	}

	@Service(name="findRoleByAll")
	public List<Role> findRoleByAll(Integer isEnable) throws TscpBaseException {
		DBParam param = new DBParam();
		param.addParam(isEnable);
		List<Map<String,Object>> lisMap = TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findRoleByAll", param);
		if(lisMap == null || lisMap.isEmpty()) {
			return null;
		}
		List<Role> roles = new ArrayList<Role>();
		for(Map<String,Object> map : lisMap) {
			roles.add(POUtils.mapConvertRole(map));
		}
		return roles;
	}

	@Service(name="deleteRoleByIds")
	public void deleteById(List<Integer> idList) throws TscpBaseException {
		
		DBParam param = new DBParam();
		StringBuilder sb= new StringBuilder();
		for(Integer id : idList) {
			sb.append(",").append(id);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteRoleByIds", param);
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteUserRoleByRoleIds", param);
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteRolePermByRoleIds", param);
	}
}
