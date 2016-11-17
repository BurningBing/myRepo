/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.ats.entity.AtsFeedback;
import com.thinkgem.jeesite.modules.ats.dao.AtsFeedbackDao;

/**
 * feedbackService
 * @author Chad
 * @version 2016-04-11
 */
@Service
@Transactional(readOnly = true)
public class AtsFeedbackService extends CrudService<AtsFeedbackDao, AtsFeedback> {

	public AtsFeedback get(String id) {
		return super.get(id);
	}
	
	public List<AtsFeedback> findList(AtsFeedback atsFeedback) {
		return super.findList(atsFeedback);
	}
	
	public Page<AtsFeedback> findPage(Page<AtsFeedback> page, AtsFeedback atsFeedback) {
		return super.findPage(page, atsFeedback);
	}
	
	@Transactional(readOnly = false)
	public void save(AtsFeedback atsFeedback) {
		super.save(atsFeedback);
	}
	
	@Transactional(readOnly = false)
	public void delete(AtsFeedback atsFeedback) {
		super.delete(atsFeedback);
	}
	
	public List<Map<String, String>> getDataSync(String editor){
		return super.dao.getDataSync(editor);
	}
	
}