package com.thinkgem.jeesite.modules.utils;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.common.websocket.GlobalHandler;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;

public class DownloadThread implements Runnable{

	private String billNumber;
	private String url;
	private String filepath;
	private final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
	
	public DownloadThread(String billNumber, String url, String filepath) {
		this.billNumber = billNumber;
		this.url = url;
		this.filepath = filepath;
	}

	@Override
	public void run() {
		try {
			System.out.println("Begin download "+this.billNumber);
			Document dom = Jsoup.connect(this.url).userAgent(userAgent).timeout(10000).get();
			String html = dom.select(".content-sidebar-wrap").first().html();
			html = RegexUtil.replace("‑", "-", html);
			html = RegexUtil.replace("<style[^>]*?>[\\s\\S]*?</style>", "", html);
			FileUtils.writeStringToFile(new File(this.filepath+"\\"+this.billNumber+".html"), html, "utf-8");
			GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : "+ billNumber+" download success...");
		} catch (IOException e) {
			GlobalHandler.invokeJsMethod(UserUtils.getUser().getName(), "checkUpdateLog","["+DateUtils.getDateTime()+"] : "+billNumber+" download failed");
		}
	}
	
}
