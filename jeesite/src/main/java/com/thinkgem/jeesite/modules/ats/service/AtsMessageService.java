/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.ats.entity.AtsMessage;
import com.thinkgem.jeesite.modules.ats.dao.AtsMessageDao;

/**
 * messageService
 * @author Chad
 * @version 2016-03-09
 */
@Service
@Transactional(readOnly = true)
public class AtsMessageService extends CrudService<AtsMessageDao, AtsMessage> {

	public AtsMessage get(String id) {
		return super.get(id);
	}
	
	public List<AtsMessage> findList(AtsMessage atsMessage) {
		return super.findList(atsMessage);
	}
	
	public Page<AtsMessage> findPage(Page<AtsMessage> page, AtsMessage atsMessage) {
		return super.findPage(page, atsMessage);
	}
	
	@Transactional(readOnly = false)
	public void save(AtsMessage atsMessage) {
		super.save(atsMessage);
	}
	
	@Transactional(readOnly = false)
	public void delete(AtsMessage atsMessage) {
		super.delete(atsMessage);
	}
	
}