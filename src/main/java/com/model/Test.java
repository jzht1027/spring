package com.model;

import com.model.service.AspectService;
import com.model.service.UserService;
import com.model.service.UserServiceImpl;
import com.spring.ApplicationContext;
import org.springframework.scheduling.annotation.Async;

/**
 * @ClassName Test
 * @Description
 *   SPring Bean的生命周期：
 *   class(UserService)
 *   --->推断构造方法：
 *      （1）默认使用无参构造方法
 *      （2）当只有一个有参构造方法，spring启动时，使用有参构造方法，参数是从spring容器中获取 【参数通过byType ,byName获取对象】；
 *      （3）当有多个带有有参构造方法，spring启动时会报错，需要通过@Autowired指定默认的构造方法；
 *   ---->实例化
 *   ---->生成对象
 *   --->属性填充（对象属性赋值）
 *   ---->初始化：（1）afterPropertiesSet(类实现了InitializingBean接口，spring会调用afterPropertiesSet()方法中编写需要完成的任务)
 *              （2）在自定义的方法上使用注解@PostConstruct，在属性填充后可达到相同的效果
 *   --->AOP（spring找到缓存中所有的切面对象@Aspect）
 *   --->代理对象
 *   ---->bean
 * @Author
 * @Date 2022/3/4 16:16
 * @Version 1.0
 **/
public class Test {
    public static void main(String[] args){
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
//        AspectService aspectService = (AspectService) applicationContext.getBean("aspectService");
//        aspectService.aspectBefor();
        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.test(); //1.代理对象   2.业务test

    }
}
