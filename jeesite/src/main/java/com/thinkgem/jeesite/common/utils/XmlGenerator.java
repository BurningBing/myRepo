package com.thinkgem.jeesite.common.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlGenerator {
	private String update;
	private String editionDate;
	private String libraryDesc;
	private String library;
	private String libConst;
	
	public List<XmlGenerator.Index> getIndexes() {
		return indexes;
	}

	public void setIndexes(List<XmlGenerator.Index> indexes) {
		this.indexes = indexes;
	}

	private List<XmlGenerator.Index> indexes;
	
	public XmlGenerator() {
		
	}
	
	public XmlGenerator(String state){
		ResultSet rs = JDBCUtils.query("select * from ats_statute where state = '"+state+"'");
		try {
			while (rs.next()) {
				this.library = rs.getString(4);
				this.libraryDesc = rs.getString(5);
				this.libConst = rs.getString(6);
				this.editionDate = rs.getString(7);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUpdate() {
		return update;
	}

	public void setUpdate(String update) {
		this.update = update;
	}

	public String getEditionDate() {
		return editionDate;
	}

	public void setEditionDate(String editionDate) {
		this.editionDate = editionDate;
	}

	public String getLibraryDesc() {
		return libraryDesc;
	}

	public void setLibraryDesc(String libraryDesc) {
		this.libraryDesc = libraryDesc;
	}

	public String getLibrary() {
		return library;
	}

	public void setLibrary(String library) {
		this.library = library;
	}

	public String getLibConst() {
		return libConst;
	}

	public void setLibConst(String libConst) {
		this.libConst = libConst;
	}
	
	//内部类
	public class Index{
		private String id;
		private String type;
		private String caption;
		private String description;
		private String eff;
		private String actNumber;
		private String content;
		private String shortName;
		private List<XmlGenerator.Index> indexes;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getCaption() {
			return caption;
		}
		public void setCaption(String caption) {
			this.caption = caption;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getEff() {
			return eff;
		}
		public void setEff(String eff) {
			this.eff = eff;
		}
		public String getActNumber() {
			return actNumber;
		}
		public void setActNumber(String actNumber) {
			this.actNumber = actNumber;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getShortName() {
			return shortName;
		}
		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
		public List<XmlGenerator.Index> getIndexes() {
			return indexes;
		}
		public void setIndexes(List<XmlGenerator.Index> indexes) {
			this.indexes = indexes;
		}
	}
	
	public void generateFile(File file){
		Element root = DocumentHelper.createElement("Content");
		Document dom = DocumentHelper.createDocument(root);
		root.addAttribute("Type", "Statutes").addAttribute("Update", this.update);
		Element indexes = root.addElement("Indexes");
		for(int i=0;i<this.indexes.size();i++){
			XmlGenerator.Index temp = this.indexes.get(i);
			Element index = indexes.addElement("Index");
			index.addAttribute("Level", "1").addAttribute("HasChildren", "0");
//			index.addElement("Ids").addElement("Id").addAttribute("Value", temp.getId()).addAttribute("Type", temp.getType());
			index.addElement("ActNumber").addText(RegexUtil.replace("-", " ", temp.getActNumber()));
			index.addElement("Caption").setText(temp.getCaption());
			index.addElement("Content").addText(temp.getContent());
			index.addElement("Description").addText(temp.getDescription());
			index.addElement("EffectiveDate").addText(temp.getEff());
			index.addElement("ExpirationDate").addText("");
			index.addElement("RevisionHistory").addText("");
			index.addElement("ShortName").addText(temp.getShortName());
			index.addElement("SourceNoteLink").addText("");
			index.addElement("Update").addText(temp.getType());
		}
		root.addElement("EditionDate").addText(this.editionDate);
		root.addElement("LibraryEditionDescription").addText(this.libraryDesc);
		root.addElement("LibrarySourceConst").addText(this.libConst);
		root.addElement("LibraryName").addText(this.library);
		OutputFormat format = new OutputFormat("  \n",true);
		format.setEncoding("utf-8");
		format.setNewlines(true);
		XMLWriter xmlWriter;
		try {
			if(!file.getParentFile().exists()){
				file.getParentFile().mkdirs();
			}
			xmlWriter = new XMLWriter(new FileOutputStream(file),format);
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
	
	public static void main(String[] args) throws SQLException {
		XmlGenerator xg = new XmlGenerator("Virginia");
		System.out.println(xg.getEditionDate());
	}
	
	
}
