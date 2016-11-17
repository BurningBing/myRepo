package com.thinkgem.jeesite.modules.ats.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.activiti.engine.impl.util.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.thinkgem.jeesite.common.utils.DateUtils;
import com.thinkgem.jeesite.common.utils.FileUtils;
import com.thinkgem.jeesite.common.utils.RegexUtil;
import com.thinkgem.jeesite.modules.ats.entity.AtsAct;
import com.thinkgem.jeesite.modules.ats.entity.AtsTask;

public class WestVirginia extends BaseStateUtils {

	public WestVirginia() {
		super();
		// TODO Auto-generated constructor stub
	}

	public WestVirginia(String state) {
		super(state);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception{
		File file = new File("e:/HB 117.html");
		String html = FileUtils.readFileToString(file);
		html = RegexUtil.replace("<p[^>]*?>", "<p>", html);
		html = RegexUtil.replace(" <p>&nbsp;</p> ", "", html);
		String eff = RegexUtil.match("\\[.*?\\]", html);
		System.out.println(eff);
		eff = RegexUtil.match("(?<=Passed\\W).*?(?=\\;)", eff);
		if(!eff.isEmpty()){
			eff = DateUtils.changeDateFormat(eff);
		}
		List<String> list = RegexUtil.matchAllList("<p><b><mark>&#167[\\s\\S]*?(?=<p><b><mark>&#167|$)", html);
		for(String str : list){
			str = RegexUtil.replace("</?div[^>]*?>", "", str);
			str = RegexUtil.replace("<br[^>]*?>", "", str);
			String caption = RegexUtil.match("(?<=<mark>).*?(?=</mark>)", str);
			str = RegexUtil.replace("<b>.*?</b>", "", str);
			str = RegexUtil.replace("<p>\\s*?</p>", "", str);

			break;
		}

	}

	@Override
	public void doCheckingUpdate(AtsTask task) {
		Document dom = null;
		try {
			dom = Jsoup.connect(task.getUrl()).timeout(10000).get();
		} catch (IOException e) {
			count = -1;
			return;
		}
		Elements list = dom.select("table[class=tabborder]").select("tr");
		for (int i = 1; i < list.size(); i++) {
			Element act = list.get(i);
			if (!RegexUtil.isFind("Vetoed", act.child(3).text())) {
				Element anchor = act.select("a").first();
				String billNumber = anchor.text();
				billNumber = RegexUtil.replace("\\s\\(.*?$", "", billNumber);
				String href = anchor.absUrl("href");
				if (!allActs.contains(href)) {
					System.out.println(billNumber);

					try {
						dom = Jsoup.connect(href).timeout(10000).get();
					} catch (IOException e) {
						continue;
					}
					Element tdElement = dom
							.select("td:contains(Final Version)").get(0)
							.select("a").first();
					String html;
					try {
						html = Jsoup.connect(tdElement.absUrl("href"))
								.timeout(10000).get().getElementById("wrapper")
								.html();
					} catch (IOException e) {
						continue;
					}
					html = RegexUtil.replace("<span[^>]*?>(A|@)</span>", "\"",
							html);
					html = RegexUtil.replace("<span[^>]*?>\'</span>", "&#167;",
							html);
					html = RegexUtil.replace("<span[^>]*?>|</span>", "", html);
					html = RegexUtil.replace("§", "&#167;", html);
					html = RegexUtil.replace("^[\\s\\S]*?(?=<style)", "", html);
					html = RegexUtil.replace("“|”", "\"", html);
					html = RegexUtil.replace("‘|’", "'", html);
					html = RegexUtil.replace("‑", "-", html);
					html = RegexUtil.replace("–", "-", html);
					html = RegexUtil.replace("—", "-", html);
					html = RegexUtil.replace("</b><b>", "", html);
					html = RegexUtil.replace("(&#167;[\\d-\\w]*)",
							"<mark>$1</mark>", html);
					String eff = RegexUtil.match("\\d/\\d+/\\d+", act.child(2)
							.text());
					eff = RegexUtil.replace("(\\d+$)", "20$1", eff);
					FileUtils.writeToFile(path + File.separator + billNumber
							+ ".html", html, "utf-8", false);
					File file = new File(path + File.separator + billNumber
							+ ".html");
					AtsAct temp = new AtsAct();
					temp.setBillNumber(billNumber);
					temp.setState("WestVirginia");
					temp.setFileSize(file.length() / 1024);
					temp.setType(2);
					temp.setSessionYear("2016");
					temp.setDay(DateUtils.getDate("yyyyMMdd"));
					temp.setWorkMode(1);
					temp.setDownloadFile(file.getAbsolutePath());
					temp.setEffectiveDate(eff);
					temp.setUrl(href);
					temp.setRemark("");
					atsActService.save(temp);
					count++;
					allActs.add(href);
				}
			}
		}

	}

	@Override
	public JSONObject doParseAct(AtsAct act) {
		// TODO Auto-generated method stub
		return null;
	}
}
