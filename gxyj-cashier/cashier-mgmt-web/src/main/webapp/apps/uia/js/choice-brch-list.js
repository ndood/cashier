/**
 * 
 */
define(function(require, exports, module) {
	var comm = require('../../../common/js/page-common'),
		pager = require('../../../lib/zuiplugin/zui.pager');
	
	function PageScript(){
		this.first = true;
		this.RowData = {};
	}
	
	PageScript.prototype.init = function(){
		// 初始化分页组件
		pager.init(10, page.renderGrid);
		
		$.useModule(['datatable', 'chosen'], function(){
			$('select').chosen();
			
			page.loadData();
		});
		
		
		
	  	page.bindEvent();
	};
	
	PageScript.prototype.loadData = function(){
		
		
		var zuiLoad = new $.ZuiLoader().show('数据加载中...');
		ajax.post({
			url: 'm105/f10506',
			checkSession: false,
			uia:true,
			data: {brchName: $('#brchName').val()},
			pager: pager // 传入分页对象
		}).done(function(res, rtn, state, msg){
			if(state){
				page.renderGrid(rtn.data);
			} else {
				$Messager('数据加载失败：' + msg);
			}
		}).fail(function(){
			
		}).always(function(){
			zuiLoad.hide();
		});
		
	};
	
	PageScript.prototype.renderGrid = function(data){
		var trHtmls = '';
		if(data.numberOfElements && data.content){
			var obj = {},  parentBrchName;
			page.RowData = {};
			for(var i = 0; i < data.numberOfElements; i++){
				obj = data.content[i][0];
				parentBrchName = data.content[i][1];
				page.RowData["row-" + obj.rowId] = obj;
				trHtmls += laytpl('list-tr.tpl').render({
					"index": (i<9?"0":"") + (i + 1),
					"rowId": obj.rowId,
					"brchName": obj.brchName,
					"parentBrchName": parentBrchName || "",
					"buttons": (function(){
						var btnHtml = '';
						btnHtml += laytpl('list-btn.tpl').render({
							"class": "btn-check",
							"icon": "icon-check",
							"title": "选择"
						}) + '&nbsp;';
						return btnHtml;
					})()
				});
			}
		}
		
		$('#data-body').html(trHtmls);
		if(this.first){
			this.first = false;
			/*$('table.datatable').datatable({
				sortable: true,
				checkable: false
			})*/
		} else {
			/*$('table.datatable').datatable('load');*/
		}
		
		// 创建分页条
		pager.create('.pager-box', data ,{ctlColumn:false});
		
		$('.icon-btn').tooltip();
	};
	
	PageScript.prototype.bindEvent = function(){
		
		window['$Messager'] = function(text){
			new $.zui.Messager(text, {
			    type: 'success',
			    placement: 'center'
			}).show();
		};
		
		
		$('.search-btn').on('click', function(){
			page.loadData();
		});
		
		$('.result-panel').on('click', '.btn-check', function(){
			var that = $(this),
				$tr = that.parent().parent(),
				rowId = $tr.data('id');
			var obj = page.RowData["row-" + rowId];
			if($('#parentBrchName', parent.document).length){
				$('#parentBrchName', parent.document).val(obj.brchName).focus();
			}else if($('#brchName', parent.document).length){
				$('#brchName', parent.document).val(obj.brchName).focus();
			}
			
			
			if($('#brchId', parent.document).length){
				$('#brchId', parent.document).val(obj.brchId);
			}
			if($('#parentBrchId', parent.document).length){
				$('#parentBrchId', parent.document).val(obj.brchId);
			}
			// 隐藏弹出框
			$('.btn-brchchoice', parent.document).click();
			
		});
		
	};
	
	var page = new PageScript();
	page.init();
	
	window.loadData = page.loadData;
	
	return page;
});