/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.modules.ats.dao.AtsSectionDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.ats.entity.AtsTree;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * sectionService
 * @author Chad
 * @version 2016-03-24
 */
@Service
@Transactional(readOnly = true)
public class AtsSectionService extends CrudService<AtsSectionDao, AtsSection> {
	
	@Autowired
	private AtsSignService atsSignService;
	@Autowired
	private AtsTreeService atsTreeService;

	public AtsSection get(String id) {
		return super.get(id);
	}
	
	public List<AtsSection> findList(AtsSection atsSection) {
		return super.findList(atsSection);
	}
	
	public Page<AtsSection> findPage(Page<AtsSection> page, AtsSection atsSection) {
		return super.findPage(page, atsSection);
	}
	
	@Transactional(readOnly = false)
	public void save(AtsSection atsSection) {
		super.save(atsSection);
	}
	
	@Transactional(readOnly = false)
	public void delete(AtsSection atsSection) {
		super.delete(atsSection);
	}
	
	public int findMaxParseOrder(String pid){
		Integer parseOrder = super.dao.findMaxParseOrder(pid);
		return parseOrder ==null?0:parseOrder+1;
	}
	
	@Transactional(readOnly = false)
	public AtsSection addNewSection(int parseOrder,String shortName,AtsAct act){
		AtsSection section = new AtsSection();
		section.setBillNumber(act.getBillNumber());
		section.setCaption(act.getBillNumber());
		section.setContent(Global.getHtmlPath(act.getState())+act.getBillNumber()+"("+parseOrder+").html");
		section.setEff(act.getEffectiveDate());
		section.setParseOrder(parseOrder);
		section.setPid(act.getId());
		section.setRemarks("Add by editor");
		section.setShortName(shortName+act.getBillNumber());
		section.setUpdateType(1);
		section.setEditor(UserUtils.getUser().getName());
		section.setIsAdd(1);
		save(section);
		return section;
	}

	@Transactional(readOnly = false)
	public boolean modifyKeyInforBatch(AtsSection section){
		try {
			super.dao.modifyKeyInforBatch(section);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	@Transactional(readOnly = false)
	public void repealBach(String value,AtsAct act,String sid,String shortName,int type){
		String[] captions = value.split(",|and");
		for(int i=0;i<captions.length;i++){
			int parseOrder = findMaxParseOrder(act.getId());
			String caption = captions[i];
			caption = RegexUtil.replace("§", "&#167;", captions[i]);
			AtsSection section = new AtsSection();
			section.setBillNumber(act.getBillNumber());
			section.setCaption(caption);
			section.setContent(Global.getHtmlPath(act.getState())+act.getBillNumber()+"("+parseOrder+").html");
			section.setEff(act.getEffectiveDate());
			section.setParseOrder(parseOrder);
			section.setPid(sid);
			section.setRemarks("");
			section.setShortName(shortName+RegexUtil.replace("Section ", "", caption).trim());
			section.setUpdateType(type);
			section.setEditor(UserUtils.getUser().getName());
			section.setIsAdd(1);
			section.setStatus(1);
			section.setIsDel(1);
			save(section);
			
			AtsTree parent = new AtsTree();
			parent.setEditor(section.getEditor());
			parent.setFid(sid);
			parent.setIsParent("1");
			parent = atsTreeService.get(parent);
			AtsTree tree = new AtsTree(parent.getId(),caption,"0","0","showEditor","0",section.getId(),"0",UserUtils.getUser().getName(),"9");
			atsTreeService.save(tree);
		}
	}

	public int findUnsubmitCount(String id){
		return super.dao.findUnsubmitCount(id);
	}
	
	public List<AtsSection> findCompareSections(AtsSection section){
		return super.dao.findCompareSections(section);
	}

	public AtsSection findCompareSection(String pid, String shortName, String editor){
		return super.dao.findCompareSection(pid,shortName,editor);
	}
	
	public boolean hasUnsubmitSection(String pid){
		return super.dao.getUnsubmitSectionsCount(pid, UserUtils.getUser().getName())==0?false:true;
	}
	
	public boolean hasOtherUnsubmitSection(String pid){
		return super.dao.getOthersUnsubmitSectionCount(pid, UserUtils.getUser().getName())==0?false:true;
	}
	
	
	/**
	 * 执行section提交
	 * @param section
	 */
	@Transactional(readOnly = false)
	public boolean doSubmit(AtsSection section, AtsTree tree){
		boolean flag = false;
		section.setStatus(2);
		save(section);
		if(!hasUnsubmitSection(section.getPid())){	// 是否有未提交的section
			flag = true;
			AtsSign sign = new AtsSign();
			sign.setId(section.getPid());
			sign = atsSignService.get(sign);
			AtsSign other = atsSignService.getOthers(sign);
			if(other.getStatus().equals("3")||other.getStatus().equals("4")){
				sign.setStatus("4");
				other.setStatus("4");
			}else{
				sign.setStatus("3");
			}
			atsSignService.save(sign);
			atsSignService.save(other);
			//隐藏tree
			AtsTree parentTree = new AtsTree();
			parentTree.setId(tree.getPid());
			parentTree = atsTreeService.get(parentTree);
			parentTree.setIsHidden("1");
			atsTreeService.save(parentTree);
		}
		tree.setStatus("2");
		atsTreeService.save(tree);
		return flag;
	}
	
}