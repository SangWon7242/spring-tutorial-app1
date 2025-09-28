package com.sbs.tutorial.app1.base.config.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration // @Configuration은 스프링이 이 클래스를 설정 파일로 인식
public class WebMvcConfig implements WebMvcConfigurer {
  @Value("${custom.genFileDirPath}")
  private String genFileDirPath;

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/gen/**")
        .addResourceLocations("file:///" + genFileDirPath + "/");
  }
}
