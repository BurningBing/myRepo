/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.dao;

import com.thinkgem.jeesite.common.persistence.CrudDao;
import com.thinkgem.jeesite.common.persistence.annotation.MyBatisDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsSign;

/**
 * 签名DAO接口
 * @author Chad
 * @version 2016-03-20
 */
@MyBatisDao
public interface AtsSignDao extends CrudDao<AtsSign> {
	public int getSignedCount(String editor);
	public AtsSign getOthers(AtsSign sign);
	

}