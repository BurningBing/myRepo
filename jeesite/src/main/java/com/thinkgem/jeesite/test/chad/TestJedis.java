package com.thinkgem.jeesite.test.chad;

import com.thinkgem.jeesite.common.utils.JedisUtils;

public class TestJedis {
	public static void main(String[] args) {
		String ret = JedisUtils.set("name", "Chad", 0);
		System.out.println(ret);
	}
}
