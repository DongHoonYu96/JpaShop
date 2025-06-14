package jpabook.jpashop.controller;

import jpabook.jpashop.controller.Form.BookForm;
import jpabook.jpashop.controller.Form.BookSaveForm;
import jpabook.jpashop.controller.Form.BookUpdateForm;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.domain.item.UploadFile;
import jpabook.jpashop.service.ItemService;
import jpabook.jpashop.util.FileStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final FileStore fileStore;

    @GetMapping("/items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookSaveForm());  //html로 이동시 이거들고가
        return "items/createItemForm";  //해당 html로 이동
    }

    @PostMapping("/items/new")
    public String create(
            @Validated @ModelAttribute("form")BookSaveForm form, //html에서 넘어온 form을 BookForm으로 바꿔서 받음
            BindingResult bindingResult) throws IOException {
        Book book = Book.builder()
                .name(form.getName())
                .price(form.getPrice())
                .stockQuantity(form.getStockQuantity())
                .author(form.getAuthor())
                .isbn(form.getIsbn())
                .build();

        List<UploadFile> storeImageFiles = fileStore.storeFiles(form.getImageFiles());
        book.setImages(storeImageFiles);

        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "items/createItemForm";  //다시 html로 이동
        }

        itemService.saveItem(book);
        return "redirect:/"; //끝나면 홈화면으로 가라
    }

    @GetMapping("/items")
    public String list(@RequestParam(value = "itemId", required = false) Long itemId,
                       @RequestParam(value = "pageSize", defaultValue = "10" ) int pageSize,
                       Model model) {
        List<Item> items = itemService.findItems(itemId, pageSize);
        model.addAttribute("items", items);
        model.addAttribute("pageSize", pageSize);
//        if(itemId == null){
//            model.addAttribute("lastItemId", 10);
//        }
        model.addAttribute("lastItemId", items.isEmpty() ? 10 : items.get(items.size() - 1).getId());

        return "items/itemList";
    }

    @GetMapping("/items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {
        Book item = (Book) itemService.findOne(itemId);

        BookForm form = new BookForm();
        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
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
