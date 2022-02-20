package com.java.practice;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

public class HelloFactoryBean implements FactoryBean {
    @Override
    public Object getObject() throws Exception {
        Hello javaProxy = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Hello.class},
                new UppercaseHandler(new HelloTarget())
        );
        return javaProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return Hello.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
