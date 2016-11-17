package com.thinkgem.jeesite.modules.ats.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.List;

import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;

public class SuperCompare {
	private String html;
	private List<String> list;
	private StringBuffer result;
	private boolean isPass;
	private String p1;
	private String description;
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPass() {
		return isPass;
	}

	public void setPass(boolean isPass) {
		this.isPass = isPass;
	}

	public StringBuffer getResult() {
		return result;
	}

	public void setResult(StringBuffer result) {
		this.result = result;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	//-------------------------------------构造函数---------------------------------------------
	
	public SuperCompare(String html){
		this.html = html;
		initialCompare();
	}
	
	public SuperCompare(File file) throws IOException {
		this(FileUtils.readFileToString(file));
	}
	
	//-------------------------------------初始化方法---------------------------------------------
	
	/**
	 * 初始化方法
	 */
	private void initialCompare(){
		isPass = true;
		result = new StringBuffer();
		formatHtml();
		toList();
	}
	
	/**
	 * 文本格式化
	 * @return
	 */
	private String formatHtml(){
		html = RegexUtil.replace("^\\s*", "", html);
		html = RegexUtil.replace("^\n", "", html,RegexUtil.MULTILINE);
		html = RegexUtil.replace("<p>\\s*?</p>", "", html);
		html = RegexUtil.replace("'#f00'", "\"#f00\"", html);
		html = RegexUtil.replace("</font>(\\s*?)<font color=\"#f00\">", "$1", html);
		html = RegexUtil.replace("</strike>(\\s*?)<strike>", "$1", html);
		html = RegexUtil.replace("</strike>(\\s*?)<strike>", "$1", html);
		html = RegexUtil.replace("</u>(\\s*?)<u>", "$1", html);
		html = RegexUtil.replace("</u>(\\s*?)<u>", "$1", html);
		html = RegexUtil.replace("<u>\\s*", "<u>", html);
		html = RegexUtil.replace("<u>\\s*", "<u>", html);
		html = RegexUtil.replace("<p>\\s*", "<p>", html);
		html = RegexUtil.replace("<p>\\s*", "<p>", html);
		html = RegexUtil.replace("&quot;", "\"", html);
		html = RegexUtil.replace("&quot;", "\"", html);
		html = RegexUtil.replace("<br[^>]*?>", "", html);
		return html;
	}
	
	/**
	 * 将文本转换为List
	 * @return list
	 */
	private List<String> toList(){
		list = RegexUtil.matchAllList("<p>[\\s\\S]*?</p>", html);
		return list;
	}
	
	/**
	 * 获取纯文本
	 * @return
	 */
	private String getPureText(String str, boolean keepBlank){
		str = RegexUtil.replace("<[^>]*?>", "", str);
		if(!keepBlank){
			str = RegexUtil.replace("\\s", "", str);
		}
		return str;
	}
	
	//-------------------------------------核心对比方法---------------------------------------------
	
	public void compare(SuperCompare superCompare){
		compare(superCompare.list);
//		if(list.size()==superCompare.list.size()){
//			compare(superCompare.list);
//		}else{
//			this.setPass(false);
//			this.setDescription("段落数量不一致");
//			result.append("<p>段落数量不一致，本人")
//		}
	}
	
	public void compare(List<String> list2){
		for(int i=0;i<list.size();i++){
			p1 = list.get(i);
			try{
				String p2 = list2.get(i);
				System.out.println(p1);
				System.out.println(p2);
				if(p1.equals(p2)){
					result.append(p1+"\n");
				}else{
					compare(list2.get(i));
				}
			}catch (Exception e) {
				result.append(p1+"\n");
				isPass = false;
			}
		}
	}
	
	public void compare(String p2){
		if(getPureText(p1,false).equals(getPureText(p2,false))){
			// 说明是标签或者空格存在差异
			PushbackInputStream stream = new PushbackInputStream(new ByteArrayInputStream(p2.getBytes()));
			compareWithByte(stream,1);
		}else{
			// 文本存在差异
			PushbackInputStream stream = new PushbackInputStream(new ByteArrayInputStream(getPureText(p2,true).getBytes()));
			compareWithByte(stream,0);
		}
	}
	
	public void compareWithByte(PushbackInputStream stream,int type){
		boolean def = false;
		if(type==0){
			byte[] bytes1 = getPureText(p1,true).getBytes();
			result.append("<p>");
			//纯文本差异
			try {
				for(byte b1:bytes1){
					if(def){
						result.append((char)b1);
					}else{
						if(b1==32){
							result.append((char)b1);
							continue;
						}
						int n = stream.read();
						while(n==32){
							n = stream.read();
						}
						if(b1 == n){
							result.append((char)b1);
						}else{
							def = true;
							stream.close();
							result.append("<span style='background-color:orange'>"+(char)b1);
						}
					}
				}
				result.append("</span></p>\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(type==1){
			byte[] bytes1 = p1.getBytes();
			try {
				for(byte b1:bytes1){
					if(def){
						result.append((char)b1);
					}else{
						if(b1==32){
							result.append((char)b1);
							continue;
						}
						int n = stream.read();
						while(n==32){
							n = stream.read();
						}
						if(b1 == n){
							result.append((char)b1);
						}else{
							def = true;
							stream.close();
							result.append("<span style='background-color:orange'>"+(char)b1);
						}
					}
				}
				result.append("\n");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(isPass&&def){
			isPass = false;
		}
		System.out.println("对比结果："+!def);
		
	}
}


