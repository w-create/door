package com.taiji.tscp.sso.server.service.impl;

import java.util.List;

import com.taiji.tscp.api.configuration.DBParam;
import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.common.annotation.BeanContainer;
import com.taiji.tscp.common.annotation.Service;
import com.taiji.tscp.mvc.model.Pagination;
import com.taiji.tscp.persistence.util.TscpPersistenceUtils;
import com.taiji.tscp.sso.client.StringUtils;
import com.taiji.tscp.sso.server.model.App;
import com.taiji.tscp.sso.server.util.POUtils;

@BeanContainer
public class AppServiceImpl {
	

	
	@Service(name="enableApp")
	public void enableApp(Integer isEnable, List<Integer> idList) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(isEnable);
		StringBuilder sb= new StringBuilder();
		for(Integer id : idList) {
			sb.append(",").append(id);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		TscpPersistenceUtils.getPersistenceService().updateDataByKey("enableApp", param);
	}
	
	@Service(name="saveAppService")
	public void saveApp(App t) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(t.getName());
		param.addParam(t.getCode());
		param.addParam(t.getSort());
		param.addParam(t.getCreateTime());
		param.addParam(t.getIsEnable() ? 1 : 0);
		TscpPersistenceUtils.getPersistenceService().insertDataByKey("saveApp", param);
	}
	
	
	@Service(name="updateAppService")
	public void updateAppService(App t) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(t.getName());
		param.addParam(t.getCode());
		param.addParam(t.getSort());
		param.addParam(t.getIsEnable() ? 1 : 0);
		param.addParam(t.getId());
		TscpPersistenceUtils.getPersistenceService().updateDataByKey("updateApp", param);
	}

	@Service(name="findAppByAll")
	public List<App> findAppByAll(Integer isEnable) throws TscpBaseException {
		
		DBParam param = new DBParam();
		param.addParam(isEnable);
		return POUtils.generateList(TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findAppByAll", param),App.class);
	}
	
	@Service(name="findAppByAppIds")
	public List<App> findAppByAppIds(List<Integer> idList) throws TscpBaseException {
		
		DBParam param = new DBParam();
		StringBuilder sb= new StringBuilder();
		for(Integer id : idList) {
			sb.append(",").append(id);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		return POUtils.generateList(TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findAppByAppIds", param),App.class);
	}

	@Service(name="findAppPaginationByName")
	public Pagination<App> findAppPaginationByName(String name, Integer pageNo,Integer pageSize) throws TscpBaseException {
		
		DBParam param = new DBParam();
		
		if(StringUtils.isEmpty(name)) {
			param.addParam((pageNo-1)*pageSize);
			param.addParam(pageSize);
			return POUtils.generatePagination(TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findAppPaginationByNameAll", param), pageNo, pageSize,
					((Long)TscpPersistenceUtils.getPersistenceService().queryMapByKey("findAppPaginationByNameAllSize", null).get("num")).intValue(), App.class);
		}else {
			param.clear();
			param.addParam(name);
			Long totalSize = (Long) TscpPersistenceUtils.getPersistenceService().queryMapByKey("findAppPaginationByNameSize", param).get("num");
			param.addParam((pageNo-1)*pageSize);
			param.addParam(pageSize);
			return POUtils.generatePagination(TscpPersistenceUtils.getPersistenceService().queryLisMapByKey("findAppPaginationByName", param), pageNo, pageSize, totalSize.intValue(), App.class);
		}
	}

	@Service(name="findAppByCode")
	public App findAppByCode(String code) throws TscpBaseException {
		DBParam param = new DBParam();
		param.addParam(code);
		return POUtils.mapConvertApp(TscpPersistenceUtils.getPersistenceService().queryMapByKey("findAppByCode", param));
	}
	
	@Service(name="deleteAppById")
	public void deleteAppById(List<Integer> idList) throws TscpBaseException {
		
		DBParam param = new DBParam();
		StringBuilder sb= new StringBuilder();
		for(Integer id : idList) {
			sb.append(",").append(id);
		}
		param.addDynamicParam(sb.length() > 0 ? sb.substring(1) : "");
		
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteRolePermissionByAppIds", param);
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deletepermissionByAppIds", param);
		TscpPersistenceUtils.getPersistenceService().deleteDataByKey("deleteAppByAppIds", param);
		
	}
}
