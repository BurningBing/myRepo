/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.common.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONException;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;
import org.springframework.core.io.DefaultResourceLoader;

import com.ckfinder.connector.ServletContextFactory;
import com.google.common.collect.Maps;
import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.PropertiesLoader;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

import freemarker.core.ReturnInstruction.Return;

/**
 * 全局配置类
 * @author ThinkGem
 * @version 2014-06-25
 */
public class Global {
	
	public static final String  PRODUCT_URL="https://services.fastcase.com/soap1.1/ResearchServices.svc";
	public static final String  ACCOUNT_CONTEXT = "e8KA2cseJv%2bye9gsjlF2SHFd5f9QoZwL4O8baxo495vudqbSYYvQ%2bNeR%2befSn9or";
	
	/**
	 * 当前对象实例
	 */
	private static Global global = new Global();
	
	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = Maps.newHashMap();
	
	/**
	 * 属性文件加载对象
	 */
	private static PropertiesLoader loader = new PropertiesLoader("jeesite.properties");

	/**
	 * 显示/隐藏
	 */
	public static final String SHOW = "1";
	public static final String HIDE = "0";

	/**
	 * 是/否
	 */
	public static final String YES = "1";
	public static final String NO = "0";
	
	/**
	 * 对/错
	 */
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	
	/**
	 * 上传文件基础虚拟路径
	 */
	public static final String USERFILES_BASE_URL = "/userfiles/";
	
	/**
	 * 获取当前对象实例
	 */
	public static Global getInstance() {
		return global;
	}
	
	/**
	 * 获取配置
	 * @see ${fns:getConfig('adminPath')}
	 */
	public static String getConfig(String key) {
		String value = map.get(key);
		if (value == null){
			value = loader.getProperty(key);
			map.put(key, value != null ? value : StringUtils.EMPTY);
		}
		return value;
	}
	
	/**
	 * 获取管理端根路径
	 */
	public static String getAdminPath() {
		return getConfig("adminPath");
	}
	
	/**
	 * 获取前端根路径
	 */
	public static String getFrontPath() {
		return getConfig("frontPath");
	}
	
	/**
	 * 获取URL后缀
	 */
	public static String getUrlSuffix() {
		return getConfig("urlSuffix");
	}
	
	/**
	 * 是否是演示模式，演示模式下不能修改用户、角色、密码、菜单、授权
	 */
	public static Boolean isDemoMode() {
		String dm = getConfig("demoMode");
		return "true".equals(dm) || "1".equals(dm);
	}
	
	/**
	 * 在修改系统用户和角色时是否同步到Activiti
	 */
	public static Boolean isSynActivitiIndetity() {
		String dm = getConfig("activiti.isSynActivitiIndetity");
		return "true".equals(dm) || "1".equals(dm);
	}
    
	/**
	 * 页面获取常量
	 * @see ${fns:getConst('YES')}
	 */
	public static Object getConst(String field) {
		try {
			return Global.class.getField(field).get(null);
		} catch (Exception e) {
			// 异常代表无配置，这里什么也不做
		}
		return null;
	}

	/**
	 * 获取上传文件的根目录
	 * @return
	 */
	public static String getUserfilesBaseDir() {
		String dir = getConfig("userfiles.basedir");
		if (StringUtils.isBlank(dir)){
			try {
				dir = ServletContextFactory.getServletContext().getRealPath("/");
			} catch (Exception e) {
				return "";
			}
		}
		if(!dir.endsWith("/")) {
			dir += "/";
		}
//		System.out.println("userfiles.basedir: " + dir);
		return dir;
	}
	
