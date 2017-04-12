package com.dante.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.dante.model.role.Role;
import com.dante.model.user.User;
import com.dante.biz.user.service.IUserService;
import com.dante.controller.BaseController;
import com.dante.common.util.RequestUtil;
import com.dante.common.util.StrUtil;
import com.dante.model.base.CommonResult;
import com.github.pagehelper.PageInfo;

@Controller
@RequestMapping("user")
public class UserController extends BaseController{
	
	@Autowired
	private IUserService userService;
	
	@RequestMapping("queryUserDetail")
	@ResponseBody
	public CommonResult queryUserDetail(HttpServletRequest request){
		CommonResult result = new CommonResult();
		User u = RequestUtil.requestToBean(request, User.class);
		logger.debug("/user/queryUserDetail ------>params : "+u);
		try {
			User user = userService.getUserById(u);
			result.setData(user);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/user/queryUserDetail ------>Exception : ", e);
		}
		logger.debug("/user/queryUserDetail ------>result : "+result);
		return result;
	}
	
	@RequestMapping("queryUserList")
	@ResponseBody
	public CommonResult queryUserList(HttpServletRequest request){
		CommonResult result = new CommonResult();
		Map<String,Object> params = new HashMap<String,Object>();
		logger.debug("/user/queryUserList ------>params : "+params);
		try {
			List<User> userList = userService.queryUserList(params);
			result.setData(userList);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/user/queryUserList ------>Exception : ", e);
		}
		logger.debug("/user/queryUserList ------>result : "+result);
		return result;
	}
	
	@RequestMapping("pageUserList")
	@ResponseBody
	public CommonResult pageUserList(HttpServletRequest request){
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
		logger.debug("/user/pageUserList ------>params : "+params);
		try {
			PageInfo<User> page = userService.pageUserList(params);
			result.setData(page);
			result.setCode(CommonResult.RESULT_SECCESS);
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/user/pageUserList ------>Exception : ", e);
		}
		logger.debug("/user/pageUserList ------>result : "+result);
		return result;
	}
	
	@RequestMapping("editUser")
	@ResponseBody
	public CommonResult editUser(HttpServletRequest request){
		CommonResult result = new CommonResult();
		User user = RequestUtil.requestToBean(request, User.class);
		String roleJson = request.getParameter("roleJson");
		if(StrUtil.isNotEmpty(roleJson)){
			user.setRoleList(JSONObject.parseArray(roleJson, Role.class));
		}
		logger.debug("/user/editUser -----> user: "+user);
		try {
			if(user.getUserId()==null||"".equals(user.getUserId())){
				userService.addUser(user);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.INSERT_SUCCESS);
			}else{
				userService.updateUser(user);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.UPDATE_SUCCESS);
			}
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/user/editUser -----> Exception: ", e);
		}
		logger.debug("/user/editUser -----> result: "+result);
		return result;
	}
	
	@RequestMapping("deleteUser")
	@ResponseBody
	public CommonResult deleteUser(HttpServletRequest request){
		CommonResult result = new CommonResult();
		User user = RequestUtil.requestToBean(request, User.class);
		logger.debug("/user/deleteUser -----> user: "+user);
		try {
			if(user.getUserId()==null||"".equals(user.getUserId())){
				result.setCode(CommonResult.RESULT_FAIL);
				result.setMessage(CommonResult.DELETE_NO_VALUE);
			}else{
				userService.deleteUser(user);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.DELETE_SUCCESS);
			}
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/user/deleteUser -----> Exception: ", e);
		}
		logger.debug("/user/deleteUser -----> result: "+result);
		return result;
	}
	
	@RequestMapping("userLogin")
	@ResponseBody
	public CommonResult userLogin(HttpServletRequest request){
		CommonResult result = new CommonResult();
		User user = RequestUtil.requestToBean(request, User.class);
		logger.debug("/user/userLogin -----> user: "+user);
		try {
			user = userService.getUserLogin(user);
			if(user==null){
				result.setCode(CommonResult.RESULT_FAIL);
				result.setMessage(CommonResult.NO_SUCH_USER);
			}else{
				request.getSession().setAttribute("user", user);
				request.getSession().setAttribute("userPermissionList", null);
				result.setData(user);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.LOGIN_SUCCESS);
			}
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/user/userLogin -----> Exception: ", e);
		}
		logger.debug("/user/userLogin -----> result: "+result);
		return result;
	}
	
	@RequestMapping("userLogout")
	@ResponseBody
	public CommonResult userLogout(HttpServletRequest request){
		CommonResult result = new CommonResult();
		User user = RequestUtil.requestToBean(request, User.class);
		logger.debug("/user/userLogout -----> user: "+user);
		try {
			user = userService.getUserById(user);
			if(user==null){
				result.setCode(CommonResult.RESULT_FAIL);
				result.setMessage(CommonResult.NO_SUCH_USER);
			}else{
				request.getSession().setAttribute("user", null);
				request.getSession().setAttribute("userPermissionList", null);
				result.setCode(CommonResult.RESULT_SECCESS);
				result.setMessage(CommonResult.LOGOUT_SUCCESS);
			}
		} catch (Exception e) {
			result.setCode(CommonResult.RESULT_FAIL);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			logger.error("/user/userLogout -----> Exception: ", e);
		}
		logger.debug("/user/userLogout -----> result: "+result);
		return result;
	}

}
