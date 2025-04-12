package jpabook.jpashop.api;

import jpabook.jpashop.controller.Form.BookSaveForm;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/items")
public class ItemApiController {

    private final ItemService itemService;

    @PostMapping("/create")
    public Object create(
            @Validated @RequestBody BookSaveForm form
    ) {
        Item book = makeItemFrom(form);

        itemService.saveItem(book);

        return form;
    }

    @GetMapping()
    public Object findAll() {
        List<Item> items = itemService.findItems();
        List<SimpleItemDto> collect = items.stream().
                map(SimpleItemDto::new).
                collect(Collectors.toList());
        return new Result<>(collect.size(), collect);
    }

    private Item makeItemFrom(BookSaveForm form) {
        return Book.builder()
                .name(form.getName())
                .price(form.getPrice())
                .stockQuantity(form.getStockQuantity())
                .author(form.getAuthor())
                .isbn(form.getIsbn())
                .build();
    }

    @Data
    static class SimpleItemDto {
        private Long itemId;
        private String name;
        private Integer price;
        private Integer stockQuantity;
        private String author;
        private String isbn;

        public SimpleItemDto(Item item) {
            this.itemId = item.getId();
            this.name = item.getName();
            this.price = item.getPrice();
            this.stockQuantity = item.getStockQuantity();
            if (item instanceof Book) {
                Book book = (Book) item;
                this.author = book.getAuthor();
                this.isbn = book.getIsbn();
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class Result<T> {
        private int count;
        private T data;
    }
}
