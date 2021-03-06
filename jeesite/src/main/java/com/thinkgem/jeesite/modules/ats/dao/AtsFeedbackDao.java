/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.dao;

import java.util.List;
import java.util.Map;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsFeedback;

/**
 * feedbackDAO接口
 * @author Chad
 * @version 2016-04-11
 */
@MyBatisDao
public interface AtsFeedbackDao extends CrudDao<AtsFeedback> {
	public List<Map<String, String>> getDataSync(String editor);
}