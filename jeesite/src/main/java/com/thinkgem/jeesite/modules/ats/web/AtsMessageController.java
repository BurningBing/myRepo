/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.thinkgem.jeesite.modules.ats.entity.AtsMessage;
import com.thinkgem.jeesite.modules.ats.service.AtsMessageService;

/**
 * messageController
 * @author Chad
 * @version 2016-03-09
 */
@Controller
@RequestMapping(value = "${adminPath}/ats/atsMessage")
public class AtsMessageController extends BaseController {

	@Autowired
	private AtsMessageService atsMessageService;
	
	@ModelAttribute
	public AtsMessage get(@RequestParam(required=false) String id) {
		AtsMessage entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = atsMessageService.get(id);
		}
		if (entity == null){
			entity = new AtsMessage();
		}
		return entity;
	}
	
	@RequiresPermissions("ats:atsMessage:view")
	@RequestMapping(value = {"list", ""})
	public String list(AtsMessage atsMessage, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtsMessage> page = atsMessageService.findPage(new Page<AtsMessage>(request, response), atsMessage); 
		model.addAttribute("page", page);
		return "modules/ats/atsMessageList";
	}

	@RequiresPermissions("ats:atsMessage:view")
	@RequestMapping(value = "form")
	public String form(AtsMessage atsMessage, Model model) {
		model.addAttribute("atsMessage", atsMessage);
		return "modules/ats/atsMessageForm";
	}

	@RequiresPermissions("ats:atsMessage:edit")
	@RequestMapping(value = "save")
	public String save(AtsMessage atsMessage, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, atsMessage)){
			return form(atsMessage, model);
		}
		atsMessageService.save(atsMessage);
		addMessage(redirectAttributes, "保存message成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsMessage/?repage";
	}
	
	@RequiresPermissions("ats:atsMessage:edit")
	@RequestMapping(value = "delete")
	public String delete(AtsMessage atsMessage, RedirectAttributes redirectAttributes) {
		atsMessageService.delete(atsMessage);
		addMessage(redirectAttributes, "删除message成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsMessage/?repage";
	}

}