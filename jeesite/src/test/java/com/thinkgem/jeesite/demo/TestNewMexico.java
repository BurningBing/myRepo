package com.thinkgem.jeesite.demo;

import java.io.File;
import java.io.FileInputStream;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class TestNewMexico {
	public static void main(String[] args) throws Exception{
		File folder = new File("E:\\Newmexico\\pdf");
		for(File file: folder.listFiles()){
			FileInputStream in = new FileInputStream(file);
			PDFParser parser = new PDFParser(in);
			parser.parse();
			PDDocument dom = parser.getPDDocument();
			PDFTextStripper stripper = new PDFTextStripper();
			String result = stripper.getText(dom);
			System.out.println(result);
			break;
		}
	}
}
