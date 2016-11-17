/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.acttostatute.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocument;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentListItem;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentUpdate;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.jaxbscheme.ImportContentIdsSchema;
import com.thinkgem.jeesite.common.jaxbscheme.ImportContentIndexSchema;
import com.thinkgem.jeesite.common.jaxbscheme.ImportContentSchema;
import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.webservice.WBNonCaseUtils;
import com.thinkgem.jeesite.modules.acttostatute.entity.LibraryDocumentListItemIObject;
import com.thinkgem.jeesite.modules.acttostatute.entity.NonCaseStatuteSection;
import com.thinkgem.jeesite.modules.acttostatute.entity.NonCaseUsEdition;
import com.thinkgem.jeesite.modules.acttostatute.service.NonCaseStatuteSectionService;
import com.thinkgem.jeesite.modules.acttostatute.service.NonCaseUsEditionService;
import com.thinkgem.jeesite.modules.sys.utils.DictUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 记录SectionController
 * @author Sid Cao
 * @version 2015-04-13                              
 */
@Controller
@RequestMapping(value = "${adminPath}/acttostatute/nonCaseStatuteSection")
public class NonCaseStatuteSectionController extends BaseController {

	@Autowired
	private NonCaseStatuteSectionService nonCaseStatuteSectionService;
	@Autowired
	private NonCaseUsEditionService  nonCaseUsEditionService;
	
	@ModelAttribute
	public NonCaseStatuteSection get(@RequestParam(required=false) String id) {
		NonCaseStatuteSection entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = nonCaseStatuteSectionService.get(id);
		}
		if (entity == null){
			entity = new NonCaseStatuteSection();
		}
		return entity;
	}
	
//	@RequiresPermissions("acttostatute:nonCaseStatuteSection:view")
	@RequestMapping(value = {"list", ""})
	public String list(NonCaseStatuteSection nonCaseStatuteSection, HttpServletRequest request, HttpServletResponse response, Model model) {
		Page<NonCaseStatuteSection> parPage=new Page<NonCaseStatuteSection>(request, response);
		System.out.println("this is new system out");
		if ("1".equals(nonCaseStatuteSection.getFlag())) {
			parPage.setPageNo(nonCaseStatuteSection.getPageNo());
			parPage.setPageSize(nonCaseStatuteSection.getPageSize());
		}
		Page<NonCaseStatuteSection> page = nonCaseStatuteSectionService.findPage(parPage, nonCaseStatuteSection);
		model.addAttribute("page", page);
		model.addAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
		nonCaseStatuteSection.setPage(null);
		request.getSession().setAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
		return "modules/acttostatute/nonCaseStatuteSectionList";
	}
	
	//校验Seciont并返还结果
	@RequestMapping(value = "validateSection")
	public String validateSection(NonCaseStatuteSection nonCaseStatuteSection,HttpServletRequest request, RedirectAttributes redirectAttributes,HttpServletResponse response, Model model) {
		nonCaseStatuteSection.setQa(UserUtils.getUser().getName());
		Long count=nonCaseStatuteSectionService.countSectionPage(new Page<NonCaseStatuteSection>(request, response), nonCaseStatuteSection).getCount(); 
		if("0".equals(request.getParameter("flag")))
		{
			List<NonCaseStatuteSection> repetitionSection=nonCaseStatuteSectionService.getRepetitionSection(nonCaseStatuteSection);
			addMessage(redirectAttributes, "总共查询出 "+repetitionSection.size()+" Act");
			model.addAttribute("repetitionSection", repetitionSection);
		}
		else{
			Page<NonCaseStatuteSection> countSectionPage = nonCaseStatuteSectionService.countSectionPage(new Page<NonCaseStatuteSection>(request, response), nonCaseStatuteSection); 
			model.addAttribute("countSectionPage",countSectionPage);
			addMessage(redirectAttributes, "总共查询出 "+countSectionPage.getCount()+" Act");
		}
		model.addAttribute("count",count);
		model.addAttribute("flag", request.getParameter("flag"));
		model.addAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
		return "modules/acttostatute/StatuteSectionValidateResult";
	}

