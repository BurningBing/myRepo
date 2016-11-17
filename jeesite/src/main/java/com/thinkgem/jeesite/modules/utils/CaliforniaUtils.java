package com.thinkgem.jeesite.modules.utils;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;

public class CaliforniaUtils extends BasicTaskUtils{

	@Override
	public void doCheckUpdate(Document dom) {
		suffix = ".html";
		Element table = dom.getElementById("bill_results");
		Elements trs = table.select("tr");
		for(int i=1; i<trs.size(); i++){
			Element td = trs.get(i).select("td").first();
			String billNumber = td.text();
			String href = "http://leginfo.legislature.ca.gov"+td.select("a").first().attr("href");
			if(RegexUtil.isFind("AB|SB", billNumber)){
				JSONObject json = new JSONObject();
				if(!allActs.contains(href)){
					json.put("state", "California");
					json.put("billNumber", billNumber);
					json.put("href", href);
					array.put(json);
					allActs.add(href);
				}
			}
		}
	}

//	public AtsAct downloadFile(AtsAct act)  {
//		Document dom = null;
//		dom = connect(act.getUrl(),"utf-8");
//		String html = dom.getElementById("bill_nav_bill_text").html();
//		html = RegexUtil.replace("<form[^>]*?>[\\s\\S]*?</form>", "", html);
//		String eff = RegexUtil.match("(?<=Approved by Governor).*?(?=\\.)", dom.text());
//		eff = DateUtils.changeDateFormat(eff);
//		act.setEffectiveDate(eff);
//		try {
//			FileUtils.write(new File(act.getDownloadFile()), html,"utf-8");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	@Override
	public List<JSONObject> parseAct(String html) {
		List<JSONObject> jsons = new ArrayList<JSONObject>();
		List<String> sections = RegexUtil.matchAllList("<div id=\"s\\d+\">[\\s\\S]*?(?=<div id=\"s\\d+\">|$)", html);
		String eff = "";
		if(RegexUtil.isFind("immediate", sections.get(sections.size()-1))){
			eff = "im";
		}
		
		for(int i=0;i<sections.size();i++){
			JSONObject json = new JSONObject();
			String section = sections.get(i);
			int updateType = RegexUtil.isFind("added", section)?1:2;
			String title = RegexUtil.match("(?<=the ).*?(?=Code)", section);
			title = "CA "+RegexUtil.replace("and", "&", title) +" Sec. ";
			List<String> divs = RegexUtil.matchAllList("<div style=\"margin:0 0 1em 0;\">[\\s\\S]*?</div>", section);
			StringBuffer content =new StringBuffer();
			String caption = RegexUtil.match("<h6[^>]*?>.*?</h6>", section).replaceAll("<[^>]*?>", "").trim().replaceAll("\\.$","");
			if(caption.isEmpty()){
				continue;
			}
			for(String div : divs){
				div = RegexUtil.replace("(</?)div[^>]*?>", "$1p>", div);
				div = RegexUtil.replace("<h6[^>]*?>.*?</h6>", "", div);
				div = div.replaceAll("\n|\r|\t", "").replaceAll("<p>(\\s|&nbsp;)*", "<p>");
				content.append(div+"\n");
			}
			json.put("caption", caption);
			json.put("content", content);
			json.put("shortName", title+caption);
			json.put("eff", eff);
			json.put("updateType", updateType);
			jsons.add(json);
		}
		return jsons;
	}

}
