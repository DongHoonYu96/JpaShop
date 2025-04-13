package jpabook.jpashop.web;

import jpabook.jpashop.web.argumentresolver.LoginMemberArgumentResolver;
import jpabook.jpashop.web.interceptor.LoginCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@Profile("!local") //로컬 환경에서는 적용하지 않음
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1) //인터셉터의 순서
                .addPathPatterns("/**") //모든 경로에 대해 인터셉터 적용
                .excludePathPatterns("/css/**", "/*.ico",  "/", "/error", "/error/page", "/members/new" ,"/login"); //제외할 경로
    }
}
