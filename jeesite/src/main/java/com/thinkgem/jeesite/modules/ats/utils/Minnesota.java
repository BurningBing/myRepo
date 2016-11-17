package com.thinkgem.jeesite.modules.ats.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.h2.util.New;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class Minnesota extends BaseStateUtils{
	
	

	public Minnesota() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Minnesota(String state) {
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
			System.out.println("连接失败");
		}
		//进入主页成功，获取Act列表
		Element table = dom.select("table[class=guided]").first();
		Elements trs = table.select("tr");
		for (int i = 1; i < trs.size(); i++) {
			Elements tds = trs.get(i).select("td");
			String link = tds.get(1).select("a").first().absUrl("href");
			String href = tds.get(0).select("a").first().absUrl("href");
			if(!allActs.contains(link)){
				String eff = "";
				try {
					dom = Jsoup.connect(link).timeout(10000).get();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if(!RegexUtil.isFind("Effective date",dom.body().text())){
					continue;
				}else{
					eff = RegexUtil.match("(?<=Effective date )\\d+/\\d+/\\d+", dom.body().text());
					if(!eff.isEmpty()){
						eff = RegexUtil.replace("(\\d+$)", "20$1", eff);
					}
				}
				
				String chapter = tds.get(0).text();
				String billNumber = tds.get(1).text();
				System.out.println(billNumber);
				String html = "";
				StringBuffer sb = new StringBuffer();
				URL url;
				try {
					url = new URL(href);
					InputStream in = url.openStream();
					byte[] buff = new byte[1024];
					while(in.read(buff)!=-1){
						sb.append(new String(buff));
					}
					html = sb.toString();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				html = RegexUtil.replace("<!--[\\s\\S]*?-->", "", html);
				html = RegexUtil.replace("<ul>[\\s\\S]*?</u>", "", html);
				html = RegexUtil.replace("<span class=\"hidden\">[\\s\\S]*?</span>", "", html);
				html = RegexUtil.replace("<script[^>]*?>[\\s\\S]*?</script>", "", html);
				html = RegexUtil.replace("class=\"title\"", "style=\"text-align:center\"", html);
				html = RegexUtil.replace("class=\"bill_sec_no\"", "style=\"display:inline\"", html);
				html = RegexUtil.replace("class=\"bill_sec_header\"", "style=\"display:inline\"", html);
				html = RegexUtil.replace("class=\"subd_no\"", "style=\"display:inline\"", html);
				html = RegexUtil.replace("class=\"hn\"", "style=\"display:inline\"", html);
				html = RegexUtil.replace("<div id=\"btitle\">([\\s\\S]*?)</div>", "<center>$1</center>", html);
				html = RegexUtil.replace("<h2 style=\"display:inline\">([\\s\\S]*?)</h2>", "<b>$1</b>", html);
				html = RegexUtil.replace("(?<=(section |\\[))(\\w+\\.\\w+)", "<mark>$2</mark>", html);
				FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
				File file = new File(path+File.separator+billNumber+".html");
				AtsAct temp = new AtsAct();
				temp.setBillNumber(billNumber);
				temp.setState("Minnesota");
				temp.setSessionYear("2016");
				temp.setDay(DateUtils.getDate("yyyyMMdd"));
				temp.setFileSize(file.length()/1024);
				temp.setType(2);
				temp.setDownloadFile(file.getAbsolutePath());
				temp.setEffectiveDate(eff);
				temp.setUrl(link);
				temp.setRemark(chapter);
				temp.setPageCount(0);
				temp.setWorkMode(1);
				atsActService.save(temp);
				count++;
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
			html = FileUtils.readFileToString(new File(act.getDownloadFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document dom = Jsoup.parse(html);
		Elements divs = dom.select("div[class~=bill_section]");
		for(Element div:divs){
			JSONObject j = new JSONObject();
			String str = div.html();
			String caption = RegexUtil.match("(?<=<mark>).*?(?=</mark>)", str);
			if(StringUtils.isBlank(caption)){
				continue;
			}
			str = RegexUtil.replace("<h1[^>]*?>[\\s\\S]*?</h1>", "", str);
			str = RegexUtil.replace("<ins[^>]*?>([\\s\\S]*?)</ins>", "<font color=\"#f00\"><u>$1</u></font>", str);
			str = RegexUtil.replace("<del[^>]*?>([\\s\\S]*?)</del>", "<font color=\"#f00\"><strike>$1</strike></font>", str);
			String eff = RegexUtil.match("<div class=\"sec_eff_date\">[\\s\\S]*?</div>", str);
			if(StringUtils.isNotBlank(eff)){
				eff = RegexUtil.match("\\w+ \\d+, \\d+", eff);
				if(!eff.isEmpty()){
					eff = DateUtils.changeDateFormat(eff);
				}
			}
			str = RegexUtil.replace("<div class=\"sec_eff_date\">[\\s\\S]*?</div>", "", str);
			str = RegexUtil.replace("<div[^>]*?>|</div>", "", str);
			str = RegexUtil.replace("<mark>|</mark>", "", str);
			str = RegexUtil.replace("\n", "", str);
			while(RegexUtil.isFind("</(([^>p]*?)(\\s[^>]*?)?)>\\s*?<\\1[^>]*?>", str)){
				str = RegexUtil.replace("</(([^>p]*?)(\\s[^>]*?)?)>\\s*?<\\1[^>]*?>", " ", str);
			}
			str = RegexUtil.replace("<b>(Subd.*?)</b>", "<p><b>&#167; $1</b></p>", str);
			str = RegexUtil.replace("</?a[^>]*?>", "", str);
			j.put("caption", caption);
			j.put("content", str);
			j.put("eff", eff);
			array.put(j);
		}
		return root;
	}
	
	
	public static void main(String[] args) throws IOException {
		Document dom = null;
		try {
			 dom = Jsoup.connect("https://www.revisor.mn.gov/laws/current/").timeout(10000).get();
		} catch (IOException e) {
			System.out.println("连接失败");
		}
		//进入主页成功，获取Act列表
		Element table = dom.select("table[class=guided]").first();
		Elements trs = table.select("tr");
		for (int i = 1; i < trs.size(); i++) {
			Elements tds = trs.get(i).select("td");
			String billNumber = tds.get(1).text();
			String link = tds.get(1).select("a").first().absUrl("href");
			dom = Jsoup.connect(link).timeout(10000).get();
			if(!RegexUtil.isFind("Governor's action Approval", dom.text())){
				continue;
			}
			System.out.println(billNumber);
		}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		File folder = new  File("C:/ATS/Download/Minnesota/20160409");
//		File[] files = folder.listFiles();
//		for(File file:files){
//			String html = FileUtils.readFileToString(new File("C:/ATS/Download/Minnesota/20160426/SF2503.html"));
//			Document dom = Jsoup.parse(html);
//			Elements divs = dom.select("div[class~=bill_section]");
//			for(Element div:divs){
//				String str = div.html();
//				String caption = RegexUtil.match("(?<=<mark>).*?(?=</mark>)", str);
//				str = RegexUtil.replace("<h1[^>]*?>[\\s\\S]*?</h1>", "", str);
//				str = RegexUtil.replace("<ins[^>]*?>([\\s\\S]*?)</ins>", "<font color=\"#f00\"><u>$1</u></font>", str);
//				String eff = RegexUtil.match("<div class=\"sec_eff_date\">[\\s\\S]*?</div>", str);
//				if(StringUtils.isNotBlank(eff)){
//					eff = RegexUtil.match("\\w+ \\d+, \\d+", eff);
//					if(!eff.isEmpty()){
//						eff = DateUtils.changeDateFormat(eff);
//					}
//				}
//				str = RegexUtil.replace("<div class=\"sec_eff_date\">[\\s\\S]*?</div>", "", str);
//				str = RegexUtil.replace("<div[^>]*?>|</div>", "", str);
//				str = RegexUtil.replace("<mark>|</mark>", "", str);
//				str = RegexUtil.replace("\n", "", str);
//				while(RegexUtil.isFind("</(([^>p]*?)(\\s[^>]*?)?)>\\s*?<\\1[^>]*?>", str)){
//					str = RegexUtil.replace("</(([^>p]*?)(\\s[^>]*?)?)>\\s*?<\\1[^>]*?>", "", str);
//				}
//				str = RegexUtil.replace("<b>(.*?)</b>", "<p><b>$1</b></p>", str);
//				System.out.println(caption);
//				System.out.println(eff);
//				System.out.println(str.trim());
//				System.out.println("******************************************************************************************************************");
//			}
//			break;
//		}
	}
	
	
	
	
	
	
}
