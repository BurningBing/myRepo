package com.thinkgem.jeesite.common.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtil {
	
	/**
	 * 默认情况下，大小写不明感的匹配只适用于US-ASCII字符集。
	 * 这个标志能让表达式忽略大小写进行匹配。
	 * 要想对Unicode字符进行大小不明感的匹配，只要将UNICODE_CASE与这个标志合起来就行了
	 */
	public static final int CASE_INSENSITIVE = Pattern.CASE_INSENSITIVE;
	
	/**
	 * 在这种模式下，匹配时会忽略(正则表达式里的)空格字符(译者注：不是指表达式里的"\\s"，而是指表达式里的空格，tab，回车之类)。
	 * 注释从#开始，一直到这行结束。可以通过嵌入式的标志来启用Unix行模式。
	 */
	public static final int COMMENTS = Pattern.COMMENTS;
	
	/**
	 * 在这种模式下，表达式'.'可以匹配任意字符，包括表示一行的结束符。
	 * 默认情况下，表达式'.'不匹配行的结束符。
	 */
	public static final int DOTALL = Pattern.DOTALL;
	
	/**
	 * 在这种模式下，'^'和'$'分别匹配一行的开始和结束。
	 * 此外，'^'仍然匹配字符串的开始，'$'也匹配字符串的结束。
	 * 默认情况下，这两个表达式仅仅匹配字符串的开始和结束。
	 */
	public static final int MULTILINE = Pattern.MULTILINE;
	
	/**
	 * 在这个模式下，如果你还启用了CASE_INSENSITIVE标志，那么它会对Unicode字符进行大小写不明感的匹配。
	 * 默认情况下，大小写不敏感的匹配只适用于US-ASCII字符集。
	 */
	public static final int UNICODE_CASE = Pattern.UNICODE_CASE;
	
	/**
	 * 在这个模式下，只有'\n'才被认作一行的中止，并且与'.'，'^'，以及'$'进行匹配。
	 */
	public static final int UNIX_LINES = Pattern.UNIX_LINES;
	
	
	
	/**
	 * 查找替换字符串
	 * @param mode
	 *            正则语句
	 * @param repString
	 *            替换的内容
	 * @param text
	 *            需要替换的字符串
	 * @param flags 不指定，则使用默认模式  
	 * 			  
	 * 
	 */
	public static String replace(String mode, String repString, String text) {
		Pattern pattern = Pattern.compile(mode);
		Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, repString);
		}
		matcher.appendTail(sb);
		text = sb.toString();
		return text;
	}
	
	/**
	 * 查找替换字符串
	 * @param mode
	 *            正则语句
	 * @param repString
	 *            替换的内容
	 * @param text
	 *            需要替换的字符串
	 * @param flags  
	 * 			  模式
	 * 
	 */
	public static String replace(String mode, String repString, String text, int flags) {
		Pattern pattern = Pattern.compile(mode, flags);
		Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, repString);
		}
		matcher.appendTail(sb);
		text = sb.toString();
		return text;
	}
	
	
	public static String replaceByUpNumber(String mode, String text, int flags) {
		Pattern pattern = Pattern.compile(mode,flags);
		Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, upNumber(matcher.group()));
		}
		matcher.appendTail(sb);
		text = sb.toString();
		return text;
	}
	
	public static String replaceByUpSecond(String mode, String text, int flags) {
		Pattern pattern = Pattern.compile(mode,flags);
		Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, upSecond(matcher.group()));
		}
		matcher.appendTail(sb);
		text = sb.toString();
		return text;
	}
	
	public static String replaceByLowCase(String mode, String text, int flags) {
		Pattern pattern = Pattern.compile(mode,flags);
		Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, lowCase(matcher.group()));
		}
		matcher.appendTail(sb);
		text = sb.toString();
		return text;
	}
	
	/**
	 * 查找字符串
	 * @param mode
	 *            正则语句
	 * @param text
	 *            需要替换的字符串
	 * @param flags 
	 * 			  不指定，则使用默认模式  
	 */
	public static String match(String mode, String text){
		Pattern pattern = Pattern.compile(mode);
		Matcher matcher = pattern.matcher(text);
		if(matcher.find()){
			return matcher.group();
		}
		
		return "";
	}
	
	/**
	 * 查找全部匹配的字符串
	 * @param mode
	 *            正则语句
	 * @param text
	 *            需要替换的字符串
	 */
	public static String match(String mode, String text, int flags){
		Pattern pattern = Pattern.compile(mode, flags);
		Matcher matcher = pattern.matcher(text);
		if(matcher.find()){
			return matcher.group();
		}
		
		return "";
	}
	/**
	 * 查找全部匹配的字符串
	 * @param mode
	 *            正则语句
	 * @param text
	 *            需要替换的字符串
	 */
	public static String[] match(String mode, String text, int flags, boolean isArr){
		Pattern pattern = Pattern.compile(mode, flags);
		Matcher matcher = pattern.matcher(text);
		Map<Integer, String> matchMaps = new HashMap<Integer, String>();
		for(int i=0;i<=matcher.groupCount();i++){
			pattern = Pattern.compile(mode, flags);
			matcher = pattern.matcher(text);
			if(matcher.find()){
				matchMaps.put(i, matcher.group(i));
			}
		}
		String[] tempMaps = new String[matchMaps.size()];
		for(int i = 0;i< matchMaps.size() ;i++){
			tempMaps[i] = matchMaps.get(i);
		}
		
		return tempMaps;
	}
	
	public static Map<Integer, String[]> matchAll(String mode, String text){
		Pattern pattern = Pattern.compile(mode);
		Matcher matcher = pattern.matcher(text);
		Map<Integer, List<String>> matchMaps = new HashMap<Integer, List<String>>();
		Map<Integer, String[]> matchAllMaps = new HashMap<Integer, String[]>();
		List<String> matchAllList = new ArrayList<String>();
		while(matcher.find()){
			matchAllList.add(matcher.group());
		}
		matchMaps.put(0, matchAllList);
		for (int j = 0; j < matcher.groupCount(); j++) {
			pattern = Pattern.compile(mode);
			matcher = pattern.matcher(text);
			List<String> matchList = new ArrayList<String>();
			while(matcher.find()){
				matchList.add(matcher.group(j+1));
			}
			matchMaps.put(j+1, matchList);
		}
		
		if(matchMaps != null && matchMaps.size() >0){
			for (Integer key : matchMaps.keySet()) {
				 String[]matches = new String[ matchMaps.get(key).size()];
				 matchAllMaps.put(key, matchMaps.get(key).toArray(matches));
			}
		}
		
		return matchAllMaps;
	}
	
	public static Map<Integer, String[]> matchAll(String mode, String text, int flags){
		Pattern pattern = Pattern.compile(mode, flags);
		Matcher matcher = pattern.matcher(text);
		Map<Integer, List<String>> matchMaps = new HashMap<Integer, List<String>>();
		Map<Integer, String[]> matchAllMaps = new HashMap<Integer, String[]>();
		List<String> matchAllList = new ArrayList<String>();
		while(matcher.find()){
			matchAllList.add(matcher.group());
		}
		matchMaps.put(0, matchAllList);
		for (int j = 0; j < matcher.groupCount(); j++) {
			pattern = Pattern.compile(mode, flags);
			matcher = pattern.matcher(text);
			List<String> matchList = new ArrayList<String>();
			while(matcher.find()){
				matchList.add(matcher.group(j+1));
			}
			matchMaps.put(j+1, matchList);
		}
		
		if(matchMaps != null && matchMaps.size() >0){
			for (Integer key : matchMaps.keySet()) {
				 String[]matches = new String[ matchMaps.get(key).size()];
				 matchAllMaps.put(key, matchMaps.get(key).toArray(matches));
			}
		}
		
		return matchAllMaps;
	}
	
	/**
	 * 查找是否匹配到该位置
	 * @param mode
	 *            正则语句
	 */
	public boolean isMatchs(String mode, String text) {
		Pattern pattern = Pattern.compile(mode);
		String[] resultArr = pattern.split(text);
		if(resultArr.length > 1){
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 查找是否匹配到该位置
	 * @param mode
	 *            正则语句
	 * @param flags 模式
	 */
	public boolean isMatchs(String mode, String text, int flags) {
		Pattern pattern = Pattern.compile(mode, flags);
		String[] resultArr = pattern.split(text);
		if(resultArr.length > 1){
			return true;
		}else {
			return false;
		}
	}
	
	public static String upSecond(String str) {
		
		str = str.substring(0, str.length() - 1)+ str.substring(str.length() - 1, str.length()).toUpperCase();
		return str;
	}
	
	public static String upNumber(String match){
         return match.toUpperCase();
     }

	public static String lowCase(String match){
         return match.toLowerCase();
     }
     
	public static void main(String[] args) {
		System.out.println(RegexUtil.replace("(?<=</?)(\\w+)(?!\\w)","","<a><apan>a</apan>bc</a>"));
		
		RegexUtil.matchAll("<script\\b.*?></script>", "<script type=\"text/javascript\" src =\"'+rootPath+'common/msofficetool/script/yftoolutil.js\"></script><script type=\"text/javascript\" src=\"system/formpubmodule/function.js\"></script>", RegexUtil.CASE_INSENSITIVE);
	}
	/**
	 * @author ZhuXingang
	 * @time 2013-10-30下午04:09:33
	 * @function <p>是否存在要找的字符</p>
	 * @param mode
	 * @param text
	 * @return
	 * @return boolean
	 */
	public static boolean isFind(String mode, String text) {
		String resultString = match(mode, text);
		if("".equals(resultString)){
			return false;
		}else{
			return true;
		}
	} 
	
	/**
	 * 查找全部匹配的字符串返回List
	 * by Sid
	 * @param mode
	 *            正则语句
	 * @param text
	 *            需要替换的字符串
	 */
	public static List<String> matchAllList(String mode, String text){
		Pattern pattern = Pattern.compile(mode);
		Matcher matcher = pattern.matcher(text);
		List <String>list=new ArrayList<String>();
		while(matcher.find())
		{
			list.add(matcher.group());
		}
		return list;
	}
	/**
	 * @author ZhuXingang
	 * @time 2013-11-19下午05:49:28
	 * @function <p>查找多值匹配，并返回结果</p>
	 * @param mode
	 * @param text
	 * @param flags
	 * @return
	 * @return List<String>
	 */
	public static List<String> matchList(String mode, String text,int flags){
		Pattern pattern = Pattern.compile(mode,flags);
		Matcher matcher = pattern.matcher(text);
		List<String> matchAllList = new ArrayList<String>();
		while(matcher.find()){
			matchAllList.add(matcher.group());
		}
		return matchAllList;
	}
	/**
	 * 
	 * @function:获取分组值
	 * @Date:Jul 29, 2015 5:01:07 PM
	 * @param mode
	 * @param text
	 * @param groupNum
	 * @param flags
	 * @return
	 * List<String>
	 * @Developer:Zhuxingang
	 */
	public static List<String> matchGroupXList(String mode, String text,int groupNum,int flags){
		Pattern pattern = Pattern.compile(mode,flags);
		Matcher matcher = pattern.matcher(text);
		List<String> matchAllList = new ArrayList<String>();
		while(matcher.find()){
			matchAllList.add(matcher.group(groupNum));
		}
		return matchAllList;
	}
	/**
	 * 查找替换字符串
	 * @param mode
	 *            正则语句
	 * @param repString
	 *            替换的内容
	 * @param text
	 *            需要替换的字符串
	 * @param flags  
	 * 			  模式
	 * 
	 */
	public static String replaceFirst(String mode, String repString, String text) {
		Pattern pattern = Pattern.compile(mode);
		Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		if (matcher.find()) {
			matcher.appendReplacement(sb, repString);
		}
		matcher.appendTail(sb);
		text = sb.toString();
		return text;
	}
	
	/**
	 * 查找替换字符串
	 * @param mode
	 *            正则语句
	 * @param repString
	 *            替换的内容
	 * @param text
	 *            需要替换的字符串
	 * @param flags  
	 * 			  模式
	 * 
	 */
	public static String replaceFirst(String mode, String repString, String text, int flags) {
		Pattern pattern = Pattern.compile(mode, flags);
		Matcher matcher = pattern.matcher(text);
		StringBuffer sb = new StringBuffer();
		if (matcher.find()) {
			matcher.appendReplacement(sb, repString);
		}
		matcher.appendTail(sb);
		text = sb.toString();
		return text;
	}
	
}
