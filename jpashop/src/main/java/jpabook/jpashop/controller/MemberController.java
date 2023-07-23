package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor    //생성자주입
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new") //membres/new 주소를 치면 여기로 매핑
    public String createForm(Model model){
        model.addAttribute("memberForm",new MemberForm());  //html로 이동할때 MemberForm객체들고감
        return "members/createMemberForm";  //resources/templates/members/createMemberForm.html 열어라!
    }

    @PostMapping("/members/new")    //주소에서 Post로 매핑되면 이거실행
    public String create(@Valid MemberForm form, BindingResult result){   //@Valid => MemberForm의 NotEmpty등 어노테이션 실행 => 이름공백이면 에러!
        //BindingResult => 에러가있으면 여기에담아라.
        //에러가잇으면 여기로가라.
        if(result.hasErrors()){
            return "members/createMemberForm";
        }


        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member=new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);
        return "redirect:/";    //종료되면 첫페이지.html를 열어라

    }

    @GetMapping("/members") //여기주소치면 이거실행해라
    public String list(Model model){
        List<Member> members=memberService.findMembers();
        model.addAttribute("members",members);  //html로 이동할때 이 객체를 들고가라.
        return "members/memberList";
    }
}
