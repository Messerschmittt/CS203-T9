// package csd.api.modules.trading;

// import java.util.List;
// import java.util.Map;

// import org.patriques.AlphaVantageConnector;
// import org.patriques.TimeSeries;
// import org.patriques.input.timeseries.Interval;
// import org.patriques.input.timeseries.OutputSize;
// import org.patriques.output.AlphaVantageException;
// import org.patriques.output.timeseries.IntraDay;
// import org.patriques.output.timeseries.data.StockData;

// import lombok.NoArgsConstructor;

// @NoArgsConstructor
// public class Prices {
//     String apiKey = "HC7YP8KHR7L82GNJ";
//     int timeout = 3000;
//     AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
//     TimeSeries stockTimeSeries = new TimeSeries(apiConnector);

//     public Stock getPrice(String symbol) {
//         double output = -1;
//         try {
//             IntraDay response = stockTimeSeries.intraDay("MSFT", Interval.ONE_MIN, OutputSize.COMPACT);
//             Map<String, String> metaData = response.getMetaData();
//             System.out.println("Information: " + metaData.get("1. Information"));
//             System.out.println("Stock: " + metaData.get("2. Symbol"));
            
//             List<StockData> stockData = response.getStockData();
//             stockData.forEach(stock -> ({ 
//               System.out.println("date:   " + stock.getDateTime());
//               System.out.println("open:   " + stock.getOpen());
//               System.out.println("high:   " + stock.getHigh());
//               System.out.println("low:    " + stock.getLow());
//               System.out.println("close:  " + stock.getClose());
//               System.out.println("volume: " + stock.getVolume());
//             });

//           } catch (AlphaVantageException e) {
//             System.out.println("something went wrong");
//           }
//           return output;
//     }

// }
