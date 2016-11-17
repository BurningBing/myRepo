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

public class Kentucky extends BaseStateUtils{
	public Kentucky() {
		super();
	}

	public Kentucky(String state) {
		super(state);
	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			count = -1;
			System.out.println("Kentucky连接失败");
			return;
		}
		Element div = dom.select("div[class=StandardText leftDivMargin]").first();
		Elements list = div.select("a");
		String prefix = "";
		for(Element el:list){
			prefix = RegexUtil.match("\\D*", el.text()).isEmpty()?prefix:RegexUtil.match("\\D*", el.text());
			if(prefix.equals("HB")||prefix.equals("SB")){
				String billNumber = prefix+RegexUtil.match("\\d+", el.text());
				String href = el.absUrl("href");
				if(allActs.contains(href)){
					continue;
				}
				try {
					dom = Jsoup.connect(href).timeout(10000).get();
				} catch (IOException e) {
					continue;
				}
				String link = dom.select("a").get(2).absUrl("href");
				//下载文件，添加记录
				try {
					DownloadUtils.downlaodPdf(link, path, billNumber);
				} catch (IOException e) {
					continue;
				}
				File file = new File(path+File.separator+billNumber+".pdf");
				AtsAct temp = new AtsAct();
				temp.setBillNumber(billNumber);
				temp.setState("Kentucky");
				temp.setFileSize(file.length()/1024);
				temp.setSessionYear("2016");
				temp.setType(1);
				temp.setDay(DateUtils.getDate("yyyyMMdd"));
				temp.setDownloadFile(file.getAbsolutePath());
				temp.setEffectiveDate("");
				temp.setUrl(href);
				temp.setWorkMode(2);
				temp.setRemark("正文链接："+link);
				atsActService.save(temp);
				count++;
				allActs.add(href);
				System.out.println(billNumber);
			}
		}
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		String html = "";
		try {
			html = FileUtils.readFileToString(new File(act.getUploadFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		html = StringUtils.formatUploadFile(html);
		html = RegexUtil.replace("</?b[^>]*?>|</?i[^>]*?>", "", html);
		html = RegexUtil.replace("\\[|\\]", "", html);
		List<String> list = RegexUtil.matchAllList("<p>(SECTION|Section)[\\s\\S]*?(?=<p>(SECTION|Section)|</html>)", html);
		int pageNumber = RegexUtil.matchAllList("<hr[^>]*?>", html).size()+1;
		root.put("pageNumber", pageNumber);
		root.put("children", array);
		for(int i=0;i<list.size();i++){
			JSONObject json = new JSONObject();
			String str = list.get(i);
			String first = RegexUtil.match("<p>.*?</p>", str);
			String caption = "";
			String eff = "See Note";
			String update = "2";
			if(RegexUtil.isFind("A NEW SECTION OF KRS.*?IS CREATED", first)){
				caption = act.getBillNumber();
			}else{
				caption = RegexUtil.match("(?<=KRS\\s)\\d+.*?(?=\\s)", first).trim();
			}
			if(caption.isEmpty()){
				caption = act.getBillNumber();
			}
			str = RegexUtil.replaceFirst("<p>.*?</p>", "", str).trim();
			str = RegexUtil.replace("<hr[^>]*?>", "", str);
			if(RegexUtil.isFind("IS CREATED TO READ AS FOLLOW", first)){
				update = "1";
			}else if(RegexUtil.isFind("is amended to read", first)){
				update = "2";
			}
			json.put("caption", caption);
			json.put("description", "");
			json.put("content", str);
			json.put("update", update);
			json.put("eff", eff);
			array.put(json);
		}
		return root;
	}
	
	public static void main(String[] args) throws IOException {
		String html = FileUtils.readFileToString(new File("C:\\Users\\Chad\\Desktop\\Kentucky\\景斌\\132.htm"));
		html = StringUtils.formatUploadFile(html);
		html = RegexUtil.replace("</?b[^>]*?>|</?i[^>]*?>", "", html);
		html = RegexUtil.replace("\\[|\\]", "", html);
		List<String> list = RegexUtil.matchAllList("<p>(SECTION|Section)[\\s\\S]*?(?=<p>(SECTION|Section)|</html>)", html);
		for(int i=0;i<list.size();i++){
			String str = list.get(i);
			String first = RegexUtil.match("<p>.*?</p>", str);
			String caption = "";
			String eff = "See Note";
			if(RegexUtil.isFind("ANEW SECTION OF KRS \\d+\\.\\d+ TO \\d+\\.\\d+ IS CREATED", first)){
				caption = "actNumber";
			}else{
				caption = RegexUtil.match("(?<=KRS\\s)\\d+.*?(?=\\s)", first);
			}
			str = RegexUtil.replaceFirst("<p>.*?</p>", "", str).trim();
			System.out.println(eff+","+caption);
			
					
		}
	}

}
