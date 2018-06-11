package edu.steam.cms.config;

import java.io.File;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.Ordered;
import org.springframework.http.CacheControl;
import org.springframework.web.accept.PathExtensionContentNegotiationStrategy;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

 

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    /**
     * 정적 리소스 위치
     */
    @Value("${spring.resources.static-locations}")
    private String staticResourceLocation;
    
    /**
     * URI 
     */
    @Value("upload.base.location")
    private String uploadBaseLocation;
    
    @Override 
    public void addCorsMappings(CorsRegistry registry) { 
    	registry.addMapping("/**"); 
    }
    
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }
    
    @Bean
    public MultipartResolver multipartResolver(){
        CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
        commonsMultipartResolver.setDefaultEncoding("utf-8");
        commonsMultipartResolver.setMaxUploadSize(20000000);
        commonsMultipartResolver.setResolveLazily(false);
        return commonsMultipartResolver;
    }
    
    @Bean
    @Description("Thymeleaf template resolver serving HTML 5")
    public ClassLoaderTemplateResolver templateResolver() {
        
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");        
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");
        
        return templateResolver;
    }

    @Bean
    @Description("Thymeleaf template engine with Spring integration")
    public SpringTemplateEngine templateEngine() {
        
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(templateResolver());

        return templateEngine;
    }

    /**
     * JSP 뷰 리졸버 설정
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        
    	ThymeleafViewResolver tmplResolver = new ThymeleafViewResolver();
        
    	tmplResolver.setTemplateEngine(templateEngine());
    	tmplResolver.setCharacterEncoding("UTF-8");
    	tmplResolver.setOrder(Ordered.LOWEST_PRECEDENCE);
    	
    	registry.viewResolver(tmplResolver);
    	
    	registry.enableContentNegotiation(new MappingJackson2JsonView());
    	
    }

    /**
     * 정적 리소스 패스 지정
     * @param registry
     */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

    	registry
	        .addResourceHandler("/**")
	        .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
	        .addResourceLocations(staticResourceLocation);

    	registry
	        .addResourceHandler("/service-worker.js")
	        .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
	        .addResourceLocations(staticResourceLocation + "service-worker.js");

    	registry
	        .addResourceHandler("/index.html")
	        .setCacheControl(CacheControl.noCache())
	        .addResourceLocations(staticResourceLocation + "index.html");
    	
    	registry.addResourceHandler("/contents/**")
	        .setCacheControl(CacheControl.noCache())
	        .addResourceLocations("file:///d:/myWorkspace/contents/");
    	System.out.println(new File(uploadBaseLocation).toURI().toString());
    	//
    	super.addResourceHandlers(registry);
    }
}
