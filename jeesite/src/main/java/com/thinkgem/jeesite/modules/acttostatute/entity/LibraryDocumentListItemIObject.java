package com.thinkgem.jeesite.modules.acttostatute.entity;

import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentListItem;

public class LibraryDocumentListItemIObject extends LibraryDocumentListItem{
	private static final long serialVersionUID = 1L;
	public String libraryDocumentEidtion;
	public String relevanceString;
	
	
	
	public String getRelevanceString() {
		return relevanceString;
	}

	public void setRelevanceString(String relevanceString) {
		this.relevanceString = relevanceString;
	}

	public String getLibraryDocumentEidtion() {
		return libraryDocumentEidtion;
	}

	public void setLibraryDocumentEidtion(String libraryDocumentEidtion) {
		this.libraryDocumentEidtion = libraryDocumentEidtion;
	}
}
