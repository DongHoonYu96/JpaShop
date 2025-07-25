package jpabook.jpashop.controller.stock;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.stock.Stock;
import jpabook.jpashop.controller.stock.StockPriceDto;
import jpabook.jpashop.service.stock.StockService;
import jpabook.jpashop.web.SessionConst;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/stocks")
public class StockController {

    private final StockService stockService;

    @GetMapping
    public String subscriptions(
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            Model model) {

        if (loginMember == null) {
            return "redirect:/login";
        }

        List<StockPriceDto> stockPrices = stockService.findMySubscriptions(loginMember.getId());
        model.addAttribute("stockPrices", stockPrices);
        return "stocks/subscriptionList";
    }

    @GetMapping("/search")
    public String searchForm(Model model) {
        model.addAttribute("stocks", null);
        return "stocks/search";
    }

    @PostMapping("/search")
    public String search(@RequestParam("keyword") String keyword, Model model) {
        List<Stock> stocks = stockService.findStocks(keyword);
        model.addAttribute("stocks", stocks);
        return "stocks/search";
    }

    @PostMapping("/subscribe/{symbol}")
    public String subscribe(
            @PathVariable("symbol") String symbol,
            @RequestParam("name") String name,
            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember) {

        if (loginMember == null) {
            return "redirect:/login";
        }

        stockService.subscribe(loginMember.getId(), symbol, name);
        return "redirect:/stocks";
    }

    @PostMapping("/unsubscribe/{subscriptionId}")
    public String unsubscribe(@PathVariable("subscriptionId") Long subscriptionId) {
        stockService.unsubscribe(subscriptionId);
        return "redirect:/stocks";
    }
}
