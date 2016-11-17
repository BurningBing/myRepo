/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.entity;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * statuteEntity
 * @author Chad
 * @version 2016-03-29
 */
public class AtsStatute extends DataEntity<AtsStatute> {
	
	private static final long serialVersionUID = 1L;
	private String state;		// state
	private String editionId;		// edition_id
	private String libraryName;		// library_name
	private String libraryEdition;		// library_edition
	private String libraryConst;		// library_const
	private String editionDate;		// edition_date
	
	public AtsStatute() {
		super();
	}

	public AtsStatute(String id){
		super(id);
	}

	@Length(min=1, max=200, message="state长度必须介于 1 和 200 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Length(min=1, max=200, message="edition_id长度必须介于 1 和 200 之间")
	public String getEditionId() {
		return editionId;
	}

	public void setEditionId(String editionId) {
		this.editionId = editionId;
	}
	
	@Length(min=1, max=500, message="library_name长度必须介于 1 和 500 之间")
	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}
	
	@Length(min=1, max=800, message="library_edition长度必须介于 1 和 800 之间")
	public String getLibraryEdition() {
		return libraryEdition;
	}

	public void setLibraryEdition(String libraryEdition) {
		this.libraryEdition = libraryEdition;
	}
	
	@Length(min=1, max=200, message="library_const长度必须介于 1 和 200 之间")
	public String getLibraryConst() {
		return libraryConst;
	}

	public void setLibraryConst(String libraryConst) {
		this.libraryConst = libraryConst;
	}
	
	@Length(min=1, max=200, message="edition_date长度必须介于 1 和 200 之间")
	public String getEditionDate() {
		return editionDate;
	}

	public void setEditionDate(String editionDate) {
		this.editionDate = editionDate;
	}
	
}