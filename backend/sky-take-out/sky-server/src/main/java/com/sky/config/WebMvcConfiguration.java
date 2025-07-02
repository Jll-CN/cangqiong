package com.sky.config;

import com.sky.interceptor.JwtTokenAdminInterceptor;
import com.sky.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 * 继承 WebMvcConfigurationSupport 父类
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenAdminInterceptor jwtTokenAdminInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenAdminInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/employee/login");
    }

    /**
     * 通过knife4j生成接口文档, 这些都是配置信息，基本所有项目都一样的
     * 为什么放在这个类中，这个类是如何启动呢？
     * @return
     */
    @Bean
    public Docket docket() {
        log.info("准备生成接口文档....");
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("苍穹外卖项目接口文档")
                .version("2.0")
                .description("苍穹外卖项目接口文档")
                .build();
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()

                // 指定接口需要扫描的包，也会扫描包中的包还有方法。
                .apis(RequestHandlerSelectors.basePackage("com.sky.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }

    /**
     * 配置类中，设置静态资源映射
     * 主要是访问接口文档（html, js, css）
     * @param registry
     */
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始设置静态资源映射.....");

        // 接口文档的请求路径就是 localhost:8080/doc.html
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 重写扩展消息转换器, 统一拦截后端发送的消息，转换日期类型
     * 扩展 Spring MVC 框架的消息转换器
     * 拦截后端发送到前端的数据，然后可以对数据进行统一的处理。
     * 比如，进行日期类型的格式化。
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        // 创建消息转换器类
        MappingJackson2HttpMessageConverter converter =  new MappingJackson2HttpMessageConverter();
        // 为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为JSON 数据
        // 对象转换器来自于common中的类
        // 叫做JasksonObject
        converter.setObjectMapper(new JacksonObjectMapper());


        // 创建好消息转换器之后，需要交给框架，让框架使用消息转换器。
        converters.add(0, converter);  //由于Spring MVC 自带一些消息转换器，List添加之后，添加到最后了。默认使用不到的。添加索引表示放在第一位，优秀按使用的。
    }
}
