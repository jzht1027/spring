package com.model;

import com.spring.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @ClassName AppConfig
 * @Description 配置类
 * @Author
 * @Date 2022/3/4 16:17
 * @Version 1.0
 **/
@ComponentScan("com.model.service")
@EnableAspectJAutoProxy
public class AppConfig {

}
