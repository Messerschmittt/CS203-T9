package csd.api.modules.trading;

import java.util.List;
import java.util.Optional;

import csd.api.tables.*;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;



@RestController
public class OrderController {
    private OrderRepository orderrepo;

    public OrderController(OrderRepository orders){
        this.orderrepo = orders;
    }

     /**
     * List all trades in the system
     * @return list of all orders
     */
    @GetMapping("/orders")
    public List<OrderInfo> getOrderlist(){
        return orderrepo.findAll();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/orders")
    public OrderInfo addOrderInfo(@RequestBody OrderInfo order) {
        return orderrepo.save(order);
    }


    /**
     * Remove a trade with the DELETE request to "/orders/{id}"
     * If there is no trade with the given "id", throw a BookNotFoundException
     * @param id
     */
    @DeleteMapping("/orders/{id}")
    public void deleteOrderInfo(@PathVariable Long id){
        if(!orderrepo.existsById(id)) {
            throw new TradeNotFoundException(id);
        }

        orderrepo.deleteById(id);
    }

}
