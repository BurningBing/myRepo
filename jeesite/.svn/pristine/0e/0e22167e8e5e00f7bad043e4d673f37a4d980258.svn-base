/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.ats.entity;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

import com.thinkgem.jeesite.common.persistence.DataEntity;
import com.thinkgem.jeesite.common.utils.DateUtils;

/**
 * messageEntity
 * @author Chad
 * @version 2016-03-09
 */
public class AtsMessage extends DataEntity<AtsMessage> {
	
	private static final long serialVersionUID = 1L;
	private String fromUser;		// from_user
	private String toUser;		// to_user
	private String sendTime = DateUtils.getDate("yyyy-MM-dd hh:mm:ss");		// send_time
	private String content;		// content
	private Integer isRead = 0;		// is_read
	
	public AtsMessage() {
		super();
	}

	public AtsMessage(String id){
		super(id);
	}

	@Length(min=1, max=100, message="from_user长度必须介于 1 和 100 之间")
	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	
	@Length(min=1, max=100, message="to_user长度必须介于 1 和 100 之间")
	public String getToUser() {
		return toUser;
	}

	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	
	@Length(min=1, max=200, message="send_time长度必须介于 1 和 200 之间")
	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}
	
	@Length(min=1, max=1000, message="content长度必须介于 1 和 1000 之间")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@NotNull(message="is_read不能为空")
	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}
	
}