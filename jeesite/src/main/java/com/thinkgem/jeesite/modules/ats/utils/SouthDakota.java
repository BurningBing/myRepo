package com.thinkgem.jeesite.modules.ats.utils;

import java.io.File;
import java.io.IOException;

import org.activiti.engine.impl.util.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class SouthDakota extends BaseStateUtils{
	
	
	
	public SouthDakota() {
		super();
	}

	public SouthDakota(String state) {
		super(state);
	}

	public static void main(String[] args) throws IOException {
		String url = "http://legis.sd.gov/Legislative_Session/Bills/Bill.aspx?File=SB131ENR.htm&Session=2016";
		Document dom = null;
		dom = Jsoup.connect(url).timeout(10000).get();
		String html = dom.body().html();
		html = StringUtils.formatHtmlFile(html);
		html = RegexUtil.replace("<table[\\s\\S]*?</table>", "", html);
		html = RegexUtil.replace("<footer[^>]*?>[\\s\\S]*?</footer>", "", html);
		html = RegexUtil.replace("(\\d+(\\.\\d+)?-\\d+(\\.\\d+)?-\\d+(\\.\\d+)?)", "<mark>$1</mark>", html);
		System.out.println(html);
		
	}
	
	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			System.out.println("连接失败");
			return;
		}
		Elements tables = dom.getElementById("ContentPlaceHolder1_divBillTables").select("table");
		for(int i=0;i<tables.size();i++){
			Elements trs = tables.get(i).select("tr");
			for(int j=1;j<trs.size();j++){
				Elements tds = trs.get(j).select("td");
				String href = tds.get(0).select("a").first().absUrl("href");
				if(!allActs.contains(href)){
					String signDate = "";
					String billNumber = tds.get(0).text();
					billNumber = RegexUtil.replace("(?<=B).*?(?=\\d)", " ", billNumber);
					try {
						dom = Jsoup.connect(href).timeout(10000).get();
					} catch (IOException e1) {
						continue;
					}
					Elements t = dom.getElementById("ContentPlaceHolder1_ctl15_tblBillActions").select("tr").last().select("td");
					if(RegexUtil.isFind("Signed by Governor", t.get(1).text())){
						signDate = t.get(0).text();
					};
					t = dom.getElementById("ContentPlaceHolder1_ctl15_tblBillVersions").select("tr").last().select("td");
					String link = t.get(1).select("a").first().absUrl("href");
					try {
						String html = Jsoup.connect(link).timeout(10000).get().body().html();
						html = StringUtils.formatHtmlFile(html);
						html = RegexUtil.replace("<table[\\s\\S]*?</table>", "", html);
						html = RegexUtil.replace("<footer[^>]*?>[\\s\\S]*?</footer>", "", html);
						html = RegexUtil.replace("(?<=That )(&#167; (\\w+(\\.\\w+)?-\\w+(\\.\\w+)?-\\w+(\\.\\w+)?))", "<mark>$1</mark>", html);
						FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
					} catch (IOException e) {
						continue;
					}
					File file = new File(path+File.separator+billNumber+".html");
					AtsAct temp = new AtsAct();
					temp.setBillNumber(billNumber);
					temp.setState("SouthDakota");
					temp.setFileSize(file.length()/1024);
					temp.setSessionYear("2016");
					temp.setType(2);
					temp.setDay(DateUtils.getDate("yyyyMMdd"));
					temp.setDownloadFile(file.getAbsolutePath());
					temp.setEffectiveDate("");
					temp.setUrl(href);
					temp.setWorkMode(1);
					temp.setRemark("Signed Date: "+signDate);
					atsActService.save(temp);
					count++;
					System.out.println(billNumber);
				}
				
				
			}
		}		
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		return null;
	}

}
