/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.web;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.restlet.util.StringReadingListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.ats.entity.AtsFeedback;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;
import com.thinkgem.jeesite.modules.ats.service.AtsFeedbackService;
import com.thinkgem.jeesite.modules.ats.service.AtsSectionService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * feedbackController
 * @author Chad
 * @version 2016-04-11
 */
@Controller
@RequestMapping(value = "${adminPath}/ats/atsFeedback")
public class AtsFeedbackController extends BaseController {

	@Autowired
	private AtsFeedbackService atsFeedbackService;
	@Autowired
	private AtsSectionService atsSectionService;
	
	
	@ModelAttribute
	public AtsFeedback get(@RequestParam(required=false) String id) {
		AtsFeedback entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = atsFeedbackService.get(id);
		}
		if (entity == null){
			entity = new AtsFeedback();
		}
		return entity;
	}
	
	@RequiresPermissions("ats:atsFeedback:view")
	@RequestMapping(value = {"list", ""})
	public String list(AtsFeedback atsFeedback, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtsFeedback> page = atsFeedbackService.findPage(new Page<AtsFeedback>(request, response), atsFeedback); 
		model.addAttribute("page", page);
		return "modules/ats/atsFeedbackList";
	}

	@RequiresPermissions("ats:atsFeedback:view")
	@RequestMapping(value = "form")
	public String form(AtsFeedback atsFeedback, Model model) {
		model.addAttribute("atsFeedback", atsFeedback);
		return "modules/ats/atsFeedbackForm";
	}

	@RequiresPermissions("ats:atsFeedback:edit")
	@RequestMapping(value = "save")
	public String save(AtsFeedback atsFeedback, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, atsFeedback)){
			return form(atsFeedback, model);
		}
		atsFeedbackService.save(atsFeedback);
		addMessage(redirectAttributes, "保存feedback成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsFeedback/?repage";
	}
	
	@RequiresPermissions("ats:atsFeedback:edit")
	@RequestMapping(value = "delete")
	public String delete(AtsFeedback atsFeedback, RedirectAttributes redirectAttributes) {
		atsFeedbackService.delete(atsFeedback);
		addMessage(redirectAttributes, "删除feedback成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsFeedback/?repage";
	}
	
	@RequestMapping(value="getDataByActIdSync")
	public void getDataByActIdSync(HttpServletRequest request,HttpServletResponse response){
		AtsFeedback feedback = new AtsFeedback();
		feedback.setActId(request.getParameter("actId"));
		feedback.setEditor(UserUtils.getUser().getName());
		List<AtsFeedback> list = atsFeedbackService.findList(feedback);
		StringBuffer result = new StringBuffer();
		List<String> sectionIds = new ArrayList<String>();
		for(AtsFeedback f : list){
			AtsSection section = new AtsSection();
			section.setId(f.getSectionId());
			section = atsSectionService.get(section);
			if(!sectionIds.contains(section.getId())){
				if(!sectionIds.isEmpty()){
					result.append("<hr style='width:100%'>");
				}
				result.append("<h2 style='margin: 5px'>Caption: "+section.getCaption()+"</h2>");
				feedbackReport(result,section,f);
				
				sectionIds.add(f.getSectionId());
			}else{
				feedbackReport(result,section,f);
			}
		}
		sendMessage(response, result.toString());
	}
	
	public void feedbackReport(StringBuffer result,AtsSection section, AtsFeedback f){
		if(f.getType()!= 7){
			result.append("<h2 style='color:orange;margin: 5px'>* Key Information</h2>");
			result.append(f.getContent());
		}else{
			result.append("<h2 style='color:orange;margin: 5px'>* Content</h2>");
			try {
				result.append(FileUtils.readFileToString(new File("C:\\ATS\\Feedback\\"+UserUtils.getUser().getName()+"\\"+f.getSectionId()+".html")));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	

}