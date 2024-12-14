package com.hmall.service;

import com.hmall.domain.dto.OrderFormDTO;
import com.hmall.domain.po.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yu_wei
 * @since 2024-10-14
 */
public interface IOrderService extends IService<Order> {

    Long createOrder(OrderFormDTO orderFormDTO);

    void markOrderPaySuccess(Long orderId);
}
