package com.study.board.controller;

import com.study.board.entity.Board;
import com.study.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/")
    public String home(Model model,                //결과들을 pageList화 하고 정렬시켜준다.
                       @PageableDefault(page = 0,size=10,sort="id",direction = Sort.Direction.DESC) Pageable pageable,
                       String searchKeyword){

        Page<Board> list=null;

        //전체검색
        list=boardService.boardList(pageable);



        //페이지 선택 번호칸이 10개씩 나오도록 구현 ( 1 2 3 4 5 6 7 8 9 10)
        int nowPage=list.getPageable().getPageNumber()+1;   //0번부터시작이므로
        int startPage=Math.max(nowPage-4,1);
        int endPage=Math.min(nowPage+5,list.getTotalPages());

        model.addAttribute("list",list);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);

        //1. boardList메소드에 리턴된값을 model에담아서
        //2.list라는 이름으로 html에 넘긴다
        //model.addAttribute("list",boardService.boardList(pageable));

        return "boardlist.html";
    }

    @GetMapping("/board/write") //lcalhost:8080/board/write 에 접속하면 실행
    public String boardWritingForm(){

        return "boardwrite.html";
    }

    @PostMapping("/board/writepro")
    //인자 : html-form의 name들
    //문제 : 인자가 너무많음
    //엔티티 클래스로 받을 수 있음
    public String boardWritePro(Board board, Model model, MultipartFile file) throws Exception {

        boardService.write(board,file);

        model.addAttribute("message","글 작성이 완료되었습니다.");
        model.addAttribute("searchUrl","/board/list");

        return "message.html";
    }

    @GetMapping("/board/list")
    public String boardList(Model model,                //결과들을 pageList화 하고 정렬시켜준다.
                            @PageableDefault(page = 0,size=10,sort="id",direction = Sort.Direction.DESC) Pageable pageable,
                            String searchKeyword){

        Page<Board> list=null;

        //전체검색
        if(searchKeyword==null){
             list=boardService.boardList(pageable);
        }
        else{  // 키워드검색
            list=boardService.boardSearchList(searchKeyword,pageable);
        }



        //페이지 선택 번호칸이 10개씩 나오도록 구현 ( 1 2 3 4 5 6 7 8 9 10)
        int nowPage=list.getPageable().getPageNumber()+1;   //0번부터시작이므로
        int startPage=Math.max(nowPage-4,1);
        int endPage=Math.min(nowPage+5,list.getTotalPages());

        model.addAttribute("list",list);
        model.addAttribute("nowPage",nowPage);
        model.addAttribute("startPage",startPage);
        model.addAttribute("endPage",endPage);

        //1. boardList메소드에 리턴된값을 model에담아서
        //2.list라는 이름으로 html에 넘긴다
        //model.addAttribute("list",boardService.boardList(pageable));

        return "boardlist.html";
    }

    @GetMapping("board/view")
    public String boardView(Model model, Integer id){   //view?id=1 하면 1이 id에 들어감.

        model.addAttribute("board", boardService.boardView(id));

        return "boardview";
    }

    @GetMapping("board/delete")
    public String boardDelete(Integer id){   //view?id=1 하면 1이 id에 들어감.

        boardService.boardDelete(id);

        return "redirect:/board/list";
    }

    //{id} 부분이 매개변수의 id로 매핑되게
    @GetMapping("board/modify/{id}")
    public String boardModify(@PathVariable("id") Integer id, Model model){

        //특정게시글을 가지고 html로 가라.
        model.addAttribute("board", boardService.boardView(id));

        return "boardmodify.html";
    }

    @PostMapping("board/update/{id}")
    public String boardUpdate(@PathVariable("id") Integer id,Board board,MultipartFile file) throws Exception {

        Board boardtemp=boardService.boardView(id);
        boardtemp.setTitle(board.getTitle());
        boardtemp.setContent(board.getContent());

        boardService.write(boardtemp,file);

        return "redirect:/board/list";
    }
}
