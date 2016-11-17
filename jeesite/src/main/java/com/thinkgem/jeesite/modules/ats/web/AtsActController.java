/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocument;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentUpdate;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.utils.UploadUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.webservice.WBNonCaseUtils;
import com.thinkgem.jeesite.modules.acttostatute.entity.NonCaseUsEdition;
import com.thinkgem.jeesite.modules.acttostatute.service.NonCaseUsEditionService;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.ats.entity.AtsTree;
import com.thinkgem.jeesite.modules.ats.service.AtsActService;
import com.thinkgem.jeesite.modules.ats.service.AtsSectionService;
import com.thinkgem.jeesite.modules.ats.service.AtsSignService;
import com.thinkgem.jeesite.modules.ats.service.AtsTaskService;
import com.thinkgem.jeesite.modules.ats.service.AtsTreeService;
import com.thinkgem.jeesite.modules.tool.AtsTool;

/**
 * actController
 * @author Chad
 * @version 2016-03-09
 */
@Controller
@RequestMapping(value = "${adminPath}/atsAct")
public class AtsActController extends BaseController {

	@Autowired
	private AtsActService atsActService;
	@Autowired
	private AtsSectionService atsSectionService;
	@Autowired
	private AtsTaskService atsTaskService;
	@Autowired
	private AtsSignService atsSignService;
	@Autowired
	private AtsTreeService atsTreeService;
	@Autowired
	private NonCaseUsEditionService nonCaseUsEditionService;
	
