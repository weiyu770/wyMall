package com.hmall.cart.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hamll.api.client.ItemClient;
import com.hamll.api.dto.ItemDTO;
import com.hmall.cart.domain.dto.CartFormDTO;
import com.hmall.cart.domain.po.Cart;
import com.hmall.cart.domain.vo.CartVO;
import com.hmall.cart.mapper.CartMapper;
import com.hmall.cart.service.ICartService;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.BeanUtils;
import com.hmall.common.utils.CollUtils;
import com.hmall.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author yu_wei
 * @since 2024-10-14
 */
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {
    
    //    private final IItemService itemService;
    
    private final DiscoveryClient discoveryClient;
    
    private final RestTemplate restTemplate;
    

//    private  final ItemClient itemClient;
    
    @Autowired
    private ItemClient itemClient;
    
    @Override
    public void addItem2Cart(CartFormDTO cartFormDTO) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();
        
        // 2.判断是否已经存在
        if (checkItemExists(cartFormDTO.getItemId(), userId)) {
            // 2.1.存在，则更新数量
            baseMapper.updateNum(cartFormDTO.getItemId(), userId);
            return;
        }
        // 2.2.不存在，判断是否超过购物车数量
        checkCartsFull(userId);
        
        // 3.新增购物车条目
        // 3.1.转换PO
        Cart cart = BeanUtils.copyBean(cartFormDTO, Cart.class);
        // 3.2.保存当前用户
        cart.setUserId(userId);
        // 3.3.保存到数据库
        save(cart);
    }
    
    @Override
    public List<CartVO> queryMyCarts() {
        // 1.查询我的购物车列表
        List<Cart> carts = lambdaQuery().eq(
                Cart::getUserId,
                //                UserContext.getUser()
                1L
        ).list();
        if (CollUtils.isEmpty(carts)) {
            return CollUtils.emptyList();
        }
        
        // 2.转换VO
        List<CartVO> vos = BeanUtils.copyList(carts, CartVO.class);
        
        // 3.处理VO中的商品信息
        handleCartItems(vos);
        
        // 4.返回
        return vos;
    }
    
    /**
     * 手写HTTP
     */
    private void handleCartItems_1(List<CartVO> vos) {
        // 1. 获取商品 ID 集合
        Set<Long> itemIds = vos.stream()
                .map(CartVO::getItemId)
                .collect(Collectors.toSet());

        if (CollUtils.isEmpty(itemIds)) {
            return; // 如果没有商品ID 直接返回
        }

        // 2. 根据服务名称获取服务实例列表
        List<ServiceInstance> instances = discoveryClient.getInstances("item-service");
        if (CollUtils.isEmpty(instances)) {
            return; // 如果没有可用的服务实例，直接返回
        }

        // 3. 负载均衡：从实例列表中随机挑选一个实例
        ServiceInstance serviceInstance = instances.get(RandomUtil.randomInt(instances.size()));

        // 4. 构建 URL
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(serviceInstance.getUri())
                .path("/items")
                .queryParam("ids", CollUtil.join(itemIds, ","));
        String url = builder.toUriString();

        // 5. 调用远程接口获取商品信息
        ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
                url,
                org.springframework.http.HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ItemDTO>>() {
                }
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            return; // 如果请求失败 直接返回
        }

        List<ItemDTO> items = response.getBody();
        if (CollUtils.isEmpty(items)) {
            return; // 如果没有返回商品数据 直接返回
        }

        // 6. 将商品信息转换为 Map，方便快速查找
        Map<Long, ItemDTO> itemMap = items.stream()
                .collect(Collectors.toMap(ItemDTO::getId, Function.identity()));

        // 7. 将商品信息写入对应的 CartVO
        for (CartVO vo : vos) {
            ItemDTO item = itemMap.get(vo.getItemId());
            if (item == null) {
                continue; // 如果商品不存在 跳过
            }
            vo.setNewPrice(item.getPrice());
            vo.setStatus(item.getStatus());
            vo.setStock(item.getStock());
            vo.setName(item.getName());
            vo.setImage(item.getImage());
        }
    }
    
    
    /**
     * 调用远程服务
     * @param vos
     */
    private void handleCartItems(List<CartVO> vos) {
        // 1. 获取商品 ID 集合
        Set<Long> itemIds = vos.stream()
                .map(CartVO::getItemId)
                .collect(Collectors.toSet());
        
        if (CollUtils.isEmpty(itemIds)) {
            return; // 如果没有商品ID 直接返回
        }
        
        // 2. 调用服务获取商品信息
        List<ItemDTO> items = itemClient.queryItemsByIds(itemIds);
        if (CollUtils.isEmpty(items)) {
            return; // 如果没有返回商品数据 直接返回
        }
        
        // 3. 将商品信息转换为 Map，方便快速查找
        Map<Long, ItemDTO> itemMap = items.stream()
                .collect(Collectors.toMap(ItemDTO::getId, Function.identity()));
        
        // 4. 将商品信息写入对应的 CartVO
        for (CartVO vo : vos) {
            ItemDTO item = itemMap.get(vo.getItemId());
            if (item == null) {
                continue; // 如果商品不存在，跳过
            }
            // 设置商品信息
            vo.setNewPrice(item.getPrice());
            vo.setStatus(item.getStatus());
            vo.setStock(item.getStock());
            vo.setName(item.getName());
            vo.setImage(item.getImage());
        }
    }

    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public void removeByItemIds(Collection<Long> itemIds) {
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        queryWrapper.lambda()
                .eq(Cart::getUserId, UserContext.getUser())
                .in(Cart::getItemId, itemIds);
        // 2.删除
        remove(queryWrapper);
    }
    
    private void checkCartsFull(Long userId) {
        int count = lambdaQuery().eq(Cart::getUserId, userId).count();
        if (count >= 10) {
            throw new BizIllegalException(StrUtil.format("用户购物车课程不能超过{}", 10));
        }
    }
    
    private boolean checkItemExists(Long itemId, Long userId) {
        int count = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getItemId, itemId)
                .count();
        return count > 0;
    }
}
