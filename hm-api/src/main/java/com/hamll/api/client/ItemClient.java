package com.hamll.api.client;


import com.hamll.api.dto.ItemDTO;
import com.hamll.api.dto.OrderDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

/**
 * @description:
 * @class: ItemClient
 * @author: yu_wei
 * @create: 2024/12/14 11:02
 */
@FeignClient("item-service")
public interface ItemClient {
    @GetMapping("/items")
    List<ItemDTO> queryItemsByIds(@RequestParam("ids") Collection<Long> ids);
    
    
    /**
     * 批量扣减库存
     * @param items
     */
    @PutMapping("items/stock/deduct")
    void deductStock(@RequestBody List<OrderDetailDTO> items);
    
    
    /**
     * 根据id批量查询商品
     * @param ids
     * @return
     */
    @GetMapping
     List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);
    
    
}
