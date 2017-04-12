package com.dante.biz.ueditor.service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dante.model.ueditor.ActionMap;
import com.dante.model.ueditor.AppInfo;
import com.dante.model.ueditor.BaseState;
import com.dante.model.ueditor.FileType;
import com.dante.model.ueditor.State;
import com.dante.common.util.ConfigManager;
import com.dante.common.util.PathFormat;
import com.dante.common.util.StorageManager;

@Service("ueditorService")
public class UeditorService implements IUeditorService {

	@Value("${rootPath}")
	private String rootPath;

	@Value("${config.json.path}")
	private String configFilePath;

	@Autowired
	private IUeditorFileService ueditorFileService;

	private ConfigManager configManager;

	public ConfigManager getConfigManager() {
		if (configManager == null) { // 如果没有实例化过进行实例化
			String configString = readConfigFile();
			ConfigManager conf = ConfigManager.getInstance(configString);
			return conf;
		} else { // 已有实例则直接返回
			return configManager;
		}
	}

	private String readConfigFile() {
		StringBuilder builder = new StringBuilder();

		try {

			InputStreamReader reader = new InputStreamReader(new FileInputStream(configFilePath), "UTF-8");
			BufferedReader bfReader = new BufferedReader(reader);

			String tmpContent = null;

			while ((tmpContent = bfReader.readLine()) != null) {
				builder.append(tmpContent);
			}

			bfReader.close();

		} catch (UnsupportedEncodingException e) {
			// 忽略
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();

	}

	public String executeRequest(HttpServletRequest request) {

		String callbackName = request.getParameter("callback");

		if (callbackName != null) {

			if (!validCallbackName(callbackName)) {
				return new BaseState(false, AppInfo.ILLEGAL).toJSONString();
			}

			return callbackName + "(" + this.invoke(request) + ");";

		} else {
			return invoke(request);
		}

	}

	private String invoke(HttpServletRequest request) {

		String actionType = request.getParameter("action");

		if (actionType == null || !ActionMap.mapping.containsKey(actionType)) {
			return new BaseState(false, AppInfo.INVALID_ACTION).toJSONString();
		}

		if (configManager == null) {
			configManager = getConfigManager();
		}

		if (configManager == null || !configManager.valid()) {
			return new BaseState(false, AppInfo.CONFIG_ERROR).toJSONString();
		}

		State state = null;

		int actionCode = ActionMap.getType(actionType);

		Map<String, Object> conf = null;

		switch (actionCode) {

		case ActionMap.CONFIG:
			return configManager.getAllConfig().toString();

		case ActionMap.UPLOAD_IMAGE:
		case ActionMap.UPLOAD_SCRAWL:
		case ActionMap.UPLOAD_VIDEO:
		case ActionMap.UPLOAD_FILE:
			conf = configManager.getConfig(actionCode);
			conf.put("rootPath", rootPath);
			state = doUpload(request, conf);
			break;

		case ActionMap.CATCH_IMAGE:
			conf = configManager.getConfig(actionCode);
			conf.put("rootPath", rootPath);
			List<String> list = Arrays.asList(request.getParameterValues((String) conf.get("fieldName")));
			state = ueditorFileService.capture(list, conf);
			break;

		case ActionMap.LIST_IMAGE:
		case ActionMap.LIST_FILE:
			conf = configManager.getConfig(actionCode);
			conf.put("rootPath", rootPath);
			int start = this.getStartIndex(request);
			state = ueditorFileService.listFile(start, conf);
			break;

		}

		return state.toJSONString();

	}

	private int getStartIndex(HttpServletRequest request) {

		String start = request.getParameter("start");

		try {
			return Integer.parseInt(start);
		} catch (Exception e) {
			return 0;
		}

	}

	/**
	 * callback参数验证
	 */
	private boolean validCallbackName(String name) {

		if (name.matches("^[a-zA-Z_]+[\\w0-9_]*$")) {
			return true;
		}

		return false;

	}

	private State doUpload(HttpServletRequest request, Map<String, Object> conf) {
		String filedName = (String) conf.get("fieldName");
		State state = null;

		if ("true".equals(conf.get("isBase64"))) {
			state = ueditorFileService.saveBase64(request.getParameter(filedName), conf);
		} else {
			state = saveBinary(request, conf);
		}

		return state;
	}

	private State saveBinary(HttpServletRequest request, Map<String, Object> conf) {
		FileItemStream fileStream = null;
		boolean isAjaxUpload = request.getHeader("X_Requested_With") != null;

		if (!ServletFileUpload.isMultipartContent(request)) {
			return new BaseState(false, AppInfo.NOT_MULTIPART_CONTENT);
		}

		ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());

		if (isAjaxUpload) {
			upload.setHeaderEncoding("UTF-8");
		}

		try {
			FileItemIterator iterator = upload.getItemIterator(request);

			while (iterator.hasNext()) {
				fileStream = iterator.next();

				if (!fileStream.isFormField())
					break;
				fileStream = null;
			}

			if (fileStream == null) {
				return new BaseState(false, AppInfo.NOTFOUND_UPLOAD_DATA);
			}

			String savePath = (String) conf.get("savePath");
			String originFileName = fileStream.getName();
			String suffix = FileType.getSuffixByFilename(originFileName);

			originFileName = originFileName.substring(0, originFileName.length() - suffix.length());
			savePath = savePath + suffix;

			long maxSize = ((Long) conf.get("maxSize")).longValue();

			if (!validType(suffix, (String[]) conf.get("allowFiles"))) {
				return new BaseState(false, AppInfo.NOT_ALLOW_FILE_TYPE);
			}

			savePath = PathFormat.parse(savePath, originFileName);

			String physicalPath = (String) conf.get("rootPath") + savePath;

			InputStream is = fileStream.openStream();
			State storageState = StorageManager.saveFileByInputStream(is, physicalPath, maxSize);
			is.close();

			if (storageState.isSuccess()) {
				storageState.putInfo("url", PathFormat.format(savePath));
				storageState.putInfo("type", suffix);
				storageState.putInfo("original", originFileName + suffix);
			}

			return storageState;
		} catch (FileUploadException e) {
			return new BaseState(false, AppInfo.PARSE_REQUEST_ERROR);
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	private static boolean validType(String type, String[] allowTypes) {
		List<String> list = Arrays.asList(allowTypes);

		return list.contains(type);
	}
}