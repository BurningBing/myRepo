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

public class Florida extends BaseStateUtils{
	
	public static void download(){
		init("Florida");
		Document dom = null;
		// 链接
		try {
			dom =  Jsoup.connect("http://laws.flrules.org/").timeout(20000).get();
		} catch (Exception e) {
			GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : Please try it again...");
		}
		// 下载
		
		Element table = dom.select(".views-table").first();
		Elements trs = table.select("tbody tr");
		for(Element tr:trs){
			String billNumber = tr.select("td").last().text();
			String href = tr.select("td").first().select("a").first().absUrl("href");
			long fileSize = Long.parseLong(tr.select("td").get(1).text().replaceAll("\\s.*?$", ""));
			if(allActs.contains(href)||!RegexUtil.isFind("(S|H) \\d+", billNumber)){
				continue;
			}
			try {
				File file = DownloadUtils.downlaodPdf(href, path, billNumber);
				//保存Act数据
				AtsAct act = new AtsAct("Florida", billNumber, href, file.getAbsolutePath(), fileSize, 1, day, 2, "");
				atsActService.save(act);
				//保存Tree数据
				AtsTree tree = new AtsTree(getStateTreeId("Florida"),act.getBillNumber(),"0","0","viewAct","0",act.getId(),"1",UserUtils.getUser().getName(),"9");
				atsTreeService.save(tree);
				GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : "+ billNumber+" download success...");
				downloadCount++;
			}catch (Exception e) {
				GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : "+billNumber+" download failed");
			}
		}
		GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : Florida checking finished. Total count : "+downloadCount);
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
				filepath = "C:\\ATS\\HTML\\"+sign.getEditor()+File.separator+"Florida"+File.separator+billNumber+File.separator+section.getCaption()+"_"+section.getParseOrder()+".html";
				section.setContent(filepath);
				FileUtils.write(new File(filepath), content);
				atsSectionService.save(section);
			}catch(Exception e){
				e.printStackTrace();
				break;
			}
			AtsTree tree = new AtsTree(treeId, section.getCaption(), "0", "0", "showEditor", "0", section.getId(), "1", UserUtils.getUser().getName(),"9");
			atsTreeService.save(tree);
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
	
	public static AtsSection refreshSection(AtsSection section){
		AtsSign sign = atsSignService.get(section.getPid());
		String html = readFile(new File(sign.getUploadFile()));
		html = StringUtils.formatUploadFile(html);
		List<String> list = getList(html);
		List<AtsSection> sections = forEachList(list);
		AtsSection temp = sections.get(section.getParseOrder());
		section.setCaption(temp.getCaption());
		section.setDescription(temp.getDescription());
		section.setShortName(temp.getShortName());
		section.setUpdateType(temp.getUpdateType());
		section.setEff(temp.getEff());
		try {
			FileUtils.writeStringToFile(new File(section.getContent()), temp.getContent());
		} catch (IOException e) {
			e.printStackTrace();
		}
		section.setRemarks(section.getContent());
		section.setContent(temp.getContent());
		
		//tree
		AtsTree tree = new AtsTree();
		tree.setFid(section.getId());
		tree.setMethod("showEditor");
		tree = atsTreeService.get(tree);
		tree.setName(section.getCaption());
		atsTreeService.save(tree);
		return section;
	}
}
