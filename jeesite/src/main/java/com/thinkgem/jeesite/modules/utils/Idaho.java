package com.thinkgem.jeesite.modules.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.DownloadUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.websocket.GlobalHandler;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.ats.entity.AtsTree;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

public class Idaho extends BaseStateUtils {

	public static void download() {
		init("Idaho");
		Document dom = null;
		// 链接
		try {
			dom = Jsoup.connect("https://legislature.idaho.gov/legislation/2016/minidata.htm").timeout(20000).get();
		} catch (Exception e) {
			e.printStackTrace();
			GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog",
					"[" + DateUtils.getDateTime() + "] : Please try it again...");
			return;
		}
		// 下载
		Elements trs = dom.select("tr[id~=bill(H|S)\\d+a?]");
		for (Element tr : trs) {
			Elements tds = tr.select("td");
			String billNumber = tds.first().select("a").first().text().trim();
			String href = tds.first().select("a").first().absUrl("href");
			if (!allActs.contains(href)) {
				AtsAct act = new AtsAct();
				act.setBillNumber(billNumber);
				act.setState("Idaho");
				act.setDay(DateUtils.getDate("yyyyMMdd"));
				act.setUrl(href);
				act.setType(1);
				act.setWorkMode(2);
				act.setStatus(1);
				act.setDelFlag("0");
				act.setSessionYear("2016");
				if (tds.get(3).text().equals("LAW")) {
					if (RegexUtil.isFind("Approp,", tds.get(1).text())) {
						act.setDelFlag("1");
					}
					try {
						String eff = "";
						dom = Jsoup.connect(href).timeout(20000).get();
						Elements anchors = dom.select("a[id~=(H|S)\\d+(E|$)");
						String link = anchors.last().absUrl("href");
						Elements temp = dom
								.select("body > table > tbody > tr:nth-child(1) > td:nth-child(2) > table:nth-child(12) > tbody")
								.first().select("tr");
						eff = temp.get(temp.size() - 2).select("td").get(2).text();
						if(RegexUtil.isFind("(?<=Effective:\\W)\\d+/\\d+/\\d+$", eff)){
							eff = RegexUtil.match("(?<=Effective:\\W)\\d+/\\d+/\\d+$", eff);
						}
						act.setRemark(link);
						act.setEffectiveDate(eff);
						//写文件
						File file = DownloadUtils.downlaodPdf(link, path, billNumber);
						act.setDownloadFile(file.getAbsolutePath());
						act.setFileSize(file.length()/1024);
						atsActService.save(act);
						//保存Tree数据
//						AtsTree tree = new AtsTree(getStateTreeId("Idaho"), act.getBillNumber(), 0, 0, "viewAct", act.getId(), 1);
//						tree.setEditor("");
//						atsTreeService.save(tree);
						GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : "+ billNumber+" download success...");
						downloadCount++;
						allActs.add(href);
					} catch (IOException e) {
						GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : "+billNumber+" download failed");
					}
				}
			}
		}
		GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : Idaho checking finished. Total count : "+downloadCount);
	}

	public static void parseAct(AtsSign sign,String billNumber,String treeId){
		String html = readFile(new File(sign.getUploadFile()));
		int pageNumber = RegexUtil.matchAllList("<hr[^>]*?>", html).size()+1;
		html = StringUtils.formatUploadFile(html);
		List<String> list = getList(html);
		List<AtsSection> sections = forEachList(list);
		for(int i=0;i<sections.size();i++){
			AtsSection section = sections.get(i);
			String content = section.getContent();
			section.setPid(sign.getId());
			section.setBillNumber(billNumber);
			section.setEditor(sign.getEditor());
			section.setParseOrder(i);
			section.setStatus(1);
			section.setIsDel(0);
			section.setIsAdd(0);
			String filepath = "";
			try{
				filepath = "C:\\ATS\\HTML\\"+sign.getEditor()+File.separator+"Idaho"+File.separator+billNumber+File.separator+section.getCaption()+"_"+section.getParseOrder()+".html";
				section.setContent(filepath);
				FileUtils.write(new File(filepath), content);
				atsSectionService.save(section);
			}catch(Exception e){
				e.printStackTrace();
				break;
			}
//			AtsTree tree = new AtsTree(treeId, section.getCaption(), 0, 0, "showEditor", section.getId(), 1);
//			atsTreeService.save(tree);
		}
		
		if(sections.size()>0){
			sign.setStatus("2");
			sign.setPageNumber(pageNumber);
			sign.setSectionCount(sections.size());
			atsSignService.save(sign);
		}
	}
	
	public static List<String> getList(String html){
		return RegexUtil.matchAllList("<p>Section[\\s\\S]*?(?=<p>Section|$)", html);
	}
	
	public static String getCaption(String str){
		String caption = RegexUtil.match("\\d+\\.\\d+", str);
		return caption;
	}
	
	public static List<AtsSection> forEachList(List<String> list){
		List<AtsSection> sections = new ArrayList<AtsSection>();
		for(int i=0;i<list.size();i++){
			AtsSection section = new AtsSection();
			String str = list.get(i);
			String first = RegexUtil.match("<p>.*?</p>", str);
			String caption = getCaption(first);
			int update = 2;
			if(RegexUtil.isFind("is created to read", first)){
				update = 1;
			}else if(RegexUtil.isFind("amended to read|is added to subsection|is added to section|is reenacted to read", first)){
				update = 2;
			}else{
				continue;
			}
			String eff = "";
			if(i == list.size()-1){
				if(RegexUtil.isFind("shall take effect", first)){
					eff = RegexUtil.match("\\w+ \\d+, \\d+", first);
					if(!eff.isEmpty()){
						eff = DateUtils.changeDateFormat(eff);
					}
					continue;
				}
			}
			str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
			first = RegexUtil.match("<p>.*?</p>", str);
			String description = RegexUtil.replaceFirst(caption, "", first);
			description = RegexUtil.replace("<strike>.*?</strike>", "", description);
			description = RegexUtil.replace("<[^>]*?>", "", description).trim();
			str = RegexUtil.replaceFirst("<p>.*?</p>", "", str);
			String shortName = "Fla. Stat. Ch. "+caption;
			section.setCaption(caption);
			section.setDescription(description);
			section.setEff(eff);
			section.setShortName(shortName);
			section.setUpdateType(update);
			section.setContent(str);
			sections.add(section);
		}
		return sections;
	}
}
