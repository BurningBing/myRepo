package com.thinkgem.jeesite.common.test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocument;
import com.fastcase.services._2009._03._06.ResearchServicesStub.OutlineViewListItem;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.utils.XmlGenerator;
import com.thinkgem.jeesite.common.utils.XmlGenerator.Index;
import com.thinkgem.jeesite.common.webservice.WBNonCaseUtils;

public class TestXmlGenerator {
	public static void main(String[] args) throws Exception {
		parse("51070705");
		
//		OutlineViewListItem[] items = WBNonCaseUtils.getStatuteTree("51067477");
//		for(OutlineViewListItem item:items){
//			String id = String.valueOf(item.getNodeID());
//			WBNonCaseUtils.getStatuteTree(id);
//		}
	}
	
	public static void parse(String id) throws Exception{
		boolean flag = false;
		File file = null;
		OutlineViewListItem[] items = WBNonCaseUtils.getStatuteTree(id,0);
		XmlGenerator generator = new XmlGenerator("Virginia");
		List<XmlGenerator.Index> indexes = new ArrayList<XmlGenerator.Index>();
		generator.setIndexes(indexes);
		for(OutlineViewListItem item:items){
//			System.out.println(RegexUtil.match(".+?(?=[A-Z])", item.getNodeDescription()));
			id = String.valueOf(item.getNodeID());
			if(item.getHasChildNodes()){
				System.out.println("P:"+item.getNodeDescription());
				parse(id);
			}else{
				flag = true;
				LibraryDocument dom = WBNonCaseUtils.findNonCaseById(item.getParentNodeID()+"");
				String f = RegexUtil.replace("\\:", "-", dom.getIndexCaption());
				file = new File("E:\\export\\20160422\\Virginia\\"+f+".xml");
				if(!file.exists()){
					file.createNewFile();
				}
				dom = WBNonCaseUtils.findNonCaseById(item.getNodeID()+"");
				generator.setUpdate("Repeal");
				Index index = generator.new Index();
				index.setCaption(dom.getIndexCaption());
				index.setActNumber("HB 209");
				index.setContent("");
				index.setDescription(dom.getIndexDescription());
				index.setEff("10/01/2016");
				index.setId(dom.getLibraryDocumentID()+"");
				index.setShortName(dom.getShortName());
				index.setType("UpdateLibraryIndexId");
				indexes.add(index);
				System.out.println(index.getCaption());
			}
		}
		if(flag){
			generator.generateFile(file);
			System.out.println(file.getAbsolutePath());
			System.out.println();
		}
		
		
	}
	
}
