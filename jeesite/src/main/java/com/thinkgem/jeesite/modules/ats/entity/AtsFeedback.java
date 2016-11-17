/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * feedbackEntity
 * @author Chad
 * @version 2016-04-11
 */
public class AtsFeedback extends DataEntity<AtsFeedback> {
	
	private static final long serialVersionUID = 1L;
	private String actId;		// act_id
	private String sectionId;		// section_id
	private String editor;		// editor
	private Integer type;		// type
	private Integer status;		// status
	private String content;		// content
	private String day;		// day
	private String checker;		// checker
	
	public AtsFeedback() {
		super();
	}

	public AtsFeedback(String id){
		super(id);
	}

	@Length(min=1, max=18, message="act_id长度必须介于 1 和 18 之间")
	public String getActId() {
		return actId;
	}

	public void setActId(String actId) {
		this.actId = actId;
	}
	
	@Length(min=1, max=18, message="section_id长度必须介于 1 和 18 之间")
	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	
	@Length(min=1, max=200, message="editor长度必须介于 1 和 200 之间")
	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}
	
	@NotNull(message="type不能为空")
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@NotNull(message="status不能为空")
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Length(min=0, max=1000, message="content长度必须介于 0 和 1000 之间")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Length(min=1, max=200, message="day长度必须介于 1 和 200 之间")
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	@Length(min=1, max=200, message="checker长度必须介于 1 和 200 之间")
	public String getChecker() {
		return checker;
	}

	public void setChecker(String checker) {
		this.checker = checker;
	}
	
}