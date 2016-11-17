package com.thinkgem.jeesite.common.jaxbscheme;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
@XmlRootElement(name = "Id")
@XmlType(propOrder = { "value", "type"})  
public class ImportContentIdsSchema {
	
	private String value;
	private String type;
	
	@XmlAttribute(name="Value")
	public String getValue() {
		return value;
	}
	@XmlAttribute(name="Type")
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
