package com.model.service;

import com.spring.BeanPostProcessor;
import com.spring.Component;
import org.springframework.cglib.proxy.InvocationHandler;
import org.springframework.cglib.proxy.Proxy;

import java.lang.reflect.Method;


/**
 * @ClassName BeanPostProcessorModer
 * @Description
 * @Author
 * @Date 2022/3/9 15:43
 * @Version 1.0
 **/
@Component
public class BeanPostProcessorModel implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("初始化前"+beanName);
        if ("userService".equals(beanName)) {
            ((UserServiceImpl)bean).setName("BeiJing");
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("初始化后");
        if ("userService".equals(beanName)) {
            Object proxyInstance = Proxy.newProxyInstance(bean.getClass().getClassLoader(),
                    bean.getClass().getInterfaces(), new InvocationHandler() {
                        @Override
                        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                            System.out.println("代理逻辑"+beanName);
                            return method.invoke(bean,objects);
                        }
                    });
            return proxyInstance;
        }
        return bean;
    }
}
