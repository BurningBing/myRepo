/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

/**
 * taskDAO接口
 * @author Chad
 * @version 2016-03-09
 */
@MyBatisDao
public interface AtsTaskDao extends CrudDao<AtsTask> {
	public List<String> findStates();
}