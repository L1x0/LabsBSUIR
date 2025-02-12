package by.astakhau.bonuslab.service;

import by.astakhau.bonuslab.data.Order;
import by.astakhau.bonuslab.data.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        Iterable<Order> ordersIterable = orderRepository.findAll();
        return StreamSupport.stream(ordersIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void deleteOrderById(Long id) {
        orderRepository.deleteById(id);
    }
}
