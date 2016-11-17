package com.thinkgem.jeesite.modules.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.DownloadUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.SpringBeanUtils;
import com.thinkgem.jeesite.common.websocket.GlobalHandler;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;
import com.thinkgem.jeesite.modules.ats.entity.AtsTree;
import com.thinkgem.jeesite.modules.ats.service.AtsActService;
import com.thinkgem.jeesite.modules.ats.service.AtsSectionService;
import com.thinkgem.jeesite.modules.ats.service.AtsSignService;
import com.thinkgem.jeesite.modules.ats.service.AtsTreeService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

public abstract class BasicTaskUtils {
	protected static AtsActService atsActService = (AtsActService) SpringBeanUtils.getBean("atsActService");
	protected static AtsSignService atsSignService = (AtsSignService) SpringBeanUtils.getBean("atsSignService");
	protected static AtsSectionService atsSectionService = (AtsSectionService) SpringBeanUtils.getBean("atsSectionService");
	protected static AtsTreeService atsTreeService = (AtsTreeService) SpringBeanUtils.getBean("atsTreeService");
	
	protected List<String> allActs;
	protected String path;
	protected JSONArray array = new JSONArray();
	protected String suffix;
	protected final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
	
	public abstract void doCheckUpdate(Document dom);
	public abstract List<JSONObject> parseAct(String filepath); 
	
	public void checkUpdates(AtsTask task){
		allActs = atsActService.findDownloadedActs(task.getState());
		path = DownloadUtils.getAtsDownloadPath(task.getState());
		try {
			Document dom = Jsoup.connect(task.getUrl()).userAgent(userAgent).timeout(10000).get();
			doCheckUpdate(dom);
		} catch (Exception e) {
			GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : Please try it again...");
			return;
		}
		analysisData(task);
		GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : "+task.getState()+" checking finished. Total count : "+array.length());
	}
	
	/**
	 * 分析更新的数据
	 * @param task
	 */
	public void analysisData(AtsTask task){
		if(array.length()>0){
			//有更新
			String pid = atsTreeService.getStateTreeId(task.getState());
			for(int i=0;i<array.length();i++){
				JSONObject json = array.getJSONObject(i);
				saveAct(json,pid);
			}
		}
	}
	
	public void saveAct(JSONObject json,String pid){
		// 保存Act数据
		AtsAct act = new AtsAct();
		String billNumber = json.getString("billNumber"); 
		act.setBillNumber(billNumber);
		act.setState(json.getString("state"));
		act.setDay(DateUtils.getTodayString());
		act.setUrl(json.getString("href"));
		act.setDownloadFile(path+File.separator+billNumber+suffix);
		act.setFileSize(new File(path+File.separator+billNumber+suffix).length());
		act.setWorkMode(Global.getWorkMode(json.getString("state")));
		act.setStatus(1);
		act.setDelFlag("0");
		act.setEffectiveDate(json.has("eff")?json.getString("eff"):"");
		act.setType(suffix.equals(".html")?2:1);
		act.setRemark(json.has("remark")?json.getString("remark"):"");
		act.setSessionYear("2016");
		act.setPageCount(0);
		atsActService.save(act);
		//保存tree数据
		AtsTree tree = new AtsTree(pid, act.getBillNumber(), "0", "0", "viewAct", "0", act.getId(), "1", "", "4");
		atsTreeService.save(tree);
	}
	
	public void parseAct(AtsAct act, String treeId) throws IOException{
		AtsSign sign = new AtsSign(act.getId(), UserUtils.getUser().getName(), "1");
		sign = atsSignService.get(sign);
		sign.setStatus("2");
		atsSignService.save(sign);
		List<JSONObject> sections = parseAct(act.getDownloadFile());
		int parseOrder = 0;
		for(JSONObject json : sections){
			AtsSection section = new AtsSection(sign.getId(), sign.getEditor());
			section.setBillNumber(act.getBillNumber());
			section.setCaption(json.has("caption")?json.getString("caption").isEmpty()?act.getBillNumber():json.getString("caption"):act.getBillNumber());
			section.setEff(json.getString("eff").equals("im")?act.getEffectiveDate():json.getString("eff"));
			section.setShortName(json.getString("shortName"));
			section.setExp("");
			section.setLink("");
			section.setUpdateType(json.getInt("updateType"));
			section.setDescription(json.has("description")?json.getString("description"):"");
			section.setStatus(1);
			section.setIsDel(0);
			section.setParseOrder(parseOrder);
			parseOrder++;
			atsSectionService.save(section);
			FileUtils.write(new File("C:\\ATS\\HTML\\"+UserUtils.getUser().getName()+File.separator+act.getState()+File.separator+act.getBillNumber()+File.separator+section.getId()+".html"), json.getString("content"));
			section.setContent("C:\\ATS\\HTML\\"+UserUtils.getUser().getName()+File.separator+act.getState()+File.separator+act.getBillNumber()+File.separator+section.getId()+".html");
			atsSectionService.save(section);
			AtsTree parentTree = atsTreeService.get(treeId);
			parentTree.setIconSkin("1");
			parentTree.setIsParent("1");
			parentTree.setOpen("1");
			atsTreeService.save(parentTree);
			AtsTree tree = new AtsTree(treeId,section.getCaption(),"0","0","showEditor","0",section.getId(),"1",UserUtils.getUser().getName(),"9");
			atsTreeService.save(tree);
		}
	}
}
