package com.model.service;

import com.spring.*;

/**
 * @ClassName userService
 * @Description
 * @Author
 * @Date 2022/3/4 16:23
 * @Version 1.0
 **/
@Component("userService")
@Scope("prototype")
public class UserServiceImpl implements UserService {

    @Autowired
    private AspectService aspectService;

    private String beanName;

    private String name;

//    @Override
//    public void setBeanName(String name) {
//        beanName = name;
//    }

    public void setName(String name) {
        this.name = name;
    }

//    @Override
//    public void afterPropertiesSet() throws Exception {
//        System.out.println("初始化");
//    }

    @Override
    public void test(){
        System.out.println(aspectService);
        System.out.println(name);
    }

}
