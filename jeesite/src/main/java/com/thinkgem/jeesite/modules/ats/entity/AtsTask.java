/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.entity;

import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;

/**
 * taskEntity
 * @author Chad
 * @version 2016-03-09
 */
public class AtsTask extends DataEntity<AtsTask> {
	
	private static final long serialVersionUID = 1L;
	private String state;		// state
	private String sessionYear;		// session_year
	private Integer isFinished;		// is_finished
	private String url;		// url
	private String shortName;		// short_name
	private String encoding;		// encoding
	private String prefix;
	
	
	public AtsTask() {
		super();
	}

	public AtsTask(String id){
		super(id);
	}

	@Length(min=1, max=200, message="state长度必须介于 1 和 200 之间")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Length(min=1, max=500, message="session_year长度必须介于 1 和 500 之间")
	public String getSessionYear() {
		return sessionYear;
	}

	public void setSessionYear(String sessionYear) {
		this.sessionYear = sessionYear;
	}
	
	@NotNull(message="is_finished不能为空")
	public Integer getIsFinished() {
		return isFinished;
	}

	public void setIsFinished(Integer isFinished) {
		this.isFinished = isFinished;
	}
	
	@Length(min=1, max=2000, message="url长度必须介于 1 和 2000 之间")
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@Length(min=1, max=1000, message="short_name长度必须介于 1 和 1000 之间")
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	@Length(min=1, max=500, message="encoding长度必须介于 1 和 500 之间")
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	
}