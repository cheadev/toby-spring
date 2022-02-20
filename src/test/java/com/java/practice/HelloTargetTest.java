package com.java.practice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "file:src/main/resources/test-applicationContext.xml")
class HelloTargetTest {
    @Autowired
    ApplicationContext context;

    @Test
    void simpleProxy() {
        //기본 기능
        Hello hello = new HelloTarget();
        assertEquals("Hello Toby", hello.sayHello("Toby"));
        assertEquals("Hi Toby", hello.sayHi("Toby"));
        assertEquals("Thank You Toby", hello.sayThankYou("Toby"));

        //프록시로 부가기능 적용
        Hello proxy = new HelloUppercase(new HelloTarget());
        assertEquals("HELLO TOBY", proxy.sayHello("Toby"));
        assertEquals("HI TOBY", proxy.sayHi("Toby"));
        assertEquals("THANK YOU TOBY", proxy.sayThankYou("Toby"));
    }

    @Test
    void dynamicProxy() {
        //다이나믹 프록시로 부가기능 적용
        Hello dynamicProxy = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Hello.class},
                new UppercaseHandler(new HelloTarget())
        );
        assertEquals("HELLO TOBY", dynamicProxy.sayHello("Toby"));
        assertEquals("HI TOBY", dynamicProxy.sayHi("Toby"));
        assertEquals("THANK YOU TOBY", dynamicProxy.sayThankYou("Toby"));


        //팩토리빈으로 다이나믹 프록시 생성하기
        Object factoryBean = context.getBean("hello");
        Hello factoryBeanProxy = (Hello) factoryBean;
        assertEquals("HELLO TOBY", factoryBeanProxy.sayHello("Toby"));
        assertEquals("HI TOBY", factoryBeanProxy.sayHi("Toby"));
        assertEquals("THANK YOU TOBY", factoryBeanProxy.sayThankYou("Toby"));


        //프록시팩토리빈으로 다이너믹 프록시 생성하기
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new HelloTarget());
        proxyFactoryBean.addAdvice(new UppercaseAdvice());
        Hello proxyFactoryBeanProxy = (Hello) proxyFactoryBean.getObject();
        assertEquals("HELLO TOBY", proxyFactoryBeanProxy.sayHello("Toby"));
        assertEquals("HI TOBY", proxyFactoryBeanProxy.sayHi("Toby"));
        assertEquals("THANK YOU TOBY", proxyFactoryBeanProxy.sayThankYou("Toby"));
    }

    @Test
    void pointcutAdvisor() {
        //프록시팩토리빈으로 다이너믹 프록시 생성하기
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxyFactoryBeanProxy = (Hello) proxyFactoryBean.getObject();
        assertEquals("HELLO TOBY", proxyFactoryBeanProxy.sayHello("Toby"));
        assertEquals("HI TOBY", proxyFactoryBeanProxy.sayHi("Toby"));
        assertEquals("Thank You Toby", proxyFactoryBeanProxy.sayThankYou("Toby"));
    }

    @Test
    void classNamePointcutAdvisor() {
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    @Override
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT");
                    }
                };
            }
        };
        pointcut.setMappedName("sayH*");

        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(new HelloTarget());
        proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxyFactoryBeanProxy = (Hello) proxyFactoryBean.getObject();

        class HelloWorld extends HelloTarget {}
        class HelloToby extends HelloTarget {}

        checkAdviced(new HelloTarget(), pointcut, true);
        checkAdviced(new HelloWorld(), pointcut, false);
        checkAdviced(new HelloToby(), pointcut, true);
    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(target);
        proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxyFactoryBeanProxy = (Hello) proxyFactoryBean.getObject();

        if(adviced) {
            assertEquals("HELLO TOBY", proxyFactoryBeanProxy.sayHello("Toby"));
            assertEquals("HI TOBY", proxyFactoryBeanProxy.sayHi("Toby"));
            assertEquals("Thank You Toby", proxyFactoryBeanProxy.sayThankYou("Toby"));
        }
        else {
            assertEquals("Hello Toby", proxyFactoryBeanProxy.sayHello("Toby"));
            assertEquals("Hi Toby", proxyFactoryBeanProxy.sayHi("Toby"));
            assertEquals("Thank You Toby", proxyFactoryBeanProxy.sayThankYou("Toby"));
        }
    }
}