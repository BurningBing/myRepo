/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;
import com.thinkgem.jeesite.modules.ats.service.AtsActService;
import com.thinkgem.jeesite.modules.ats.service.AtsTaskService;

/**
 * taskController
 * @author Chad
 * @version 2016-03-09
 */
@Controller
@RequestMapping(value = "${adminPath}/atsTask")
public class AtsTaskController extends BaseController {

	@Autowired
	private AtsTaskService atsTaskService;
	@Autowired
	private AtsActService atsActService;
	
	
	@ModelAttribute
	public AtsTask get(@RequestParam(required=false) String id) {
		AtsTask entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = atsTaskService.get(id);
		}
		if (entity == null){
			entity = new AtsTask();
		}
		return entity;
	}
	
	@RequiresPermissions("ats:atsTask:view")
	@RequestMapping(value = {"list", ""})
	public String list(AtsTask atsTask, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtsTask> page = atsTaskService.findPage(new Page<AtsTask>(request, response), atsTask); 
		model.addAttribute("page", page);
		return "modules/ats/atsTaskList";
	}
	
	@RequiresPermissions("ats:atsTask:view")
	@RequestMapping(value = "form")
	public String form(AtsTask atsTask, Model model) {
		model.addAttribute("atsTask", atsTask);
		return "modules/ats/atsTaskForm";
	}

	@RequiresPermissions("ats:atsTask:edit")
	@RequestMapping(value = "save")
	public String save(AtsTask atsTask, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, atsTask)){
			return form(atsTask, model);
		}
		atsTaskService.save(atsTask);
		addMessage(redirectAttributes, "保存task成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsTask/?repage";
	}
	
	@RequiresPermissions("ats:atsTask:edit")
	@RequestMapping(value = "delete")
	public String delete(AtsTask atsTask, RedirectAttributes redirectAttributes) {
		atsTaskService.delete(atsTask);
		addMessage(redirectAttributes, "删除task成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsTask/?repage";
	}

	
	
}