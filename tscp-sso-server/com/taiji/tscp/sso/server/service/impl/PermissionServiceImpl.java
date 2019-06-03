package com.taiji.tscp.sso.server.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.taiji.tscp.api.configuration.DBParam;
import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.common.annotation.BeanContainer;
import com.taiji.tscp.common.annotation.Service;
import com.taiji.tscp.kernel.util.TscpServiceUtil;
import com.taiji.tscp.persistence.util.TscpPersistenceUtils;
import com.taiji.tscp.sso.rpc.RpcPermission;
import com.taiji.tscp.sso.server.model.Permission;
import com.taiji.tscp.sso.server.model.RolePermission;
import com.taiji.tscp.sso.server.util.POUtils;

@BeanContainer
public class PermissionServiceImpl{


	
	@Service(name="savePermission")
	public void savePermission(Permission t) throws TscpBaseException {
		DBParam param = new DBParam();
		param.addParam(t.getAppId());
		param.addParam(t.getParentId());
		param.addParam(t.getIcon());
		param.addParam(t.getName());
		param.addParam(t.getUrl());
		param.addParam(t.getSort());
		param.addParam(t.getIsMenu()?1:0);
		param.addParam(t.getIsEnable()?1:0);
		TscpPersistenceUtils.getPersistenceService().insertDataByKey("savePermission", param);
	}
	

	@Service(name="updatePermission")
	public void updatePermission(Permission t) throws TscpBaseException {
		DBParam param = new DBParam();
		param.addParam(t.getAppId());
		param.addParam(t.getParentId());
		param.addParam(t.getIcon());
		param.addParam(t.getName());
		param.addParam(t.getUrl());
		param.addParam(t.getSort());
		param.addParam(t.getIsMenu()?1:0);
		param.addParam(t.getIsEnable()?1:0);
		param.addParam(t.getId());
		TscpPersistenceUtils.getPersistenceService().updateDataByKey("updatePermission", param);
	}

	@SuppressWarnings("unchecked")
	@Service(name = "findPermissionService")
	public List<Permission> findPermissionService(Integer appId, Integer roleId, Integer isEnable) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(appId);
		param.addParam(isEnable);
		List<Permission> permissionList = POUtils.generateList(TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findAppByAppId", param),Permission.class);
		if(permissionList == null || permissionList.isEmpty()) {
			return new ArrayList<Permission>();
		}
		if (roleId != null) {
			List<RolePermission> rolePermissionList = (List<RolePermission>) TscpServiceUtil.callService("findRolePermissionByRoleId", roleId);
			if(rolePermissionList == null || rolePermissionList.isEmpty()) {
				return permissionList;
			}
			for (Permission permission : permissionList) {
				for (RolePermission rp : rolePermissionList) {
					if (permission.getId().equals(rp.getPermissionId())) {
						permission.setChecked(true);
						break;
					}
				}
			}
		}
		return permissionList;
	}

	@Service(name = "deletePermission")
	public void deletePermission(Integer id, Integer appId) throws TscpBaseException {
		List<Integer> idList = new ArrayList<Integer>();
		
		//List<Permission> list = permissionService.findByAppId(appId, null, null);
		DBParam param = new DBParam();
		param.addParam(appId);
		List<Permission> list = POUtils.generateList(TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findPermissionByAppId", param),Permission.class);
		loopSubList(id, idList, list);
		idList.add(id);
		param.clear();
		StringBuilder sb= new StringBuilder();
		for(Integer pid : idList) {
			sb.append(",").append(pid);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		TscpServiceUtil.callService("deleteRPByPermissionIds", idList);
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deletePermissionById", param);
	}

	// 递归方法，删除子权限
	protected void loopSubList(Integer id, List<Integer> idList, List<Permission> list) {
		for (Permission p : list) {
			if (id.equals(p.getParentId())) {
				idList.add(p.getId());
				loopSubList(p.getId(), idList, list);
			}
		}
	}

	public void deleteByAppIds(List<Integer> idList) {
		//dao.deleteByAppIds(idList);
	}

	public List<RpcPermission> findListById(String appCode, Integer userId) {
		//return dao.findListById(appCode, userId);
		return null;
	}
	
	@Service(name="findPermissionById")
	public Permission findPermissionById(Integer id) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(id);
		return POUtils.mapConvertPermission(TscpPersistenceUtils.getPersistenceService().queryMapByKey("findPermissionById", param));
		
	}
}
