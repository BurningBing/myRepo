package com.thinkgem.jeesite.modules.ats.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.poi.hssf.record.aggregates.RecordAggregate;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.restlet.util.StringReadingListener;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleIfStatement.Else;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class Hawaii extends BaseStateUtils{
	
	public Hawaii() {
		super();
	}

	public Hawaii(String state) {
		super(state);
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			URL url = new URL(task.getUrl());
			InputStream in = url.openStream();
			
			int i = 0;
			StringBuffer sb = new StringBuffer();
			while((i = in.read())!=-1){
				sb.append((char)i);
			}
			in.close();
			dom = Jsoup.parse(sb.toString());
		} catch (IOException e) {
			count = -1;
			System.out.println("连接失败");
			return ;
		}
		Element div = dom.getElementById("UpdatePanel1");
		Elements trs = div.select("table").first().select("tr");
		for(int i=1;i<trs.size();i++){
			Elements tds = trs.get(i).select("td");
			String billNumber = tds.get(2).select("a").first().text();
			String approveDate = tds.get(3).text();
			approveDate = RegexUtil.match("\\d+/\\d+/\\d+(?=\\s\\(Gov)", approveDate);
			String href = tds.get(2).select("a").first().absUrl("href");
			if(!allActs.contains(href)){
				String[] array = billNumber.split("\\s");
				String link = array.length==1?"http://www.capitol.hawaii.gov/session2016/bills/"+array[0]+"_.HTM":"http://www.capitol.hawaii.gov/session2016/bills/"+array[0]+"_"+array[array.length-1]+"_.HTM";
				String html = "";
				try {
					dom = Jsoup.connect(link).timeout(10000).get();
					html = dom.body().html();
				} catch (IOException e) {
					continue;
				}
				FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
				File file = new File(path+File.separator+billNumber+".html");
				AtsAct temp = new AtsAct();
				temp.setBillNumber(billNumber);
				temp.setState("Hawaii");
				temp.setFileSize(file.length()/1024);
				temp.setSessionYear("2016");
				temp.setType(2);
				temp.setDay(DateUtils.getDate("yyyyMMdd"));
				temp.setDownloadFile(file.getAbsolutePath());
				temp.setEffectiveDate("");
				temp.setUrl(href);
				temp.setWorkMode(2);
				temp.setRemark(approveDate);
				atsActService.save(temp);
				count++;
				allActs.add(href);
			}
		}
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		String html = "";
		try {
			html = FileUtils.readFileToString(new File(act.getDownloadFile()),"utf-8");
		} catch (IOException e) {
			System.err.println("错误类型:文件不存在。错误地址:Florida.java 78行。");
		}
		root.put("children", array);
		html = StringUtils.formatHtmlFile(html);
		List<String> list = RegexUtil.matchAllList("<p>(&nbsp;|\\s)*?SECTION[\\s\\S]*?(?=<p>(&nbsp;|\\s)*?SECTION|<table|</div>)", html);
		//生效日期
		String eff = list.get(list.size()-1);
		if(RegexUtil.isFind("This Act shall take effect upon its approval", eff)){
			eff = act.getRemark();
		}else{
			eff = RegexUtil.match("\\w+ \\d+, \\d+", eff);
			if(!eff.isEmpty()){
				eff = DateUtils.changeDateFormat(eff);
			}
		}
		
		for(String str: list){
			JSONObject json = new JSONObject();
			json.put("eff", eff);
			String first = RegexUtil.match("<p>.*?</p>", str);
			str = RegexUtil.replace("&nbsp;", "", str);
			str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
			
			str = RegexUtil.replace("<p>\\s*?\"", "<p>", str);
			str = RegexUtil.replace("\"(?=</p>)", "", str);
			
			str = RegexUtil.replace("</?b>", "", str);
			str = RegexUtil.replace("(\\[|\\])(?!\\s*?</s>)", "", str);
			str = RegexUtil.replace("<s>(.*?)</s>", "<font color=\"#f00\"><strike>$1</strike></font>", str);
			str = RegexUtil.replace("<u>(.*?)</u>", "<font color=\"#f00\"><u>$1</u></font>", str);
			String temp = RegexUtil.match("&#167;\\w+(\\.\\w+)?-\\w+(\\.\\w+)?.*?\\.", str);
			String caption = RegexUtil.match("&#167;\\w+(\\.\\w+)?-\\w+(\\.\\w+)?", temp);
			
			temp = RegexUtil.replace("<font color=\"#f00\"><strike>.*?(</strike></font>|\\.)", "", temp);
			String description = RegexUtil.replaceFirst("&#167;\\w+-\\w+\\s", "", temp);
			if(caption.isEmpty()){
				caption = RegexUtil.match("\\w+(\\.\\w+)?-\\w+(\\.\\w+)?", first);
				if(!caption.isEmpty()){
					json.put("shortName", "Haw. Rev. Stat. "+caption);
					caption = "&#167;"+caption;
				}else{
					caption = act.getBillNumber();
				}
			}else{
				json.put("shortName", "Haw. Rev. Stat. "+RegexUtil.replace("&#167;", "", caption));
			}
			
			json.put("caption", caption);
			description = RegexUtil.replace("<strike>.*?</strike>", "", description);
			description = RegexUtil.replace("<[^>]*?>", "", description);
			description = RegexUtil.replaceFirst("^&#167;\\w+(\\.\\w+)?-\\w+(\\.\\w+)?", "", description.trim()).trim();
			json.put("description", description);
			//未分配
			if(RegexUtil.isFind("is amended by adding a new chapter to be appropriately", first)){
				List<String> sections = RegexUtil.matchAllList("<p> &#167;[\\s\\S]*?(?=<p> &#167;|$)", str);
				for(String section:sections){
					json = new JSONObject();
					String cap = RegexUtil.match("&#167;-\\d+", section);
					String desc = RegexUtil.match("(?<="+cap+" ).*?\\.", section);
					section = RegexUtil.replaceFirst(cap, "<b>"+cap+"</b>", section);
					section = RegexUtil.replaceFirst(desc, "<b>"+desc+"</b>", section);
					section = RegexUtil.replace("<br[^>]*?>", "", section);
					section = RegexUtil.replace("<p>", "<p><font color=\"#f00\"><u>", section);
					section = RegexUtil.replace("</p>", "</u></font></p>", section);
					json.put("eff", eff);
					json.put("content", section);
					json.put("shortName", "Haw. Rev. Stat. "+cap.replace("&#167;", ""));
					json.put("caption", cap);
					json.put("description", desc);
					json.put("update", "1");
					array.put(json);
				}
				
			}else if(RegexUtil.isFind("Chapter (\\w+), Hawaii Revised Statutes, is amended by adding a new", first)){
				str = RegexUtil.replace("<p>", "<p><font color=\"#f00\"><u>", str);
				str = RegexUtil.replace("</p>", "</u></font></p>", str);
				json.put("content", str);
				json.put("shortName", "Haw. Rev. Stat. "+act.getBillNumber());
				json.put("caption", act.getBillNumber());
				json.put("update", "1");
				String chapter = RegexUtil.match("(?<=Chapter ).*?(?=,)", first); 
				JSONObject tempJson = new JSONObject();
				tempJson.put("caption", "Uncodified Acts");
				tempJson.put("update", "1");
				tempJson.put("description", "");
				tempJson.put("eff", eff);
				tempJson.put("content", "<p></p>");
				tempJson.put("shortName", "Haw. Rev. Stat. Uncodified Acts "+chapter);
				array.put(tempJson);
				array.put(json);
			}else if(RegexUtil.isFind("(is amended by (amending|deleting))|is amended to read|is amended as follows", first)){
				json.put("content", str);
				json.put("caption", caption);
				json.put("update", "2");
				array.put(json);
			}else if(RegexUtil.isFind("Chapter (\\w+).*? Hawaii Revised Statutes, is repealed", first)){
				first = RegexUtil.replace("<[^>]*?>", "", first);
				json.put("content", first);
				json.put("caption", RegexUtil.match("(?<=Chapter ).*?(?=\\,)", first));
				json.put("description", "");
				json.put("update", "3");
				array.put(json);
			}else if(RegexUtil.isFind("Section.*?is repealed", first)){
				json.put("content", "<p></p>");
				json.put("update", "3");
				array.put(json);
			}else{
				json.put("content", str);
				json.put("update", "1");
				array.put(json);
			}
		}
		return root;
	}
	
	public static void main(String[] args) {
		List<String> allActs = new ArrayList<String>();
		Document dom = null;
		try {
			dom = Jsoup.connect("http://www.capitol.hawaii.gov/advreports/advreport.aspx?year=2016&report=deadline&rpt_type=gov_acts&measuretype=HB,SB&title=Acts").timeout(10000).get();
		} catch (IOException e) {
			System.out.println("连接失败");
			return ;
		}
		Element div = dom.getElementById("UpdatePanel1");
		Elements trs = div.select("table").first().select("tr");
		for(int i=1;i<trs.size();i++){
			Elements tds = trs.get(i).select("td");
			String billNumber = tds.get(2).select("a").first().text();
			String approveDate = tds.get(3).text();
			approveDate = RegexUtil.match("\\d+/\\d+/\\d+(?=\\s\\(Gov)", approveDate);
			String href = tds.get(2).select("a").first().absUrl("href");
			if(!allActs.contains(href)){
				System.out.println(billNumber);
			}
		}
		
		
		
//		File file = new File("C:\\ATS\\Download\\Hawaii\\20160428\\SB2934 SD2.html");
//		try {
//			String html = FileUtils.readFileToString(file);
//			html = StringUtils.formatHtmlFile(html);
//			List<String> list = RegexUtil.matchAllList("<p>(&nbsp;|\\s)*?SECTION[\\s\\S]*?(?=<p>(&nbsp;|\\s)*?SECTION|<table)", html);
//			for(String str: list){
//				str = RegexUtil.replace("&nbsp;", "", str);
//				str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
//				str = RegexUtil.replaceFirst("\"", "", str);
//				str = RegexUtil.replace("\"(?=</p>)", "", str);
//				str = RegexUtil.replace("</?b>", "", str);
//				String temp = RegexUtil.match("&#167;286-107.*?\\.", str);
//				String caption = RegexUtil.match("&#167;\\d+-\\d+", temp);
//				String description = RegexUtil.replaceFirst("&#167;\\d+-\\d+\\s", "", temp);
//				System.out.println(caption);
//				System.out.println(description);
//				break;
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		
		
	
	}
	

}
