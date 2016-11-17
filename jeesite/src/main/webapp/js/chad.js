/**
 * Created by Chad on 2016/2/18.
 */

(function(window){
    var D = {
        curSect : null
    };
    /**
     * 创建 HTML节点
     * @param name 节点名称
     * @param attr 节点属性
     * @param style 节点样式
     * @returns {HTMLElement}
     */
    D.newEl = function(name,attr,style){
        var el = document.createElement(name);
        // 为节点添加属性
        for(var a in attr){
            el[a] = attr[a];
        }
        // 为节点添加样式
        for(var s in style){
            el.style[s] = style[s];
        }
        return el;
    };

    /**
     * 通过Id查找元素
     * @param id
     * @returns {HTMLElement}
     */
    D.byId = function(id){
        return document.getElementById(id);
    };
    
    /**
     * 通过样式查找元素
     * @param name 样式名称
     * @returns {array}
     */
    D.byClassName = function(name){
    	return document.getElementsByClassName(name);
    }

    /**
     * 赋予对象拖动能力
     * @param o
     */
    D.move = function(o){
        o.children[0].draggable = "true";
        o.children[0].ondragstart = function(ev){
        	D.moveId = o.id;
        	o.x = ev.x;
        	o.y = ev.y;
        };
        window.ondragover = function(ev){
        	var p = D.byId(D.moveId);
            ev.preventDefault();
  		    p.style.left = p.offsetLeft + (ev.x-p.x)+"px";
  		    p.style.top = p.offsetTop +(ev.y-p.y)+"px";
  		    p.x = ev.x;
  		    p.y = ev.y;
        };
        window.ondrop = function(ev){
            ev.preventDefault();
        }
    };

    /**
     * 创建通用窗体
     * @param id
     * @param size 自定义大小{w:"xx",h:"xx"}
     * @returns {HTMLElement}
     */
    D.newCommonWin = function(id,size){
        var win = this.newEl("div",{"id":id});
        if(size){
        	if(size.w){
        		win.style.width = size.w;
        	}
            if(size.h){
            	win.style.height = size.h;
            }
            if(size.l){
            	win.style.left = size.l;
            }
            if(size.t){
            	win.style.top = size.t;
            }
            
        }
        win.className = "common-panel";
        // 创建头部
        var title = this.newEl("div",{"className":"common-title"});
        var titleName = this.newEl("div",{"className":"common-title-name"});
        win.setTitleName = function(name){
              titleName.innerHTML = name;
        };
        var titleTool = this.newEl("div");
        var minBtn = this.newEl("div",{"className":"minBtn"});
        minBtn.onclick = function(){
            this.parentElement.parentElement.parentElement.style.display = "none";
        };
        var closeBtn = this.newEl("div",{"className":"closeBtn"});
        closeBtn.onclick = function(){
        	D.byId("main").removeChild(win);
        	D.removeMinIcon(id+"-min");
        }
        
        titleTool.appendChild(minBtn);
        titleTool.appendChild(closeBtn);
        title.appendChild(titleName);
        title.appendChild(titleTool);
        // 创建主体
        var main = this.newEl("div",{"className":"common-body"});
        win.body = main;
        win.appendChild(title);
        win.appendChild(main);
        this.move(win);
        return win;
    };
    
    D.removeMinIcon = function(id){
    	if(D.byId(id)){
    		D.byId("footer").removeChild(D.byId(id));    		
    	}
    }
    
    
    /**
     * 窗体置顶
     * @param el 要置顶的窗体
     */
    D.topPanel = function(el){
    	var top_el = this.byClassName("top-panel")[0];
    	if(top_el){
    		D.removeClassName(top_el,"top-panel");
    	}
    	D.addClassName(el,"top-panel");
    }

    /**
     * 通过Jquery Ajax进行异步请求
     * @param url
     * @param data
     * @param fun
     */
    D.ajax = function(url,data,fun){
        $.post(
            url,
            data,
            fun,
            "json"
        );
    };
    
    /**
     * 添加样式
     * @param el 需要添加样式的元素
     * @param cls 需要添加的样式名称
     */
    D.addClassName = function(el,cls){
    	if(el.className){
    		el.className+=" "+cls;
    	}else{
    		el.className = cls;
    	}
    }
    
    /**
     * 删除样式
     * @param el 要删除样式的元素
     * @param c 要删除的样式
     */
    D.removeClassName = function(el,c){
    	var cls = el.className;
        var cls_array = cls.split(" ");
        var new_cls = [];
        for(var i in cls_array){
            if(cls_array[i] != c){
                new_cls.push(cls_array[i]);
            }
        }
        el.className = new_cls.join(" ");
    };
    
    /**
     * 签名时加载Act列表
     * @param state
     * @param mode
     */
    D.loadActs = function(state,mode){
    	D.byId("acts-list-data").innerHTML = "";
    	$.post(
    		"/noncase/a/acttostatute/nonCaseA2SAct/findUnsignedActs",
    		{
    			"state":state,
    			"workMode":mode
    		},
    		function(acts){
    			var dataList = D.byId("acts-list-data");
    			for(var j=0;j<acts.length;j++){
    				var ul = D.newEl("ul");
    				var li1 = D.newEl("li",{"innerText":j+1});
    				var li2 = D.newEl("li");
    				var li3 = D.newEl("li");
    				if(mode==1){
    					li2.innerText = acts[j].billNumber;
//    					li2.innerText = "******";
    					if(acts[j].SignerNumber=="0"){
    						li3.innerHTML = "<img src='../image/sign-flag.png'><img src='../image/sign-flag.png'>";
    					}else{
    						li3.innerHTML = "<img src='../image/sign-flag.png'>";
    					}
    				}else{
    					li2.innerText = acts[j].billNumber;
    					li3.innerHTML = "<img src='../image/sign-flag.png'>";
    				}
    				li3.id = acts[j].Id;
    				li3.onclick = function(){
    					var msg = {};
    					msg['action'] = 'sign';
    					msg['id'] = this.id;
    					msg['state'] = state;
    					websocket.send(JSON.stringify(msg));
    				}
    				ul.appendChild(li1);
    				ul.appendChild(li2);
    				ul.appendChild(li3);
    				dataList.appendChild(ul);
    			}
    		},
    		"json"
    	);
    }
    
    /**
     * 创建编辑器
     * @param p 编辑器的容器
     * @returns e 编辑器对象
     */
    D.editor = function(p){
    	var e = {}
    	//构造框架
    	p.style.display = "flex";
    	var e = this.newEl("div",{},{"flex":"1","height":"100%"});
    	var textarea = this.newEl("textarea",{"id":"act-content"},{"width":"100%","height":"100%"});
    	e.appendChild(textarea);
    	var simditor = new Simditor({
	        textarea: $(textarea),
	        toolbar:['underline','strikethrough','alignment','bold','italic','blockquote','eraser']
	    });
    	p.appendChild(e);
    	var t = this.newEl("div",null,{"flex":"0 0 34px","height":"100%","backgroundColor":"rgb(232, 232, 232)"});
    	p.appendChild(t);
    	
    	e.simditor = simditor;
    	e.setValue = function(filePath){
    		D.ajax(
				"/noncase/a/acttostatute/nonCaseA2SAct/readActContent",
				{"contentFile":filePath},
				function(json){
					e.simditor.setValue(json.html);
				}
			);
    	}
    	// 工具栏
    	//提交
    	t.appendChild(this.elToolItem("提交","confirm-24",function(){
    		var sectionList = D.byId("section-list");
			if(sectionList){
				var unsubmit = 0;
				for(var i=1;i<sectionList.children.length;i++){
					var section = sectionList.children[i];
					if(!section.style.color){
						unsubmit++;
					}
				}
				if(unsubmit==1&&!confirm("确定提交后，此Act将进入对比流程，确认提交吗？")){
					return;
				}
				$.post(
					"/noncase/a/acttostatute/nonCaseA2sSection/onSubmit",
					{"id":D.curSect.Id},
					function(d){
						if(d.finish){
							//关闭编辑界面
							D.byId("main").removeChild(D.byId("editor-window"));
				        	D.removeMinIcon("editor-window-min");
							//Act列表移除
				        	D.byId("my-acts-list").removeChild(D.byId(D.curSect.ActId));
						}else{
							D.byClassName("current-section")[0].style.color="yellow";
						}
					},
					"json"
				);
			}
    	}));
    	//快照
    	t.appendChild(this.elToolItem("快照","snapshot-24",function(){
    		if(!D.byId("snapshot-min")){
    			var snapshotWin = D.newCommonWin("snapshot-panel",{"l":"100px","t":"10px","h":"700px"});
        		snapshotWin.setTitleName(D.curSect.Caption);
        		var iframe = D.newEl("iframe",{"id":"snapshot"});
        		snapshotWin.body.style.height = "570px";
        		snapshotWin.body.appendChild(iframe);
        		D.byId("main").appendChild(snapshotWin);
        		D.showMinIcon("snapshot-panel-min","snapshot-panel-min.png","snapshot-panel");
        		D.topPanel(snapshotWin);
    			iframe.src = "/noncase/a/acttostatute/nonCaseA2SAct/viewSnapshot?filePath="+D.curSect.FilePath;
    			
    		}else{
    			var snapshot = el_Id("snapshot");
    			snapshot.src = "/noncase/a/acttostatute/nonCaseA2SAct/viewSnapshot?filePath="+D.curSect.FilePath;
    		}
    	}));
    	t.appendChild(this.newEl("hr"));
    	//新增节点
    	t.appendChild(this.elToolItem("新增","create-24",function(){
    		if(confirm("确认要新增节点吗？")){
    			$.post(
    				"/noncase/a/acttostatute/nonCaseA2sSection/createNewSection",
    				{"id":D.curSect.Id},
    				function(json){
    					var sectionList = D.byId("section-list");
    					if(sectionList){
    						var div = D.newEl("div",{"innerHTML":"<input type='checkbox'><span>("+(sectionList.children.length+1)+") "+json.Caption+"</span>","id":json.Id});
    						div.json = json;
    						sectionList.appendChild(div);
    						div.onclick = function(){
    							D.curSect = this.json;
    							var el = D.byClassName("current-section")[0];
    							removeClassName(el,"current-section");
    							this.className = "current-section";
    							simditor.setValue("");
    							D.key.setValue();
    						}
    					}
    				},
    				"json"
    			);
    		}
    	}));
    	//删除节点
    	t.appendChild(this.elToolItem("删除","delete-24",function(){
    		var sectionList = D.byId("section-list");
    		if(confirm("确认要删除此节点吗？")){
    			$.post(
    				"/noncase/a/acttostatute/nonCaseA2sSection/removeSection",
    				{"id":D.curSect.Id},
    				function(ret){
    					var n = null;
    					var el = D.byId(D.curSect.Id);
    					if(el){
    						if(el.nextElementSibling){
        						n = el.nextElementSibling;
        					}else{
        						n = el.previousElementSibling;
        					}
        					D.curSect = n.json;
    						n.className = "current-section";
    						e.setValue(n.json.ContentFilePath);
    						D.key.setValue();
        					sectionList.removeChild(el);
    					}else{
    						$.post(
								"/noncase/a/acttostatute/nonCaseA2sSection/changeSection",
								{
									"direction":"right",
									"actId":D.curSect.ActId,
									"parseOrder":D.curSect.ParseOrder
								},
								function(section){
									if(!section.end){
										D.curSect = section;
										D.key.setValue();
										D.e.setValue(section.ContentFilePath);
									}else{
										$.post(
											"/noncase/a/acttostatute/nonCaseA2sSection/changeSection",
											{
												"direction":"left",
												"actId":D.curSect.ActId,
												"parseOrder":D.curSect.ParseOrder
											},
											function(section){
												D.curSect = section;
												D.key.setValue();
												D.e.setValue(section.ContentFilePath);
											},
											"json"
										);
									}
								},
								"json"
							);
    					}
    				}
    			);
    		}
    	}));
    	t.appendChild(this.newEl("hr"));
    	// 批量修改关键信息
    	t.appendChild(this.elToolItem("批量修改关键信息","key-24",function(){
    		var popWin = D.newCommonWin("keyinfro",{"w":"600px","h":"90px","t":"250px","l":"600px"});
    		popWin.setTitleName("Key Information");
    		var type = D.newEl("select",{},{"margin":"10px 0 10px 10px"});
    		var opt1 = D.newEl("option",{"innerHTML":"EffectiveDate"});
    		var opt2 = D.newEl("option",{"innerHTML":"SourceNoteLink"});
    		var opt3 = D.newEl("option",{"innerHTML":"ExpirationDate"});
    		type.appendChild(opt1);
    		type.appendChild(opt2);
    		type.appendChild(opt3);
    		popWin.body.appendChild(type);
    		var input = D.newEl("input",{},{"margin":"10px 0","width":"420px"});
    		popWin.body.appendChild(type);
    		popWin.body.appendChild(input);
    		var btn = D.newEl("div",{"innerText":"Ok","className":"btn"});
    		btn.onclick = function(){
    			$.post(
    					"/noncase/a/acttostatute/nonCaseA2sSection/modifyKeyInforBatch",
    					{"actId":D.curSect.ActId,"keyType":type.value,"keyValue":input.value}
    				);
    		    	var sectionList = D.byId("section-list");
    		    	for(var i=0;i<sectionList.children.length;i++){
    		    		var sect = sectionList.children[i];
    		    		sect.json[type.value] = input.value;
    		    	}
    		    	popWin.parentElement.removeChild(popWin);
    		    	D.byId(type.value).value = input.value;
    		}
    		popWin.body.appendChild(btn);
    		D.byId("main").appendChild(popWin);
    		D.topPanel(D.byId("keyinfro"));
    	}));
    	//批量Repeal
    	t.appendChild(this.elToolItem("批量Repeal","repeal-24",function(){
    		var popWin = D.newCommonWin("keyinfro",{"w":"600px","h":"90px","t":"250px","l":"600px"});
    		popWin.setTitleName("Key Information");
    		var label = D.newEl("div",{"innerText":"Captions"},{
    			display: "flex",
	    	    justifyContent: "center",
	    	    alignItems: "center",
	    	    backgroundColor: "white",
	    	    width: "105px",
	    	    margin: "10px 0 10px 10px",
	    	    padding: "0 5px",
	    	    border: "1px solid #ccc"
    		});
    		popWin.body.appendChild(label);
    		var input = D.newEl("input",{},{"margin":"10px 0","width":"420px"});
    		popWin.body.appendChild(input);
    		var btn = D.newEl("div",{"innerText":"Ok","className":"btn"});
    		btn.onclick = function(){
    			$.post(
    					"/noncase/a/acttostatute/nonCaseA2sSection/repealBath",
    					{"id":D.curSect.Id,"value":input.value},
    					function(array){
    						var sectionList = D.byId("section-list");
    						for(var i=0;i<array.length;i++){
    							var json = array[i];
    							var div = D.newEl("div",{"innerHTML":"<input type='checkbox'><span>("+(sectionList.children.length+1)+") "+json.Caption+"</span>","id":json.Id});
        						div.json = json;
        						sectionList.appendChild(div);
        						div.onclick = function(){
        							D.curSect = this.json;
        							var el = D.byClassName("current-section")[0];
        							removeClassName(el,"current-section");
        							this.className = "current-section";
        							simditor.setValue("");
        							D.key.setValue();
        						}
    						}
    					},
    					"json"
    				);
    		    	var sectionList = D.byId("section-list");
    		    	popWin.parentElement.removeChild(popWin);
    		}
    		popWin.body.appendChild(btn);
    		D.byId("main").appendChild(popWin);
    		D.topPanel(D.byId("keyinfro"));
    	}));
    	t.appendChild(this.newEl("hr"));
    	// 加链接
    	t.appendChild(this.elToolItem("加链接","link-24",function(){
    		
    	}));
    	// 日期查询
    	t.appendChild(this.elToolItem("加链接","history-24",function(){
    		var historyWin = D.newCommonWin("historyWin");
    		historyWin.setTitleName("Bill History");
    		D.byId("main").appendChild(historyWin);
    		D.topPanel(D.byId("historyWin"));
    		historyWin.body.style.backgroundColor = "white";
    		historyWin.body.style.overflow = "auto";
    		D.ajax(
    			"/noncase/a/acttostatute/nonCaseA2SAct/viewBillHistory",
    			{"id":D.curSect.ActId},
    			function(ret){
    				historyWin.body.innerHTML = ret.html;
    			}
    		);
    	}));
    	// 原始版本
    	t.appendChild(this.elToolItem("原始版本","original statute-24",function(){
    		var originalWin = D.newCommonWin("originalWin");
    		originalWin.setTitleName("Original History");
    		D.byId("main").appendChild(originalWin);
    		D.topPanel(D.byId("originalWin"));
    		originalWin.body.style.backgroundColor = "black";
    		originalWin.body.style.overflow = "auto";
    		originalWin.body.style.padding = "5px";
    		originalWin.body.style.justifyContent = "center";
    		originalWin.body.style.alignItems = "center";
    		originalWin.body.innerHTML = "<img width='32' height='32' src = '../image/waiting.jpg' />"
    		D.ajax(
    			"/noncase/a/acttostatute/nonCaseA2SAct/viewOriginalStatute",
    			{"shortName":D.curSect.ShortName,"actId":D.curSect.ActId},
    			function(ret){
    				originalWin.body.style.backgroundColor = '#fff';
    				originalWin.body.style.justifyContent = "";
    	    		originalWin.body.style.alignItems = "";
    				originalWin.body.innerHTML = ret.html;
    			}
    		);
    	}));
    	D.e = e;
    	return e;
    }
    
    /**
     * 添加按钮菜单
     * @param hotName 热点名称
     * @param ico 图标
     * @param fn 点击事件
     */
    D.elToolItem = function(hotName,ico,fn){
		var div = this.newEl(
	        "div",
	        {"className":"editor-tool","title":hotName},
	        {"backgroundImage":"url('simditor/images/"+ico+".png')"}
	    );
	    div.appendChild(this.newEl("div",{"className":"hot-point"}));
	    div.onclick=fn;
	    return div;
	}
    
    D.newKeyItem = function(name){
    	var outer = this.newEl("div",null,{"display":"flex","border-top":"1px solid #ccc","flex":"1"});
		var div1 = this.newEl(
	        "div",
	        {"innerText":name},
	        {
	            "flex":"1",
	            "display":"flex",
	            "alignItems":"centr",
	            "padding-left":"2px",
	            "text-transform":"capitalize",
	            "color":"#eee",
	            "backgroundColor":"rgb(145, 145, 145)",
	            "boxShadow":"2px 0px 5px rgb(87, 87, 87)",
	            "alignItems":"center"
	        }
	    );
		var div2 = this.newEl("div",null,{"flex":"3","display":"flex","flexDirection":"column","justifyContent":"center"});
		if(name=="updateType"){
			var select = this.newEl("select",{"id":"updateType"},{"margin":"0 5px","border":"none","height":"100%"});
			select.onchange = function(){
				if(this.value!=D.curSect.ActionType){
					$.post(
						"a/atsSection/saveKeyInfor",
						{"id":D.curSect.id,"key":"updateType","value":this.value},
						function(ret){
							if(ret){
								var html = simditor.getValue();
								if(!html.match(/<strike>/)){
									html = html.replace(/<p>/g,"<p><font color=\"#f00\"><u>");
									html = html.replace(/<\/p>/g,"</u></font></p>");
									D.e.simditor.setValue(html);
								}
							}
						}
					);
				}
			};
			var opt1 = this.newEl("option",{"innerText":"New","value":"1"});
			var opt2 = this.newEl("option",{"innerText":"Modify","value":"2"});
			var opt3 = this.newEl("option",{"innerText":"Repeal","value":"3"});
			select.appendChild(opt1);
			select.appendChild(opt2);
			select.appendChild(opt3);
			div2.appendChild(select);
		}else{
			var input = this.newEl("input",{"id":name,"className":"key-infor-value","type":"text"});
			input.onblur = function(){
				if(this.value!=D.curSect[this.id]){
					if(this.id=='caption'){
						var treeObj = $.fn.zTree.getZTreeObj("sectionList");
						var node = treeObj.getSelectedNodes()[0];
						node.name = this.value;
						treeObj.updateNode(node);
						//修改Caption,联动修改ShortName
						var st = D.byId("shortName").value;
						st = st.replace(D.curSect[this.id],this.value);
						D.byId("shortName").value = st;
						D.curSect[this.id] = this.value;
						$.post(
							"a/atsSection/saveKeyInfor",
							{"id":D.curSect.id,"key":"shortName","value":st}
						);
					}
					$.post(
						"a/atsSection/saveKeyInfor",
						{"id":D.curSect.id,"key":this.id,"value":this.value}
					);
				}
			}
			div2.appendChild(input);
		}
	    outer.appendChild(div1);
	    outer.appendChild(div2);
	    return outer;
    }
    window.D = D;
}(window));