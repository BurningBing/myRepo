package com.thinkgem.jeesite.modules.ats.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class NorthCarolina extends BaseStateUtils{
	
	
	public NorthCarolina() {
		super();
	}

	public NorthCarolina(String state) {
		super(state);
	}

	public static void main(String[] args) throws Exception {
		String url = "http://www.ncleg.net/gascripts/EnactedLegislation/ELTOC.pl?sType=Law&sSessionToView=2015&sSort=sSortKey";
		Document dom = Jsoup.connect(url).timeout(10000).get();
		Elements trs = dom.select("#mainBody > table:nth-child(10)").select("tr");
		for(int i=1;i<trs.size();i++){
			Element tr = trs.get(i);
			Elements tds = tr.select("td");
			String temp = tds.get(0).text();
			String billNumber = tds.get(1).text();
			String href = "http://www.ncleg.net/EnactedLegislation/SessionLaws/HTML/2015-2016/SL"+temp+".html";
			System.out.println(billNumber);
			System.out.println(href);
		}
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			count = -1;
			return;
		}
		Elements trs = dom.select("#mainBody > table:nth-child(10)").select("tr");
		for(int i=1;i<trs.size();i++){
			Element tr = trs.get(i);
			Elements tds = tr.select("td");
			String temp = tds.get(0).text();
			String billNumber = tds.get(1).text();
			String href = "http://www.ncleg.net/EnactedLegislation/SessionLaws/HTML/2015-2016/SL"+temp+".html";
			if(!allActs.contains(href)){
				System.out.println(billNumber);
				try {
					URL url = new URL(href);
					dom = Jsoup.parse(url.openStream(), "windows-1252", "http://www.ncleg.net/");
				} catch (IOException e) {
					continue;
				}
				String html = dom.body().html();
				html = RegexUtil.replace("<p[^>]*?text-align:center[^>]*?>([\\s\\S]*?)</p>", "<center>$1</center>", html);
				html = RegexUtil.replace("<p[^>]*?>", "<p>", html);
				html = RegexUtil.replace("</?span[^>]*?>", "", html);
				html = RegexUtil.replace("&#x2011;", "-", html);
				html = RegexUtil.replace("<s>", "<font color=\"#f00\"><strike>", html);
				html = RegexUtil.replace("</s>", "</strike></font>", html);
				html = RegexUtil.replace("<u>", "<font color=\"#f00\"><u>", html);
				html = RegexUtil.replace("</u>", "</u></font>", html);
				html = RegexUtil.replace("</font><font color=\"#f00\">", "", html);
				html = RegexUtil.replace("&nbsp;", " ", html);
				html = RegexUtil.replace("<p>&nbsp;</p>", "", html);
				html = RegexUtil.replace("–", "-", html);
				html = RegexUtil.replace("…", "...", html);
				html = RegexUtil.replace("§", "&#167;", html);
				FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
				File file = new File(path+File.separator+billNumber+".html");
				AtsAct act = new AtsAct();
				act.setBillNumber(billNumber);
				act.setState("NorthCarolina");
				act.setFileSize(file.length()/1024);
				act.setSessionYear("2016");
				act.setType(2);
				act.setDay(DateUtils.getDate("yyyyMMdd"));
				act.setDownloadFile(file.getAbsolutePath());
				act.setEffectiveDate("");
				act.setUrl(href);
				act.setWorkMode(2);
				act.setRemark("");
				atsActService.save(act);
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
		List<String> list = RegexUtil.matchAllList("<p><b>SECTION[\\s\\S]*?[\\s\\S]*?(?=<p><b>SECTION|$)", html);
		String eff = list.get(list.size()-1);
		eff = RegexUtil.match("<p>Approved.*?</p>", eff);
		eff = RegexUtil.replace("<[^>]*?>", "", eff);
		eff = RegexUtil.match("(?<=this ).*", eff);
		eff = RegexUtil.replace("(\\d+)th day of (\\w+), (\\d+)", "$2 $1,$3", eff);
		if(!eff.isEmpty()){
			eff = DateUtils.changeDateFormat(eff);
		}
		root.put("eff", eff);
		parseList(act.getBillNumber(), list, array, null);
		return root;
	}
	
	public void parseList(String billNumber,List<String> list,JSONArray array,String update){
		if(update==null) {
			for(String str : list){
				JSONObject json = new JSONObject();
				str = RegexUtil.replace("<p>\"", "<p>", str);
				str = RegexUtil.replace("\"</p> ", "</p> ", str);
				
				String first = RegexUtil.match("<p>.*?</p>", str);
				String caption = RegexUtil.match("\\w+(\\.\\w+)?-\\w+(\\.\\w+)?(-\\w+(\\.\\w+)?)?", first);
				update = "2";
				if(RegexUtil.isFind("amended by adding", str)){
					update = "1";
				}else if(RegexUtil.isFind("is repealed", str)){
					update = "3";
					str = "";
				}
				str = RegexUtil.replaceFirst("<p>[\\s\\S]*?</p>", "", str);
				String description = RegexUtil.match("(?<=&#167; "+caption+"\\.).*?</p>", str).trim();
				System.out.println(description);
				if(!description.isEmpty()){
					str = RegexUtil.replaceFirst("<p>[\\s\\S]*?</p>", "", str);
					description = RegexUtil.replace("<strike>[\\s\\S]*?</strike>", "", description);
					description = RegexUtil.replace("<[^>]*?>", "", description);
				}
				
				if(caption.isEmpty()){
					List<String> list2 = RegexUtil.matchAllList("<p>(<[^>]*?>)*?&#167;[\\s\\S]*?(?=<p>(<[^>]*?>)*?&#167;|$)", str);
					if(list2.size()>0){
						parseList(billNumber,list2,array,update);
						continue;
					}else{
						caption = billNumber;
					}
					
				}
				
				json.put("content", str);
				json.put("caption", "&#167; "+caption);
				json.put("description", description);
				json.put("shortName", "NC Gen. Stat. "+caption);
				json.put("update", update);
				array.put(json);
			}
		}else {
			for(String str : list){
				JSONObject json2 = new JSONObject();
				String first = RegexUtil.match("<p>.*?</p>", str);
				String caption = RegexUtil.match("\\w+(\\.\\w+)?-\\w+(\\.\\w+)?(-\\w+(\\.\\w+)?)?", first);
				String description = RegexUtil.match("(?<=&#167; "+caption+"\\.).*?</p>", str).trim();
				System.out.println(description);
				if(!description.isEmpty()){
					str = RegexUtil.replaceFirst("<p>[\\s\\S]*?</p>", "", str);
					description = RegexUtil.replace("<strike>[\\s\\S]*?</strike>", "", description);
					description = RegexUtil.replace("<[^>]*?>", "", description);
				}
				json2.put("content", str);
				json2.put("caption", "&#167; "+caption);
				json2.put("description", description);
				json2.put("shortName", "NC Gen. Stat. "+caption);
				json2.put("update", update);
				array.put(json2);
			}
		}
	}
	
}
