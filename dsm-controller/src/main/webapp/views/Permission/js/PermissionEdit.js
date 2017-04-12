var curr = 1;
$(function() {
	load(curr);
})

function load(curr) {
	$.ajax({
		url : "/dsm-controller/permission/pagePermissionList",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : {
			"flag" : "load",
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
						html += " <tr> " + " <td>"
								+ item.permissionName
								+ "</td> "
								+ " <td>"
								+ item.permissionType
								+ "</td> "
								+ " <td>"
								+ item.permissionUrl
								+ "</td> "
								+ " <td>"
								+ item.permissionDesc
								+ "</td> "
								+ " <td>"
								+ item.isMenu
								+ "</td> "
								+ " <td>"
								+ item.menuName
								+ "</td> "
								+ " <td>"
								+ item.menuNo
								+ "</td> "
								+ " <td>"
								+ item.parentMenu
								+ "</td> "
								+ " <td><a class=\"btn btn-info\" onclick='openedt(\""
								+ item.permissionId
								+ "\");'>修改</a>&nbsp;&nbsp;<a class=\"btn btn-warning\" onclick='del(\""
								+ item.permissionId
								+ "\");'>删除</a></td> "
								+ " </tr>";
					})
			$("#tbody").html(html.replace(/null/g, ''));
			initPage("page", result.data)
		}
	})
}

function openadd() {
	$("#myModalLabel").text("新增权限");
	$("input").val("");
	$("#addModal").modal("show");
	$("#add").show();
	$("#edt").hide();
}

function add() {
	if ($("#permissionName").val() == "") {
		layer.tips('不能为空', '#permissionName');
		return;
	}
	if ($("#permissionType").val() == "") {
		layer.tips('不能为空', '#permissionType');
		return;
	}
	if ($("#permissionDesc").val() == "") {
		layer.tips('不能为空', '#permissionDesc');
		return;
	}
	if ($("#permissionUrl").val() == "") {
		layer.tips('不能为空', '#permissionUrl');
		return;
	}
	var formdata = {
		flag : "add",
		permissionId : $("#permissionId").val(),
		permissionName : $("#permissionName").val(),
		permissionType : $("#permissionType").val(),
		permissionUrl : $("#permissionUrl").val(),
		permissionDesc : $("#permissionDesc").val(),
		isMenu : $("#isMenu").val(),
		menuName : $("#menuName").val(),
		menuNo : $("#menuNo").val(),
		perentMenu : $("#perentMenu").val()
	}
	$.ajax({
		url : "/dsm-controller/permission/editPermission",
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
			$("#addModal").modal("hide");
			var message = result.message;
			layer.alert(message);
			$("input").val("");
			load(curr);
		}
	})
}

function openedt(permissionId) {
	$.ajax({
		url : "/dsm-controller/permission/queryPermissionDetail",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : {
			"flag" : "getPermission",
			"permissionId" : permissionId
		},
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			var data = result.data;
			$("#myModalLabel").text("修改权限");
			$("#permissionId").val(permissionId);
			$("#permissionName").val(data.permissionName);
			$("#permissionType").val(data.permissionType);
			$("#permissionUrl").val(data.permissionUrl);
			$("#permissionDesc").val(data.permissionDesc);
			$("#isMenu").val(data.isMenu);
			$("#menuName").val(data.menuName);
			$("#menuNo").val(data.menuNo);
			$("#perentMenu").val(data.perentMenu);

			$("#edt").show();
			$("#add").hide();
			$("#addModal").modal("show");
		}
	})
}

function edt() {
	if ($("#userName").val() == "") {
		layer.tips('不能为空', '#userName');
		return;
	}
	if ($("#Chinese").val() == "") {
		layer.tips('不能为空', '#Chinese');
		return;
	}
	if ($("#Math").val() == "") {
		layer.tips('不能为空', '#Math');
		return;
	}
	if ($("#English").val() == "") {
		layer.tips('不能为空', '#English');
		return;
	}
	var formdata = {
		flag : "edt",
		userName : $("#userName").val(),
		Chinese : $("#Chinese").val(),
		Math : $("#Math").val(),
		English : $("#English").val()
	}
	$.ajax({
		url : "../Json/Index.aspx",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : formdata,
		success : function(data) {
			$("#addModal").modal("hide");
			layer.alert(data.msg);
			$("input").val("");
			load(curr);
		}
	})
}

function del(permissionId) {
	// 询问框
	layer.confirm('您确定要删除？', {
		btn : [ '确定', '取消' ]
	// 按钮
	}, function() {
		$.ajax({
			url : "/dsm-controller/permission/deletePermission",
			timeout : 300000,
			dataType : "json",
			type : "post",
			data : {
				"flag" : "del",
				"permissionId" : permissionId
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