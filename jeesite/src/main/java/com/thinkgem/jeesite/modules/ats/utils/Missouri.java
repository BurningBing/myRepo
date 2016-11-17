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

public class Missouri extends BaseStateUtils{
	public Missouri(){
		
	}
	public Missouri(String state) {
		super(state);
	}
	
	public static void main(String[] args) throws IOException {
		File file = new File("C:/Users/Chad/Desktop/Missouri/HB1983.htm");
		String html = FileUtils.readFileToString(file);
		html = StringUtils.formatUploadFile(html);
		List<String> list = RegexUtil.matchAllList("<p>(<b>)?\\d+\\.\\d+[\\s\\S]*?(?=<p>(<b>)?\\d+\\.\\d+|</body>)", html);
		for(String str:list){
//			String update = "";
//			String caption = RegexUtil.match("(?<=<p>(<b>)?)\\d+\\.\\d+", str);
			str = RegexUtil.replace("<b>", "<font color=\"#f00\"><u>", str);
			str = RegexUtil.replace("</b>", "</u></font>", str);
			str = RegexUtil.replace("\\[", "<font color=\"#f00\"><strike>", str);
			str = RegexUtil.replace("\\]", "</strike></font>", str);
			str = RegexUtil.replaceFirst("(?<=<p>(<b>)?)\\d+\\.\\d+", "", str);
//			if(RegexUtil.isFind("<strike>", str)){
//				update = "2";
//			}else{
//				update = "1";
//			}
			
			
		}
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			count = -1;
			System.out.println("Missouri连接失败");
			return;
		}
		Element table1 = dom.getElementById("table1");
		Elements dl = table1.select("tr").last().select("dl");
		for(Element el:dl){
			String infor = el.select("dd").first().text();
			System.out.println(infor);
			if(RegexUtil.isFind("Signed by Governor|Legislature voted to override Governor's veto|Vetoed in part by Governor|sent to Secretary of State", infor)){
				String billNumber = el.select("dt").first().select("a").first().text();
				String eff = el.select("dt").first().select("a").first().absUrl("href");
				String href = "";
				if(RegexUtil.replace("\\s", "", billNumber.trim()).contains("SB")){
					href = "http://www.senate.mo.gov/16info/pdf-bill/tat/"+RegexUtil.replace("\\s", "", billNumber.trim())+".pdf";
				}else if(RegexUtil.replace("\\s", "", billNumber.trim()).contains("HB")){
					href = "http://www.house.mo.gov//billtracking/bills161/billpdf/truly/"+RegexUtil.replace("\\s", "", billNumber.trim())+"T.PDF";
				}
				if(!allActs.contains(href)){
					if(RegexUtil.replace("\\s", "", billNumber.trim()).contains("SB")){
						try {
							dom = Jsoup.connect(eff).timeout(10000).get();
							eff = dom.getElementById("lblEffDate").text();
							if(!dom.getElementById("lblBillTitle").text().isEmpty()){
								billNumber = dom.getElementById("lblBillTitle").text();
								billNumber = RegexUtil.replace("SCS ", "", billNumber);
								billNumber = RegexUtil.replace("SBs", "SB", billNumber);
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else if(RegexUtil.replace("\\s", "", billNumber.trim()).contains("HB")){
						try {
							dom = Jsoup.connect(eff).timeout(10000).get();
							eff = dom.getElementById("BillDetails").select("table").first().select("tr").get(2).select("td").first().text();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(RegexUtil.isFind("\\d", eff)){
						eff = DateUtils.changeDateFormat(eff);
					}else{
						eff = "";
					}
					try {
						DownloadUtils.downlaodPdf(href, path, billNumber);
					} catch (IOException e) {

						continue;
					}
					File file = new File(path+File.separator+billNumber+".pdf");
					AtsAct temp = new AtsAct();
					temp.setBillNumber(billNumber);
					temp.setState("Missouri");
					temp.setFileSize(file.length()/1024);
					temp.setSessionYear("2016");
					temp.setType(1);
					temp.setDay(DateUtils.getDate("yyyyMMdd"));
					temp.setDownloadFile(file.getAbsolutePath());
					temp.setEffectiveDate(eff);
					temp.setUrl(href);
					temp.setWorkMode(2);
					temp.setRemark("");
					atsActService.save(temp);
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
			System.err.println("错误类型:文件不存在。错误地址:Missouri.java 78行。");
		}
		int pageNumber = RegexUtil.matchAllList("<hr[^>]*?>", html).size()+1;
		html = StringUtils.formatUploadFile(html);
		System.out.println(html);
		List<String> list = RegexUtil.matchAllList("<p>(\\[)?(<b>)?\\d+\\.\\d+[\\s\\S]*?(?=<p>(\\[)?(<b>)?\\d+\\.\\d+|</body>)", html);
		root.put("pageNumber", pageNumber);
		root.put("children", array);
		for(int i=0;i<list.size();i++){
			String str = list.get(i);
			JSONObject json = new JSONObject();
			String update = "";
			String caption = RegexUtil.match("(?<=<p>(\\[)?(<b>)?)\\w+\\.\\w+(-\\w+(\\.\\w+)?)?", str);
			if(caption.equals("478.430")){
				System.out.println("1");
			}
			if(RegexUtil.isFind("^<p>(\\w|\\s)", str)){
				update = "2";
			}else if(RegexUtil.isFind("^<p>\\[", str)){
				update = "3";
				str = "";
			}else{
				update = "1";
			}
			str = RegexUtil.replaceFirst(caption+"\\.\\s", "", str);
			str = RegexUtil.replace("<b>", "<font color=\"#f00\"><u>", str);
			str = RegexUtil.replace("</b>", "</u></font>", str);
			str = RegexUtil.replace("\\[", "<font color=\"#f00\"><strike>", str);
			str = RegexUtil.replace("\\]", "</strike></font>", str);
			str = RegexUtil.replaceFirst("(?<=<p>(<b>)?)\\d+\\.\\d+\\.", "", str);
			
			json.put("caption", caption);
			json.put("description", "");
			json.put("content", str);
			json.put("update", update);
			array.put(json);
		}
		return root;
	}
}
