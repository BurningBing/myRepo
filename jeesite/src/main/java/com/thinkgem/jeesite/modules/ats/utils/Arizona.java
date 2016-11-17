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
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class Arizona extends BaseStateUtils{
	public Arizona() {
		super();
	}

	public Arizona(String state) {
		super(state);
	}
	
	public static void main(String[] args) throws IOException {
		File file = new File("C:/ATS/Download/Arizona/20160601/SB1413.html");
		String html = FileUtils.readFileToString(file);
		html = RegexUtil.match("<div class=\"WordSection2\">[\\s\\S]*?</div>", html);
		html = RegexUtil.replace("</?span[^>]*?>", "", html);
		html = RegexUtil.replace("<p[^>]*?>", "<p>", html);
		html = RegexUtil.replace("START_STATUTE", "", html);
		html = RegexUtil.replace("END_STATUTE", "", html);
		html = RegexUtil.replace("&#x2011;", "-", html);
		List<String> list = RegexUtil.matchAllList("<p>Sec[\\s\\S]*?(?=<p>Sec|$)", html);
		for(String str:list){
			String update = "2";
			String first = StringUtils.getFirst(str);
			str = StringUtils.removeFirst(str);
			str = RegexUtil.replace("<p>\\s*?</p>", "", str).trim();
			String title = RegexUtil.match("(?<=Title )\\w+", first);
			String chapter = RegexUtil.match("(?<=<p>).*?(?=</p>)", str);
			str = StringUtils.removeFirst(str);
			String cDesc = StringUtils.getFirst(str);
			cDesc = RegexUtil.replace("<[^>]*?>", "", cDesc);
			str = StringUtils.removeFirst(str);
			List<String> articleList = RegexUtil.matchAllList("<p>ARTICLE[\\s\\S]*?(?=<p>ARTICLE|$)", str);
			for(String article:articleList){
				first = StringUtils.getFirst(article);
				String articleCaption = RegexUtil.match("ARTICLE \\w+(\\.\\w+)?", first);
				String articleDescription = RegexUtil.replace(articleCaption, "", first);
				articleDescription = RegexUtil.replace("<[^>]*?>", "", articleDescription.trim());
				articleDescription = RegexUtil.replace("^\\.", "", articleDescription.trim()).trim();
				List<String> sectionList = RegexUtil.matchAllList("<p><b class='caption'>[\\s\\S]*?(?=<p><b class='caption'>|$)", article);
				
			}
			
			break;
		}
		
	}
	
	public String clearHtml(String html){
		html = RegexUtil.replace("<form[^>]*?>[\\s\\S]*?</form>", "", html);
		html = RegexUtil.replace("&nbsp;", " ", html);
		html = RegexUtil.replace("<a[^>]*?>[\\s\\S]*?</a>", "", html);
		html = RegexUtil.replace("<ul[^>]*?>[\\s\\S]*?</ul>", "", html);
		html = RegexUtil.replace("<li[^>]*?>[\\s\\S]*?</li>", "", html);
		html = RegexUtil.replace("</?li[^>]*?>|</?ul[^>]*?>", "", html);
		html = RegexUtil.replace("<!--[\\s\\S]*?-->", "", html);
		html = RegexUtil.replace("<img[^>]*?>", "", html);
		html = RegexUtil.replace("<span[^>]*?green[^>]*?>([\\s\\S]*?)</span>", "<b class='caption'>$1</b>", html);
		html = RegexUtil.replace("<span[^>]*?purple[^>]*?>([\\s\\S]*?)</span>", "<b class='desc'>$1</b>", html);
		html = RegexUtil.replace("<span[^>]*?red[^>]*?>([\\s\\S]*?)</span>", "<font color=\"#f00\"><strike>$1</strike></font>", html);
		html = RegexUtil.replace("<span[^>]*?blue[^>]*?>([\\s\\S]*?)</span>", "<font color=\"#f00\"><u>$1</u></font>", html);
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
		} catch (Exception e) {
			count = -1;
			System.out.println("连接失败");
			return;
		}
		Elements sessionLawsElements = dom.select("table[class=ContentAreaBackground]>tbody>tr>td>table>tbody>tr");
		for (int i = 2; i < sessionLawsElements.size(); i++) {
			Element sessionLawElement = sessionLawsElements.get(i);
			String chapterNumber = sessionLawElement.select("td").get(0).text();
			String billNumber = sessionLawElement.select("td").get(2).text();
			String downloadHref = sessionLawElement.select("td").get(0).select("a").first().attr("abs:href");
			
			if(!allActs.contains(downloadHref)){
				// 新的Act
				try {
					dom = Jsoup.parse(new URL(downloadHref).openStream(), "windows-1252", downloadHref);
				} catch (Exception e) {
					continue;
				}
				String html = dom.body().html();
				html = clearHtml(html);
				FileUtils.writeToFile(path+File.separator+billNumber+".html", html, false);
				File file = new File(path+File.separator+billNumber+".html");
				AtsAct temp = new AtsAct();
				temp.setBillNumber(billNumber);
				temp.setState("Arizona");
				temp.setFileSize(file.length()/1024);
				temp.setType(2);
				temp.setSessionYear("2016");
				temp.setDay(DateUtils.getDate("yyyyMMdd"));
				temp.setWorkMode(1);
				temp.setDownloadFile(file.getAbsolutePath());
				temp.setEffectiveDate("");
				temp.setUrl(downloadHref);
				temp.setRemark(chapterNumber);
				atsActService.save(temp);
				count++;
				allActs.add(downloadHref);
			}
		}
	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		
		JSONObject root = new JSONObject();
		JSONArray array = new JSONArray();
		root.put("children", array);
		String html = "";
		try {
			html = FileUtils.readFileToString(new File(act.getDownloadFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		html = RegexUtil.match("<div class=\"WordSection2\">[\\s\\S]*?</div>", html);
		html = RegexUtil.replace("</?span[^>]*?>", "", html);
		html = RegexUtil.replace("<p[^>]*?>", "<p>", html);
		html = RegexUtil.replace("START_STATUTE", "", html);
		html = RegexUtil.replace("END_STATUTE", "", html);
		html = RegexUtil.replace("&#x2011;", "-", html);
//		html = RegexUtil.replace("<u>", "<u style='text-transform:uppercase'>", html);
		List<String> list = RegexUtil.matchAllList("<p>(Section \\d+\\.|Sec\\.)[\\s\\S]*?(?=<p>(Section \\d+\\.|Sec\\.)|$)", html);
		for(String str:list){
			JSONObject json = new JSONObject();
			String update = "2";
			String first = RegexUtil.match("<p>.*?</p>", str);
			if(RegexUtil.isFind("is amended by adding chapter", first)){
				JSONObject jChapter = new JSONObject();
				String title = RegexUtil.match("(?<=Title )\\w+", first);
				str = StringUtils.removeFirst(str);
				String chapter = RegexUtil.match("(?<=<p>).*?(?=</p>)", str);
				chapter = RegexUtil.replace("<[^>]*?>", "", chapter);
				String cDesc = StringUtils.getFirst(str);
				cDesc = RegexUtil.replace("<[^>]*?>", "", cDesc);
				jChapter.put("caption", chapter);
				jChapter.put("description", cDesc);
				jChapter.put("update", "1");
				jChapter.put("content", "");
				jChapter.put("shortName", "ARS "+title+"-"+chapter);
				array.put(jChapter);
				str = StringUtils.removeFirst(str);
				List<String> articleList = RegexUtil.matchAllList("<p>ARTICLE[\\s\\S]*?(?=<p>ARTICLE|$)", str);
				for(String article:articleList){
					JSONObject jArticle = new JSONObject();
					first = StringUtils.getFirst(article);
					String articleCaption = RegexUtil.match("ARTICLE \\w+(\\.\\w+)?", first);
					String articleDescription = RegexUtil.replace(articleCaption, "", first);
					articleDescription = RegexUtil.replace("<[^>]*?>", "", articleDescription.trim());
					articleDescription = RegexUtil.replace("^\\.", "", articleDescription.trim()).trim();
					jArticle.put("caption", articleCaption);
					jArticle.put("description", articleDescription);
					jArticle.put("content", "");
					jArticle.put("update", "1");
					jArticle.put("shortName", "ARS "+title+"-"+chapter+"-"+articleCaption);
					array.put(jArticle);
					List<String> sectionList = RegexUtil.matchAllList("<p><b class='caption'>[\\s\\S]*?(?=<p><b class='caption'>|$)", article);
					for(String sec:sectionList){
						json = new JSONObject();
						first = StringUtils.getFirst(sec);
						String caption = RegexUtil.match("(?<=<b class='caption'>).*?(?=</b>)", sec);
						caption = RegexUtil.replace("\\.$", "", caption);
						if(caption.isEmpty()){
							continue;
						}
						String description = RegexUtil.match("(?<=<b class='desc'>).*?(?=</b>)", sec);
						sec = StringUtils.removeFirst(sec);
						json.put("caption", caption.trim());
						json.put("description", description.trim());
						json.put("update", update);
						json.put("content", sec);
						json.put("update", "1");
						json.put("eff", "08/06/2016");
						array.put(json);
						
					}
				}
			}else{
				str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
				String caption = RegexUtil.match("(?<=<b class='caption'>).*?(?=</b>)", str);
				caption = RegexUtil.replace("\\.$", "", caption);
				if(caption.isEmpty()){
					continue;
				}
				String description = RegexUtil.match("(?<=<b class='desc'>).*?(?=</b>)", str);
				str = RegexUtil.replace("<b class='caption'>.*?</b>\\.?", "", str);
				str = RegexUtil.replace("<b class='desc'>.*?</b>", "", str);
				str = RegexUtil.replace("<p>\\s*?</p>", "", str).trim();
				json.put("caption", caption.trim());
				json.put("description", description.trim());
				json.put("update", update);
				json.put("content", str);
				json.put("eff", "08/06/2016");
				array.put(json);
			}
		}
		return root;
	}

}
