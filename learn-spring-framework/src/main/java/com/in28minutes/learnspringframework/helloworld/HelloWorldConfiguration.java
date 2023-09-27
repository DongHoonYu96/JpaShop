package com.in28minutes.learnspringframework.helloworld;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

//클래스생성 && getter, setter 자동생성
record Person(String name, int age, Address address) {};
record Address(String firstLine, String city){};

@Configuration
//=> SpringBean(스프링에서 관리해주는해주길 원하는 것들) 정의
public class HelloWorldConfiguration {

    @Bean   //빈 => 관리해줘
    public String name(){
        return "Renga";
    }

    @Bean
    public int age(){
        return 15;
    }

    @Bean
    public Person person(){
        var person=new Person("Ravi",20,new Address("Lacoon","city"));

        return person;
    }

    @Bean   //메소드로 주입
    public Person person2MethodCall(){
        var person=new Person(name(),age(),address2());

        return person;
    }

    @Bean   //파라미터로 주입
    public Person person3Parameters(String name, int age, Address address3){
        var person=new Person(name,age,address3);

        return person;
    }

    @Bean   //파라미터로 주입
    @Primary
    public Person person4Parameters(String name, int age, Address address){
        var person=new Person(name,age,address);

        return person;
    }

    @Bean   //파라미터로 주입                                  //기본값빈이 아니라 해당빈을 받겟다.
    public Person person5Qualifier(String name, int age, @Qualifier("address3qualifier") Address address){
        var person=new Person(name,age,address);

        return person;
    }

    //빈이름 기본값: 메소드이름
    // 임의지정 : name 매개변수 이용
    @Bean(name = "address2")
    @Primary //빈여러개일시 기본값 빈 설정
    public Address address2(){
        var address=new Address("London","manCity");
        return address;
    }

    @Bean(name = "address3")
    @Qualifier("address3qualifier") //Bean간에 특정빈 전달 (시 식별자)
    public Address address3(){
        var address=new Address("London","33333");
        return address;
    }



}
