package com.model.service;

import com.spring.Aspect;
import com.spring.Before;
import com.spring.Component;

/**
 * @ClassName Util
 * @Description
 * @Author
 * @Date 2022/3/4 17:26
 * @Version 1.0
 **/
@Aspect
@Component("aspectService")
public class AspectService {

    @Before("execution(public void com.model.service.UserService.test())")
    public void aspectBefor(){
        System.out.println("aspectBefor");
    }


}
