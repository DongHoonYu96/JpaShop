package jpabook.jpashop.controller;

import jpabook.jpashop.controller.Form.BookForm;
import jpabook.jpashop.controller.Form.BookSaveForm;
import jpabook.jpashop.controller.Form.BookUpdateForm;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/items/new")   //이주소를 치면 이 함수실행
    public String createForm(Model model) {
        model.addAttribute("form", new BookSaveForm());  //html로 이동시 이거들고가
        return "items/createItemForm";  //해당 html로 이동
    }

    @PostMapping("/items/new")
    public String create(
            @Validated @ModelAttribute("form")BookSaveForm form, //html에서 넘어온 form을 BookForm으로 바꿔서 받음
            BindingResult bindingResult) {
        Book book = Book.builder()
                .name(form.getName())
                .price(form.getPrice())
                .stockQuantity(form.getStockQuantity())
                .author(form.getAuthor())
                .isbn(form.getIsbn())
                .build();

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "items/createItemForm";  //다시 html로 이동
        }

        itemService.saveItem(book);
        return "redirect:/"; //끝나면 홈화면으로 가라
    }

    @GetMapping("/items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit") //변경될수있는값 -> 해결 : {Pathvaliable}, //@PathVariable(매핑대상) 매핑값
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);    //return의 html로 이동시 이거들고가
        return "items/updateItemForm";
    }

    @PostMapping("/items/{itemId}/edit")    //itemId는 html에서 넘어오니까 @PathValiable필요X
    //@ModelAttribue로 html에서 {form}에 해당하는 객체를 받아온다.
    public String updateItem(
            @PathVariable Long itemId,
            @Validated @ModelAttribute("form") BookUpdateForm form,
            BindingResult bindingResult) {
//        Book book = new Book();

        //스마트하게 복붙 : edit-colum selection mode - shift, ctrl로 조정
        //shift+ctrl+u => 대문자로

        //보안취약점 : id조작가능
        //해결 : 유저가 item권한이 있는지 체크해주는 로직추가
//        book.setId(form.getId());
//        book.setName(form.getName());
//        book.setPrice(form.getPrice());
//        book.setStockQuantity(form.getStockQuantity());
//        book.setAuthor(form.getAuthor());
//        book.setIsbn(form.getIsbn());
//        itemService.saveItem(book);

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "items/updateItemForm";  //다시 html로 이동
        }
        //리팩토링
        itemService.updateItem(itemId, form.getName(),form.getPrice(), form.getStockQuantity());

        return "redirect:/items";   //끝나면 목록으로 ㄱㄱ
    }
}
