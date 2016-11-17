package com.thinkgem.jeesite.common.jaxbscheme;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "Index")
@XmlType(propOrder = { "level", "hasChildren","idList","caption","description","sourceNoteLink","content","shortName","revisionHistory","effectiveDate","expirationDate","actNumber"})  
public class ImportContentIndexSchema {
	
	protected String level = "1";
		
	protected String hasChildren = "0";
	
    protected String caption;
    
    protected String description;
    
    protected String sourceNoteLink;
    
    protected String content;
    
    protected String shortName;
    
    protected String revisionHistory;
    
    protected String effectiveDate;
    
    protected String expirationDate;
    
    protected String actNumber;
    
    @XmlTransient
    public List<ImportContentIdsSchema> idList=new ArrayList<ImportContentIdsSchema>();
    
    @XmlElement(name = "Caption", required = true)
    public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	@XmlElement(name = "Description", required = true)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@XmlElement(name = "SourceNoteLink", required = true)
	public String getSourceNoteLink() {
		return sourceNoteLink;
	}
	public void setSourceNoteLink(String sourceNoteLink) {
		this.sourceNoteLink = sourceNoteLink;
	}
	@XmlElement(name = "Content", required = true)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@XmlElement(name = "ShortName", required = true)
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	@XmlElement(name = "RevisionHistory", required = true)
	public String getRevisionHistory() {
		return revisionHistory;
	}
	public void setRevisionHistory(String revisionHistory) {
		this.revisionHistory = revisionHistory;
	}
	@XmlElement(name = "EffectiveDate", required = true)
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	@XmlElement(name = "ExpirationDate", required = true)
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	@XmlElement(name = "ActNumber", required = true)
	public String getActNumber() {
		return actNumber;
	}
	public void setActNumber(String actNumber) {
		this.actNumber = actNumber;
	}
	@XmlAttribute(name="Level")
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	@XmlAttribute(name="HasChildren")
	public String getHasChildren() {
		return hasChildren;
	}
	public void setHasChildren(String hasChildren) {
		this.hasChildren = hasChildren;
	}
	
	public void addIds(ImportContentIdsSchema ids){
		this.idList.add(ids);
	}
	@XmlElementWrapper(name="Ids")
	@XmlElement(name="Id")
	public List<ImportContentIdsSchema> getIdList() {
		return idList;
	}
	public void setIdsList(List<ImportContentIdsSchema> idList) {
		this.idList = idList;
	}
}
