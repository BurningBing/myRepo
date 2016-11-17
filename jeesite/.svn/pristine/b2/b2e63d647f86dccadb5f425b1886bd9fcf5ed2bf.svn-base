package com.thinkgem.jeesite.common.utils;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.thinkgem.jeesite.common.service.CrudService;

public class SpringBeanUtils {
	public static CrudService<?,?> getBean(String beanId)
	{
		WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
		return (CrudService<?, ?>) webApplicationContext.getBean(beanId);
	}
}
