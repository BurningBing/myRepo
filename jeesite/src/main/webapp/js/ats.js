(function(window){
	var dom = {};
	
	dom.select = function(selector){
		return document.querySelector(selector);
	}
	
	dom.selectAll = function(selector){
		return document.querySelectorAll(selector);
	}
	
	dom.foreach = function(array, callback){
		for(var i=0;i<array.length;i++){
			callback(array[i]);
		}
	}
	
	dom.newElement = function(name,attr,style){
        var el = document.createElement(name);
        for(var a in attr){
            el[a] = attr[a];
        }
        for(var s in style){
            el.style[s] = style[s];
        }
        return el;
    };
	
	window.dom = dom;
}
)(window);


//页面加载后事件
window.onload = function(){
	// 给州赋予点击事件
	var states = dom.selectAll(".list-data ul li");
	dom.foreach(states,function(item){
		var url = item.getAttribute('data-href');
		item.onclick = function(){
			window.open(url);
		}
	});
}

//表格编辑器
function generate(){
	var input = dom.select("#table-input");
	var output = dom.select("#table-result");
	var str = input.value;
	
	if(str.indexOf("<table")>=0){
		output.innerHTML = "";
		output.innerHTML = str;
	}else{
		var trs = str.split("\n");
		var table = dom.newElement("table",{id:'table',cellPadding:'8px',cellSpacing:"0"});
		table.onselectstart = function(){
			return false;
		}
		dom.foreach(trs,function(sTr){
			var oTr = document.createElement("tr");
			var tds = sTr.split("  ");
			dom.foreach(tds, function(sTd){
				var oTd = dom.newElement("td",{innerHTML:sTd});
				oTr.appendChild(oTd);
			});
			table.appendChild(oTr);
		});
		output.innerHTML = "";
		output.appendChild(table);
	}

	
}
