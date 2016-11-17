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

public class Illinois extends BaseStateUtils{
	
	public Illinois() {
		super();
	}

	public Illinois(String state) {
		super(state);
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			count = -1;
			System.out.println("连接失败");
			return;
		}
		Elements list = dom.select("a[href~=grplist.asp\\?GA=99&Min=\\d+\\&Max=\\d+]");
		for(Element el:list){
			try {
				dom = Jsoup.connect(el.absUrl("href")).timeout(10000).get();
			} catch (IOException e) {
				continue;
				
			}
			Elements lis = dom.select("li");
			for(Element li:lis){
				String billNumber = li.select("a").first().text();
				if(RegexUtil.isFind("(H|S)B\\s\\d+\\W*?\\$", billNumber)){
					continue;
				}
				billNumber = RegexUtil.match("Public Act \\d+-\\d+", billNumber);
				
				String href = li.select("a").first().absUrl("href");
				if(allActs.contains(href)){
					continue;
				}
				System.out.println(billNumber);
				
				try {
					dom = Jsoup.parse(new URL(href).openStream(), "Windows-1252", "http://www.ilga.gov/legislation/publicacts/");
				} catch (IOException e) {
					System.out.println(billNumber + "连接失败");
					continue;
				}
				
				String eff = "";
				if(!RegexUtil.isFind("\\d+", dom.select("div[class=content]").first().text())){
					continue;
				}else{
					eff = RegexUtil.match("\\d+/\\d+/\\d+", dom.select("div[class=content]").first().text());
				}
				if(dom.getElementsContainingOwnText("click here").size()>0){
					try {
						dom = Jsoup.connect(dom.getElementsContainingOwnText("click here").first().absUrl("href")).timeout(10000).get();
					} catch (IOException e) {
						continue;
					}
				}
				
				//遍历TD
				StringBuffer sb = new StringBuffer();
				Elements tds = dom.select("td.xsl");
				for(Element td : tds) {
					try{
						String firstLine = td.select("code").first().html();
						if(firstLine.equals("&nbsp;&nbsp;&nbsp;&nbsp;")){
							td.select("code").first().tagName("p");
						}
					}catch(Exception e){
						continue;
					}
					sb.append(td.html());
				}
				String html = sb.toString();
				// 处理正文
				html = RegexUtil.replace("\n", "", html);
				html = RegexUtil.replace("(&nbsp;){4,}(<code>|<u>|<strike>)", " $2", html);
				html = RegexUtil.replace("<code>(\\(Source[\\s\\S]*\\))</code>", "<p>$1</p>", html);
				html = RegexUtil.replace("</?code>", "", html);
				html = RegexUtil.replace("</p>", "", html);
				html = RegexUtil.replace("<p>", "</p><p>", html);
				html = RegexUtil.replace("–", "-", html);
				
				html = RegexUtil.replace("<p>(&nbsp;){16}(.*?)</p>", "<blockquote><blockquote><blockquote>$2</blockquote></blockquote></blockquote>", html);
				html = RegexUtil.replace("<p>(&nbsp;){12}(.*?)</p>", "<blockquote><blockquote>$2</blockquote></blockquote>", html);
				html = RegexUtil.replace("<p>(&nbsp;){8}(.*?)</p>", "<blockquote>$2</blockquote>", html);
				html = RegexUtil.replace("(\\(Source:)", "</p><p>$1", html);
				html = RegexUtil.replace("(&nbsp;){4,}", "", html);
				
				FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
				File file = new File(path+File.separator+billNumber+".html");
				AtsAct temp = new AtsAct();
				temp.setBillNumber(billNumber);
				temp.setState("Illinois");
				temp.setFileSize(file.length()/1024);
				temp.setType(2);
				temp.setSessionYear("2016");
				temp.setDay(DateUtils.getDate("yyyyMMdd"));
				temp.setWorkMode(1);
				temp.setDownloadFile(file.getAbsolutePath());
				temp.setEffectiveDate(eff);
				temp.setUrl(href);
				System.out.println(billNumber);
				temp.setRemark("");
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
		root.put("children", array);
		try {
			html = FileUtils.readFileToString(new File(act.getDownloadFile()),"Windows-1252");
		} catch (IOException e) {
			System.err.println("错误类型:文件不存在。错误地址:Florida.java 78行。");
		}
		List<String> sections = RegexUtil.matchAllList("<p>Section \\d[\\s\\S]*?(?=<p>Section |$)", html);
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<=sections.size()-1; i++) {
			List<String> nodes = RegexUtil.matchAllList("<p>\\(\\d+ ILCS[\\s\\S]*?(?=<p>\\(\\d+ ILCS|$)", sections.get(i));
			for(String node : nodes) {
				JSONObject json = new JSONObject();
				String update = "2";
				String caption = RegexUtil.match("Sec. (\\w+(\\.\\w+)?|-)*", node).trim();
				String description = "";
				String shortName = RegexUtil.match("\\(.*?\\)", node);
				node = RegexUtil.replace(shortName, "", node).trim();
				node = RegexUtil.replace("\\(\\)", "", node).trim();
				shortName = RegexUtil.replace("<[^>]*?>", "", shortName).trim();
				shortName = RegexUtil.replace("\\(|\\)", "", shortName).trim();
				
				if(RegexUtil.isFind("new", shortName)){
					update = "1";
					description = RegexUtil.match("(?<="+caption+"\\. ).*?\\.", node);
					description = RegexUtil.replace("<[^>]*?>", "", description);
					shortName = RegexUtil.replace(" new", "", shortName);
				}else if(RegexUtil.isFind("rep\\.", shortName)){
					update = "3";
					caption = "Sec. " + RegexUtil.match("(?<=/)(\\w+(\\.\\w+)?|-)*", shortName);
					shortName = RegexUtil.replace(" rep\\.", "", shortName);
					node = "";
				}
				if(description.length()>200){
					description = "";
				}
				node = RegexUtil.replace("<u>(.*?)</u>", "<font color=\"#f00\"><u>$1</u></font>", node);
				node = RegexUtil.replace("<strike>(.*?)</strike>", "<font color=\"#f00\"><strike>$1</strike></font>", node);
				node = RegexUtil.replace("<p>(&nbsp;)*", "<p>", node);
				node = RegexUtil.replace("<br[^>]*?>", "", node);
				node = RegexUtil.replace("<p>", "\n<p>", node);
				
				if(caption.isEmpty()){
					caption = act.getBillNumber();
				}
				json.put("caption", caption);
				json.put("description", description);
				json.put("content", node);
				json.put("update", update);
				json.put("shortName", shortName);
				array.put(json);
			}
			if(nodes.size()==0){
				sb.append(sections.get(i)+"\n");
			}
		}
		if(sb.length()>0){
			JSONObject json = new JSONObject();
			html = sb.toString();
			html +="</p>";
			
			html = RegexUtil.replace("<p>([\\s\\S]*?)</p>", "<p><font color=\"#f00\"><u>$1</u></font></p>" , html);
			html = RegexUtil.replace("<blockquote>([\\s\\S]*?)</blockquote>", "<blockquote><font color=\"#f00\"><u>$1</u></font></blockquote>" , html);
			json.put("caption", act.getBillNumber());
			json.put("description", "");
			json.put("content", html);
			json.put("update", "1");
			json.put("shortName", act.getBillNumber());
			array.put(json);
		}
		
		return root;
	}
}



