/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.dao;

import java.util.List;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsSection;

/**
 * sectionDAO接口
 * @author Chad
 * @version 2016-03-24
 */
@MyBatisDao
public interface AtsSectionDao extends CrudDao<AtsSection> {
	public Integer findMaxParseOrder(String pid);
	public void modifyKeyInforBatch(AtsSection section);
	public int findUnsubmitCount(String id);
	public List<AtsSection> findCompareSections(AtsSection section);
	public AtsSection findCompareSection(String pid, String shortName, String editor);
	public int getUnsubmitSectionsCount(String pid, String user);
	public int getOthersUnsubmitSectionCount(String pid, String user);

}