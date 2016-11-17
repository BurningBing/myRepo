package com.thinkgem.jeesite.common.webservice;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONArray;
import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.axis2.AxisFault;
import org.apache.commons.lang3.StringUtils;

import com.fastcase.services._2009._03._06.IResearchServices_Basic_GetLibraries_ServiceFaultDataFault_FaultMessage;
import com.fastcase.services._2009._03._06.ResearchServicesStub;
import com.fastcase.services._2009._03._06.ResearchServicesStub.GetLibraries;
import com.fastcase.services._2009._03._06.ResearchServicesStub.GetLibraryDocuments;
import com.fastcase.services._2009._03._06.ResearchServicesStub.GetLibraryDocumentsByCitation;
import com.fastcase.services._2009._03._06.ResearchServicesStub.GetOutlineView;
import com.fastcase.services._2009._03._06.ResearchServicesStub.Library;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocument;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentListItem;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentQuery;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentRequest;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryDocumentUpdate;
import com.fastcase.services._2009._03._06.ResearchServicesStub.LibraryRequest;
import com.fastcase.services._2009._03._06.ResearchServicesStub.ListOfLibrary;
import com.fastcase.services._2009._03._06.ResearchServicesStub.ListOfLibraryDocumentListItem;
import com.fastcase.services._2009._03._06.ResearchServicesStub.ListOfint;
import com.fastcase.services._2009._03._06.ResearchServicesStub.OutlineViewListItem;
import com.fastcase.services._2009._03._06.ResearchServicesStub.OutlineViewRequest;
import com.fastcase.services._2009._03._06.SecurityServicesStub;
import com.fastcase.services._2009._03._06.SecurityServicesStub.AuthenticateServiceAccountContext;
import com.fastcase.services._2009._03._06.SecurityServicesStub.AuthenticateServiceAccountRequest;
import com.thinkgem.jeesite.common.config.Global;
import com.thinkgem.jeesite.common.utils.FileUtils;

public class WBNonCaseUtils {
	public static ResearchServicesStub rs;
	public static ResearchServicesStub.SecurityContext sc = new ResearchServicesStub.SecurityContext();
	
