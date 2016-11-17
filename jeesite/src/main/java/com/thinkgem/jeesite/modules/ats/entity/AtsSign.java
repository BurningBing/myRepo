/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.entity;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

/**
 * 签名Entity
 * @author Chad
 * @version 2016-03-20
 */
public class AtsSign extends DataEntity<AtsSign> {
	
	private static final long serialVersionUID = 1L;
	private String pid;		// pid
	private String editor;		// editor
	private String status;
	private String uploadFile;
	private int pageNumber;
	private int sectionCount;
	
	public AtsSign() {
		super();
	}
	
	public AtsSign(String pid){
		this.pid = pid;
//		this.editor = UserUtils.getUser().getName();
	}

	public AtsSign(String pid, String editor, String status) {
		this.pid = pid;
		this.editor = editor;
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(String uploadFile) {
		this.uploadFile = uploadFile;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getSectionCount() {
		return sectionCount;
	}

	public void setSectionCount(int sectionCount) {
		this.sectionCount = sectionCount;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	@Length(min=1, max=200, message="editor长度必须介于 1 和 200 之间")
	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}
	
}