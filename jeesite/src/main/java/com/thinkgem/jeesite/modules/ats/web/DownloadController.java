package com.thinkgem.jeesite.modules.ats.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.websocket.GlobalHandler;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;
import com.thinkgem.jeesite.modules.ats.service.AtsTaskService;
import com.thinkgem.jeesite.modules.ats.utils.BaseStateUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.utils.BasicTaskUtils;

@Controller
@RequestMapping(value = "a/download")
public class DownloadController extends BaseController  {
	@Autowired
	private AtsTaskService taskService;
	
	@ModelAttribute
	public AtsTask get(@RequestParam(required=false) String id) {
		AtsTask entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = taskService.get(id);
		}
		if (entity == null){
			entity = new AtsTask();
		}
		return entity;
	}
	
	@RequestMapping(value="checkUpdates")
	public void checkUpdates(HttpServletRequest request, HttpServletResponse response){
		String state  = request.getParameter("state");
		GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog", "["+DateUtils.getDateTime()+"]"+" : Begin to check updates of "+state);
		AtsTask task = taskService.getByState(state);
		try {
			Class.forName("com.thinkgem.jeesite.modules.utils."+state).getMethod("download").invoke(null);
//			BasicTaskUtils utils = (BasicTaskUtils) Class.forName(Global.getStateUtils(state)).newInstance();
//			utils.checkUpdates(task);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
