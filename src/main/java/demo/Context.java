package demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Context {
	public static ApplicationContext context = new ClassPathXmlApplicationContext("gameContext.xml");
}
