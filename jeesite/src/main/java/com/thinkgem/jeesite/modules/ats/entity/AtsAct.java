/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.entity;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * actEntity
 * @author Chad
 * @version 2016-03-09
 */
public class AtsAct extends DataEntity<AtsAct> {
	
	private static final long serialVersionUID = 1L;
	private String state;		// 州名
	private String billNumber;		// Bill Number
	private String url;		// 链接
	private String downloadFile;		// 下载文件路径
	private long fileSize;		// 下载文件大小
	private Integer type;		// 文件类型：1.pdf ;2.html ;3.word;
	private String day;		// 下载日期
	private Integer workMode;		// 操作类型： 1.单打，2双打，3多打
	private Integer status;		// 状态： 1.下载，2.签名
	private String uploadFile;		// 上传文件路径
	private String remark;		// 备注
	private String sessionYear;
	private String effectiveDate;
	private Integer pageCount;
	private String delFlag;
	
	
	
	
	public AtsAct(String state, String billNumber, String url, String downloadFile, long fileSize, Integer type,
			String day, Integer workMode, String effectiveDate) {
		super();
		this.state = state;
		this.billNumber = billNumber;
		this.url = url;
		this.downloadFile = downloadFile;
		this.fileSize = fileSize;
		this.type = type;
		this.day = day;
		this.workMode = workMode;
		this.effectiveDate = effectiveDate;
		this.status = 1;
		this.delFlag = "0";
		this.sessionYear = "2016";
		this.pageCount = 0;
	}

	public String getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(String sessionYear) {
		this.sessionYear = sessionYear;
	}

	public AtsAct() {
		super();
	}

	public AtsAct(String id){
		super(id);
	}

	@Length(min=1, max=100, message="州名长度必须介于 1 和 100 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Length(min=1, max=200, message="Bill Number长度必须介于 1 和 200 之间")
	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}
	
	@Length(min=1, max=1000, message="链接长度必须介于 1 和 1000 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Length(min=1, max=1000, message="下载文件路径长度必须介于 1 和 1000 之间")
	public String getDownloadFile() {
		return downloadFile;
	}

	public void setDownloadFile(String downloadFile) {
		this.downloadFile = downloadFile;
	}
	
	@NotNull(message="下载文件大小不能为空")
	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
	
	@Length(min=1, max=100, message="下载日期长度必须介于 1 和 100 之间")
	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}
	
	public Integer getWorkMode() {
		return workMode;
	}

	public void setWorkMode(Integer workMode) {
		this.workMode = workMode;
	}
	
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	@Length(min=1, max=1000, message="上传文件路径长度必须介于 1 和 1000 之间")
	public String getUploadFile() {
		return uploadFile;
	}

	public void setUploadFile(String uploadFile) {
		this.uploadFile = uploadFile;
	}
	
	@Length(min=1, max=2000, message="备注长度必须介于 1 和 2000 之间")
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
}