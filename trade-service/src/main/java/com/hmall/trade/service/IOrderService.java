package com.hmall.trade.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.trade.domain.dto.OrderFormDTO;
import com.hmall.trade.domain.po.Order;

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
