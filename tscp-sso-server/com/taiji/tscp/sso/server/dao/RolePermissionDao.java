package com.taiji.tscp.sso.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taiji.tscp.mvc.dao.mybatis.Dao;
import com.taiji.tscp.sso.server.model.RolePermission;

/**
 * 角色权限映射持久化接口
 * 
 * @author Joe
 */
public interface RolePermissionDao extends Dao<RolePermission, Integer> {
	
	public List<RolePermission> findByRoleId(@Param("roleId") Integer roleId);
	
	public int deleteByPermissionIds(@Param("idList") List<Integer> idList);
	
	public int deleteByRoleIds(@Param("idList") List<Integer> idList);
	
	public int deleteByAppIds(@Param("idList") List<Integer> idList);
	
	public int deleteByAppAndRoleId(@Param("appId") Integer appId, @Param("roleId") Integer roleId);
}
