package com.ansj.delivery.restaurant.service;

import com.ansj.delivery.common.exception.BusinessException;
import com.ansj.delivery.restaurant.domain.*;
import com.ansj.delivery.restaurant.dto.*;
import com.ansj.delivery.restaurant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuRepository menuRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final MenuOptionItemRepository menuOptionItemRepository;

    @Transactional
    public RestaurantResponse createRestaurant(UUID ownerId, CreateRestaurantRequest request) {
        Restaurant restaurant = Restaurant.builder()
                .name(request.name())
                .phone(request.phone())
                .address(request.address())
                .category(request.category())
                .minOrderAmount(request.minOrderAmount())
                .deliveryFee(request.deliveryFee())
                .estimatedDeliveryMinutes(request.estimatedDeliveryMinutes())
                .ownerId(ownerId)
                .build();
        return RestaurantResponse.from(restaurantRepository.save(restaurant));
    }

    public List<RestaurantResponse> getRestaurants() {
        return restaurantRepository.findByStatus(RestaurantStatus.OPEN).stream()
                .map(RestaurantResponse::from)
                .toList();
    }

    public RestaurantDetailResponse getRestaurant(UUID id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("가게를 찾을 수 없습니다."));
        return RestaurantDetailResponse.from(restaurant);
    }

    @Transactional
    public RestaurantResponse updateRestaurant(UUID ownerId, UUID restaurantId, UpdateRestaurantRequest request) {
        Restaurant restaurant = getOwnedRestaurant(ownerId, restaurantId);
        restaurant.update(request.name(), request.phone(), request.address(), request.category(),
                request.minOrderAmount(), request.deliveryFee(), request.estimatedDeliveryMinutes());
        return RestaurantResponse.from(restaurant);
    }

    @Transactional
    public RestaurantResponse openRestaurant(UUID ownerId, UUID restaurantId) {
        Restaurant restaurant = getOwnedRestaurant(ownerId, restaurantId);
        restaurant.open();
        return RestaurantResponse.from(restaurant);
    }

    @Transactional
    public RestaurantResponse closeRestaurant(UUID ownerId, UUID restaurantId) {
        Restaurant restaurant = getOwnedRestaurant(ownerId, restaurantId);
        restaurant.close();
        return RestaurantResponse.from(restaurant);
    }

    @Transactional
    public MenuResponse createMenu(UUID ownerId, UUID restaurantId, CreateMenuRequest request) {
        Restaurant restaurant = getOwnedRestaurant(ownerId, restaurantId);

        Menu menu = Menu.builder()
                .restaurant(restaurant)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .imageUrl(request.imageUrl())
                .categoryName(request.categoryName())
                .sortOrder(request.sortOrder())
                .build();
        menuRepository.save(menu);

        if (request.options() != null) {
            for (CreateMenuOptionRequest optionReq : request.options()) {
                MenuOption option = MenuOption.builder()
                        .menu(menu)
                        .name(optionReq.name())
                        .isRequired(optionReq.isRequired())
                        .maxSelectCount(optionReq.maxSelectCount())
                        .build();
                menuOptionRepository.save(option);

                if (optionReq.items() != null) {
                    for (CreateMenuOptionItemRequest itemReq : optionReq.items()) {
                        MenuOptionItem item = MenuOptionItem.builder()
                                .menuOption(option)
                                .name(itemReq.name())
                                .extraPrice(itemReq.extraPrice())
                                .build();
                        menuOptionItemRepository.save(item);
                    }
                }
            }
        }

        return MenuResponse.from(menuRepository.findById(menu.getId()).orElseThrow());
    }

    @Transactional
    public MenuResponse updateMenu(UUID ownerId, UUID restaurantId, UUID menuId, UpdateMenuRequest request) {
        getOwnedRestaurant(ownerId, restaurantId);
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> BusinessException.notFound("메뉴를 찾을 수 없습니다."));
        menu.update(request.name(), request.description(), request.price(), request.imageUrl(),
                request.categoryName(), request.sortOrder(), request.available());
        return MenuResponse.from(menu);
    }

    @Transactional
    public void deleteMenu(UUID ownerId, UUID restaurantId, UUID menuId) {
        getOwnedRestaurant(ownerId, restaurantId);
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> BusinessException.notFound("메뉴를 찾을 수 없습니다."));
        menuRepository.delete(menu);
    }

    public List<MenuResponse> getMenusByRestaurant(UUID restaurantId) {
        return menuRepository.findByRestaurantIdOrderBySortOrder(restaurantId).stream()
                .map(MenuResponse::from)
                .toList();
    }

    private Restaurant getOwnedRestaurant(UUID ownerId, UUID restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> BusinessException.notFound("가게를 찾을 수 없습니다."));
        if (!restaurant.getOwnerId().equals(ownerId)) {
            throw BusinessException.forbidden("해당 가게에 대한 권한이 없습니다.");
        }
        return restaurant;
    }
}
