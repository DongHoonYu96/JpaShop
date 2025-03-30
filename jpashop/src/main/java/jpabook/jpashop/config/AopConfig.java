package jpabook.jpashop.config;

import jpabook.jpashop.aop.LogTraceAspect;
import jpabook.jpashop.aop.Trace.LogTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AopConfig {

    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }
}
