package com.taiji.tscp.sso.server.controller.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
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
import com.taiji.tscp.sso.server.model.App;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author Joe
 */
@Api(tags = "应用管理")
@Action
public class AppAction extends BaseController {


	@ApiOperation("初始页")
	public IResData app(IReqData req) throws ServletException, IOException {
		
		IResData res = new TscpRes();
		req.getHttpReq().getRequestDispatcher("/admin/app.jsp").forward(req.getHttpReq(), req.getHttpRes());
		return res;
	}

	@ApiOperation("新增/修改页")
	public IResData edit(IReqData req) throws TscpBaseException, ServletException, IOException {
		
		IResData res = new TscpRes();
		Integer id = (req.getAttr("id") == null || "".equals(req.getAttr("id"))) ? null : Integer.parseInt((String)req.getAttr("id"));
		App app = null;
		if (id == null) {
			app = new App();
		}
		else {
			List<Integer> ids = new ArrayList<Integer>();
			ids.add(id);
			@SuppressWarnings("unchecked")
			List<App> apps = (List<App>) TscpServiceUtil.callService("findAppByAppIds", ids);
			if(apps != null && apps.size() > 0) {
				app = apps.get(0);
			}
		}
		req.getHttpReq().setAttribute("app", app);
		req.getHttpReq().getRequestDispatcher("/admin/appEdit.jsp").forward(req.getHttpReq(), req.getHttpRes());
		return res;
	}

	@ApiOperation("列表")
	public IResData list(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			String name = (String) req.getAttr("name");
			Integer pageNo = (req.getAttr("pageNo") == null || "".equals(req.getAttr("pageNo"))) ? null : Integer.parseInt((String)req.getAttr("pageNo"));
			Integer pageSize = (req.getAttr("pageSize") == null || "".equals(req.getAttr("pageSize"))) ? null : Integer.parseInt((String)req.getAttr("pageSize"));
			if(pageNo == null || pageSize == null) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
			res.addJsonObjectOutWrite(Result.createSuccessResult().setData(TscpServiceUtil.callService("findAppPaginationByName", name,pageNo,pageSize)));
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
		}
		return res;
	}

	@ApiOperation("验证应用编码")
	public IResData validateCode(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			Integer id = (req.getAttr("id") == null || "".equals(req.getAttr("id"))) ? null : Integer.parseInt((String)req.getAttr("id"));
			String code = (String) req.getAttr("code");
			if(StringUtils.isEmpty(code)) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
			Result result = Result.createSuccessResult();
			App db = (App) TscpServiceUtil.callService("findAppByCode", code);
			if (null != db && !db.getId().equals(id)) {
				result.setCode(ResultCode.ERROR).setMessage("应用编码已存在");
			}
			res.addJsonObjectOutWrite(result);
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
		}
		return res;
	}

	@ApiOperation("启用/禁用")
	public IResData enable(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			String ids = (String)req.getAttr("ids");
			Boolean isEnable =  (req.getAttr("isEnable") == null || "".equals(req.getAttr("isEnable"))) ? false : Boolean.parseBoolean((String)req.getAttr("isEnable"));
			if(StringUtils.isEmpty(ids) || isEnable == null) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
			TscpServiceUtil.callService("enableApp", isEnable?1:0,getAjaxIds(ids));
			res.addJsonObjectOutWrite(Result.createSuccessResult());
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	@ApiOperation("新增/修改提交")
	public IResData save(IReqData req) {
		
		IResData res = new TscpRes();
		try {
			Integer id = (req.getAttr("id") == null || "".equals(req.getAttr("id"))) ? null : Integer.parseInt((String)req.getAttr("id"));
			String name = (String) req.getAttr("name");
			String code = (String) req.getAttr("code");
			Boolean isEnable =  (req.getAttr("isEnable") == null || "".equals(req.getAttr("isEnable"))) ? false : Boolean.parseBoolean((String)req.getAttr("isEnable"));
			Integer sort = (req.getAttr("sort") == null || "".equals(req.getAttr("sort"))) ? null : Integer.parseInt((String)req.getAttr("sort"));
			if(StringUtils.isEmpty(name) || StringUtils.isEmpty(code) || isEnable == null || sort == null) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
		
			App app = null;
			if (id == null) {
				app = new App();
				app.setCreateTime(new Date());
			}else {
				List<Integer> ids = new ArrayList<Integer>();
				ids.add(id);
				List<App> apps = (List<App>) TscpServiceUtil.callService("findAppByAppIds", ids);
				if(apps != null && apps.size() > 0) {
					app = apps.get(0);
				}
			}
			app.setName(name);
			app.setSort(sort);
			app.setIsEnable(isEnable);
			app.setCode(code);
			if(id == null) {
				TscpServiceUtil.callService("saveAppService", app);
			}else {
				TscpServiceUtil.callService("updateAppService", app);
			}
			res.addJsonObjectOutWrite(Result.createSuccessResult());
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
			String ids = (String)req.getAttr("ids");
			if(StringUtils.isEmpty(ids)) {
				res.addJsonObjectOutWrite(Result.create(ResultCode.VALIDATE_ERROR));
				return res;
			}
			TscpServiceUtil.callService("deleteAppById", getAjaxIds(ids));
			res.addJsonObjectOutWrite(Result.createSuccessResult());
			return res;
		}catch(Exception e) {
			e.printStackTrace();
			res.addJsonObjectOutWrite(Result.create(ResultCode.APPLICATION_ERROR));
			return res;
		}
	}
}