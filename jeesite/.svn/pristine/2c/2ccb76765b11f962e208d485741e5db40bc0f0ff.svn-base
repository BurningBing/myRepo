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
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class Oregon extends BaseStateUtils{
	
	
	public Oregon() {
		super();
	}

	public Oregon(String state) {
		super(state);
	}

	public static void main(String[] args) {
		StringBuffer sb = new StringBuffer();
		Document dom = null;
		try {
			dom = Jsoup.connect("https://olis.leg.state.or.us/liz/2016R1/Measures/list").timeout(10000).get();
		} catch (IOException e) {
			
			System.out.println("连接失败");
		}
		Element el = dom.getElementById("billsTop_search");
		Elements els = el.select("li[class=panel-group]");
		for(Element li:els){
			if(!RegexUtil.isFind("SB|HB", li.select("span[class=main-text-color]").first().text())){
				continue;
			}
			String href = li.getElementsByTag("ul").first().absUrl("data-load-action");
			try {
				dom = Jsoup.connect(href).timeout(10000).get();
			} catch (IOException e) {
				System.out.println(href+"连接失败");
				continue;
			}
			Elements acts = dom.select("li[class=measure-desc row]");
			for(Element act:acts){
				JSONObject json = new JSONObject();
				String billNumber = act.select("a").first().text(); 
				href = act.getElementsByTag("ul").first().absUrl("data-load-action");
				try {
					dom = Jsoup.connect(href).timeout(10000).get();
				} catch (IOException e) {
					continue;
				}
				System.out.println(json.toString());
				Element anchor = dom.select("li").last().select("a").first();
				if(anchor==null){
					System.out.println(billNumber +"is null");
					continue;
				}
				if((anchor.text().equals("Enrolled"))){
					
					String e = act.select("a").first().absUrl("href");
					try {
						dom = Jsoup.connect(e).timeout(10000).get();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					Element d = dom.getElementById("MeasureHistory");
					e = d.absUrl("data-load-action");
					try {
						dom = Jsoup.connect(e).timeout(10000).get();
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					String ed = dom.select("table").first().select("tr").last().select("td").last().text();
					if(RegexUtil.isFind("Effective date", ed)){
						ed = RegexUtil.match("\\w*?\\s\\d+,\\s\\d+", ed);
						String downloadLink = anchor.absUrl("href");
						sb.append(billNumber+"\t"+downloadLink+"\t"+ed+"\n");
						System.out.println(billNumber);
					}
				}
			}
		}
		
		FileUtils.writeToFile("E:\\test.txt", sb.toString(), false);
		System.out.println("done");
		
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect("https://olis.leg.state.or.us/liz/2016R1/Measures/list").timeout(10000).get();
		} catch (IOException e) {
			count = -1;
			System.out.println("连接失败");
			return;
		}
		Element el = dom.getElementById("billsTop_search");
		Elements els = el.select("li[class=panel-group]");
		for(Element li:els){
			if(!RegexUtil.isFind("SB|HB", li.select("span[class=main-text-color]").first().text())){
				continue;
			}
			String href = li.getElementsByTag("ul").first().absUrl("data-load-action");
			try {
				dom = Jsoup.connect(href).timeout(10000).get();
			} catch (IOException e) {
				System.out.println(href+"连接失败");
				continue;
			}
			Elements acts = dom.select("li[class=measure-desc row]");
			for(Element act:acts){
				String billNumber = act.select("a").first().text(); 
				href = act.getElementsByTag("ul").first().absUrl("data-load-action");
				if(!allActs.contains(href)){
					try {
						dom = Jsoup.connect(href).timeout(10000).get();
					} catch (IOException e) {
						continue;
					}
					Element anchor = dom.select("li").last().select("a").first();
					if(anchor==null){
						System.out.println(billNumber +"is null");
						continue;
					}
					if(anchor.text().equals("Enrolled")){
						String e = act.select("a").first().absUrl("href");
						try {
							dom = Jsoup.connect(e).timeout(10000).get();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						Element d = dom.getElementById("MeasureHistory");
						e = d.absUrl("data-load-action");
						try {
							dom = Jsoup.connect(e).timeout(10000).get();
						} catch (IOException e2) {
							e2.printStackTrace();
						}
						String ed = dom.select("table").first().select("tr").last().select("td").last().text();
						if(RegexUtil.isFind("Effective date", ed)){
							ed = RegexUtil.match("\\w*?\\s\\d+,\\s\\d+", ed);
							ed = DateUtils.changeDateFormat(ed);
							String downloadLink = anchor.absUrl("href");
							try {
								DownloadUtils.downlaodPdf(downloadLink, path, billNumber);
							} catch (IOException e1) {
								continue;
							}
							File file = new File(path+File.separator+billNumber+".pdf");
							AtsAct temp = new AtsAct();
							temp.setBillNumber(billNumber);
							temp.setState("Oregon");
							temp.setFileSize(file.length()/1024);
							temp.setSessionYear("2016");
							temp.setType(1);
							temp.setDay(DateUtils.getDate("yyyyMMdd"));
							temp.setDownloadFile(file.getAbsolutePath());
							temp.setEffectiveDate(ed);
							temp.setUrl(href);
							temp.setWorkMode(2);
							temp.setRemark("File Link: "+downloadLink);
							atsActService.save(temp);
							count++;
						}
					}
				}
				
				
				
				
			}
		}
		
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		String html = "";
		try {
			html = FileUtils.readFileToString(new File(act.getUploadFile()));
		} catch (IOException e) {
			System.err.println("错误类型:文件不存在。错误地址:Florida.java 78行。");
		}
		int pageNumber = RegexUtil.matchAllList("<hr[^>]*?>", html).size()+1;
		html = RegexUtil.match("<body>[\\s\\S]*?</body>", html);
		html = RegexUtil.replace("&nbsp;", " ", html);
		html = RegexUtil.replace("<u>|</u>", "", html);
		html = RegexUtil.replace("<span[^>]*?>|</span>", "", html,RegexUtil.CASE_INSENSITIVE);
		html = RegexUtil.replace("&quot;", "\"", html);
		html = RegexUtil.replace("<p[^>]*?>\\s*?</p>", "", html);
		html = RegexUtil.replace("<hr[^>]*?>", "", html);
		List<String> list = RegexUtil.matchAllList("<p><b>SECTION[\\s\\S]*?(?=<p><b>SECTION|$)", html);
		root.put("pageNumber", pageNumber);
		root.put("children", array);
		for(int i=0;i<list.size();i++){
			JSONObject json = new JSONObject();
			String str = list.get(i);
			String f = RegexUtil.match("<p>[\\s\\S]*?</p>", str);
			f = RegexUtil.replace("<[^>]*?>", "", f);
			f = RegexUtil.replace("SECTION\\s\\d+\\.\\s", "", f);
			String update = "";
			if(RegexUtil.isFind("add", f)){
				update = "1";
			}else if(RegexUtil.isFind("amend", f)){
				update = "2";
			}else{
				update = "3";
			}
			String caption = RegexUtil.match("(?<=^ORS ).*?(?=\\s)", f);
			if(RegexUtil.isFind("ORS\\s\\d.*?to read", f)){
				str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
			}
			if(StringUtils.isNotBlank(str)){
				str = RegexUtil.replace("(?<=<p>)\\d.*?\\.\\s", "", str);
				str = RegexUtil.replace("<i>", "<font color=\"#f00\"><strike>", str);
				str = RegexUtil.replace("</i>", "</strike></font>", str);
				str = RegexUtil.replace("\\[|\\]", "", str);
				str = RegexUtil.replace("<b>", "<font color=\"#f00\"><u>", str);
				str = RegexUtil.replace("</b>", "</u></font>", str);
				str = RegexUtil.replace("\n|\r|\t", "", str);
			}
			String description = "";
			json.put("caption", caption);
			json.put("description", description);
			json.put("content", str);
			json.put("update", update);
			array.put(json);
		}
		return root;
	}
}