//	@RequiresPermissions("acttostatute:nonCaseStatuteSection:view")
	@RequestMapping(value = "form")
	public String form(NonCaseStatuteSection nonCaseStatuteSection, Model model) {
		model.addAttribute("eidtionMap",getEidtionMap());
		model.addAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
		return "modules/acttostatute/nonCaseStatuteSectionForm";
	}

	@RequestMapping(value = "updatePassed")
	public String updatePassed(NonCaseStatuteSection nonCaseStatuteSection, Model model) {
		nonCaseStatuteSection.setQa(UserUtils.getUser().getName());
		nonCaseStatuteSectionService.updatePassed(nonCaseStatuteSection);
		model.addAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
		return "redirect:"+Global.getAdminPath()+"/acttostatute/nonCaseStatuteSection/list";
	}
	
	@RequestMapping(value = "confirmValidate")
	public String confirmValidate(NonCaseStatuteSection nonCaseStatuteSection,HttpServletRequest request, Model model) {
//		nonCaseStatuteSection.setQa(UserUtils.getUser().getName());
		nonCaseStatuteSection.setRemark(UserUtils.getUser().getName()+" "+UserUtils.getUser().getName()+" Confirm Validate");
		nonCaseStatuteSection.setStatus("Confirmed");
		nonCaseStatuteSectionService.save(nonCaseStatuteSection);
//		model.addAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
		NonCaseStatuteSection ns=(NonCaseStatuteSection) request.getSession().getAttribute("nonCaseStatuteSection");
		ns.setFlag("1");
		return list(ns, request, null, model);
	}
	//跳转到标记页面
	@RequestMapping(value = "remark")
	public String remark(NonCaseStatuteSection nonCaseStatuteSection, Model model) {
		String[] ids=nonCaseStatuteSection.getIds().split(",");
		List<NonCaseStatuteSection> remarList=new ArrayList<NonCaseStatuteSection>();
		for(int i=0;i<ids.length;i++){
			remarList.add(this.get(ids[i]));
		}
		model.addAttribute("remarList", remarList);
		return "modules/acttostatute/StatuteSectionRemarkForm";
	}
	
	@RequestMapping(value = "matchingIndex")
	public String matchingIndex(NonCaseStatuteSection nonCaseStatuteSection, Model model) throws Exception {
		return "modules/acttostatute/StatuteSectionMatchingIndex";
	}
	
	
	@RequestMapping(value = "qaIndex")
	public String qa2Index(NonCaseStatuteSection nonCaseStatuteSection, Model model) throws Exception {
		model.addAttribute("eidtionMap",getEidtionMap());
		return "modules/acttostatute/StatuteSectionQAIndex";
	}
	
	@RequestMapping(value = "findQA2List")
	public String findQA2List(NonCaseStatuteSection nonCaseStatuteSection, Model model) throws Exception {
		model.addAttribute("eidtionMap",getEidtionMap());
    	List<LibraryDocumentListItem> ldlList=WBNonCaseUtils.findNonCaseByShortNameList(nonCaseStatuteSection.getShortName(),nonCaseStatuteSection.getState());
    	if(ldlList==null)
    	{
        	return "modules/acttostatute/StatuteSectionQAIndex";
    	}
    	List<LibraryDocumentListItemIObject> libraryList=new ArrayList<LibraryDocumentListItemIObject>();
    	for(LibraryDocumentListItem lli:ldlList)
    	{
    		LibraryDocumentListItemIObject llo=new LibraryDocumentListItemIObject();
    		llo.setCitation(lli.getCitation());
    		llo.setLibraryDocumentEidtion(Global.getMap().get(Integer.valueOf(nonCaseStatuteSection.getState())));
    		llo.setRelevanceString(lli.getRelevance()+"%");
    		llo.setLibraryDocumentID(lli.getLibraryDocumentID());
    		libraryList.add(llo);                    
    	}
    	model.addAttribute("libraryList", libraryList);
    	return "modules/acttostatute/StatuteSectionQAIndex";
	}
	
	@RequestMapping(value = "matchingTree")
	public String matchingTree(NonCaseStatuteSection nonCaseStatuteSection, Model model) throws Exception {
		return "modules/acttostatute/StatuteSectionMatchingTree";
	}
	
	//跳转到statute tree页面
	@RequestMapping(value = "statuteTree")
	public String statuteTree(NonCaseStatuteSection nonCaseStatuteSection, Model model,HttpServletRequest request) throws Exception {
		model.addAttribute("eidtionMap",getEidtionMap());
		return "modules/acttostatute/StatuteSectionTree";
	}
	
	//跳转到statute tree页面
	@RequestMapping(value = "operateTree")
	public String operateTree(NonCaseStatuteSection nonCaseStatuteSection, Model model,HttpServletRequest request) throws Exception {
		model.addAttribute("eidtionMap",getEidtionMap());
		return "modules/acttostatute/OperateStatuteTree";
	}
		
	//得到EidtionMap
	private Map<Integer, String> getEidtionMap() {
		Map<Integer, String> eidtionMap=new HashMap<Integer, String>();
		List<NonCaseUsEdition> list=nonCaseUsEditionService.findList(new NonCaseUsEdition());
		for(NonCaseUsEdition nu:list ){
			eidtionMap.put(nu.getEditionId(),nu.getLibraryEdition());
		}
		return eidtionMap;
	}

	//QA页面查看服务器上Section内容
	@RequestMapping(value = "showStatuteSection")
	public String showStatuteSection(HttpServletRequest request,NonCaseStatuteSection nonCaseStatuteSection, Model model) throws Exception {
		LibraryDocument libraryDocument=WBNonCaseUtils.findNonCaseById(request.getParameter("libraryDocumentID"));
		LibraryDocument libraryDocumentParent=WBNonCaseUtils.findNonCaseById(String.valueOf(libraryDocument.getParentLibraryIndexID()));
		model.addAttribute("libraryDocument", libraryDocument);
		model.addAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
		model.addAttribute("libraryDocumentParent", libraryDocumentParent);
		return "modules/acttostatute/StatuteSectionShowContent";
	}
	
	
	//QA页面查看服务器上Section内容
	@RequestMapping(value = "showMyStatuteSectionByJson")
	public void showMyStatuteSectionByJson(HttpServletRequest request,NonCaseStatuteSection nonCaseStatuteSection,HttpServletResponse response, Model model) throws Exception {
		LibraryDocument libraryDocument=WBNonCaseUtils.findNonCaseById(request.getParameter("libraryDocumentID"));
		model.addAttribute("libraryDocument", libraryDocument);
		model.addAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
		System.out.println(JsonMapper.nonDefaultMapper().toJson(libraryDocument));
		sendMessage(response, JsonMapper.nonDefaultMapper().toJson(libraryDocument));
	}
	
	//QA页面查看服务器上Section内容
	@RequestMapping(value = "showStatuteSectionByJson")
	public void showStatuteSectionByJson(HttpServletRequest request,NonCaseStatuteSection nonCaseStatuteSection,HttpServletResponse response, Model model) throws Exception {
		LibraryDocument libraryDocument=WBNonCaseUtils.findNonCaseById(request.getParameter("libraryDocumentID"));
		List<Map<String, String>> list = new 	ArrayList<Map<String,String>>();
		if(libraryDocument.getDocumentUpdates().getLibraryDocumentUpdate() == null||libraryDocument.getDocumentUpdates().getLibraryDocumentUpdate().length==1){
			Map<String, String> map = new HashMap<String, String>();
			String caption = libraryDocument.getIndexCaption();
			String description = libraryDocument.getIndexDescription();
			String shortName = libraryDocument.getShortName();
			String content = libraryDocument.getLibraryDocumentHtml();
			map.put("id", libraryDocument.getLibraryDocumentID()+"");
			map.put("caption", caption);
			map.put("description", description);
			map.put("shortName", shortName);
			map.put("content", content);
			map.put("isFix", "0");
			list.add(map);
		}else{
			LibraryDocumentUpdate [] libs = libraryDocument.getDocumentUpdates().getLibraryDocumentUpdate();
			if(libs.length>1){
				for(int i=0;i<libs.length;i++){
					LibraryDocumentUpdate lib = libs[i];
					Map<String, String> map = new HashMap<String, String>();
					String caption = lib.getIndexCaption();
					String description = lib.getIndexDescription();
					String shortName = libraryDocument.getShortName();
					String content = lib.getLibraryDocumentUpdateHtml();
					int id = lib.getLibraryDocumentUpdateId();
					System.err.println("order:"+lib.getUpdateOrder());
					if(lib.getActNumber()!=null){
						String actNumber = lib.getActNumber();
						SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
						String effectiveDate = sdf.format(lib.getEffectiveDate().getTime());
						if(effectiveDate.equals("01/01/0001")){
							effectiveDate = "See Note";
						}
						map.put("actNumber", actNumber);
						map.put("effectiveDate", effectiveDate);
						
					}
					map.put("description", description);
					map.put("caption", caption);
					map.put("shortName", shortName);
					map.put("content", content);
					map.put("id", id+"");
					map.put("isFix", "1");
					map.put("updateOrder", lib.getUpdateOrder()+"");
					list.add(map);
				}
			}
			
		}
		
		System.out.println(JsonMapper.nonDefaultMapper().toJson(list));
		sendMessage(response, JsonMapper.nonDefaultMapper().toJson(list));
	}
		
	//QA页面查看服务器上Section内容
	@RequestMapping(value = "showStatuteSectionVersion")
	public String showStatuteSectionVersion(HttpServletRequest request,NonCaseStatuteSection nonCaseStatuteSection, Model model) throws Exception {
		LibraryDocumentUpdate[] libraryDocuments=WBNonCaseUtils.findNonCaseUpdateById(request.getParameter("libraryDocumentID"));
		model.addAttribute("libraryDocuments", libraryDocuments);
		libraryDocuments[0].getUpdateOrder();
		nonCaseStatuteSection.setActNumber(libraryDocuments[1].getActNumber());
		NonCaseUsEdition nonCaseUsEdition=new NonCaseUsEdition();
		nonCaseUsEdition.setStatus(1);
		List<NonCaseUsEdition> nonCaseUsEditionList=nonCaseUsEditionService.findList(nonCaseUsEdition);
		model.addAttribute("nonCaseUsEditionList",nonCaseUsEditionList);
		model.addAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
		return "modules/acttostatute/StatuteSectionShowVersionContent";
	}
		
	//主界面点击ShortName跳转到Section界面
	@RequestMapping(value = "showSectionByShortName")
	public String showSectionByShortName(HttpServletRequest request,NonCaseStatuteSection nonCaseStatuteSection, RedirectAttributes redirectAttributes,Model model)  {
		List<LibraryDocumentListItem> ldlList;
		LibraryDocument  libraryDocumentParent;
		LibraryDocument libraryDocument;
		try {
			ldlList = WBNonCaseUtils.findNonCaseByShortNameList(nonCaseStatuteSection.getShortName(),Global.getStateToIdMap().get(nonCaseStatuteSection.getState()).toString());
			if(ldlList.size()>1)
			{
//				model.addAttribute("message",nonCaseStatuteSection.getShortName()+"  have many result please to section query page!");
				nonCaseStatuteSection.setState(Global.getStateToIdMap().get(nonCaseStatuteSection.getState()).toString());
				return findQA2List(nonCaseStatuteSection,model);
//				throw new Exception("总共查询出 多条请去QAfind页面查询相关信息！");
			}
			else{
				for(LibraryDocumentListItem lli:ldlList){
					libraryDocument=WBNonCaseUtils.findNonCaseById(String.valueOf(lli.getLibraryDocumentID()));
					libraryDocumentParent=WBNonCaseUtils.findNonCaseById(String.valueOf(libraryDocument.getParentLibraryIndexID()));
					libraryDocument.setRevisionHistory(nonCaseStatuteSection.getActNumber());
					model.addAttribute("libraryDocument", libraryDocument);
					model.addAttribute("nonCaseStatuteSection", nonCaseStatuteSection);
					model.addAttribute("libraryDocumentParent", libraryDocumentParent);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "modules/acttostatute/StatuteSectionShowContent";
	}
	
//	//跳转到标记页面
//	@RequestMapping(value = "matching")
//	public String matching(NonCaseStatuteSection nonCaseStatuteSection, Model model) throws Exception {
//		nonCaseStatuteSection.setStatus("Waiting");
//		nonCaseStatuteSection.setQa(UserUtils.getUser().getName());;
//		List<NonCaseStatuteSection> matchingList=nonCaseStatuteSectionService.findList(nonCaseStatuteSection);
//		for(NonCaseStatuteSection ncss:matchingList)
//		{
// 	    	 LibraryDocumentListItem ldl=WBNonCaseUtils.findNonCaseByShortName(ncss.getShortName(),Global.ACCOUNT_CONTEXT);
// 	    	 if(ldl!=null)
// 	    	 {
// 	    		 System.out.println(ldl.getRelevance());
// 	    	 	 ncss.setRelevance(String.valueOf(ldl.getRelevance())+"%");
// 	    	 	 if("Unknow".equals(ncss.getUpdateType()))
// 	    	 	 {
// 	    	 		 ncss.setUpdateType("Modify");
// 	    	 	 }
// 	    	 }
//		}
//		model.addAttribute("matchingList", matchingList);
//		return "modules/acttostatute/StatuteSectionMatchingResult";
//	}
	//标记section
	@RequestMapping(value = "remarkSection")
	public String remarkSection(NonCaseStatuteSection nonCaseStatuteSection, Model model) {
		String[] ids=nonCaseStatuteSection.getIds().split(",");
		List<NonCaseStatuteSection> remarList=new ArrayList<NonCaseStatuteSection>();
		for(int i=0;i<ids.length;i++){
			NonCaseStatuteSection nss=this.get(ids[i]);
			nss.setRemark(nonCaseStatuteSection.getRemark());
			nss.setStatus("Remark");
			nonCaseStatuteSectionService.save(nss);
			remarList.add(nss);
		}
		model.addAttribute("remarList", remarList);
		return "redirect:"+Global.getAdminPath()+"/acttostatute/nonCaseStatuteSection/?repage";
	}
	
	//展示本地数据比较section
	@RequestMapping(value = "showSection")
	public String showSection(NonCaseStatuteSection nonCaseStatuteSection, Model model) {
		String[] ids=nonCaseStatuteSection.getIds().split(",");
		for(int i=0;i<ids.length;i++){
			model.addAttribute("nonCaseStatuteSection"+i, this.get(ids[i]));
		}
		return "modules/acttostatute/qaIndex";
	}
	

	//上传需要校验的文件
	@RequestMapping(value = "/upload", method = RequestMethod.POST)   
    public String onSubmit(HttpServletRequest request, RedirectAttributes redirectAttributes,  
            HttpServletResponse response) throws IOException  {   
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
        response.setContentType("application/msword");
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest   
                .getFile("fileName");   
        // 获得文件名：   
        String realFileName = file.getOriginalFilename();   
        // 获取路径   
        String path=DictUtils.getDictValue("文件存放目录", "upload_file_path", "");
        String ctxPath ="C:\\FCC\\"+File.separator+"StatuteSection"+File.separator+DateUtils.getTodayString()+File.separator+"ZIP"+File.separator;
        // 创建文件   
        File dirPath = new File(ctxPath);   
        if (!dirPath.exists()) {   
            dirPath.mkdirs();   
        }   
        File uploadFile = new File(ctxPath + realFileName);
        try{
        	FileUtils.writeInputStreamToFile(file, uploadFile);
        	//解压文件
        	saveSection(uploadFile,request);
        }
        catch(Exception e)
        {
            addMessage(redirectAttributes, " Failed to upload ! Exception is:"+e);
            return "redirect:"+Global.getAdminPath()+"/acttostatute/nonCaseStatuteSection/?repage";
        }
        addMessage(redirectAttributes, "操作成功！");
        return "redirect:"+Global.getAdminPath()+"/acttostatute/nonCaseStatuteSection/?repage";
    }

	//上传需要校验的文件
	@RequestMapping(value = "/newUpload", method = RequestMethod.POST)   
    public String newUpload(HttpServletRequest request, RedirectAttributes redirectAttributes,  
            HttpServletResponse response) throws IOException  {   
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;   
        response.setContentType("application/msword");
        CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest   
                .getFile("fileName");   
        // 获得文件名：   
        String realFileName = file.getOriginalFilename();   
        // 获取路径   
        String path=DictUtils.getDictValue("文件存放目录", "upload_file_path", "");
        String ctxPath ="C:\\FCC\\"+File.separator+"StatuteSection"+File.separator+DateUtils.getTodayString()+File.separator+"ZIP"+File.separator;
        // 创建文件   
        File dirPath = new File(ctxPath);   
        if (!dirPath.exists()) {   
            dirPath.mkdirs();   
        }   
        File uploadFile = new File(ctxPath + realFileName);
        try{
        	FileUtils.writeInputStreamToFile(file, uploadFile);
        	//解压文件
        	saveNewSection(uploadFile,request);
        }
        catch(Exception e)
        {
            addMessage(redirectAttributes, " Failed to upload ! Exception is:"+e);
            return "redirect:"+Global.getAdminPath()+"/acttostatute/nonCaseStatuteSection/list";
        }
        addMessage(redirectAttributes, "操作成功！");
        return "redirect:"+Global.getAdminPath()+"/acttostatute/nonCaseStatuteSection/list";
    }
	
	@ResponseBody
	@RequestMapping(value = "getStatuteTree")
	public String getStatuteTree(HttpServletRequest request,NonCaseStatuteSection nonCaseStatuteSection) throws Exception {
		System.out.println(request.getParameter("editionId")+"=="+request.getParameter("initFlag"));
		if(nonCaseStatuteSection.getId()==null)
		{
			String editionId = request.getParameter("editionId");
			NonCaseUsEdition nonCaseUsEdition = new NonCaseUsEdition();
			nonCaseUsEdition.setEditionId(Integer.parseInt(editionId));
			nonCaseUsEdition = nonCaseUsEditionService.get(nonCaseUsEdition);
		
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", nonCaseUsEdition.getLibraryEdition());
			jsonObject.put("id", nonCaseUsEdition.getEditionId());
			jsonObject.put("isParent", false);
			jsonObject.put("children", new JSONArray(JsonMapper.nonDefaultMapper().toJson(WBNonCaseUtils.getStatuteQATree(nonCaseStatuteSection.getId(),request.getParameter("editionId")))));
			return jsonObject.toString();
		}
		else
		{
			//正式库
			String json = JsonMapper.nonDefaultMapper().toJson(WBNonCaseUtils.getStatuteQATree(nonCaseStatuteSection.getId(),request.getParameter("editionId")));
			System.out.println(json);
			return json;
			//测试库
//			return JsonMapper.nonDefaultMapper().toJson(WBQA2NonCaseUtils.getStatuteQATree(nonCaseStatuteSection.getId(),request.getParameter("editionId")));
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "getMyStatuteTree")
	public String getMyStatuteTree(HttpServletRequest request,NonCaseStatuteSection nonCaseStatuteSection) throws Exception {
		System.out.println(request.getParameter("editionId")+"=="+request.getParameter("initFlag"));
		if("0".equals(request.getParameter("initFlag")))
		{
			return "{note:please choose edithon}";
		}
		else
		{
			//正式库
			return JsonMapper.nonDefaultMapper().toJson(WBNonCaseUtils.getStatuteQATree(nonCaseStatuteSection.getId(),request.getParameter("editionId")));
			//测试库
//			return JsonMapper.nonDefaultMapper().toJson(WBQA2NonCaseUtils.getStatuteQATree(nonCaseStatuteSection.getId(),request.getParameter("editionId")));
		}
	}
	@RequestMapping(value = "removeVersion")
	public String removeVersion(NonCaseStatuteSection nonCaseStatuteSection, Model model,String id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String[] ids=nonCaseStatuteSection.getIds().split(",");
		List<NonCaseStatuteSection> remarList=new ArrayList<NonCaseStatuteSection>();
		for(int i=0;i<ids.length;i++){
			remarList.add(this.get(ids[i]));
		}
	    response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;  
        String filePath="C:\\FCC\\opt\\fcms\\NoneCase\\Remove\\"+nonCaseStatuteSection.getActNumber()+"-"+11+".xml";
        generateImportXmlFile(nonCaseStatuteSection,filePath,ids);
        File f=new File(filePath);
        if(f.exists())
        {
        	System.out.println(f.getName());
        }
        try {   
            long fileLength = new File(filePath).length();   
            response.setContentType("application/x-msdownload;");   
            response.setHeader("Content-disposition", "attachment; filename="  
                    + new String(f.getName().getBytes(), "ISO8859-1"));   
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(filePath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            if (bis != null)   
                bis.close();   
            if (bos != null)   
                bos.close();   
        }   
        return null;   
	}
		
	//保存文件
	private void saveSection(File uploadFile, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			 String descFileName ="C:\\FCC\\"+File.separator+"StatuteSection"+File.separator+DateUtils.getTodayString()+File.separator+"File"+File.separator;
			 FileUtils.deleteDirectory(descFileName);
			 FileUtils.unZipFiles(uploadFile.getPath(),descFileName);
			 String state=request.getParameter("state");
			 File[] files=new File(descFileName+File.separator+"Rhode Island General Laws (2014 Edition)").listFiles();
			 for(File file:files)
			 {
				 SAXReader saxReader = new SAXReader();  
			     Document doc = saxReader.read(file);
			     Element editionDate=(Element) doc.selectSingleNode("//EditionDate");
			     Element revisionDate=(Element)doc.selectSingleNode("//RevisionDate");
			     List<Element> elements=doc.selectNodes("//Index");
			     for(Element element:elements)
			     {
			    	 NonCaseStatuteSection nonCaseStatuteSection=new NonCaseStatuteSection();
			    	 nonCaseStatuteSection.setCaption(element.elementText("Caption"));
			    	 nonCaseStatuteSection.setDescription(element.elementText("Description"));
			    	 nonCaseStatuteSection.setContent(element.elementText("Content"));
			    	 nonCaseStatuteSection.setShortName(element.elementText("ShortName"));
			    	 nonCaseStatuteSection.setEffectiveDate(element.elementText("EffectiveDate"));
			    	 nonCaseStatuteSection.setExpirationDate(element.elementText("ExpirationDate"));
			    	 nonCaseStatuteSection.setFileName(file.getName());
			    	 nonCaseStatuteSection.setSourceNoteLink(element.elementText("SourceNoteLink"));
			    	 nonCaseStatuteSection.setActNumber(element.elementText("ActNumber"));
			    	 System.err.println(new Date());
			    	 System.err.println(new Date());
			    	 if(StringUtils.isNotBlank(element.elementText("Update")))
			    	 {
			    		 nonCaseStatuteSection.setUpdateType(element.elementText("Update"));
			    	 }
			    	 else
			    	 {
			    		 nonCaseStatuteSection.setUpdateType("Unknow");
			    	 }
			    	 nonCaseStatuteSection.setState(state);
			    	 nonCaseStatuteSection.setContentType("Statutes");
			    	 nonCaseStatuteSection.setQa(UserUtils.getUser().getName());
			    	 if(editionDate==null||StringUtils.isBlank(editionDate.getText())){
			    		 nonCaseStatuteSection.setDownloadTime(DateUtils.getTodayString());
			    	 }
			    	 else
			    	 {
			    		 nonCaseStatuteSection.setDownloadTime(editionDate.getText());
			    	 }
			    	 if(revisionDate==null||StringUtils.isBlank(revisionDate.getText())){
			    		 nonCaseStatuteSection.setExportTime(DateUtils.getTodayString());
			    	 }
			    	 else
			    	 {
			    		 nonCaseStatuteSection.setExportTime(revisionDate.getText());
			    	 }
			    	 nonCaseStatuteSection.setStatus("Passed");
			    	 if(nonCaseStatuteSectionService.findList(nonCaseStatuteSection).size()==0)
			    	 {
			    		nonCaseStatuteSection.setStatus("Waiting");
			    		nonCaseStatuteSectionService.save(nonCaseStatuteSection);
			    	 }
			    	 else {
						System.out.println(nonCaseStatuteSection.getActNumber());
					}
			     }
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}   
	
	
	//保存文件
	private void saveNewSection(File uploadFile, HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			 String descFileName ="C:\\FCC\\"+File.separator+"StatuteSection"+File.separator+DateUtils.getTodayString()+File.separator+"File"+File.separator;
			 String edtionNameString=uploadFile.getName().replace(".zip", "");
			 descFileName=descFileName+File.separator+edtionNameString;
			 FileUtils.deleteDirectory(descFileName);
			 FileUtils.createDirectory(descFileName);
			 FileUtils.unZipFiles(uploadFile.getPath(),descFileName);
			 String state=request.getParameter("state");
			 File[] files=new File(descFileName).listFiles();
			 for(File wfile:files)
			 {
				 File[] nFiles=wfile.listFiles();
				 for(File file:nFiles)
				 {
					 SAXReader saxReader = new SAXReader();  
				     Document doc = saxReader.read(file);
				     Element editionDate=(Element) doc.selectSingleNode("//EditionDate");
				     Element revisionDate=(Element)doc.selectSingleNode("//RevisionDate");
				     Element libraryEditionDescription=(Element)doc.selectSingleNode("//LibraryEditionDescription");
				     Element updateElement= doc.getRootElement();
				     Attribute attribute=updateElement.attribute("Update");
				     List<Element> elements=doc.selectNodes("//Index");
				     for(Element element:elements)
				     {
				    	 Element subElement=(Element) element.selectSingleNode(".//ShortName");
				    	 Element actNumberElement=(Element) element.selectSingleNode(".//ActNumber");
				    	 NonCaseStatuteSection nonCaseStatuteSection=new NonCaseStatuteSection();
				    	 nonCaseStatuteSection.setCaption(element.elementText("Caption"));
				    	 nonCaseStatuteSection.setDescription(element.elementText("Description"));
				    	 nonCaseStatuteSection.setContent(element.elementText("Content"));
				    	 if(StringUtils.isBlank(element.elementText("ShortName"))){
				    		 nonCaseStatuteSection.setShortName(subElement.getText()+"--parent");
				    		 nonCaseStatuteSection.setActNumber(actNumberElement.getText());
				    	 }else {
					    	 nonCaseStatuteSection.setShortName(element.elementText("ShortName"));
					    	 nonCaseStatuteSection.setActNumber(element.elementText("ActNumber"));

				    	 }
				    	 nonCaseStatuteSection.setEffectiveDate(element.elementText("EffectiveDate"));
				    	 nonCaseStatuteSection.setExpirationDate(element.elementText("ExpirationDate"));
				    	 nonCaseStatuteSection.setFileName(file.getName());
				    	 nonCaseStatuteSection.setSourceNoteLink(element.elementText("SourceNoteLink"));
				    	 nonCaseStatuteSection.setUpdateType(attribute.getText());
				    	 nonCaseStatuteSection.setState(state);
				    	 nonCaseStatuteSection.setContentType("Statutes");
				    	 nonCaseStatuteSection.setQa(UserUtils.getUser().getName());
				    	 if(editionDate==null||StringUtils.isBlank(editionDate.getText())){
				    		 nonCaseStatuteSection.setDownloadTime(DateUtils.getTodayString());
				    	 }
				    	 else
				    	 {
				    		 nonCaseStatuteSection.setDownloadTime(editionDate.getText());
				    		 nonCaseStatuteSection.setLibraryName(libraryEditionDescription.getText());
				    	 }
				    	 if(revisionDate==null||StringUtils.isBlank(revisionDate.getText())){
				    		 nonCaseStatuteSection.setExportTime(DateUtils.getTodayString());
				    	 }
				    	 else
				    	 {
				    		 nonCaseStatuteSection.setExportTime(revisionDate.getText());
				    	 }
				    	 nonCaseStatuteSection.setStatus("Passed");
				    	 if(nonCaseStatuteSectionService.findList(nonCaseStatuteSection).size()==0)
				    	 {
				    		nonCaseStatuteSection.setStatus("Waiting");
				    		nonCaseStatuteSectionService.save(nonCaseStatuteSection);
				    	 }
				    	 else {
							System.out.println(nonCaseStatuteSection.getActNumber());
						}
				      }
				 }
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}  
		
	/**
	 * 生成xml文件
	 * @param content
	 * @param filePath
	 * @param idString
	 */
	public void generateImportXmlFile(NonCaseStatuteSection nonCaseStatuteSection,String filePath,String[] idString){
		File xmlFile = new File(filePath);
		JAXBContext jaxbContext;
		try {
			ImportContentSchema iContentSchema=new ImportContentSchema();
			for(String id:idString){
				ImportContentIndexSchema iContentIndexSchema=new ImportContentIndexSchema();
				ImportContentIdsSchema ids=new ImportContentIdsSchema();
				iContentSchema.setType("Statutes");
				iContentSchema.setUpdate("Remove");
				iContentIndexSchema.setLevel("1");
				iContentIndexSchema.setHasChildren("0");
				iContentIndexSchema.setEffectiveDate(nonCaseStatuteSection.getEffectiveDate());
				iContentIndexSchema.setActNumber(nonCaseStatuteSection.getActNumber());
				ids.setValue(id);
				ids.setType("DeactivateLibraryDocumentIndexId");
	 			iContentIndexSchema.addIds(ids);
	 			iContentSchema.addIndex(iContentIndexSchema);
			}
			NonCaseUsEdition nonCaseUsEdition=new NonCaseUsEdition();
			nonCaseUsEdition.setLibraryEdition(nonCaseStatuteSection.getLibraryName());
			nonCaseUsEdition=nonCaseUsEditionService.get(nonCaseUsEdition);
			iContentSchema.setCurrencyText("");
			String editionDate="01/01/"+nonCaseUsEdition.getEditionYear();
			iContentSchema.setEditionDate(editionDate);
			iContentSchema.setLibraryEditionDescription(nonCaseUsEdition.getLibraryEdition());
			iContentSchema.setLibraryName(nonCaseUsEdition.getLibraryName());
			iContentSchema.setLibrarySourceConst(nonCaseUsEdition.getLibrarySourceConst());
			jaxbContext = JAXBContext.newInstance(iContentSchema.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller(); 
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 
			jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "utf-8");
	        jaxbMarshaller.marshal(iContentSchema, xmlFile);
	        StringWriter sw = new StringWriter();
	        jaxbMarshaller.marshal(iContentSchema,sw);
		} catch (JAXBException e) {
			e.printStackTrace();
		}  
	}
	//报表显示有更新的州
	@RequestMapping(value = {"findByState", ""})
	public String findByState(HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> search=new HashMap<String, Object>();
		search.put("day", "20150905");
		List<Map<String, Object>> list = nonCaseStatuteSectionService.findByState(search);
		Page<Map<String, Object>> p = new Page<Map<String, Object>>(request, response);
		p.setCount(list.size());
		p.setList(list);
		p.setPageNo(1);
		model.addAttribute("page", p);
		return "modules/acttostatute/StatuteStateUpdateList";
	}
	
	//报表显示有更新的州
	@RequestMapping(value = {"findActs", ""})
	public String findActs(HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> search=new HashMap<String, Object>();
		search.put("day", "20150905");
		search.put("state", request.getParameter("state").toString());
		List<Map<String, Object>> list = nonCaseStatuteSectionService.findActs(search);
		Page<Map<String, Object>> p = new Page<Map<String, Object>>(request, response);
		p.setCount(list.size());
		p.setList(list);
		p.setPageNo(1);
		model.addAttribute("page", p);
		return "modules/acttostatute/StatuteActsUpdateList";
	}
	
	//报表显示有更新的州
	@RequestMapping(value = {"findSection", ""})
	public String findSection(HttpServletRequest request, HttpServletResponse response, Model model) {
		Map<String, Object> search=new HashMap<String, Object>();
		search.put("day", "20150905");
		search.put("state", request.getParameter("state").toString());
		search.put("actNumber", request.getParameter("actnum").toString());
		List<Map<String, Object>> list = nonCaseStatuteSectionService.findSection(search);
		Page<Map<String, Object>> p = new Page<Map<String, Object>>(request, response);
		p.setCount(list.size());
		p.setList(list);
		p.setPageNo(1);
		model.addAttribute("page", p);
		return "modules/acttostatute/StatuteSectionUpdateList";
	}
}