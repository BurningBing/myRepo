/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.acttostatute.entity;

import java.util.Date;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * 记录SectionEntity
 * @author Sid Cao
 * @version 2015-04-13
 */
public class NonCaseStatuteSection extends DataEntity<NonCaseStatuteSection> {
	
	private static final long serialVersionUID = 1L;
	private String contentType;		// content_type
	private String libraryName;		// library_name
	private String state;		// state
	private String caption;		// caption
	private String description;		// description
	private String sourceNoteLink;		// source_note_link
	private String shortName;		// short_name
	private String revisionHistory;		// revision_history
	private String updateType;		// update_type
	private String effectiveDate;		// effective_date
	private String expirationDate;		// expiration_date
	private String dateEnacted;		// date_enacted
	private String actNumber;		// act_number
	private String content;		// content
	private String status;
	private String fileName;
	private Date createTime;		// create_time
	private Date updateTime;		// update_time
	private String exportTime;
	private String downloadTime;
	private Integer countNum;
	private String remark;
	private String qa;
	private String ids;
	private Date beginDate;		// 开始日期
	private Date endDate;		
	private String relevance;
	private String flag;
	private int pageNo = -1; // 当前页码
	private int pageSize = -1; // 页面大小，设置为“-1”表示不进行分页（分页无效）
	// 结束日期
	public NonCaseStatuteSection() {
		super();
	}

	public NonCaseStatuteSection(String id){
		super(id);
	}

	@Length(min=0, max=500, message="content_type长度必须介于 0 和 500 之间")
	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	
	@Length(min=0, max=500, message="library_name长度必须介于 0 和 500 之间")
	public String getLibraryName() {
		return libraryName;
	}

	public void setLibraryName(String libraryName) {
		this.libraryName = libraryName;
	}
	
	@Length(min=0, max=500, message="state长度必须介于 0 和 500 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Length(min=0, max=1000, message="caption长度必须介于 0 和 1000 之间")
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	@Length(min=0, max=1000, message="description长度必须介于 0 和 1000 之间")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Length(min=0, max=1000, message="source_note_link长度必须介于 0 和 1000 之间")
	public String getSourceNoteLink() {
		return sourceNoteLink;
	}

	public void setSourceNoteLink(String sourceNoteLink) {
		this.sourceNoteLink = sourceNoteLink;
	}
	
	@Length(min=0, max=1000, message="short_name长度必须介于 0 和 1000 之间")
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	@Length(min=0, max=1000, message="revision_history长度必须介于 0 和 1000 之间")
	public String getRevisionHistory() {
		return revisionHistory;
	}

	public void setRevisionHistory(String revisionHistory) {
		this.revisionHistory = revisionHistory;
	}
	
	@Length(min=0, max=1000, message="update_type长度必须介于 0 和 1000 之间")
	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}
	
	@Length(min=0, max=100, message="effective_date长度必须介于 0 和 100 之间")
	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	@Length(min=0, max=100, message="expiration_date长度必须介于 0 和 100 之间")
	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	@Length(min=0, max=100, message="date_enacted长度必须介于 0 和 100 之间")
	public String getDateEnacted() {
		return dateEnacted;
	}

	public void setDateEnacted(String dateEnacted) {
		this.dateEnacted = dateEnacted;
	}
	
	@Length(min=0, max=100, message="act_number长度必须介于 0 和 100 之间")
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getExportTime() {
		return exportTime;
	}

	public void setExportTime(String exportTime) {
		this.exportTime = exportTime;
	}

	public String getDownloadTime() {
		return downloadTime;
	}

	public void setDownloadTime(String downloadTime) {
		this.downloadTime = downloadTime;
	}

	public Integer getCountNum() {
		return countNum;
	}

	public void setCountNum(Integer countNum) {
		this.countNum = countNum;
	}

	public String getQa() {
		return qa;
	}

	public void setQa(String qa) {
		this.qa = qa;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public Date getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(Date beginDate) {
		this.beginDate = beginDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getRelevance() {
		return relevance;
	}

	public void setRelevance(String relevance) {
		this.relevance = relevance;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}



	
}