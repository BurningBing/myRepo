/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;
import com.thinkgem.jeesite.modules.ats.dao.AtsTaskDao;

/**
 * taskService
 * @author Chad
 * @version 2016-03-09
 */
@Service
@Transactional(readOnly = true)
public class AtsTaskService extends CrudService<AtsTaskDao, AtsTask> {

	public AtsTask get(String id) {
		AtsTask task = new AtsTask();
		task.setId(id);
		return super.get(task);
	}
	
	public AtsTask getByState(String state){
		AtsTask t = new AtsTask();
		t.setState(state);
		return super.get(t);
	}
	
	
	public List<AtsTask> findList(AtsTask atsTask) {
		return super.findList(atsTask);
	}
	
	public Page<AtsTask> findPage(Page<AtsTask> page, AtsTask atsTask) {
		return super.findPage(page, atsTask);
	}
	
	@Transactional(readOnly = false)
	public void save(AtsTask atsTask) {
		super.save(atsTask);
	}
	
	@Transactional(readOnly = false)
	public void delete(AtsTask atsTask) {
		super.delete(atsTask);
	}
	
	public List<String> findStates(){
		return super.dao.findStates();
	}
	
}