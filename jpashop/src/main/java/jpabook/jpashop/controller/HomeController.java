package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
@Slf4j  //로그찍기위해
public class HomeController {

    @RequestMapping("/")    //기본화면으로 매핑해라
    public String home(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            Model model) {

        if (loginMember == null) {  //로그인 안한사람
            return "home";  //home.html 열어라
        }

        model.addAttribute("member", loginMember);  //로그인한사람
        return "loginHome";
    }
}
