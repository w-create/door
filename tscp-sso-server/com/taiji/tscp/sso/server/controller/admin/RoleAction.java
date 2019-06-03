package com.taiji.tscp.sso.server.controller.admin;


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
import com.taiji.tscp.sso.server.model.Role;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Joe
 */
@Api(tags = "角色管理")
@Action
public class RoleAction extends BaseController {

	
	@ApiOperation("初始页")
	public IResData role(IReqData req) throws Exception {
		
		IResData res = new TscpRes();
		req.getHttpReq().getRequestDispatcher("/admin/role.jsp").forward(req.getHttpReq(), req.getHttpRes());
		return res;
	}

	@ApiOperation("新增/修改页")
	public IResData edit(IReqData req) throws Exception {
		
		IResData res = new TscpRes();
		try {
		Integer id = (req.getAttr("id") == null || "".equals(req.getAttr("id"))) ? null : Integer.parseInt((String)req.getAttr("id"));
		Role role;
		if (id == null) {
			role = new Role();
		}else {
			role = (Role) TscpServiceUtil.callService("getRoleById", id);
		}
		req.getHttpReq().setAttribute("role", role);
		req.getHttpReq().getRequestDispatcher("/admin/roleEdit.jsp").forward(req.getHttpReq(), req.getHttpRes());
		return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}

	@ApiOperation("列表")
	public IResData list(IReqData req) throws TscpBaseException {

		IResData res = new TscpRes();
		try {
			String name = (String) req.getAttr("name");
			Integer pageNo = (req.getAttr("pageNo") == null || "".equals(req.getAttr("pageNo"))) ? null : Integer.parseInt((String)req.getAttr("pageNo"));
			Integer pageSize = (req.getAttr("pageSize") == null || "".equals(req.getAttr("pageSize"))) ? null : Integer.parseInt((String)req.getAttr("pageSize"));
			if(pageNo == null || pageSize == null) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
			res.addJsonObjectOutWrite(Result.createSuccessResult().setData(TscpServiceUtil.callService("findRolePaginationByName", name,pageNo,pageSize)));
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}

	@ApiOperation("启用/禁用")
	public IResData enable(IReqData req) throws TscpBaseException {
		
		IResData res = new TscpRes();
		try {
			String ids = (String) req.getAttr("ids");
			int isEnable = (req.getAttr("isEnable") != null && !"".equals(req.getAttr("isEnable")) && Boolean.parseBoolean((String)req.getAttr("isEnable")) ? 1 : 0);
			TscpServiceUtil.callService("enableRole", isEnable, getAjaxIds(ids));
			res.addJsonObjectOutWrite(Result.createSuccessResult());
		return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}

	@ApiOperation("新增/修改提交")
	public IResData save(IReqData req) throws TscpBaseException {
		
		IResData res = new TscpRes();
		try {
			Integer id = (req.getAttr("id") == null || "".equals(req.getAttr("id"))) ? null : Integer.parseInt((String)req.getAttr("id"));
			String name = (String)req.getAttr("name");
			Integer sort = (req.getAttr("sort") == null || "".equals(req.getAttr("sort"))) ? null : Integer.parseInt((String)req.getAttr("sort"));
			String description =  (String)req.getAttr("description");
			Boolean isEnable = (req.getAttr("isEnable") == null || "".equals(req.getAttr("isEnable"))) ? null : Boolean.parseBoolean((String)req.getAttr("isEnable"));
			if(StringUtils.isEmpty(name) || sort == null || isEnable == null) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
			Role role;
			if (id == null) {
				role = new Role();
			}else {
				role = (Role) TscpServiceUtil.callService("getRoleById", id);
			}
			role.setName(name);
			role.setSort(sort);
			role.setDescription(description);
			role.setIsEnable(isEnable);
			TscpServiceUtil.callService("saveRoleService", role);
			res.addJsonObjectOutWrite(Result.createSuccessResult());
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}
	
	
	@ApiOperation("删除")
	public IResData delete(IReqData req) throws TscpBaseException {
		
		IResData res = new TscpRes();
		try {
			String ids = (String)req.getAttr("ids");
			if(StringUtils.isEmpty(ids)) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
			TscpServiceUtil.callService("deleteRoleByIds", getAjaxIds(ids));
			res.addJsonObjectOutWrite(Result.createSuccessResult());
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}
}