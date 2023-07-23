package jpabook.jpashop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j  //로그찍기위해
public class HomeController {

    @RequestMapping("/")    //기본화면으로 매핑해라
    public String home(){
        log.info("home controller");
        return "home" ;  //home.html 열어라
    }
}
