package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {
    @GetMapping("hello")    //hello라는 url이오면, 호출해라
    public String hello(Model model) {
        //hello.html안의 -- data에 hello!!로 렌더링해라.
        model.addAttribute("data","hello!!");
        return "hello"; //resources\templates\'hello'.html로 매핑
    }
}
