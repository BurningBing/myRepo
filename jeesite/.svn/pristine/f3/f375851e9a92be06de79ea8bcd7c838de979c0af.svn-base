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
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class Connecticut extends BaseStateUtils{
	public Connecticut() {
		super();
	}

	public Connecticut(String state) {
		super(state);
	}

	public static void main(String[] args) throws IOException {
		String url = "https://www.cga.ct.gov/2016/lbp/LOBP_files/sheet001.htm";
		Document dom = Jsoup.connect(url).timeout(10000).get();
		Elements trs = dom.select("table").first().select("tr");
		System.out.println(trs.size());
		for(int i=9;i<trs.size();i++){
			Element tr = trs.get(i);
			Elements tds = tr.select("td");
			if(!tds.get(7).text().isEmpty()){
				String actNumber = RegexUtil.match("\\d+", tds.get(6).text());
				String billNumber = tds.get(0).select("a").first().text();
				// https://www.cga.ct.gov/2016/ACT/pa/2016PA-00001-R00SB-00474-PA.htm
				String[] temp = billNumber.split("-");
				String href = "https://www.cga.ct.gov/2016/ACT/pa/2016PA-0"+actNumber+"-R00"+temp[0]+"-0"+temp[1]+"-PA.htm";
				System.out.println(href);
			}
		}
		
		
//		System.out.println(html);
		
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		for(int j=1;j<3;j++){
			String url = "https://www.cga.ct.gov/2016/lbp/LOBP_files/sheet00"+j+".htm";
			Document dom = null;
			try {
				dom = Jsoup.connect(url).timeout(10000).get();
			} catch (IOException e) {
				count = -1;
				System.out.println("Connecticut连接失败");
				return;
			}
			Elements trs = dom.select("table").first().select("tr");
			for(int i=9;i<trs.size();i++){
				Element tr = trs.get(i);
				Elements tds = tr.select("td");
				if(RegexUtil.isFind("\\d",tds.get(7).text())||tds.get(7).text().equals("LAW NS")||tds.get(7).text().equals("VETO/OVR")){
					String actNumber = RegexUtil.match("\\d+", tds.get(6).text());
					String v = RegexUtil.match("\\D+", tds.get(6).text());
					System.out.println(v+" "+actNumber);
					if(tds.get(0).select("a").first()==null){
						continue;
					}
					String billNumber = tds.get(0).select("a").first().text();
					String[] temp = billNumber.split("-");
					String href = "";
					if(j==1){
						href = "https://www.cga.ct.gov/2016/ACT/"+v+"/2016"+v+"-0"+actNumber+"-R00"+temp[0]+"-0"+temp[1]+"-"+v+".htm";
					}else if(j==2){
						href = "https://www.cga.ct.gov/2016/ACT/"+v+"/2016"+v+"-0"+actNumber+"-R00"+temp[0]+"-0"+temp[1]+"SS1-"+v+".htm";
					}
					
					if(!allActs.contains(href)){
						System.out.println(billNumber);
						try {
							dom = Jsoup.connect(href).timeout(10000).get();
						} catch (IOException e) {
//							e.printStackTrace();
							continue;
						}
						String html = dom.html();
						FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
						File file = new File(path+File.separator+billNumber+".html");
						AtsAct tempAct = new AtsAct();
						billNumber = RegexUtil.replace("-", "0", billNumber);
						tempAct.setBillNumber(billNumber);
						tempAct.setState("Connecticut");
						tempAct.setFileSize(file.length()/1024);
						tempAct.setType(2);
						tempAct.setSessionYear("2016");
						tempAct.setDay(DateUtils.getDate("yyyyMMdd"));
						tempAct.setWorkMode(1);
						tempAct.setDownloadFile(file.getAbsolutePath());
						tempAct.setEffectiveDate("");
						tempAct.setUrl(href);
						tempAct.setRemark("");
						atsActService.save(tempAct);
						count++;
						allActs.add(href);
					}
				}
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
			html = RegexUtil.replace("<span class=\"remove\">(.*?)</span>", "<strike>$1</strike>", html);
			html = StringUtils.formatHtmlFile(html);
			html = RegexUtil.replace("<strike>(.*?)</strike>", "<font color=\"#f00\"><strike>$1</strike></font>", html);
			html = RegexUtil.replace("<(/?\\w+)[^>]*?><\\1[^>]*?>", "<$1>", html);
			List<String> list = RegexUtil.matchAllList("<p>(Sec\\.|Section)[\\s\\S]*?(?=<p>(Sec\\.|Section)|<p>Approved |</body>|$)", html);
			for(String str:list){
				JSONObject j = new JSONObject();
				String f = RegexUtil.match("<p>.*?</p>", str);
				f = RegexUtil.replace("<[^>]*?>", "", f);
				String eff = RegexUtil.match("(?<=<i>Effective ).*?(?=</i>)", f);
				if(!eff.isEmpty()){
					eff = DateUtils.changeDateFormat(eff);
				}
				j.put("eff", eff);
				if(RegexUtil.isFind("\\(NEW\\) ", f)){
					if(RegexUtil.isFind("\\(Effective", f)){
						eff = RegexUtil.match("(?<=Effective ).*?(?=\\))", f);
						if(!eff.isEmpty()){
							eff = DateUtils.changeDateFormat(eff);
						}
						j.put("eff", eff);
					}
					
					j.put("caption", act.getBillNumber());
					j.put("description", "");
					j.put("update", "1");
					str = RegexUtil.replace("(Section|Sec\\.).*?</i>\\) ", "", str);
					str = RegexUtil.replace("<p>([\\s\\S]*?)</p>", "<p><font color=\"#f00\"><u>$1</u></font></p>", str);
					str = RegexUtil.replaceFirst("<u>", "<u>"+RegexUtil.match("(Sec\\.|Section)\\s\\d+\\.", f)+" ", str);
					j.put("content", str);
					array.put(j);
				}
				if(RegexUtil.isFind("(section|Section) \\w+-\\w+.*?in lieu thereof", f)){
					String caption = RegexUtil.match("\\w+-\\w+", f);
					str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
					str = RegexUtil.replace("<b>(\\[|\\])</b>", " ", str);
					str = RegexUtil.replace("<span class=\"remove\">(.*?)</span>", "<font color=\"#f00\"><strike>$1</strike></font>", str);
					str = RegexUtil.replace("<u>(.*?)</u>", "<font color=\"#f00\"><u>$1</u></font>", str);
					j.put("caption", "Sec. "+caption.trim());
					j.put("description", "");
					j.put("content", str.trim());
					j.put("update", "2");
					j.put("shortName", task.getShortName()+caption.trim());
					array.put(j);
				}else if(RegexUtil.isFind("are repealed", f)){
					f = RegexUtil.match("(?<=Sections ).*?(?=of the genera)", f);
					String[] sections = f.split(",|and");
					for(String section:sections){
						if(section.contains("to")){
							String begin = RegexUtil.match("\\w+-\\w+", section);
							String end = RegexUtil.match("(?<=to ).*?$", section.trim());
							if(RegexUtil.isFind("\\D", begin)||RegexUtil.isFind("\\D", end)){
								JSONObject j1 = new JSONObject();
								j1.put("caption", "Sec. "+section);
								j1.put("description", "");
								j1.put("content", "");
								j1.put("update", "3");
								j1.put("shortName", "Conn. Gen. Stat. "+section);
								j1.put("eff", eff);
								array.put(j1);
								continue;
							}
							JSONObject j1 = new JSONObject();
							j1.put("caption", "Sec. "+begin);
							j1.put("description", "");
							j1.put("content", "");
							j1.put("update", "3");
							j1.put("shortName", "Conn. Gen. Stat. "+begin);
							j1.put("eff", eff);
							array.put(j1);
							int x = Integer.parseInt(RegexUtil.match("(?<=-).*", begin));
							int y = Integer.parseInt(RegexUtil.match("(?<=-).*", end));
							for(int n=(x+1);n<y;n++){
								String caption = RegexUtil.match("\\w+", begin)+"-"+n;
								j1 = new JSONObject();
								j1.put("caption", "Sec. "+caption);
								j1.put("description", "");
								j1.put("content", "");
								j1.put("update", "3");
								j1.put("shortName", "Conn. Gen. Stat. "+caption);
								j1.put("eff", eff);
								array.put(j1);
							}
							j1 = new JSONObject();
							j1.put("caption", "Sec. "+end);
							j1.put("description", "");
							j1.put("content", "");
							j1.put("update", "3");
							j1.put("eff", eff);
							j1.put("shortName", "Conn. Gen. Stat. "+end);
							array.put(j1);
						}else{
							String caption = RegexUtil.match("\\w+-\\w+", section);
							if(caption.isEmpty()){
								continue;
							}
							JSONObject j1 = new JSONObject();
							j1.put("caption", "Sec. "+caption);
							j1.put("description", "");
							j1.put("content", "");
							j1.put("update", "3");
							j1.put("shortName", "Conn. Gen. Stat. "+caption);
							j1.put("eff", eff);
							array.put(j1);
						}
					}
				}else if(RegexUtil.isFind("is amended by adding sub", f)){
					String caption = RegexUtil.match("\\w+-\\w+", f);
					str = RegexUtil.replace("\\(NEW\\) ", "", str);
					str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
					str = RegexUtil.replace("<p>([\\s\\S]*?)</p>", "<p><font color=\"#f00\"><u>$1</u></font></p>", str);
					j.put("caption", "Sec. "+caption.trim());
					j.put("description", "");
					j.put("content", str.trim());
					j.put("update", "2");
					j.put("shortName", task.getShortName()+caption.trim());
					array.put(j);
				}else if(RegexUtil.isFind("statutes is repealed", f)){
					String caption = RegexUtil.match("\\w+-\\w+", f);
					j.put("caption", "Sec. "+caption.trim());
					j.put("description", "");
					j.put("content", "");
					j.put("update", "3");
					j.put("shortName", task.getShortName()+caption.trim());
					array.put(j);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		root.put("children", array);
		return root;
	}
}
