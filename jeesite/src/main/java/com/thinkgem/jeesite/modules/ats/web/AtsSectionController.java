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
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.websocket.GlobalHandler;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsFeedback;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.ats.entity.AtsTree;
import com.thinkgem.jeesite.modules.ats.service.AtsActService;
import com.thinkgem.jeesite.modules.ats.service.AtsFeedbackService;
import com.thinkgem.jeesite.modules.ats.service.AtsSectionService;
import com.thinkgem.jeesite.modules.ats.service.AtsSignService;
import com.thinkgem.jeesite.modules.ats.service.AtsTaskService;
import com.thinkgem.jeesite.modules.ats.service.AtsTreeService;
import com.thinkgem.jeesite.modules.ats.utils.SuperCompare;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * sectionController
 * @author Chad
 * @version 2016-03-24
 */
@Controller
@RequestMapping(value = "${adminPath}/atsSection")
public class AtsSectionController extends BaseController {

	@Autowired
	private AtsSectionService atsSectionService;
	@Autowired
	private AtsActService atsActService;
	@Autowired
	private AtsTaskService atsTaskService;
	@Autowired
	private AtsSignService atsSignService;
	@Autowired
	private AtsTreeService atsTreeService;
	@Autowired
	private AtsFeedbackService atsFeedbackService;
	
	
	@ModelAttribute
	public AtsSection get(@RequestParam(required=false) String id) {
		AtsSection entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = atsSectionService.get(id);
		}
		if (entity == null){
			entity = new AtsSection();
		}
		return entity;
	}
	
	@RequiresPermissions("ats:atsSection:view")
	@RequestMapping(value = {"list", ""})
	public String list(AtsSection atsSection, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtsSection> page = atsSectionService.findPage(new Page<AtsSection>(request, response), atsSection); 
		model.addAttribute("page", page);
		return "modules/ats/atsSectionList";
	}

	@RequiresPermissions("ats:atsSection:view")
	@RequestMapping(value = "form")
	public String form(AtsSection atsSection, Model model) {
		model.addAttribute("atsSection", atsSection);
		return "modules/ats/atsSectionForm";
	}

	@RequiresPermissions("ats:atsSection:edit")
	@RequestMapping(value = "save")
	public String save(AtsSection atsSection, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, atsSection)){
			return form(atsSection, model);
		}
		atsSectionService.save(atsSection);
		addMessage(redirectAttributes, "保存section成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsSection/?repage";
	}
	
	@RequiresPermissions("ats:atsSection:edit")
	@RequestMapping(value = "delete")
	public String delete(AtsSection atsSection, RedirectAttributes redirectAttributes) {
		atsSectionService.delete(atsSection);
		addMessage(redirectAttributes, "删除section成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsSection/?repage";
	}

	/**
	 * 通过Id找到Section，点击编辑器中Section调用此方法
	 * @param section
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="findSectionById")
	public void findSectionById(AtsSection section,HttpServletRequest request,HttpServletResponse response){
		AtsAct act = atsActService.get(section.getPid()+"");
		JSONObject json = section.toJson();
		File file = null;
		json.put("state", act.getState());
		file = new File(section.getContent());
		try {
			if(file.exists()){
				String content = FileUtils.readFileToString(file);
				json.put("html", content);
			}else{
				json.put("html", "");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(section.getStatus()==3){
			AtsFeedback temp = new AtsFeedback();
			temp.setSectionId(section.getId());
			List<AtsFeedback> feedbacks = atsFeedbackService.findList(temp);
			JSONArray fs = new JSONArray();
			for(AtsFeedback feedback:feedbacks){
				if(feedback.getType()==7){
					file = new File("C:\\ATS\\Feedback\\"+section.getEditor()+"\\"+section.getId()+".html");
					try {
						json.put("html", FileUtils.readFileToString(file));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				fs.put(feedback.getType());
			}
			json.put("feedback", fs);
		}
		json.put("filePath", act.getDownloadFile());
		System.out.println(json);
		sendMessage(response, json.toString());
	}
	
	/**
	 * 保存正文
	 * @param section
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="saveContent")
	public void saveContent(AtsSection section,HttpServletRequest request,HttpServletResponse response){
		if(section.getStatus()!=1){
			section.setStatus(1);
			atsSectionService.save(section);
		}
		JSONObject json = new JSONObject();
		String html = request.getParameter("html");
		html = RegexUtil.replace("\n", "", html);
		html = RegexUtil.replace("&nbsp;", " ", html);
		html = RegexUtil.replace("<p>", "\n<p>", html);
		html = RegexUtil.replace("§", "&#167;", html);
		html = RegexUtil.replace("&lt;(.*?)&gt;", "<$1>", html);
		html = RegexUtil.replace("</?mark>", "", html);
		html = RegexUtil.replace("ff0000", "f00", html);      
    	html = RegexUtil.replace("'#f00'", "\"#f00\"", html); 
		html = RegexUtil.replace("<u[^>]+>(.*?)</u>", "<font color=\"#f00\"><u>$1</u></font>", html);
    	html = RegexUtil.replace("</u></font><font color=\"#f00\"><u>", "", html);
    	html = RegexUtil.replace("</strike><strike>", "", html);
    	html = RegexUtil.replace("<u>\\s*", "<u>", html);
    	html = RegexUtil.replace("<p>\\s*", "<p>", html);
    	html = RegexUtil.replace("</strike>(\\s*?)<strike>", "$1", html);
    	html = RegexUtil.replace("</u>(\\s*?)<u>", "$1", html);
    	html = RegexUtil.replace("</font>(\\s*?)<font color=\"#f00\">", "$1", html);
    	//剔除任意空标签
    	while (RegexUtil.isFind("<([^t/][^>]*?)(\\s[^>]*?)?>\\s*?</\\1>", html)) {
    		html = RegexUtil.replace("<([^t/][^>]*?)(\\s[^>]*?)?>\\s*?</\\1>", "", html);
		}
		try {
			FileUtils.writeStringToFile(new File(section.getContent()), html);
			json.put("flag", true);
			json.put("msg", "保存成功");
			json.put("html", html);
			sendMessage(response, json.toString());
		} catch (IOException e) {
			json.put("flag", true);
			json.put("msg", "保存失败，请联系管理员.");
			e.printStackTrace();
		}
	}

	/**
	 * 删除不影响的Section
	 * @param section
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="removeSection")
	public void removeSection(HttpServletRequest request,HttpServletResponse response){
		String[] ids = request.getParameter("ids").split(",");
		for(int i=0;i<ids.length;i++){
			AtsSection section = atsSectionService.get(ids[i]);
			section.setIsDel(1);
			atsSectionService.save(section);
		}
	}
	
	/**
	 * 根据Act Id 批量修改关键信息
	 * @param act
	 * @param request
	 * @param response
	 */
	@RequestMapping("modifyKeyInforBatch")
	public void modifyKeyInforBatch(HttpServletRequest request,HttpServletResponse response){
		AtsSection section = new AtsSection();
		section.setPid(request.getParameter("pid"));
		String key = StringUtils.toUpperCaseFirstLetter(request.getParameter("key"));
		try {
			section.getClass().getDeclaredMethod("set"+key, String.class).invoke(section, request.getParameter("value"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean flag = atsSectionService.modifyKeyInforBatch(section);
		if(flag){
			sendMessage(response, "修改成功！");	
		}else{
			sendMessage(response, "修改失败！");
		}
		
		

				
	}

	/**
	 * 批量添加repeal节点
	 */
	@RequestMapping(value="repealBath")
	public void repealBath(HttpServletRequest request, HttpServletResponse response){
		AtsSign sign = atsSignService.get(request.getParameter("sid"));
		AtsAct act = atsActService.get(sign.getPid());
		String shortName = atsTaskService.getByState(act.getState()).getShortName();
		atsSectionService.repealBach(request.getParameter("value"), act, sign.getId(), shortName, Integer.parseInt(request.getParameter("type")));
		sendMessage(response, sign.getId());
	}

	/**
	 * 提交Section
	 * @param section
	 * @param request
	 * @param response
	 */
//	@RequestMapping(value="submitSection")
//	public void submitSection(AtsSection section, HttpServletRequest request,HttpServletResponse response){
//		// 获取Act
//		AtsAct act = atsActService.get(section.getPid()+"");
//		String flag = request.getParameter("flag");
//		//个人全部提交，更改个人数据状态
//		if(flag.equals("true")){
//			AtsSign sign = new AtsSign(); 
//			sign.setEditor(UserUtils.getUser().getName());
//			sign.setPid(section.getPid()+"");
//			sign = atsSignService.get(sign);
//			sign.setStatus("3");
//			atsSignService.save(sign);
//		}
//		// 首次提交
//		section.setStatus(2);
//		atsSectionService.save(section);
//		//首先判断act是否被两人签名
//		AtsSign t = new AtsSign();
//		t.setPid(section.getPid()+"");
//		if(atsSignService.findList(t).size()==2){
//			//判断是否满足对比条件
//			t = new AtsSign();
//			t.setStatus("3");
//			t.setPid(section.getPid()+"");
//			if(atsSignService.findList(t).size()==2){
//				AtsSection temp = new AtsSection();
//				temp.setPid(section.getPid());
//				List<AtsSection> sections = atsSectionService.findList(temp);
//				List<AtsSection> sl1 = new ArrayList<AtsSection>();
//				List<AtsSection> sl2 = new ArrayList<AtsSection>();
//				for(AtsSection s:sections){
//					if(s.getEditor().equals(UserUtils.getUser().getName())){
//						sl1.add(s);
//					}else{
//						sl2.add(s);
//					}
//				}
//				int r = 0;
//				if(sl1.size()!=sl2.size()){
//					//双方Section数量不一致
//					if(sl2.size()==0){
//						sendMessage(response, section.getId());
//						return ;
//					}else{
//						AtsFeedback f1 = new AtsFeedback();
//						f1.setActId(section.getPid()+"");
//						f1.setSectionId("");
//						f1.setEditor(sl1.get(0).getEditor());
//						f1.setType(8);
//						f1.setStatus(0);
//						f1.setContent("Section数量不一致，本人为"+sl1.size()+",对方为"+sl2.size());
//						f1.setDay(DateUtils.getDate("yyyyMMdd hh:mm:ss"));
//						f1.setChecker("管理员");
//						atsFeedbackService.save(f1);
//						AtsFeedback f2 = new AtsFeedback();
//						f2.setActId(section.getPid()+"");
//						f2.setSectionId("");
//						f2.setEditor(sl2.get(0).getEditor());
//						f2.setType(8);
//						f2.setStatus(0);
//						f2.setContent("Section数量不一致，本人为"+sl2.size()+",对方为"+sl1.size());
//						f2.setDay(DateUtils.getDate("yyyyMMdd hh:mm:ss"));
//						f2.setChecker("管理员");
//						atsFeedbackService.save(f2);
//						r = 1;
//					}
//				}else{
//					for(int i=0;i<sl1.size();i++){
//						AtsSection s1 = sl1.get(i);
//						boolean found = false;
//						for(int j=0;j<sl2.size();j++){
//							AtsSection s2 = sl2.get(j);
//							if(s1.getShortName().equals(s2.getShortName())){
//								found = true;
//								//对比关键信息
//								boolean f1 = compareKeyInformation(s1,s2);
//								//对比正文
//								boolean f2 = false;
//								try {
//									f2 = compareContent(s1, s2);
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
//								if(!f2){
//									List<AtsFeedback> feedbacks = new ArrayList<AtsFeedback>();
//									addFeedback(s1, s1.getCaption()+": 正文对比存在差异，请查看详细对比结果", 7, feedbacks);
//									addFeedback(s2, s2.getCaption()+": 正文对比存在差异，请查看详细对比结果", 7, feedbacks);
//									for(AtsFeedback f:feedbacks){
//										atsFeedbackService.save(f);
//									}
//								}
//								if(!f1||!f2){
//									s1.setStatus(3);
//									s2.setStatus(3);
//									atsSectionService.save(s1);
//									atsSectionService.save(s2);
//									r++;
//								}
//								break;
//							}
//						}
//						if(!found){
//							System.out.println("没有发现caption");
//						}
//					}
//				}
//				if(r!=0){
//					//给双方发送对比反馈信息
//					GlobalHandler.sendMessage(sl1.get(0).getEditor(), "您有新的反馈信息，请及时查看！", "管理员");
//					GlobalHandler.sendMessage(sl2.get(0).getEditor(), "您有新的反馈信息，请及时查看！", "管理员");
//					act.setStatus(3);
//				}else{
//					act.setStatus(4);
//				}
//				atsActService.save(act);
//				GlobalHandler.invokeJsMethod(sl1.get(0).getEditor(), "removeTreeNode", section.getPid()+"");
//				GlobalHandler.invokeJsMethod(sl2.get(0).getEditor(), "removeTreeNode", section.getPid()+"");
//			}
//		}
//		sendMessage(response, section.getId());
//	}
	
	/**
	 * 二次提交
	 * @param section
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	@RequestMapping("submitFeedback")
	public void submitFeedback(AtsSection section1, HttpServletRequest request, HttpServletResponse response) throws IOException{
		//将关于此section已有的反馈标为已修改
		AtsFeedback feedback = new AtsFeedback();
		feedback.setSectionId(section1.getId());
		List<AtsFeedback> feedbacks = atsFeedbackService.findList(feedback);
		for(AtsFeedback f:feedbacks){
			f.setStatus(1);
			atsFeedbackService.save(f);
		}
		//获取对方section是否满足二次对比条件
		AtsSection section2 = atsSectionService.findCompareSection(section1.getPid(), section1.getShortName(),section1.getEditor());
		if(section2==null){
			//对方未修改
			section1.setStatus(5);
			atsSectionService.save(section1);
		}else{
			//二次对比
			boolean f1 = compareKeyInformation(section1, section2);
			boolean f2 = compareContent(section1, section2);
			if(f1&&f2){
				//二次对比通过
				AtsAct act = atsActService.get(section1.getPid()+"");
				act.setStatus(4);
				atsActService.save(act);
				GlobalHandler.sendMessage(section1.getEditor(), "对比通过", "管理员");
			}else{
				section2.setStatus(3);
				atsSectionService.save(section2);
				GlobalHandler.sendMessage(section1.getEditor(), "您有新的反馈信息，请及时查看！", "管理员");
				GlobalHandler.sendMessage(section2.getEditor(), "您有新的反馈信息，请及时查看！", "管理员");
			}
		}
	}
	/**
	 * 对比关键信息
	 * @param s1
	 * @param s2
	 * @return
	 */
	public boolean compareKeyInformation(AtsSection s1,AtsSection s2){
		boolean flag = true;
		List<AtsFeedback> feedbacks = new ArrayList<AtsFeedback>();
		if(!s1.getCaption().equals(s2.getCaption())){
			addFeedback(s1, s1.getCaption()+": Caption不一致，请检查。", 1, feedbacks);
			addFeedback(s2, s2.getCaption()+": Caption不一致，请检查。", 1, feedbacks);
			flag = false;
		}
		if(s1.getDescription()==null){
			s1.setDescription("");
		}
		if(s2.getDescription()==null){
			s2.setDescription("");
		}
		if(!s1.getDescription().equals(s2.getDescription())){
			addFeedback(s1, s1.getCaption()+": Description不一致，请检查。", 2, feedbacks);
			addFeedback(s2, s2.getCaption()+": Description不一致，请检查。", 2, feedbacks);
			flag = false;
		}
		if(!s1.getEff().equals(s2.getEff())){
			addFeedback(s1, s1.getCaption()+": 生效日期不一致，请检查。", 3, feedbacks);
			addFeedback(s2, s2.getCaption()+": 生效日期不一致，请检查。", 3, feedbacks);
			flag = false;
		}
//		if(!s1.getLink().equals(s2.getLink())){
//			addFeedback(s1, s1.getCaption()+": Source Note Link 不一致，请检查。", 1, feedbacks);
//			addFeedback(s2, s2.getCaption()+": Source Note Link 不一致，请检查。", 1, feedbacks);
//			flag = false;
//		}
		if(s1.getExp()!=null||s2.getExp()!=null){
			String exp1 = s1.getExp();
			String exp2 = s2.getExp();
			exp1 = exp1==null?"":exp1;
			exp2 = exp2==null?"":exp2;
			if(!exp1.equals(exp2)){
				addFeedback(s1, s1.getCaption()+": 失效日期不一致，请检查。", 5, feedbacks);
				addFeedback(s2, s2.getCaption()+": 失效日期不一致，请检查。", 5, feedbacks);
				flag = false;
			}
		}
		if(!s1.getUpdateType().equals(s2.getUpdateType())){
			addFeedback(s1, s1.getCaption()+": Update不一致，请检查。", 6, feedbacks);
			addFeedback(s2, s2.getCaption()+": Update不一致，请检查。", 6, feedbacks);
			flag = false;
		}
		for(AtsFeedback feedback:feedbacks){
			atsFeedbackService.save(feedback);
		}
		return flag;
	}

	/**
	 * 对比正文
	 * @param s1
	 * @param s2
	 * @return
	 * @throws IOException 
	 */
	public boolean compareContent(AtsSection s1,AtsSection s2) throws IOException{
		SuperCompare cu1 = new SuperCompare(new File(s1.getContent()));
		SuperCompare cu2 = new SuperCompare(new File(s2.getContent()));
		cu1.compare(cu2);
		cu2.compare(cu1);
		if(!cu1.isPass()){
			try {
				FileUtils.writeStringToFile(new File("C:\\ATS\\Feedback\\"+s1.getEditor()+"\\"+s1.getId()+".html"), cu1.getResult().toString());
				FileUtils.writeStringToFile(new File("C:\\ATS\\Feedback\\"+s2.getEditor()+"\\"+s2.getId()+".html"), cu2.getResult().toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cu1.isPass();
	}

	/**
	 * 合并节点
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="combineSections")
	public void combineSections(HttpServletRequest request,HttpServletResponse response){
		String  param = request.getParameter("ids");
		param = RegexUtil.replace("\\[|\\]|\"", "", param);
		String[] ids = param.split(",");
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<ids.length;i++){
			AtsSection section = atsSectionService.get(ids[i]);
			File file = new File(section.getContent());
			try {
				String html = FileUtils.readFileToString(file);
				sb.append(html);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(i!=0){
				section.setIsDel(1);
				atsSectionService.save(section);
				AtsTree tree = new AtsTree();
				tree.setFid(section.getId());
				tree.setMethod("showEditor");
				tree = atsTreeService.get(tree);
				atsTreeService.delete(tree);
			}
		}
		AtsSection section = atsSectionService.get(ids[0]);
		AtsAct act = atsActService.get(atsSignService.get(section.getPid()).getPid());
		File f = new File(section.getContent());
		try {
			FileUtils.writeStringToFile(f, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		section.setContent(sb.toString());
		JSONObject json = new JSONObject(JsonMapper.toJsonString(section));
		json.put("state", act.getState());
		json.put("href", act.getUrl());
		json.put("type", act.getType());
		json.put("snapshot", act.getDownloadFile());
		System.out.println(json.toString());
		sendMessage(response, json.toString());
	}
	
	public List<AtsFeedback> addFeedback(AtsSection s,String content,int type,List<AtsFeedback> list){
		AtsFeedback f = new AtsFeedback();
		f.setActId(s.getPid()+"");
		f.setSectionId(s.getId());
		f.setEditor(s.getEditor());
		f.setType(type);
		f.setStatus(0);
		f.setContent(content);
		f.setDay(DateUtils.getDate("yyyyMMdd hh:mm:ss"));
		f.setChecker("管理员");
		list.add(f);
		return list;
	}
	
	@RequestMapping(value="getFeedbackTree")
	public void getFeedbackTree(HttpServletRequest request, HttpServletResponse response){
		AtsSection section = new AtsSection();
		section.setPid(request.getParameter("actId"));
		section.setEditor(UserUtils.getUser().getName());
		section.setIsDel(0);
		section.setStatus(3);
		
		List<AtsSection> list = atsSectionService.findList(section);
		JSONObject root = new JSONObject();
		root.put("name", "Feedbacks");
		JSONArray array = new JSONArray();
		root.put("children", array);
		for(AtsSection s : list){
			JSONObject json = new JSONObject();
			json.put("id", s.getId());
			json.put("name", s.getCaption());
			array.put(json);
		}
		sendMessage(response, root.toString());
	}
	
	@RequestMapping(value="getFixSectionData")
	public void getFixSectionData(AtsSection section, HttpServletResponse response) throws IOException{
		String content = FileUtils.readFileToString(new File(section.getContent()));
		JSONObject json = new JSONObject();
		json.put("description", section.getDescription());
		json.put("effectiveDate", section.getEff());
		json.put("expirationDate", section.getExp());
		json.put("link", section.getLink());
		json.put("shortName", section.getShortName());
		json.put("updateType", section.getUpdateType());
		json.put("content", content);
		sendMessage(response, json.toString());
	}
	
	//---------------------------------------------------------------------------------------------------------------------------------------------------------------

	/**
	 * 保存正文
	 * @param section
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="saveSection")
	public void saveSection(AtsSection section ,HttpServletRequest request, HttpServletResponse response){
		String html = request.getParameter("html");
		try {
			FileUtils.writeStringToFile(new File(section.getContent()), html);
			sendMessage(response, "Save Success!");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存关键信息
	 * @param section
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="saveKeyInfor")
	public void saveKeyInfor(AtsSection section, HttpServletRequest request, HttpServletResponse response){
		atsSectionService.save(section);
		AtsTree tree = new AtsTree();
		tree.setFid(section.getId());
		tree.setMethod("showEditor");
		tree = atsTreeService.get(tree);
		tree.setName(section.getCaption());
		atsTreeService.save(tree);
		sendMessage(response, "Save Success!");
	}
	
	/**
	 * Section 提交 (Section对比可搜submitSection)
	 * @param section
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="submitSection")
	public void submitSection(AtsSection section, HttpServletRequest request, HttpServletResponse response){
		AtsTree tree = atsTreeService.get(request.getParameter("tid"));
		boolean flag = atsSectionService.doSubmit(section, tree);
		if(flag){
			String actId = atsSignService.get(section.getPid()).getPid();
			AtsAct act = atsActService.get(actId);
			act.setStatus(3);
			atsActService.save(act);
		}
		sendMessage(response, new JSONObject().put("finish", flag).toString());
	}
	
	@RequestMapping(value="refreshSection")
	public void refreshSection(AtsSection section, HttpServletResponse response){
		AtsSign sign = atsSignService.get(section.getPid());
		AtsAct act = atsActService.get(sign.getPid());
		try {
			section = (AtsSection) Class.forName("com.thinkgem.jeesite.modules.utils."+act.getState()).getMethod("refreshSection", AtsSection.class).invoke(null, section);
			JSONObject json = new JSONObject(JsonMapper.toJsonString(section));
			json.put("type", act.getType());
			json.put("state", act.getState());
			json.put("href", act.getUrl());
			json.put("snapshot", act.getDownloadFile());
			sendMessage(response, json.toString());
			String filepath = section.getRemarks();
			section.setRemarks("");
			section.setContent(filepath);
			atsSectionService.save(section);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	
	@RequestMapping(value="deleteSection")
	public void deleteSection(AtsSection section, HttpServletRequest request, HttpServletResponse response){
		section.setIsDel(1);
		atsSectionService.save(section);
		AtsSection temp = new AtsSection();
		temp.setPid(section.getPid());
		temp.setIsDel(0);
		//更改tree数据
		AtsTree tree = new AtsTree();
		tree.setFid(section.getId());
		tree.setMethod("showEditor");
		tree = atsTreeService.get(tree);
		tree.setIsHidden("1");
		atsTreeService.save(tree);
		int count = atsSectionService.findList(temp).size();
		if(count==0){
			AtsAct act = atsActService.get(section.getPid()+"");
			act.setDelFlag("1");
			atsActService.save(act);
			//更改tree数据
			AtsTree treeTemp = atsTreeService.get(tree.getPid());
			treeTemp.setIsHidden("1");
			atsTreeService.save(treeTemp);
		}
		JSONObject json = new JSONObject();
		json.put("count", count);
		json.put("id", section.getId());
		sendMessage(response, json.toString());
	}
	
	@RequestMapping(value="modifyKeyInforBat")
	public void modifyKeyInforBat(HttpServletRequest request, HttpServletResponse response){
		AtsSection temp = new AtsSection();
		String type = request.getParameter("type");
		temp.setPid(request.getParameter("pid"));
		temp.setIsDel(0);
		temp.setEditor(UserUtils.getUser().getName());
		List<AtsSection> list = atsSectionService.findList(temp);
		for(AtsSection sec: list){
			try {
				Class.forName("com.thinkgem.jeesite.modules.ats.entity.AtsSection").getDeclaredMethod("set"+type,String.class).invoke(sec, request.getParameter("value"));
				atsSectionService.save(sec);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sendMessage(response, "Save Success!");
	}
	
	@RequestMapping(value="addSection")
	public void addSection(AtsSection section, HttpServletResponse response ){
		section.setId(null);
		AtsSection temp = new AtsSection();
		temp.setPid(section.getPid());
		temp.setEditor(UserUtils.getUser().getName());
		int parseOrder = atsSectionService.findList(temp).size(); 
		section.setParseOrder(parseOrder);
		section.setShortName(RegexUtil.replace(section.getCaption(), "", section.getShortName()));
		section.setCaption(section.getBillNumber());
		section.setStatus(1);
		section.setUpdateType(1);
		section.setIsAdd(1);
		String content = section.getContent();
		content = RegexUtil.replace("\\d+?\\.html", section.getCaption()+"_"+parseOrder+".html", content);
		section.setContent(content);
		section.setDescription("");
		atsSectionService.save(section);
		//添加tree数据
		AtsTree tempTree = new AtsTree();
		tempTree.setFid(section.getPid());
		tempTree.setIsParent("1");
		tempTree = atsTreeService.get(tempTree);
		AtsTree tree = new AtsTree();
		tree.setName(section.getBillNumber());
		tree.setFid(section.getId());
		tree.setIsParent("0");
		tree.setPid(tempTree.getId());
		tree.setStatus("1");
		tree.setMethod("showEditor");
		atsTreeService.save(tree);
		//返回数据
		JSONObject msg = new JSONObject();
		JSONObject sectionJson = new JSONObject(JsonMapper.toJsonString(section));
		msg.put("section", sectionJson);
		JSONObject treeJson = new JSONObject(JsonMapper.toJsonString(tree));
		treeJson.put("isParent", "false");
		treeJson.put("iconSkin", "file");
		msg.put("tree", treeJson);
		//更改sectionCount
		AtsSign sign = atsSignService.get(section.getPid());
		sign.setSectionCount(sign.getSectionCount()+1);
		atsSignService.save(sign);
		sendMessage(response, msg.toString());
	}
}