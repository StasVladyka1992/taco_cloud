package by.vladyka.tacocloud.repository;

import by.vladyka.tacocloud.entity.Order;
import by.vladyka.tacocloud.entity.Taco;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepostioryImpl implements OrderRepository {
    private SimpleJdbcInsert orderInserter;
    private SimpleJdbcInsert orderTacoInserter;
    private ObjectMapper objectMapper;

    @Override
    public Order save(Order order) {
        order.setPlacedAt(new Date());
        long orderId = saveOrderDetails(order);
        order.setId(orderId);
        List<Taco> tacos = order.getTacos();
        tacos.forEach(taco -> saveTacoToOrder(taco, orderId));
        return order;
    }

    private long saveOrderDetails(Order order) {
        //it's a bit hackish use of Jackson's objectMapper, but since it in the class path and jdbcTemplate accepts map
        // why not to use it?)))
        @SuppressWarnings("unchecked")
        Map<String, Object> values = objectMapper.convertValue(order, Map.class);
        //i need to do it manually because otherwise objectMapper will convert date to long
        values.put("placedAt", order.getPlacedAt());

        return orderInserter.executeAndReturnKey(values).longValue();
    }

    private void saveTacoToOrder(Taco taco, long orderId) {
        Map<String, Object> values = new HashMap<>();
        values.put("tacoOrder", orderId);
        values.put("taco", taco.getId());
        orderTacoInserter.execute(values);
    }

    @Autowired
    public OrderRepostioryImpl(JdbcTemplate template) {
        this.orderInserter = new SimpleJdbcInsert(template)
                .withTableName("Taco_Order")
                .usingGeneratedKeyColumns("id");

        this.orderTacoInserter = new SimpleJdbcInsert(template)
                .withTableName("Taco_Order_Tacos");

        this.objectMapper = new ObjectMapper();
    }
}
