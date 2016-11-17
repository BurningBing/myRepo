/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.test.web;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.JDBCUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import com.thinkgem.jeesite.modules.test.entity.Test;
import com.thinkgem.jeesite.modules.test.service.TestService;

/**
 * 测试Controller
 * @author ThinkGem
 * @version 2013-10-17
 */
@Controller
@RequestMapping(value = "f/test")
public class TestController extends BaseController {

	@Autowired
	private TestService testService;
	
	@ModelAttribute
	public Test get(@RequestParam(required=false) String id) {
		if (StringUtils.isNotBlank(id)){
			return testService.get(id);
		}else{
			return new Test();
		}
	}
	
	/**
	 * 显示列表页
	 * @param test
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("test:test:view")
	@RequestMapping(value = {"list", ""})
	public String list(Test test, HttpServletRequest request, HttpServletResponse response, Model model) {
		return "modules/test/testList";
	}
	
	/**
	 * 获取硕正列表数据（JSON）
	 * @param test
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 */
	@RequiresPermissions("test:test:view")
	@RequestMapping(value = "listData")
	@ResponseBody
	public Page<Test> listData(Test test, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			test.setCreateBy(user);
		}
        Page<Test> page = testService.findPage(new Page<Test>(request, response), test); 
        return page;
	}
	
	/**
	 * 新建或修改表单
	 * @param test
	 * @param model
	 * @return
	 */
	@RequiresPermissions("test:test:view")
	@RequestMapping(value = "form")
	public String form(Test test, Model model) {
		model.addAttribute("test", test);
		return "modules/test/testForm";
	}

	/**
	 * 表单保存方法
	 * @param test
	 * @param model
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("test:test:edit")
	@RequestMapping(value = "save")
	public String save(Test test, Model model, RedirectAttributes redirectAttributes) {
		if (!beanValidator(model, test)){
			return form(test, model);
		}
//		testService.save(test);
		addMessage(redirectAttributes, "保存测试'" + test.getName() + "'成功");
		return "redirect:" + adminPath + "/test/test/?repage";
	}
	
	/**
	 * 删除数据方法
	 * @param id
	 * @param redirectAttributes
	 * @return
	 */
	@RequiresPermissions("test:test:edit")
	@RequestMapping(value = "delete")
	@ResponseBody
	public String delete(Test test, RedirectAttributes redirectAttributes) {
//		testService.delete(test);
//		addMessage(redirectAttributes, "删除测试成功");
//		return "redirect:" + adminPath + "/test/test/?repage";
		return "true";
	}

	@RequestMapping(value="loadExportList")
	public void loadExportList(HttpServletRequest request,HttpServletResponse response){
		String sql = "select * from ats_act where state = ? and day = ? and del_flag = 0 order by day,bill_number";
		ResultSet rs = JDBCUtils.queryByParameters(sql, request.getParameter("state"),request.getParameter("date"));
		JSONArray array = new JSONArray();
		try {
			while(rs.next()){
				JSONObject json = new JSONObject();
				json.put("id", rs.getString(1));
				json.put("state", rs.getString(2));
				json.put("billNumber", rs.getString(3));
				json.put("filePath", rs.getString("download_file"));
				json.put("date", rs.getString("day"));
				json.put("status", rs.getString("status"));
				json.put("delFlag", rs.getString("del_flag"));
				array.put(json);
			}
			sendMessage(response, array.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping(value="exportXmlFile")
	public void exportXmlFile(HttpServletRequest request,HttpServletResponse response){
		String state = request.getParameter("state");
		String date = request.getParameter("date");
		String sql = "select * from ats_section s left join ats_act a on s.pid = a.id where a.state = '"+state+"' and a.day = '"+date+"' and s.is_del = 0 and a.del_flag = 0";
		System.out.println(sql);
		ResultSet rs = JDBCUtils.query(sql);
		Set<String> billNumbers = new HashSet<String>();
		try {
			while(rs.next()){
				String caption = rs.getString("caption");
				String description = rs.getString("description");
				String content = "";
				try {
					content = FileUtils.readFileToString(new File(rs.getString("content")));
				} catch (Exception e) {
					System.out.println("caption Is repeal?");
				}
				String billNumber = rs.getString("bill_number");
				if(state.equals("California")){
					billNumber = RegexUtil.replace("(\\D)0", "$1-", billNumber);
				}else if(!state.equals("Illinois")){
					billNumber = RegexUtil.replace("-", "0", billNumber);
				}
				
				String eff = rs.getString("eff");
				String exp = rs.getString("exp");
				String link = rs.getString("link");
				String shortName = rs.getString("short_name");
				String update = rs.getString("update_type");
//				String editor = rs.getString("editor");
				File xmlFile = new File("c:\\export\\"+date+"\\"+state+"\\"+billNumber+"\\"+billNumber+"("+rs.getString("parse_order")+").xml");
				//拼装Dom
				Element root = DocumentHelper.createElement("Content");
				Document dom = DocumentHelper.createDocument(root);
				Element indexes = root.addElement("Indexes");
				Element index = indexes.addElement("Index");
				index.addAttribute("HasChildren", "0");
				index.addAttribute("Level", "1");
				billNumbers.add(billNumber);
				index.addElement("ActNumber").addText(billNumber);
				index.addElement("Caption").addText(caption);
				index.addElement("Content").addText(content);
				if(description!=null){
					index.addElement("Description").addText(description);
				}else{
					index.addElement("Description").addText("");
				}
				
				index.addElement("EffectiveDate").addText(eff);
				if(exp!=null){
					index.addElement("ExpirationDate").addText(exp);
				}else{
					index.addElement("ExpirationDate").addText("");
				}
				index.addElement("RevisionHistory").addText("");
				index.addElement("ShortName").addText(shortName);
				if(link!=null){
					index.addElement("SourceNoteLink").addText(link);
				}else{
					index.addElement("SourceNoteLink").addText("");
				}
				if(update.equals("1")){
					update = "New";
				}else if(update.equals("2")){
					update = "Modify";
				}else if(update.equals("3")){
					update = "Repeal";
				}
				index.addElement("Update").addText(update);
				
				//写文件
				OutputFormat format = new OutputFormat("  ",true);
				format.setEncoding("utf-8");
				format.setNewlines(true);
				XMLWriter xmlWriter;
				try {
					if(!xmlFile.getParentFile().exists()){
						xmlFile.getParentFile().mkdirs();
					}
					xmlWriter = new XMLWriter(new FileOutputStream(xmlFile),format);
					xmlWriter.write(dom);
					xmlWriter.close();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		System.out.println(billNumbers.toString());
		sendMessage(response, billNumbers.toString());
	}	
	
	@RequestMapping(value="testStatic")
	public void testStatic(){
		try {
			Class.forName("com.thinkgem.jeesite.modules.utils.Florida").getMethod("test").invoke(null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
}
