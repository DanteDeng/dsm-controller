package com.dante.controller.apiInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dante.biz.apiInfo.service.IApiInfoService;
import com.dante.common.util.DocUtil;
import com.dante.common.util.RequestUtil;
import com.dante.controller.BaseController;
import com.dante.model.apiInfo.ApiColumn;
import com.dante.model.apiInfo.ApiInfo;
import com.dante.model.apiInfo.ApiInfoParam;
import com.dante.model.base.CommonResult;
import com.github.pagehelper.PageInfo;
@Controller
@RequestMapping("apiInfo")
public class ApiInfoController extends BaseController {
	
	@Value("${dante.fileConfig.docTempFilePath}")
	private String filePath;
	
	@Autowired
	private IApiInfoService apiInfoService;
	
	@RequestMapping("queryApiInfoList")
	@ResponseBody
	public CommonResult queryApiInfoList(HttpServletRequest request){
		//获得入参
		ApiInfoParam param = RequestUtil.request2Bean(request, ApiInfoParam.class);
		logger.debug("[apiInfo-----queryApiInfoList-----param]"+param);
		CommonResult result = new CommonResult();
		try{
			List<ApiInfo> apiInfoList = apiInfoService.queryApiInfoList(param);
			result.setData(apiInfoList);
			result.setCode(CommonResult.RESULT_SECCESS);
		}catch(Exception e){
			logger.error("[apiInfo-----queryApiInfoList-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[apiInfo-----queryApiInfoList-----result]"+result);
		return result;
	}
	
	@RequestMapping("queryApiInfoDetail")
	@ResponseBody
	public CommonResult queryApiInfoDetail(HttpServletRequest request){
		//获得入参
		ApiInfo apiInfo = RequestUtil.request2Bean(request, ApiInfo.class);
		logger.debug("[apiInfo-----queryApiInfoDetail-----param]"+apiInfo);
		CommonResult result = new CommonResult();
		try{
			ApiInfo record = apiInfoService.queryApiInfoDetail(apiInfo);
			result.setData(record);
			result.setCode(CommonResult.RESULT_SECCESS);
		}catch(Exception e){
			logger.error("[apiInfo-----queryApiInfoDetail-----error]",e);
			result.setMessage(CommonResult.SYSTEM_ERROR);
			result.setCode(CommonResult.RESULT_FAIL);
		}
		logger.debug("[apiInfo-----queryApiInfoDetail-----result]"+result);
		return result;
	}
	
	@RequestMapping("pageApiInfoList")
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
	}
	
	@RequestMapping("exportApiInfoDoc")
	public void exportApiInfoDoc(HttpServletRequest request, HttpServletResponse response) {
		//获得入参
		ApiInfo apiInfo = RequestUtil.request2Bean(request, ApiInfo.class);
		logger.debug("[apiInfo-----exportApiInfoDoc-----param]"+apiInfo);
		// 否则Freemarker的模板殷勤在处理时可能会因为找不到值而报错 这里暂时忽略这个步骤了
		try {
			ApiInfo record = apiInfoService.queryApiInfoDetail(apiInfo);
			Map<String,String> data = new HashMap<String, String>();
			data.put("apiInfoName", record.getApiInfoName());
			data.put("apiInfoDescription", record.getApiInfoDescription());
			data.put("apiInfoUrl", record.getApiInfoUrl());
			data.put("paramInList", getColumnString(record.getParamInList()));
			data.put("paramOutList", getColumnString(record.getParamOutList()));
			// 调用工具类WordGenerator的createDoc方法生成Word文档
			String fileName = record.getApiInfoName()+".doc";
			DocUtil.createDoc(fileName,data,"resume",response);
			logger.debug("[apiInfo-----exportApiInfoDoc-----success]");
		} catch (Exception e) {
			logger.error("[apiInfo-----exportApiInfoDoc-----error]",e);
		} 
	}
	
	private String getColumnString(List<ApiColumn> columnList){
		if(columnList!=null){
			String columnString = "";
			for (ApiColumn apiColumn : columnList) {
				columnString = columnString + apiColumn.getColumnInfoId()+"\t\t"	//字段名称与字段描述设置制表符分隔
								+apiColumn.getColumnInfoName()+"<w:br />";			//设置换行
			}
			return columnString;
		}
		return null;
	}
	
}
