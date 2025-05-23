package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){
        Item findItem = itemRepository.findOne(itemId);
        //findItem.change(price,name,stockQuantity);    //todo : 리팩토링 set남발금지
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public List<Item> findItems(Long itemId, int pageSize) {
        return itemRepository.findAllByQueryDsl(itemId, pageSize);
    }

    public List<Item> findItems(int pageNum) {
        return itemRepository.findAllByQueryDsl(pageNum);
    }

    public List<Item> findItemsKeySet(int pageNum) {
        return itemRepository.findAllByQueryDslWithIdsAndInClause(pageNum);
    }

    @Transactional
    public Item findOne(Long itemId){
        return itemRepository.findOne(itemId);
    }
}
