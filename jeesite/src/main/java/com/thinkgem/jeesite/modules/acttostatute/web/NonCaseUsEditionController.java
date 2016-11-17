/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.acttostatute.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fastcase.services._2009._03._06.ResearchServicesStub.Library;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.webservice.WBNonCaseUtils;
import com.thinkgem.jeesite.modules.acttostatute.entity.NonCaseUsEdition;
import com.thinkgem.jeesite.modules.acttostatute.service.NonCaseUsEditionService;

/**
 * NonCaseUSEditionController
 * @author Sid
 * @version 2015-08-03
 */
@Controller
@RequestMapping(value = "${adminPath}/acttostatute/nonCaseUsEdition")
public class NonCaseUsEditionController extends BaseController {

	@Autowired
	private NonCaseUsEditionService nonCaseUsEditionService;
	
	@ModelAttribute
	public NonCaseUsEdition get(@RequestParam(required=false) String id) {
		NonCaseUsEdition entity = null;
		if (StringUtils.isNotBlank(id)){
			NonCaseUsEdition nonCaseUsEdition=new NonCaseUsEdition();
			nonCaseUsEdition.setId(id);
			entity = nonCaseUsEditionService.get(nonCaseUsEdition);
		}
		if (entity == null){
			entity = new NonCaseUsEdition();
		}
		return entity;
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(NonCaseUsEdition nonCaseUsEdition, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<NonCaseUsEdition> page = nonCaseUsEditionService.findPage(new Page<NonCaseUsEdition>(request, response), nonCaseUsEdition); 
		model.addAttribute("page", page);
		return "modules/acttostatute/nonCaseUsEditionList";
	}

	@RequestMapping(value = "form")
	public String form(NonCaseUsEdition nonCaseUsEdition, Model model) {
		model.addAttribute("nonCaseUsEdition", nonCaseUsEdition);
		return "modules/acttostatute/nonCaseUsEditionForm";
	}

	@RequestMapping(value = "save")
	public String save(NonCaseUsEdition nonCaseUsEdition, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, nonCaseUsEdition)){
			return form(nonCaseUsEdition, model);
		}
		nonCaseUsEditionService.save(nonCaseUsEdition);
		addMessage(redirectAttributes, "保存NonCaseUSEdition成功");
		return "redirect:"+Global.getAdminPath()+"/acttostatute/nonCaseUsEdition/?repage";
	}
	
	@RequestMapping(value = "delete")
	public String delete(NonCaseUsEdition nonCaseUsEdition, RedirectAttributes redirectAttributes) {
		nonCaseUsEditionService.delete(nonCaseUsEdition);
		addMessage(redirectAttributes, "删除NonCaseUSEdition成功");
		return "redirect:"+Global.getAdminPath()+"/acttostatute/nonCaseUsEdition/?repage";
	}
	
	@RequestMapping(value = "mark")
	public void mark(NonCaseUsEdition nonCaseUsEdition, RedirectAttributes redirectAttributes) {
		nonCaseUsEditionService.save(nonCaseUsEdition);
	}
	
	@RequestMapping(value = "synchrodata")
	public String synchrodata(NonCaseUsEdition nonCaseUsEdition, Model model, RedirectAttributes redirectAttributes) throws Exception {
		Library[] libraries=WBNonCaseUtils.queryNonCaseUSEdtion();
		for(Library library:libraries)
		{
			if(!RegexUtil.isFind("HeinOnline", library.getLibraryName()))
			{
				nonCaseUsEdition.setContentType("Statutes");
				nonCaseUsEdition.setLibraryEdition(library.getLibraryEditionDescription());
				nonCaseUsEdition.setLibraryName(library.getLibraryName());
				nonCaseUsEdition.setLibrarySourceConst(library.getLibrarySourceConst());
				nonCaseUsEdition.setJurisdictionId(library.getJurisdictionID());
				nonCaseUsEdition.setEditionId(library.getLibraryEditionID());
				nonCaseUsEdition.setStatus(0);
				String editionYear=RegexUtil.match("(?<=\\()\\d{4}", library.getLibraryEditionDescription());
 				nonCaseUsEdition.setEditionYear(editionYear);
				nonCaseUsEdition.setEditionDate("01/01/"+editionYear);
 				NonCaseUsEdition ncue=new NonCaseUsEdition();
 				ncue.setLibraryEdition(library.getLibraryEditionDescription());
 				ncue=nonCaseUsEditionService.get(ncue);
 				if(ncue==null)
 				{
 	 				nonCaseUsEdition.setIsNewRecord(true);
 				}
 				else {
 					nonCaseUsEdition.setId(ncue.getId());
 					nonCaseUsEdition.setIsNewRecord(false);
				}
 				Integer yearInteger=Integer.valueOf(editionYear);
 				if(yearInteger>2013||RegexUtil.isFind("Oregon", library.getLibraryName())){
 					if (!RegexUtil.isFind("Act", library.getLibraryName())) {
 	 					nonCaseUsEditionService.save(nonCaseUsEdition);
					}
 				}
			}
		}
		nonCaseUsEditionService.updateStatus();
		addMessage(redirectAttributes, "保存NonCaseUSEdition成功");
		return "redirect:"+Global.getAdminPath()+"/acttostatute/nonCaseUsEdition/?repage";
	}
	
}