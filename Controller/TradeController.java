package Controller;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import Model.Trade;

public class TradeController {
    private LinkedList<Trade> buys;
    private LinkedList<Trade> sells;
    private LinkedList<Trade> rewards;
    private LinkedList<String> transactions;
    private LinkedList<String> unaccounted;
    private double totalFiatDeposit;
    private double totalFiatFees;
    private double totalFiatWithdraw;
    
    private double profit = 0.0;
    private double loss = 0.0;
    private int limit = 0;
    private double unaccountedSells = 0;
    private double unaccountedBuys = 0;

    public TradeController() {
        this.buys = new LinkedList<Trade>();
        this.sells  = new LinkedList<Trade>();
        this.rewards = new LinkedList<Trade>();
        this.transactions = new LinkedList<String>();
        this.unaccounted = new LinkedList<String>();
        this.totalFiatDeposit = 0.0;
        this.totalFiatWithdraw = 0.0;
        this.totalFiatFees = 0.0;
    }




    public LinkedList<Trade> getBuys() {
        return this.buys;
    }

    public LinkedList<Trade> getSells() {
        return this.sells;
    }

    // Sorts the trades by date
    public void sort() {
        this.buys.sort(new Comparator<Trade>() {

            @Override
            public int compare(Trade o1, Trade o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
            
        });

        this.sells.sort(new Comparator<Trade>() {

            @Override
            public int compare(Trade o1, Trade o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
            
        });
    }

    // Sorts the sell and buys linked lists, disables/enables the necessary trades based on the date limit and updates the totalFiatFees value
    public void preprocess() {
        sort();

        Iterator<Trade> sIterator = this.sells.iterator();
        Iterator<Trade> bIterator = this.buys.iterator();

        if (this.limit != 0) {
            // Disable trades from ending up in the report if limited to a specific year
            String limitYearLower = String.format("%d-07-01 00:00:00", this.limit-1);
            String limitYearUpper = String.format("%d-07-01 00:00:00", this.limit);

            limitTrades(bIterator, limitYearLower, limitYearUpper);
            limitTrades(sIterator, limitYearLower, limitYearUpper);
        }
        else{
            // No limited to a specific year, ensure all trades are enabled            
            enableTrades(bIterator);
            enableTrades(sIterator);
        }

        // UPDATE FEES
        sIterator = sells.iterator();
        bIterator = buys.iterator();

        double newFees = 0.0;
        newFees += calculateFees(sIterator);
        newFees += calculateFees(bIterator);

        this.totalFiatFees = newFees;
    }

    // Returns the total fees from a given set of trades
    private double calculateFees(Iterator<Trade> iter) {
        double fees = 0.0;

        while (iter.hasNext()) {
            Trade t = iter.next();
            
            // Reset volume of trade to original volume
            // IMPORTANT: required since report() modifies the volume of the trades
            t.setVolume(t.getOriginalVolume());
            
            // Add fees if the trade is not disabled
            if (!t.isDisabled())
            {
                fees += t.getFee();
            }
        }

        return fees;
    }

    // Iterates through each Trade from a linked list and disables any Trades that are out of the date range
    // otherwise, enables Trades in the date range
    private void limitTrades(Iterator<Trade> iter, String limitYearLower, String limitYearUpper) {
        while (iter.hasNext()) {
            Trade t = iter.next();

            if (t.getDate().compareTo(limitYearLower) >= 0 && t.getDate().compareTo(limitYearUpper) < 0) {
                t.setDisabled(false);
            }                
            else {
                t.setDisabled(true);
            }
        }
    }

    // Iterates through each Trade from a linked list and enables all Trades
    private void enableTrades(Iterator<Trade> iter) {
        while (iter.hasNext()) {
            Trade t = iter.next();
            t.setDisabled(false);
        }
    }

    // Adds the Trade to the valid lists and increments the necessary totals by the related value from the Trade
    public void addTrade(String date, String type, String asset, double volume, double price, double fee, double value) {
        Trade trade = null;
        
        if (type.equals("Deposit"))
        {
            if (asset.equals("AUD"))
            {
                this.totalFiatDeposit += value;
            }
            else
            {
                // TODO: Handle crypto deposits
            }
        }
        else if (type.equals("Withdraw"))
        {
            if (asset.equals("AUD"))
            {
                this.totalFiatWithdraw += Math.abs(value);
            }
        }
        else if (type.equals("Buy Order"))
        {
            trade = new Trade(date, type, asset, volume, price, fee, value);
            buys.add(trade);
            this.totalFiatFees += fee;
        }
        else if (type.equals("Sell Order"))
        { 
            trade = new Trade(date, type, asset, volume, price, fee, value);
            sells.add(trade);
            this.totalFiatFees += fee;
        }
        else if (type.equals("Reward"))
        {
            trade = new Trade(date, type, asset, volume, price, fee, value);
            this.rewards.add(trade);
        }
    }

    // Prints out the purchase transactions - DEBUG only
    public void display()
    {
        Iterator<Trade> iter = this.buys.iterator();

        while (iter.hasNext())
        {
            Trade trade = (Trade)iter.next();
            System.out.println(trade.getDate());
        }
    }


    // Processes the transactions
    // Finds the related buys and sells and calculates the profit.
    public void process() {   
        this.profit = 0;
        this.loss = 0;
        this.transactions.clear();
        Iterator<Trade> buyIterator = this.buys.iterator();

        // Sum rewards list and add as new trade to buys with price of 0
        /*
        Iterator<Trade> rewardsIterator = rewards.iterator();
        double sumRewards = 0;

        while (rewardsIterator.hasNext())
        {
            Trade rewards 
        }
        */

        while (buyIterator.hasNext())
        {
            Trade buy = buyIterator.next();

            if (!buy.isDisabled()) {
                String buyAsset = buy.getAsset();
                Iterator<Trade> sellIterator = sells.iterator();
                while (sellIterator.hasNext() && buy.getVolume() > 0.0)
                {
                    Trade sell = sellIterator.next();
                    if (!sell.isDisabled()) {
                        if (sell.getAsset().equals(buyAsset))
                        {
                            // Now, check if sell has any volume left
                            double sellVolume = sell.getVolume();
                            if (sellVolume > 0.0)
                            {
                                // Has volume, so subtract from sellTrade and buyTrade
                                // Subtract minimum between sellVolume and buyVolume from sellVolume for final volume
                                double volumeSold = Math.min(sellVolume, buy.getVolume());
                                sell.setVolume(sell.getVolume() - volumeSold);
                                buy.setVolume(buy.getVolume() - volumeSold);

                                // Calculate profit/loss
                                double profitLoss = (sell.getPrice() * volumeSold) - (buy.getPrice() * volumeSold);
                                if (profitLoss > 0.0) {
                                    profit += profitLoss;
                                    transactions.add(String.format("[PROFIT]  $%.2f    %s-%s", profitLoss, buyAsset, sell.getAsset()));                      }
                                else
                                {
                                    loss += profitLoss;
                                    transactions.add(String.format("[LOSS]    $%.2f    %s-%s", profitLoss, buyAsset, sell.getAsset())); 
                                }
                            }
                        }
                    }
                }
            }
        }

        Iterator<Trade> sellsIterator = this.sells.iterator();
        this.unaccountedSells = 0.0;
        while (sellsIterator.hasNext())
        {
            Trade s = sellsIterator.next();
            if (!s.isDisabled()) {
                if (s.getVolume() > 0)
                {
                    this.unaccountedSells += s.getVolume() * s.getPrice();
                    this.unaccounted.add(String.format("SOLD but no BUY volume found\n    %s %s - Volume Left: %f    -> $%.2f", s.getDate(), s.getAsset(), s.getVolume(), s.getVolume() * s.getPrice()));
                }
            }
        }

        Iterator<Trade> buysIterator = this.buys.iterator();
        this.unaccountedBuys = 0.0;
        while (buysIterator.hasNext())
        {
            Trade b = buysIterator.next();
            if (!b.isDisabled()) {
                if (b.getVolume() > 0)
                {
                    this.unaccountedBuys += b.getVolume() * b.getPrice();
                    this.unaccounted.add(String.format("BOUGHT but not SOLD yet\n    %s %s - Volume Left: %f bought for $%.2f", b.getDate(), b.getAsset(), b.getVolume(), b.getVolume() * b.getPrice()));
                }
            }
        }
    }

    // Generates the report
    public void report() {
        Iterator<String> trasIter = this.transactions.iterator();
        System.out.println("TRANSACTIONS\n");
        while (trasIter.hasNext()) {
            System.out.println(trasIter.next());
        }

        
        System.out.println("--TRANSACTIONS END--\n\n");

        if (this.limit != 0) 
        {
            System.out.println("Report for Financial Year " + this.limit);
        }
        else 
        {
            System.out.println("Report for All Years");
        }
        System.out.println("Note: Total Deposit and Withdraw ignore Range Limit");
        System.out.println(String.format("Total Deposit:            AUD$ %.2f", this.totalFiatDeposit));
        System.out.println(String.format("Total Withdraw:           AUD$ %.2f", this.totalFiatWithdraw));
        System.out.println(String.format("Total Profit: (exc fees)  AUD$ %.2f", this.profit));
        System.out.println(String.format("Total Fees:               AUD$ %.2f", this.totalFiatFees));
        System.out.println(String.format("Total Loss:               AUD$ %.2f", this.loss)); 

        System.out.println("Press any key to continue");

        try {
            System.in.read();
        }
        catch (IOException e) {}


        System.out.println("\nUnaccounted Sells & Unrealised Profits/Losses");
        Iterator<String> uIterator = this.unaccounted.iterator();
        while(uIterator.hasNext()) {
            System.out.println(uIterator.next());
        }

        System.out.println(String.format("Total Unaccounted Sells: $%.2f", this.unaccountedSells));
        System.out.println(String.format("Total Unaccounted Buys (yet to be sold): $%.2f", this.unaccountedBuys));
    }


    // Sets the financial year limit
    public void limit(int i) {
        this.limit = i;
        preprocess();
    }
}
