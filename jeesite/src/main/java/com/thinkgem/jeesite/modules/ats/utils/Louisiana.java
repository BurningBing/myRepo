package com.thinkgem.jeesite.modules.ats.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.DownloadUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class Louisiana extends BaseStateUtils{
	
	private Map<String, String> data = new HashMap<String, String>();
	
	
	public Louisiana() {
		super();
	}

	public Louisiana(String state) {
		super(state);
	}

	public static void main(String[] args) throws Exception {
		
	}
	
//	public static Document getNextPageTest(Document dom ,String param) throws IOException{
//		String p1 = "ctl00$ctl00$PageBody$PageContent$UpdatePanelSession|"+param;
//		data2.put("ctl00$ctl00$PageBody$PageContent$ScriptManager1", p1);
//		data2.put("__EVENTTARGET", param);
//		if(dom.getElementById("__EVENTVALIDATION")!=null){
//			String p3 = dom.getElementById("__VIEWSTATE").attr("value");
//			String p5 = dom.getElementById("__EVENTVALIDATION").attr("value");
//			data2.put("__EVENTVALIDATION", p5);
//			data2.put("__VIEWSTATE", p3);
//			data2.put("__PREVIOUSPAGE", "ewpwWgOcR22ghepbHYTpg6dwJDKQx2UQnVizPdRI3ySbH5IJ_5m3FbOvC9VsFil3VlLJSyG8HDjuQU5t5G7ZLBSA-sHqSbO3a_YbvyAvnOs1");
//			data2.put("__ASYNCPOST", "true");
//		}else{
//			String p3 = RegexUtil.match("(?<=__VIEWSTATE\\|).*?(?=\\|)", dom.html());
//			String p5 = RegexUtil.match("(?<=__EVENTVALIDATION\\|).*?(?=\\|)", dom.html());
//			data2.put("__EVENTVALIDATION", p5);
//			data2.put("__VIEWSTATE", p3);
//		}
//		dom = Jsoup.connect(dom.baseUri()).data(data2).timeout(10000).post();
//		FileUtils.writeStringToFile(new File("E://demo.html"), dom.html());
//		return dom;
//	}
	
	
	
	
	public Document getNextPage(Document dom ,String param) throws IOException{
		String p1 = "ctl00$ctl00$PageBody$PageContent$UpdatePanelSession|"+param;
		data.put("ctl00$ctl00$PageBody$PageContent$ScriptManager1", p1);
		data.put("__EVENTTARGET", param);
		if(dom.getElementById("__EVENTVALIDATION")!=null){
			String p3 = dom.getElementById("__VIEWSTATE").attr("value");
			String p5 = dom.getElementById("__EVENTVALIDATION").attr("value");
			data.put("__EVENTVALIDATION", p5);
			data.put("__VIEWSTATE", p3);
			data.put("__PREVIOUSPAGE", "ewpwWgOcR22ghepbHYTpg6dwJDKQx2UQnVizPdRI3ySbH5IJ_5m3FbOvC9VsFil3VlLJSyG8HDjuQU5t5G7ZLBSA-sHqSbO3a_YbvyAvnOs1");
			data.put("__ASYNCPOST", "true");
		}else{
			String p3 = RegexUtil.match("(?<=__VIEWSTATE\\|).*?(?=\\|)", dom.html());
			String p5 = RegexUtil.match("(?<=__EVENTVALIDATION\\|).*?(?=\\|)", dom.html());
			data.put("__EVENTVALIDATION", p5);
			data.put("__VIEWSTATE", p3);
		}
		dom = Jsoup.connect(dom.baseUri()).data(data).timeout(10000).post();
		return dom;
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		Map<String, String> cookies = new HashMap<String, String>();
		cookies.put("ASP.NET_SessionId", "s3mgyi4scg40voug5azwm2qd");
		cookies.put("LegisSessionId", "162ES");
		cookies.put("LegisSessionName", "2016+Second+Extraordinary+Session");
		try {
			dom = Jsoup.connect(task.getUrl()).cookies(cookies).timeout(10000).get();
		} catch (IOException e) {
			count = -1;
			System.out.println("连接失败");
			return;
		}
		checking(dom);
		
	}
	
	public void checking (Document dom){
		Elements trs = dom.select("#ctl00_ctl00_PageBody_PageContent_PanelActs > table.ResultsListTable").first().select("tr");
		for(int i=1;i<trs.size();i++){
			Elements tds = trs.get(i).select("td");
			String billNumber = tds.get(1).text().trim();
			billNumber = RegexUtil.replace("\\W", "", billNumber);
			System.out.println(billNumber);
			String href = tds.get(1).select("a").first().absUrl("href");
			if(!allActs.contains(href)){
				try {
					dom = Jsoup.connect(href).timeout(10000).get();
				} catch (IOException e) {
					continue;
				}
				Elements anchors = dom.select("#ctl00_PageBody_MenuDocumentsn0Items a");
				String link = "";
				for(Element anchor:anchors){
					if(RegexUtil.isFind("Enrolled", anchor.text())){
						link = anchor.absUrl("href");
						break;
					}
				}
				String eff = "";
				if(RegexUtil.isFind("Effective date", dom.html())){
					String temp = dom.select("#ctl00_PageBody_PanelBillInfo > table:nth-child(12) > tbody > tr:nth-child(1) > td:nth-child(4)").first().text();
					eff = RegexUtil.match("\\d+/\\d+/\\d+", temp);
					if(eff.isEmpty()){
						eff = RegexUtil.match("\\d{4}/\\d{4}", temp);
						if(!eff.isEmpty()){
							eff = eff.substring(0,2)+"/"+eff.substring(2,eff.length());
							eff = DateUtils.formatEffectiveDate(eff);
						}else{
							eff = "";
						}
					}else{
						eff = DateUtils.formatEffectiveDate(eff);
					}
					System.out.println(billNumber+":"+link+" : "+eff);
				}
				try {
					DownloadUtils.downlaodPdf(link, path, billNumber);
				} catch (IOException e) {
					continue;
				}
				File file = new File(path+File.separator+billNumber+".pdf");
				AtsAct temp = new AtsAct();
				temp.setBillNumber(billNumber);
				temp.setState("Louisiana");
				temp.setFileSize(file.length()/1024);
				temp.setSessionYear("2016");
				temp.setType(1);
				temp.setDay(DateUtils.getDate("yyyyMMdd"));
				temp.setDownloadFile(file.getAbsolutePath());
				temp.setEffectiveDate(eff);
				temp.setUrl(href);
				temp.setWorkMode(2);
				temp.setRemark(link);
				atsActService.save(temp);
				count++;
				System.out.println(billNumber);
			}
		}
		Element page = dom.getElementById("ctl00_ctl00_PageBody_PageContent_DataPager1");
		if(page==null){
			return;
		}
		Elements els = page.getAllElements();
		for(int i=3;i<els.size()-2;i++){
			Element el = els.get(i);
			if(el.tagName().equals("span")){
				el = el.nextElementSibling();
				if(el!=null){
					String param = RegexUtil.match("(?<=\\(').*?(?=\\')", el.outerHtml());
					if(param.isEmpty()){
						break;
					}
					try {
						dom = getNextPage(dom, param);
						checking(dom);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		root.put("children", array);
		String html = "";
		try {
			html = FileUtils.readFileToString(new File(act.getUploadFile()), "WINDOWS-1252");
		} catch (IOException e) {
			e.printStackTrace();
		}
		int pageNumber = RegexUtil.matchAllList("<hr[^>]*?>", html).size()+1;
		root.put("pageNumber", pageNumber);
		html = StringUtils.formatUploadFile(html);
		FileUtils.writeToFile("E:\\chad.html", html, false);
		List<String> list = RegexUtil.matchAllList("<p>(<font[^>]*?><u>)?Section \\d{1,2}\\.[\\s\\S]*?(?=<p>(<font[^>]*?><u>)?Section \\d{1,2}\\.|$)", html);
		for(String str:list){
			String keyInfo = RegexUtil.match("<p>.*?</p>", str);
			if(keyInfo.contains("R.S.")){
				array = parseRS(array, keyInfo, str);
			}
			else if(keyInfo.contains("Code of Criminal Procedure")){
				array = parseOthers(array, keyInfo, str, "LA CCRP Art. ");
			}
			else if(keyInfo.contains("Civil Code")){
				array = parseOthers(array, keyInfo, str, "LA CC Art. ");
			}
			else if(keyInfo.contains("Code of Civil Procedure")){
				array = parseOthers(array, keyInfo, str, "LA CCP Art. ");
			}
			else if(keyInfo.contains("Children's Code")){
				array = parseOthers(array, keyInfo, str, "LA CHC Art. ");
			}
			//下面两种情况的case还没遇到
			else if(keyInfo.contains("Code of Evidence")){
				array = parseOthers(array, keyInfo, str, "LA CE Art. ");
			}
			else if(keyInfo.contains("Constitution Ancillaries")){
				array = parseOthers(array, keyInfo, str, "CA +Art. ");
			}
			
			else if(keyInfo.contains("Louisiana Constitution")){
				String titleNum = RegexUtil.match("(?<=Article )[A-Z]*", keyInfo).trim();
				titleNum = StringUtils.changeRomanNumber(titleNum);
				String update = getUpdageType(keyInfo);
				if(update != null&&update.equals("3")){
					String secNum = RegexUtil.match("(?<=:)\\d.*?(?=\\s|\\(|\\.)", keyInfo);
					String secCaption = "&sect;" + secNum;
					String shortName = "La. Sec. " + titleNum + ":" + secNum;
					JSONObject  json = setJson(secCaption, "", "", update, "", shortName);
					array.put(json);
				}
				List<String> secTextList = null;
				secTextList = RegexUtil.matchList("<p>\\s*(<b>)?(<font color='#f00'><u>)?\\s*&sect;[\\s\\S]*?(?=<p>\\s*(<b>)?(<font color='#f00'><u>)?\\s*&sect;|$)", str, RegexUtil.CASE_INSENSITIVE);			
				for (String secText : secTextList) {
					String secTitle = RegexUtil.match("<p>\\s*(<b>)?(<font color='#f00'><u>)?\\s*&sect;[\\s\\S]*?</p>", secText);
					secText = secText.replace(secTitle, "").trim();
					String secCaption = RegexUtil.match("&sect;\\s*?\\d.*?(?=[A-Z])", secTitle).trim();
					secCaption = RegexUtil.replace("<[^>]*?>", "", secCaption).trim();
					String secDesc = secTitle.replace(secCaption, "").trim();
					secDesc = RegexUtil.replace("<[^>]*?>", "", secDesc).trim();
					secDesc = StringUtils.RemoveTags(secDesc).trim();
					if(secCaption.endsWith(".")){
						secCaption = secCaption.substring(0, secCaption.length() - 1).trim();
					}
					String secNum = secCaption.replace("&sect;", "").trim();		
					String shortName = "CA " + titleNum + " " + secNum;
					String effectiveDate = "";
					JSONObject  json = setJson(secCaption, secDesc, secText, update, effectiveDate, shortName);
					array.put(json);
				}
			}
		}
		return root;
	}
	
	private JSONArray parseOthers(JSONArray array, String keyInfo, String sectionText, String pixShortName) {
		String update = getUpdageType(keyInfo);
		if(update.equals("3")){
			String titleNum = RegexUtil.match("(?<=Article)\\d.*?(?=\\s|\\(|\\.)", keyInfo);
			String titleCaption = "Art. " + titleNum;
			String shortName = pixShortName + titleNum + ":" + titleNum;
			JSONObject  json = setJson(titleCaption, "", "", update, "", shortName);
			array.put(json);
			return array;
		}
		
		List<String> artTextList = RegexUtil.matchList("<p>\\s*(<font color='#f00'><u>)?\\s*Art\\.[\\s\\S]*?"
				+ "(?=<p>\\s*(<font color='#f00'><u>)?\\s*Art\\.|$)", sectionText, RegexUtil.CASE_INSENSITIVE);
		for (String artText : artTextList) {
			String artTitle = RegexUtil.match("<p>\\s*(<font color='#f00'><u>)?\\s*Art\\.[\\s\\S]*?</p>", artText);
			artText = artText.replace(artTitle, "").trim();
			String artCaption = RegexUtil.match("Art\\.\\s+\\d.*?(?=[A-Z])", artTitle).trim();
			String artDesc = artTitle.replace(artCaption, "");
			artDesc = StringUtils.RemoveTags(artDesc).trim();
			artCaption = StringUtils.RemoveTags(artCaption).trim();
			if(artCaption.endsWith(".")){
				artCaption = artCaption.substring(0, artCaption.length() - 1).trim();
			}
			String artNum = artCaption.replace("Art.", "").trim();	
			String shortName = pixShortName + artNum;
			String effectiveDate = "";
			JSONObject  json = setJson(artCaption, artDesc, artText, update, effectiveDate, shortName);
			array.put(json);
		}	
		return array;
	}
	
	private  JSONObject setJson(String caption, String description, String content, String update, String effectiveDate, String shortName){
		content = RegexUtil.replace("</?b>", "", content);
		JSONObject json = new JSONObject();
		json.put("caption", caption);
		json.put("description", description);
		json.put("content", content);
		json.put("update", update);
		if(!effectiveDate.isEmpty()){
			json.put("effectiveDate", "");			
		}
		json.put("shortName", shortName);
		return json;
	}
	
	private JSONArray parseRS(JSONArray array, String keyInfo, String sectionText){
		String titleNum = RegexUtil.match("(?<=R\\.S\\.).*?(?=:)", keyInfo).trim();
		String update = getUpdageType(keyInfo);
		if(update != null&&update.equals("3")){
			String secNum = RegexUtil.match("(?<=:)\\d.*?(?=\\s|\\(|\\.)", keyInfo);
			String secCaption = "&sect;" + secNum;
			String shortName = "La. Sec. " + titleNum + ":" + secNum;
			JSONObject  json = setJson(secCaption, "", "", update, "", shortName);
			array.put(json);
			return array;
		}
		List<String> secTextList = null;
		secTextList = RegexUtil.matchList("<p>\\s*(<b>)?(<font color='#f00'><u>)?\\s*&sect;[\\s\\S]*?(?=<p>\\s*(<b>)?(<font color='#f00'><u>)?\\s*&sect;|$)", sectionText, RegexUtil.CASE_INSENSITIVE);			

		for (String secText : secTextList) {
			String secTitle = RegexUtil.match("<p>\\s*(<b>)?(<font color='#f00'><u>)?\\s*&sect;[\\s\\S]*?</p>", secText);
			secText = secText.replace(secTitle, "").trim();
			String secCaption = RegexUtil.match("&sect;\\s*?\\d.*?(?=[A-Z])", secTitle).trim();
			secCaption = RegexUtil.replace("<[^>]*?>", "", secCaption).trim();
			String secDesc = secTitle.replace(secCaption, "").trim();
			secDesc = RegexUtil.replace("<[^>]*?>", "", secDesc).trim();
			secDesc = StringUtils.RemoveTags(secDesc).trim();
			if(secCaption.endsWith(".")){
				secCaption = secCaption.substring(0, secCaption.length() - 1).trim();
			}
			String secNum = secCaption.replace("&sect;", "").trim();		
			String shortName = "La. Sec. " + titleNum + ":" + secNum;
			String effectiveDate = "";
			JSONObject  json = setJson(secCaption, secDesc, secText, update, effectiveDate, shortName);
			array.put(json);
		}
		return array;
	}
	
	private String getUpdageType(String keyInfo) {
		keyInfo = StringUtils.RemoveTags(keyInfo).toLowerCase();
		if(keyInfo.contains("amended"))
			return "2";
		else if (keyInfo.contains(" enacted")) {
			return "1";
		}
		else if (keyInfo.contains("repealed")) {
			return "3";
		}
		return "";
	}
}
