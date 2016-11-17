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

public class Virginia extends BaseStateUtils{
	
	public Virginia(){
		
	}
	
	public Virginia(String state) {
		super(state);
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
		// 链接成功，开始下载
		Elements acts = dom.select("ul[class=linkSect]").select("li");
		for(Element act:acts){
			Element anchor = act.select("a").first();
			String billNumber = anchor.text();
			String chapter = RegexUtil.match("(?<=\\().*?(?=\\))", billNumber);
			billNumber = RegexUtil.replace("\\s\\(.*?$", "", billNumber);
			String href = anchor.absUrl("href");
			try {
				if(!allActs.contains(href)){
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					System.out.println(billNumber);
					if(!RegexUtil.isFind("More", billNumber)){
						dom = Jsoup.connect(href).timeout(10000).get();
						anchor = dom.select("a[href~=cgi-bin/legp604\\.exe\\?161\\+ful]").last().previousElementSibling();
						String eff = RegexUtil.match("^.*?(?=\\s)", anchor.text());
						eff = RegexUtil.replace("(\\d+$)", "20$1", eff);
						URL u = new URL(anchor.absUrl("href"));
						dom = Jsoup.parse(u.openStream(), "windows-1252", "http://lis.virginia.gov/");
						String html = dom.getElementById("mainC").html();
						html = RegexUtil.replace("§", "&#167;", html);
						FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
						File file = new File(path+File.separator+billNumber+".html");
						AtsAct temp = new AtsAct();
						temp.setBillNumber(billNumber);
						temp.setState("Virginia");
						temp.setFileSize(file.length()/1024);
						temp.setType(2);
						temp.setSessionYear("2016");
						temp.setDay(DateUtils.getDate("yyyyMMdd"));
						temp.setWorkMode(1);
						temp.setDownloadFile(file.getAbsolutePath());
						temp.setEffectiveDate(eff);
						temp.setUrl(href);
						temp.setRemark(chapter);
						atsActService.save(temp);
						count++;
						allActs.add(href);
					}else{
						task.setUrl(href);
						doCheckingUpdate(task);
					}
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		AtsTask task = new AtsTask();
		task.setState(act.getState());
		task = atsTaskService.get(task);
		try {
			String html = FileUtils.readFileToString(new File(act.getDownloadFile()));
			List<String> list = RegexUtil.matchAllList("<p>(<i>)?&#167;[\\s\\S]*?(?=<p>(<i>)?&#167;|$)", html);
			for(String str:list){
				JSONObject j = new JSONObject();
				str = RegexUtil.replace("<a[^>]*?>|</a>", "", str);
				String temp = RegexUtil.match("<p>[\\s\\S]*?</p>", str);
				String caption = RegexUtil.match("(\\d|\\.)+-(\\d|\\.)+", temp);
				String description = RegexUtil.match("(?<=\\.\\s).*?(?=</p>)", temp);
				description = RegexUtil.replace("<[^>]*?>", "", description);
				str = RegexUtil.replaceFirst("<p>[\\s\\S]*?</p>", "", str);
				str = RegexUtil.replace("<i>", "<font color=\"#f00\"><u>", str);
				str = RegexUtil.replace("</i>", "</u></font>", str);
				str = RegexUtil.replace("<s>", "<font color=\"#f00\"><strike>", str);
				str = RegexUtil.replace("</s>", "</strike></font>", str);
				j.put("caption", "&#167; "+caption.trim());
				j.put("description", description.trim());
				j.put("content", str);
				j.put("update", "2");
				j.put("effectiveDate", "");
				j.put("shortName", task.getShortName()+caption.trim());
				array.put(j);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		root.put("children", array);
		return root;
	}

	
}
