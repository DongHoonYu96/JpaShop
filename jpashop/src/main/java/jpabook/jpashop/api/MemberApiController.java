package jpabook.jpashop.api;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController //data를 바로 json,xml으로 보냄
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    //배열리턴X, DTO 리턴 => 변수추가등 확장성 up
    //API 필요한정보(name)만 노출 => 보안 up
    @GetMapping("api/v2/members")
    public Result membersV2() {
        List<Member> findMembers = memberService.findMembers();
        //1.memberList 를 stream 돌리기
        //2.member엔티티(m)에서 이름꺼내서 Dto에 넣기
        //3.맵 =>조건에맞는 스트림으로 바꿔치기
        //4.리스트로 바꾸기
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result(collect.size(), collect);
    }

    @Data
    @AllArgsConstructor
    private static class Result<T> {
        private int count;
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(
            @RequestBody @Valid Member member) {//json 데이터를 member로 바꿔줘, Member클래스로가서 @들 검증해줘
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }
    //문제 : 외부에 엔티티 파라미터 노출은 위험, member 변수명 변경시 api 다 터짐

    //해결 : DTO
    // member 변수명 변경시 컴파일 오류로 알려줌
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(
            @RequestBody @Valid CreateMemberRequest request) {//json 데이터를 member로 바꿔줘, Member클래스로가서 @들 검증해줘
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //수정은 put매핑
    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        memberService.update(id, request.getName());
        Member findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }


    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty
        private String name;
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    //DTO에는 롬복 어노에티션 막써도됨(큰로직없으므로) / 엔티티에는 getter정도만 사용
    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }


}
