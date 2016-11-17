package com.thinkgem.jeesite.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection.Response;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.DownloadUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.XmlGenerator;
import com.thinkgem.jeesite.common.utils.XmlGenerator.Index;

public class NewMexico {
	
	public static void main(String[] args) {
		try {
//			download();
			parse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void download() throws Exception{
		String userAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.84 Safari/537.36";
		String url = "https://www.nmlegis.gov/Legislation/BillFinder/Chaptered";
		Response response = Jsoup.connect(url).userAgent(userAgent).timeout(20000).execute();
		Map<String, String> cookies = response.cookies();
		Document dom = response.parse();
		Elements inputs = dom.select("input[type=hidden]");
		Map<String, String> data = new HashMap<String,String>();
		for(Element el:inputs){
			if(!el.val().isEmpty()){
				data.put(el.attr("name"), el.val());
			}
		}
		data.put("ctl00$MainContent$ddlSessionStart", "53");
		data.put("ctl00$MainContent$ddlSessionEnd", "53");
		data.put("ctl00$MainContent$btnSearch", "Go");
		data.put("ctl00$MainContent$ddlResultsPerPage", "2000");
		dom = Jsoup.connect(url).userAgent(userAgent).timeout(100000).data(data).cookies(cookies).post();
		Element table = dom.getElementById("MainContent_gridViewLegislation");
		Elements trs = table.select("tr");
		for(int i=1;i<trs.size();i++){
			Elements tds = trs.get(i).select("td");
			String billNumber = tds.get(0).text();
			billNumber = RegexUtil.replace("\\*", "", billNumber);
			String temp = RegexUtil.replace(" ", "000", billNumber);
			temp = temp.toUpperCase();
			String href = "https://www.nmlegis.gov/Sessions/16%20Special/final/"+temp+".pdf";
			DownloadUtils.downlaodPdf(href, "e:\\NewMexico", billNumber);
		}
	}
	
	public static void parse(){
		File file = new File("e:\\NewMexico\\SB 8.html");
		try {
			String html = FileUtils.readFileToString(file);
			List<String> list = RegexUtil.matchAllList("<p>SECTION[\\S\\s]*?(?=<p>SECTION|$)", html);
			XmlGenerator xg = new XmlGenerator("New Mexico");
			List<XmlGenerator.Index> indexes = new ArrayList<XmlGenerator.Index>();
			xg.setIndexes(indexes);
			for(String str:list){
				String update = "";
				String first = RegexUtil.match("<p>.*?</p>", str);
				if(first.contains("A new section of the")){
					update = "New";
				}
				if(first.contains("amended to read")){
					update = "Modify";
				}
				String caption = RegexUtil.match("\\w+(\\.\\w+)?-\\w+(\\.\\w+)?-\\w+(\\.\\w+)?", first);
				String description = RegexUtil.match("([A-Z-]+(\\s|--)?)+\\.(?=--)", str);
				str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
				str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
				if(update.equals("New")){
					str = RegexUtil.replace("<p>(.*?)</p>", "<p><font color=\"#f00\"><u>$1</u></p>", str);
				}
				Index index = new XmlGenerator().new Index();
				index.setActNumber("Sb 8");
				index.setCaption(caption.trim());
				index.setContent(str.trim());
				index.setDescription(description.trim());
				index.setEff("10/21/2016");
				index.setType(update);
				index.setShortName("NM Stat. "+caption);
				indexes.add(index);
			}
			xg.generateFile(new File("E:\\NewMexico\\Sb 8.xml"));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
