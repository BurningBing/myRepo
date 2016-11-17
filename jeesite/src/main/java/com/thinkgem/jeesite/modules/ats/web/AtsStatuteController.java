/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.web;

import java.util.List;

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

import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocument;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentListItem;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.webservice.WBNonCaseUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsStatute;
import com.thinkgem.jeesite.modules.ats.service.AtsStatuteService;

/**
 * statuteController
 * @author Chad
 * @version 2016-03-29
 */
@Controller
@RequestMapping(value = "${adminPath}/atsStatute")
public class AtsStatuteController extends BaseController {

	@Autowired
	private AtsStatuteService atsStatuteService;
	
	@ModelAttribute
	public AtsStatute get(@RequestParam(required=false) String id) {
		AtsStatute entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = atsStatuteService.get(id);
		}
		if (entity == null){
			entity = new AtsStatute();
		}
		return entity;
	}
	
	@RequiresPermissions("ats:atsStatute:view")
	@RequestMapping(value = {"list", ""})
	public String list(AtsStatute atsStatute, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtsStatute> page = atsStatuteService.findPage(new Page<AtsStatute>(request, response), atsStatute); 
		model.addAttribute("page", page);
		return "modules/ats/atsStatuteList";
	}

	@RequiresPermissions("ats:atsStatute:view")
	@RequestMapping(value = "form")
	public String form(AtsStatute atsStatute, Model model) {
		model.addAttribute("atsStatute", atsStatute);
		return "modules/ats/atsStatuteForm";
	}

	@RequiresPermissions("ats:atsStatute:edit")
	@RequestMapping(value = "save")
	public String save(AtsStatute atsStatute, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, atsStatute)){
			return form(atsStatute, model);
		}
		atsStatuteService.save(atsStatute);
		addMessage(redirectAttributes, "保存statute成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsStatute/?repage";
	}
	
	@RequiresPermissions("ats:atsStatute:edit")
	@RequestMapping(value = "delete")
	public String delete(AtsStatute atsStatute, RedirectAttributes redirectAttributes) {
		atsStatuteService.delete(atsStatute);
		addMessage(redirectAttributes, "删除statute成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsStatute/?repage";
	}

	@RequestMapping(value="viewOriginalSection")
	public void viewOriginalSection(HttpServletRequest request,HttpServletResponse response){
		String state = request.getParameter("state");
		String shortName = request.getParameter("shortName");
		List<AtsStatute> list = atsStatuteService.findStatuteByState(state);
		if(shortName.contains("La. Sec")){
			try {
				List<LibraryDocumentListItem> items = WBNonCaseUtils.findNonCaseByShortNameList(shortName, list.get(6).getEditionId());
				if(items!=null&&items.size()>0){
					LibraryDocument dom = WBNonCaseUtils.findNonCaseById(items.get(0).getLibraryDocumentID()+"");
					sendMessage(response, dom.getLibraryDocumentHtml());
				}
			} catch (Exception e) {
				sendMessage(response, "<p>查询失败</p>");
				e.printStackTrace();
			}
		}else{
			try {
				List<LibraryDocumentListItem> items = WBNonCaseUtils.findNonCaseByShortNameList(shortName, list.get(0).getEditionId());
				if(items!=null&&items.size()>0){
					LibraryDocument dom = WBNonCaseUtils.findNonCaseById(items.get(0).getLibraryDocumentID()+"");
					sendMessage(response, dom.getLibraryDocumentHtml());
				}
			} catch (Exception e) {
				sendMessage(response, "<p>查询失败</p>");
				e.printStackTrace();
			}
		}
		
		
		
		
	}
	
}