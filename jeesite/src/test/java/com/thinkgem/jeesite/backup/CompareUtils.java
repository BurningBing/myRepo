package com.thinkgem.jeesite.backup;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.pdfbox.io.PushBackInputStream;
import org.dom4j.Document;

import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;

public class CompareUtils {
	
	private String html1;
	private String html2;
	private StringBuffer sb1;
	private StringBuffer sb2;
	private boolean pass;
	private boolean isMark = false;
	private boolean isAppend = false;
	

	public StringBuffer getSb1() {
		return sb1;
	}

	public boolean isPass() {
		return pass;
	}

	public void setResult(boolean pass) {
		this.pass = pass;
	}

	public void setSb1(StringBuffer sb1) {
		this.sb1 = sb1;
	}

	public StringBuffer getSb2() {
		return sb2;
	}

	public void setSb2(StringBuffer sb2) {
		this.sb2 = sb2;
	}

	public CompareUtils(String file1,String file2) {
		pass = true;
		System.out.println("compare utils");
		sb1 = new StringBuffer();
		sb2 = new StringBuffer();
		try {
			html1 = FileUtils.readFileToString(new File(file1));
			html2 = FileUtils.readFileToString(new File(file2));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public class MyIterator{
		
		public MyIterator(List<String> list){
			this.list = list;
			this.iterator = this.list.iterator();
		}
		private List<String> list;
		private String str;
		private Iterator<String> iterator;
		
		public Iterator<String> getIterator(){
			return this.iterator;
		}
		public List<String> getList(){
			return this.list;
		}
		public String getStr(){
			return this.str;
		}
		public void setStr(String str){
			this.str = str;
		}
		
	}
	
	public static void main(String[] args) {
		CompareUtils cu = new CompareUtils("C:\\ATS\\HTML\\20160408\\Idaho\\张睿\\S1302(1).html", "C:\\ATS\\HTML\\20160408\\Idaho\\霍旭红\\S1302(1).html");
		cu.starCompare();
		
		String h1 = RegexUtil.replace("font>(\\w)", "font> $1", cu.getSb1().toString());
		String h2 = RegexUtil.replace("font>(\\w)", "font> $1", cu.getSb2().toString());
		System.out.println(h1);
		System.out.println(h2);
		
	}
	
	public void starCompare(){
		List<String> list1 = RegexUtil.matchAllList("<p>[\\s\\S]*?</p>", html1);
		List<String> list2 = RegexUtil.matchAllList("<p>[\\s\\S]*?</p>", html2);
		loop(list1, list2, 1);
	}
	
	public void loop(List<String> list1,List<String> list2,int level){
		MyIterator m1 = new MyIterator(list1);
		MyIterator m2 = new MyIterator(list2);		
		while(m1.getIterator().hasNext()&&m2.getIterator().hasNext()){
			Iterator<String> iterator1 = m1.getIterator();
			Iterator<String> iterator2 = m2.getIterator();
			String str1 = iterator1.next();
			String str2 = iterator2.next();
			iterator1.remove();
			iterator2.remove();
			if(str1.equals(str2)){
				sb1.append(str1);
				sb2.append(str2);
			}else{
				System.out.println("s1: " +str1);
				System.out.println("s2: " +str2);
				pass = false;
				if(level == 1){
					//分词
					List<String> list3 = RegexUtil.matchAllList(".*?(\\s|$)", str1);
					List<String> list4 = RegexUtil.matchAllList(".*?(\\s|$)", str2);
					loop(list3, list4, 2);
				}else{
					m1.str = str1;
					m2.str = str2;
					compare(m1, m2);
				}
			}
		}
	}
	
	public void compare(MyIterator m1,MyIterator m2){
		char[] ca1 = m1.str.toCharArray();
		char[] ca2 = m2.str.toCharArray();
		int count = ca1.length<ca2.length?ca1.length:ca2.length;
		boolean f = true;
		for(int i=0;i<count;i++){
			char c1 = ca1[i];
			char c2 = ca2[i];
			if(c1!=c2&&f){
				sb1.append("<mark>"+c1);
				sb2.append("<mark>"+c2);
				f = false;
				if(i == count-1){
					if(c1==' '){
						sb1.append(m1.getIterator().next());
						m1.getIterator().remove();
					}else{
						sb2.append(m2.getIterator().next());
						m2.getIterator().remove();
					}
				}
			}else{
				sb1.append(c1);
				sb2.append(c2);
			}
		}
		if(count<ca2.length){
			for(int i=count;i<ca2.length;i++){
				sb2.append(ca2[i]);
			}
		}else{
			for(int i=count;i<ca1.length;i++){
				sb1.append(ca1[i]);
			}
		}
		sb1.append("</mark>");
		sb2.append("</mark>");
	}
	
	public void compareLine(String str1,String str2){
		char[] cs1 = str1.toCharArray();
		char[] cs2 = str2.toCharArray();
		int length = cs1.length<cs2.length?cs1.length:cs2.length;
		for(int i=0;i<length;i++){
			char c1 = cs1[i];
			char c2 = cs2[i];
			if(c1==c2){
				sb1.append(c1);
				sb2.append(c2);
			}else{
				if(c1=='<'){
					cs1 = clearTaget(cs1, length, i,sb1);
					c1 = cs1[i];
					while(c1=='<'){
						cs1 = clearTaget(cs1, length, i,sb1);
						c1 = cs1[i];
					}
					sb1.append(cs1[i]);
					sb2.append(c2);
				}
				if(c2=='<'){
					cs2 = clearTaget(cs2, length, i,sb2);
					c2 = cs2[i];
					while(c2=='<'){
						cs2 = clearTaget(cs2, length, i,sb2);
						c2 = cs2[i];
					}
					sb1.append(cs2[i]);
					sb2.append(c1);
				}
			}
		}
		if(length<cs1.length){
			for(int i=length;i<cs1.length;i++){
				sb1.append(cs1[i]);
			}
		}else{
			for(int i=length;i<cs2.length;i++){
				sb2.append(cs2[i]);
			}
		}
		if(isMark){
			sb1.append("</mark>");
			sb2.append("</mark>");
		}
	}
	
	public char[] clearTaget(char[] cs,int length,int i,StringBuffer sb){
		if(length-i>1&&cs[i+1]!='/'&&!isMark){
			sb1.append("<mark>");
			sb2.append("<mark>");
			isMark = true;
		}else if(length-i>1&&cs[i+1]=='/'){
			isAppend = true;
		}
		for(int j=i;j<cs.length;j++){
			char c2 = cs[j];
			sb.append(c2);
			if(c2=='>'){
				cs = ArrayUtils.addAll(Arrays.copyOfRange(cs, 0, i),Arrays.copyOfRange(cs, j+1, cs.length));
				break;
			}
		}
		if(isMark&&isAppend){
			sb1.append("</mark>");
			sb2.append("</mark>");
			isMark = false;
			isAppend = false;
		}
		return cs;
	}
	
	public void compareByChar(char[] ca1,char[] ca2){
		int count = ca1.length<ca2.length?ca1.length:ca2.length;
		boolean f = true;
		for(int i=0;i<count;i++){
			char c1 = ca1[i];
			char c2 = ca2[i];
			if(c1!=c2&&f){
				if(i == count-1){
					if(c1==' '){
						ca1 = ArrayUtils.addAll(Arrays.copyOfRange(ca1, 0, i),Arrays.copyOfRange(ca1, i+1, ca1.length));
					}else{
						ca2 = ArrayUtils.addAll(Arrays.copyOfRange(ca2, 0, i),Arrays.copyOfRange(ca2, i+1, ca1.length));
					}
				}
				sb1.append("<mark>"+c1);
				sb2.append("<mark>"+c2);
				f = false;
			}else{
				sb1.append(c1);
				sb2.append(c2);
			}
		}
		sb1.append("</mark>");
		sb2.append("</mark>");
		if(count<ca2.length){
			for(int i=count;i<ca2.length;i++){
				sb2.append(ca2[i]);
			}
		}else{
			for(int i=count;i<ca1.length;i++){
				sb1.append(ca1[i]);
			}
		}
	}
	
	public String getNext(Iterator<String> iterator){
		String str = "";
		if(iterator.hasNext()){
			str = iterator.next();
		}else{
			str = "$EOL";
		}
		return str;
	}
	
}
