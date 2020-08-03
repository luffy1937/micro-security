package org.liuyuefeng.security.priceapi.price;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/prices")
@Slf4j
public class PriceController {
    @GetMapping("/{id}")
    public PriceInfo create(@PathVariable Long id){
        log.info("productId is " + id);
        PriceInfo info = new PriceInfo();
        info.setId(id);
        info.setPrice(new BigDecimal(100));
        return info;
    }
}
