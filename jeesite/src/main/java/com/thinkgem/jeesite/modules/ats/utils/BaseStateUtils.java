package com.thinkgem.jeesite.modules.ats.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties.BaseState;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.DownloadUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.SpringBeanUtils;
import com.thinkgem.jeesite.common.websocket.GlobalHandler;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsMessage;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;
import com.thinkgem.jeesite.modules.ats.service.AtsActService;
import com.thinkgem.jeesite.modules.ats.service.AtsSectionService;
import com.thinkgem.jeesite.modules.ats.service.AtsSignService;
import com.thinkgem.jeesite.modules.ats.service.AtsTaskService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

public abstract class BaseStateUtils {
	public BaseStateUtils(){}
	
	protected AtsActService atsActService = (AtsActService) SpringBeanUtils.getBean("atsActService");
	protected AtsSectionService atsSectionService = (AtsSectionService) SpringBeanUtils.getBean("atsSectionService");
	protected AtsTaskService atsTaskService = (AtsTaskService) SpringBeanUtils.getBean("atsTaskService");
	protected AtsSignService atsSignService = (AtsSignService) SpringBeanUtils.getBean("atsSignService");
	
	protected List<String> allActs = new ArrayList<String>();
	protected String path;
	protected int count;
	public abstract void doCheckingUpdate(AtsTask task);
	public abstract JSONObject doParseAct(AtsAct act);
	public BaseStateUtils(String state){
		allActs = atsActService.findDownloadedActs(state);
		path = DownloadUtils.getAtsDownloadPath(state);
		count = 0;
	}
	
	
	/**
	 * 检查更新
	 * @param task
	 */
	public void checkingUpdate(AtsTask task){
		String state = task.getState();
		try {
			@SuppressWarnings("unchecked")
			Class<BaseStateUtils> claze = (Class<BaseStateUtils>) Class.forName(Global.STATE_PACKAGE+"."+state);
			BaseStateUtils entity = claze.getDeclaredConstructor(String.class).newInstance(state);
			entity.doCheckingUpdate(task);
			AtsMessage message = new AtsMessage();
			message.setFromUser("管理员");
			message.setToUser(UserUtils.getUser().getName());
			message.setContent(state+"更新"+count+"个");
			if(entity.count==-1){
				message.setContent(state+"连接失败");
			}else if(entity.count==0){
				message.setContent(state+"没有更新");
			}else{
				message.setContent(state+"更新"+entity.count+"个");
			}
			GlobalHandler.sendMessage(UserUtils.getUser().getName(), message.getContent(), "管理员");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int parseAct(AtsAct act){
		AtsTask task = atsTaskService.getByState(act.getState());
		try {
			@SuppressWarnings("unchecked")
			Class<BaseStateUtils> claze = (Class<BaseStateUtils>) Class.forName(Global.STATE_PACKAGE+"."+act.getState());
			BaseStateUtils entity = claze.getDeclaredConstructor(String.class).newInstance(act.getState());
			JSONObject jInfor = entity.doParseAct(act);
			if(jInfor.has("pageNumber")){
				act.setPageCount(jInfor.getInt("pageNumber"));
				atsActService.save(act);
			}
			
			JSONArray aInfor = jInfor.getJSONArray("children");
			//构建树状图（State-Bill）
			if(aInfor.length()>0){
				for(int i=0;i<aInfor.length();i++){
					AtsSection section = new AtsSection();
					int parseOrder = i+1;
					JSONObject json = aInfor.getJSONObject(i);
					// 保存正文内容
					File file = new File(Global.getHtmlPath(act.getState())+act.getBillNumber()+"("+parseOrder+").html");
					FileUtils.writeStringToFile(file, json.getString("content"));
					// 添加Section表数据
					section.setPid(act.getId());
					section.setCaption((task.getPrefix()==null?"":task.getPrefix())+json.getString("caption"));
					section.setDescription(json.has("description")?json.getString("description"):"");
					section.setContent(file.getAbsolutePath());
					section.setBillNumber(act.getBillNumber());
					if(jInfor.has("eff")){
						section.setEff(jInfor.getString("eff"));
					}else{
						section.setEff(json.has("eff")?json.getString("eff"):act.getEffectiveDate());
					}
					section.setShortName(json.has("shortName")?json.getString("shortName"):task.getShortName()+json.getString("caption"));
					section.setUpdateType(json.has("update")?json.getInt("update"):2);
					section.setParseOrder(parseOrder);
					section.setEditor(UserUtils.getUser().getName());
					section.setExp("");
					section.setLink("");
					atsSectionService.save(section);
				}
			}
			return aInfor.length();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public void stopTask(String errorLog,String errorInfor,String state){
//		NonCaseA2sMessage message = new NonCaseA2sMessage();
//		message.setContent(errorInfor);
//		message.setFromUser("管理员");
//		message.setToUser(UserUtils.getUserName());
//		nonCaseA2sMessageService.save(message);
//		logInfor(errorLog);
//		FileUtils.writeToFile("C:\\FCC\\opt\\fcms\\NoneCase\\Log\\"+state+"\\"+DateUtils.getDate("yyyyMMdd")+".txt", log.toString(), true);
//		SystemWebSocketHandler.sendMessage(message);
//		return;
	}
}
