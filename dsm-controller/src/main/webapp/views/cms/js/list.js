$(function(){
	initThead('cmsListTable','json/list.json');
	queryList(1);
})

var listData = [];			//全局变量，用来存储表的列信息

//初始化thead
function initThead(tableId,dataUrl){
	$.ajax({url:dataUrl,
			async:false,
			success:function(data){
			listData = data;
			}
	});
	
	var table = $('#'+tableId)[0];	//获取相应ID的表格
	//判断表格是否存在，类型是否正确
	if(typeof table=='undefined'||table=='undefined'||table.tagName!='TABLE'){
		alert("there is no such table");
		return;
	}
	var thead = $(table).find('thead')[0];
	if(typeof thead=='undefined'||thead=='undefined'){
		$(table).html('<thead></thead>');
		thead = $(table).find('thead')[0];
	}
	$(thead).html('<tr></tr>');			//往thead中写入一行
	var tr = $(thead).find('tr')[0];	//获取写入的行
	for(var i=0;i<listData.length;i++){		//循环向行中加入列
		addTdToTr(tr,listData[i],'columnDescription');
	}
}

//往tr行里面加入td列
function addTdToTr(tr,data,columnName){
	var td = '<td>'+data[columnName]+'</td>';
	$(tr).append(td);
}

//查询cms信息分页列表
function queryList(pageNumber){
	var queryUrl = '/dsm-controller/cmsInfo/pageCmsInfoList';
	var queryData = {
			pageNumber:pageNumber
	}
	$.ajax({
		url : queryUrl,
		timeout : 300000,
		dataType : "json",
		type : "post",
		data : queryData,
		success : function(result) {
			var code = result.code;
			if (code != '0') {
				layer.alert(result.message);
				return;
			}
			//有权限在继续操作
			showList('cmsListTable',result.data,listData);
		},
		error : function(error){
			layer.alert("出错啦！");
		}
	})
}

//展示列表内容
function showList(tableId,page,columnInfo){
	//layer.alert(JSON.stringify(page));
	$("#"+tableId+" tbody").html('');
	if(page!=null){
		var list = page.list;
		if(list!=null){
			for (var i = 0; i < list.length; i++) {
				var row = list[i];
				formatNewRow(tableId,row,columnInfo,i+1);
			}
		}
	}
}

//新建table中的一行
function formatNewRow(tableId,row,columnInfo,rowNum){
	var tr = '<tr id="row'+rowNum+'"></tr>';
	$("#"+tableId+" tbody").append(tr);
	for(var i=0;i<listData.length;i++){		//循环向行中加入列
		insertTdToTr('row'+rowNum,row,listData[i]);
	}
}

function insertTdToTr(rowId,row,tdData){
	var td = null;
	var columnType = tdData['columnType'];
	if(columnType=='checkBox'){
		td = '<td><input class="list-checkBox" type="checkBox" value="'+row[tdData['columnName']]+'"/></td>';
	}else if(columnType=='input'){
		td = '<td>'+row[tdData['columnName']]+'</td>';
	}else if(columnType=='textarea'){
		td = '<td><div class="list-textarea">'+row[tdData['columnName']]+'</div></td>';
	}else if(columnType=='image'){
		var srcUrl = row[tdData['columnName']]==null?' ':row[tdData['columnName']];
		td = '<td><img class="list-image" src="'+srcUrl+'"/></td>';
	}
	$("#"+rowId).append(td.replace(/null|undefined/g,''));
}