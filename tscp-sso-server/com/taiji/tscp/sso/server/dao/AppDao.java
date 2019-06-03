package com.taiji.tscp.sso.server.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.taiji.tscp.mvc.dao.mybatis.Dao;
import com.taiji.tscp.mvc.model.Pagination;
import com.taiji.tscp.sso.server.model.App;

/**
 * 应用持久化接口
 * 
 * @author Joe
 */
public interface AppDao extends Dao<App, Integer> {
	
	public int enable(@Param("isEnable") Boolean isEnable, @Param("idList") List<Integer> idList);
	
	public List<App> findPaginationByName(@Param("name") String name, @Param("isEnable") Boolean isEnable,
			Pagination<App> p);
	
	public App findByCode(@Param("code") String code);
}
