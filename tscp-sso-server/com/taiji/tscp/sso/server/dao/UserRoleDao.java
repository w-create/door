package com.taiji.tscp.sso.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taiji.tscp.mvc.dao.mybatis.Dao;
import com.taiji.tscp.sso.server.model.UserRole;

/**
 * 用户角色映射持久化接口
 * 
 * @author Joe
 */
public interface UserRoleDao extends Dao<UserRole, Integer> {

	public UserRole findByUserRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);

	public int deleteByRoleIds(@Param("idList") List<Long> idList);

	public int deleteByUserIds(@Param("idList") List<Long> idList);
}
