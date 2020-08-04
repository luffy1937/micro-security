package org.liuyuefeng.security.orderapi.order;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {
    //private RestTemplate restTemplate = new RestTemplate();
    @PostMapping
    public OrderInfo create(@RequestBody OrderInfo info, @RequestHeader String username){//@AuthenticationPrincpal(expression = "#this.id") Long id
        log.info("user is " + username);
/*
        PriceInfo price = restTemplate.getForObject("http://localhost:9080/prices/" + info.getProductId(), PriceInfo.class);
        log.info("price is " + price.getPrice());
*/
        return info;
    }
    @GetMapping("/{id}")
    public OrderInfo getInfo(@PathVariable Long id){
        log.info("orderId is " + id);
        return new OrderInfo();
    }
}
