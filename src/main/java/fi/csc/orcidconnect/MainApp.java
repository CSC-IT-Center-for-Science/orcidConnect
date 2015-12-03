package fi.csc.orcidconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
public class MainApp {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(MainApp.class);
		app.run(args);
	}
	
	@Bean
	public ServletRegistrationBean appFirst() {
		DispatcherServlet dpServlet = new DispatcherServlet();
		
		WebApplicationContext appCtx = new AnnotationConfigEmbeddedWebApplicationContext();
		dpServlet.setApplicationContext(appCtx);
		
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(
				dpServlet, "/app/*");
		servletRegistrationBean.setName("appfirst");
		System.out.println("----- instantiated" + servletRegistrationBean.getServletName() + "-----");
		return servletRegistrationBean;
	}
	
	@Bean
	public ServletRegistrationBean appSecond() {
		DispatcherServlet dpServlet = new DispatcherServlet();
		
		WebApplicationContext appCtx = new AnnotationConfigEmbeddedWebApplicationContext();
		dpServlet.setApplicationContext(appCtx);
		
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(
				dpServlet, "/bigg/*");
		servletRegistrationBean.setName("appSecond");
		System.out.println("----- instantiated" + servletRegistrationBean.getServletName() + "-----");
		return servletRegistrationBean;
	}

	
}
