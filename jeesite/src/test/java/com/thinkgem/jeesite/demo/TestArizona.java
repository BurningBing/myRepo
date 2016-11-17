package com.thinkgem.jeesite.demo;

import java.io.File;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;

public class TestArizona {
	public static void main(String[] args) throws IOException {
		String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
		String url = "http://www.azleg.gov/sessionlaws/";
		Document dom = Jsoup.connect(url).userAgent(userAgent).timeout(10000).get();
		Element table = dom.getElementById("chaptered");
		Elements trs = table.select("tr");
		
		for(int i=1;i<trs.size();i++){
			Element tr = trs.get(i);
			String billNumber = tr.select("td").get(2).text();
			String href = tr.select("td").first().select("a").first().absUrl("href");
			href = href.replace(".pdf", ".htm");
			href = "http://www.azleg.gov/viewDocument/?docName="+href;
			dom = Jsoup.connect(href).userAgent(userAgent).timeout(10000).get();
			String html = dom.body().html();
			html = RegexUtil.replace("<script[^>]*?>[\\s\\S]*?</script>", "", html);
			html = RegexUtil.replace("<link[^>]*?>[\\s\\S]*?</link>", "", html);
			html = RegexUtil.replace("<form[]", "", html);
			System.out.println(html);
			break;
//			MyThread thread = new MyThread(href, billNumber);
//			new Thread(thread).start();
//			while(Thread.activeCount()==10){
//				try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
		}
		
	}
}

class MyThread implements Runnable{
	private String url;
	private String billNumber;
	private final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
	public MyThread(String url, String billNumber) {
		this.url = url;
		this.billNumber = billNumber;
	}
	
	@Override
	public void run() {
		System.out.println("开始下载"+this.billNumber);
		try {
			Document dom = Jsoup.connect(this.url).userAgent(userAgent).timeout(10000).get();
			String html = dom.html();
			html = RegexUtil.replace("", "", html);
			FileUtils.write(new File("E:\\Downlaod\\Arizona\\"+this.billNumber+".html"), dom.html(), "UTF-8");
		} catch (IOException e) {
			System.err.println(this.billNumber+"下载失败");
		}
	}
}