    /**
     * 获取工程路径
     * @return
     */
    public static String getProjectPath(){
    	// 如果配置了工程路径，则直接返回，否则自动获取。
		String projectPath = Global.getConfig("projectPath");
		if (StringUtils.isNotBlank(projectPath)){
			return projectPath;
		}
		try {
			File file = new DefaultResourceLoader().getResource("").getFile();
			if (file != null){
				while(true){
					File f = new File(file.getPath() + File.separator + "src" + File.separator + "main");
					if (f == null || f.exists()){
						break;
					}
					if (file.getParentFile() != null){
						file = file.getParentFile();
					}else{
						break;
					}
				}
				projectPath = file.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return projectPath;
    }
    
    public static String STATE_PACKAGE = "com.thinkgem.jeesite.modules.ats.utils";
    
    public static String getHtmlPath(String state){
    	return "C:\\ATS\\HTML\\"+DateUtils.getDate("yyyMMdd")+"\\"+state+"\\"+UserUtils.getUser().getName()+"\\"; 
    }

    public static String getCaliforniaShortName(String code){
		Map<String,String> params = new HashMap<String,String>();
		params.put("Business and Professions Code","CA Bus. & Prof. Sec. ");
		params.put("Civil Code","CA Civ. Sec. ");
		params.put("Code of Civil Procedure","CA Civ. Proc. Sec. ");
		params.put("Commercial Code", "CA Com. Sec. ");
		params.put("Corporations Code", "CA Corp. Sec. ");
		params.put("Education Code", "CA Educ. Sec. ");
		params.put("Elections Code", "CA Elec. Sec. ");
		params.put("Evidence Code", "CA Evid. Sec. ");
		params.put("Family Code", "CA Fam. Sec. ");
		params.put("Financial Code", "CA Fin. Sec. ");
		params.put("Fish and Game Code","CA Fish & Game Sec. ");
		params.put("Food and Agricultural Code","CA Food & Agric. Sec. ");
		params.put("Government Code","CA Govt Sec. ");
		params.put("Harbors and Navigation Code","CA Harb. & Nav. Sec. ");
		params.put("Health and Safety Code","CA Health & Safety Sec. ");
		params.put("Insurance Code","CA Ins. Sec. ");
		params.put("Labor Code","CA Lab. Sec. ");
		params.put("Military and Veterans Code","CA Mil. & Vet. Sec. ");
		params.put("Penal Code","CA Penal Sec. ");
		params.put("Probate Code","CA Prob. Sec. ");
		params.put("Public Contract Code","CA Pub. Cont Sec. ");
		params.put("Public Resources Code","CA Pub. Res. Sec. ");
		params.put("Public Utilities Code","CA Pub. Util. Sec. ");
		params.put("Revenue and Taxation Code","CA Rev. & Tax. Sec. ");
		params.put("Streets and Highways Code","CA Sts. & High. Sec. ");
		params.put("Unemployment Insurance Code","CA Unemp. Ins. Sec. ");
		params.put("Vehicle Code","CA Veh. Sec. ");
		params.put("Water Code","CA Water Sec. ");
		params.put("Welfare and Institutions Code","CA Welf. & Inst. Sec. ");
		return params.get(code);
	}
    public static Map<Integer, String> getMap()
	{
		Map<Integer,String> map=new HashMap<Integer, String>();
		map.put(7777,"Missouri Revised Statutes (2015 Edition)");
		map.put(8862,"Illinois Compiled Statutes (2016 Edition)");
		map.put(7566,"Virginia Statutes (2015 Edition)");
		map.put(5673,"Wisconsin Statutes (2015 Edition)");
		map.put(7546,"Arizona Revised Statutes (2015 Edition)");
		map.put(8275,"Iowa Statutes (2016 Edition)");
		map.put(5536,"Ohio Revised Code (2014 Edition)");
		map.put(8400,"California Code (2016 Edition)");
		map.put(5518,"Louisiana Revised Statutes (2014 Edition)");
		map.put(5221,"Minnesota Statutes (2014 Edition)");
		map.put(5697,"Michigan Compiled Laws (2015 Edition)");
		map.put(7677,"New Mexico Statutes (2015 Edition)");
		map.put(9032,"North Carolina General Statutes (2015 Edition)");
		map.put(5236,"Maine Revised Statutes (2014 Edition)");
		map.put(8255,"South Carolina Code of Laws (2016 Edition)");
		map.put(8716,"South Dakota Codified Laws (2016 Edition)");
		map.put(5274,"Kansas Statutes (2014 Edition)");
		map.put(5773,"Nevada Revised Statutes (2014 Edition)");
		map.put(5617,"Arkansas Code (2014 Edition)");
		map.put(5457,"Alaska Statutes (2014 Edition)");
		map.put(7672,"Kentucky Revised Statutes (2015 Edition)");
		map.put(7600,"Oklahoma Statutes (2015 Edition)");
		map.put(5239,"Montana Code (2014 Edition)");
		map.put(8067,"Rhode Island General Laws (2015 Edition)");
		map.put(5165,"The General Laws of Massachusetts (2014 Edition)");
		map.put(5788,"Texas Statutes (2014 Edition)");
		map.put(8401,"Hawaii Revised Statutes (2016 Edition)");
		map.put(5659,"Minnesota Session Laws (2015 Edition)");
		map.put(7524,"Idaho Statutes (2015 Edition)");
		map.put(7565,"Florida Statutes (2015 Edition)");
		map.put(5687,"General Statutes of Connecticut (2015 Edition)");
		map.put(8286,"Louisiana Revised Statutes (2015 Edition)");
		map.put(5478,"Louisiana Constitution Ancillaries (2014 Edition)");
		map.put(5464,"Louisiana Childrens Code (2014 Edition)");
		map.put(5468,"Louisiana Civil Code (2014 Edition)");
		map.put(5525,"Louisiana Code of Civil Procedure (2014 Edition)");
		map.put(5526,"Louisiana Code of Criminal Procedure (2014 Edition)");
		map.put(5467,"Louisiana Code of Evidence (2014 Edition)");
		map.put(8281,"Revised Code of Washington (2015 Edition)");
		map.put(8781,"Oregon Revised Statutes (2015 Edition)");
		map.put(7683,"West Virginia Code (2015 Edition)");
		map.put(7673,"Minnesota Statutes (2015 Edition)");
		return map;
	}
	
	public static Map<String,String> getQA2StateToIdMap(){
		Map<String,String> map=new HashMap<String,String>();
        map.put("RhodeIsland","5476");
        return map;
	}
	
	public static Map<String, String> getStateToIdMap()
	{
		Map<String,String> map=new HashMap<String,String>();
        map.put("Missouri","7777");
		map.put("Illinois","8862");
		map.put("Virginia","7566");
		map.put("Arizona","7546");
		map.put("Iowa","8275");
		map.put("Ohio","5536");
		map.put("California","8400");
		map.put("Louisiana","8286");
		map.put("Minnesota","5221");
		map.put("New Mexico","7677");
		map.put("North Carolina","9032");
		map.put("SouthCarolina","8255");
		map.put("SouthDakota","8716");
		map.put("Alaska","5457");
		map.put("Kentucky","7672");
		map.put("Oklahoma","7600");
		map.put("Montana","5239");
		map.put("RhodeIsland","8067");
		map.put("Texas","5788");
		map.put("Hawaii","8401");
		map.put("Idaho","7524");
		map.put("Nevada", "5773");
		map.put("Connecticut", "5687");
		map.put("Florida", "7565");
		map.put("Oregon", "8781");
		map.put("Washington", "8281");
		map.put("West Virginia", "7683");
		map.put("Minnesota", "7673");
		return map;
	}
	
	public static String getExportType(String type){
		if(type.equals("Modify")){
			return "UpdateLibraryIndexId";
		}else if(type.equals("New")){
			return "ParentLibraryIndexId";
		}else if(type.equals("Repeal")){
			return "UpdateLibraryIndexId";
		}
		return null;
	}
	
	public static String getStateUtils(String state){
		return "com.thinkgem.jeesite.modules.utils."+state+"Utils";
	}
	
	public static int getWorkMode(String state){
		String path = Global.class.getClassLoader().getResource("states.json").getPath();
		try {
			JSONObject json = new JSONObject(FileUtils.readFileToString(new File(path)));
			JSONObject stateJson = json.getJSONObject(state);
			return stateJson.getInt("workMode");
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	public static void main(String[] args) {
		getWorkMode("Arizona");
	}
	
	public static String getTypeByState(String state){
		List<String> html = new ArrayList<String>();
		html.add("Arizona");
		html.add("California");
		html.add("Hawaii");
		html.add("Illinois");
		List<String> pdf = new ArrayList<String>();
		pdf.add("Florida");
		pdf.add("Idaho");
		if(html.contains(state)){
			return "html";
		}else if(pdf.contains(state)){
			return "pdf";
		}else{
			return "";
		}
		
		
		
	}
	
}
