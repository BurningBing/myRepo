/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.acttostatute.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.acttostatute.entity.NonCaseUsEdition;

/**
 * NonCaseUSEditionDAO接口
 * @author Sid
 * @version 2015-08-03
 */
@MyBatisDao
public interface NonCaseUsEditionDao extends CrudDao<NonCaseUsEdition> {
	void updateStatus();
}