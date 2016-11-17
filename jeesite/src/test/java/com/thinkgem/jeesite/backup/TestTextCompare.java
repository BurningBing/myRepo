package com.thinkgem.jeesite.backup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;

public class TestTextCompare {
	public static void main(String[] args) throws IOException {
		File file1 = new File("e:\\test\\1.html");
		File file2 = new File("e:\\test\\2.html");
		System.out.println("开始读取文件...");
		String html1 = formatHtml(FileUtils.readFileToString(file1).trim()) ;
		String html2 = formatHtml(FileUtils.readFileToString(file2).trim()) ;
		// 将文本以段落分割，转化为List。
		List<String> list1 = Arrays.asList(html1.split("\n"));
		List<String> list2 = Arrays.asList(html2.split("\n"));
		//进行对比
		compare(list1, list2);
	}
	
	
	
	public static void compare(List<String> list1, List<String> list2){
		if(list1.size()!=list2.size()){
			System.out.println("文本段落数量不一致");
		}else{
			for(int i=0;i<list1.size();i++){
				compare(list1.get(i),list2.get(i));
			}
		}
	}
	
	public static void compare(String p1, String p2) {
		if(p1.equals(p2)){
			
		}
	}
	
	public static String formatHtml(String html) {
		html = RegexUtil.replace("^\\s*", "", html, RegexUtil.MULTILINE);
		return html;
	}
	
	
}
