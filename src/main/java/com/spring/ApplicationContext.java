package com.spring;


import org.springframework.context.annotation.Bean;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ApplicationContestTest
 * @Description spring 容器
 * @Author
 * @Date 2022/3/4 16:03
 * @Version 1.0
 **/
public class ApplicationContext {
    private Class configClass;

    private ConcurrentHashMap<String,Object> singletonObjects = new ConcurrentHashMap<>(); //单例池（单例模式）
    private ConcurrentHashMap<String,BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private List<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();
    private List<Object> aspectList = new ArrayList<>();

    public ApplicationContext(Class configClass) {
        this.configClass = configClass; //配置类
        //解析配置类
        //ComponentScan注解-->扫描路径-->扫描-->BeanDefinition-->beanDefinitionMap
        scan(configClass);
    }

    private void scan(Class configClass) {
        // （1）获取componentScan 注解
        ComponentScan componentScan = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScan.value();//扫描路径 com.model.service
        System.out.println(path);
        path = path.replace(".","/");
        //(2) 获取扫描路径下的带有component注解的类
        // 获取扫描路径下的类

        /* 三款内加载器，对应不同路径下的jar包和类
           Bootstrap---->jre/lib
           Ext---------->jre/ext/lib
           APP---------->classpath
         **/
        ClassLoader classLoader = ApplicationContext.class.getClassLoader(); //app应用内加载器
        URL resource = classLoader.getResource(path); // path 是 classpath 的相对目录：com.model.service，得到的目录；
        //通过目录获取目录下的文件
        File file = new File(resource.getFile());
        if (file.isDirectory()) {
            File[] files = file.listFiles();// 获取目录下的所有文件
            for (File f : files) {
                String fileName = f.getAbsolutePath();
                if (fileName.endsWith(".class")) {
                    String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                    className = className.replace("\\",".");
                    try {
                        Class<?> clazz = classLoader.loadClass(className);
                        if (clazz.isAnnotationPresent(Component.class)) {//扫描Bean对象
                            //表示当前这个类是一个Bean
                            // 解析类，判断是单例Bean，还是原型Bean
                            //BeanDefinition

                            Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                            String beanName = componentAnnotation.value();

                            BeanDefinition beanDefinition = new BeanDefinition();
                            beanDefinition.setClazz(clazz);
                            if (clazz.isAnnotationPresent(Scope.class)) {
                                Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                                beanDefinition.setScope(scopeAnnotation.value());
                            }else{
                                beanDefinition.setScope("singleton");
                                //单例Bean 存放到单例池中
                                Object bean = creatBean(beanName,beanDefinition);
                                singletonObjects.put(beanName,bean);
                            }
                            beanDefinitionMap.put(beanName,beanDefinition);

                            //判断是否为BeanPostProcessor，加入到list中
                            if (BeanPostProcessor.class.isAssignableFrom(clazz)) {
                                BeanPostProcessor bean = (BeanPostProcessor) getBean(beanName);
//                              BeanPostProcessor bean = (BeanPostProcessor) clazz.getDeclaredConstructor().newInstance();
                                beanPostProcessorList.add(bean);
                            }

                            if (clazz.isAnnotationPresent(Aspect.class)) {
                                Aspect aspect = clazz.getDeclaredAnnotation(Aspect.class);
                                Object object = getBean(beanName);
                                aspectList.add(object);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public Object creatBean(String BeanName,BeanDefinition beanDefinition){
        Class clazz =  beanDefinition.getClazz();
        Object instance = null;
        try {
            //实例化对象
            instance = clazz.getDeclaredConstructor().newInstance();
            //依赖注入
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (declaredField.isAnnotationPresent(Autowired.class)) {
                    Object bean = getBean(declaredField.getName());
                    if(bean == null){
                        throw new NullPointerException("");
                    }
                    declaredField.setAccessible(true);
                    declaredField.set(instance,bean);
                }
            }

            //Aware 回调
            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(BeanName);
            }

            //初始化之前调用BeanPostProcessor
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessBeforeInitialization(instance, BeanName);
            }

            //初始化
            if (instance instanceof InitializingBean) {
                try {
                    ((InitializingBean) instance).afterPropertiesSet();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            //初始化之后调用BeanPostProcessor
            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessAfterInitialization(instance, BeanName);
            }

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return instance;
    }

    //获取Bean对象
    public Object getBean(String BeanName){

        if (beanDefinitionMap.containsKey(BeanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(BeanName);
            if (beanDefinition.getScope().equals("singleton")) {
                //单例Bean是直接从map中获取，不会重复创建对象
                Object o = singletonObjects.get(BeanName);
                return o;
            }else{
                //原型Bean，每次请求都重新创建Bean
                Object o = creatBean(BeanName,beanDefinition);
                return o;
            }
        }else{
            //不存在对应的Bean
            throw new RuntimeException("不存在对应的Bean");
        }
    }
}
