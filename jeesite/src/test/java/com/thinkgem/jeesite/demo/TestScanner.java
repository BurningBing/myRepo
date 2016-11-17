package com.thinkgem.jeesite.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestScanner {
	private static List<String> books = new ArrayList<String>();
	
	static{
		books.add("java编程核心思想");
		books.add("Android应用基础");
		books.add("Oracle实战应用");
		books.add("HTML 5实训讲解");
	}
	
	public static void main(String[] args) {
		System.out.println("-----图书管理系统-----");
		System.out.println();
		System.out.println("*******************");
		System.out.println("请选择要您要执行的操作：");
		System.out.println("1. 查看所有书籍");
		System.out.println("2. 借出书籍");
		System.out.println("3. 还回书籍");
		System.out.println("4. 添加书籍");
		System.out.println("5. 删除书籍");
		System.out.println("(输入0退出系统)");
		printMenu();
	}
	
	public static void printMenu(){
		Scanner scanner = new Scanner(System.in);
		int choice = scanner.nextInt();
		switch (choice) {
		case 1:
			// 查看书籍列表
			System.out.println("*********书籍列表**********");
			for(int i=0;i<books.size();i++){
				System.out.println((i+1)+".  "+books.get(i));
			}
			System.out.println("*******************");
			System.out.println("请选择要您要执行的操作：");
			System.out.println("1. 查看所有书籍");
			System.out.println("2. 借出书籍");
			System.out.println("3. 还回书籍");
			System.out.println("4. 添加书籍");
			System.out.println("5. 删除书籍");
			System.out.println("(输入0退出系统)");
			printMenu();
			break;
		case 2:
			// 借出书籍
			System.out.println("请选择书籍编号：");
			int no = scanner.nextInt();
			System.out.println("您已借出 "+books.get(no-1));
			books.remove(no-1);
			System.out.println("*******************");
			System.out.println("请选择要您要执行的操作：");
			System.out.println("1. 查看所有书籍");
			System.out.println("2. 借出书籍");
			System.out.println("3. 还回书籍");
			System.out.println("4. 添加书籍");
			System.out.println("5. 删除书籍");
			System.out.println("(输入0退出系统)");
			printMenu();
			break;
		case 3:
			// 还回书籍
			break;
		case 4:
			// 添加书籍
			break;
		case 5: 
			// 删除书籍
			break;
		default:
			if(choice == 0){
				scanner.close();
				System.out.println("退出系统成功,谢谢使用");
			}else{
				System.out.println("输入无效");
			}
		}
	}
	
}
