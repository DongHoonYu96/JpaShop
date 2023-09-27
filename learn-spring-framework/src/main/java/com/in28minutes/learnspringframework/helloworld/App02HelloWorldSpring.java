package com.in28minutes.learnspringframework.helloworld;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

public class App02HelloWorldSpring {

    public static void main(String[] args) {
        //1. 스프링 콘택스트 런치
        //JVM 내에 Spring 콘텍스트 설치함. using config.class
        try (var context = new AnnotationConfigApplicationContext(HelloWorldConfiguration.class)) {

            //Spring이 관리하는 빈 검색
            System.out.println(context.getBean("name"));
            System.out.println(context.getBean("age"));
            System.out.println(context.getBean("person"));
            System.out.println(context.getBean("person2MethodCall"));
            System.out.println(context.getBean("person3Parameters"));
            System.out.println(context.getBean("person5Qualifier"));

            System.out.println(context.getBean("address2"));
            System.out.println(context.getBean(Person.class));
            System.out.println(context.getBean(Address.class));

            //모든 빈 출력
            Arrays.stream(context.getBeanDefinitionNames()).forEach(System.out::println);
        }


    }
}
