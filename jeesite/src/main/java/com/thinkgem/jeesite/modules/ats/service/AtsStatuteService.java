/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.service;

import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thinkgem.jeesite.common.mapper.JsonMapper;
import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.service.CrudService;
import com.thinkgem.jeesite.modules.ats.dao.AtsStatuteDao;
import com.thinkgem.jeesite.modules.ats.entity.AtsStatute;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

/**
 * statuteService
 * @author Chad
 * @version 2016-03-29
 */
@Service
@Transactional(readOnly = true)
public class AtsStatuteService extends CrudService<AtsStatuteDao, AtsStatute> {

	public AtsStatute get(String id) {
		return super.get(id);
	}
	
	public List<AtsStatute> findList(AtsStatute atsStatute) {
		return super.findList(atsStatute);
	}
	
	public Page<AtsStatute> findPage(Page<AtsStatute> page, AtsStatute atsStatute) {
		return super.findPage(page, atsStatute);
	}
	
	@Transactional(readOnly = false)
	public void save(AtsStatute atsStatute) {
		super.save(atsStatute);
	}
	
	@Transactional(readOnly = false)
	public void delete(AtsStatute atsStatute) {
		super.delete(atsStatute);
	}
	
	public List<AtsStatute> findStatuteByState(String state){
		return super.dao.findStatuteByState(state); 
	}
	
	public JSONArray getStatuteTreeRoot(List<AtsTask> tasks){
		JSONArray array = new JSONArray();
		for(AtsTask task:tasks){
			JSONObject stateJson = new JSONObject();
			String state = task.getState();
			stateJson.put("name", state);
			List<AtsStatute> statutes = findStatuteByState(state);
			JSONArray stateArray = new JSONArray();
			for(AtsStatute statute:statutes){
				JSONObject statuteJson = new JSONObject(JsonMapper.toJsonString(statute));
				statuteJson.put("id", "");
				statuteJson.put("name", statute.getLibraryEdition());
				statuteJson.put("isParent", true);
				stateArray.put(statuteJson);
			}
			stateJson.put("children", stateArray);
			array.put(stateJson);
		}
		return array;
	}
}