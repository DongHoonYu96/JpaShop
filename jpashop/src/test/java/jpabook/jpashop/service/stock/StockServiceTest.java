package jpabook.jpashop.service.stock;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.stock.Stock;
import jpabook.jpashop.domain.stock.Subscription;
import jpabook.jpashop.repository.stock.StockRepository;
import jpabook.jpashop.repository.stock.SubscriptionRepository;
import jpabook.jpashop.service.stock.client.AlphaVantageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class StockServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    StockService stockService;

    @Autowired
    StockRepository stockRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @MockBean
    AlphaVantageClient alphaVantageClient;

    @Test
    public void 주식_구독() {
        //given
        Member member = createMember("회원1", "test@test.com");
        String symbol = "AAPL";
        String name = "Apple Inc.";

        //when
        Long subscriptionId = stockService.subscribe(member.getId(), symbol, name);

        //then
        Subscription getSubscription = subscriptionRepository.findById(subscriptionId).get();

        assertEquals("구독한 회원 정보가 일치해야 한다.", member, getSubscription.getMember());
        assertEquals("구독한 주식 심볼이 일치해야 한다.", symbol, getSubscription.getStock().getSymbol());
        assertEquals("구독한 주식 이름이 일치해야 한다.", name, getSubscription.getStock().getName());
    }

    @Test
    public void 주식_구독_취소() {
        //given
        Member member = createMember("회원1", "test@test.com");
        Stock stock = createStock("AAPL", "Apple Inc.");
        Subscription subscription = Subscription.createSubscription(member, stock);
        em.persist(subscription);

        //when
        stockService.unsubscribe(subscription.getId());

        //then
        List<Subscription> subscriptions = subscriptionRepository.findByMemberId(member.getId());
        assertTrue("구독 취소 후 목록에 없어야 한다.", subscriptions.isEmpty());
    }

    @Test
    public void 주식_검색_DB에_결과_있음() {
        //given
        createStock("AAPL", "Apple Inc.");
        createStock("MSFT", "Microsoft Corporation");

        //when
        List<Stock> stocks = stockService.findStocks("Apple");

        //then
        assertEquals("검색된 주식의 수가 1개여야 한다.", 1, stocks.size());
        assertEquals("검색된 주식의 이름이 일치해야 한다.", "Apple Inc.", stocks.get(0).getName());
    }

    @Test
    public void 주식_검색_DB에_결과_없고_API에_있음() {
        //given
        JsonObject searchResult = new JsonObject();
        JsonArray bestMatches = new JsonArray();
        JsonObject apple = new JsonObject();
        apple.addProperty("1. symbol", "AAPL");
        apple.addProperty("2. name", "Apple Inc.");
        bestMatches.add(apple);
        searchResult.add("bestMatches", bestMatches);

        when(alphaVantageClient.searchStocks(anyString())).thenReturn(searchResult);

        //when
        List<Stock> stocks = stockService.findStocks("Apple");

        //then
        assertEquals("API에서 검색된 주식 수가 1개여야 한다.", 1, stocks.size());
        assertEquals("API에서 검색된 주식 이름이 일치해야 한다.", "Apple Inc.", stocks.get(0).getName());
    }


    private Member createMember(String name, String email) {
        Member member = Member.builder()
                .name(name)
                .email(email)
                .loginId("testuser")
                .password("1234")
                .address(new Address("서울", "강남", "123-123"))
                .build();
        em.persist(member);
        return member;
    }

    private Stock createStock(String symbol, String name) {
        Stock stock = new Stock(symbol, name);
        em.persist(stock);
        return stock;
    }
}
