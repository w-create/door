package com.taiji.tscp.sso.server.controller.admin;

import java.util.List;

import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.api.kernel.IReqData;
import com.taiji.tscp.api.kernel.IResData;
import com.taiji.tscp.common.annotation.Action;
import com.taiji.tscp.kernel.util.TscpServiceUtil;
import com.taiji.tscp.mvc.model.Result;
import com.taiji.tscp.mvc.model.ResultCode;
import com.taiji.tscp.platform.web.event.TscpRes;
import com.taiji.tscp.sso.client.StringUtils;
import com.taiji.tscp.sso.server.controller.common.BaseController;
import com.taiji.tscp.sso.server.model.Permission;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Joe
 */
@Api(tags = "权限(含菜单)管理")
@Action
public class PermissionAction extends BaseController {


	@ApiOperation("初始页")
	public IResData permission(IReqData req) throws Exception {
		
		IResData res = new TscpRes();
		req.getHttpReq().setAttribute("appList", TscpServiceUtil.callService("findAppByAll", 1));
		req.getHttpReq().getRequestDispatcher("/admin/permission.jsp").forward(req.getHttpReq(), req.getHttpRes());
		return res;
	}

	
	
	@SuppressWarnings("unchecked")
	@ApiOperation("权限树节点")
	public IResData nodes(IReqData req) throws TscpBaseException {

		IResData res = new TscpRes();
		try {
			Integer appId = (req.getAttr("appId") == null || "".equals(req.getAttr("appId"))) ? null : Integer.parseInt((String)req.getAttr("appId"));
			Integer roleId = (req.getAttr("roleId") == null || "".equals(req.getAttr("roleId"))) ? null : Integer.parseInt((String)req.getAttr("roleId"));
			int isEnable = ((req.getAttr("isEnable") != null && !"".equals(req.getAttr("isEnable")) && Boolean.parseBoolean((String)req.getAttr("isEnable"))
					||req.getAttr("isEnable") == null || "".equals(req.getAttr("isEnable")))) ? 1 : 0;
			List<Permission> list = (List<Permission>) TscpServiceUtil.callService("findPermissionService", appId, roleId, isEnable);
			Permission permission = new Permission();
			permission.setId(null);
			permission.setParentId(-1);
			permission.setName("根节点");
			permission.setAppId(appId);
			list.add(0, permission);
			res.addJsonObjectOutWrite(list);
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
		}
		return res;
	}

	@ApiOperation("新增/修改提交")
	public IResData save(IReqData req) {
		
		IResData res = new TscpRes();
		try {
		Integer id = (req.getAttr("id") == null || "".equals(req.getAttr("id"))) ? null : Integer.parseInt((String)req.getAttr("id"));
		Integer appId = (req.getAttr("appId") == null || "".equals(req.getAttr("appId"))) ? null : Integer.parseInt((String)req.getAttr("appId"));
		Integer parentId = (req.getAttr("parentId") == null || "".equals(req.getAttr("parentId"))) ? null : Integer.parseInt((String)req.getAttr("parentId"));
		String icon = (String) req.getAttr("icon");
		String name = (String) req.getAttr("icon");
		String url = (String) req.getAttr("icon");
		Integer sort = (req.getAttr("sort") == null || "".equals(req.getAttr("sort"))) ? null : Integer.parseInt((String)req.getAttr("sort"));
		Boolean isMenu =  (req.getAttr("isMenu") == null || "".equals(req.getAttr("isMenu"))) ? false : Boolean.parseBoolean((String)req.getAttr("isMenu"));
		Boolean isEnable = (req.getAttr("isEnable") == null || "".equals(req.getAttr("isEnable"))) ? false : Boolean.parseBoolean((String)req.getAttr("isEnable"));
		
		if(appId == null || parentId == null || StringUtils.isEmpty(name) || StringUtils.isEmpty(url) || sort == null || isMenu == null || isEnable == null) {
			res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
			return res;
		}
		
		Permission permission;
		if (id == null) {
			permission = new Permission();
		}
		else {
			permission = (Permission) TscpServiceUtil.callService("findPermissionById", id);
		}
		permission.setAppId(appId);
		permission.setParentId(parentId);
		permission.setIcon(icon);
		permission.setName(name);
		permission.setUrl(url);
		permission.setSort(sort);
		permission.setIsMenu(isMenu);
		permission.setIsEnable(isEnable);
		if(id == null) {
			TscpServiceUtil.callService("savePermission", permission);
		}else {
			TscpServiceUtil.callService("updatePermission", permission);
		}
		res.addJsonObjectOutWrite(Result.createSuccessResult().setMessage("保存成功"));
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
		}
		return res;
	}

	@ApiOperation("删除")
	public IResData delete(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			Integer id = (req.getAttr("id") == null || "".equals(req.getAttr("id"))) ? null : Integer.parseInt((String)req.getAttr("id"));
			Integer appId = (req.getAttr("appId") == null || "".equals(req.getAttr("appId"))) ? null : Integer.parseInt((String)req.getAttr("appId"));
			if(id == null || appId == null) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
			TscpServiceUtil.callService("deletePermission", id, appId);
			res.addJsonObjectOutWrite(Result.createSuccessResult().setMessage("删除成功"));
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
		}
		return res;
	}
}