package com.thinkgem.jeesite.tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hamcrest.core.Is;

import com.thinkgem.jeesite.common.utils.FileUtils;

public class GenerateXml {
	
	
	
	public static void main(String[] args) {
		String state = "Hawaii";
		String date = "20160503";
		String sql = "select * from ats_section s left join ats_act a on s.pid = a.id where state = '"+state+"' and a.day = '"+date+"' and is_del = 0";
		System.out.println(sql);
		ResultSet rs = JDBCUtils.query(sql);
		Set<String> billNumbers = new HashSet<String>();
		try {
			while(rs.next()){
				String caption = rs.getString("caption");
				String description = rs.getString("description");
				String content = "";
				try {
					content = FileUtils.readFileToString(new File(rs.getString("content")));
				} catch (Exception e) {
					System.out.println("caption Is repeal?");
				}
				String billNumber = rs.getString("bill_number");
				String eff = rs.getString("eff");
				String exp = rs.getString("exp");
				String link = rs.getString("link");
				String shortName = rs.getString("short_name");
				String update = rs.getString("update_type");
//				String editor = rs.getString("editor");
				File xmlFile = new File("E:\\export\\"+date+"\\"+state+"\\"+billNumber+"\\"+billNumber+"("+rs.getString("parse_order")+").xml");
				//拼装Dom
				Element root = DocumentHelper.createElement("Content");
				Document dom = DocumentHelper.createDocument(root);
				Element indexes = root.addElement("Indexes");
				Element index = indexes.addElement("Index");
				index.addAttribute("HasChildren", "0");
				index.addAttribute("Level", "1");
				billNumbers.add(billNumber);
				index.addElement("ActNumber").addText(billNumber);
				index.addElement("Caption").addText(caption);
				index.addElement("Content").addText(content);
				if(description!=null){
					index.addElement("Description").addText(description);
				}else{
					index.addElement("Description").addText("");
				}
				
				index.addElement("EffectiveDate").addText(eff);
				if(exp!=null){
					index.addElement("ExpirationDate").addText(exp);
				}else{
					index.addElement("ExpirationDate").addText("");
				}
				index.addElement("RevisionHistory").addText("");
				index.addElement("ShortName").addText(shortName);
				if(link!=null){
					index.addElement("SourceNoteLink").addText(link);
				}else{
					index.addElement("SourceNoteLink").addText("");
				}
				if(update.equals("1")){
					update = "New";
				}else if(update.equals("2")){
					update = "Modify";
				}else if(update.equals("3")){
					update = "Repeal";
				}
				index.addElement("Update").addText(update);
				
				//写文件
				OutputFormat format = new OutputFormat("  ",true);
				format.setEncoding("utf-8");
				format.setNewlines(true);
				XMLWriter xmlWriter;
				try {
					if(!xmlFile.getParentFile().exists()){
						xmlFile.getParentFile().mkdirs();
					}
					xmlWriter = new XMLWriter(new FileOutputStream(xmlFile),format);
					xmlWriter.write(dom);
					xmlWriter.close();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		System.out.println(billNumbers.toString());
		JDBCUtils.close();
	}
}
