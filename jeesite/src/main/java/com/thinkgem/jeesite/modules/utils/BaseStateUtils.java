package com.thinkgem.jeesite.modules.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.DownloadUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.SpringBeanUtils;
import com.thinkgem.jeesite.modules.ats.service.AtsActService;
import com.thinkgem.jeesite.modules.ats.service.AtsSectionService;
import com.thinkgem.jeesite.modules.ats.service.AtsSignService;
import com.thinkgem.jeesite.modules.ats.service.AtsTreeService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

public class BaseStateUtils {
	protected static AtsActService atsActService = (AtsActService) SpringBeanUtils.getBean("atsActService");
	protected static AtsSignService atsSignService = (AtsSignService) SpringBeanUtils.getBean("atsSignService");
	protected static AtsSectionService atsSectionService = (AtsSectionService) SpringBeanUtils.getBean("atsSectionService");
	protected static AtsTreeService atsTreeService = (AtsTreeService) SpringBeanUtils.getBean("atsTreeService");
	protected static List<String> allActs;
	protected static String path;
	protected static String day;
	protected static String userName;
	protected static int downloadCount;
	
	public static void init(String state){
		allActs = atsActService.findDownloadedActs(state);
		path = DownloadUtils.getAtsDownloadPath(state);
		day = DateUtils.getDate("yyyyMMdd");
		userName = UserUtils.getUser().getName();
		downloadCount = 0;
	}
	
	public static String getStateTreeId(String state){
		return atsTreeService.getStateTreeId(state);
	}
	
	public static String readFile(File file){
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
