(function(dom){

	var ats = new Object();

	//创建元素
	ats.createElement = function(opt,parent){
		if(opt.length){
			this.forEachArray(opt,function(item,index){
				ats.biuldElement(item,parent);
			});
			return null;
		}else{
			return this.biuldElement(opt,parent);
		}
		
	}
	
	ats.biuldElement = function(opt,parent){
		var element = dom.createElement(opt.name);
		//为元素添加属性
		if(opt.attrs){
			this.forEachJson(opt.attrs,function(ret){
				element[ret.key] = ret.value;
			});
		}
		//为元素添加样式
		if(opt.style){
			this.forEachJson(opt.style, function(ret){
				element.style[ret.key] = ret.value;
			});
		}
		//将元素添加到父元素中
		parent.appendChild(element);
		if(opt.children){
			this.forEachArray(opt.children,function(item,index){
				ats.createElement(item,element);
			});
		}
		return element;
	}
	

	//循环遍历数组
	ats.forEachArray = function(array,callback){
		if(array.length){
			var index = 0;
			for(var i in array){
				console.log();
				callback(array[i],index);
				index++;
			}
		}
	}
	//循环遍历JSON
	ats.forEachJson =function(json,callback){
		for(var i in json){
				var ret = {}
				ret.key = i;
				ret.value = json[i];
				callback(ret);
		}
	}

	//控制台打印
	ats.print = function(msg){
		console.log(msg);
	}
	window.ats = ats;

})(document);

