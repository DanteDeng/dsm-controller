var curr = 1;
$(function() {
	load(curr);
	//getAllRole();
})

function load(curr) {
	$.ajax({
		url : "/dsm-controller/apiInfo/pageApiInfoList",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : {
			"pageNum" : curr,
			"pageSize" : 5
		},
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			var html = "";
			$.each(
					result.data.list,
					function(i, item) {
						html += " <tr> "
							+ " <td>"
							+ item.apiInfoName
							+ "</td> "
							+ " <td>"
							+ item.apiInfoDescription
							+ "</td> "
							+ " <td>"
							+ item.apiInfoUrl
							+ "</td> "
							+ " <td>"
							+ item.createBy
							+ "</td> "
							+ " <td>"
							+ parent.formatDate(item.createDate,'yyyy-MM-dd hh:mm:ss')
							+ "</td> "
							+ " <td>"
							+ item.updateBy
							+ "</td> "
							+ " <td>"
							+ parent.formatDate(item.updateDate,'yyyy-MM-dd hh:mm:ss')
							+ "</td> "
							+ " <td><a class=\"btn btn-info\" onclick='exportDoc(\""
							+ item.apiInfoId
							+ "\");'>导出word</a>&nbsp;&nbsp;<a class=\"btn btn-info\" onclick='openedt(\""
							+ item.apiInfoId
							+ "\");'>修改</a>&nbsp;&nbsp;<a class=\"btn btn-warning\" onclick='del(\""
							+ item.apiInfoId
							+ "\");'>删除</a></td> "
							+ " </tr>";
					})
					$("#tbody").html(html.replace(/null/g, ''));
			initPage("page", result.data)
		}
	})
}

function openadd() {
	$("#saveModalLabel").text("新增接口信息");
	$("#saveModal input").val("");
	$("#saveModal textarea").val("");
	$("#saveModal select option").each(function(index, element) {
		element.selected=false;
	})
	$("#paramInModal tbody").html('');
	$("#saveModal").modal("show");
	$("#save").show();
}

function save() {
	if ($("#apiInfoName").val() == "") {
		layer.tips('不能为空', '#apiInfoName');
		return;
	}
	if ($("#apiInfoUrl").val() == "") {
		layer.tips('不能为空', '#apiInfoUrl');
		return;
	}
	var formdata = {
		apiInfoId : $("#apiInfoId").val(),
		apiInfoName : $("#apiInfoName").val(),
		apiInfoUrl : $("#apiInfoUrl").val(),
		apiInfoDescription : $("#apiInfoDescription").val(),
		paramInListBase64 : getColumnListBase64('0'),
		paramOutListBase64 : getColumnListBase64('1')
	}
	$.ajax({
		url : "/dsm-controller/apiInfo/saveApiInfo",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : formdata,
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			$("#saveModal").modal("hide");
			var message = result.message;
			layer.alert(message);
			load(curr);
		}
	})
}

function openedt(apiInfoId) {
	$.ajax({
		url : "/dsm-controller/apiInfo/queryApiInfoDetail",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : {
			"apiInfoId" : apiInfoId
		},
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			var data = result.data;
			$("#saveModalLabel").text("修改接口信息");
			$("#apiInfoId").val(apiInfoId);
			$("#apiInfoName").val(data.apiInfoName);
			$("#apiInfoUrl").val(data.apiInfoUrl);
			$("#apiInfoDescription").val(data.apiInfoDescription);
			$("#paramInModal tbody").html('');
			initColumnList('0',data.paramInList);
			initColumnList('1',data.paramOutList);
			$("#save").show();
			$("#saveModal").modal("show");
		}
	})
}


function del(apiInfoId) {
	// 询问框
	layer.confirm('您确定要删除？', {
		btn : [ '确定', '取消' ]
	// 按钮
	}, function() {
		$.ajax({
			url : "/dsm-controller/apiInfo/deleteApiInfo",
			timeout : 300000,
			dataType : "json",
			type : "post",
			data : {
				"apiInfoId" : apiInfoId
			},
			success : function(result) {
				var code = result.code;
				if (code != '0') {
					layer.alert(result.message);
					return;
				}
				var msg = result.message;
				layer.alert(msg);
				load(curr);
			}
		})
	}, function() {
		layer.close();
	});

}

function editColumn(apiColumnType) {
	if(apiColumnType=='0'){
		$("#paramInModalLabel").text("编辑入参");
		$("#saveModal").modal("hide");
		$("#paramInModal").modal("show");
	}else if(apiColumnType=='1'){
		$("#paramOutModalLabel").text("编辑出参");
		$("#saveModal").modal("hide");
		$("#paramOutModal").modal("show");
	}
	
}

