$(function(){
	initThead('cmsListTable','json/list.json');
})

//初始化thead
function initThead(tableId,dataUrl){
	var listData = null;
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