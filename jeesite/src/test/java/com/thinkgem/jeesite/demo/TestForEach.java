package com.thinkgem.jeesite.demo;

public class TestForEach {
	/**
	 * \t相当于按下Tab键, 向后空出一段距离
	 * @param args
	 */
	public static void main(String[] args) {
		for(int x=1;x<10;x++){
			for(int y=1;y<=x;y++){
				System.out.print(y + " X " + x + " = " + x*y + "\t");
			}
			System.out.println();
		}
	}
}
