package com.thinkgem.jeesite.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class DownloadUtils {
	private static int count;

	public static Document getConnectPage(String url,String charset){
		Document dom = null;
		url = RegexUtil.replace(" ", "%20", url);
		try {
			dom = Jsoup.parse(new URL(url).openStream(), charset, url);
			if(count!=0){
				count=0;
			}
		} catch (Exception e) {
			count++;
			if(count==5){
				count=0;
				return null;
			}else{
				return getConnectPage(url,charset);
			}
		}
		return dom;
	}
	
	public static String getAtsDownloadPath(String state){
		String systemName=System.getProperty("os.name");
		String path ="";
		if(StringUtils.isNotBlank(RegexUtil.match("Win", systemName)))
			path = "C:/ATS/Download/"+state+"/"+DateUtils.getTodayString();
		else
			path = "/opt/ATS/Download/"+state+"/"+DateUtils.getTodayString();
		File folder = new File(path);
		if(!folder.isDirectory()){
			folder.mkdirs();
		}
		return path;
	}
	
	/**
	 * @author Chad
	 * @param url 链接地址
	 * @param directory 文件存放路径
	 * @param fileName 文件名称
	 * @throws IOException
	 * @time 2014-11-12 13:52:03
	 */
	public static File downlaodPdf(String url,String directory,String fileName) throws IOException{
		File folder = new File(directory);
		if(!folder.exists()){
			folder.mkdirs();
		}
		File file = new File(directory+File.separator+fileName+".pdf");
		
		file.createNewFile();
		URL siteUrl = new URL(url);
		URLConnection myurlcon = siteUrl.openConnection();
		myurlcon.setConnectTimeout(100000);
		myurlcon.setReadTimeout(100000);
		InputStream is  = 	myurlcon.getInputStream();
		FileOutputStream fos = new FileOutputStream(file);
		byte[] bytes = new byte[1024];
		int offset = 0;
		while ((offset = is.read(bytes)) != -1) {
			fos.write(bytes, 0, offset);
		}
		fos.flush();
		fos.close();
		is.close();
		
		return file;
	}
	
	public static void trustEveryone() throws NoSuchAlgorithmException, KeyManagementException{
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(String hostname, SSLSession session) {
				return false;
			}
		});

			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { new X509TrustManager() {  
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
                }  
  
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {  
                }  
  
                public X509Certificate[] getAcceptedIssuers() {  
                    return new X509Certificate[0];  
                }  
            } }, new SecureRandom());  
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());  
	}
	
	public static Document getDocument(URL url,int timeout) throws KeyManagementException, NoSuchAlgorithmException, IOException{
        trustEveryone();  
       return  Jsoup.parse(url, 10000);  

	}
	
}
