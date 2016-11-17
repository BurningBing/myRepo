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
import com.thinkgem.jeesite.common.websocket.GlobalHandler;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.ats.service.AtsActService;
import com.thinkgem.jeesite.modules.ats.service.AtsSignService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 签名Controller
 * @author Chad
 * @version 2016-03-20
 */
@Controller
@RequestMapping(value = "${adminPath}/atsSign")
public class AtsSignController extends BaseController {

	@Autowired
	private AtsSignService atsSignService;
	@Autowired
	private AtsActService atsActService;
	
	@ModelAttribute
	public AtsSign get(@RequestParam(required=false) String id) {
		AtsSign entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = atsSignService.get(id);
		}
		if (entity == null){
			entity = new AtsSign();
		}
		return entity;
	}
	
	@RequiresPermissions("ats:atsSign:view")
	@RequestMapping(value = {"list", ""})
	public String list(AtsSign atsSign, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtsSign> page = atsSignService.findPage(new Page<AtsSign>(request, response), atsSign); 
		model.addAttribute("page", page);
		return "modules/ats/atsSignList";
	}

	@RequiresPermissions("ats:atsSign:view")
	@RequestMapping(value = "form")
	public String form(AtsSign atsSign, Model model) {
		model.addAttribute("atsSign", atsSign);
		return "modules/ats/atsSignForm";
	}

	@RequiresPermissions("ats:atsSign:edit")
	@RequestMapping(value = "save")
	public String save(AtsSign atsSign, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, atsSign)){
			return form(atsSign, model);
		}
		atsSignService.save(atsSign);
		addMessage(redirectAttributes, "保存签名成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsSign/?repage";
	}
	
	@RequiresPermissions("ats:atsSign:edit")
	@RequestMapping(value = "delete")
	public String delete(AtsSign atsSign, RedirectAttributes redirectAttributes) {
		atsSignService.delete(atsSign);
		addMessage(redirectAttributes, "删除签名成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsSign/?repage";
	}
	

}