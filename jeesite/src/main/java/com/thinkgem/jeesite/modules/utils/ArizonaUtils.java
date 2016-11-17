package com.thinkgem.jeesite.modules.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.websocket.GlobalHandler;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

public class ArizonaUtils extends BasicTaskUtils{
	
	@Override
	public void doCheckUpdate(Document dom) {
		suffix = ".html";
		Element table = dom.getElementById("chaptered");
		Elements trs = table.select("tr");
		for(int i=1;i<trs.size();i++){
			JSONObject json = new JSONObject();
			Element tr = trs.get(i);
			String billNumber = tr.select("td").get(2).text();
			String href = tr.select("td").first().select("a").first().absUrl("href");
			href = href.replace(".pdf", ".htm");
			href = "http://www.azleg.gov/viewDocument/?docName="+href;
			if(allActs.contains(href)){
				continue;
			}
			try {
//				dom = Jsoup.connect(href).userAgent(userAgent).timeout(10000).get();
//				String html = dom.select(".content-sidebar-wrap").first().html();
//				html = RegexUtil.replace("‑", "-", html);
//				html = RegexUtil.replace("<style[^>]*?>[\\s\\S]*?</style>", "", html);
//				FileUtils.writeStringToFile(new File(path+"\\"+billNumber+".html"), html, "utf-8");
				GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : "+ billNumber+" download success...");
				json.put("billNumber", billNumber);
				json.put("href", href);
				json.put("state", "Arizona");
				array.put(json);
				allActs.add(href);
			} catch (Exception e) {
				GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : "+billNumber+" download failed");
			}
		}
	}

	@Override
	public List<JSONObject> parseAct(String filePath) {
		List<JSONObject> jsons = new ArrayList<JSONObject>();
		try {
			String html = FileUtils.readFileToString(new File(filePath), "utf-8");
			html = formatHtml(html);
			List<String> list = RegexUtil.matchAllList("<p>(Sec\\.|Section)[\\s\\S]*?(?=<p>(Sec\\.|Section)| </div> )", html);
			//eff
			String eff = "";
			String temp = list.get(list.size()-1);
			temp = RegexUtil.match("(?<=<span style=\"color:purple\">).*?(?=</span>)", temp);
			if(temp.contains("Effective")){
				eff = "**";
			}else if(temp.contains("Emergency")){
				eff = "+++";
			}else{
				eff = "08/06/2016";
			}
			for(String str:list){
				JSONObject json = new JSONObject();
				//update
				int updateType = 2;
				temp = RegexUtil.match("<p>.*?</p>", str);
				if(temp.contains("add")){
					updateType = 1;
				}else if(temp.contains("is amended to read")||temp.contains("Heading change")){
					updateType = 2;
				}else{
					updateType = 3;
				}
				String caption = RegexUtil.match("(?<=<span style=\"color:green\">).*?(?=</span>)", str);
				String description = RegexUtil.match("(?<=<span style=\"color:purple\">).*?(?=</span>)", str);
				if(!caption.isEmpty()){
					str = RegexUtil.replaceFirst("<p>[\\s\\S]*?</p>", "", str);
					str = RegexUtil.replaceFirst("<p>[\\s\\S]*?</p>", "", str);
				}
				caption = RegexUtil.replace("\\.$", "", caption);
				str = RegexUtil.replace("</?span[^>]*?>", "", str);
				String shortName = "ARS "+caption;
				json.put("caption", caption);
				json.put("description", description);
				json.put("shortName", shortName);
				json.put("eff", eff);
				json.put("updateType", updateType);
				json.put("content", str);
				jsons.add(json);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsons;
	}

	public String formatHtml(String html){
		html = RegexUtil.replace("<span[^>]*?blue[^>]*?uppercase[^>]*?>([\\s\\S]*?)</span>", "<font color=\"#f00\"><u style=\"text-transform:uppercase\">$1</u></font>", html);
		html = RegexUtil.replace("<p[^>]*?>", "<p>", html);
		html = RegexUtil.replace("<span style=\"display:none\">[\\s\\S]*?</span>", "", html);
		html = RegexUtil.replace("<span style=\"color:blue\">([\\s\\S]*?)</span>", "<font color=\"#f00\"><u>$1</u></font>", html);
		html = RegexUtil.replace("<span style=\"color:red\">([\\s\\S]*?)</span>", "<font color=\"#f00\"><strike>$1</strike></font>", html);
		return html;
	}
}
