/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.acttostatute.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.acttostatute.entity.NonCaseUsEdition;
import com.thinkgem.jeesite.modules.acttostatute.dao.NonCaseUsEditionDao;

/**
 * NonCaseUSEditionService
 * @author Sid
 * @version 2015-08-03
 */
@Service("nonCaseUsEditionService")
@Transactional(readOnly = true)
public class NonCaseUsEditionService extends CrudService<NonCaseUsEditionDao, NonCaseUsEdition> {
	@Autowired
	private NonCaseUsEditionDao nonCaseUsEditionDao;
	public NonCaseUsEdition get(String id) {
		NonCaseUsEdition edition = new NonCaseUsEdition();
		edition.setId(id);
		return super.get(edition);
	}
	
	public List<NonCaseUsEdition> findList(NonCaseUsEdition nonCaseUsEdition) {
		return super.findList(nonCaseUsEdition);
	}
	
	public Page<NonCaseUsEdition> findPage(Page<NonCaseUsEdition> page, NonCaseUsEdition nonCaseUsEdition) {
		return super.findPage(page, nonCaseUsEdition);
	}
	
	@Transactional(readOnly = false)
	public void save(NonCaseUsEdition nonCaseUsEdition) {
		super.save(nonCaseUsEdition);
	}
	
	@Transactional(readOnly = false)
	public void delete(NonCaseUsEdition nonCaseUsEdition) {
		super.delete(nonCaseUsEdition);
	}

	public void updateStatus() {
		nonCaseUsEditionDao.updateStatus();		
	}
	
}