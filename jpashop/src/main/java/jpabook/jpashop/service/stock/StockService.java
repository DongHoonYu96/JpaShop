package jpabook.jpashop.service.stock;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jpabook.jpashop.controller.stock.StockPriceDto;
import jpabook.jpashop.domain.stock.Stock;
import jpabook.jpashop.repository.stock.StockRepository;
import jpabook.jpashop.repository.stock.SubscriptionRepository;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.stock.Subscription;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.service.stock.client.AlphaVantageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final MemberRepository memberRepository;
    private final AlphaVantageClient alphaVantageClient;
    private final EmailService emailService;

    @Transactional
    public Long subscribe(Long memberId, String symbol, String name) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("No such member"));
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseGet(() -> stockRepository.save(new Stock(symbol, name)));

        Subscription subscription = Subscription.createSubscription(member, stock);
        subscriptionRepository.save(subscription);
        return subscription.getId();
    }

    @Transactional
    public void unsubscribe(Long subscriptionId) {
        subscriptionRepository.deleteById(subscriptionId);
    }

    public List<StockPriceDto> findMySubscriptions(Long memberId) {
        List<Subscription> subscriptions = subscriptionRepository.findByMemberId(memberId);
        List<StockPriceDto> stockPriceDtos = new ArrayList<>();

        for (Subscription subscription : subscriptions) {
            Stock stock = subscription.getStock();
            JsonObject stockData = alphaVantageClient.getDailyTimeSeries(stock.getSymbol());
            if (stockData != null && stockData.has("Time Series (Daily)")) {
                JsonObject timeSeries = stockData.getAsJsonObject("Time Series (Daily)");
                List<Double> closingPrices = new ArrayList<>();
                for (Map.Entry<String, JsonElement> entry : timeSeries.entrySet()) {
                    closingPrices.add(entry.getValue().getAsJsonObject().get("4. close").getAsDouble());
                }

                if (!closingPrices.isEmpty()) {
                    double currentPrice = closingPrices.get(0);
                    double ma20 = calculateMA(closingPrices, 20);
                    stockPriceDtos.add(new jpabook.jpashop.controller.stock.StockPriceDto(subscription, currentPrice, ma20));
                }
            }
        }
        return stockPriceDtos;
    }

    public List<Stock> findStocks(String keyword) {
        // DB에서 먼저 검색
        List<Stock> stocks = stockRepository.findByNameContaining(keyword);
        if (!stocks.isEmpty()) {
            return stocks;
        }

        // DB에 없으면 외부 API 검색
        JsonObject searchResult = alphaVantageClient.searchStocks(keyword);
        List<Stock> apiStocks = new ArrayList<>();
        if (searchResult != null && searchResult.has("bestMatches")) {
            for (JsonElement element : searchResult.getAsJsonArray("bestMatches")) {
                JsonObject stockObject = element.getAsJsonObject();
                // API 결과는 DB에 저장하지 않고, 화면에 보여주기만 함
                apiStocks.add(new Stock(
                        stockObject.get("1. symbol").getAsString(),
                        stockObject.get("2. name").getAsString()
                ));
            }
        }
        return apiStocks;
    }


    @Scheduled(cron = "0 0 18 * * MON-FRI") // 매주 월~금 18시에 실행
    @Transactional
    public void checkStockPrices() {
        List<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            JsonObject stockData = alphaVantageClient.getDailyTimeSeries(stock.getSymbol());
            if (stockData != null && stockData.has("Time Series (Daily)")) {
                processStockData(stock, stockData);
            }
        }
    }

    private void processStockData(Stock stock, JsonObject stockData) {
        JsonObject timeSeries = stockData.getAsJsonObject("Time Series (Daily)");
        List<Double> closingPrices = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : timeSeries.entrySet()) {
            closingPrices.add(entry.getValue().getAsJsonObject().get("4. close").getAsDouble());
        }

        if (closingPrices.size() >= 120) {
            double currentPrice = closingPrices.get(0);
            double ma20 = calculateMA(closingPrices, 20);
            double ma60 = calculateMA(closingPrices, 60);
            double ma120 = calculateMA(closingPrices, 120);

            checkAndNotify(stock, currentPrice, ma20, 20);
            checkAndNotify(stock, currentPrice, ma60, 60);
            checkAndNotify(stock, currentPrice, ma120, 120);
        }
    }

    private double calculateMA(List<Double> prices, int period) {
        return prices.stream().limit(period).mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private void checkAndNotify(Stock stock, double currentPrice, double movingAverage, int period) {
        if (Math.abs(currentPrice - movingAverage) / movingAverage < 0.01) { // 1% 이내로 근접하면 알림
            subscriptionRepository.findByStock(stock).forEach(subscription -> {
                String to = subscription.getMember().getEmail();
                String subject = String.format("[%s] 주가 알림", stock.getName());
                String text = String.format(
                        "구독하신 %s(%s)의 현재가가 %d일 이동평균선(%.2f)에 근접했습니다. 현재가: %.2f",
                        stock.getName(), stock.getSymbol(), period, movingAverage, currentPrice
                );
                emailService.sendMail(to, subject, text);
            });
        }
    }
}
