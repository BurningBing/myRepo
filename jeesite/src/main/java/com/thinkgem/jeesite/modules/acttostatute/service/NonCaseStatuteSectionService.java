/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.acttostatute.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.acttostatute.entity.NonCaseStatuteSection;
import com.thinkgem.jeesite.modules.acttostatute.dao.NonCaseStatuteSectionDao;

/**
 * 记录SectionService
 * @author Sid Cao
 * @version 2015-04-13
 */
@Service
@Transactional(readOnly = true)
public class NonCaseStatuteSectionService extends CrudService<NonCaseStatuteSectionDao, NonCaseStatuteSection> {
	@Autowired
	private NonCaseStatuteSectionDao nonCaseStatuteSectionDao;
	public NonCaseStatuteSection get(String id) {
		return super.get(id);
	}
	
	public List<NonCaseStatuteSection> findList(NonCaseStatuteSection nonCaseStatuteSection) {
		return super.findList(nonCaseStatuteSection);
	}
	
	public Page<NonCaseStatuteSection> findPage(Page<NonCaseStatuteSection> page, NonCaseStatuteSection nonCaseStatuteSection) {
		return super.findPage(page, nonCaseStatuteSection);
	}
	
	@Transactional(readOnly = false)
	public void save(NonCaseStatuteSection nonCaseStatuteSection) {
		super.save(nonCaseStatuteSection);
	}
	
	@Transactional(readOnly = false)
	public void delete(NonCaseStatuteSection nonCaseStatuteSection) {
		super.delete(nonCaseStatuteSection);
	}
	
	public List<NonCaseStatuteSection> getRepetitionSection(NonCaseStatuteSection nonCaseStatuteSection) {
		return nonCaseStatuteSectionDao.getRepetitionSection(nonCaseStatuteSection);
	}
	public List<NonCaseStatuteSection> countSection(NonCaseStatuteSection nonCaseStatuteSection) {
		return nonCaseStatuteSectionDao.countSection(nonCaseStatuteSection);
	}
	
	public Page<NonCaseStatuteSection> countSectionPage(Page<NonCaseStatuteSection> page, NonCaseStatuteSection nonCaseStatuteSection) {
		page.setPageSize(1000);
		nonCaseStatuteSection.setPage(page);
		page.setList(nonCaseStatuteSectionDao.countSection(nonCaseStatuteSection));
		return page;
	}
	
	@Transactional(readOnly = false)
	public void updatePassed(NonCaseStatuteSection nonCaseStatuteSection) {
		nonCaseStatuteSectionDao.updatePassed(nonCaseStatuteSection);
	}

	public List<Map<String, Object>> findByState(Map<String, Object> search) {
		// TODO Auto-generated method stub
		return nonCaseStatuteSectionDao.findByState(search);
	}
	
	public List<Map<String, Object>> findActs(Map<String, Object> search) {
		// TODO Auto-generated method stub
		return nonCaseStatuteSectionDao.findActs(search);
	}
	
	public List<Map<String, Object>> findSection(Map<String, Object> search) {
		// TODO Auto-generated method stub
		return nonCaseStatuteSectionDao.findSection(search);
	}
}