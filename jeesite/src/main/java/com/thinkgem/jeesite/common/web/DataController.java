package com.thinkgem.jeesite.common.web;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocument;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentListItem;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentUpdate;
import com.fastcase.services._2009._03._06.ResearchServicesStub.OutlineViewListItem;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.webservice.WBNonCaseUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.ats.entity.AtsStatute;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;
import com.thinkgem.jeesite.modules.ats.entity.AtsTree;
import com.thinkgem.jeesite.modules.ats.service.AtsActService;
import com.thinkgem.jeesite.modules.ats.service.AtsSectionService;
import com.thinkgem.jeesite.modules.ats.service.AtsSignService;
import com.thinkgem.jeesite.modules.ats.service.AtsStatuteService;
import com.thinkgem.jeesite.modules.ats.service.AtsTaskService;
import com.thinkgem.jeesite.modules.ats.service.AtsTreeService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.utils.BasicTaskUtils;

@Controller
@RequestMapping(value = "${adminPath}/data")
public class DataController extends BaseController{
	@Autowired
	private AtsTaskService atsTaskService;
	@Autowired
	private AtsActService atsActService;
	@Autowired
	private AtsSignService atsSignService;
	@Autowired
	private AtsSectionService atsSectionService;
	@Autowired
	private AtsTreeService atsTreeService;
	@Autowired
	private AtsStatuteService atsStatuteService;
	
