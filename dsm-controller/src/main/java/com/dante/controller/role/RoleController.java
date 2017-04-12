package com.dante.controller.role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dante.model.permission.Permission;
import com.dante.model.role.Role;
import com.dante.biz.role.service.IRoleService;
import com.dante.controller.BaseController;
import com.dante.common.util.RequestUtil;
import com.dante.common.util.StrUtil;
import com.dante.model.base.CommonResult;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("role")
public class RoleController extends BaseController{
	
	@Autowired
	private IRoleService roleService;
	
	@RequestMapping("queryRoleDetail")
	@ResponseBody
	public CommonResult queryRoleDetail(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Role r = RequestUtil.requestToBean(request, Role.class);
		logger.debug("/role/queryRoleDetail ------>params : "+r);
		try {
			Role role = roleService.getRoleById(r);
			result.setData(role);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/role/queryRoleDetail ------>Exception : ", e);
		}
		logger.debug("/role/queryRoleDetail ------>result : "+result);
		return result;
	}
	
	@RequestMapping("queryRoleList")
	@ResponseBody
	public CommonResult queryRoleList(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Map<String,Object> params = new HashMap<String,Object>();
		logger.debug("/role/queryRoleList ------>params : "+result);
		try {
			List<Role> roleList = roleService.queryRoleList(params);
			result.setData(roleList);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/role/queryRoleList ------>Exception : ", e);
		}
		logger.debug("/role/queryRoleList ------>result : "+result);
		return result;
	}
	
	@RequestMapping("pageRoleList")
	@ResponseBody
	public CommonResult pageRoleList(HttpServletRequest request){
		CommonResult result = new CommonResult();
		String pn = request.getParameter("pageNum");
		String ps = request.getParameter("pageSize");
		int pageNum = 0;
		int pageSize = 0;
		try{
			pageNum = Integer.parseInt(pn);
		}catch(Exception e){}
		try{
			pageSize = Integer.parseInt(ps);
		}catch(Exception e){}
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("pageNum", pageNum);
		params.put("pageSize", pageSize);
		logger.debug("/role/pageRoleList ------>params : "+params);
		try {
			PageInfo<Role> page = roleService.pageRoleList(params);
			result.setData(page);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/role/pageRoleList ------>Exception : ", e);
		}
		logger.debug("/role/pageRoleList ------>result : "+result);
		return result;
	}
	
	@RequestMapping("editRole")
	@ResponseBody
	public CommonResult editRole(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Role role = RequestUtil.requestToBean(request, Role.class);
		String permissionJson = request.getParameter("permissionJson");
		if(StrUtil.isNotEmpty(permissionJson)){
			role.setPermissionList(JSONObject.parseArray(permissionJson, Permission.class));
		}
		logger.debug("/role/editRole -----> role: "+role);
		try {
			if(role.getRoleId()==null||"".equals(role.getRoleId())){
				roleService.addRole(role);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.INSERT_SUCCESS);
			}else{
				roleService.updateRole(role);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.UPDATE_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("/Role/editRole -----> Exception: ", e);
		}
		logger.debug("/Role/editRole -----> result: "+result);
		return result;
	}
	
	@RequestMapping("deleteRole")
	@ResponseBody
	public CommonResult deleteRole(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Role role = RequestUtil.requestToBean(request, Role.class);
		logger.debug("/role/deleteRole -----> role: "+role);
		try {
			if(role.getRoleId()==null||"".equals(role.getRoleId())){
				result.setCode(CommonResult.RESULT_FAIL);
				result.setMessage(CommonResult.DELETE_NO_VALUE);
			}else{
				roleService.deleteRole(role);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.DELETE_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("/role/deleteRole -----> Exception: ", e);
		}
		logger.debug("/role/deleteRole -----> result: "+result);
		return result;
	}

}
