package com.thinkgem.jeesite.modules.tool;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;

import com.fastcase.services._2009._03._06.ResearchServicesStub.OutlineViewListItem;
import com.thinkgem.jeesite.common.utils.EhCacheUtils;
import com.thinkgem.jeesite.common.utils.SpringBeanUtils;
import com.thinkgem.jeesite.common.webservice.WBNonCaseUtils;
import com.thinkgem.jeesite.modules.ats.service.AtsTaskService;

public class AtsTool {

	private static AtsTaskService atsTaskService = (AtsTaskService) SpringBeanUtils.getBean("atsTaskService");

	/**
	 * 获取州列表
	 * @author Chad
	 * @return List
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getStateList(){ 
		List<String> states = new ArrayList<String>();
		if(EhCacheUtils.get("states")!=null){
			states = (List<String>) EhCacheUtils.get("states");
		}else{
			states = atsTaskService.findStates(); 
			EhCacheUtils.put("states", states);
		}
		return states;
	}
	
	public static JSONArray getOutlineView(String nodeId, String editionId){
		JSONArray array = new JSONArray();
		try {
			OutlineViewListItem[] items = WBNonCaseUtils.getStatuteTree(nodeId, Integer.parseInt(editionId));
			for(OutlineViewListItem item: items){
				JSONObject json = new JSONObject();
				json.put("id", item.getNodeID());
				json.put("name", item.getNodeDescription());
				json.put("editionId", editionId);
				json.put("isParent", item.getHasChildNodes());
				array.put(json);
			}
		} catch (Exception e) {
			return null;
		}
		return array;
	}
	
	

	
	
	
}
