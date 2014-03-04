package org.fao.fenix.d3s.server.tools.spring;

import java.util.Properties;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringContext {
	
	private static boolean initialized;
	private static ApplicationContext context;
	
	
	public static void init(Properties initProperties) throws Exception {
		String filePath = initProperties.getProperty("spring.config.file");
		context = filePath==null ? new ClassPathXmlApplicationContext("org/spring/config/applicationContext.xml") : new FileSystemXmlApplicationContext(filePath);
		initialized = true;
	}
	
	public static ApplicationContext getContext() {
		return initialized ? context : null;
	}
	
	public static <T> T getBean(Class<T> beanClass) {
		return initialized ? context.getBean(beanClass) : null;
	}
	
	public static Object getBean(String beanName) {
		return initialized ? context.getBean(beanName) : null;
	}

}