function closeColumnEdit() {
	$("#saveModal").modal("show");
	$("#paramInModal").modal("hide");
	$("#paramOutModal").modal("hide");
}

function addColumn(apiColumnType) {
	var flag = '';
	if(apiColumnType=='0'){
		flag = 'paramIn';
	}else if(apiColumnType=='1'){
		flag = 'paramOut';
	}
	var columnInfoId = $("#"+flag+"Modal #columnInfoId").val();
	var columnInfoName = $("#"+flag+"Modal #columnInfoName").val();
	if (columnInfoId == "") {
		layer.tips('不能为空', '#'+flag+'Modal #columnInfoId');
		return;
	}
	if (columnInfoName == "") {
		layer.tips('不能为空', '#'+flag+'Modal #columnInfoName');
		return;
	}
	var columnList = getColumnList(apiColumnType);
	var returnFlag = false;
	for(var i=0;i<columnList.length;i++){
		if(columnInfoId==columnList[i].columnInfoId){
			returnFlag = true;
		}
	}
	if(returnFlag){
		layer.tips('名称重复', '#'+flag+'Modal #addColumn');
		return;
	}
	var columnInfo = '<tr><td>'
			+ columnInfoId
			+ '</td><td>'
			+ columnInfoName
			+ '</td><td><a class="btn btn-primary" onclick="deleteColumn(this)">删除</a></td></tr>';
	$("#"+flag+"Tbody").append(columnInfo);
}

function deleteColumn(element){
	$(element).parent().parent().remove();
}

function getColumnListBase64(apiColumnType){
	var columnList = getColumnList(apiColumnType);
	var columnListJson = JSON.stringify(columnList);
	var columnListBase64 = $.base64.encode(columnListJson,'utf-8');
	return columnListBase64;
}

function getColumnList(apiColumnType){
	var flag = '';
	if(apiColumnType=='0'){
		flag = 'paramIn';
	}else if(apiColumnType=='1'){
		flag = 'paramOut';
	}
	var columnList = [];
	$("#"+flag+"Modal #"+flag+"Tbody tr").each(function(i,e){
		var columnInfoId = $(e).children().eq(0).html().trim();
		var columnInfoName = $(e).children().eq(1).html().trim();
		var columnInfo = {
				columnInfoId:columnInfoId,
				columnInfoName:columnInfoName,
				apiColumnType:apiColumnType
		};
		columnList.push(columnInfo);
	});
	return columnList;
}

function initColumnList(apiColumnType,columnList) {
	var flag = '';
	if(apiColumnType=='0'){
		flag = 'paramIn';
	}else if(apiColumnType=='1'){
		flag = 'paramOut';
	}
	if(columnList!=null){
		for(var i=0;i<columnList.length;i++){
			var columnInfo = '<tr><td>'
				+ columnList[i].columnInfoId
				+ '</td><td>'
				+ columnList[i].columnInfoName
				+ '</td><td><a class="btn btn-primary" onclick="deleteColumn(this)">删除</a></td></tr>';
			$("#"+flag+"Tbody").append(columnInfo);
		}
	}
}

$("#paramInModal,#paramOutModal").on('blur','#columnInfoId',function(){
	var columnInfoId = $(this).val();
	var columnInfoName = $(this).parent().parent().siblings().find("#columnInfoName").val();
	if(columnInfoId==''||columnInfoName!=''){
		return;
	}
	var columnInfo = getColumnInfo(columnInfoId);
	if(columnInfo!=null){
		$(this).parent().parent().siblings().find("#columnInfoName").val(columnInfo.columnInfoName);
	}
})

function getColumnInfo(columnInfoId) {
	var columnInfo = null; 
	$.ajax({
		url : "/dsm-controller/columnInfo/queryColumnInfoDetail",
		timeout : 300000,
		dataType : "json",
		type : "post",
		async : false,
		data : {columnInfoId:columnInfoId},
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
			}else{
				columnInfo = result.data;
			}
			
		}
	})
	return columnInfo;
}

function exportDoc(apiInfoId){
	$("#apiInfoIdInForm").val(apiInfoId);
	$("#exportDocForm")[0].action="/dsm-controller/apiInfo/exportApiInfoDoc";
	$("#exportDocForm")[0].method="POST";
	$("#exportDocForm")[0].submit();
}