	static {
		if (rs == null) {
			try {
				rs = new ResearchServicesStub(Global.PRODUCT_URL);
				sc.setServiceAccountContext(Global.ACCOUNT_CONTEXT);
			} catch (AxisFault e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
//	private static void trustAllHttpsCertificates() throws Exception {
//		javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
//		javax.net.ssl.TrustManager tm = new miTM();
//		trustAllCerts[0] = tm;
//		javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
//				.getInstance("SSL");
//		sc.init(null, trustAllCerts, null);
//		javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
//				.getSocketFactory());
//	}
//
//	static class miTM implements javax.net.ssl.TrustManager,
//			javax.net.ssl.X509TrustManager {
//		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//			return null;
//		}
//
//		public boolean isServerTrusted(
//				java.security.cert.X509Certificate[] certs) {
//			return true;
//		}
//
//		public boolean isClientTrusted(
//				java.security.cert.X509Certificate[] certs) {
//			return true;
//		}
//
//		public void checkServerTrusted(
//				java.security.cert.X509Certificate[] certs, String authType)
//				throws java.security.cert.CertificateException {
//			return;
//		}
//
//		public void checkClientTrusted(
//				java.security.cert.X509Certificate[] certs, String authType)
//				throws java.security.cert.CertificateException {
//			return;
//		}
//	}


	public static LibraryDocumentListItem[] findSectionByShortName(String shortName, String editionId) throws Exception {
		ResearchServicesStub.SecurityContext sc = new ResearchServicesStub.SecurityContext();
		sc.setServiceAccountContext(Global.ACCOUNT_CONTEXT);
		GetLibraryDocumentsByCitation gc = new GetLibraryDocumentsByCitation();
		LibraryDocumentQuery query = new LibraryDocumentQuery();
		query.setSearchPhrase(shortName);
		query.setLibraryEditionIDList(editionId);
		query.setLibraryType("Statutes");
		query.setSearchType("Citation");
		gc.setQuery(query);
		gc.setContext(sc);
		ListOfLibraryDocumentListItem list = rs
				.getLibraryDocumentsByCitation(gc)
				.getGetLibraryDocumentsByCitationResult().getResult();
		if (list == null) {
			return null;
		} else {
			return list.getLibraryDocumentListItem();
		}
	}

	public static LibraryDocumentListItem findNonCaseByShortName(
			String shortName, String accountContext) throws Exception {
		ResearchServicesStub.SecurityContext sc = new ResearchServicesStub.SecurityContext();
		sc.setServiceAccountContext(accountContext);
		GetLibraryDocumentsByCitation gc = new GetLibraryDocumentsByCitation();
		LibraryDocumentQuery query = new LibraryDocumentQuery();
		query.setSearchPhrase(shortName);
		query.setLibraryEditionIDList("5069");
		query.setLibraryType("Statutes");
		query.setSearchType("Citation");
		gc.setQuery(query);
		gc.setContext(sc);
		ListOfLibraryDocumentListItem loll = rs
				.getLibraryDocumentsByCitation(gc)
				.getGetLibraryDocumentsByCitationResult().getResult();
		if (loll == null || loll.getLibraryDocumentListItem() == null)
			return null;
		for (LibraryDocumentListItem ll : loll.getLibraryDocumentListItem()) {
			// int[] lr={ll.getLibraryDocumentID()};
			// GetLibraryDocuments gc3=new GetLibraryDocuments();
			// gc3.setContext(sc);
			// LibraryDocumentRequest lq=new LibraryDocumentRequest();
			// lq.setIncludeHtml(true);
			// ListOfint lo=new ListOfint();
			// lo.set_int(lr);
			// lq.setLibraryDocumentIDs(lo);
			// lq.setLibraryType("Statutes");
			// gc3.setRequest(lq);
			// GetLibraryDocumentIndexes gi=new GetLibraryDocumentIndexes();
			// gi.setRequest(lq);
			// gi.setContext(sc);
			// System.out.println(rs.getLibraryDocuments(gc3).getGetLibraryDocumentsResult().getResult().getLibraryDocument()[0].getLibraryDocumentHtml());
			return ll;
		}
		return null;
	}

	public static LibraryDocumentListItem[] findNonCaseByShortName(
			String shortName) throws Exception {
		ResearchServicesStub.SecurityContext sc = new ResearchServicesStub.SecurityContext();
		sc.setServiceAccountContext(Global.ACCOUNT_CONTEXT);
		GetLibraryDocumentsByCitation gc = new GetLibraryDocumentsByCitation();
		LibraryDocumentQuery query = new LibraryDocumentQuery();
		query.setSearchPhrase(shortName);
		query.setLibraryEditionIDList("7565");
		query.setLibraryType("Statutes");
		query.setSearchType("Citation");
		gc.setQuery(query);
		gc.setContext(sc);
		ListOfLibraryDocumentListItem list = rs
				.getLibraryDocumentsByCitation(gc)
				.getGetLibraryDocumentsByCitationResult().getResult();
		return list.getLibraryDocumentListItem();
	}

	public static List<LibraryDocumentListItem> findNonCaseByShortNameList(
			String shortName, String editionId) throws Exception {
		List<LibraryDocumentListItem> llList = new ArrayList<LibraryDocumentListItem>();
		GetLibraryDocumentsByCitation gc = new GetLibraryDocumentsByCitation();
		LibraryDocumentQuery query = new LibraryDocumentQuery();
		query.setSearchPhrase(shortName);
		query.setLibraryEditionIDList(editionId);
		query.setLibraryType("Statutes");
		query.setSearchType("Citation");
		gc.setQuery(query);
		gc.setContext(sc);
		ListOfLibraryDocumentListItem loll = rs
				.getLibraryDocumentsByCitation(gc)
				.getGetLibraryDocumentsByCitationResult().getResult();
		if (loll == null || loll.getLibraryDocumentListItem() == null)
			return null;
		for (LibraryDocumentListItem ll : loll.getLibraryDocumentListItem()) {
			llList.add(ll);
		}
		return llList;
	}

	public static OutlineViewListItem[] getStatuteTree(String nodeId ,Integer editionId) throws Exception {
		GetOutlineView gov = new GetOutlineView();
		OutlineViewRequest ovr = new OutlineViewRequest();
		gov.setContext(sc);
		ovr.setLibraryType("Statutes");
		if (nodeId!=null) {
			ovr.setNodeID(Integer.parseInt(nodeId));
		}
		ovr.setViewTypeID(editionId);
		ovr.setViewType("Edition");
		ovr.setViewDirection("descendant");
		gov.setRequest(ovr);
		return rs.getOutlineView(gov).getGetOutlineViewResult().getResult().getOutlineViewListItem();
	}
	

	public static OutlineViewListItem[] getStatuteQATree(String id, String editionId) throws Exception {
		GetOutlineView gov = new GetOutlineView();
		OutlineViewRequest ovr = new OutlineViewRequest();
		gov.setContext(sc);
		ovr.setLibraryType("Statutes");
		if (!StringUtils.isBlank(id)) {
			ovr.setNodeID(Integer.valueOf(id));
		}
		ovr.setViewTypeID(Integer.valueOf(editionId));
		ovr.setViewType("Edition");
		ovr.setViewDirection("descendant");
		gov.setRequest(ovr);
		return rs.getOutlineView(gov).getGetOutlineViewResult().getResult()
				.getOutlineViewListItem();
	}

	/**
	 * @author Sid
	 * @param documentId
	 * @return LibraryDocumentUpdate[]
	 * @throws Exception
	 *             查询Section本身
	 */
	public static LibraryDocument findNonCaseById(String documentId)
			throws Exception {
		GetLibraryDocuments gld = new GetLibraryDocuments();
		LibraryDocumentRequest ldr = new LibraryDocumentRequest();
		gld.setContext(sc);
		int[] lr = { Integer.valueOf(documentId) };
		ListOfint lo = new ListOfint();
		lo.set_int(lr);
		ldr.setLibraryType("Statutes");
		ldr.setIncludeHtml(true);
		ldr.setIncludeUpdates(true);
		ldr.setLibraryDocumentIDs(lo);
		gld.setRequest(ldr);
		return rs.getLibraryDocuments(gld).getGetLibraryDocumentsResult()
				.getResult().getLibraryDocument()[0];
	}

	/**
	 * @author Sid
	 * @param documentId
	 * @return LibraryDocumentUpdate[]
	 * @throws Exception
	 *             查询Section本身
	 */
	public static LibraryDocument[] findNonCaseByIds(int[] lr) throws Exception {
		GetLibraryDocuments gld = new GetLibraryDocuments();
		LibraryDocumentRequest ldr = new LibraryDocumentRequest();
		gld.setContext(sc);
		ListOfint lo = new ListOfint();
		lo.set_int(lr);
		ldr.setLibraryType("Statutes");
		ldr.setIncludeHtml(true);
		ldr.setIncludeUpdates(true);
		ldr.setLibraryDocumentIDs(lo);
		gld.setRequest(ldr);
		return rs.getLibraryDocuments(gld).getGetLibraryDocumentsResult()
				.getResult().getLibraryDocument();
	}
	
	public static JSONArray getLibraries(){
		JSONArray jsonArray = new JSONArray();
		LibraryRequest request = new LibraryRequest();
		request.setLibraryType("Statutes");
		GetLibraries libs = new GetLibraries();
		libs.setContext(sc);
		libs.setRequest(request);
		try {
			ListOfLibrary list = rs.getLibraries(libs).getGetLibrariesResult().getResult();
			Library[] array = list.getLibrary();
			for (Library library : array) {
				JSONObject json = new JSONObject();
				json.put("libraryDescription", library.getLibraryEditionDescription());
				json.put("jurisditionId", library.getJurisdictionID());
				json.put("editionId", library.getLibraryEditionID());
				json.put("libraryName", library.getLibraryName());
				jsonArray.put(json);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (IResearchServices_Basic_GetLibraries_ServiceFaultDataFault_FaultMessage e) {
			e.printStackTrace();
		}
		return jsonArray;
	}

//	public static void getLibraries() {
//		LibraryRequest request = new LibraryRequest();
//		request.setLibraryType("Statutes");
//		GetLibraries libs = new GetLibraries();
//		libs.setContext(sc);
//		libs.setRequest(request);
//		try {
//			ListOfLibrary list = rs.getLibraries(libs).getGetLibrariesResult().getResult();
//			Library[] array = list.getLibrary();
//			for (Library library : array) {
//				
//			}
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		} catch (IResearchServices_Basic_GetLibraries_ServiceFaultDataFault_FaultMessage e) {
//			e.printStackTrace();
//		}
//	}
	
	
	
	public static void main(String[] args) {
		try {
			List<LibraryDocumentListItem> list = findNonCaseByShortNameList("Okla. Stat. tit. 74 Sec. 85.55", "7600");
			System.out.println(list.size());
//			LibraryDocument dom = findNonCaseById("51905013");
//			System.out.println(dom.getIndexCaption());
//			System.out.println(dom.getIndexDescription());
//			System.out.println(dom.getParentLibraryIndexID());
//			System.out.println(dom.getLibraryDocumentHtml());
//			LibraryDocumentUpdate[] updates = dom.getDocumentUpdates().getLibraryDocumentUpdate();
//			for(LibraryDocumentUpdate update: updates){
//				System.out.println("[  "+update.getUpdateOrder()+"  ]");
//				System.out.println(update.getIndexCaption());
//				System.out.println(update.getLibraryDocumentUpdateHtml());
//				System.out.println("****************************************************************");
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Sid
	 * @param documentId
	 * @return LibraryDocumentUpdate[]
	 * @throws Exception
	 *             查询Section下的全部版本
	 */
	public static LibraryDocumentUpdate[] findNonCaseUpdateById(
			String documentId) throws Exception {
		GetLibraryDocuments gld = new GetLibraryDocuments();
		LibraryDocumentRequest ldr = new LibraryDocumentRequest();
		gld.setContext(sc);
		int[] lr = { Integer.valueOf(documentId) };
		ListOfint lo = new ListOfint();
		lo.set_int(lr);
		ldr.setLibraryType("Statutes");
		ldr.setIncludeHtml(true);
		ldr.setLibraryDocumentIDs(lo);
		ldr.setIncludeUpdates(true);
		gld.setRequest(ldr);
		LibraryDocument ld = rs.getLibraryDocuments(gld)
				.getGetLibraryDocumentsResult().getResult()
				.getLibraryDocument()[0];
		LibraryDocumentUpdate[] list = ld.getDocumentUpdates()
				.getLibraryDocumentUpdate();
		return list;
	}

	public static Library[] queryNonCaseUSEdtion() throws Exception {
		GetLibraries gl = new GetLibraries();
		LibraryRequest lrr = new LibraryRequest();
		lrr.setLibraryType("Statutes");
		gl.setRequest(lrr);
		gl.setContext(sc);
		return rs.getLibraries(gl).getGetLibrariesResult().getResult().getLibrary();
	}
}
