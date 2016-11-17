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

public class RhodeIsland extends BaseStateUtils{
	
	
	public RhodeIsland() {
		super();
	}

	public RhodeIsland(String state) {
		super(state);
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			count=-1;
			return;
		}
		Elements trs = dom.select("table[class=MsoNormalTable]").first().select("tr");
		for(int i=1;i<trs.size();i++){
			Element tr = trs.get(i);
			String chapter = tr.select("td").get(0).select("a").first().text();
			String billNumber = tr.select("td").get(1).text();
			String ed = tr.select("td").get(3).text();
			Elements anchors = tr.select("td").get(2).select("a");
			if(anchors.size()>0){
				for(Element anchor:anchors){
					if(anchor.text().isEmpty()){
						continue;
					}
					String href = anchor.absUrl("href");
					if(!allActs.contains(href)){
						doDownload(href,billNumber+"_"+anchor.text(),ed,chapter);
					}
				}
			}else{
				String href = tr.select("td").get(0).select("a").first().absUrl("href");
				if(!allActs.contains(href)){
					doDownload(href,billNumber,ed,chapter);
				}else{
					System.out.println(tr.select("td").get(0).select("a").first().text() + "pass.");
				}
			}
		}
	}
	
	public void doDownload(String href,String billNumber,String ed,String chapter){
		Document dom = null;
		try {
			URL u = new URL(href);
			dom = Jsoup.parse(u.openStream(), "utf-8", "http://webserver.rilin.state.ri.us/");
			String html = dom.body().html();
			html = RegexUtil.replace("’", "'", html);
			html = RegexUtil.replace("§", "&#167;", html);
			html = RegexUtil.replace("<span[^>]*?line-through[^>]*?>([\\s\\S]*?)</span>", "<font color=\"#f00\"><strike>$1</strike></font>", html);
			html = RegexUtil.replace("<span[^>]*?underline[^>]*?>([\\s\\S]*?)</span>", "<font color=\"#f00\"><u>$1</u></font>", html);
			html = RegexUtil.replace("<span[^>]*?bold[^>]*?>([\\s\\S]*?)</span>", "<b>$1</b>", html);
			html = RegexUtil.replace("</?span[^>]*?>", "", html);
			html = RegexUtil.replace("</?td[^>]*?>", "", html);
			html = RegexUtil.replace("<tr[^>]*?>", "<p>", html);
			html = RegexUtil.replace("</tr>", "</p>", html);
			html = RegexUtil.replace("</?table[^>]*?>", "", html);
			html = RegexUtil.replace("</?tbody[^>]*?>", "", html);
			html = RegexUtil.replace("<p>\\s*", "<p>", html);
			html = RegexUtil.replace("(</p>\\s*?<p>)((<[^>]*?>)*)(?!&nbsp;)", " $2", html);
			html = RegexUtil.replace("–", "-", html);
			html = RegexUtil.replace("</b>\\s*?<b>", " ", html);
//			html = RegexUtil.replace("</font>\\s*?<font[^>]*?>", " ", html);
			html = RegexUtil.replace("</u>\\s*?<u>", " ", html);
			FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
			File file = new File(path+File.separator+billNumber+".html");
			AtsAct temp = new AtsAct();
			temp.setBillNumber(billNumber);
			temp.setState("RhodeIsland");
			temp.setFileSize(file.length()/1024);
			temp.setType(2);
			temp.setSessionYear("2016");
			temp.setDay(DateUtils.getDate("yyyyMMdd"));
			temp.setWorkMode(1);
			temp.setDownloadFile(file.getAbsolutePath());
			temp.setEffectiveDate(ed);
			temp.setUrl(href);
			temp.setRemark(chapter);
			atsActService.save(temp);
			count++;
			allActs.add(href);
		} catch (Exception e) {
			System.out.println(billNumber+"下载失败");
		}
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		root.put("children", array);
		try {
			String html = FileUtils.readFileToString(new File(act.getDownloadFile()));
			String eff = RegexUtil.match("(?<=effect on ).*?(?=\\.)", html);
			if(!eff.isEmpty()){
				eff = DateUtils.changeDateFormat(eff);
			}
			List<String> list = RegexUtil.matchAllList("<p>(&nbsp;)*?SECTION[\\s\\S]*?(?=<p>(&nbsp;)*?SECTION|$)", html);
			for(String str:list){
				String update = "2";
				String first = RegexUtil.match("<p>[\\s\\S]*?</p>", str);
				if(RegexUtil.isFind("adding thereto the", first)){
					update = "1";
				}
				List<String> sections = RegexUtil.matchAllList("<p>(&nbsp;)*?(<[^>]*?>)*?\\d+[\\s\\S]*?(?=<p>(&nbsp;)*?(<[^>]*?>)*?\\d+|$)", str);
				for(String section:sections){
					JSONObject json = new JSONObject();
					first = RegexUtil.match("<b>.*?(?=-\\s*?<)", section);
					String description = "";
					if(RegexUtil.isFind("<u>|<strike>", first)){
						first = RegexUtil.replace("<strike>.*?</strike>", "", first);
						first = RegexUtil.replace("<[^>]*?>", "", first);
						description = RegexUtil.match("(?<=\\.\\s).*?(?=\\s-)", first);
					}
					String caption = RegexUtil.match("\\d+(\\.\\d+)?-\\d+(\\.\\d+)?-\\d+(\\.\\d+)?", first);
					section = RegexUtil.replaceFirst("<b>.*?</b>", "", section);
					section = RegexUtil.replace("</?b>", "", section);
					section = RegexUtil.replace("\\-\\s", "-", section);
					json.put("caption", "&#167; "+caption.trim());
					json.put("description", description.trim());
					json.put("content", section);
					if(!eff.isEmpty()){
						json.put("eff", eff);
					}else{
						json.put("eff", DateUtils.formatEffectiveDate(act.getEffectiveDate()));
					}
					json.put("update", update);
					json.put("shortName", "RI Gen. Laws "+caption.trim());
					array.put(json);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}
	
	public static void main(String[] args) {
		try {
			String html = FileUtils.readFileToString(new File("C:\\ATS\\Download\\RhodeIsland\\20160519\\H7409Aaa.html"));
			List<String> list = RegexUtil.matchAllList("<p>(&nbsp;)*?SECTION[\\s\\S]*?(?=<p>(&nbsp;)*?SECTION|$)", html);
			for(String str:list){
				String first = RegexUtil.match("<p>[\\s\\S]*?</p>", str);
				List<String> sections = RegexUtil.matchAllList("<p>(&nbsp;)*?(<[^>]*?>)*?\\d+[\\s\\S]*?(?=<p>(&nbsp;)*?(<[^>]*?>)*?\\d+|$)", str);
				for(String section:sections){
					first = RegexUtil.match("<b>.*?(?=-\\s*?<)", section);
					first = RegexUtil.replace("<[^>]*?>", "", first);
					String caption = RegexUtil.match("\\d+(\\.\\d+)?-\\d+(\\.\\d+)?-\\d+(\\.\\d+)?", first);
					String description = RegexUtil.match("(?<=\\.\\s).*?(?=\\s)", first);
					section = RegexUtil.replace("<b>.*?</b>", "", section);
					System.out.println(caption);
					System.out.println(description);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
