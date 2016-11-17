package com.thinkgem.jeesite.modules.ats.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

public class Idaho extends BaseStateUtils{
	
	

	public Idaho() {
		super();
	}

	public Idaho(String state) {
		super(state);
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		System.out.println(task.getUrl());
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			count = -1;
			return;
		}
		Elements trs = dom.select("tr[id~=bill[A-Z]*?\\d+(\\w)*?]");
		for(Element tr:trs){
			String billNumber = "";
			Elements tds = tr.getElementsByTag("td"); 
			if(tds.get(4).text().equals("+")){
				String effectiveDate = "";
				billNumber = tds.get(0).text();
				billNumber = RegexUtil.replace("\\W", "", billNumber);
				String title = tds.get(1).text();
				if(RegexUtil.isFind("^Approp", title)||RegexUtil.isFind("^\\D{2,}", billNumber)){
					continue;
				}
				String href2 = tds.get(0).getElementsByTag("a").get(0).absUrl("href");
				if(!allActs.contains(href2)){
					Document dom2 = DownloadUtils.getConnectPage(href2,"utf-8");
					String htmlTempString = dom2.html();
					String edt = RegexUtil.match("(?<=Effective:(\\s|&nbsp;))[\\s\\S]*?(?=</td>)", htmlTempString);
					if(edt.isEmpty()){
						continue;
					}
					effectiveDate = RegexUtil.match("\\d+/\\d+/\\d+", edt);
					String downloadLink = "";
					if(RegexUtil.isFind("a$", billNumber)){
						String bn = RegexUtil.replace("a$", "", billNumber);
						downloadLink = dom2.select("a[href~="+bn+"E]").last().absUrl("href");
					}else{
						downloadLink = dom2.getElementById(billNumber).absUrl("href");
					}
					try {
						DownloadUtils.downlaodPdf(downloadLink, path, billNumber);
					} catch (IOException e) {
						continue;
					}
					File file = new File(path+File.separator+billNumber+".pdf");
					AtsAct temp = new AtsAct();
					temp.setBillNumber(billNumber);
					temp.setState("Idaho");
					temp.setFileSize(file.length()/1024);
					temp.setSessionYear("2016");
					temp.setType(1);
					temp.setDay(DateUtils.getDate("yyyyMMdd"));
					temp.setDownloadFile(file.getAbsolutePath());
					temp.setEffectiveDate(effectiveDate);
					temp.setUrl(href2);
					temp.setWorkMode(2);
					temp.setRemark("");
					atsActService.save(temp);
					System.out.println(billNumber);
					count++;
				}
			}
		}
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		AtsSign sign = new AtsSign();
		sign.setEditor(UserUtils.getUser().getName());
		sign.setPid(act.getId());
		sign = atsSignService.get(sign);
		
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		String html = "";
		try {
			html = FileUtils.readFileToString(new File(sign.getUploadFile()));
		} catch (IOException e) {
			System.err.println("错误类型:文件不存在。错误地址:Florida.java 103行。");
		}
		int pageNumber = RegexUtil.matchAllList("<hr[^>]*?>", html).size()+1;
		html = StringUtils.formatUploadFile(html);
		List<String> list = RegexUtil.matchAllList("<p>SECTION[\\s\\S]*?(?=<p>SECTION|$)", html);
		root.put("pageNumber", pageNumber);
		root.put("children", array);
		for(String str:list){
			JSONObject j = new JSONObject();
			String update = "";
			String first = RegexUtil.match("<p>.*?</p>", str);
			if(RegexUtil.isFind("add", first)){
				update = "1";
			}else if(RegexUtil.isFind("amended", first)){
				update = "2";
			}else{
				update = "3";
			}
			str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
			String caption = RegexUtil.match("\\w+(\\.\\w+)?-\\w+(\\.\\w+)?", str).trim();
			String description = RegexUtil.match("\\s[A-Z|\\s]+(--[A-Z|\\s]+)*\\.", str).trim();
			str = RegexUtil.replaceFirst("(?<=<p>).*?\\s[A-Z|\\s]+(--[A-Z|\\s]+)*\\.", "", str);
			str = RegexUtil.replace("<hr[^>]*?>", "", str);
			if(StringUtils.isBlank(caption)){
				continue;
			}
			j.put("caption", caption);
			j.put("description", description);
			j.put("content", str);
			j.put("update", update);
			array.put(j);
		}
		return root;
	}
	

}
