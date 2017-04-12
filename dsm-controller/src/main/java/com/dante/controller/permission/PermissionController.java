package com.dante.controller.permission;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dante.model.permission.Permission;
import com.dante.biz.permission.service.IPermissionService;
import com.dante.controller.BaseController;
import com.dante.common.util.RequestUtil;
import com.dante.model.base.CommonResult;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("permission")
public class PermissionController extends BaseController{
	
	@Autowired
	private IPermissionService permissionService;
	
	@RequestMapping("queryPermissionDetail")
	@ResponseBody
	public CommonResult queryRoleDetail(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Permission p = RequestUtil.requestToBean(request, Permission.class);
		logger.debug("/role/queryPermissionDetail ------>params : "+p);
		try {
			Permission permission = permissionService.getPermissionById(p);
			result.setData(permission);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setData(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
			logger.error("/role/queryPermissionDetail ------>Exception : ",e);
		}
		logger.debug("/role/queryPermissionDetail ------>result : "+result);
		return result;
	}
	
	@RequestMapping("queryPermissionList")
	@ResponseBody
	public CommonResult queryPermissionList(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Map<String,Object> params = new HashMap<String,Object>();
		logger.debug("/role/queryPermissionList ------>params : "+params);
		try {
			List<Permission> roleList = permissionService.queryPermissionList(params);
			result.setData(roleList);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setData(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
			logger.error("/role/queryPermissionList ------>Exception : ",e);
		}
		logger.debug("/role/queryPermissionList ------>result : "+result);
		return result;
	}
	
	@RequestMapping("pagePermissionList")
	@ResponseBody
	public CommonResult pagePermissionList(HttpServletRequest request){
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
			PageInfo<Permission> page = permissionService.pagePermissionList(params);
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
	
	@RequestMapping("editPermission")
	@ResponseBody
	public CommonResult editPermission(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Permission permission = RequestUtil.requestToBean(request, Permission.class);
		logger.debug("/permission/editPermission -----> permission: "+permission);
		try {
			if(permission.getPermissionId()==null||"".equals(permission.getPermissionId())){
				permissionService.addPermission(permission);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.INSERT_SUCCESS);
			}else{
				permissionService.updatePermission(permission);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.UPDATE_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("/permission/editpermission -----> permission: ", e);
		}
		logger.debug("/Permission/editPermission -----> result: "+result);
		return result;
	}
	
	@RequestMapping("deletePermission")
	@ResponseBody
	public CommonResult deletePermission(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Permission permission = RequestUtil.requestToBean(request, Permission.class);
		logger.debug("/permission/deletePermission -----> permission: "+permission);
		try {
			if(permission.getPermissionId()==null||"".equals(permission.getPermissionId())){
				result.setCode(CommonResult.RESULT_FAIL);
				result.setMessage(CommonResult.DELETE_NO_VALUE);
			}else{
				permissionService.deletePermission(permission);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.DELETE_SUCCESS);
			}
		} catch (Exception e) {
			logger.error("/permission/deletePermission -----> Exception: ", e);
		}
		logger.debug("/permission/deletePermission -----> result: "+result);
		return result;
	}

}
