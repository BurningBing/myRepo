/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.ats.dao.AtsSignDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 签名Service
 * @author Chad
 * @version 2016-03-20
 */
@Service
@Transactional(readOnly = true)
public class AtsSignService extends CrudService<AtsSignDao, AtsSign> {

	public AtsSign get(String id) {
		AtsSign sign = new AtsSign();
		sign.setId(id);
		return super.get(sign);
	}
	
	public List<AtsSign> findList(AtsSign atsSign) {
		return super.findList(atsSign);
	}
	
	public Page<AtsSign> findPage(Page<AtsSign> page, AtsSign atsSign) {
		return super.findPage(page, atsSign);
	}
	
	@Transactional(readOnly = false)
	public void save(AtsSign atsSign) {
		super.save(atsSign);
	}
	
	@Transactional(readOnly = false)
	public void delete(AtsSign atsSign) {
		super.delete(atsSign);
	}
	
	public int getSignedCount(){
		return super.dao.getSignedCount(UserUtils.getUser().getName());		 
	}
	
	public AtsSign getOthers(AtsSign sign){
		return super.dao.getOthers(sign);
	}
}