	@RequestMapping(value="findTreeData")
	public void findTreeData(HttpServletRequest request, HttpServletResponse response) {
		String id = request.getParameter("id");
		String method = request.getParameter("method");
		System.out.println(method);
		if(id==null){
			//获取根目录
			AtsTree root = atsTreeService.get("0");
			root.setOpen("1");
			sendMessage(response, JsonMapper.toJsonString(root));
		}else{
			if(method!=null){
				try {
					Class<? extends DataController> clazz = this.getClass();
					clazz.getMethod(method).invoke(this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			AtsTree temp = new AtsTree();
			temp.setPid(id);
			temp.setEditor(UserUtils.getUser().getName());
			List<AtsTree> nodes = atsTreeService.findList(temp);
			System.out.println(JsonMapper.toJsonString(nodes));
			sendMessage(response, JsonMapper.toJsonString(nodes));
		}
	}
	
	/**
	 * 构造All Act 树，拼接第一层：State
	 * @param array
	 */
	public void findAllActsTreeData(JSONArray array){
		List<String> states = atsActService.findUnsignedStates();
		for(String state: states){
			JSONObject json = new JSONObject();
			json.put("name", state);
			json.put("isParent", true);
			json.put("iconSkin", "folder");
			array.put(json);
			findDateByState(json);
		}
	}
	
	/**
	 * 构造All Act 树，拼接第二层：Date
	 * @param json
	 */
	public void findDateByState(JSONObject json){
		List<String> dates = atsActService.findDateByState(json.getString("name"));
		JSONArray children = new JSONArray();
		for(String date:dates){
			JSONObject child = new JSONObject();
			child.put("name", date);
			child.put("isParent", true);
			child.put("iconSkin", "folder");
			children.put(child);
			findActs(json.getString("name"), child);
		}
		json.put("children", children);
	}
	
	public void findActs(String state,JSONObject json){
		AtsAct temp = new AtsAct();
		temp.setState(state);
		temp.setDay(json.getString("name"));
		temp.setDelFlag("0");
		temp.setStatus(1);
		List<AtsAct> acts = atsActService.findList(temp);
		JSONArray children = new JSONArray();
		for(AtsAct act:acts){
			JSONObject child = new JSONObject();
			child.put("name", act.getBillNumber());
			child.put("id", act.getId());
			child.put("filePath", act.getDownloadFile());
			child.put("link", act.getUrl());
			child.put("iconSkin", "file");
			children.put(child);
		}
		json.put("children", children);
	}
	
	/**
	 * 获取每日检查下载任务列表
	 * @param response
	 */
	@RequestMapping(value = "getDownloadTasks")
	public void getDownloadTasks(HttpServletResponse response){
		AtsTask task = new AtsTask();
		task.setIsFinished(0);
		List<AtsTask> tasks = atsTaskService.findList(task);
		sendMessage(response, JsonMapper.toJsonString(tasks));
	}
	
	@RequestMapping(value="getTaskInfor")
	public void getTaskInfor(HttpServletRequest request, HttpServletResponse response){
		System.out.println(UserUtils.getUser().getName());
		String state = request.getParameter("state");
		AtsAct act = new AtsAct();
		act.setState(state);
		int totalCount = atsActService.findList(act).size();
		JSONObject json = new JSONObject();
		json.put("totalCount", totalCount);
		sendMessage(response, json.toString());
	}
	
	@RequestMapping(value="signUpActs")
	public synchronized void signUpActs(HttpServletRequest request, HttpServletResponse response){
		JSONObject msg = new JSONObject();
		//检查当前签名人是否签够5个
		int count = atsSignService.getSignedCount();
		if(count>=5){
			msg.put("msg", "You have signed up five acts!");
			sendMessage(response, msg.toString());
		}else{
			String state = request.getParameter("state");
			List<AtsAct> acts = atsActService.findSignActs(state,5-count);
			if(acts.size()>0){
				for(AtsAct act: acts){
					atsActService.signUpActs(act);
				}
				msg.put("msg", "You got "+acts.size()+" acts!");
				msg.put("pid", Global.getTypeByState(state).equals("pdf")?"7":"8");
				sendMessage(response, msg.toString());
			}else{
				msg.put("msg", "All acts have been signed!");
				sendMessage(response, msg.toString());
			}
		}
	}
	
	@RequestMapping(value="findUnsignedState")
	public void findUnsignedState(HttpServletResponse response){
		List<String> states = atsActService.findUnsignedStates();
		System.out.println(JsonMapper.toJsonString(states));
		sendMessage(response, JsonMapper.toJsonString(states));
	}
	
	@RequestMapping(value="getActsCount")
	public void getActsCount(HttpServletRequest request,HttpServletResponse response){
		String state = request.getParameter("state");
		int count = atsActService.findCount(state);
		sendMessage(response, count+"");
	}
	
	
	/**
	 * 获取编辑人员姓名
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="getEditor")
	public void getEditor(HttpServletRequest request, HttpServletResponse response){
		AtsSection temp = new AtsSection();
		temp.setPid(request.getParameter("actId"));
		List<AtsSection> sections = atsSectionService.findList(temp);
		String[] signer = new String [sections.size()];
		for(int i=0;i<sections.size();i++){
			signer[i] = sections.get(i).getEditor();
		}
		sendMessage(response, signer.toString());
	}
	
	/**
	 * 获取编辑器所需数据
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="parseAct")
	public void parseAct(HttpServletRequest request, HttpServletResponse response){
		String id = request.getParameter("id");
		String treeId = request.getParameter("treeId");
		AtsSign sign = atsSignService.get(id);
		AtsAct act = atsActService.get(sign.getPid());
		AtsTree tree = atsTreeService.get(treeId);
		if(tree.getPid().equals("7")){
			// pdf
		}else if(tree.getPid().equals("8")){
			try {
				BasicTaskUtils utils = (BasicTaskUtils)Class.forName(Global.getStateUtils(act.getState())).newInstance();
				utils.parseAct(act, treeId);
			} catch (Exception e) {
				e.printStackTrace();
				JSONObject ret = new JSONObject();
				
				ret.put("msg", "Act parses failed! ");
				sendMessage(response, ret.toString());
				return;
			}
			sendMessage(response, new JSONObject().toString());
		}
	}
	
	@RequestMapping(value="getEditorData")
	public void getEditorData(HttpServletRequest request, HttpServletResponse response){
		AtsSection section = atsSectionService.get(request.getParameter("id"));
		AtsSign sign = atsSignService.get(section.getPid());
		String content = "";
		try {
			content = FileUtils.readFileToString(new File(section.getContent()));
		} catch (Exception e) {
			content = "<p>File doesn't exists.</p>";
		}
		section.setContent(content);
		AtsAct act = atsActService.get(sign.getPid());
		JSONObject json = new JSONObject(JsonMapper.toJsonString(section));
		json.put("type", act.getType());
		json.put("state", act.getState());
		json.put("href", act.getUrl());
		json.put("snapshot", act.getDownloadFile());
		sendMessage(response, json.toString());
	}
	
	@RequestMapping(value="getSnapshot")
	public void getSnapshot(HttpServletRequest request, HttpServletResponse response){
		File file = new File(request.getParameter("filepath"));
		String fileType = RegexUtil.match("(?<=\\.)((?!\\.).)*?$", file.getName());
		if(fileType.equals("pdf")){
			response.setContentType("application/pdf");
			try {
				DataOutputStream dos = new DataOutputStream(response.getOutputStream());
				DataInputStream dis = new DataInputStream(new FileInputStream(file));
				response.setContentLength(dis.available());
				byte[] b = new byte[2048];
				int length = 0;
				while((length=dis.read(b)) != -1){
					dos.write(b,0,length);
					dos.flush();
				}
				dis.close();
				dos.close();
			} catch (IOException e) {
				System.out.println("IO 异常");
			}
		}else if(fileType.equals("html")){
			try {
				PrintWriter writer =  response.getWriter();
				String html = Jsoup.parse(file, "utf-8").html();
				writer.write(html);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@RequestMapping(value="getSectionHistory")
	public void getSectionHistory(HttpServletRequest request, HttpServletResponse response){
		String state = request.getParameter("state");
		String shortName = request.getParameter("shortName");
		AtsStatute statute = new AtsStatute();
		statute.setState(state);
		statute = atsStatuteService.get(statute);
		try {
			List<LibraryDocumentListItem> list = WBNonCaseUtils.findNonCaseByShortNameList(shortName, statute.getEditionId());
			if(list!=null && list.size()==1){
				LibraryDocument dom = WBNonCaseUtils.findNonCaseById(list.get(0).getLibraryDocumentID()+"");
				LibraryDocumentUpdate[] updates = dom.getDocumentUpdates().getLibraryDocumentUpdate();
				if(updates!=null){
					StringBuffer sb = new StringBuffer();
					for(LibraryDocumentUpdate update:updates){
						sb.append(update.getLibraryDocumentUpdateHtml());
						sb.append("<hr>");
					}
					sendMessage(response, sb.toString());
				}else{
					sendMessage(response, dom.getLibraryDocumentHtml());
				}
			}else{
				sendMessage(response, "<p>There are multiple sections found!</p>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="getStatuteTreeRoot")
	public void getStatuteTreeRoot(HttpServletRequest request, HttpServletResponse response){
		List<AtsTask> tasks = atsTaskService.findList(null);
		JSONArray array = atsStatuteService.getStatuteTreeRoot(tasks);
		sendMessage(response, array.toString());
	}
	
	@RequestMapping(value="findStatuteTreeData")
	public void findStatuteTreeData(HttpServletRequest request, HttpServletResponse response){
		String id = request.getParameter("id");
		String editionId = request.getParameter("editionId");
		try {
			OutlineViewListItem[] items = WBNonCaseUtils.getStatuteQATree(id, editionId);
			sendMessage(response, JsonMapper.toJsonString(items));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@RequestMapping(value="viewSection")
	public void viewSection(HttpServletRequest request, HttpServletResponse response){
		String id = request.getParameter("id");
		try {
			LibraryDocument dom = WBNonCaseUtils.findNonCaseById(id);
			sendMessage(response, JsonMapper.toJsonString(dom));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void feedback(){
		AtsSign sign = new AtsSign();
		sign.setStatus("4");
		sign.setEditor(UserUtils.getUser().getName());
		List<AtsSign> signs = atsSignService.findList(sign);
		atsActService.preCompare(signs);
	}
	
	@RequestMapping(value="getFeedback")
	public void getFeedback(HttpServletRequest request, HttpServletResponse response){
		response.setContentType("text/html;charset=utf-8");
		File file = new File("C:\\ATS\\Feedback\\"+UserUtils.getUser().getName()+"\\"+request.getParameter("sid")+".html");
		if(file.exists()){
			try {
				PrintWriter writer =response.getWriter();
				writer.write(FileUtils.readFileToString(file));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
