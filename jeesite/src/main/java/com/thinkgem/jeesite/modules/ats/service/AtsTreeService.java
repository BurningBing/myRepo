/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.modules.ats.dao.AtsTreeDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsTree;

/**
 * actService
 * @author Chad
 * @version 2016-03-09
 */
@Service
@Transactional(readOnly = true)
public class AtsTreeService extends CrudService<AtsTreeDao, AtsTree> {
	
	public AtsTree get(String id) {
		AtsTree tree = new AtsTree();
		tree.setId(id);
		return super.get(tree);
	}
	
	public List<AtsTree> findList(AtsTree AtsTree) {
		return super.findList(AtsTree);
	}
	
	@Transactional(readOnly = false)
	public void save(AtsTree AtsTree) {
		super.save(AtsTree);
	}
	
	public String getStateTreeId(String state){
		AtsTree temp = new AtsTree("4",state,"1","1","","0","","1","","4");
		temp = getOrSave(temp);
		temp = new AtsTree(temp.getId(),DateUtils.getDate("yyyyMMdd"),"1","1","","0","","1","","4");
		temp.setEditor("");
		temp = getOrSave(temp);
		return temp.getId();
	}
	
	public AtsTree getOrSave(AtsTree tree){
		AtsTree temp = new AtsTree();
		temp.setName(tree.getName());
		temp.setPid(tree.getPid());
		temp.setEditor(tree.getEditor());
		temp = get(temp);
		if(temp!=null){
			return temp;
		}else{
			//添加数据，默认值
			save(tree);
			return tree;
		}
	}
	
}