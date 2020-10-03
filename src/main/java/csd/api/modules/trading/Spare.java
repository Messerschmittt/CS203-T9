// public void matching(Trade trade, Integer acc_id){

//     List<Trade> bTrades = tradeRepo.findByActionAndStatusAndSymbol("buy","open",trade.getSymbol());
//     List<Trade> bTrades2 = tradeRepo.findByActionAndStatusAndSymbol("buy","partial-filled",trade.getSymbol());
//     bTrades.addAll(bTrades2);
//     Collections.sort(bTrades);
//     Collections.reverse(bTrades);   //descending order


//     List<Trade> sTrades = tradeRepo.findByActionAndStatusAndSymbol("sell","open", trade.getSymbol());
//     List<Trade> sTrades2 = tradeRepo.findByActionAndStatusAndSymbol("sell","partial-filled", trade.getSymbol());
//     sTrades.addAll(sTrades2);
//     Collections.sort(sTrades);  //ascending order
    
    
//     Stock currstock = stockRepo.findBySymbol(trade.getSymbol());
//     // double currAsk = currstock.getAsk();
//     // double currBid = currstock.getBid();

//     // *   + Buy trades having limit price above market price (current ask) will be matched at current ask.
//     // *      * Example: a buy trade for A17U with price of $4 will be matched at $3.29 (current ask)
//     // * 
    
//     Boolean fill = false;
//     int i = 0;
//     double matchedPrice = 0;
//     int updatequantity = 0;

//     int tradequantity = trade.getQuantity() - trade.getFilled_quantity();
    
//     //for buying matching
//     if(trade.getAction().equals("buy")){
//         trade.setIn("In If");
//         tradeRepo.save(trade);
//         double tradeBid = trade.getBid();
//         while(!fill || i < sTrades.size()){
//             Trade s = sTrades.get(i);       //ascending order
//             // *   + Sell trades having limit price below market price (current bid) will be matched at current bid.
//             // *   Example: a sell trade for A17U with price of $3 will be match at $3.26 (current bid)
//             double askPrice = s.getAsk();
//             if(askPrice < tradeBid){         //not sure
//                 matchedPrice = askPrice;
//             } else if(askPrice > trade.getBid()){      //cannot buy
//                 tradeRepo.save(trade);
//                 return;
//             }

//             // if selling quantity equal to buying quantity
//             String tradeStatus = null;
//             String sStatus = null;
//             double transaction_amt = 0.0;
//             int squantity = s.getQuantity() - s.getFilled_quantity();   //available selling quantity
//             int sFilled_quantity = 0;
//             int tradeFilled_quantity = 0;
//             if(squantity < tradequantity){  //partially filled in buy trade, but sell trade -> "filled"
//                 sStatus = "filled";
//                 tradeStatus = "partial-filled";
//                 sFilled_quantity = s.getQuantity();
//                 tradeFilled_quantity = trade.getFilled_quantity() + squantity;
//                 transaction_amt = squantity * askPrice;
//                 updatequantity += squantity;

//             } else if(squantity > tradequantity){//partially filled in sell trade, but buy trade -> "filled"
//                 sStatus = "partial-filled";
//                 tradeStatus = "filled";
//                 sFilled_quantity = s.getFilled_quantity() + squantity;
//                 tradeFilled_quantity = trade.getQuantity();
//                 transaction_amt = tradequan

//                 updatequantity += tradequantity;

//             } else if(squantity == tradequantity){
//                 sStatus = "filled";
//                 tradeStatus = "filled";
//                 sFilled_quantity = s.getQuantity();
//                 tradeFilled_quantity = trade.getQuantity();
//                 fill = true;
//                 updatequantity += tradequantity;
//             }
            
//             //Make transaction 
//             // try{
//                 Trans trans = new Trans(acc_id, s.getAccount_id(), total_price);  //from, to , ammount
//                 Trans makeTrans = accController.makeTransaction(trans);

