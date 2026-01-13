package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * Web configuration class for Spring MVC
 * Contains bean definitions for web-related components
 */
@Configuration
public class WebConfig {

	/**
	 * Configures MultipartResolver for file uploads
	 * Maximum upload size is set to 10MB (10240000 bytes)
	 * 
	 * @return MultipartResolver instance
	 */
	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(10240000); // 10MB
		multipartResolver.setMaxInMemorySize(4096); // 4KB
		return multipartResolver;
	}
}
