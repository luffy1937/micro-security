package org.liuyuefeng.security.orderapi.order;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {
    @Autowired//可以拿到令牌，并放到header里
    private OAuth2RestTemplate restTemplate;
    @PostMapping
    public OrderInfo create(@RequestBody OrderInfo info, @AuthenticationPrincipal String username){//@AuthenticationPrincpal(expression = "#this.id") Long id
        log.info("user is " + username);
        PriceInfo price = restTemplate.getForObject("http://localhost:9080/prices/" + info.getProductId(), PriceInfo.class);
        log.info("price is " + price.getPrice());
        return info;
    }
    @GetMapping("/{id}")
    public OrderInfo getInfo(@PathVariable Long id, @RequestHeader String username){
        log.info("orderId is " + id);
        log.info("user is " + username);
        OrderInfo info = new OrderInfo();
        info.setProductId(id * 5);
        info.setId(id);
        return info;
    }
}
