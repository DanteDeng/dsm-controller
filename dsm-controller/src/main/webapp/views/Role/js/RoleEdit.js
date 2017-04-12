var curr = 1;
$(function() {
	load(curr);
	getAllPermission();
})

function load(curr) {
	$
			.ajax({
				url : "/dsm-controller/role/pageRoleList",
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
										html += " <tr> "
												+ " <td>"
												+ item.roleName
												+ "</td> "
												+ " <td>"
												+ item.roleType
												+ "</td> "
												+ " <td>"
												+ item.roleDesc
												+ "</td> "
												+ " <td>"
												+ item.roleDept
												+ "</td> "
												+ " <td><a class=\"btn btn-info\" onclick='openedt(\""
												+ item.roleId
												+ "\");'>修改</a>&nbsp;&nbsp;<a class=\"btn btn-warning\" onclick='del(\""
												+ item.roleId
												+ "\");'>删除</a></td> "
												+ " </tr>";
									})
					$("#tbody").html(html.replace(/null/g, ''));
					initPage("page", result.data)
				}
			})
}

function openadd() {
	$("#myModalLabel").text("新增角色");
	$("#addModal input").val("");
	$("#addModal select option").each(function(index, element) {
		element.selected=false;
	})
	$("#permissionList [name='permission']").each(function(index, element) {
		element.checked=false;
	})
	$("#addModal").modal("show");
	$("#add").show();
	$("#edt").hide();
}

function editPerission() {
	$("#permissionModalLabel").text("权限编辑");
	$("#addModal").modal("hide");
	$("#permissionModal").modal("show");
}

function closePermissionEdit() {
	$("#addModal").modal("show");
	$("#permissionModal").modal("hide");
}

function initPermissionUl(permissionList) {
	$("#permissionList").empty();
	if (permissionList != null) {
		for ( var i in permissionList) {
			var op = '<li><input type="checkBox" name="permission" value="'
					+ permissionList[i].permissionId + '">'
					+ permissionList[i].permissionName + '</option></li>'
			$("#permissionList").append(op);
		}
	}

}

function getAllPermission() {
	$.ajax({
		url : "/dsm-controller/permission/queryPermissionList",
		timeout : 300000,
		dataType : "json",
		type : "post",
		async : false,
		data : {},
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			var permissionList = result.data;
			initPermissionUl(permissionList);
		}
	})
}

function getPermissionJson() {
	var permissionList = [];
	$("#permissionList [name='permission']:checked").each(function(index, element) {
		permissionList.push({
			permissionId : element.value
		});
	})
	return JSON.stringify(permissionList);
}

function add() {
	if ($("#roleName").val() == "") {
		layer.tips('不能为空', '#roleName');
		return;
	}
	if ($("#roleType").val() == "") {
		layer.tips('不能为空', '#roleType');
		return;
	}
	if ($("#roleDesc").val() == "") {
		layer.tips('不能为空', '#roleDesc');
		return;
	}
	if ($("#roleDept").val() == "") {
		layer.tips('不能为空', '#roleDept');
		return;
	}
	var formdata = {
		flag : "add",
		roleId : $("#roleId").val(),
		roleName : $("#roleName").val(),
		roleType : $("#roleType").val(),
		roleDesc : $("#roleDesc").val(),
		roleDept : $("#roleDept").val(),
		permissionJson : getPermissionJson()
	}
	$.ajax({
		url : "/dsm-controller/role/editRole",
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
			$("#addModal input").val("");
			load(curr);
		}
	})
}

function openedt(roleId) {
	$.ajax({
		url : "/dsm-controller/role/queryRoleDetail",
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : {
			"flag" : "getUser",
			"roleId" : roleId
		},
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			var data = result.data;
			$("#myModalLabel").text("修改角色");
			$("#roleId").val(roleId);
			$("#roleName").val(data.roleName);
			$("#roleType").val(data.roleType);
			$("#roleDesc").val(data.roleDesc);
			$("#roleDept").val(data.roleDept);
			$("#permissionList [name='permission']").each(function(index, element) {
				element.checked=false;
			})
			var permissionList = data.permissionList;
			if(permissionList!=null){
				for(var i in permissionList){
					$("#permissionList [name='permission']").each(function(index, element) {
						var permissionId = $(element).val();
						if(permissionId==permissionList[i].permissionId){
							element.checked=true;
						}
					})
				}
			}
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

function del(roleId) {
	// 询问框
	layer.confirm('您确定要删除？', {
		btn : [ '确定', '取消' ]
	// 按钮
	}, function() {
		$.ajax({
			url : "/dsm-controller/role/deleteRole",
			timeout : 300000,
			dataType : "json",
			type : "post",
			data : {
				"flag" : "del",
				"roleId" : roleId
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