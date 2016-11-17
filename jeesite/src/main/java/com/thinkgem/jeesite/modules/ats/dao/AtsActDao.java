/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;

/**
 * actDAO接口
 * @author Chad
 * @version 2016-03-09
 */
@MyBatisDao
public interface AtsActDao extends CrudDao<AtsAct> {
	List<String> findDownloadedActs(String state);
	int findCount(String state);
	List<String> findUnsignedStates();
	List<String> findDateByState(String state);
	List<AtsAct> findSignActs(String state,int count,String editor);
	List<AtsAct> findActsByEditor(String editor);
}