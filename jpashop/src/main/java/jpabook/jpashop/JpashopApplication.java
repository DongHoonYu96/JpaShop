package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import jpabook.jpashop.aop.Trace.LogTrace;
import jpabook.jpashop.aop.Trace.ThreadLocalLogTrace;
import jpabook.jpashop.config.AopConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@Import(AopConfig.class)
@SpringBootApplication
public class JpashopApplication {
    public static void main(String[] args) {
        SpringApplication.run(JpashopApplication.class, args);
    }

    @Bean
    Hibernate5Module hibernate5Module() {
        return new Hibernate5Module();
    }

    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }
}
