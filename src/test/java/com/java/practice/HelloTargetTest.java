package com.java.practice;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.*;

class HelloTargetTest {

    @Test
    void simpleProxy() {
        Hello hello = new HelloTarget();
        assertEquals("Hello Toby", hello.sayHello("Toby"));
        assertEquals("Hi Toby", hello.sayHi("Toby"));
        assertEquals("Thank You Toby", hello.sayThankYou("Toby"));

        Hello proxy = new HelloUppercase(new HelloTarget());
        assertEquals("HELLO TOBY", proxy.sayHello("Toby"));
        assertEquals("HI TOBY", proxy.sayHi("Toby"));
        assertEquals("THANK YOU TOBY", proxy.sayThankYou("Toby"));

        Hello dynamicProxy = (Hello) Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {Hello.class},
                new UppercaseHandler(new HelloTarget())
        );
        assertEquals("HELLO TOBY", dynamicProxy.sayHello("Toby"));
        assertEquals("HI TOBY", dynamicProxy.sayHi("Toby"));
        assertEquals("THANK YOU TOBY", dynamicProxy.sayThankYou("Toby"));
    }
}