//                 s.setFilled_quantity(sFilled_quantity);
//                 s.setStatus(sStatus);
//                 trade.setFilled_quantity(tradeFilled_quantity);
//                 trade.setStatus(tradeStatus);
//                 tradeRepo.save(trade);
//                 tradeRepo.save(s);
//             // } catch (AccountNotFoundException e){
//             //     System.out.println(e.toString());
//             // } catch(ExceedAvailableBalanceException e){
//             //     System.out.println(e.toString());
//             // }
//         }
//         //update stock function ..
//         //update avg_price to trade

        
//     }else if(trade.getAction().equals("sell")){
//         trade.setIn("In If");
//         tradeRepo.save(trade);
//         while(!fill || i < bTrades.size()){
//             Trade b = bTrades.get(i);       //descending order
//             int bquantity = b.getQuantity() - b.getFilled_quantity();   //available selling quantity
//             //*   + Buy trades having limit price above market price (current ask) will be matched at current ask.
// //*      * Example: a buy trade for A17U with price of $4 will be matched at $3.29 (current ask)
//             double bidPrice = b.getBid();
//             matchedPrice = bidPrice;
//             if(bidPrice > currAsk){         //not sure
//                 matchedPrice = currAsk;
//             } else if(bidPrice < trade.getAsk()){      //will not sell
//                 tradeRepo.save(trade);
//                 return;
//             }

//             // if selling quantity equal to buying quantity
//             String tradeStatus = null;
//             String bStatus = null;
//             double total_price = bquantity * matchedPrice;
//             int bFilled_quantity = 0;
//             int tradeFilled_quantity = 0;
//             if(bquantity < tradequantity){  //partially filled in sell trade, but buy trade -> "filled"
//                 bStatus = "filled";
//                 tradeStatus = "partial-filled";
//                 bFilled_quantity = b.getQuantity();
//                 tradeFilled_quantity = trade.getFilled_quantity() + bquantity;
//                 i++;
//                 updatequantity = bquantity;
//             } else if(bquantity > tradequantity){//partially filled in buy trade, but sell trade -> "filled"
//                 bStatus = "partial-filled";
//                 tradeStatus = "filled";
//                 bFilled_quantity = b.getFilled_quantity() + bquantity;
//                 tradeFilled_quantity = trade.getQuantity();
//                 fill = true;
//                 updatequantity = tradequantity;
//             } else if(bquantity == tradequantity){
//                 bStatus = "filled";
//                 tradeStatus = "filled";
//                 bFilled_quantity = b.getQuantity();
//                 tradeFilled_quantity = trade.getQuantity();
//                 fill = true;
//                 updatequantity = tradequantity;
//             }

//             //Make transaction 
//             try{
//                 Trans trans = new Trans(acc_id, b.getAccount_id(), total_price);  //from, to , ammount
//                 Trans makeTrans = accController.makeTransaction(trans);

//                 b.setFilled_quantity(bFilled_quantity);
//                 b.setStatus(bStatus);
//                 trade.setFilled_quantity(tradeFilled_quantity);
//                 trade.setStatus(tradeStatus);
//                 tradeRepo.save(trade);
//                 tradeRepo.save(b);
//             } catch (AccountNotFoundException e){
//                 System.out.println(e.toString());
//             } catch(ExceedAvailableBalanceException e){
//                 System.out.println(e.toString());
//             }
//             //update stock function ..
//             //update avg_price to trade
//         }
            
//     }
//     tradeRepo.save(trade);
//     //update customer's asset 
//     if(fill){

//         int customerid = trade.getCustomer_id();
//         Optional<Customer> customer = custRepo.findById(customerid);
//         Customer c = customer.get();
//         Portfolio p = portfolioRepo.findByCustomer_Id(customerid);
//         List<Assets> aList = p.getAssets();
//         //get asset based on customer id and stock symbol
//         Assets a = assetsRepo.findByCustomer_IdAndCode(customerid, trade.getSymbol());
//         //if asset not exisit, add new asset to asset list 
//         if(a == null){
//             Assets newAssets = new Assets(trade.getSymbol(),trade.getFilled_quantity(),trade.getAvg_price(),matchedPrice);
//             newAssets.setCustomer(c);
//             assetsRepo.save(newAssets);
//         //asset exisits, update asset value 
//         }else{
//             a.setAvg_price(trade.getAvg_price());
//             if(trade.getSymbol().equals("buy")){
//                 //if action is buy, add the buying stock quantity to the current stock quantity
//                 a.setQuantity(a.getQuantity() + updatequantity);
//             }else{
//                 //if action is sell, deduct the selling stock quantity from the current stock quantity
//                 a.setQuantity(a.getQuantity() - updatequantity);
//             }  
//             //update the current stock price            
//             a.setCurrent_price(matchedPrice);  
//             assetsRepo.save(a);
//         }
//         //update the unrealized stock price of portfolio
//         double unrealised = 0;
//         for(Assets ast: aList){
//             unrealised += ast.getGain_loss();
//         }

//         p.setUnrealised(unrealised);
//         portfolioRepo.save(p);
//         //update total gain and lost ??

//     }
// } 