package com.thinkgem.jeesite.demo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;

public class Arizona {
	public static void main(String[] args) throws IOException {
		File file = new File("C:/ATS/Download/Arizona/20161015/HB2074.html");
		String html = FileUtils.readFileToString(file, "utf-8");
		html = formatHtml(html);
		List<String> list = RegexUtil.matchAllList("<p>(Sec\\.|Section)[\\s\\S]*?(?=<p>(Sec\\.|Section)| </div> )", html);
		//eff
		String eff = "";
		String temp = list.get(list.size()-1);
		temp = RegexUtil.match("(?<=<span style=\"color:purple\">).*?(?=</span>)", temp);
		if(temp.contains("Effective")){
			eff = "**";
		}else if(temp.contains("Emergency")){
			eff = "+++";
		}else{
			eff = "08/06/2016";
		}
		for(String str:list){
			//update
			int updateType = 2;
			temp = RegexUtil.match("<p>.*?</p>", str);
			if(temp.contains("add")){
				updateType = 1;
			}else if(temp.contains("is amended to read")||temp.contains("Heading change")){
				updateType = 2;
			}else{
				updateType = 3;
			}
			String caption = RegexUtil.match("(?<=<span style=\"color:green\">).*?(?=</span>)", str);
			String description = RegexUtil.match("(?<=<span style=\"color:purple\">).*?(?=</span>)", str);
			if(!caption.isEmpty()){
				str = RegexUtil.replaceFirst("<p>[\\s\\S]*?</p>", "", str);
				str = RegexUtil.replaceFirst("<p>[\\s\\S]*?</p>", "", str);
			}else{
				caption ="bill number";
			}
			str = RegexUtil.replace("</?span[^>]*?>", "", str);
			String shortName = "ARS "+caption;
			System.out.println("Caption:"+ caption);
			System.out.println("Description:"+ description);
			System.out.println("ShortName:"+ shortName);
			System.out.println("EffectiveDate:"+ eff);
			System.out.println("UpdateType:"+updateType);
			System.out.println("**************************");
			System.out.println(str.trim());
			System.out.println();
			System.out.println();
			System.out.println("-------------------------------------------------------------------------------------------------------------------");
		
		}
	}
	
	public static String formatHtml(String html){
		html = RegexUtil.replace("<p[^>]*?>", "<p>", html);
		html = RegexUtil.replace("<span style=\"display:none\">[\\s\\S]*?</span>", "", html);
		html = RegexUtil.replace("<span style=\"color:blue\">([\\s\\S]*?)</span>", "<font color=\"#f00\"><u>$1</u></font>", html);
		html = RegexUtil.replace("<span style=\"color:red\">([\\s\\S]*?)</span>", "<font color=\"#f00\"><strike>$1</strike></font>", html);
		return html;
	}
}