	@ModelAttribute
	public AtsAct get(@RequestParam(required=false) String id) {
		AtsAct entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = atsActService.get(id);
		}
		if (entity == null){
			entity = new AtsAct();
		}
		return entity;
	}
	
	@RequiresPermissions("ats:atsAct:view")
	@RequestMapping(value = {"list", ""})
	public String list(AtsAct atsAct, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<AtsAct> page = atsActService.findPage(new Page<AtsAct>(request, response), atsAct); 
		model.addAttribute("page", page);
		return "modules/ats/atsActList";
	}

	
	
	@RequiresPermissions("ats:atsAct:view")
	@RequestMapping(value = "form")
	public String form(AtsAct atsAct, Model model) {
		model.addAttribute("atsAct", atsAct);
		return "modules/ats/atsActForm";
	}
	
	@RequestMapping("testForm")
	public String testForm(Model model) {
		return "modules/ats/testForm";
	}
	
	@RequestMapping(value="receiveData")
	public String receiveData(HttpServletRequest request, Model model){
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String sex = request.getParameter("more");
		System.out.println(sex);
		if(username.equals(password)){
			model.addAttribute("msg","登录成功");
		}else{
			model.addAttribute("msg","登录失败");
		}
		return "modules/ats/loginResult";
	}

	@RequiresPermissions("ats:atsAct:edit")
	@RequestMapping(value = "save")
	public String save(AtsAct atsAct, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, atsAct)){
			return form(atsAct, model);
		}
		atsActService.save(atsAct);
		addMessage(redirectAttributes, "保存act成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsAct/?repage";
	}
	
	@RequiresPermissions("ats:atsAct:edit")
	@RequestMapping(value = "delete")
	public String delete(AtsAct atsAct, RedirectAttributes redirectAttributes) {
		atsActService.delete(atsAct);
		addMessage(redirectAttributes, "删除act成功");
		return "redirect:"+Global.getAdminPath()+"/ats/atsAct/?repage";
	}

	/**
	 * 创建新的Section
	 * @param act
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="createNewSection")
	public void createNewSection(AtsAct act,HttpServletRequest request, HttpServletResponse response){
		JSONObject json = new JSONObject();
		int parseOrder = atsSectionService.findMaxParseOrder(act.getId());
		String shortName = atsTaskService.getByState(act.getState()).getShortName();
		AtsSection section =  atsSectionService.addNewSection(parseOrder, shortName, act);
		json.put("id", section.getId());
		json.put("type", act.getType()+"");
		json.put("pid", act.getId());
		json.put("name", section.getCaption());
		sendMessage(response, json.toString());
	}
	
	/**
	 * 查看Bill 状态历史页面
	 * @param act
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="viewBillHistory")
	public void viewBillHistory(AtsAct act,HttpServletRequest request,HttpServletResponse response){
		JSONObject json = new JSONObject();
		String url = act.getUrl();
		try {
			Document dom = Jsoup.connect(url).timeout(10000).get();
			String html = dom.html();
			html = RegexUtil.replace("<a[^>]*?>|</a>", "", html);
			html = RegexUtil.replace("<img[^>]*?>", "", html);
			html = RegexUtil.replace("<input[^>]*?>", "", html);
			html = RegexUtil.replace("<select[^>]*?>[\\s\\S]*?</select>", "", html);
			html = RegexUtil.replace("<link[^>]*?>", "", html);
			html = RegexUtil.replace("(Approved)", "<mark>$1</mark>", html);
			
			json.put("html", html);
		} catch (IOException e) {
			e.printStackTrace();
			json.put("html", "<p style='font-weight:bold'>链接失败，请重试或跳到<a href='"+url+"' target='blank'>外部链接</a></p>");
		}
		sendMessage(response, json.toString());
		
		
		
		
		
	}

	/**
	 * 下载PDF文件
	 * @param act
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="downloadAct")
	public void downloadAct(HttpServletRequest request,HttpServletResponse response){
		AtsSign sign = atsSignService.get(request.getParameter("sid"));
		AtsAct act = atsActService.get(sign.getPid());
		File file = new File(act.getDownloadFile());
		response.setHeader("Location", file.getName());
		response.setHeader("Cache-Control", "max-age=" + 100000);
		response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
		response.setContentLength((int)file.length());
		try {
			OutputStream outputStream = response.getOutputStream();
			InputStream inputStream = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			int i = -1;
			while ((i = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, i);
			}
			outputStream.flush();
			outputStream.close();
			inputStream.close();
			outputStream = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 上传HTML文件，进行解析
	 * @param act
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="uploadAct")
	public void uploadAct(HttpServletRequest request,HttpServletResponse response){
		String treeId = request.getParameter("treeId");
		AtsSign sign = atsSignService.get(request.getParameter("sid"));
		AtsAct act = atsActService.get(sign.getPid());
		UploadUtils uploadUtils = new UploadUtils(act.getBillNumber());
		request.setAttribute("day", act.getDay());
		request.setAttribute("state", act.getState());
		String[] infors = uploadUtils.uploadFile(request);
		//上传成功
		if(infors[0].equals("true")){
			//删除历史数据
			AtsSection temp = new AtsSection();
			temp.setPid(sign.getId());
			temp.setEditor(sign.getEditor());
			List<AtsSection> historyList = atsSectionService.findList(temp);
			for(AtsSection section:historyList){
				AtsTree tree = new AtsTree();
				tree.setFid(section.getId());
				tree.setMethod("showEditor");
				atsTreeService.delete(tree);
				atsSectionService.delete(section);
			}
			//获取并更新签名数据
			sign.setUploadFile(infors[1]+File.separator+infors[2]+".html");
			//开始解析
			try {
				Class.forName("com.thinkgem.jeesite.modules.utils."+act.getState()).getMethod("parseAct",AtsSign.class,String.class,String.class).invoke(null,sign,act.getBillNumber(),treeId);
				AtsTree tree = atsTreeService.get(treeId);
				tree.setIconSkin("1");
				tree.setIsParent("1");
				atsTreeService.save(tree);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sendMessage(response, "Upload success!");
	}

	/**
	 * 标记废除的ACT
	 * @param act
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="deleteAct")
	public void deleteAct(AtsAct act,HttpServletRequest request,HttpServletResponse response){
		act.setDelFlag("1");
		atsActService.save(act);
		AtsTree tree = new AtsTree();
		tree.setFid(act.getId());
		tree.setMethod("viewAct");
		tree = atsTreeService.get(tree);
		tree.setIsHidden("1");
		atsTreeService.save(tree);
		sendMessage(response, tree.getId());
	}
	
	@RequestMapping(value="integration")
	public String integrationTool(Model model){
		// 获取州列表
		List<String> states = AtsTool.getStateList();
		model.addAttribute("states",states);
		return "modules/ats/integration";
	}
	
	/**
	 * 获取Statute树根
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="getStatuteTree")
	public void getStatuteTree(HttpServletRequest request, HttpServletResponse response){
		NonCaseUsEdition edition = new NonCaseUsEdition();
		String state = request.getParameter("state");
		if(state!=null){
			edition.setState(state);
			List<NonCaseUsEdition> edtions = nonCaseUsEditionService.findList(edition);
			JSONObject root = new JSONObject();
			JSONArray children = new JSONArray();
			root.put("name", edtions.get(0).getLibraryName());
			root.put("children", children);
			for(NonCaseUsEdition e:edtions){
				JSONObject json = new JSONObject();
				json.put("name", e.getLibraryEdition());
				json.put("editionId", e.getEditionId());
				json.put("isParent", true);
				children.put(json);
			}
			sendMessage(response, root.toString());
		}
	}
	
	/**
	 * 异步加载Statute树数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="loadStatuteTreeData")
	public void loadStatuteTreeData(HttpServletRequest request, HttpServletResponse response){
		String nodeId = request.getParameter("nodeId");
		String editionId = request.getParameter("editionId");
		JSONArray array = AtsTool.getOutlineView(nodeId, editionId);
		if(array==null){
			sendMessage(response, "加载失败");
		}else{
			sendMessage(response, array.toString());
		}
	}
	
	@RequestMapping(value="showSection")
	public void showSection(HttpServletRequest request, HttpServletResponse response){
		String nodeId = request.getParameter("nodeId");
		try {
			LibraryDocument dom = WBNonCaseUtils.findNonCaseById(nodeId);
			String json = JsonMapper.toJsonString(dom);
			sendMessage(response, json);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	@RequestMapping(value="getFeedbackDataSync")
//	public void getDataSync(HttpServletRequest request, HttpServletResponse response){
//		List<Map<String, String>> list = atsActService.getFeedbackData(UserUtils.getUser().getName());
//		String json = JsonMapper.toJsonString(list);
//		System.out.println(json);
//		sendMessage(response, json);
//		
//	}
	
	@RequestMapping(value="findFixSection")
	public void findFixSection(HttpServletRequest req, HttpServletResponse res){
		try {
			LibraryDocument dom = WBNonCaseUtils.findNonCaseById(req.getParameter("sid"));
			LibraryDocumentUpdate[] updates = dom.getDocumentUpdates().getLibraryDocumentUpdate();
			if(updates==null){
				JSONObject json = new JSONObject();
				json.put("content", dom.getLibraryDocumentHtml());
				sendMessage(res, json.toString());
			}else{
				sendMessage(res, JsonMapper.toJsonString(updates));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="doFixSection")
	public void doFixSection(HttpServletRequest request, HttpServletResponse response){
		//正常修复
		
	}
	
	
	//------------------------------------------------------------------------------
	@RequestMapping(value="getActById")
	public void getActById(HttpServletRequest request, HttpServletResponse response){
		AtsAct act = atsActService.get(request.getParameter("sid"));
		sendMessage(response, JsonMapper.toJsonString(act));
	}
	
}