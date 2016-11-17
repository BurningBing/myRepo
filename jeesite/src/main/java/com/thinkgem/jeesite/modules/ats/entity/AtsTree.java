package com.thinkgem.jeesite.modules.ats.entity;

import com.thinkgem.jeesite.common.persistence.DataEntity;

public class AtsTree extends DataEntity<AtsTree>{

	private static final long serialVersionUID = 1L;
	
	public AtsTree(){
		
	}
	
	public AtsTree(String id) {
		super();
		this.id = id;
	}

	public AtsTree(String pid, String name, String iconSkin, String isParent, String method, String isHidden,
			String fid, String status, String editor, String rootId) {
		super();
		this.pid = pid;
		this.name = name;
		this.iconSkin = iconSkin;
		this.isParent = isParent;
		this.method = method;
		this.isHidden = isHidden;
		this.fid = fid;
		this.status = status;
		this.editor = editor;
		this.rootId = rootId;
	}

	private String pid;
	private String name;
	private String iconSkin;
	private String isParent;
	private String method;
	private String isHidden;
	private String open;
	private String fid;
	private String status;
	private String editor;
	private String rootId;

	public String getPid() {
		return pid;
	}
	
	public void setPid(String pid) {
		this.pid = pid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getIconSkin() {
		return iconSkin;
	}
	
	public void setIconSkin(String iconSkin) {
		this.iconSkin = iconSkin;
	}
	
	public String getIsParent() {
		return isParent;
	}
	
	public void setIsParent(String isParent) {
		this.isParent = isParent;
	}
	
	public String getMethod() {
		return method;
	}
	
	public void setMethod(String method) {
		this.method = method;
	}
	
	public String getIsHidden() {
		return isHidden;
	}
	
	public void setIsHidden(String isHidden) {
		this.isHidden = isHidden;
	}
	
	public String getOpen() {
		return open;
	}
	
	public void setOpen(String open) {
		this.open = open;
	}
	
	public String getFid() {
		return fid;
	}
	
	public void setFid(String fid) {
		this.fid = fid;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getEditor() {
		return editor;
	}
	
	public void setEditor(String editor) {
		this.editor = editor;
	}
	
	public String getRootId() {
		return rootId;
	}
	
	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

}
