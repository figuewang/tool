package cn.com.yusys.yusp.tool.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import cn.com.yusys.yusp.tool.web.fillter.AMRequestFilter;

@Configuration
public class FilterConfiguration {
	@Bean
	public AMRequestFilter getAMRequestFilter(){
		return new AMRequestFilter();
	}
}
