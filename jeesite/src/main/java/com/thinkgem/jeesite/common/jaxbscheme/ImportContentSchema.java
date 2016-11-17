package com.thinkgem.jeesite.common.jaxbscheme;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Content")
@XmlType(propOrder = { "type", "update","contentIndexList","editionDate","revisionDate","currencyText","libraryEditionDescription","librarySourceConst","libraryName"})  
public class ImportContentSchema {
	
	private String type;
	
	private String update;
	
    @XmlTransient
	private List<ImportContentIndexSchema> contentIndexList = new ArrayList<ImportContentIndexSchema>();
	
	protected String editionDate;
	
	protected String revisionDate;
	
	protected String currencyText;
	
	protected String libraryEditionDescription;
	
	protected String librarySourceConst;
	
	protected String libraryName;
	
	public void addIndex(ImportContentIndexSchema index){
		this.contentIndexList.add(index);
	}
	
	@XmlAttribute(name="Type")
	public String getType() {
		return type;
	}
	
	@XmlAttribute(name="Update")
	public String getUpdate() {
		return update;
	}
	public void setUpdate(String update) {
		this.update = update;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	@XmlElementWrapper(name="Indexes")
	@XmlElement(name="Index")
	public List<ImportContentIndexSchema> getContentIndexList() {
		return contentIndexList;
	}

	@XmlElement(name = "EditionDate", required = true)
	public String getEditionDate() {
		return editionDate;
	}

	public void setEditionDate(String editionDate) {
		this.editionDate = editionDate;
	}

	@XmlElement(name = "RevisionDate", required = true)
	public String getRevisionDate() {
		return revisionDate;
	}

	public void setRevisionDate(String revisionDate) {
		this.revisionDate = revisionDate;
	}

	@XmlElement(name = "CurrencyText", required = true)
	public String getCurrencyText() {
		return currencyText;
	}

	public void setCurrencyText(String currencyText) {
		this.currencyText = currencyText;
	}

	@XmlElement(name = "LibraryEditionDescription", required = true)
	public String getLibraryEditionDescription() {
		return libraryEditionDescription;
	}

	public void setLibraryEditionDescription(String libraryEditionDescription) {
		this.libraryEditionDescription = libraryEditionDescription;
	}

	@XmlElement(name = "LibrarySourceConst", required = true)
	public String getLibrarySourceConst() {
		return librarySourceConst;
	}

	public void setLibrarySourceConst(String librarySourceConst) {
		this.librarySourceConst = librarySourceConst;
	}

	@XmlElement(name = "LibraryName", required = true)
	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}

	public void setContentIndexList(List<ImportContentIndexSchema> contentIndexList) {
		this.contentIndexList = contentIndexList;
	} 
	
}
