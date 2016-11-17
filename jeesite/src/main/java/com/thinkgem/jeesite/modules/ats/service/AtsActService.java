/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.websocket.GlobalHandler;
import com.thinkgem.jeesite.modules.ats.dao.AtsActDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.ats.entity.AtsTree;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * actService
 * @author Chad
 * @version 2016-03-09
 */
@Service
@Transactional(readOnly = true)
public class AtsActService extends CrudService<AtsActDao, AtsAct> {
	JSONArray tree = null;
	JSONObject cur = null;
	int index = 0;
	
	@Autowired
	AtsSignService atsSignService;
	@Autowired
	AtsTreeService atsTreeService;
	@Autowired
	AtsSectionService atsSectionService;

	public AtsAct get(String id) {
		AtsAct act = new AtsAct();
		act.setId(id);
		return super.get(act);
	}
	
	public List<AtsAct> findList(AtsAct atsAct) {
		return super.findList(atsAct);
	}
	
	public Page<AtsAct> findPage(Page<AtsAct> page, AtsAct atsAct) {
		return super.findPage(page, atsAct);
	}
	
	@Transactional(readOnly = false)
	public void save(AtsAct atsAct) {
		super.save(atsAct);
	}
	
	@Transactional(readOnly = false)
	public void delete(AtsAct atsAct) {
		super.delete(atsAct);
	}
	
	public List<String> findDownloadedActs(String state){
		return super.dao.findDownloadedActs(state);
	}
	
	public int findCount(String state){
		return super.dao.findCount(state);
	}
	
	public List<String> findUnsignedStates(){
		return super.dao.findUnsignedStates();
	}
	
	public List<String> findDateByState(String state){
		return super.dao.findDateByState(state);
	}

	public List<AtsAct> findSignActs(String state,int count){
		return super.dao.findSignActs(state,count,UserUtils.getUser().getName());
	}
	
	public void signUpActs(AtsAct act){
		AtsSign sign = null;
		if(act.getWorkMode()!=2){
			// 单打
			saveSignData(act, false);
		}else{
			// 双打
			sign = atsSignService.get(new AtsSign(act.getId()));
			saveSignData(act, sign==null);
		}
		updateCountAfterSignUp(act.getState());
	}
	
	public void saveSignData(AtsAct act, boolean flag){
		if(!flag){
			act.setStatus(2);
			save(act);
		}
		AtsSign sign = new AtsSign(act.getId(), UserUtils.getUser().getName(), "1");
		atsSignService.save(sign);
		// Tree
		AtsTree state = atsTreeService.getOrSave(new AtsTree("7", act.getState(), "1", "1", "", "0", "", "1", sign.getEditor(), "7"));
		AtsTree tree = new AtsTree(state.getId(), act.getBillNumber(), "0", "0", "", "0", sign.getId(), "1", sign.getEditor(), "7");
		atsTreeService.save(tree);
	}
	
	public void updateCountAfterSignUp(String state){
		int totalCount = findCount(state);
		GlobalHandler.afterSignUp(totalCount,state);
	}
	
	public JSONArray findActsByEditor(){
		List<AtsAct> acts = super.dao.findActsByEditor(UserUtils.getUser().getName());
		JSONArray array = new JSONArray();
		for(AtsAct act:acts){
			JSONObject json = new JSONObject();
			json.put("id", act.getId());
			json.put("name", act.getBillNumber());
			json.put("iconSkin", "folder");
			json.put("isParent", true);
			json.put("index", act.getType()==1?"2-3":"2-4");
			array.put(json);
		}
		return array;
	}
	
	public void preCompare(List<AtsSign> signs){
		for(AtsSign sign:signs){
			AtsAct act = get(new AtsAct(sign.getPid()));
			AtsSign other = atsSignService.getOthers(sign);
			AtsSection temp1 = new AtsSection(sign.getId(), sign.getEditor());
			List<AtsSection> mySections = atsSectionService.findList(temp1);
			AtsSection temp2 = new AtsSection(other.getId(), other.getEditor());
			List<AtsSection> otherSections = atsSectionService.findList(temp2);
			if(mySections.size()!=otherSections.size()){
				saveSectionCountFeedback(mySections, otherSections, sign);
				saveSectionCountFeedback(otherSections, mySections, other);
				//添加feedback tree数据
				AtsTree tree1 = new AtsTree("11", act.getState(), "1", "1", "", "0", "", "1", sign.getEditor(), "11");
				AtsTree tree2 = new AtsTree("11", act.getState(), "1", "1", "", "0", "", "1", other.getEditor(), "11");
				tree1 = atsTreeService.getOrSave(tree1);
				tree2 = atsTreeService.getOrSave(tree2);
				AtsTree treeTem1 = new AtsTree(tree1.getId(), act.getBillNumber(), "0", "0", "showFeedback", "0", sign.getId(), "1", sign.getEditor(), "11");
				AtsTree treeTem2 = new AtsTree(tree2.getId(), act.getBillNumber(), "0", "0", "showFeedback", "0", other.getId(), "1", other.getEditor(), "11");
				atsTreeService.save(treeTem1);
				atsTreeService.save(treeTem2);
				act.setStatus(4);
				save(act);
				//更改Tree数据
				AtsTree treeTemp = new AtsTree();
				treeTemp.setRootId("7");
				treeTemp.setName(act.getBillNumber());
				treeTemp.setIsParent("1");
				List<AtsTree> tempList = atsTreeService.findList(treeTemp);
				for(AtsTree t1:tempList){
					t1.setIsHidden("0");
					t1.setStatus("3");
					atsTreeService.save(t1);
				}
				// 更改sign表状态
				sign.setStatus("5");
				other.setStatus("5");
				atsSignService.save(sign);
				atsSignService.save(other);
				GlobalHandler.invokeJsMethod(sign.getEditor(), "showTip","您有新的反馈信息，请及时查看！");
				GlobalHandler.invokeJsMethod(other.getEditor(), "showTip","您有新的反馈信息，请及时查看！");
			}else{
				
			}
		}
	}
	
	public void saveSectionCountFeedback(List<AtsSection> sections1, List<AtsSection> sections2, AtsSign signer){
		StringBuffer sb = new StringBuffer("<h4>反馈信息</h4><p>双方Section数量不一直，请各自检是否有遗漏或多余的Section。</p>");
		sb.append("<p>本人section数量："+sections1.size()+"</p>");
		sb.append("<ol>");
		for(AtsSection section:sections1){
			sb.append("<li>"+section.getCaption()+"</li>");
		}
		sb.append("</ol>");
		sb.append("<hr>");
		sb.append("<p>对方section数量："+sections2.size()+"</p>");
		sb.append("<ol>");
		for(AtsSection section:sections2){
			sb.append("<li>"+section.getCaption()+"</li>");
		}
		sb.append("</ol>");
		try {
			FileUtils.write(new File("c:\\ATS\\Feedback\\"+signer.getEditor()+"\\"+signer.getId()+".html"), sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}