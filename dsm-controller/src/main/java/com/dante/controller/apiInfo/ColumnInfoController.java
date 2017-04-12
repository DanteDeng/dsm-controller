package com.dante.controller.apiInfo;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dante.biz.apiInfo.service.IColumnInfoService;
import com.dante.common.util.RequestUtil;
import com.dante.controller.BaseController;
import com.dante.model.apiInfo.ColumnInfo;
import com.dante.model.base.CommonResult;
@Controller
@RequestMapping("columnInfo")
public class ColumnInfoController extends BaseController {
	
	@Autowired
	private IColumnInfoService columnInfoService;
	
	/*@RequestMapping("queryApiInfoList")
	@ResponseBody
	public CommonResult queryApiInfoList(HttpServletRequest request){
		//获得入参
		ApiInfoParam param = RequestUtil.request2Bean(request, ApiInfoParam.class);
		logger.debug("[apiInfo-----queryApiInfoList-----param]"+param);
		CommonResult result = new CommonResult();
		try{
			List<ApiInfo> apiInfoList = columnInfoService.queryApiInfoList(param);
			result.setData(apiInfoList);
			result.setCode(CommonResult.RESULT_SECCESS);
		}catch(Exception e){
			logger.error("[apiInfo-----queryApiInfoList-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[apiInfo-----queryApiInfoList-----result]"+result);
		return result;
	}*/
	
	@RequestMapping("queryColumnInfoDetail")
	@ResponseBody
	public CommonResult queryColumnInfoDetail(HttpServletRequest request){
		//获得入参
		ColumnInfo columnInfo = RequestUtil.request2Bean(request, ColumnInfo.class);
		logger.debug("[columnInfo-----queryColumnInfoDetail-----param]"+columnInfo);
		CommonResult result = new CommonResult();
		try{
			ColumnInfo record = columnInfoService.queryColumnInfoDetail(columnInfo);
			result.setData(record);
			result.setCode(CommonResult.RESULT_SECCESS);
		}catch(Exception e){
			logger.error("[columnInfo-----queryColumnInfoDetail-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[columnInfo-----queryColumnInfoDetail-----result]"+result);
		return result;
	}
	
	/*@RequestMapping("pageApiInfoList")
	@ResponseBody
	public CommonResult pageApiInfoList(HttpServletRequest request){
		//获得入参
		ApiInfoParam param = RequestUtil.request2Bean(request, ApiInfoParam.class);
		logger.debug("[apiInfo-----pageApiInfoList-----param]"+param);
		CommonResult result = new CommonResult();
		try{
			PageInfo<ApiInfo> apiInfoList = apiInfoService.pageApiInfoList(param);
			result.setData(apiInfoList);
			result.setCode(CommonResult.RESULT_SECCESS);
		}catch(Exception e){
			logger.error("[apiInfo-----pageApiInfoList-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[apiInfo-----pageApiInfoList-----result]"+result);
		return result;
	}
	
	@RequestMapping("saveApiInfo")
	@ResponseBody
	public CommonResult saveApiInfo(HttpServletRequest request){
		//获得入参
		ApiInfo apiInfo = RequestUtil.request2Bean(request, ApiInfo.class);
		apiInfo.setParamInList(RequestUtil.convertBase64String2List(request, ApiColumn.class, "paramInListBase64"));
		apiInfo.setParamOutList(RequestUtil.convertBase64String2List(request, ApiColumn.class, "paramOutListBase64"));
		logger.debug("[apiInfo-----saveApiInfo-----param]"+apiInfo);
		CommonResult result = new CommonResult();
		try{
			result = apiInfoService.saveApiInfo(apiInfo);
		}catch(Exception e){
			logger.error("[apiInfo-----saveApiInfo-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[apiInfo-----saveApiInfo-----result]"+result);
		return result;
	}
	
	@RequestMapping("deleteApiInfo")
	@ResponseBody
	public CommonResult deleteApiInfo(HttpServletRequest request){
		//获得入参
		ApiInfo apiInfo = RequestUtil.request2Bean(request, ApiInfo.class);
		logger.debug("[apiInfo-----deleteApiInfo-----param]"+apiInfo);
		CommonResult result = new CommonResult();
		try{
			result = apiInfoService.deleteApiInfo(apiInfo);
		}catch(Exception e){
			logger.error("[apiInfo-----deleteApiInfo-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[apiInfo-----deleteApiInfo-----result]"+result);
		return result;
	}*/
	
}
