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

public class Iowa extends BaseStateUtils{
	

	public Iowa() {
		super();
	}

	public Iowa(String state) {
		super(state);
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			count = -1;
			System.out.println("Iowa连接失败");
			return ;
		}
		Element table = dom.select("table[class=legis]").first();
		Elements trs = table.select("tr");
		for(int i=1;i<trs.size();i++){
			Elements tds = trs.get(i).select("td");
			String billNumber = tds.get(0).select("a").first().text();
			System.out.println(billNumber);
			if(tds.get(3).text().equals("N/A")&&!RegexUtil.isFind("Item vetoed", tds.get(1).text())){
				continue;
			}
			//链接
			
			String link = "https://www.legis.iowa.gov/docs/publications/LGE/86/Attachments/"+RegexUtil.replace("\\s*", "", billNumber)+"_GovLetter.pdf";
			if(billNumber.equals("SF 2314")){
				System.out.println("123");
				
			}
			if(allActs.contains(link)){
				continue;
			}
			//签署日期
			String signDate = tds.get(3).text();
			if(!signDate.equals("N/A")){
				signDate = DateUtils.changeDateFormat(signDate);
			}
			//生效日期
			String eff = tds.get(4).text();
			if(RegexUtil.isFind("\\d", eff)){
				eff = DateUtils.changeDateFormat(eff);
			}
			
			//下载文件，添加记录
			try {
				DownloadUtils.downlaodPdf(link, path, billNumber);
			} catch (IOException e) {
				continue;
			}
			File file = new File(path+File.separator+billNumber+".pdf");
			AtsAct temp = new AtsAct();
			temp.setBillNumber(billNumber);
			temp.setState("Iowa");
			temp.setFileSize(file.length()/1024);
			temp.setSessionYear("2016");
			temp.setType(1);
			temp.setDay(DateUtils.getDate("yyyyMMdd"));
			temp.setDownloadFile(file.getAbsolutePath());
			temp.setEffectiveDate(eff);
			temp.setUrl(link);
			temp.setWorkMode(2);
			temp.setRemark("签署日期："+signDate);
			atsActService.save(temp);
			count++;
			allActs.add(link);
			System.out.println(billNumber);
		}
		
		
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		root.put("children", array);
		String html="";
		try {
			html = FileUtils.readFileToString(new File(act.getUploadFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		html = StringUtils.formatUploadFile(html);
		List<String> list = RegexUtil.matchAllList("<p>Sec[\\s\\S]*?(?=<p>Sec|$)", html);
		for(String str:list){
			JSONObject json = new JSONObject();
			String update = "2";
			String first = RegexUtil.match("<p>[\\s\\S]*?</p>", str);
			str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
			String caption = RegexUtil.match("\\w+\\.\\w+", first);
			String description = RegexUtil.match("<b>.*?</b>", first);
			description = RegexUtil.match("(?<="+caption+").*?(?=</b>)", first).trim();
			description = RegexUtil.replace("<[^>]*?>", "", description);
			if(RegexUtil.isFind("NEW SECTION", first)){
				update = "1";
				str = RegexUtil.replace("<p>", "<p><font color='#f00'><u>", str);
				str = RegexUtil.replace("</p>", "</u></font></p>", str);
				
			}else if(RegexUtil.isFind("by adding the following new subsection", first)){
				str = RegexUtil.replace("<u>.*?</u>", "", str);
				str = RegexUtil.replace("<p>", "<p><font color='#f00'><u>", str);
				str = RegexUtil.replace("</p>", "</u></font></p>", str);
				str = RegexUtil.replace("<font color='#f00'></font>\\. ", "", str);
			}else if(!RegexUtil.isFind("subsection", first)&&RegexUtil.isFind("amended to read as follows", first)){
				first = RegexUtil.match("<p>.*?</p>", str);
				first = RegexUtil.replace("<strike>.*?</strike>", "", first);
				description = RegexUtil.match("<b>.*?</b>", first);
				description = RegexUtil.match("(?<="+caption+").*?(?=</b>)", description).trim();
				description = RegexUtil.replace("<[^>]*?>", "", description);
				if(!description.isEmpty()){
					str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
				}
			}
			str = RegexUtil.replace("</?b>", "", str);
			description = RegexUtil.replace("�", "-", description);
			json.put("caption", "&#167;"+caption);
			json.put("description", description);
			json.put("content", str);
			json.put("update", update);
			json.put("shortName", "IA Code Sec. "+caption);
			array.put(json);
		}
		return root;
	}
	
	public static void main(String[] args) {
		String url = "http://coolice.legis.iowa.gov/Cool-ICE/default.asp?Category=BillInfo&Service=Enrolled";
		try {
			Document dom = Jsoup.connect(url).timeout(10000).get();
			Element table = dom.select("table[class=legis]").first();
			Elements trs = table.select("tr");
			for(int i=1;i<trs.size();i++){
				Elements tds = trs.get(i).select("td");
				if(tds.get(3).text().equals("N/A")){
					continue;
				}
				//签署日期
				String signDate = tds.get(3).text();
				signDate = DateUtils.changeDateFormat(signDate);
				//生效日期
				String eff = tds.get(4).text();
				if(RegexUtil.isFind("\\d", eff)){
					eff = DateUtils.changeDateFormat(eff);
				}
				//链接
				String billNumber = tds.get(0).select("a").first().text();
				String link = "https://www.legis.iowa.gov/docs/publications/LGE/86/Attachments/"+RegexUtil.replace("\\s*", "", billNumber)+"_GovLetter.pdf";
				System.out.println(link);
				
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	
	}

}
