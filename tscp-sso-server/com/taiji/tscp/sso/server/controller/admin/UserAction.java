package com.taiji.tscp.sso.server.controller.admin;

import java.util.Date;
import java.util.List;
import com.alibaba.fastjson.JSONObject;
import com.taiji.tscp.api.exception.TscpBaseException;
import com.taiji.tscp.api.kernel.IReqData;
import com.taiji.tscp.api.kernel.IResData;
import com.taiji.tscp.common.annotation.Action;
import com.taiji.tscp.kernel.util.TscpServiceUtil;
import com.taiji.tscp.mvc.config.ConfigUtils;
import com.taiji.tscp.mvc.exception.ValidateException;
import com.taiji.tscp.mvc.model.Pagination;
import com.taiji.tscp.mvc.model.Result;
import com.taiji.tscp.mvc.model.ResultCode;
import com.taiji.tscp.mvc.util.StringUtils;
import com.taiji.tscp.platform.web.event.TscpRes;
import com.taiji.tscp.sso.server.controller.common.BaseController;
import com.taiji.tscp.sso.server.enums.TrueFalseEnum;
import com.taiji.tscp.sso.server.model.Role;
import com.taiji.tscp.sso.server.model.User;
import com.taiji.tscp.sso.server.model.UserRole;
import com.taiji.tscp.sso.server.provider.PasswordProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Joe
 */
@Api(tags = "用户管理")
@Action
public class UserAction extends BaseController {


	public IResData user(IReqData req) {
		IResData res = new TscpRes();
		try {
			req.getHttpReq().getRequestDispatcher("/admin/user.jsp").forward(req.getHttpReq(), req.getHttpRes());
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			return res;
		}
	}

	@ApiOperation("新增/修改页")
	public IResData edit(IReqData req) {
		IResData res = new TscpRes();
		try {
			Long id = StringUtils.isBlank((String)req.getAttr("id"))? null : Long.parseLong((String)req.getAttr("id"));
			User user;
			if (id == null) {
				user = new User();
			}else {
				user = (User) TscpServiceUtil.callService("getUserById", id);
			}
			req.setAttr("user", user);
			req.setAttr("roleList", getRoleList(id));
			req.getHttpReq().getRequestDispatcher("/admin/userEdit.jsp").forward(req.getHttpReq(), req.getHttpRes());
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonStringOutWrite(JSONObject.toJSONString(Result.create(ResultCode.APPLICATION_ERROR)));
			return res;
		}
	}

	@SuppressWarnings("unchecked")
	@ApiOperation("列表")
	public IResData list(IReqData req) {
		IResData res = new TscpRes();
		try {
			String account  = (String) req.getAttr("account");
			Integer pageNo = Integer.parseInt((String)req.getAttr("pageNo"));
			Integer pageSize = Integer.parseInt((String) req.getAttr("pageSize"));
			res.addJsonObjectOutWrite(Result.createSuccessResult().setData((Pagination<User>)TscpServiceUtil.callService("findUserPaginationByAccount", account,pageNo,pageSize)));
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
		
	}

	@ApiOperation("验证登录名")
	public IResData validateAccount(IReqData req) {
		IResData res = new TscpRes();
		try {
			Integer id = (req.getAttr("id") == null || "".equals(req.getAttr("id"))) ? null : Integer.parseInt((String)req.getAttr("id"));
			String account = (String)req.getAttr("account");
			Result result = Result.createSuccessResult();
			User user = (User) TscpServiceUtil.callService("findUserByAccount", account);
			if (null != user && !user.getId().equals(id)) {
				result.setCode(ResultCode.ERROR).setMessage("登录名已存在");
			}
			res.addJsonObjectOutWrite(result);
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@ApiOperation("启用/禁用")
	public IResData enable(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			String ids = (String)req.getAttr("ids");
			Boolean isEnable = (req.getAttr("isEnable") == null || "".equals(req.getAttr("isEnable")) || !Boolean.parseBoolean((String)req.getAttr("isEnable"))) ? false : true;
			TscpServiceUtil.callService("enableUser",isEnable, getAjaxIds(ids));
			res.addJsonObjectOutWrite(Result.createSuccessResult());
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
			String account = (String)req.getAttr("account");
			String password = (String)req.getAttr("password");
			Boolean isEnable = ("".equals((String)req.getAttr("isEnable")) || (String)req.getAttr("isEnable") == null) ? null : Boolean.parseBoolean((String)req.getAttr("isEnable"));
			String roleIds = (String)req.getAttr("roleIds");
			if(StringUtils.isBlank(account) || isEnable == null || StringUtils.isBlank(password)) {
				throw new ValidateException("用户信息不能为空");
			}
			User user = null;
			if (id == null) {
				user = new User();
				user.setCreateTime(new Date());
			}else {
				user = (User) TscpServiceUtil.callService("getUserById", id);
			}
			user.setAccount(account);
			if (StringUtils.isNotBlank(password)) {
				user.setPassword(PasswordProvider.encrypt(password));
			}
			user.setIsEnable(isEnable);
			TscpServiceUtil.callService("saveUserService", user,getAjaxIds(roleIds));
			res.addJsonObjectOutWrite(Result.createSuccessResult());
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}

	@ApiOperation("重置密码")
	public IResData resetPassword(IReqData req) {

		IResData res = new TscpRes();
		try {
			String ids = (String) req.getAttr("ids");
			if(ids == null || "".equals(ids)) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			}else {
				ConfigUtils.processProperties(this.getClass().getResourceAsStream("/config.properties"));
				TscpServiceUtil.callService("resetPassword", PasswordProvider.encrypt(ConfigUtils.getProperty("system.reset.password")),getAjaxIds(ids));
				res.addJsonObjectOutWrite(Result.createSuccessResult());
			}
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
			String ids = (String) req.getAttr("ids");
			if(ids == null || "".equals(ids)) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			}else {
				TscpServiceUtil.callService("deleteUserByIds", getAjaxIds(ids));
				res.addJsonObjectOutWrite(Result.createSuccessResult());
			}
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}
	
	
	/**
	 * 
	 * @param userId
	 * @return
	 * @throws TscpBaseException
	 */
	@SuppressWarnings("unchecked")
	private List<Role> getRoleList(Long userId) throws TscpBaseException {
		
		List<Role> roles = (List<Role>) TscpServiceUtil.callService("findRoleByAll", TrueFalseEnum.TRUE.getValue());
		if (userId != null) {
			for (Role role : roles) {
				UserRole userRole = (UserRole) TscpServiceUtil.callService("findByUserRoleId", userId,role.getId());
				if (null != userRole) {
					role.setIsChecked(true);
				}
				else {
					role.setIsChecked(false);
				}
			}
	}
		return roles;
	}
}