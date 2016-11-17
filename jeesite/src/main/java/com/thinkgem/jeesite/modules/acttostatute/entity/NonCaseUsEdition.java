/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.acttostatute.entity;

import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * NonCaseUSEditionEntity
 * @author Sid
 * @version 2015-08-03
 */
public class NonCaseUsEdition extends DataEntity<NonCaseUsEdition> {
	
	private static final long serialVersionUID = 1L;
	private Integer editionId;		// edition_id
	private String state;		// state
	private String contentType;		// content_type
	private String libraryName;		// library_name
	private String libraryEdition;		// library_edition
	private String librarySourceConst;		// library_source_const
	private String editionDate;		// edition_date
	private Date createTime;		// create_time
	private String editionYear;		// edition_year
	private Integer status;		// status
	private Integer jurisdictionId;		// jurisdiction_id
	
	public NonCaseUsEdition() {
		super();
	}

	public NonCaseUsEdition(String id){
		super(id);
	}

	@NotNull(message="edition_id不能为空")
	public Integer getEditionId() {
		return editionId;
	}

	public void setEditionId(Integer editionId) {
		this.editionId = editionId;
	}
	
	@Length(min=1, max=200, message="state长度必须介于 1 和 200 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Length(min=1, max=200, message="content_type长度必须介于 1 和 200 之间")
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	@Length(min=1, max=400, message="library_name长度必须介于 1 和 400 之间")
	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}
	
	@Length(min=1, max=50, message="library_edition长度必须介于 1 和 50 之间")
	public String getLibraryEdition() {
		return libraryEdition;
	}

	public void setLibraryEdition(String libraryEdition) {
		this.libraryEdition = libraryEdition;
	}
	
	@Length(min=0, max=400, message="library_source_const长度必须介于 0 和 400 之间")
	public String getLibrarySourceConst() {
		return librarySourceConst;
	}

	public void setLibrarySourceConst(String librarySourceConst) {
		this.librarySourceConst = librarySourceConst;
	}
	
	@Length(min=0, max=400, message="edition_date长度必须介于 0 和 400 之间")
	public String getEditionDate() {
		return editionDate;
	}

	public void setEditionDate(String editionDate) {
		this.editionDate = editionDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@Length(min=0, max=50, message="edition_year长度必须介于 0 和 50 之间")
	public String getEditionYear() {
		return editionYear;
	}

	public void setEditionYear(String editionYear) {
		this.editionYear = editionYear;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Integer getJurisdictionId() {
		return jurisdictionId;
	}

	public void setJurisdictionId(Integer jurisdictionId) {
		this.jurisdictionId = jurisdictionId;
	}
	
}