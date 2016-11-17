/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.acttostatute.dao;

import java.util.List;
import java.util.Map;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.acttostatute.entity.NonCaseStatuteSection;

/**
 * 记录SectionDAO接口
 * @author Sid Cao
 * @version 2015-04-13
 */
@MyBatisDao
public interface NonCaseStatuteSectionDao extends CrudDao<NonCaseStatuteSection> {

	List<NonCaseStatuteSection> getRepetitionSection(NonCaseStatuteSection nonCaseStatuteSection);

	List<NonCaseStatuteSection> countSection(NonCaseStatuteSection nonCaseStatuteSection);

	void updatePassed(NonCaseStatuteSection nonCaseStatuteSection);

	List<Map<String, Object>> findByState(Map<String, Object> search);

	List<Map<String, Object>> findActs(Map<String, Object> search);
	
	List<Map<String, Object>> findSection(Map<String, Object> search);
}