package com.spring;

/**
 * @ClassName BeanDefinition
 * @Description
 * @Author
 * @Date 2022/3/7 18:09
 * @Version 1.0
 **/
public class BeanDefinition {
    private Class Clazz;
    private String scope;



    public Class getClazz() {
        return Clazz;
    }

    public void setClazz(Class clazz) {
        Clazz = clazz;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
