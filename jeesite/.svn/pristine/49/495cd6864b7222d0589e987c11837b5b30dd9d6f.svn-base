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
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class Oklahoma extends BaseStateUtils{
	
	
	public Oklahoma() {
		super();
	}

	public Oklahoma(String state) {
		super(state);
	}

	public static void main(String[] args) throws Exception {
		File file = new File("C:\\ATS\\Download\\Oklahoma\\20160521\\HB 1697.html");
		String html = FileUtils.readFileToString(file);
		List<String> list = RegexUtil.matchAllList("<p><b>\\s*?SECTION[\\s\\S]*?(?=<p><b>\\s*?SECTION|$)", html);
		for(String str:list){
			String update = "2";
			String caption = "";
			String title = "";
			String first = RegexUtil.match("<p>.*?</p>", str);
			if(RegexUtil.isFind("not to be codified in the Oklahoma Statutes", first)){
				continue;
			}
			
			title = RegexUtil.match("(.(?<!\\s))*?(?=\\sO\\.S\\.)", first);
			
			if(RegexUtil.isFind("NEW LAW", first)){
				update = "1";
				caption = RegexUtil.match("(?<=Section )\\d+-\\d+", first);
				str = RegexUtil.replace("<p>([\\s\\S]*?)</p>", "<p><font color=\"#f00\"><u>$1</u></font></p>", str);
			}
			
		}
	}
	
	public String cleanHtml(String html){
		html = RegexUtil.replace("&nbsp;", " ", html);
		html = RegexUtil.replace("<br>", "", html);
		html = RegexUtil.replace("<body[^>]*?>[\\s\\S]*?<hr>", "<body>", html);
		html = RegexUtil.replace("<script[^>]*?>[\\s\\S]*?</script>", "", html);
		html = RegexUtil.replace("<img[^>]*?>", "", html);
		html = RegexUtil.replace("<!--[\\s\\S]*?-->", "", html);
		html = RegexUtil.replace("<table[^>]*?>[\\s\\S]*?</table>", "", html);
		html = RegexUtil.replace("<ol[^>]*?>[\\s\\S]*?</ol>", "", html);
		html = RegexUtil.replace("</?font[^>]*?>", "", html);
		html = RegexUtil.replace("<p>(\\s*SECTION[\\s\\S]*?)</p>", "<p><b>$1</b></p>", html);
		html = RegexUtil.replace("<u>([\\s\\S]*?)</u>", "<font color=\"#f00\"><u>$1</u></font>", html);
		html = RegexUtil.replace("<strike>([\\s\\S]*?)</strike>", "<font color=\"#f00\"><strike>$1</strike></font>", html);
		html=html.replace("§", "&#167;");
		while(RegexUtil.isFind("<([^>]*?)(\\s[^>]*?)?>\\s*?</\\1>", html)){
			html = RegexUtil.replace("<([^>]*?)(\\s[^>]*?)?>\\s*?</\\1>", "", html);
		}
		return html;
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			count=-1;
			return ;
		}
		Elements ps = dom.select("a[href~=DeliverDocument\\.asp\\?CiteID=]");
		for(Element el:ps){
			int len = allActs.size();
			String billNumber = RegexUtil.match("(?<=\\[).*?(?=\\]|\\})", el.text());
			String href = el.absUrl("href");
			if(!allActs.contains(href)){
				try {
					dom = Jsoup.connect(href).timeout(10000).get();
				} catch (IOException e) {
					System.err.println(billNumber);
					continue;
				}
				String html = cleanHtml(dom.html());
				FileUtils.writeToFile(path+File.separator+billNumber+"("+len+")"+".html", html, false);
				File file = new File(path+File.separator+billNumber+"("+len+")"+".html");
				AtsAct temp = new AtsAct();
				temp.setBillNumber(billNumber);
				temp.setState("Oklahoma");
				temp.setFileSize(file.length()/1024);
				temp.setType(2);
				temp.setSessionYear("2016");
				temp.setDay(DateUtils.getDate("yyyyMMdd"));
				temp.setWorkMode(1);
				temp.setDownloadFile(file.getAbsolutePath());
				temp.setEffectiveDate("");
				temp.setUrl(href);
				temp.setRemark("");
				atsActService.save(temp);
				count++;
				len++;
				allActs.add(href);
			}
		}
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		root.put("children", array);
		String html;
		try {
			html = FileUtils.readFileToString(new File(act.getDownloadFile()));
		} catch (IOException e) {
			return root;
		}
		List<String> list = RegexUtil.matchAllList("<p><b>\\s*?SECTION[\\s\\S]*?(?=<p><b>\\s*?SECTION|<p align=\"right\">|$)", html);
		String eff = "";
		String last = "";
		if(list.size()>=2){
			last = list.get(list.size()-2)+list.get(list.size()-1);
		}
		if(RegexUtil.isFind("This act shall become effective [A-Z]", last)){
			eff = RegexUtil.match("(?<=become effective ).*?(?=\\.)", last);
			eff = DateUtils.changeDateFormat(eff);
			if(!eff.isEmpty()){
				eff = DateUtils.formatEffectiveDate(eff);
			}
		}else if(RegexUtil.isFind("emergency", html)){
			last = RegexUtil.replace("<[^>]*>", "", html);
			eff = RegexUtil.match("\\d+(th|st|nd|rd).*?\\d{2}\\s*\\d{2}", last);
			String year = RegexUtil.match("\\d{2}\\s*\\d{2}", eff).replaceAll("\\s", "");
			String day = RegexUtil.match("\\d+(?=(th|st|nd|rd))", eff).trim();
			String month = RegexUtil.match("[A-Z].*?(?=,)", eff).trim();
			eff = month+" "+day+","+year;
			eff = DateUtils.changeDateFormat(eff);
		}
		for(String str:list){
			String update = "2";
			String caption = "";
			String title = "";
			String first = RegexUtil.match("<p>.*?</p>", str);
			if(RegexUtil.isFind("\\(", first)){
				first = RegexUtil.match("\\(.*?\\)", first);
			}
			
			if(RegexUtil.isFind("not to be codified in the Oklahoma Statutes", first)){
				continue;
			}
			title = RegexUtil.match("(.(?<!\\s))*?(?=\\sO\\.S\\.)", first);
			title = RegexUtil.replace("\\(", "", title);
			if(RegexUtil.isFind("REPEALER", first)){
				update = "3";
				if(RegexUtil.isFind("are hereby repealed", first)){
					String t = RegexUtil.match("(?<=Sections ).*(?=, are)", first);
					String[] a = t.split(",|and");
					for(String c:a){
						JSONObject j = new JSONObject();
						j.put("caption", "&#167; "+c.trim());
						j.put("description", "");
						j.put("content", str);
						j.put("update", update);
						j.put("eff", eff);
						j.put("shortName", "Okla. Stat. tit. " + title + " Sec. " + caption.trim());
						array.put(j);
					}
				}
			}else{
				caption = RegexUtil.match("(?<=Section ).*?(?=,|\\)|\\s)", first);
				if(caption.isEmpty()){
					caption = RegexUtil.match("\\d+\\.\\d+", first);
				}
				if(caption.isEmpty()){
					continue;
				}
				if(RegexUtil.isFind("NEW LAW", first)){
					update = "1";
					title = RegexUtil.match("(?<=Title ).*?(?=\\,)", first);
					caption = RegexUtil.match("(?<=as Section ).*?(?=\\sof)", first);
					str = RegexUtil.replace("<p>([\\s\\S]*?)</p>", "<p><font color=\"#f00\"><u>$1</u></font></p>", str);
				}
				str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
				str = RegexUtil.replaceFirst("Section "+caption+"(\\.)?\\s", "", str);
				JSONObject json = new JSONObject();
				json.put("caption", "&#167; "+caption.trim());
				json.put("description", "");
				json.put("content", str);
				json.put("update", update);
				json.put("eff", eff);
				json.put("shortName", "Okla. Stat. tit. " + title + " Sec. " + caption.trim());
				array.put(json);
			}
			
			
			
		}
		return root;
	}
}
