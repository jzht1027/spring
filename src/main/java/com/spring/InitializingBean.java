package com.spring;
/**
 * @Description: spring
 * @Date: 2022/3/10 18:00
 * @param:
 * @return:
 **/
public interface InitializingBean {
    void afterPropertiesSet() throws Exception;
}
