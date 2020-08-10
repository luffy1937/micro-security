package org.liuyuefeng.security.priceapi.price;


import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/prices")
@Slf4j
public class PriceController {
    @GetMapping("/{id}")
    public PriceInfo create(@PathVariable Long id
            //, @AuthenticationPrincipal String username
    ){


        try(Entry entry = SphU.entry("price")){
            // log.info("user is " + username);
            log.info("productId is " + id);
            PriceInfo info = new PriceInfo();
            info.setId(id);
            info.setPrice(new BigDecimal(100));
            return info;
        } catch (BlockException e) {
            log.info("Blocked");
            return null;
        }
    }
}
