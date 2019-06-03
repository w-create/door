package com.taiji.tscp.sso.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taiji.tscp.mvc.dao.mybatis.Dao;
import com.taiji.tscp.sso.rpc.RpcPermission;
import com.taiji.tscp.sso.server.model.Permission;

/**
 * 权限持久化接口
 * 
 * @author Joe
 */
public interface PermissionDao extends Dao<Permission, Integer> {
	
	public int enable(@Param("isEnable") Boolean isEnable, @Param("idList") List<Integer> idList);
	
	public int resetPassword(@Param("password") String password, @Param("idList") List<Integer> idList);

	public List<Permission> findByAppId(@Param("appId") Integer appId, @Param("isEnable") Boolean isEnable);
	
	public int deleteByAppIds(@Param("idList") List<Integer> idList);
	
	public List<RpcPermission> findListById(@Param("appCode") String appCode, @Param("userId") Integer userId);
}
