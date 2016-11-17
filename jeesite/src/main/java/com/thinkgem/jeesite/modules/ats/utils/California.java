package com.thinkgem.jeesite.modules.ats.utils;

import java.io.File;
import java.io.IOException;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class California extends BaseStateUtils{
	
	public California() {
		super();
	}

	public California(String state) {
		super(state);
	}

	public static void main(String[] args) throws IOException {
		String url = "http://leginfo.legislature.ca.gov/faces/billSearchClient.xhtml?session_year=20152016&house=Both&author=All&chapterYear=2016&lawCode=All";
		Document dom = Jsoup.connect(url).timeout(10000).get();
		Elements bills = dom.select("a[href~=faces/billNavClient\\.xhtml\\?bill_id]");
		for(int i=0;i<bills.size();i++){
			Element bill = bills.get(i);
			String billNumber = bill.text();
			if(RegexUtil.isFind("SB|AB", billNumber)){
				String href = bill.absUrl("href");
				String html = Jsoup.connect(href).timeout(10000).get().getElementById("bill_all").html();
//				html = RegexUtil.replace("<form[^>]*?>[\\s\\S]*?</form>", "", html);
//				html = RegexUtil.replace("<ul[^>]*?>[\\s\\S]*?</ul>","", html);
				html = RegexUtil.replace("<h6[^>]*?>([\\s\\S]*?)</h6>", "<mark>$1</mark>", html);
				System.out.println(StringUtils.formatHtmlFile(html));
				break;
			}
		}
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (Exception e) {
			//异常处理，终止程序
			count = -1;
			e.printStackTrace();
		}
		Elements bills = dom.select("a[href~=faces/billNavClient\\.xhtml\\?bill_id]");
		for(int i=0;i<bills.size();i++){
			Element bill = bills.get(i);
			String billNumber = bill.text();
			if(RegexUtil.isFind("SB|AB", billNumber)){
				String href = bill.absUrl("href");
				if(!allActs.contains(href)){
					try {
						dom = Jsoup.connect(href).timeout(10000).get();
					} catch (IOException e) {
						//异常处理，跳过
						continue;
					}
					String html = dom.getElementById("bill_all").html();
					html = StringUtils.formatHtmlFile(html);
					html = RegexUtil.replace("<h6[^>]*?>([\\s\\S]*?)</h6>", "<mark>$1</mark>", html);
					FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
					File file = new File(path+File.separator+billNumber+".html");
					AtsAct temp = new AtsAct();
					temp.setBillNumber(billNumber);
					temp.setState("California");
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
					System.out.println(billNumber);
				}
			}
		}
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		root.put("children", array);
		try {
			String html = FileUtils.readFileToString(new File(act.getDownloadFile()));
			html = RegexUtil.replace("&nbsp;", " ", html);
			Document dom = Jsoup.parse(html);
			Elements els = dom.select("div[id~=s\\d]");
			String eff = "";
			eff = els.get(els.size()-1).text();
			if(RegexUtil.isFind("take effect immediately", eff)){
				eff = RegexUtil.match("\\w+ \\d+, \\d+", eff);	
				eff = DateUtils.changeDateFormat(eff);
			}else{
				eff = "01/01/2017";
			}
			root.put("eff", eff);
			
			for(Element el:els){
				JSONObject j = new JSONObject();
				String update = "";
				Elements divs = el.children();
				if(divs.size()>=1){
					update = divs.get(0).text();
					String title = RegexUtil.match("(?<=the\\s).*?Code", update);
					if(title.equals("Code")){
						title = RegexUtil.match("(?<=the\\s).*?(?=\\sis)", update);
					}
					title = Global.getCaliforniaShortName(title);
					if(divs.size()>1&&divs.get(1).getElementById("law_heading_text")!=null){
						if(RegexUtil.isFind("added", update)){
							String temp = divs.get(1).getElementById("law_heading_text").text();
							String caption = RegexUtil.match("(TITLE|Article|CHAPTER|PART)\\s*?\\w+(\\.\\w+)*", temp);
							if(caption.isEmpty()){
								System.out.println(temp);
							}
							String description = RegexUtil.match("(?<=\\.\\s).*", temp);
							j.put("caption", caption);
							j.put("description", description);
							j.put("content", "");
							j.put("shortName", caption);
							j.put("update", "1");
							array.put(j);
							Elements secs = divs.get(1).select("div[id=law_section]");
							for(Element sec:secs){
								j = new JSONObject();
								html = sec.html();
								html = RegexUtil.replace("<div[^>]*?>", "<p>", html);
								html = RegexUtil.replace("</div>", "</p>", html);
								html = RegexUtil.replace("&nbsp;", " ", html);
								html = RegexUtil.replace("<p></p>", "", html);
								String c = RegexUtil.match("<mark>.*?</mark>", html);
								c = RegexUtil.replace("<[^>]*?>", "", c);
								c =RegexUtil.replace("\\.\\s*?$", "", c);
								html = RegexUtil.replace("<h6[^>]*?>.*?</h6>", "", html);
								html = RegexUtil.replace("<mark[^>]*?>.*?</mark>", "", html);
								update = "1";
								html = RegexUtil.replace("<p>", "<p><font color=\"#f00\"><u>", html);
								html = RegexUtil.replace("</p>", "</u></font></p>", html);
								j.put("caption", c);
								j.put("description", "");
								j.put("content", html);
								j.put("shortName", title+c);
								j.put("update", update);
								array.put(j);
							}
							
						}
						
						System.out.println("is chapter");
					}else if(divs.size()==1){
						html = divs.get(0).text();
						String caption = RegexUtil.match("(?<=Section )\\d+", html);
						if(caption.isEmpty()){
							continue;
							
						}
						j.put("caption", caption);
						j.put("description", "");
						j.put("content", "");
						j.put("shortName", title+caption);
						j.put("update", "3");
						array.put(j);
					}else{
						try{
							html = divs.get(1).getElementById("law_section_element").html();
						}catch(Exception e){
							continue;
						}
						
						html = RegexUtil.replace("<div[^>]*?>", "<p>", html);
						html = RegexUtil.replace("</div>", "</p>", html);
						html = RegexUtil.replace("&nbsp;", " ", html);
						html = RegexUtil.replace("<p></p>", "", html);
						String caption = RegexUtil.match("<mark>.*?</mark>", html);
						caption = RegexUtil.replace("<[^>]*?>", "", caption);
						caption =RegexUtil.replace("\\.$", "", caption);
						html = RegexUtil.replace("<h6[^>]*?>.*?</h6>", "", html);
						html = RegexUtil.replace("<mark>.*?</mark>", "", html);
						if(RegexUtil.isFind("is amended", update)){
							update = "2";
						}else if(RegexUtil.isFind("is added", update)){
							update = "1";
							html = RegexUtil.replace("<p>", "<p><font color=\"#f00\"><u>", html);
							html = RegexUtil.replace("</p>", "</u></font></p>", html);
						}else if(RegexUtil.isFind("is repealed", update)){
							update = "3";
							html = "";
						}
						j.put("caption", caption);
						j.put("description", "");
						j.put("content", html);
						j.put("shortName", title+caption);
						j.put("update", update);
						array.put(j);
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return root;
	}
}
