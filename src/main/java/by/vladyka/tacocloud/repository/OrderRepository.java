package by.vladyka.tacocloud.repository;

import by.vladyka.tacocloud.entity.Order;

public interface OrderRepository {
    Order save(Order order);
}
