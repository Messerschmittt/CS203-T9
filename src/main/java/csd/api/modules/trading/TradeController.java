// package csd.api.modules.trading;

// import java.util.List;
// import java.util.Optional;

// import csd.api.tables.*;

// import org.springframework.dao.EmptyResultDataAccessException;
// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.ResponseStatus;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// public class TradeController {

//     private Prices prices_generator;
    
//     @PostMapping("/trade/price/{symbol}")
//     public double getPriceFor(@PathVariable("symbol") String symbol){
//         System.out.println("Symbol: " + symbol);
//         System.out.println(prices_generator.getPrice(symbol));
//         return prices_generator.getPrice(symbol);
//     }
// }
