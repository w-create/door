package com.taiji.tscp.sso.server.controller.admin;

import com.taiji.tscp.api.kernel.IReqData;
import com.taiji.tscp.api.kernel.IResData;
import com.taiji.tscp.common.annotation.Action;
import com.taiji.tscp.kernel.util.TscpServiceUtil;
import com.taiji.tscp.mvc.model.Result;
import com.taiji.tscp.mvc.model.ResultCode;
import com.taiji.tscp.platform.web.event.TscpRes;
import com.taiji.tscp.sso.server.controller.common.BaseController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Joe
 */
@Api(tags = "用户角色关系管理")
@Action
public class RolePermissionAction extends BaseController {


	@ApiOperation("初始页")
	public IResData rolePermission(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			Integer roleId = (req.getAttr("roleId") == null || "".equals(req.getAttr("roleId"))) ? null : Integer.parseInt((String)req.getAttr("roleId"));
			req.getHttpReq().setAttribute("roleId", roleId);
			req.getHttpReq().setAttribute("appList", TscpServiceUtil.callService("findAppByAll", 1));
			req.getHttpReq().getRequestDispatcher("/admin/rolePermission.jsp").forward(req.getHttpReq(), req.getHttpRes());
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}
	
	@ApiOperation("角色授权提交")
	public IResData save(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			Integer appId  = (req.getAttr("appId") == null || "".equals(req.getAttr("appId"))) ? null : Integer.parseInt((String)req.getAttr("appId"));
			Integer roleId = (req.getAttr("roleId") == null || "".equals(req.getAttr("roleId"))) ? null : Integer.parseInt((String)req.getAttr("roleId"));
			if(appId == null || roleId == null) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
			String permissionIds = (String)req.getAttr("permissionIds");
			//rolePermissionService.allocate(appId, roleId, getAjaxIds(permissionIds));
			TscpServiceUtil.callService("saveRoleAppService", appId,roleId,getAjaxIds(permissionIds));
			res.addJsonObjectOutWrite(Result.createSuccessResult().setMessage("授权成功"));
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}
}